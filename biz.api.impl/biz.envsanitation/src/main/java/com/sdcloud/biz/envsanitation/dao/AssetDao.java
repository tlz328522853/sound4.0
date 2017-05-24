package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author lms
 * @param <T>
 */
public interface AssetDao<T> {
	
//	void insert(T t);
	void insert(List<T> ts);
	
	void delete(@Param("assetIds") List<Long> assetIds);
	
	void update(T t);
	
	List<T> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);

	T findById(@Param("assetId") Long assetId);

}
