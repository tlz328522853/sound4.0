package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.sdcloud.api.envsanitation.entity.AppVersion;
/**
 * 
 * @author lihuiquan
 *
 */
public interface AppVersionDao {
	void insert(@Param("appVersions")List<AppVersion> appVersions);
	
	void delete(@Param("appVersionIds") List<Long> appVersionIds);
	
	void update(AppVersion appVersion);
	
	List<AppVersion> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	/**
	 * 根据id查找版本
	 * @param appVersion
	 * @throws Exception
	 */
	AppVersion findById(Long appVersionId); 
}
