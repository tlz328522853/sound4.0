package com.sdcloud.web.lar.util;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * 服务器属性
 * 
 * @author wrs
 *
 */
public class ServerPropUtil {
	private ServerPropUtil() {
	}

	private static final ServerPropUtil single = new ServerPropUtil();
	private static Properties properties = new Properties();

	// 静态工厂方法
	public static ServerPropUtil getInstance() {
		try {
			InputStream is = ServerPropUtil.class.getResourceAsStream("/sysConfig.properties");
			properties.load(is);
		} catch (Exception e) {
			System.err.println("不能读取属性文件. " + "请确保properties在CLASSPATH指定的路径中");
		}
		return single;
	}

	public String getProp(String key) {
		return properties.getProperty(key);
	}
}
