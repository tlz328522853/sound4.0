package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.FormTemplate;

/**
 * 
 * @author lms
 */
public interface FormTemplateDao {
	
	void insert(FormTemplate formTplI);
	
	void update(FormTemplate formTplI);
	
	void delete(@Param("formTplIds") List<Long> formTplIds);

	List<FormTemplate> findAll(Map<String, Object> param);
	
	FormTemplate findById(@Param("formTplId") Long formTplId);

	long getTotalCount(Map<String, Object> param);

	List<FormTemplate> findByPid(@Param("pid") Long pid);

}
