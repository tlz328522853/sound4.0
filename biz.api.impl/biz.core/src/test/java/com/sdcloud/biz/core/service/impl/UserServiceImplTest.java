package com.sdcloud.biz.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class UserServiceImplTest {
	
	@Autowired
	private UserService userService;

	@Test
	public void insertAndDeleteTest() throws Exception{
		/*User user = new User();
		user.setName("zhangsan");
		user.setUserType(1);
		user.setTelephone("13427429830");
		user.setEmail("test@acv.com");
		user.setOrgId(3L);
		user.setPortalTypeId(1L);
		user.setCreator(1L);
		user.setTenantId(1L);
		Long userId = userService.insert(user);
		Assert.assertTrue(userId > 1);*/
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(3419099044399521L);
		userService.deleteById(userIds);
		
	}
	
	@Test
	public void updateTest() throws Exception{
		User user = new User();
		user.setUserId(4504762888572711L);
		user.setName("yaoming5577");

//		user.setUserType(1);

		user.setTelephone("13427429830");
		user.setEmail("test@sina.com");
//		user.setOrgId(3L);
		user.setPortalTypeId(1L);
		user.setCreator(1L);
		user.setTenantId(1L);
		//userService.update(user);
//		Assert.assertEquals(1, 1);
	}
	
	@Test
	public void findByOrgTest(){
		Long orgId = 3L;
		boolean includeSub = false;//不包含子机构
		Pager<User> pager = new Pager<User>();
		pager.setPageSize(2);
//		Pager<User> users = userService.findByOrg(orgId, includeSub, pager);
//		System.out.println(users);
//		Assert.assertTrue(users.getResult().size() > 0);
	}
	
	@Test
	public void findByOrgIncludeSubTest(){
		Long orgId = 1L;
		boolean includeSub = true;//不包含子机构
		Pager<User> pager = new Pager<User>();
		pager.setPageNo(1);
		pager.setPageSize(2);
		pager.setOrderBy("name, userId");
		pager.setOrder("desc");
//		Pager<User> users = userService.findByOrg(orgId, includeSub, pager);
//		Assert.assertTrue(users.getResult().size() > 0);
//		Assert.assertTrue(2L == users.getResult().get(0).getUserId());
	}
	
	@Test
	public void findByPwd() throws Exception{
		String userName = "admin";
		String pwd = "admin";
		User res = userService.verifyUser(userName, pwd);
		Assert.assertTrue(res != null);
	}
	
	@Test
	public void findByPwdNotExist() throws Exception{
		String userName = "admin";
		String pwd = "admin_error";
		User res = userService.verifyUser(userName, pwd);
		Assert.assertFalse(res!=null);
	}
	
	@Test
	public void findByRole() throws Exception{
		Long roleId = 153L;
		Pager<User> pager = new Pager<User>();
		pager.setPageSize(2);
		pager.setPageNo(2);
		
		String orderSql = pager.getOrderSql();
		int first = pager.getFirst();
		int pageSize = pager.getPageSize();
		
		userService.findByRole(roleId, pager);
	}
	
	@Test
	public void insertAnd2UserRole() throws Exception{
		
		User user = new User();
		user.setName("luomasi");
		user.setTelephone("18890091009");
		//userService.insertAnd2UserRole(155L, user,null,);
	}
	
	@Test
	public void findUserMapByIds() throws Exception{
		
		List<Long> userIds = new ArrayList<>();
		userIds.add(5788443375650035l);
		userIds.add(5043670974484029l);
		userIds.add(2945190656690793l);
		Map<Long,User> result = userService.findUserMapByIds(userIds);
		for(Long key:result.keySet()){
			System.out.println(result.get(key).toString());
		}
	}
}
