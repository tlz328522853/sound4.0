package com.sdcloud.biz.authority.dao.impl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.biz.authority.util.FakUserInfo;

//@Component("fakeUserLoginInfoDao")
public class FakeUserLoginInfoDaoImpl {
	
	
	private Map<String,FakUserInfo>    usid_login=new HashMap<String,FakUserInfo>();
	private Map<String, String>   ip_token=new HashMap<String,String>();
	 
	public void addTokenByIP(String ip, String token) {
		// TODO Auto-generated method stub
		ip_token.put(ip, token);
	}

	public void addUserLoginInfo(String userId, Map<String, String> loginInfo) {
		// TODO Auto-generated method stub
		FakUserInfo fui=new FakUserInfo();
		fui.setIp(loginInfo.get(CacheKeyGenerator.INFO_TYPE_IP));
		fui.setToken(loginInfo.get(CacheKeyGenerator.INFO_TYPE_TOKEN));
		fui.setTime(loginInfo.get(CacheKeyGenerator.INFO_TYPE_TIME));
		usid_login.put("userId", fui);
	}

	public String getTokenByIP(String ip) {
		// TODO Auto-generated method stub
		Set<String> tokens=new HashSet<String>();
		tokens.add(ip_token.get(ip));
		return null;//ip_token.get(ip);
	}

	
	public Map<String, String> getUserLoginInfo(String userId) {
		// TODO Auto-generated method stub
		return usid_login.get(userId).toMap();
	}

	public String getUserLoginIP(String userId) {
		// TODO Auto-generated method stub
		return usid_login.get(userId).getIp();
	}

	public String getUserLoginTime(String userId) {
		// TODO Auto-generated method stub
		return usid_login.get(userId).getTime();
	}

	public String getUserLoginToken(String userId) {
		// TODO Auto-generated method stub
		return usid_login.get(userId).getToken();
	}

	public void removeTokenByIP(String ip) {
		// TODO Auto-generated method stub
		ip_token.remove(ip);
	}

	public void removeUserLoginInfo(String userId) {
		// TODO Auto-generated method stub
		usid_login.remove(userId);
	}

	public long getTenantIdByUser(String userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAdminiRole(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
