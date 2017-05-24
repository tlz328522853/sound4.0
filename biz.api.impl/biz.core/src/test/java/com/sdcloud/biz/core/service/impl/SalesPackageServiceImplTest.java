package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.SalesPackage;
import com.sdcloud.api.core.entity.SalesPackageModule;
import com.sdcloud.api.core.service.SalesPackageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class SalesPackageServiceImplTest {

	@Autowired
	private SalesPackageService salesPackageService;
	
	@Test
	public void insert() throws Exception {
		SalesPackage sp = new SalesPackage();
		sp.setSalesPackageName("普通包");
		sp.setDescription("普通用户");
		sp.setCreator(105L);
		sp.setEditor(205L);
		long spId = salesPackageService.insert(sp);
//		Assert.assertEquals(1, spId);
		System.out.println("==============" + spId);
	}
	
	//@Test
	public void setSalesPackageModule() throws Exception {
		List<SalesPackageModule> salesPackageModules = new ArrayList<SalesPackageModule>();
		SalesPackageModule spm = new SalesPackageModule();
		spm.setSalesPackageId(7L);
		spm.setModuleId(31L);
		spm.setModuleCode("vjpe");
		

		SalesPackageModule spm2 = new SalesPackageModule();
		spm2.setSalesPackageId(7L);
		spm2.setModuleId(32L);
		spm2.setModuleCode("48h5");
		
		salesPackageModules.add(spm);
		salesPackageModules.add(spm2);
		
		salesPackageService.setSalesPackageModule(salesPackageModules, spm.getSalesPackageId());
		
	}
	
	@Test
	public void update() throws Exception {
		SalesPackage sp = new SalesPackage();
		
		sp.setSalesPackageId(3L);
//		sp.setSalesPackageName("尊贵独享包");
		sp.setDescription("尊贵用户");
//		sp.setCreator(1003L);
//		sp.setEditor(2003L);
		salesPackageService.update(sp);
	}
	
	//@Test
	public void delete() throws Exception {
		Long salesPackageId = 1L;
		
		salesPackageService.delete(salesPackageId);
	}
	
	//@Test
	public void findById() throws Exception {
		
		SalesPackage p = salesPackageService.findById(7L);
		
		System.out.println(p);
	}
}
