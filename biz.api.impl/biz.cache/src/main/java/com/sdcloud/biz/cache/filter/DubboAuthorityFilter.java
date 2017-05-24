package com.sdcloud.biz.cache.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

/**
* @author jzc
* @version 2016年9月12日 下午4:37:43
* AuthorityFilter描述:dubbo接口调用 的 安全验证,验证访问者 IP
* http://dubbo.io/Developer+Guide-zh.htm#DeveloperGuide-zh-%E8%B0%83%E7%94%A8%E6%8B%A6%E6%88%AA%E6%89%A9%E5%B1%95
*/
public class DubboAuthorityFilter implements Filter {  
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboAuthorityFilter.class);  
  
    @Override  
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {  
        String clientIp = RpcContext.getContext().getRemoteHost();
        LOGGER.debug("访问ip为{}", clientIp);  
        return invoker.invoke(invocation);  
    }  
    
    
} 
