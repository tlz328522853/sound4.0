package com.sdcloud.biz.cache.util.aspectj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdcloud.api.cache.redis.service.ModuleDefineService;

/**
 * redis service 请求参数key head 验证
 * 
 * @author jzc 2016年9月18日
 */
@Component
@Aspect
public class RedisAopHandler {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ModuleDefineService moduleHeadDefineService;
	//批量操作方法集合
	public static String[] MULTI_VALIDATE_METHODS={"multiSet","multiSetIfAbsent","multiGet"};

	/**
	 * 请求参数验证方法
	 * 
	 * @author jzc 2016年9月18日
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings("finally")
	@Around("execution(* com.sdcloud.biz.cache.redis.service.impl.*OperationsServiceImpl.*(..))")
	public Object requestParmtesHandler(ProceedingJoinPoint joinPoint) throws Throwable {

		boolean flag = false;
		String methodName =null;
		String mess="参数key-验证：";
		try {
			methodName = joinPoint.getSignature().getName();//获取方法名
			if(methodName.contains("_")){//此类方法 是对象操作，暂不处理，放过
				flag=true;
				mess="对象操作-暂未验证：";
			}
			else{
				List<String> multiMethods = new ArrayList<String>(Arrays.asList(MULTI_VALIDATE_METHODS));
				if(multiMethods.contains(methodName)){//此类方法是批量操作，Map|Collection 两种类型，暂不处理，放过
					flag=true;
					mess="批量操作-暂未验证：";
				}
				else{
					flag = validateKey(joinPoint.getArgs());//验证参数key
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			flag = false;
		} finally {
			if (flag) {
				logger.info(mess+methodName+">方法通过！flag:"+flag);
				return joinPoint.proceed();
			} else {
				logger.warn(mess+methodName+">方法未通过！返回 null  flag:"+flag);
				return null;
			}
		}
	}

	protected boolean validateKey(Object[] objects) {

		String key = null;// redis 请求的 key值
		if (objects.length > 0 && objects[0] instanceof String) {
			key = (String) objects[0];
			logger.debug("request key：（" + key + "）");
			if (StringUtils.isEmpty(key)) {
				return false;
			}
			if (!validateModuleCode(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证 key的modulecode是否正确
	 * 
	 * @param key
	 * @return
	 */
	protected boolean validateModuleCode(String key) {
		String code = key.split(":")[0] + ":";// 截取modulecode
		boolean flag = moduleHeadDefineService.validateModuleCode(code);
		logger.info("method:{},cache key is :{},validate success is {}",
				Thread.currentThread().getStackTrace()[1].getMethodName(), key, flag);
		return flag;
	}

}
