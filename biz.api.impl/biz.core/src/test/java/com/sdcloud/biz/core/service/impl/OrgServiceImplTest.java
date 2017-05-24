package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class OrgServiceImplTest {
	
	@Autowired
	private OrgService orgService;
	
	
	private long notExistOrgId = -1;
	private long rootOrgId = 1;
	private long hasChildOrgId = 2;
	private long hasUserOrgId = 3L;
	private long hasEmployeeOrgId = 4L;
	private long insertOrgId = -1;
	
	
	@Test
	public void insertAndDeleteTest() throws IllegalArgumentException, Exception{
		Org org = new Org();
		org.setName("新环卫");
		org.setSequence(1);
		org.setDescription("新环卫集团公司");
		org.setParentId(1L);
		org.setIsSubCompany(1);		
		org.setSubCompanyCode("C001");
		org.setTenantId(1L);
		org.setOrgId(null);
//		insertOrgId = orgService.insert(org);
//		Assert.assertTrue(insertOrgId > 1);		
//		orgService.deleteById(insertOrgId, true);
	}
	
	@Test
	public void updateTest() throws IllegalAccessException, Exception{
		Org org = new Org();
		org.setOrgId(1L);
		org.setName("新环卫");		
		orgService.update(org);		
	}
	
	@Test
	public void deleteByIdRootErrTest(){
		
		try {
			orgService.deleteById(rootOrgId, true);
			Assert.assertEquals(1, 0);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().endsWith("根节点，无法删除"));
		}
	}
	
	@Test
	public void deleteByIdHasChildErrTest(){
		
		try {
			orgService.deleteById(hasChildOrgId, true);
			Assert.assertEquals(1, 0);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().endsWith("包含子部门，无法删除"));
		}
	}
	
	@Test
	public void deleteByIdHasUserErrTest(){
		
		try {
			orgService.deleteById(hasUserOrgId, true);
			Assert.assertEquals(1, 0);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().endsWith("包含用户，无法删除"));
		}
	}
	
	@Test
	public void deleteByIdNotExistErrTest(){
		
		try {
			orgService.deleteById(notExistOrgId, true);
			Assert.assertEquals(1, 0);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().endsWith("部门不存在"));
		}
	}
	
	@Test
	public void deleteByIdHasEmployeeErrTest(){
		
		try {
			orgService.deleteById(hasEmployeeOrgId, true);
			Assert.assertEquals(1, 0);
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().endsWith("部门不存在"));
		}
	}
	
	@Test
	public void findByIdNotIncludeChildTest() throws Exception{
		List<Org> orgs = orgService.findById(rootOrgId,false);
		Assert.assertEquals(orgs.size(), 1);
	}
	
	@Test
	public void findByIdIncludeChildTest() throws Exception{
		List<Org> orgs = orgService.findById(rootOrgId,true);
		Assert.assertTrue(orgs.size() > 1);
	}
	
	@Test
	public void findByOwnerCodeTest(){
	
	}
	
	@Test
	public void findOrgMapByIds(){
		Map<Long,Org> orgMap = new HashMap<>();
		List<Long> orgIds = new ArrayList<>();
		orgIds.add(815333161191680l);
		try {
			orgMap = orgService.findOrgMapByIds(orgIds, true);
			for(Long orgId:orgMap.keySet()){
				System.out.println(orgMap.get(orgId));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
