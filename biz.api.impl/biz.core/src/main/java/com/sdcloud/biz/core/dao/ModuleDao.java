package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Module;

/**
 * 
 * @author lms
 */
public interface ModuleDao {
	
	void insert(Module module);
	
	void delete(@Param("moduleIds") List<Long> moduleIds);
	
	void update(Module module);

	Module findById(@Param("moduleId") Long moduleId);

	List<Module> findAll(Map<String, Object> param);
	List<Module> findAllModule(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	List<Module> findModuleByAuthority(Map<String, Object> param);
}
