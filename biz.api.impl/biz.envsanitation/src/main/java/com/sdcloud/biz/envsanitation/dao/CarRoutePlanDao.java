package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.CarRoutePlan;

/**
 * 环卫车辆作业路线
 * @author czz
 *
 */
public interface CarRoutePlanDao {
	
	List<CarRoutePlan> findAll(Map<String, Object> params);//获取某部门的所有路线规划{orgIds, empIds, assetIds}
	
	Long getTotalNum(Map<String, Object> params);//获取符合条件的总数
	
	void insert(CarRoutePlan carRoutePlan);
	
	void update(CarRoutePlan carRoutePlan);
	
	void delete(@Param("carRoutePlanIds")List<Long> carRoutePlanIds);
}
