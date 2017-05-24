package com.sdcloud.biz.cache.redis.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.sdcloud.biz.cache.redis.dao.ValueOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:41:03
* ValueOperationsDaoImpl描述:
*/
@Repository
public class ValueOperationsDaoImpl implements ValueOperationsDao {
	
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String>  valueOperationRedis;

	@Override
	public void set(String key, String value) {
		// TODO Auto-generated method stub
		valueOperationRedis.set(key, value);

	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		valueOperationRedis.set(key, value, timeout, unit);
	}

	@Override
	public Boolean setIfAbsent(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationRedis.setIfAbsent(key, value);
	}

	@Override
	public void multiSet(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		valueOperationRedis.multiSet(m);
	}

	@Override
	public Boolean multiSetIfAbsent(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		return valueOperationRedis.multiSetIfAbsent(m);
	}

	@Override
	public String get(Object key) {
		// TODO Auto-generated method stub
		return valueOperationRedis.get(key);
	}

	@Override
	public String getAndSet(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationRedis.getAndSet(key, value);
	}

	@Override
	public List<String> multiGet(Collection<String> keys) {
		// TODO Auto-generated method stub
		return valueOperationRedis.multiGet(keys);
	}

	@Override
	public Long increment(String key, long delta) {
		// TODO Auto-generated method stub
		return valueOperationRedis.increment(key, delta);
	}

	@Override
	public Double increment(String key, double delta) {
		// TODO Auto-generated method stub
		return valueOperationRedis.increment(key, delta);
	}

	@Override
	public Integer append(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationRedis.append(key, value);
	}

	@Override
	public String get(String key, long start, long end) {
		// TODO Auto-generated method stub
		return valueOperationRedis.get(key, start, end);
	}

	@Override
	public void set(String key, String value, long offset) {
		// TODO Auto-generated method stub
		valueOperationRedis.set(key, value, offset);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return valueOperationRedis.size(key);
	}

	@Override
	public RedisOperations<String, String> getOperations() {
		// TODO Auto-generated method stub
		return valueOperationRedis.getOperations();
	}

	@Override
	public Boolean setBit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return valueOperationRedis.setBit(key, offset, value);
	}

	@Override
	public Boolean getBit(String key, long offset) {
		// TODO Auto-generated method stub
		return valueOperationRedis.getBit(key, offset);
	}

}
