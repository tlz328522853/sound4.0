package com.sdcloud.web.sa.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcloud.framework.entity.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 物流云数据用户验证
 * @author luorongjie
 * 2017年5月15日
 */
public class SAAuthorityFilter extends CompositeFilter {
	
	String validToken;//token码

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		String types = httpRequest.getHeader("Accept");
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "content-type,application/x-www-form-urlencoded,x-requested-with,content-type,token");
		
		boolean hasError=false;
		int errorCode = 0;
		String message = null;
		
		//从头部获取token
		String token = httpRequest.getHeader("token");
		token=token==null?token:token.trim();
		logger.info("hl request header token :"+token);
		// token校验
		if (!StringUtils.isEmpty(token)
				&&token.equals(validToken)) {
				chain.doFilter(req, response);
		} 
		else {
			hasError=true;
			errorCode= -1;
			message= "无效请求";
			logger.warn("无效请求");
 		}
		if(hasError){
			response.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
			res.setContentType("text/html;charset=UTF-8");
			PrintWriter pw=	response.getWriter();
			if(!StringUtils.isEmpty(types) && types.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
				response.setStatus(errorCode, message);
			}
			ResultDTO resultDTO = ResultDTO.getFailure(errorCode, message);
			// 对象转换成json字符串
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(resultDTO);
			pw.write(jsonStr);
			pw.flush();
			pw.close();
		}
	}
	

	public void init(FilterConfig arg0) throws ServletException {

	}

	public String getValidToken() {
		return validToken;
	}

	public void setValidToken(String validToken) {
		this.validToken = validToken;
	}
	
	

}
