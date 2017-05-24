package com.sdcloud.biz.envsanitation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.ReportEntity;

public interface AnalysisReportDao {
	
	public List<ReportEntity> getEmployeeGenderReport(@Param("orgIds") List<Long> orgId);
	
	public List<ReportEntity> getEmployeeAgeReport(@Param("orgIds") List<Long> orgId);
	
	public List<ReportEntity> getFacilityCountReport(@Param("orgIds") List<Long> orgId);
	
	public List<ReportEntity> getCarCountReport(@Param("orgIds") List<Long> orgId);

	public List<ReportEntity> getCarCostReport(@Param("orgIds") List<Long> orgId);
}
