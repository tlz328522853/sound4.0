package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.CarOilAnomaly;

/**
 * 
 * @author lms
 */
public interface CarOilAnomalyDao {
	
	void insert(CarOilAnomaly anomaly);
	
	void update(CarOilAnomaly anomaly);
	
	void delete(@Param("oilAnomalyIds") List<Long> oilAnomalyIds);

	List<CarOilAnomaly> findAll(Map<String, Object> param);
	
	CarOilAnomaly findById(@Param("oilAnomalyId") Long oilAnomalyId);

	long getTotalCount(Map<String, Object> param);

}
