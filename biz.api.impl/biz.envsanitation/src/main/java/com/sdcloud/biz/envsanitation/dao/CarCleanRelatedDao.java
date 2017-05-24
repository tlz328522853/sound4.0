package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.CarClean;
/**
 * 
 * @author lihuiquan
 *
 */
public interface CarCleanRelatedDao {
	void insert(@Param("carCleans")List<CarClean> carCleans);
	
	void delete(@Param("carCleanIds") List<Long> carCleanIds);
	
	void update(CarClean carClean);
	
	List<CarClean> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
