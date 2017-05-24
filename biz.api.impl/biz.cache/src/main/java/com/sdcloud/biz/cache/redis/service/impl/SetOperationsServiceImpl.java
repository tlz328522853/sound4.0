package com.sdcloud.biz.cache.redis.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import com.sdcloud.api.cache.redis.service.SetOperationsService;
import com.sdcloud.biz.cache.redis.dao.SetOperationsDao;

/**
* @author jzc
* @version 2016年9月13日 下午1:51:57
* SetOperationsServiceImpl描述:
*/
@Service(value="setOperationsService")
public class SetOperationsServiceImpl implements SetOperationsService {

	@Autowired
	private SetOperationsDao setOperationsDao;
	
	@Override
	public Set<String> difference(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.difference(otherKey, otherKey);
	}

	@Override
	public Set<String> difference(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsDao.difference(key, otherKeys);
	}

	@Override
	public Long differenceAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.differenceAndStore(key, otherKey, destKey);
	}

	@Override
	public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.differenceAndStore(key, otherKeys, destKey);
	}

	@Override
	public Set<String> intersect(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.intersect(key, otherKey);
	}

	@Override
	public Set<String> intersect(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsDao.intersect(key, otherKeys);
	}

	@Override
	public Long intersectAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.intersectAndStore(key, otherKey, destKey);
	}

	@Override
	public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.intersectAndStore(destKey, otherKeys, destKey);
	}

	@Override
	public Set<String> union(String key, String otherKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.union(key, otherKey);
	}

	@Override
	public Set<String> union(String key, Collection<String> otherKeys) {
		// TODO Auto-generated method stub
		return setOperationsDao.union(key, otherKeys);
	}

	@Override
	public Long unionAndStore(String key, String otherKey, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.unionAndStore(key, otherKey, destKey);
	}

	@Override
	public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.unionAndStore(key, otherKeys, destKey);
	}

	@Override
	public Long add(String key, String... values) {
		// TODO Auto-generated method stub
		return setOperationsDao.add(key, values);
	}

	@Override
	public Boolean isMember(String key, Object o) {
		// TODO Auto-generated method stub
		return setOperationsDao.isMember(key, o);
	}

	@Override
	public Set<String> members(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.members(key);
	}

	@Override
	public Boolean move(String key, String value, String destKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.move(key, value, destKey);
	}

	@Override
	public String randomMember(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.randomMember(key);
	}

	@Override
	public Set<String> distinctRandomMembers(String key, long count) {
		// TODO Auto-generated method stub
		return setOperationsDao.distinctRandomMembers(key, count);
	}

	@Override
	public List<String> randomMembers(String key, long count) {
		// TODO Auto-generated method stub
		return setOperationsDao.randomMembers(key, count);
	}

	@Override
	public Long remove(String key, Object... values) {
		// TODO Auto-generated method stub
		return setOperationsDao.remove(key, values);
	}

	@Override
	public String pop(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.pop(key);
	}

	@Override
	public Long size(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.size(key);
	}

	
	/**********************************************************************/
	@Override
	public Boolean getOperations_hasKey(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().hasKey(key);
	}

	@Override
	public void getOperations_delete(String key) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().delete(key);
	}

	@Override
	public void getOperations_delete(Collection<String> key) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().delete(key);
	}

	@Override
	public Set<String> getOperations_keys(String pattern) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().keys(pattern);
	}

	@Override
	public String getOperations_randomKey() {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().randomKey();
	}

	@Override
	public void getOperations_rename(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().rename(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_renameIfAbsent(String oldKey, String newKey) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().renameIfAbsent(oldKey, newKey);
	}

	@Override
	public Boolean getOperations_expire(String key, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().expire(key, timeout, unit);
	}

	@Override
	public Boolean getOperations_expireAt(String key, Date date) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().expireAt(key, date);
	}

	@Override
	public Boolean getOperations_persist(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().persist(key);
	}

	@Override
	public Boolean getOperations_move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().move(key, dbIndex);
	}

	@Override
	public byte[] getOperations_dump(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().dump(key);
	}

	@Override
	public void getOperations_restore(String key, byte[] value, long timeToLive, TimeUnit unit) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().restore(key, value, timeToLive, unit);
	}

	@Override
	public Long getOperations_getExpire(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().getExpire(key);
	}

	@Override
	public Long getOperations_getExpire(String key, TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().getExpire(key, timeUnit);
	}

	@Override
	public void getOperations_watch(String keys) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().watch(keys);
	}

	@Override
	public void getOperations_watch(Collection<String> keys) {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().watch(keys);
	}

	@Override
	public int getOperations_unwatch() {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().unwatch();
		return 1;
	}

	@Override
	public int getOperations_multi() {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().multi();
		return 1;
	}

	@Override
	public int getOperations_discard() {
		// TODO Auto-generated method stub
		setOperationsDao.getOperations().discard();
		return 1;
	}
	
	@Override
	public DataType getOperations_type(String key) {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().type(key);
	}

	@Override
	public List<Object> getOperations_exec() {
		// TODO Auto-generated method stub
		return setOperationsDao.getOperations().exec();
	}

	
/*****************************************************************/	
	@Override
	public long scan_getCursorId(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return setOperationsDao.scan(key, options).getCursorId();
	}

	@Override
	public boolean scan_isClosed(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return setOperationsDao.scan(key, options).isClosed();
	}

	@Override
	public long scan_getPosition(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return setOperationsDao.scan(key, options).getPosition();
	}

	@Override
	public <T> Cursor<T> scan_open(String key, ScanOptions options) {
		// TODO Auto-generated method stub
		return (Cursor<T>) setOperationsDao.scan(key, options).open();
	}
	

}
