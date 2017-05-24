package com.sdcloud.biz.authority.dao.impl;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.biz.authority.dao.UserAuthorityDao;





@Service
public class UserAuthorityDaoImpl implements UserAuthorityDao{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	  @Resource(name = "redisTemplate")
	  private SetOperations<String, String>    usid_token;
	  
	  @Resource(name = "redisTemplate")
	  private SetOperations<String, String>    usid_role;
	  
	  @Resource(name = "redisTemplate")
	  private SetOperations<String, String>    usid_function;
	  

	  @Resource(name = "redisTemplate")
	  private SetOperations<String, String>   role_function;
	  
	  @Resource(name = "redisTemplate")
	  private SetOperations<String, String>   group_function;
	  
	  @Resource(name = "redisTemplate")
	  private ValueOperations<String, String>    function_url;
	  
	  @Resource(name = "redisTemplate")
	  private ValueOperations<String, String>    url_function;
	  
	  @Resource(name = "redisTemplate")
	  private ValueOperations<String, String>    function_name;
	  
	  @Resource(name = "redisTemplate")
	  private ValueOperations<String, String>    role_type;
	  
	  @Resource(name = "redisTemplate")
	  private HashOperations<String, String, String>    usid_login;


	/**
	 * 判断用户对该请求是否有权限访问
	 * 
	 * @param userId
	 *            用户id
	 * @param URl
	 *            请求地址
	 * @return boolean类型值 如果有权限则返回true 反之为false
	 */
	public boolean hasAuthorityByURL(String userId, String url) {

		String functionId = url_function.get(CacheKeyGenerator
				.getUrlFunction(url));
		if(StringUtils.isEmpty(functionId)){
			return false;
		}
		boolean hasAuthority = this.usid_function.isMember(
				CacheKeyGenerator.getUsidFunction(userId), functionId);
		if (hasAuthority) {
			return hasAuthority;
		}
		Set<String> roleIds=usid_role.members(CacheKeyGenerator
				.getUsidRole(userId));
		
		if(roleIds==null||roleIds.size()<=0){
			return false;
		}else{
			for (String roleId : roleIds) {
				hasAuthority = this.role_function.isMember(
						CacheKeyGenerator.getRoleFunction(roleId), functionId);
				if(hasAuthority){
					break;
				}
			}
		}
		
		if (hasAuthority) {
			return hasAuthority;
		}
		return false;

	}
	
	
	public void addAuthorityFunction(String userId,String... functionIds){
		usid_function.add(CacheKeyGenerator.getUsidFunction(userId), functionIds);
		usid_function.getOperations().expire(CacheKeyGenerator.getUsidFunction(userId), CacheKeyGenerator.getTokenoverdue(), TimeUnit.MINUTES);
		
	}
	
	public void addFunction(String functionId,String url){
	     function_url.set(CacheKeyGenerator.getFunctionUrl(functionId), url);
	}
	public void addAuthorityRole(String userId,String... roleIds){
		usid_role.add(CacheKeyGenerator.getUsidRole(userId), roleIds);
		usid_role.getOperations().expire(CacheKeyGenerator.getUsidRole(userId),CacheKeyGenerator.getTokenoverdue(), TimeUnit.MINUTES);
	}
	public void addRoleFunction(String roleId,String functionId){
		role_function.add(CacheKeyGenerator.getRoleFunction(roleId), functionId);
		
	}
	
	public void initRoleFunction(String roleId,String...functionIds){
		
			role_function.add(CacheKeyGenerator.getRoleFunction(roleId), functionIds);
		
		
	}
	public void initFunction(String functionId,String url){
		
			function_url.set(CacheKeyGenerator.getFunctionUrl(functionId), url);
		
	}
	public String getAuthorityFunction(String functionId){
		return function_url.get(CacheKeyGenerator.getFunctionUrl(functionId));
		
	}
	public void removeAuthorityFunction(String userId,String...functionIds){
			if(!StringUtils.isEmpty(userId)&&functionIds==null){
				usid_function.getOperations().delete(CacheKeyGenerator.getUsidFunction(userId));
			}else if(!StringUtils.isEmpty(userId)){
				usid_function.remove(CacheKeyGenerator.getUsidFunction(userId),functionIds);
			}
	}
	public void removeAuthorityFunctionByUserIds(List<Long> userIds){
		if(userIds != null && userIds.size() > 0 ){
			List<String> keys = new ArrayList<String>();
			for (Long user_id : userIds) {
				keys.add(CacheKeyGenerator.getUsidFunction(user_id+""));
			}
			usid_function.getOperations().delete(keys);
		}
	
	
	}
	
