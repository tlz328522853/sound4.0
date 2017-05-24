package com.sdcloud.biz.cache.redis.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.stereotype.Service;

import com.sdcloud.api.cache.redis.service.ListOperationsService;
import com.sdcloud.biz.cache.redis.dao.ListOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午4:45:39
* ListOperationsServiceImpl描述:
*/
@Service(value="listOperationsService")
public class ListOperationsServiceImpl implements ListOperationsService {
	
	@Autowired
	private ListOperationsDao listOperationsDao;

	@Override
	public List<String> range(String key, long start, long end) {
		// TODO Auto-generated method stub
		return listOperationsDao.range(key, start, end);
	}

	@Override
	public void trim(String key, long start, long end) {
		// TODO Auto-generated method stub
		listOperationsDao.trim(key, start, end);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.size(key);
	}

	@Override
	public Long leftPush(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPush(key, value);
	}

	@Override
	public Long leftPushAll(String key, String... values) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPushAll(key, values);
	}

	@Override
	public Long leftPushAll(String key, Collection<String> values) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPushAll(key, values);
	}

	@Override
	public Long leftPushIfPresent(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPushIfPresent(key, value);
	}

	@Override
	public Long leftPush(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPush(key, value);
	}

	@Override
	public Long rightPush(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPush(key, value);
	}

	@Override
	public Long rightPushAll(String key, String... values) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPushAll(key, values);
	}

	@Override
	public Long rightPushAll(String key, Collection<String> values) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPushAll(key, values);
	}

	@Override
	public Long rightPushIfPresent(String key, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPushIfPresent(key, value);
	}

	@Override
	public Long rightPush(String key, String pivot, String value) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPush(key, value);
	}

	@Override
	public void set(String key, long index, String value) {
		// TODO Auto-generated method stub
		listOperationsDao.set(key, index, value);
	}

	@Override
	public Long remove(String key, long i, Object value) {
		// TODO Auto-generated method stub
		return listOperationsDao.remove(key, i, value);
	}

	@Override
	public String index(String key, long index) {
		// TODO Auto-generated method stub
		return listOperationsDao.index(key, index);
	}

	@Override
	public String leftPop(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPop(key);
	}

	@Override
	public String leftPop(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsDao.leftPop(key);
	}

	@Override
	public String rightPop(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPop(key);
	}

	@Override
	public String rightPop(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPop(key);
	}

	@Override
	public String rightPopAndLeftPush(String sourceKey, String destinationKey) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPopAndLeftPush(sourceKey, destinationKey);
	}

	@Override
	public String rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsDao.rightPopAndLeftPush(sourceKey, destinationKey);
	}


/*****************************************************************/	
	@Override
	public Boolean getOperations_hasKey(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().hasKey(key);
	}

	@Override
	public void getOperations_delete(String key) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().delete(key);
	}

	@Override
	public void getOperations_delete(Collection<String> key) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().delete(key);
	}

	@Override
	public DataType getOperations_type(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().type(key);
	}

	@Override
	public Set<String> getOperations_keys(String pattern) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().keys(pattern);
	}

	@Override
	public String getOperations_randomKey() {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().randomKey();
	}

	@Override
	public void getOperations_rename(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().rename(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_renameIfAbsent(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().renameIfAbsent(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_expire(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().expire(key, timeout, unit);
	}

	@Override
	public Boolean getOperations_expireAt(String key, Date date) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().expireAt(key, date);
	}

	@Override
	public Boolean getOperations_persist(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().persist(key);
	}

	@Override
	public Boolean getOperations_move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().move(key, dbIndex);
	}

	@Override
	public byte[] getOperations_dump(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().dump(key);
	}

	@Override
	public void getOperations_restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().restore(key, value, timeToLive, unit);
	}

	@Override
	public Long getOperations_getExpire(String key) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().getExpire(key);
	}

	@Override
	public Long getOperations_getExpire(String key, TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().getExpire(key,timeUnit);
	}

	@Override
	public void getOperations_watch(String keys) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().watch(keys);
	}

	@Override
	public void getOperations_watch(Collection<String> keys) {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().watch(keys);
	}

	@Override
	public int getOperations_unwatch() {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().unwatch();
		return 1;
	}

	@Override
	public int getOperations_multi() {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().multi();
		return 1;
	}

	@Override
	public int getOperations_discard() {
		// TODO Auto-generated method stub
		listOperationsDao.getOperations().discard();
		return 1;
	}

	@Override
	public List<Object> getOperations_exec() {
		// TODO Auto-generated method stub
		return listOperationsDao.getOperations().exec();
	}



}
