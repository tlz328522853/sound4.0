package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.sdcloud.api.core.entity.Role;
import com.sdcloud.api.core.service.RoleService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class RoleServiceImplTest {

	@Autowired
	private RoleService roleService;
	
	@Test
	public void insertAndDeleteTest() throws Exception{
		Role role = new Role();
		role.setRoleName("管理员");
		role.setRoleType(1);
		role.setDescription("this is a test");
		role.setTenantId(1L);
		long roleId = roleService.insert(role);
		Assert.assertTrue(roleId > 1);
		roleService.deleteById(roleId);
	}
	
	@Test
	public void deleteFailedTest(){
		try {
			roleService.deleteById(1L); //role 1 has 2 users
			Assert.assertEquals(1,2);
		} catch (Exception e) {
			Assert.assertEquals(e.getMessage(),"包含用户，无法删除");
		}
	}
	
	@Test
	public void findByUserTest() throws Exception{
		
		List<Role> roles = roleService.findByUser(1L);
		Assert.assertTrue(roles.size() == 2);
	}
	
	@Test
	public void updateTest() throws Exception{
		Role role = new Role();
		role.setRoleName("管理员");
		role.setRoleType(1);
		role.setDescription("this is a update test");
		role.setTenantId(1L);
		role.setEditor(2L);
		role.setRoleId(1L);
		roleService.update(role);
		
	}
	
	@Test
	public void updateRoleUser() throws Exception{
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(1L);
		userIds.add(2L);
		userIds.add(3L);
		userIds.add(4L);
		roleService.updateRoleUser(2L, userIds);
	}
	
	@Test
	public void findAll() throws Exception{
		List<Role> roles = roleService.findAll();
		System.out.println(roles);
		
	}
}
