
package com.sdcloud.web.lar.util.oms;

import com.sdcloud.web.lar.util.ServerPropUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUrlUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(HttpUrlUtil.class);

    public static String identSearchJson(String param,String url){
        ServerPropUtil prop = ServerPropUtil.getInstance();
        String  omsUrl= prop.getProp("omsUrl");

    	String pathUrl = omsUrl +url;
        String str="";
		try {
			str = loadPostStr(pathUrl,param);
			System.out.println(str);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(StringUtils.isEmpty(str)){
			return null;
		}
		//str=str.replaceAll(" ", "").replaceAll(":null", ":''");
    	return str;
    }
    

    protected static String loadPostStr(String pathUrl,String param) throws Exception{
        String urlPath = new String(pathUrl); 
        //建立连接
        URL url=new URL(urlPath);
        HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
        //设置参数
        httpConn.setDoOutput(true);   //需要输出
        httpConn.setDoInput(true);   //需要输入
        httpConn.setUseCaches(false);  //不允许缓存
        httpConn.setRequestMethod("POST");   //设置POST方式连接
        //设置请求属性
        httpConn.setRequestProperty("Content-Type", "text/plain");
        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        httpConn.setRequestProperty("Charset", "UTF-8");
        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();
        //建立输入流，向指向的URL传入参数
        byte[] data = param.getBytes();
        OutputStream dos=httpConn.getOutputStream();
        dos.write(data);
        dos.flush();
        dos.close();
        //获得响应状态
        int resultCode=httpConn.getResponseCode();
        StringBuffer sb=new StringBuffer();
        if(HttpURLConnection.HTTP_OK==resultCode){
          String readLine=new String();
          BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
          while((readLine=responseReader.readLine())!=null){
            sb.append(readLine).append("\n");
          }
          responseReader.close();
        } 
        return sb.toString();
    }
    
}

