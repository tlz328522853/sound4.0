package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Table;

/**
 * 
 * @author lms
 */
public interface TableDao {
	
	void insert(Table tbl);
	
	void update(Table tbl);
	
	void delete(@Param("tblIds") List<Long> tblIds);

	List<Table> findAll(Map<String, Object> param);
	
	Table findById(@Param("tblId") Long tblId);

	long getTotalCount(Map<String, Object> param);

	List<Table> findByPid(@Param("pid") Long pid);

}
