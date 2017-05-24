package com.sdcloud.biz.authority.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserLoginInfoDao {
	public void addTokenByIP(String ip,String token);
	public void addUserLoginInfo(String userId,Map<String,String>  loginInfo);
	public String getTokenByIP(String ip);
	public Map<String,Map<String,String>> getUserLoginInfo(String userId);
	public Map<String,String> getUserLoginIP(String userId);
	public String getUserLoginTime(String userId);
	public List<String> getUserLoginToken(String userId);
	public void removeTokenByIP(String ip);
	public void removeUserLoginInfo(String userId,String loginType);
	public void removeUserLoginInfoByUserIds(List<Long> userIds);
	public long getTenantIdByUser(String userId);
	public long getUserGroupIdByUser(String userId);
	public Map<String,Map<String,String>> getUserLoginInfo(String userId,String...keys);
	public Long getUserLoginEmployee(String userId);
	public Map<String,String> getUserLoginTokenMap(String userId);
	
}
