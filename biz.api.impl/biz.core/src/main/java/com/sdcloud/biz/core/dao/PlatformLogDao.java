package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.PlatformLog;


/**
 * 
 * @author lihuiquan
 *
 */
public interface PlatformLogDao {
	void insert(PlatformLog platformLog);
	
	void delete(@Param("platformLogIds") List<Long> platformLogIds);
	
	void update(PlatformLog platformLog);
	
	List<PlatformLog> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
