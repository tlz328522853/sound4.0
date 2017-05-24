package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.Advertisement;
import com.sdcloud.api.envsanitation.service.AdvertisementService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author shilin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:conf/test-bean-context.xml")
public class AdvertisementServiceTest {
	
	Logger logger = LoggerFactory.getLogger(super.getClass());
	@Autowired
	private AdvertisementService advertisementService;
	
	@Test
	public void insert(){
		System.out.println("start insert method ...");
		try {
			Advertisement advert = new Advertisement();
			//advert.setAdvertisementId(UUIDUtil.getUUNum());
			advert.setAdvertisementName("新活动来了1");
			this.advertisementService.insert(advert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
	@Test
	public void delete(){
		System.out.println("start delete method ...");
		try {
			
			List<Long> advertIds = new  ArrayList<Long>();
			advertIds.add(4L);//advertIds.add(2L);advertIds.add(3L);
			this.advertisementService.delete(advertIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
	@Test
	public void update(){
		System.out.println("start update method ...");
		try {
			
			Advertisement advert = new Advertisement();
			//advert.setAdvertisementId(5);
			advert.setAdvertisementName("新活动来了");
			this.advertisementService.update(advert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
	@Test
	public void findBy(){
		System.out.println("start findBy method ...");
		try {
			Pager<Advertisement> pager = new Pager<Advertisement>();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("advertisementName", "新活动来了2");
			pager = this.advertisementService.findBy(pager, param);
			System.out.println(pager.getResult().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
}
