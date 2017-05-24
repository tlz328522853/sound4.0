package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.FormTemplate;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.FormTemplateService;
import com.sdcloud.framework.entity.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class FormTemplateServiceImplTest {

	@Autowired
	private FormTemplateService formTplService;
	
	@Test
	public void insert() {
		
		FormTemplate tpl = new FormTemplate();
		tpl.setTplName("表单模板1");
		
		try {
			tpl = formTplService.insert(tpl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(tpl);
	}
	
	@Test
	public void delete(){
		
		try {
			formTplService.delete(Arrays.asList(4400508732393041L));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("==================");
	}
	
	@Test
	public void update(){
		FormTemplate tpl = new FormTemplate();
		tpl.setFormTplId(4400508732393038L);
		tpl.setTplName("表单模板6");
		try {
			tpl = formTplService.update(tpl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findAll() throws Exception{
		
		Pager<FormTemplate> pager = new Pager<FormTemplate>();
		pager.setPageSize(2);
		pager.setPageNo(1);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tplName", "模板");
		List<FormTemplate> dics = formTplService.findAll(pager, map).getResult();
	}
	
	@Test
	public void findByPid() throws Exception{
		formTplService.findByPid(3610835821088761L);
	}
	
}
