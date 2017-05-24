package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Function;

/**
 * 
 * @author lms
 */
public interface FunctionDao {
	
	void insert(Function function);
	
	void update(Function function);
	
	void delete(@Param("funcIds") List<Long> funcIds);

	void deleteByModuleId(@Param("moduleIds") List<Long> moduleIds);

	List<Function> findByModuleId(Map<String, Object> params);
	
	List<Function> findByPid(@Param("pid") Long pid);

	List<Function> findById(@Param("funcIds") List<Long> funcIds);
	
	List<Function> findAll(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	List<String> findUrlByUserId(@Param("userId")String userId);
	
	List<String> findUrls(Map<String, Object> params);
	List<Long> findFunIdByModuleId(Map<String, Object> params);
	
	List<Function> findAuthenFunction(@Param("moduleId")Long moduleId,@Param("pid")Long pId);
}
