package com.sdcloud.biz.authority.dao.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import com.sdcloud.biz.authority.dao.CodeDao;

@Component
public class CodeDaoImpl implements CodeDao {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name="redisTemplate")
	private SetOperations<String, String> type;
	
	/**
	 * 缓存
	 */
	public void addType(String urlType, String ... codes) {
		
		try {
			type.add(urlType, codes);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),urlType,e);
		}
	}
	
	/**
	 * 获取
	 */
	public Set<String> getType(String urlType) {
		
		Set<String> codes = null;
		try {
			codes =  type.members(urlType);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),urlType,e);
		}
		
		return codes;
	}
	
	/**
	 * 删除
	 */
	public void removeType(String urlType, String code) {
		
		try {
			type.remove(urlType, code);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),urlType,e);
		}
	}
}
