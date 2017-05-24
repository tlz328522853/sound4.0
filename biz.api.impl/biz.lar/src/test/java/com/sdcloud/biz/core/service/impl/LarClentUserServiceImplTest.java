package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.lar.entity.IntegralSetting;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.biz.lar.service.impl.IntegralSettingServiceImpl;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class LarClentUserServiceImplTest {
	
	@Autowired
	private LarClientUserService larclientUserService;
	
	@Autowired
	private IntegralSettingServiceImpl integralSettingServiceImpl;
	
	@Autowired
	private LarClientUserAddressService larClientUserAddressService;
	
/*	ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");*/
	
	//分页查询
	//@Test
/*	public void test1(){
		try {
			LarPager<IntegralSetting> larPager = new LarPager<IntegralSetting>();
			//每页显示的条数
			larPager.setPageSize(2);
			//当前页
			larPager.setPageNo(2);
			//是否去重复，默认false(否)
			larPager.setDistinct(false);
			Map<String, Object> params = larPager.getParams();
			params.put("id", 1);
			//LarPager<IntegralSetting> pager = integralSettingServiceImpl.selectByExample(larPager);
			List<IntegralSetting> result = pager.getResult();
			for (IntegralSetting larClientUser2 : result) {
				System.out.println(larClientUser2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	//更新
	@Test
	public void test2(){
		try {
			//LarClientUser larClientUser = new LarClientUser(null, "8888888888888888", "18511661031", "888888", "刘文钊1", 1, 21, 8, "www.baidu.com", 66666, 66666, "6666666666666666", "www.baidu.com", new Date(), 0);
			LarClientUser larClientUser = new LarClientUser();
			larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
			larClientUser.setName("刘文钊");
			boolean updateByExampleSelective = larclientUserService.updateByExampleSelective(larClientUser);
			System.out.println(updateByExampleSelective);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除
	@Test
	public void test3(){
		try {
			//LarClientUser larClientUser = new LarClientUser(null, "8888888888888888", "18511661031", "888888", "刘文钊1", 1, 21, 8, "www.baidu.com", 66666, 66666, "6666666666666666", "www.baidu.com", new Date(), 0);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("enable", 1);
			params.put("id", 1);
			larclientUserService.deleteByExample(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test4(){
		try {
			LarRegion pro = new LarRegion();
			pro.setRegionId(1);
			LarRegion city = new LarRegion();
			city.setRegionId(2);
			LarRegion area = new LarRegion();
			area.setRegionId(3);
			LarClientUser larClientUser = new LarClientUser();
			larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
//			LarClientUserAddress larClientUserAddress = new LarClientUserAddress(String.valueOf(UUIDUtil.getUUNum()), "董鑫3", "13103410799", "山西省太原市小店区太行小区", "山西省太原市小店区", "太行小区", pro, city, area, 0, 0, larClientUser);
//			boolean insert = larClientUserAddressService.insertAddressGetId(larClientUserAddress);
//			System.out.println(larClientUserAddress.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test5(){
		try {
			LarPager<LarClientUserAddress> larPager = new LarPager<LarClientUserAddress>();
			larPager.setPageNo(1);
			larPager.setPageSize(2);
			LarPager<LarClientUserAddress> pager = larClientUserAddressService.selectByExample(larPager);
			List<LarClientUserAddress> result = pager.getResult();
			for (LarClientUserAddress larClientUserAddress2 : result) {
				System.out.println(larClientUserAddress2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test6(){
		try {
			LarRegion pro = new LarRegion();
			pro.setRegionId(3);
			LarRegion city = new LarRegion();
			city.setRegionId(2);
			LarRegion area = new LarRegion();
			area.setRegionId(1);
			LarClientUser larClientUser = new LarClientUser();
			larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
			//LarClientUserAddress larClientUserAddress = new LarClientUserAddress("7049334952867205", "董鑫3", "13103410799", "山西省太原市小店区太行小区", "山西省太原市小店区", "太行小区", pro, city, area, 0, 0, larClientUser);
			//boolean updateByExampleSelective = larClientUserAddressService.updateByExampleSelective(larClientUserAddress);
//			LarClientUserAddress larClientUserAddress = new LarClientUserAddress("5648451668953576", "董鑫3", "13103410799", "山西省太原市小店区太行小区", "山西省太原市小店区", "太行小区", pro, city, area, 0, 0, larClientUser);
//			boolean updateByExampleSelective = larClientUserAddressService.updateById(larClientUserAddress);
//			System.out.println(updateByExampleSelective);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test7(){
		try {
			LarClientUserAddress larClientUserAddress = larClientUserAddressService.selectByPrimaryKey("3623741028742169");
			System.out.println(larClientUserAddress);
			System.out.println(larClientUserAddress.getProvinceId().getRegionName());
			System.out.println(larClientUserAddress.getCityId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test8(){
		try {
			LarPager<LarClientUserAddress> larPager = new LarPager<LarClientUserAddress>();
			larPager.setPageNo(1);
			larPager.setPageSize(2);
			LarPager<LarClientUserAddress> pager = larClientUserAddressService.selectByUserId(larPager,"4108453614236823");
			List<LarClientUserAddress> result = pager.getResult();
			for (LarClientUserAddress larClientUserAddress : result) {
				System.out.println(larClientUserAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
