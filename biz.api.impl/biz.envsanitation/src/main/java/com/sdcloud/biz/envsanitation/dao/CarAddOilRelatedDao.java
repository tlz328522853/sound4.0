package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.CarAddOil;
/**
 * 
 * @author lihuiquan
 *
 */
public interface CarAddOilRelatedDao {
	void insert(@Param("carAddOils")List<CarAddOil> carAddOils);
	
	void delete(@Param("carAddOilIds") List<Long> addOidIds);
	
	void update(CarAddOil carAddOil);
	
	List<CarAddOil> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
