package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpSecurityProductionMonth;

/**
 * 
 * @author dc
 */
public interface RpSecurityProductionMonthDao {
	
	void insert(List<RpSecurityProductionMonth> securityProductionMonths);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpSecurityProductionMonth> findAllBy(Map<String, Object> param);

	void update(RpSecurityProductionMonth securityProductionMonth);

	void delete(@Param("ids") List<Long> ids);

	List<RpSecurityProductionMonth> findRpSecurityProductionMonthByParam(Map<String, Object> param);

}