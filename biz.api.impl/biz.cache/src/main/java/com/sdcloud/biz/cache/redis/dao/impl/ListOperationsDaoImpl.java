package com.sdcloud.biz.cache.redis.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Repository;

import com.sdcloud.biz.cache.redis.dao.ListOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午4:45:39
* ListOperationsServiceImpl描述:
*/
@Repository
public class ListOperationsDaoImpl implements ListOperationsDao {
	
	@Resource(name = "redisTemplate")
	private ListOperations<String, String>  listOperationsRedis;

	@Override
	public List<String> range(String key, long start, long end) {
		// TODO Auto-generated method stub
		return listOperationsRedis.range(key, start, end);
	}

	@Override
	public void trim(String key, long start, long end) {
		// TODO Auto-generated method stub
		listOperationsRedis.trim(key, start, end);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return listOperationsRedis.size(key);
	}

	@Override
	public Long leftPush(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPush(key, value);
	}

	@Override
	public Long leftPushAll(String key, String... values) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPushAll(key, values);
	}

	@Override
	public Long leftPushAll(String key, Collection<String> values) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPushAll(key, values);
	}

	@Override
	public Long leftPushIfPresent(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPushIfPresent(key, value);
	}

	@Override
	public Long leftPush(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPush(key, value);
	}

	@Override
	public Long rightPush(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPush(key, value);
	}

	@Override
	public Long rightPushAll(String key, String... values) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPushAll(key, values);
	}

	@Override
	public Long rightPushAll(String key, Collection<String> values) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPushAll(key, values);
	}

	@Override
	public Long rightPushIfPresent(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPushIfPresent(key, value);
	}

	@Override
	public Long rightPush(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPush(key, value);
	}

	@Override
	public void set(String key, long index, String value) {
		// TODO Auto-generated method stub
		listOperationsRedis.set(key, index, value);
	}

	@Override
	public Long remove(String key, long i, Object value) {
		// TODO Auto-generated method stub
		return listOperationsRedis.remove(key, i, value);
	}

	@Override
	public String index(String key, long index) {
		// TODO Auto-generated method stub
		return listOperationsRedis.index(key, index);
	}

	@Override
	public String leftPop(String key) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPop(key);
	}

	@Override
	public String leftPop(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsRedis.leftPop(key);
	}

	@Override
	public String rightPop(String key) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPop(key);
	}

	@Override
	public String rightPop(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPop(key);
	}

	@Override
	public String rightPopAndLeftPush(String sourceKey, String destinationKey) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPopAndLeftPush(sourceKey, destinationKey);
	}

	@Override
	public String rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsRedis.rightPopAndLeftPush(sourceKey, destinationKey);
	}

	@Override
	public RedisOperations<String, String> getOperations() {
		// TODO Auto-generated method stub
		return listOperationsRedis.getOperations();
	}

}
