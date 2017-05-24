package com.sdcloud.biz.cache.redis.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.sdcloud.biz.cache.redis.dao.SetOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:51:57
* SetOperationsServiceImpl描述:
*/
@Repository
public class SetOperationsDaoImpl implements SetOperationsDao {

	@Resource(name = "redisTemplate")
	private SetOperations<String, String>  setOperationsRedis;
	
	@Override
	public Set<String> difference(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.difference(otherKey, otherKey);
	}

	@Override
	public Set<String> difference(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsRedis.difference(key, otherKeys);
	}

	@Override
	public Long differenceAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.differenceAndStore(key, otherKey, destKey);
	}

	@Override
	public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.differenceAndStore(key, otherKeys, destKey);
	}

	@Override
	public Set<String> intersect(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.intersect(key, otherKey);
	}

	@Override
	public Set<String> intersect(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsRedis.intersect(key, otherKeys);
	}

	@Override
	public Long intersectAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.intersectAndStore(key, otherKey, destKey);
	}

	@Override
	public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.intersectAndStore(destKey, otherKeys, destKey);
	}

	@Override
	public Set<String> union(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.union(key, otherKey);
	}

	@Override
	public Set<String> union(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsRedis.union(key, otherKeys);
	}

	@Override
	public Long unionAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.unionAndStore(key, otherKey, destKey);
	}

	@Override
	public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.unionAndStore(key, otherKeys, destKey);
	}

	@Override
	public Long add(String key, String... values) {
		// TODO Auto-generated method stub
		return setOperationsRedis.add(key, values);
	}

	@Override
	public Boolean isMember(String key, Object o) {
		// TODO Auto-generated method stub
		return setOperationsRedis.isMember(key, o);
	}

	@Override
	public Set<String> members(String key) {
		// TODO Auto-generated method stub
		return setOperationsRedis.members(key);
	}

	@Override
	public Boolean move(String key, String value, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsRedis.move(key, value, destKey);
	}

	@Override
	public String randomMember(String key) {
		// TODO Auto-generated method stub
		return setOperationsRedis.randomMember(key);
	}

	@Override
	public Set<String> distinctRandomMembers(String key, long count) {
		// TODO Auto-generated method stub
		return setOperationsRedis.distinctRandomMembers(key, count);
	}

	@Override
	public List<String> randomMembers(String key, long count) {
		// TODO Auto-generated method stub
		return setOperationsRedis.randomMembers(key, count);
	}

	@Override
	public Long remove(String key, Object... values) {
		// TODO Auto-generated method stub
		return setOperationsRedis.remove(key, values);
	}

	@Override
	public String pop(String key) {
		// TODO Auto-generated method stub
		return setOperationsRedis.pop(key);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return setOperationsRedis.size(key);
	}

	@Override
	public RedisOperations<String, String> getOperations() {
		// TODO Auto-generated method stub
		return setOperationsRedis.getOperations();
	}

	@Override
	public Cursor<String> scan(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return setOperationsRedis.scan(key, options);
	}

}
