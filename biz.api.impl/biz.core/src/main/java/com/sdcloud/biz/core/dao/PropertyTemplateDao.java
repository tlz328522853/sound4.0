package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.PropertyTemplate;

/**
 * 
 * @author lms
 */
public interface PropertyTemplateDao {
	
	void insert(PropertyTemplate propTpl);
	
	void update(PropertyTemplate propTpl);
	
	void delete(@Param("propTplIds") List<Long> propTplIds);

	List<PropertyTemplate> findAll(Map<String, Object> param);
	
	PropertyTemplate findById(@Param("propTplId") Long propTplId);

	long getTotalCount(Map<String, Object> param);

	List<PropertyTemplate> findByPid(@Param("pid") Long pid);

}
