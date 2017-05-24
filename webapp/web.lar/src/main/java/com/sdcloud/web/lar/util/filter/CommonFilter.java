package com.sdcloud.web.lar.util.filter;

import java.io.IOException;
import java.io.PrintWriter;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * 公用过滤器
 * @author wanghs
 *
 */
@Component
public class CommonFilter extends CompositeFilter{

	private static final Logger logger = LoggerFactory.getLogger(CommonFilter.class);
	
	@Autowired
	private CityService cityService;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

	 	HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		/*response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "content-type,application/x-www-form-urlencoded,x-requested-with,content-type,cityId");
		res.reset();*/
	
		//返回的状态信息
		String message = null;
		ResultDTO resultDTO = null;
		//从请求头中获取城市Id
		String cityId = request.getHeader("cityId");
		
		logger.info("Enter the :{} method  cityId:{}", Thread.currentThread()
				.getStackTrace()[1].getMethodName(),cityId);
		try {
			//城市Id为空时不过滤 ,有值时才进行滤过
			if(null == cityId || cityId == ""){
				chain.doFilter(request, response);
			}else {
				boolean flag = cityService.isDisable(Long.valueOf(cityId));
				if(flag){
					message = "此城市已被禁用";
					//TODO 状态码需要用appcode
					resultDTO =  ResultDTO.getFailure(10506, message);
				}else{
					chain.doFilter(request, response);
				}
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
			resultDTO = ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "参数错误!");
		}
		
		if(null != resultDTO){
			//输出信息
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			res.setContentType("text/html;charset=UTF-8");
			PrintWriter writer = response.getWriter();
			
			// 对象转换成json字符串
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(resultDTO);
			writer.write(jsonStr);
			writer.flush();
			writer.close();
		}
	}
	
}
