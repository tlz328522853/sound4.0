package com.sdcloud.biz.cache.redis.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import com.sdcloud.api.cache.redis.service.HashOperationsService;
import com.sdcloud.biz.cache.redis.dao.HashOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:52:57
* HashOperationsServiceImpl描述:
*/
@Service(value="hashOperationsService")
public class HashOperationsServiceImpl implements HashOperationsService {

	@Autowired
	private HashOperationsDao hashOperationsDao;
	
	@Override
	public void delete(String key, Object... hashKeys) {
		// TODO Auto-generated method stub
		hashOperationsDao.delete(key, hashKeys);
	}

	@Override
	public Boolean hasKey(String key, Object hashKey) {
		// TODO Auto-generated method stub
		return hashOperationsDao.hasKey(key, hashKey);
	}

	@Override
	public String get(String key, Object hashKey) {
		// TODO Auto-generated method stub
		return hashOperationsDao.get(key, hashKey);
	}

	@Override
	public List<String> multiGet(String key, Collection<String> hashKeys) {
		// TODO Auto-generated method stub
		return hashOperationsDao.multiGet(key, hashKeys);
	}

	@Override
	public Long increment(String key, String hashKey, long delta) {
		// TODO Auto-generated method stub
		return hashOperationsDao.increment(key, hashKey, delta);
	}

	@Override
	public Double increment(String key, String hashKey, double delta) {
		// TODO Auto-generated method stub
		return hashOperationsDao.increment(key, hashKey, delta);
	}

	@Override
	public Set<String> keys(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.keys(key);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.size(key);
	}

	@Override
	public void putAll(String key, Map<? extends String, ? extends String> m) {
		// TODO Auto-generated method stub
		hashOperationsDao.putAll(key, m);
	}

	@Override
	public void put(String key, String hashKey, String value) {
		// TODO Auto-generated method stub
		hashOperationsDao.put(key, hashKey, value);
	}

	@Override
	public Boolean putIfAbsent(String key, String hashKey, String value) {
		// TODO Auto-generated method stub
		return hashOperationsDao.putIfAbsent(key, hashKey, value);
	}

	@Override
	public List<String> values(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.values(key);
	}

	@Override
	public Map<String, String> entries(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.entries(key);
	}

//	@Override
//	public RedisOperations<String, ?> getOperations() {
//		// TODO Auto-generated method stub
//		return hashOperationsDao.getOperations();
//	}
//
//	@Override
//	public Cursor<Entry<String, String>> scan(String key, ScanOptions options) {
//		// TODO Auto-generated method stub
//		return hashOperationsDao.scan(key, options);
//	}

/*******************************************************************/	
	@Override
	public Boolean getOperations_hasKey(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().hasKey(key);
	}

	@Override
	public void getOperations_delete(String key) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().delete(key);
	}

	@Override
	public void getOperations_delete(Collection<String> key) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().delete(key);
	}

	@Override
	public DataType getOperations_type(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().type(key);
	}

	@Override
	public Set<String> getOperations_keys(String pattern) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().keys(pattern);
	}

	@Override
	public String getOperations_randomKey() {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().randomKey();
	}

	@Override
	public void getOperations_rename(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().rename(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_renameIfAbsent(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().renameIfAbsent(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_expire(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().expire(key, timeout, unit);
	}

	@Override
	public Boolean getOperations_expireAt(String key, Date date) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().expireAt(key, date);
	}

	@Override
	public Boolean getOperations_persist(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().persist(key);
	}

	@Override
	public Boolean getOperations_move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().move(key, dbIndex);
	}

	@Override
	public byte[] getOperations_dump(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().dump(key);
	}

	@Override
	public void getOperations_restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().restore(key, value, timeToLive, unit);
	}

	@Override
	public Long getOperations_getExpire(String key) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().getExpire(key);
	}

	@Override
	public Long getOperations_getExpire(String key, TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().getExpire(key, timeUnit);
	}

	@Override
	public void getOperations_watch(String keys) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().watch(keys);
	}

	@Override
	public void getOperations_watch(Collection<String> keys) {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().watch(keys);
	}

	@Override
	public int getOperations_unwatch() {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().unwatch();
		return 1;
	}

	@Override
	public int getOperations_multi() {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().multi();
		return 1;
	}

	@Override
	public int getOperations_discard() {
		// TODO Auto-generated method stub
		hashOperationsDao.getOperations().discard();
		return 1;
	}

	@Override
	public List<Object> getOperations_exec() {
		// TODO Auto-generated method stub
		return hashOperationsDao.getOperations().exec();
	}

	
/*******************************************************************/
	@Override
	public long scan_getCursorId(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return hashOperationsDao.scan(key, options).getCursorId();
	}

	@Override
	public boolean scan_isClosed(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return hashOperationsDao.scan(key, options).isClosed();
	}

	@Override
	public <T> Cursor<T> scan_open(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return (Cursor<T>) hashOperationsDao.scan(key, options).open();
	}

	@Override
	public long scan_getPosition(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return hashOperationsDao.scan(key, options).getPosition();
	}

}
