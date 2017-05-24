package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.EmpRoutePlan;
import com.sdcloud.api.envsanitation.service.EmpRoutePlanService;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class EmpRoutePlanServiceImplTest {
	
	@Autowired
	private EmpRoutePlanService empRoutePlanService;
	
	
	@Test
	public void insertTest() throws Exception{
		EmpRoutePlan empRoutePlan = new EmpRoutePlan();
		//empRoutePlan.setEmpName("海淀南路线");
		empRoutePlan.setEmpId(1L);
		//empRoutePlan.setWorkTimeStart(workTimeStart);
		//empRoutePlan.setWorkTimeEnd(workTimeEnd);
		empRoutePlan.setWorkPlanLength(220.34);
		empRoutePlan.setWorkPlanName("海淀南路线");
		empRoutePlan.setWorkPlanPosition("[{12,123},{123,123}]");
		empRoutePlan.setBounds(0);
		empRoutePlan.setRemark("这就是备注");
		long id = empRoutePlanService.insert(empRoutePlan);
		
	}
	
	@Test
	public void updateTest() throws Exception{
		
		EmpRoutePlan empRoutePlan = new EmpRoutePlan();
		empRoutePlan.setEmpRoutePlanId(6197272205046119L);
		//empRoutePlan.setEmpName("海淀南路线");
		empRoutePlan.setEmpId(1L);
		//empRoutePlan.setWorkTimeStart(workTimeStart);
		//empRoutePlan.setWorkTimeEnd(workTimeEnd);
		empRoutePlan.setWorkPlanLength(221.34);
		empRoutePlan.setWorkPlanName("海淀南路线_1");
		empRoutePlan.setWorkPlanPosition("[{12,123},{123,123}]");
		empRoutePlan.setBounds(0);
		empRoutePlan.setRemark("这就是备注1");
		empRoutePlanService.update(empRoutePlan);
		
	}
	
	@Test
	public void deleteTest() throws Exception{
		List<Long> ids = new ArrayList<Long>();
		ids.add(6886318019922523L);
		empRoutePlanService.delete(ids);
	}
	
	@Test
	public void findAllByOrgIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(3L);
		params.put("orgIds", orgIds);
		Pager<EmpRoutePlan> res = empRoutePlanService.findAll(params, null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByEmpIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> empIds = new ArrayList<Long>();
		empIds.add(1L);
		params.put("empIds", empIds);
		Pager<EmpRoutePlan> res = empRoutePlanService.findAll(params, null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByPagerTest() throws Exception{
		Pager pager = new Pager();
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(3L);
		params.put("orgIds", orgIds);
		Pager<EmpRoutePlan> res = empRoutePlanService.findAll(params, pager);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
}
