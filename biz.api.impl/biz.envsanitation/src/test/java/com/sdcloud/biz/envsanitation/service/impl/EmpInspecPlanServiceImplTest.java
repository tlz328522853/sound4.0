package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.EmpInspecPlan;
import com.sdcloud.api.envsanitation.service.EmpInspecPlanService;
import com.sdcloud.framework.entity.Pager;


/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class EmpInspecPlanServiceImplTest {
	@Autowired
	private EmpInspecPlanService empInspecPlanService;
	
	
	@Test
	public void insertTest() throws Exception{
		EmpInspecPlan empInspecPlan = new EmpInspecPlan();
		//empInspecPlan.setEmpName("海淀南路线");
		empInspecPlan.setEmpId(1L);
		//empInspecPlan.setWorkTimeStart(workTimeStart);
		//empInspecPlan.setWorkTimeEnd(workTimeEnd);
		empInspecPlan.setWorkArea(220.34);
		empInspecPlan.setWorkAreaName("海淀南区");
		empInspecPlan.setWorkAreaPosition("[{12,123},{123,123}]");
		empInspecPlan.setBounds(0);
		empInspecPlan.setRemark("这就是备注");
		long id = empInspecPlanService.insert(empInspecPlan);
		
	}
	
	@Test
	public void updateTest() throws Exception{
		
		EmpInspecPlan empInspecPlan = new EmpInspecPlan();
		empInspecPlan.setEmpInspecPlanId(6197272205046119L);
		//empInspecPlan.setEmpName("海淀南路线");
		empInspecPlan.setEmpId(1L);
		//empInspecPlan.setWorkTimeStart(workTimeStart);
		//empInspecPlan.setWorkTimeEnd(workTimeEnd);
		empInspecPlan.setWorkArea(221.34);
		empInspecPlan.setWorkAreaName("海淀南路线_1");
		empInspecPlan.setWorkAreaPosition("[{12,123},{123,123}]");
		empInspecPlan.setBounds(0);
		empInspecPlan.setRemark("这就是备注1");
		empInspecPlanService.update(empInspecPlan);
		
	}
	
	@Test
	public void deleteTest() throws Exception{
		List<Long> ids = new ArrayList<Long>();
		ids.add(6886318019922523L);
		empInspecPlanService.delete(ids);
	}
	
	@Test
	public void findAllByOrgIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(3L);
		params.put("orgIds", orgIds);
		Pager<EmpInspecPlan> res = empInspecPlanService.findAll(params,null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByEmpIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> empIds = new ArrayList<Long>();
		empIds.add(1L);
		params.put("empIds", empIds);
		Pager<EmpInspecPlan> res = empInspecPlanService.findAll(params, null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByPagerTest() throws Exception{
		Pager pager = new Pager();
		Map<String, Object> params = new HashMap<String, Object>();
		Pager<EmpInspecPlan> res = empInspecPlanService.findAll(params, pager);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
}
