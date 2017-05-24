package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Function;
import com.sdcloud.api.core.service.FunctionService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class FunctionServiceImplTest {
	
	@Autowired
	private FunctionService functionService;
	
	//@Test
	public void insert(){
		Function f = new Function();
		f.setFuncCode("R2D2007");
		f.setFuncName("功能XXXX");
		
		f.setModuleId(37L);
		f.setModuleCode("zcq2");
		
		try {
			functionService.insert(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delete() throws Exception{
		List<Long> funcIds = new ArrayList<Long>();
		funcIds.add(11L);
		funcIds.add(12L);
		
		functionService.delete(funcIds);
	}
	
	//@Test
	public void update() throws Exception{
		Function f = new Function();
		f.setFuncId(10L);
		f.setFuncName("功能XXXX");
	
		f.setModuleId(37L);
		f.setModuleCode("zcq2");
		
		functionService.update(f);
	}
	
	@Test
	public void findByModuleId() throws Exception{
		List<Long> list = new ArrayList<Long>();
		/*list.add(30L);
		list.add(31L);
		list.add(32L);
		list.add(33L);
		list.add(34L);
		list.add(35L);
		list.add(36L);
		list.add(39L);
		list.add(40L);
		list.add(42L);
		list.add(43L);*/
		
		list.add(40L);
//		Map<Long, List<Function>> map = functionService.findByModuleId(list, -1);
//		System.out.println(map);
		/*System.out.println("==================");
		for (Function function : functions) {
			System.out.println(function);
		}
		System.out.println("==================");*/
	}
	
	//@Test
	public void findFunctionsByOwnerId() throws Exception{
		
		List<Function> functions = functionService.findByOwnerId(81L);
		
		System.out.println("==================");
		for (Function function : functions) {
			System.out.println(function);
		}
		System.out.println("==================");
	}
	
	@Test
	public void findAll() throws Exception{
		
		List<Function> functions = functionService.findAll();
		
		System.out.println("==================");
		for (Function function : functions) {
			System.out.println(function);
		}
		System.out.println("==================");
	}
}
