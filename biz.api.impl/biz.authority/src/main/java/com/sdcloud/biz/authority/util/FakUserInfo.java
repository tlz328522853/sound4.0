package com.sdcloud.biz.authority.util;

import java.util.HashMap;
import java.util.Map;

import com.sdcloud.api.authority.util.CacheKeyGenerator;

public class FakUserInfo {
	private String token;
	private String ip;
	private String time;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public Map<String, String> toMap() {
		// TODO Auto-generated method stub
		Map<String, String> loginInfo=new HashMap<String,String>();
		loginInfo.put(CacheKeyGenerator.INFO_TYPE_IP, this.ip);
		loginInfo.put(CacheKeyGenerator.INFO_TYPE_TIME, this.time);
		loginInfo.put(CacheKeyGenerator.INFO_TYPE_TOKEN, this.token);
		return loginInfo;
	}
	
	
	
}