	public void removeFunction(String functionId){
		
			if(!StringUtils.isEmpty(functionId)){
				String oldUrl=function_url.get(CacheKeyGenerator.getFunctionUrl(functionId));
				url_function.getOperations().delete(CacheKeyGenerator.getUrlFunction(oldUrl));
				function_url.getOperations().delete(CacheKeyGenerator.getFunctionUrl(functionId));//remove(CacheKeyGenerator.getFunctionUrl(functionId),"*");
				
			}
		
	}
	public void removeRole(String roleId){
		
			role_function.getOperations().delete(CacheKeyGenerator.getRoleFunction(roleId));
		
	}
	public void removeAuthorityRole(String userId,String...roleIds){
		
			if(!StringUtils.isEmpty(userId)&&roleIds==null){
				usid_role.getOperations().delete(CacheKeyGenerator.getUsidRole(userId));
			}else if(!StringUtils.isEmpty(userId)){
				usid_role.remove(CacheKeyGenerator.getUsidRole(userId), roleIds);
			}
	}
	public void removeAuthorityRoleByUserIds(List<Long> userIds){
		if(userIds != null && userIds.size() > 0 ){
			List<String> keys = new ArrayList<String>();
			for (Long user_id : userIds) {
				keys.add(CacheKeyGenerator.getUsidRole(user_id+""));
			}
			usid_role.getOperations().delete(keys);
		}
		
}
	
	public void updataAuthorityFunction(String userId,String ...functionIds ){
			usid_function.getOperations().delete(CacheKeyGenerator.getUsidFunction(userId));
			if(functionIds!=null&&functionIds.length>0){
				usid_function.add(CacheKeyGenerator.getUsidFunction(userId), functionIds);
			}
							
	}
	public void updataFuncation(String functionId,String url){
			String oldUrl=function_url.get(CacheKeyGenerator.getFunctionUrl(functionId));
			url_function.getOperations().delete(CacheKeyGenerator.getUrlFunction(oldUrl));
			url_function.set(CacheKeyGenerator.getUrlFunction(url), functionId);
			function_url.getOperations().delete(CacheKeyGenerator.getFunctionUrl(functionId));//remove(CacheKeyGenerator.getFunctionUrl(functionId),"*");
			function_url.set(CacheKeyGenerator.getFunctionUrl(functionId), url);
			
		
	}
	
	public void updataUrlFuncation(String url,String functionId){
		
			url_function.set(CacheKeyGenerator.getUrlFunction(url), functionId);
		
	}
	@Override
	public void updataRoleFuncation(String roleId, String... functionIds) {
		
			role_function.getOperations().delete(CacheKeyGenerator.getRoleFunction(roleId));
			if(functionIds!=null&&functionIds.length>0){
				role_function.add(CacheKeyGenerator.getRoleFunction(roleId), functionIds);

			}
			
		
		
	}
	
	public void updataUserRole(String userId, String... roleIds) {
		
			usid_role.getOperations().delete(CacheKeyGenerator.getUsidRole(userId));//(CacheKeyGenerator.getUsidRole(userId),"*");
			if(roleIds!=null&&roleIds.length>0){
				usid_role.add(CacheKeyGenerator.getUsidRole(userId), roleIds);
			}
	}


	@Override
	public String getAdminiRoleType(String userId) {
		Set<String> roles=usid_role.members(CacheKeyGenerator.getUsidRole(userId));
		int type=2;
		String returnRole=null;
		for (String key : roles) {
			String types=role_type.get(CacheKeyGenerator.getRoleType(key));
			if(!StringUtils.isEmpty(types)&&Integer.valueOf(types)>=type){
				returnRole=types;
				
			}
		}
		return returnRole;
	}


	@Override
	public void addRoleType(Map<String, String> roletype) {
		for (Entry role : roletype.entrySet()) {
			role_type.set(CacheKeyGenerator.getRoleType(role.getKey().toString()),role.getValue().toString());
		}
		
	}


	@Override
	public List<String> getUserRole(String userId) {
		// TODO Auto-generated method stub
		List<String> roleIds=new ArrayList<String>();
		Set<String> roleIdSets=usid_role.members(CacheKeyGenerator.getUsidRole(userId));
		if(roleIdSets!=null){
			roleIds.addAll(roleIdSets);
		}
		return roleIds;
	}


	@Override
	public void updataGroupFuncation(String groupId, String... functionIds) {
		role_function.getOperations().delete(CacheKeyGenerator.getGroupFunction(groupId));
		if(functionIds!=null&&functionIds.length>0){
			role_function.add(CacheKeyGenerator.getGroupFunction(groupId), functionIds);

		}
				
	}


	@Override
	public void addRoleFuncation(String roleId, String... functionIds) {
		role_function.add(CacheKeyGenerator.getRoleFunction(roleId), functionIds);

		
	}


	@Override
	public String findFunctionName(String url) {
		
		return function_name.get(CacheKeyGenerator.getFunctionName(url));
	}


	@Override
	public void updataFuncationName(String url, String name) {
		function_name.getOperations().delete(CacheKeyGenerator.getFunctionName(url));
		if(!StringUtils.isEmpty(name)){
			function_name.set(CacheKeyGenerator.getFunctionName(url), name);
		}
	}


	@Override
	public void removeFunctionName(String url) {
		function_name.getOperations().delete(CacheKeyGenerator.getFunctionName(url));
		
	}


	


	


	
}
