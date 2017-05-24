package com.sdcloud.web.lar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import com.github.sardine.Sardine;

/**
 * 文件工具类
 * @author wanghs
 * 2016年4月19日
 */
public class FileOperationsUtils extends SardineUtil{
	
	/**
	 * 通过内网-文件流(名称不变)上传到服务器
	 * @param is
	 * @param fileName
	 * @return 外网文件访问路径
	 * @throws Exception
	 */
	public static String fileIsUpload(InputStream is,String filePath, String fileName) throws Exception{
		String webUrl = null;
		String outerWebUrl=null;//外部网络文件地址
		Sardine sardine=null;
		try {
			sardine=initSardineUtil();
			String path = createDirectory(sardine,filePath);
			outerWebUrl=getOuterFileUrl(filePath)+"/"+fileName;
			webUrl = path+"/"+fileName;
			upload(sardine,is, webUrl);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(sardine,is, null);
		}
		return outerWebUrl;
	}
	
	/**
	 * 通过内网-文件流(修改文件名称)上传到服务器
	 * @param is
	 * @param fileName
	 * @return 外网文件访问路径
	 * @throws Exception
	 */
	public static String fileIsUpload(InputStream is, String fileName) throws Exception{
		String webUrl = null;
		String outerWebUrl=null;//外部网络文件地址
		Sardine sardine=null;
		try {
			sardine=initSardineUtil();
			String filename = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
			String dateFileName=getDatePath(new Date());
			String path = createDirectory(sardine,dateFileName);
			outerWebUrl=getOuterFileUrl(dateFileName)+"/"+filename;
			webUrl = path+"/"+filename;
			upload(sardine,is, webUrl);
		} catch (Exception e) {
			throw e;
		} finally {
			close(sardine,is, null);
		}
		return outerWebUrl;
	}
	
	/**
	 * 通过内网-文件(修改文件名称)上传
	 * @param file
	 * @return 外网文件访问路径
	 * @throws Exception
	 */
	public static String fileUpload(MultipartFile file) throws Exception{
		InputStream is = null;
		String webUrl = null;
		String outerWebUrl=null;//外部网络文件地址
		Sardine sardine=null;
		if (!file.isEmpty()) {
			try {
				sardine=initSardineUtil();
				String originalFilename = file.getOriginalFilename();
				String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
				String dateFileName=getDatePath(new Date());
				String path = createDirectory(sardine,dateFileName);
				outerWebUrl=getOuterFileUrl(dateFileName)+"/"+filename;
				webUrl = path+"/"+filename;
				is = file.getInputStream();
				upload(sardine,is, webUrl);
			} catch (Exception e) {
				throw e;
			} finally {
				close(sardine,is, null);
			}
		}
		return outerWebUrl;
	}
	
	/**
	 * 文件下载
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void fileDown(Map<String, Object> param,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String name = request.getParameter("name");
		String path = request.getParameter("path");
		if(name == null && param !=null) {
			name = (String)param.get("name");
		}
		if(path == null && param !=null) {
			path = (String)param.get("path");
		}
		
		name = new String(name.getBytes("ISO8859-1"),"utf-8");
		path = new String(path.getBytes("ISO8859-1"),"utf-8");
		
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition", "attachment;filename="+new String(name.getBytes("utf-8"), "ISO8859-1"));
		
		InputStream is = null;
		OutputStream os = null;
		Sardine sardine=null;
		try {
			sardine=initSardineUtil();
			is = get(sardine,path);
			os = response.getOutputStream();
			byte[] buff = new byte[1024];
			int b;
			while((b=is.read(buff, 0, buff.length)) != -1){
				os.write(buff, 0, b);
			}
			os.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			close(sardine,is, os);
		}
	}
	
	/**
	 * 日期文件夹
	 * @param date
	 * @return
	 */
	public static String getDatePath(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String strDate = simpleDateFormat.format(date);
		return strDate;
	}
	
	public static void main(String[] args) throws Exception{
		String filename="2.txt";
		InputStream fis = new FileInputStream(new File("D:/1.txt"));
		String url=fileIsUpload(fis, filename);
		System.out.println(url);
	}
}
