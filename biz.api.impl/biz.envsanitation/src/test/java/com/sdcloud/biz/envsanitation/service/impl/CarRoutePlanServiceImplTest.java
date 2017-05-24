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

import com.sdcloud.api.envsanitation.entity.CarRoutePlan;
import com.sdcloud.api.envsanitation.service.CarRoutePlanService;
import com.sdcloud.api.envsanitation.service.CarRoutePlanService;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class CarRoutePlanServiceImplTest {
	
	@Autowired
	private CarRoutePlanService carRoutePlanService;
	
	
	@Test
	public void insertTest() throws Exception{
		CarRoutePlan carRoutePlan = new CarRoutePlan();
		//carRoutePlan.setEmpName("海淀南路线");
		carRoutePlan.setEmpId(1L);
		carRoutePlan.setAssetId(4886893085477909L);
		//carRoutePlan.setWorkTimeStart(workTimeStart);
		//carRoutePlan.setWorkTimeEnd(workTimeEnd);
		carRoutePlan.setWorkPlanLength(220.34);
		carRoutePlan.setWorkRouteName("海淀南路线");
		carRoutePlan.setWorkRoutePosition("[{12,123},{123,123}]");
		carRoutePlan.setBounds(0);
		carRoutePlan.setRemark("这就是备注");
		long id = carRoutePlanService.insert(carRoutePlan);
		
	}
	
	@Test
	public void updateTest() throws Exception{
		
		CarRoutePlan carRoutePlan = new CarRoutePlan();
		carRoutePlan.setCarRoutePlanId(6197272205046119L);
		//carRoutePlan.setEmpName("海淀南路线");
		carRoutePlan.setEmpId(1L);
		//carRoutePlan.setWorkTimeStart(workTimeStart);
		//carRoutePlan.setWorkTimeEnd(workTimeEnd);
		carRoutePlan.setWorkPlanLength(221.34);
		carRoutePlan.setWorkRouteName("海淀南路线_1");
		carRoutePlan.setWorkRoutePosition("[{12,123},{123,123}]");
		carRoutePlan.setBounds(0);
		carRoutePlan.setRemark("这就是备注1");
		carRoutePlanService.update(carRoutePlan);
		
	}
	
	@Test
	public void deleteTest() throws Exception{
		List<Long> ids = new ArrayList<Long>();
		ids.add(6886318019922523L);
		carRoutePlanService.delete(ids);
	}
	
	@Test
	public void findAllByOrgIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(2L);
		params.put("orgIds", orgIds);
		Pager<CarRoutePlan> res = carRoutePlanService.findAll(params,null);
		System.out.println(res);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByAssetIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(4886893085477909L);
		params.put("assetIds", orgIds);
		Pager<CarRoutePlan> res = carRoutePlanService.findAll(params, null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByEmpIdsTest() throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		List<Long> empIds = new ArrayList<Long>();
		empIds.add(1L);
		params.put("empIds", empIds);
		Pager<CarRoutePlan> res = carRoutePlanService.findAll(params, null);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
	
	@Test
	public void findAllByPagerTest() throws Exception{
		Pager pager = new Pager();
		Map<String, Object> params = new HashMap<String, Object>();		
		Pager<CarRoutePlan> res = carRoutePlanService.findAll(params, pager);
		org.junit.Assert.assertTrue(res.getResult().size() > 0);
	}
}
