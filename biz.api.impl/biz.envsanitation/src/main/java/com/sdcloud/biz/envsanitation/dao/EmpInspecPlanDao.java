package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.EmpInspecPlan;

/**
 * 巡查人员作业路线
 * @author czz
 *
 */
public interface EmpInspecPlanDao {
	
	List<EmpInspecPlan> findAll(Map<String, Object> params);//获取某部门的所有路线规划{orgIds, empIds, assetIds}
	
	Long getTotalNum(Map<String, Object> params);//获取符合条件的总数
	
	void insert(EmpInspecPlan empInspecPlan);
	
	void update(EmpInspecPlan empInspecPlan);
	
	void delete(@Param("empInspecPlanIds")List<Long> empInspecPlanIds);
}
