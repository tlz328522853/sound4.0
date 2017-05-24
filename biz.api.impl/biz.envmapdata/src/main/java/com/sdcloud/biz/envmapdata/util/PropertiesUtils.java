package com.sdcloud.biz.envmapdata.util;

import java.util.Properties;

public class PropertiesUtils {
	
    Properties properties;
    
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public String getProperty(String name) {
		return properties.getProperty(name);
	}
}

