package com.sdcloud.biz.authority.dao;

import java.util.List;
import java.util.Map;

public interface UserAuthorityDao {
	public void addAuthorityFunction(String userId,String... functionIds);
	public void addAuthorityRole(String userId,String... roleIds);
	public void addRoleFunction(String roleId,String functionId);
	public void addFunction(String functionId,String url);
	public boolean hasAuthorityByURL(String userId,String Url);
	public void initRoleFunction(String roleId,String...functionIds);
	public void initFunction(String functionId,String url);
	public void updataAuthorityFunction(String userId,String...functionIds);
	public void updataRoleFuncation(String roleId,String... functionId);
	public void addRoleFuncation(String roleId,String... functionIds);
	
	public void updataGroupFuncation(String groupId,String... functionIds);
	public void updataFuncation(String functionId,String url);
	public void removeAuthorityFunction(String userId,String...functionIds);
	/**
	 * 删除用户功能缓存
	 * @param userIds
	 */
	public void removeAuthorityFunctionByUserIds(List<Long> userIds);
	public void removeAuthorityRole(String userId,String...roleIds);
	/**
	 * 删除用户角色缓存
	 * @param userIds
	 */
	public void removeAuthorityRoleByUserIds(List<Long> userIds);
	public void updataUserRole(String userId, String... roleIds);
	public void updataUrlFuncation(String url,String functionId);
	public void removeFunction(String functionId);
	public void removeRole(String roleId);
	public void addRoleType(Map<String,String> role_type);
	public String getAdminiRoleType(String userId);
	
	public void updataFuncationName(String url,String name);
	public void removeFunctionName(String url);
	public String findFunctionName(String url);
	
	public List<String> getUserRole(String userId);
}
