package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpAccidentCar;

/**
 * 
 * @author dc
 */
public interface RpAccidentCarDao {
	
	void insert(List<RpAccidentCar> rpAccidentCars);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpAccidentCar> findAllBy(Map<String, Object> param);

	void update(RpAccidentCar rpAccidentCar);

	void delete(@Param("ids") List<Long> ids);

}