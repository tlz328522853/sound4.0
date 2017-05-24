package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Project;

/**
 * 
 * @author lms
 */
public interface ProjectDao {

	int insert(Project project);

	int update(Map<String, Object> param);

	int delete(Map<String, Object> param);

	Project findById(@Param("orgId") Long orgId);
	
	List<Project> findAll(Map<String, Object> param);
	
	Long findForDup(@Param("districtCode") String districtCode, @Param("orgId") Long orgId);
	
	List<Project> findMonitorTree(Map<String, Object> param);

}
