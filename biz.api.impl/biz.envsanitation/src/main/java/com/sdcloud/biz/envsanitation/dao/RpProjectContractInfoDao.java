package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpProjectContractInfo;

/**
 * 
 * @author dc
 */
public interface RpProjectContractInfoDao {
	
	void insert(List<RpProjectContractInfo> projectContractInfos);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpProjectContractInfo> findAllBy(Map<String, Object> param);

	void update(RpProjectContractInfo projectContractInfo);

	void delete(@Param("ids") List<Long> ids);

	void file(RpProjectContractInfo projectContractInfo);

}