package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Dic;

/**
 * 
 * @author lms
 */
public interface DicDao {
	
	void insert(Dic dic);
	
	int update(Map<String, Object> param);
//	void update(Dic dic);
	
	int delete(Map<String, Object> param);
//	void delete(@Param("dicIds") List<Long> dicIds);

	List<Dic> findAll(Map<String, Object> param);
	
	Dic findById(@Param("dicId") Long dicId);

	long getTotalCount(Map<String, Object> param);

	/*List<Dic> findByPid(@Param("pid") Long pid);*/
	List<Dic> findByPid(Map<String, Object> param);
	
	List<Dic> findByIds(@Param("ids")List<Long> ids);
	
	List<Long> findTenantDicByPid(@Param("pIds")List<Long> pIds,@Param("tenantIds")List<Long> tenantIds);
}
