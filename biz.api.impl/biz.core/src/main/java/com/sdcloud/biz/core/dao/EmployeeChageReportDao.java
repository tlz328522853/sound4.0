package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.EmployeeChageReport;
import com.sdcloud.api.core.entity.EmployeeInnerReport;


/**
 * 
 * @author lihuiquan
 *
 */
public interface EmployeeChageReportDao {
	void insert(@Param("employeeChageReports")List<EmployeeChageReport> employeeChageReports);
	
	void insertInner(@Param("employeeChageReports")List<EmployeeChageReport> employeeChageReports);
	
	void delete(@Param("employeeChageReportIds") List<Long> employeeChageReportIds);
	
	void update(EmployeeChageReport employeeChageReport);
	
	void updateReason(EmployeeChageReport employeeChageReport);
	
	List<EmployeeChageReport> findAllBy(Map<String, Object> param);
	
	List<EmployeeInnerReport> findAllInnerBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	long getInnerTotalCount(Map<String, Object> param);
	
	void updateParam(Map<String,Object> param);
}
