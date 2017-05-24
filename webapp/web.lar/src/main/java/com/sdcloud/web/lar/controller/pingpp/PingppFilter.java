package com.sdcloud.web.lar.controller.pingpp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CompositeFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcloud.framework.entity.ResultDTO;

/**
* @author jzc
* @version 2016年6月28日 上午9:26:49
* PingppFilter描述:pingxx回调地址的 过滤器
*/
public class PingppFilter extends CompositeFilter{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void destroy() {

	}
    
	/**
	 * pingxx请求 webhooks安全验证
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		logger.info("-------------进入pingxx过滤器----------------------------------------------------");
 	    HttpServletRequest request = (HttpServletRequest) req;
		res.reset();
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "content-type,application/x-www-form-urlencoded,x-requested-with,content-type,token");
 	    
		
		// 该数据请从 request中获取原始 POST请求(json)数据
		String webhooksRawPostData = WebhooksVerifyUtil.getBodyString(request.getReader());
		logger.info("------- POST 原始数据 ------- \n\t"+webhooksRawPostData);
        // 签名数据请从 request 的 header 中获取, key 为 X-Pingplusplus-Signature(忽略大小写,自己做格式化)
        String signature =WebhooksVerifyUtil.getLowerHeadersInfo(request).get("x-pingplusplus-signature");
	    logger.info("------- 签名 -------\n\t"+signature);
		boolean flag=WebhooksVerifyUtil.webhooksVerify(webhooksRawPostData,signature);
		logger.warn("验签结果：" + (flag ? "通过" : "失败"));
 	    if(flag){
 	    	request.setAttribute("eventData", webhooksRawPostData);
 	    	logger.info("-------------通过：--过滤器-----------------------------------------------------------------");
 	    	chain.doFilter(request, response);
 	    }
 	    else{
 	    	response.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
			res.setContentType("text/html;charset=UTF-8");
			PrintWriter pw=	response.getWriter();
			ResultDTO resultDTO = ResultDTO.getFailure(500, "webhooks：非法请求数据！");
			// 对象转换成json字符串
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(resultDTO);
			logger.warn("-------------被拦截：--过滤器----------------------------------------------------");
			pw.write(jsonStr);
			pw.flush();
			pw.close();
 	    }
	}
	
	public void init(FilterConfig arg0) throws ServletException {

	}

}
