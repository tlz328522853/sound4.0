package com.sdcloud.web.lar.util.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CompositeFilter;

import com.sdcloud.api.lar.entity.UserLog;
import com.sdcloud.api.lar.service.UserLogService;
import com.sdcloud.framework.util.GetRequestIPUtil;

/**
 * 操作日志过滤器
 * @author jzc
 * 2017年1月11日
 */
@Component
public class LogFilter extends CompositeFilter{

	private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);
	
	@Autowired
	private UserLogService userLogService;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
        UserLog userLog=new UserLog();
		String uri=(String)request.getRequestURI();
		String app=(String)request.getParameter("app");
		String mac=(String)request.getParameter("deviceId");
		String optionIp = GetRequestIPUtil.getIpAddr(request);
		String userId =(String) request.getAttribute("token_userId");
		if(uri.length()>128){
			uri=uri.substring(0, 126);
		}
		if(userId!=null){
			userLog.setUserId(Long.parseLong(userId));
			userLog.setRemark("正常");
		}else{
			userLog.setUserId(1l);
			userLog.setRemark("异常");
		}
		userLog.setUserApp(app!=null?app:"web");
		userLog.setUserMac(mac!=null?mac:"web");
		userLog.setUserIp(optionIp);
		userLog.setUserUrl(uri);
		userLog.setUserTime(new Date());
		
		userLogService.save(userLog);
		chain.doFilter(request, response);

	}
	
}
