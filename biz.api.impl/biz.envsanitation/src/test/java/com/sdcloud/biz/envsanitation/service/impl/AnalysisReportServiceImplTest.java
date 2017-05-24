package com.sdcloud.biz.envsanitation.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.ReportEntity;
import com.sdcloud.api.envsanitation.service.AnalysisReportService;

import junit.framework.Assert;

/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class AnalysisReportServiceImplTest {
	
	@Autowired
	private AnalysisReportService analysisReportService;
	
	
	@Test
	public void getEmployeeGenderReport(){
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(22l);
		List<ReportEntity> result = analysisReportService.getEmployeeGenderReport(orgIds);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getEmployeeAgeReport(){
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(22l);
		List<ReportEntity> result = analysisReportService.getEmployeeAgeReport(orgIds);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getFacilityCountReport(){
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(22l);
		List<ReportEntity> result = analysisReportService.getFacilityCountReport(orgIds);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getCarCountReport(){
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(22l);
		List<ReportEntity> result = analysisReportService.getCarCountReport(orgIds);
		Assert.assertNotNull(result);
	}
	@Test
	public void getCarCostReport(){
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(22l);
		List<ReportEntity> result = analysisReportService.getCarCostReport(orgIds);
		Assert.assertNotNull(result);
	}
	

}
