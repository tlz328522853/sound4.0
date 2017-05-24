package com.sdcloud.biz.authority.dao;

import java.util.Set;

public interface CodeDao {
	
	public void addType(String urlType, String ... code);
	
	public Set<String> getType(String urlType);
	
	public void removeType(String urlType, String code);
	
}
