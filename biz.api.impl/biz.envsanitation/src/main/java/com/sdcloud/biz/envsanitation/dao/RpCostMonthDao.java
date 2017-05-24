package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpCostMonth;

/**
 * 
 * @author dc
 */
public interface RpCostMonthDao {
	
	void insert(List<RpCostMonth> costMonths);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpCostMonth> findAllBy(Map<String, Object> param);

	void update(RpCostMonth costMonth);

	void delete(@Param("ids") List<Long> ids);
	
	void file(RpCostMonth rpCostMonth);

	List<RpCostMonth> findRpCostMonthByParam(Map<String, Object> param);

}