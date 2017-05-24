package com.sdcloud.biz.authority.dao.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.sdcloud.biz.authority.dao.CaptchaDao;

@Component
public class CaptchaDaoImpl implements CaptchaDao {

	// 设置过期时间expire
	private static final long DATA_TIME_OUT = 60000;

	@Resource(name = "redisTemplate")
	private SetOperations<String, String> setOp;
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOp;
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOp;

	@Override
	public void add(String key, String value) {

		valOp.set(key, value, DATA_TIME_OUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public String get(String key) {

		RedisOperations<String, String> operations = valOp.getOperations();
		Long expire = operations.getExpire(key);
		if (expire <= DATA_TIME_OUT) {
			operations.expire(key, DATA_TIME_OUT, TimeUnit.MILLISECONDS);
		}
		return valOp.get(key);
	}

	@Override
	public void remove(String key) {
		valOp.getOperations().delete(key);
	}

}
