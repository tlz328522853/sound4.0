package com.sdcloud.biz.core.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Tenant;
import com.sdcloud.api.core.service.TenantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class TenantServiceImplTest {
	
	@Autowired
	private TenantService tenantService;
	
	@Test
	public void insertTest() throws Exception{
		Tenant tenant = new Tenant();
		tenant.setAddress("北京海淀");
		tenant.setCity("北京");
		tenant.setCountry("中国");
		tenant.setName("桑德新环卫");
		tenant.setProvince("北京");
		tenant.setTelephone("010-81289028");
				
		long tenantId = tenantService.insert(tenant);
		Assert.assertTrue(tenantId > 0);
	}
	
	@Test
	public void updateTest() throws Exception{
		Tenant tenant = new Tenant();
		tenant.setAddress("北京海淀");
		tenant.setCity("北京");
		tenant.setCountry("中国x");
		tenant.setName("桑德新环卫");
		tenant.setProvince("北京");
		tenant.setTelephone("010-81289028");
		tenant.setTenantId(1620160427006343072L);	
		tenantService.update(tenant);
	}
}
