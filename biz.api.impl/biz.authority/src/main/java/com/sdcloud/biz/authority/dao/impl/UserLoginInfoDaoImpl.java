package com.sdcloud.biz.authority.dao.impl;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.biz.authority.dao.UserLoginInfoDao;
import com.sdcloud.framework.common.UserLoginType;





@Service
public class UserLoginInfoDaoImpl implements UserLoginInfoDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> usid_login;
	
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> usid_login_app;

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> ip_token;

	/**
	 * 添加用户登入信息
	 * 
	 * @param functionId
	 * @param Url
	 */
	public void addUserLoginInfo(String userId, Map<String, String> loginInfo) {

		
		//usid_login.getOperations().expireAt(key, date)
		if(loginInfo.get(CacheKeyGenerator.INFO_TYPE_APP).equals(UserLoginType.web)){
			usid_login.putAll(CacheKeyGenerator.getUsidLogin(userId), loginInfo);
			usid_login.getOperations().expire(
					CacheKeyGenerator.getUsidLogin(userId),
					1800000, TimeUnit.MILLISECONDS);
		}else{
			usid_login.putAll(CacheKeyGenerator.getUsidLoginApp(userId), loginInfo);
		}
		

	}

	/**
	 * 获取用户登入信息
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String,Map<String,String>> getUserLoginInfo(String userId) {
		Map<String,Map<String,String>> loginInfos=new HashMap<String,Map<String,String>>();
		Long time=usid_login.getOperations().getExpire(CacheKeyGenerator.getUsidLogin(userId));
		if(time!=null&&time<300000){
			usid_login.getOperations().expire(
					CacheKeyGenerator.getUsidLogin(userId),
					1800000, TimeUnit.MILLISECONDS);
		}
		Map<String, String> loginInfoWeb=usid_login.entries(CacheKeyGenerator.getUsidLogin(userId));
		if(loginInfoWeb!=null&&loginInfoWeb.size()>0){
			loginInfos.put(UserLoginType.web, loginInfoWeb);
		}
		Map<String, String> loginInfoApp=usid_login.entries(CacheKeyGenerator.getUsidLoginApp(userId));
		if(loginInfoApp!=null&&loginInfoApp.size()>0){
			loginInfos.put(UserLoginType.app, loginInfoApp);
		}
		
		return loginInfos;

	}

	/**
	 * 获取用户登入IP
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String,String> getUserLoginIP(String userId) {
		Map<String,String> ips=new HashMap<String,String>();
		String ipWeb=usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_IP);
		if(!StringUtils.isEmpty(ipWeb)){
			ips.put(UserLoginType.web, ipWeb);
		}
		String ipApp=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId),
				CacheKeyGenerator.INFO_TYPE_IP);
		if(!StringUtils.isEmpty(ipApp)){
			ips.put(UserLoginType.web, ipApp);
		}
		return ips;

	}

	/**
	 * 获取用户登入token
	 * 
	 * @param userId
	 * @return
	 */
	public List<String> getUserLoginToken(String userId) {
		Long time=usid_login.getOperations().getExpire(CacheKeyGenerator.getUsidLogin(userId));
		if(time!=null&&time<300000){
			usid_login.getOperations().expire(
					CacheKeyGenerator.getUsidLogin(userId),
					1800000, TimeUnit.MILLISECONDS);
		}
		List<String> tokens=new ArrayList<String>();
		String tokenWeb=usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_TOKEN);
		if(!StringUtils.isEmpty(tokenWeb)){
			tokens.add(tokenWeb);
		}
		String tokenApp=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId),
				CacheKeyGenerator.INFO_TYPE_TOKEN);
		if(!StringUtils.isEmpty(tokenApp)){
			tokens.add(tokenApp);
		}
		return tokens;

	}

	/**
	 * 获取用户登入时间
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserLoginTime(String userId) {

		return usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_TIME);

	}

	public void removeUserLoginInfo(String userId,String loginType) {

		String ip = usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_IP);
		if (!StringUtils.isEmpty(ip)) {
			ip_token.getOperations().delete(CacheKeyGenerator.getIpToken(ip));
		}
		if(loginType.equals(UserLoginType.web)){
			usid_login.getOperations().delete(
					CacheKeyGenerator.getUsidLogin(userId));
		}else{
			usid_login.getOperations().delete(
					CacheKeyGenerator.getUsidLoginApp(userId));
		}
	}

	public void removeUserLoginInfoByUserIds(List<Long> userIds) {
		//要批量删除的缓存 user key
		List<String> user_keys = new ArrayList<String>();
		//要删除的缓存 token key
		List<String> ip_token_keys = new ArrayList<String>();
		for (Long user_id : userIds) {
			String ip = usid_login.get(CacheKeyGenerator.getUsidLogin(user_id+""),
					CacheKeyGenerator.INFO_TYPE_IP);
			String ip_token_key = CacheKeyGenerator.getIpToken(ip);
			ip_token_keys.add(ip_token_key);
			user_keys.add(CacheKeyGenerator.getUsidLogin(user_id+""));
			user_keys.add(CacheKeyGenerator.getUsidLoginApp(user_id+""));
		}
		ip_token.getOperations().delete(user_keys);
		usid_login.getOperations().delete(ip_token_keys);
		
	}
	
	
	/**
	 * 缓存用户登入IP和token
	 * 
	 * @param Ip
	 * @param token
	 */
	public void addTokenByIP(String ip, String token) {

		ip_token.add(CacheKeyGenerator.getIpToken(ip), token);

	}

	/**
	 * 获取用户登入token
	 * 
	 * @param ip
	 * @return
	 */
	public String getTokenByIP(String ip) {

		return ip_token.randomMember(CacheKeyGenerator.getIpToken(ip));

	}

	public void removeTokenByIP(String ip) {

		ip_token.remove(CacheKeyGenerator.getIpToken(ip), "*");

	}

	@Override
	public long getTenantIdByUser(String userId) {
		String tenantId=usid_login.get(CacheKeyGenerator.getUsidLogin(userId), CacheKeyGenerator.INFO_TYPE_TENANTID);
		if(!StringUtils.isEmpty(tenantId)){
			return Long.valueOf(tenantId);
		}else{
			tenantId=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId), CacheKeyGenerator.INFO_TYPE_TENANTID);
			
		}
		if(!StringUtils.isEmpty(tenantId)){
			return Long.valueOf(tenantId);
		}
		return -1;
	}

	@Override
	public Map<String,Map<String,String>> getUserLoginInfo(String userId,String... keys) {
		Map<String,Map<String,String>> loginInfos=new HashMap<String,Map<String,String>>();
		Map<String,String> valuesWeb=new HashMap<String,String>();
		for(String key : keys){
			String value=usid_login.get(CacheKeyGenerator.getUsidLogin(userId), key);
			valuesWeb.put(key, value);
		}
		if(valuesWeb.size()>0){
			loginInfos.put(UserLoginType.web, valuesWeb);
		}
		Map<String,String> valuesApp=new HashMap<String,String>();
		for(String key : keys){
			String value=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId), key);
			valuesApp.put(key, value);
		}
		if(valuesApp.size()>0){
			loginInfos.put(UserLoginType.app, valuesApp);
		}
		return loginInfos;
	}

	@Override
	public long getUserGroupIdByUser(String userId) {
		// TODO Auto-generated method stub
		String userGroupId=usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_USERGROUPID);
		if(!StringUtils.isEmpty(userGroupId)){
			return Long.valueOf(userGroupId);
		}else{
			userGroupId=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId),
					CacheKeyGenerator.INFO_TYPE_USERGROUPID);
			
			
		}
		if(!StringUtils.isEmpty(userGroupId)){
			return Long.valueOf(userGroupId);
		}
		return -1;
	}

	@Override
	public Long getUserLoginEmployee(String userId) {
		// TODO Auto-generated method stub
		String employeeId=usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_EMP);
		if(StringUtils.isEmpty(employeeId)){
			employeeId=usid_login_app.get(CacheKeyGenerator.getUsidLoginApp(userId),
					CacheKeyGenerator.INFO_TYPE_EMP);
		}
		if(!StringUtils.isEmpty(employeeId)){
			return Long.valueOf(employeeId);
		}
		return null;
	}

	@Override
	public Map<String, String> getUserLoginTokenMap(String userId) {
		Long time=usid_login.getOperations().getExpire(CacheKeyGenerator.getUsidLogin(userId));
		if(time!=null&&time<300000){
			usid_login.getOperations().expire(
					CacheKeyGenerator.getUsidLogin(userId),
					1800000, TimeUnit.MILLISECONDS);
		}
		Map<String, String> tokens=new HashMap<String, String>();
		String tokenWeb=usid_login.get(CacheKeyGenerator.getUsidLogin(userId),
				CacheKeyGenerator.INFO_TYPE_TOKEN);
		if(!StringUtils.isEmpty(tokenWeb)){
			tokens.put(UserLoginType.web,tokenWeb);
		}
		String tokenApp=usid_login.get(CacheKeyGenerator.getUsidLoginApp(userId),
				CacheKeyGenerator.INFO_TYPE_TOKEN);
		if(!StringUtils.isEmpty(tokenApp)){
			tokens.put(UserLoginType.app,tokenApp);
		}
		return tokens;
	}

	
}
