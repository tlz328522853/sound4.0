package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.CarAddWater;
/**
 * 
 * @author lihuiquan
 *
 */
public interface CarAddWaterRelatedDao {
	void insert(@Param("carAddWaters") List<CarAddWater> carAddWaters);
	
	void delete(@Param("carAddWaterIds") List<Long> carAddWaterIds);
	
	void update(CarAddWater carAddWater);
	
	List<CarAddWater> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
