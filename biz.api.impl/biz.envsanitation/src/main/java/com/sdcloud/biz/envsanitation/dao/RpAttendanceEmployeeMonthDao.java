package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpAttendanceEmployeeMonth;

/**
 * 
 * @author dc
 */
public interface RpAttendanceEmployeeMonthDao {
	
	void insert(List<RpAttendanceEmployeeMonth> attendanceEmployeeMonths);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpAttendanceEmployeeMonth> findAllBy(Map<String, Object> param);

	void update(RpAttendanceEmployeeMonth attendanceEmployeeMonth);

	void delete(@Param("ids") List<Long> ids);

	List<RpAttendanceEmployeeMonth> findRpAttendanceEmployeeMonthByParam(Map<String, Object> param);

}