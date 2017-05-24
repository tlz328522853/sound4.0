package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.DeviceAdvertisement;
import com.sdcloud.api.envsanitation.service.DeviceAdvertisementService;

/**
 * 
 * @author shilin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:conf/test-bean-context.xml")
public class DeviceAdvertisementServiceTest {
	
	Logger logger = LoggerFactory.getLogger(super.getClass());
	@Autowired
	private DeviceAdvertisementService deviceAdvertisementService;
	
	@Test
	public void insert(){
		System.out.println("start insert method ...");
		try {
			List<DeviceAdvertisement> deviceAdvertisements = new ArrayList<DeviceAdvertisement>(5);
			for (int i = 1; i <= 5; i++) {
				DeviceAdvertisement e = new DeviceAdvertisement(); 
				//e.setDeviceId(i);
				//e.setAdvertisementId(i);
				e.setDeviceMac("DeviceMac"+i);
				deviceAdvertisements.add(e);
			}
			this.deviceAdvertisementService.insert(deviceAdvertisements);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
	@Test
	public void delete(){
		System.out.println("start delete method ...");
		try {
			
			List<Long> deviceAdvertisementIds = new  ArrayList<Long>();
			
			deviceAdvertisementIds.add(5666982460737860L);
			deviceAdvertisementIds.add(1179162604816528L);
			this.deviceAdvertisementService.delete(deviceAdvertisementIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("complete method ...");
	}
	
	
}
