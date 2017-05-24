package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpIncomePayMonth;

/**
 * 
 * @author dc
 */
public interface RpIncomePayMonthDao {
	
	void insert(List<RpIncomePayMonth> incomePayMonths);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpIncomePayMonth> findAllBy(Map<String, Object> param);

	void update(RpIncomePayMonth incomePayMonth);

	void delete(@Param("ids") List<Long> ids);
	
	void file(RpIncomePayMonth rpIncomePayMonth);

	List<RpIncomePayMonth> findRpIncomePayMonthByParam(Map<String, Object> param);

}