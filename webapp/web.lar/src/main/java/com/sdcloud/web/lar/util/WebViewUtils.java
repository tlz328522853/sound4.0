package com.sdcloud.web.lar.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class WebViewUtils {

	public static void printView(HttpServletResponse response,String content){
		PrintWriter out = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			StringBuilder sb = new StringBuilder();
			sb.append(" <html>");
			sb.append(" <head>");
			sb.append(" </head>");
			sb.append(" <body>");
			sb.append(content);
			sb.append(" </body>");
			sb.append(" </html>");
			
			out = response.getWriter();
			out.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out != null){
				out.close();
				out = null;
			}
		}
		
	}
}
