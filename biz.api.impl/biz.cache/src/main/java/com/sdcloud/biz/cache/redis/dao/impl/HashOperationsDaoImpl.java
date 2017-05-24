package com.sdcloud.biz.cache.redis.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import com.sdcloud.biz.cache.redis.dao.HashOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:52:57
* HashOperationsServiceImpl描述:
*/
@Repository
public class HashOperationsDaoImpl implements HashOperationsDao {

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String>  hashOperationsRedis;
	
	@Override
	public void delete(String key, Object... hashKeys) {
		// TODO Auto-generated method stub
		hashOperationsRedis.delete(key, hashKeys);
	}

	@Override
	public Boolean hasKey(String key, Object hashKey) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.hasKey(key, hashKey);
	}

	@Override
	public String get(String key, Object hashKey) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.get(key, hashKey);
	}

	@Override
	public List<String> multiGet(String key, Collection<String> hashKeys) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.multiGet(key, hashKeys);
	}

	@Override
	public Long increment(String key, String hashKey, long delta) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.increment(key, hashKey, delta);
	}

	@Override
	public Double increment(String key, String hashKey, double delta) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.increment(key, hashKey, delta);
	}

	@Override
	public Set<String> keys(String key) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.keys(key);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.size(key);
	}

	@Override
	public void putAll(String key, Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		hashOperationsRedis.putAll(key, m);
	}

	@Override
	public void put(String key, String hashKey, String value) {
		// TODO Auto-generated method stub
		hashOperationsRedis.put(key, hashKey, value);
	}

	@Override
	public Boolean putIfAbsent(String key, String hashKey, String value) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.putIfAbsent(key, hashKey, value);
	}

	@Override
	public List<String> values(String key) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.values(key);
	}

	@Override
	public Map<String, String> entries(String key) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.entries(key);
	}

	@Override
	public RedisOperations<String, ?> getOperations() {
		// TODO Auto-generated method stub
		return hashOperationsRedis.getOperations();
	}

	@Override
	public Cursor<Entry<String, String>> scan(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return hashOperationsRedis.scan(key, options);
	}

}
