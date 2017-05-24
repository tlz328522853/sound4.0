package com.sdcloud.biz.cache.redis.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.stereotype.Service;

import com.sdcloud.api.cache.redis.service.ValueOperationsService;
import com.sdcloud.biz.cache.redis.dao.ValueOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:32:08
* ValueOperationsServiceImpl描述:
*/
@Service(value="valueOperationsService")
public class ValueOperationsServiceImpl implements ValueOperationsService {
	
	@Autowired
	private ValueOperationsDao valueOperationsDao;

	@Override
	public void set(String key, String value) {
		valueOperationsDao.set(key, value);

	}

	@Override
	public void set(String key, String value, long timeout, TimeUnit unit) {
		valueOperationsDao.set(key, value, timeout, unit);

	}

	@Override
	public Boolean setIfAbsent(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationsDao.setIfAbsent(key, value);
	}

	@Override
	public void multiSet(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		valueOperationsDao.multiSet(m);

	}

	@Override
	public Boolean multiSetIfAbsent(Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		return valueOperationsDao.multiSetIfAbsent(m);
	}

	@Override
	public String get(Object key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.get(key);
	}

	@Override
	public String getAndSet(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getAndSet(key, value);
	}

	@Override
	public List<String> multiGet(Collection<String> keys) {
		// TODO Auto-generated method stub
		return valueOperationsDao.multiGet(keys);
	}

	@Override
	public Long increment(String key, long delta) {
		// TODO Auto-generated method stub
		return valueOperationsDao.increment(key, delta);
	}

	@Override
	public Double increment(String key, double delta) {
		// TODO Auto-generated method stub
		return valueOperationsDao.increment(key, delta);
	}

	@Override
	public Integer append(String key, String value) {
		// TODO Auto-generated method stub
		return valueOperationsDao.append(key, value);
	}

	@Override
	public String get(String key, long start, long end) {
		// TODO Auto-generated method stub
		return valueOperationsDao.get(key, start, end);
	}

	@Override
	public void set(String key, String value, long offset) {
		// TODO Auto-generated method stub
		valueOperationsDao.set(key, value, offset);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.size(key);
	}

//	@Override
//	public RedisOperations<String, String> getOperations() {
//		// TODO Auto-generated method stub
//		return valueOperationsDao.getOperations();
//	}

	@Override
	public Boolean setBit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return valueOperationsDao.setBit(key, offset, value);
	}

	@Override
	public Boolean getBit(String key, long offset) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getBit(key, offset);
	}

	
/******************************************************************/
	@Override
	public Boolean getOperations_hasKey(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().hasKey(key);
	}

	@Override
	public void getOperations_delete(String key) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().delete(key);
	}

	@Override
	public void getOperations_delete(Collection<String> key) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().delete(key);
	}

	@Override
	public Set<String> getOperations_keys(String pattern) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().keys(pattern);
	}

	@Override
	public String getOperations_randomKey() {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().randomKey();
	}

	@Override
	public void getOperations_rename(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().rename(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_renameIfAbsent(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().renameIfAbsent(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_expire(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().expire(key, timeout, unit);
	}

	@Override
	public Boolean getOperations_expireAt(String key, Date date) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().expireAt(key, date);
	}

	@Override
	public Boolean getOperations_persist(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().persist(key);
	}

	@Override
	public Boolean getOperations_move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().move(key, dbIndex);
	}

	@Override
	public byte[] getOperations_dump(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().dump(key);
	}

	@Override
	public void getOperations_restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().restore(key, value, timeToLive, unit);
	}

	@Override
	public Long getOperations_getExpire(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().getExpire(key);
	}

	@Override
	public Long getOperations_getExpire(String key, TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().getExpire(key);
	}

	@Override
	public void getOperations_watch(String keys) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().watch(keys);
	}

	@Override
	public void getOperations_watch(Collection<String> keys) {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().watch(keys);
	}

	@Override
	public int getOperations_unwatch() {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().unwatch();
		return 1;
	}

	@Override
	public DataType getOperations_type(String key) {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().type(key);
	}

	@Override
	public int getOperations_multi() {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().multi();
		return 1;
	}

	@Override
	public int getOperations_discard() {
		// TODO Auto-generated method stub
		valueOperationsDao.getOperations().discard();
		return 1;
	}

	@Override
	public List<Object> getOperations_exec() {
		// TODO Auto-generated method stub
		return valueOperationsDao.getOperations().exec();
	}

}
