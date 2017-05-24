package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.FunctionRight;
import com.sdcloud.api.core.service.FunctionRightService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class FunctionRightServiceImplTest {
	
	@Autowired
	private FunctionRightService functionRightService;
	
	//@Test
	public void insert() throws Exception{
		List<FunctionRight> functionRights = new ArrayList<FunctionRight>();
		
		FunctionRight fr = new FunctionRight();
		fr.setOwnerId(82L);
		fr.setOwnerType(2);
		fr.setOwnerCode(123477L);
		
		fr.setFuncId(9L);
		fr.setFuncCode("mq7v");
		
		FunctionRight fr2 = new FunctionRight();
		fr2.setOwnerId(82L);
		fr2.setOwnerType(2);
		fr2.setOwnerCode(123477L);
		
		fr2.setFuncId(10L);
		fr2.setFuncCode("j0ls");
		
		functionRights.add(fr);
		functionRights.add(fr2);
		
		functionRightService.insert(functionRights);
		
	}
	
	@Test
	public void deleteByOwnerId() throws Exception{
		functionRightService.deleteByOwnerId(82L);
	}
	
	@Test
	public void update() throws Exception{
		
		// List<FunctionRight> functionRights, Long ownerId
		List<FunctionRight> functionRights = new ArrayList<FunctionRight>();
		
		FunctionRight fr = new FunctionRight();
		fr.setOwnerId(82L);
		fr.setOwnerType(2);
		fr.setOwnerCode(123480L);
		
		fr.setFuncId(9L);
		fr.setFuncCode("mq7v");
		fr.setRightValue(8080);
		
		FunctionRight fr2 = new FunctionRight();
		fr2.setOwnerId(82L);
		fr2.setOwnerType(2);
		fr2.setOwnerCode(123480L);
		
		fr2.setFuncId(10L);
		fr2.setFuncCode("j0ls");
		fr2.setRightValue(8080);
		
		functionRights.add(fr);
		functionRights.add(fr2);
		

		functionRightService.update(functionRights, 82L,null);

		
	}
}
