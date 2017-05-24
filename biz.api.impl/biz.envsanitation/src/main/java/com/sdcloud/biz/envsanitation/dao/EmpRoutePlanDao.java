package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.EmpRoutePlan;

/**
 * 环卫人员作业路线
 * @author czz
 *
 */
public interface EmpRoutePlanDao {
	
	List<EmpRoutePlan> findAll(Map<String, Object> params);//获取某部门的所有路线规划{orgId, empId}
	
	Long getTotalNum(Map<String, Object> params);//获取符合条件的总数
	
	void insert(EmpRoutePlan empRoutePlan);
	
	void update(EmpRoutePlan empRoutePlan);
	
	void delete(@Param("empRoutePlanIds")List<Long> empRoutePlanIds);
}
