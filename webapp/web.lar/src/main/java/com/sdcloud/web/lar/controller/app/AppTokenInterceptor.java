package com.sdcloud.web.lar.controller.app;

import java.lang.reflect.Field;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import com.sdcloud.api.lar.service.LoginService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@Component
@Aspect
public class AppTokenInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(AppTokenInterceptor.class);
	@Autowired
	private LoginService loginService;

	@Pointcut("  execution(* com.sdcloud.web.lar.controller.app.AppShipmentCitySendController.*(..)) "
			+ "||execution(* com.sdcloud.web.lar.controller.app.AppShipmentHelpMeBuyController.*(..)) "
			+ "||execution(* com.sdcloud.web.lar.controller.app.AppShipmentSendExpressController.*(..))")
	private void TolenMessage() {
	}

	@Around(value = "TolenMessage()")
	public ResultDTO printAroundAdvice(ProceedingJoinPoint point) throws Throwable {
		try {
			// 访问目标方法的参数：
			Object[] args = point.getArgs();
			if (args == null || args.length <= 0) {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "操作非法，请重新登陆");
			}
			String token = null;
			for (Object obj : args) {
				if (obj instanceof HttpHeaders) {
					token = ((HttpHeaders) obj).getFirst("token");
					if (token != null && token.length() > 0) {
						break;
					}
				} else {
					// 通过反射获得请求的token
					try {
						Class objClass = obj.getClass();
						Field field = objClass.getDeclaredField("token");
						field.setAccessible(true);
						token = String.valueOf(field.get(obj));
						if (token != null && token.length() > 0) {
							break;
						}
					} catch (Exception e) {
						logger.info("class is not contain token");
					}
					if (token == null) {
						try {
							Class objClass = obj.getClass().getSuperclass();
							Field field = objClass.getDeclaredField("token");
							field.setAccessible(true);
							token = String.valueOf(field.get(obj));
							if (token != null && token.length() > 0) {
								break;
							}
						} catch (Exception e) {
							logger.info("super class is not contain token");
						}
					}
				}
			}
			// 进行token认证
			Map<String, Object> authorityInfo = loginService.authorityUserByToken(token);
			int code = (Integer) authorityInfo.get("code");
			String message = authorityInfo.get("message").toString();
			if (AppCode.SUCCESS == code) {
				return (ResultDTO) point.proceed();// 认证通过执行方法
			} else {
				return ResultDTO.getFailure(code, message);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
