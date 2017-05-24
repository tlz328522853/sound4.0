package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpOperationQualityMonth;

/**
 * 
 * @author dc
 */
public interface RpOperationQualityMonthDao {
	
	void insert(List<RpOperationQualityMonth> operationQualityMonths);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpOperationQualityMonth> findAllBy(Map<String, Object> param);

	void update(RpOperationQualityMonth operationQualityMonth);

	void delete(@Param("ids") List<Long> ids);

	List<RpOperationQualityMonth> findRpOperationQualityMonthByParam(Map<String, Object> param);

}