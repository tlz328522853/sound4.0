package com.sdcloud.web.lar.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.ProtocolException;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class WeixinUtil {
	private static Logger log = Logger.getLogger(WeixinUtil.class);

	private static StringBuffer httpsRequest(String requestUrl, String requestMethod, String output)
			throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, MalformedURLException,
			IOException, ProtocolException, UnsupportedEncodingException {
		URL url = new URL(requestUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(requestMethod);
		if (null != output) {
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(output.getBytes("UTF-"));
			outputStream.close();
		}
		// 从输入流读取返回内容
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String str = null;
		StringBuffer buffer = new StringBuffer();
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		inputStream = null;
		connection.disconnect();
		return buffer;
	}

	public static JSONObject httpsRequestToJsonObject(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			StringBuffer buffer = httpsRequest(requestUrl, requestMethod, outputStr);
			// jsonObject = JSONObject.fromObject(buffer.toString());
			jsonObject = (JSONObject) JSONObject.stringToValue(buffer.toString());
		} catch (ConnectException ce) {
			log.error("连接超时：" + ce.getMessage());
		} catch (Exception e) {
			log.error("https请求异常：" + e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 获取用户的openId，并放入session
	 * 
	 * @param code
	 *            微信返回的code
	 */
	private void setOpenId(String code) {
		ServerPropUtil prop = ServerPropUtil.getInstance();
		String oauth_url = "https://api.weixin.qq.com/sns/oauth/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		String appid = prop.getProp("wx.appid");
		String secret = prop.getProp("wx.secret");
		oauth_url = oauth_url.replace("APPID", appid).replace("SECRET", secret).replace("CODE", code);
		log.info("oauth_url:" + oauth_url);
		JSONObject jsonObject = WeixinUtil.httpsRequestToJsonObject(oauth_url, "POST", null);
		log.info("jsonObject:" + jsonObject);
		Object errorCode = jsonObject.get("errcode");
		if (errorCode != null) {
			log.info("code不合法");
		} else {
			String openId = jsonObject.getString("openid");
			log.info("openId:" + openId);
		}
	}
}
