package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author czz
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class EmployeeServiceImplTest {
	
	@Autowired
	private EmployeeService employeeService;
	
	
	@Test
	public void insertAndDeleteTest() throws Exception{
		Employee employee = new Employee();
		employee.setName("yaoming");
		employee.setTelephone("189100122348");
		
		Org org = new Org();
		org.setOrgId(3L);
		employee.setOrg(org);
		
		employee.setDescription("this is test desc");
		employee.setEmail("employee@test.com");
//		Long employeeId = employeeService.insert(employee);
//		Assert.assertTrue(employeeId > 1);
//		List<Long> employeeIds = new ArrayList<Long>();
//		employeeIds.add(employeeId);		
//		employeeService.delete(employeeIds);
	}
	
	@Test
	public void deleteBindUserErrTest() throws Exception{
		List<Long> employeeIds = new ArrayList<Long>();
		employeeIds.add(5L); // bind with user 1
		employeeIds.add(7L);
		
		try {
			employeeService.delete(employeeIds);
			Assert.assertTrue(false);//should get err
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().equals("存在员工被绑定到用户上"));
		}
	}
	
	@Test
	public void updateInfoTest() throws Exception{
		Employee employee = new Employee();
		employee.setEmployeeId(5L);
		employee.setName("zhangsan");
		employee.setTelephone("13712233");
		Org org = new Org();
		org.setOrgId(3L);
		employee.setOrg(org);
		employee.setDescription("this is test desc");
		employee.setEmail("employee@test.com");
		employee.setTenantId(1L);
		employeeService.updateInfo(employee);
	}
	
	
	@Test
	public void updateOrg() throws Exception{
		List<Long> employeeIds = new ArrayList<Long>();
		employeeIds.add(5L); // change to org 2 from org 3
		employeeService.updateOrg(employeeIds, 2);
	}
	
	@Test
	public void updateNotExistOrg(){
		List<Long> employeeIds = new ArrayList<Long>();
		employeeIds.add(5L); // change to org 2 from org 3
		try {
			employeeService.updateOrg(employeeIds, -1);
			Assert.assertTrue(false); //-1 not exist
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().equals("新组织不存在"));
		}
	}
	
	@Test
	public void findByOrgNotIncludeSubTest() throws Exception{
		Long orgId = 3L;
		boolean includeSub = false;//不包含子机构
		Pager<Employee> pager = new Pager<Employee>();
		pager.setPageSize(2);
//		Pager<Employee> employees = employeeService.findByOrg(orgId, includeSub, pager);
//		Assert.assertTrue(employees.getResult().size() > 0);
	}
	
	@Test
	public void findByOrgIncludeSubTest() throws Exception{
		Long orgId = 1L;
		boolean includeSub = true; //包含子机构
		Pager<Employee> pager = new Pager<Employee>();
		pager.setPageSize(3);
//		Pager<Employee> employees = employeeService.findByOrg(orgId, includeSub, pager);
//		Assert.assertTrue(employees.getResult().size() == 3);
	}
	
	@Test
	public void findByOrgIncludeSubPage2Test() throws Exception{
		Long orgId = 1L;
		boolean includeSub = true; //包含子机构
		Pager<Employee> pager = new Pager<Employee>();
		pager.setPageNo(2);
		pager.setPageSize(4);
//		Pager<Employee> employees = employeeService.findByOrg(orgId, includeSub, pager);
//		Assert.assertTrue(employees.getResult().size() > 0);
	}
	
	@Test
	public void findByOrgSortByNameTest() throws Exception{
		Long orgId = 1L;
		boolean includeSub = true; 
		Pager<Employee> pager = new Pager<Employee>();
		pager.setPageNo(1);
		pager.setPageSize(4);
		pager.setOrderBy("name");
		pager.setOrder("asc");
//		Pager<Employee> employees = employeeService.findByOrg(orgId, includeSub, pager);
//		Assert.assertTrue(employees.getResult().size() == 4);
//		Assert.assertEquals(employees.getResult().get(0).getName(), "azhangsan");
	}
	
}
