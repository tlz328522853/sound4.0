package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpProjectInfo;

/**
 * 
 * @author dc
 */
public interface RpProjectInfoDao {
	
	void insert(List<RpProjectInfo> projectInfos);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpProjectInfo> findAllBy(Map<String, Object> param);

	void update(RpProjectInfo projectInfo);

	void delete(@Param("ids") List<Long> ids);

}