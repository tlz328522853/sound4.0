package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.TableField;

/**
 * 
 * @author lms
 */
public interface TableFieldDao {
	
	void insert(TableField field);
	
	void update(TableField field);
	
	void delete(@Param("fieldIds") List<Long> fieldIds);

	List<TableField> findAll(Map<String, Object> param);
	
	TableField findById(@Param("fieldId") Long fieldId);

	long getTotalCount(Map<String, Object> param);

	List<TableField> findByPid(@Param("pid") Long pid);

	List<String> find(Map<String, Object> param);

}
