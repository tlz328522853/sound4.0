package com.sdcloud.web.lar.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SendPhoneMessage {

	public static String sendPhoneMessage(String phone, String content) throws IOException {
		ServerPropUtil prop = ServerPropUtil.getInstance();
		String username = prop.getProp("msg.username");
		String password = prop.getProp("msg.password");
		String apikey = prop.getProp("msg.apikey");
		// 发送内容
		String sign = "【好嘞社区】";//签名
		// 创建StringBuffer对象用来操作字符串
		StringBuffer sb = new StringBuffer("http://m.5c.com.cn/api/send/index.php?");
		// 向StringBuffer追加用户名
		sb.append("username=");
		sb.append(username);
		// 向StringBuffer追加密码（登陆网页版，在管理中心--基本资料--接口密码，是28位的）
		sb.append("&password=");
		sb.append(password);
		// 向StringBuffer追加手机号码
		sb.append("&apikey=");
		sb.append(apikey);
		sb.append("&mobile=");
		sb.append(phone);
		// 向StringBuffer追加消息内容转URL标准码
		sb.append("&content=" + URLEncoder.encode(sign+content, "GBK"));
		 URL url = new URL(sb.toString());
		// // 打开url连接
		 HttpURLConnection connection = (HttpURLConnection)
		 url.openConnection();
		// // 设置url请求方式 ‘get’ 或者 ‘post’
		 connection.setRequestMethod("POST");
		// // 发送
		 InputStream is =url.openStream();
		// //转换返回值
		 String returnStr = convertStreamToString(is);
		// // 返回结果为‘0，20140009090990,1，提交成功’ 发送成功 具体见说明文档
		 return returnStr;
//		 System.out.println(returnStr);
		// // 返回发送结果

	}

	public static String convertStreamToString(InputStream is) {
		StringBuilder sb1 = new StringBuilder();
		byte[] bytes = new byte[4096];
		int size = 0;

		try {
			while ((size = is.read(bytes)) > 0) {
				String str = new String(bytes, 0, size, "UTF-8");
				sb1.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb1.toString();
	}

	public static void main(String args[]) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("您正在注册好嘞账户，验证码：146819，");
		sb.append("好嘞将给您提供优质的服务。");
		System.out.println(sendPhoneMessage("13718330696", sb.toString()));
	}
}
