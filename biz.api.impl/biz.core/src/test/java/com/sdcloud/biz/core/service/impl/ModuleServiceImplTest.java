package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Module;
import com.sdcloud.api.core.service.ModuleService;
import com.sdcloud.framework.entity.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:conf/test-bean-context.xml")
public class ModuleServiceImplTest {

	@Autowired
	private ModuleService moduleService;
	
	@Test
	public void insert() {
		Module module = new Module();
		module.setModuleCode("MODULE2016");
		module.setModuleName("桑德");
//		module.setUrl("aa");
		module.setStatus(1);
//		module.setSequence(1);
//		module.setCreator(1L);
//		module.setEditor(1L);
//		module.setIcon("aa");
		
		try {
			module = moduleService.insert(module);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(module);
	}
	
	//@Test
	public void delete(){
		List<Long> moduleIds = new ArrayList<Long>();
		moduleIds.add(37L);
		moduleIds.add(38L);
		
		try {
			moduleService.delete(moduleIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("==================");
	}
	
	//@Test
	public void update(){
		Module module = new Module();
		module.setModuleId(39L);
		module.setModuleName("模型");
		module.setStatus(0);
		
		try {
			moduleService.update(module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findAll() throws Exception{
	
		Pager<Module> pager = new Pager<Module>();
		pager.setPageSize(10);
		pager.setPageNo(2);
		
		moduleService.findAll(pager, null);
	}
	
	@Test
	public void findAll2() throws Exception{
		
		Pager<Module> pager = new Pager<Module>();
		pager.setPageSize(10);
		pager.setPageNo(2);
		
		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("topMenu", "topMenu");
		moduleService.findAll(null, map);*/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("topMenu", "topMenu");
		List<Module> modules = moduleService.findAll(null, map).getResult();
	}
	
}
