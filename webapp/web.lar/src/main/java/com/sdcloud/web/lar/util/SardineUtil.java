package com.sdcloud.web.lar.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
/**
 * webdav服务器连接，上传下载
 * 2016年12月21日
 */
public abstract class SardineUtil {
	static private String fileserverinner;//内网文件服务器地址
	static private String fileserver;// 外网文件服务器
	static private String uploadpath;// 上传路径
	static private String webDavUser;// 用户名
	static private String webDavPassword;// 密码
	//static Sardine sardine;
    
	/**
	 * 创建内网文件地址，用于上传文件
	 * @author jzc 2016年12月13日
	 * @param dir
	 * @return
	 */
	public static String createDirectory(Sardine sardine,String dir) {
		initSardineUtil();
		String path = fileserverinner + "/" + uploadpath + "/" + dir;
		try {
			if(!exists(sardine,path)){
				sardine.createDirectory(path);
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg != null && msg.contains("301 Moved Permanently")) {
				System.out.println("WebDav createDirectory success: ");
			} else {
				e.printStackTrace();
				return "";
			}
		}
		return path;
	}
	
	/**
	 * 获取外网文件地址，用于文件下载
	 * @author jzc 2016年12月13日
	 * @return
	 */
	public static String getOuterFileUrl(String dir){
		
		return fileserver + "/" + uploadpath + "/" + dir;
	}

	public static Boolean upload(Sardine sardine,InputStream fis, String filePath) {
		try {
			sardine.put(filePath, fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加载webdav服务器配置信息
	 */
	public static synchronized void loadProperties() {
		if (fileserver == null || uploadpath == null || webDavUser == null || webDavPassword == null) {
			InputStream is = SardineUtil.class.getResourceAsStream("/sysConfig.properties");
			Properties properties = new Properties();
			try {
				properties.load(is);
				fileserverinner = properties.getProperty("fileserverinner").toString();
				fileserver = properties.getProperty("fileserver").toString();
				uploadpath = properties.getProperty("uploadpath").toString();
				webDavUser = properties.getProperty("webDavUser").toString();
				webDavPassword = properties.getProperty("webDavPassword").toString();
			} catch (Exception e) {
				System.err.println("不能读取属性文件. " + "请确保properties在CLASSPATH指定的路径中");
			}
		}
	}

	public static InputStream get(Sardine sardine,String filePath) {
		InputStream in = null;
		try {
			in = sardine.get(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return in;
	}

	public static boolean delete(Sardine sardine,String path) {
		try {
			sardine.delete(path);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean exists(Sardine sardine,String uri){
		boolean exists = false;
		try {
			exists = sardine.exists(uri);
		}catch(SardineException e){
			//返回码403,说明此目录已创建
			if(e.getStatusCode()==403){
				return true;
			}
		}catch(Throwable t){
			return exists;
		}
		return exists;
	}
    
	/**
	 * 初始化Sardine 对象
	 * @author jzc 2016年12月27日
	 * @return
	 */
	public static Sardine initSardineUtil() {
		loadProperties();
		return SardineFactory.begin(webDavUser, webDavPassword);
	}

	@SuppressWarnings("deprecation")
	public static void printResources(Sardine sardine,String path) {
		List<DavResource> resources;
		try {
			resources = sardine.getResources(path);
			for (DavResource res : resources) {
				System.out.println(res); // calls the .toString() method.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void close(Sardine sardine,InputStream is,OutputStream os) throws IOException{
		if(is != null){
			is.close();
			is = null;
		}
		if(os != null){
			os.close();
			os = null;
		}
		if(sardine != null){
			sardine.shutdown();
			sardine = null;
		}
	}


}
