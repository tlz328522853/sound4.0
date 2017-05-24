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

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.framework.entity.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class PropertyTemplateServiceImplTest {/*

	@Autowired
	private DicService dicService;
	
	@Test
	public void insert() {
		Dic dic = new Dic();
		dic.setName("性别代码");
		dic.setCode("DIC2016");
		dic.setLevel(1);
		dic.setType(0);
		
		try {
			dic = dicService.insert(dic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(dic);
	}
	
	@Test
	public void delete(){
		List<Long> dicIds = new ArrayList<Long>();
		dicIds.add(3461539966109016L);
		dicIds.add(8556905318956168L);
		
		try {
			dicService.delete(dicIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("==================");
	}
	
	@Test
	public void update(){
		Dic dic = new Dic();
		dic.setDicId(7493143609323538L);
		dic.setType(22);
		
		try {
			dic = dicService.update(dic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findAll() throws Exception{
		
		Pager<Dic> pager = new Pager<Dic>();
		pager.setPageSize(2);
		pager.setPageNo(3);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "代码");
		List<Dic> dics = dicService.findAll(pager, map).getResult();
	}
	
	@Test
	public void findByPid() throws Exception{
		dicService.findByPid(3610835821088761L);
	}
	
*/}
