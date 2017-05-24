package com.sdcloud.biz.watersupply.service.impl;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.watersupply.entity.Device;
import com.sdcloud.api.watersupply.service.DeviceService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath*:test-bean-context.xml")

public class DeviceServiceTest {

	@Autowired
	DeviceService deviceService;
	@Test
	public void add() {
		Device device = new Device();
		device.setCode("code");
		device.setCreateTime(new Date());
		device.setCreator("testuser");;
		device.setDeviceNo("111");
		device.setManufacturerId(12356l);
		device.setOrgId("123456");
		device.setProductionDate(new Date());
		device.setWorkingLife(1);
		
		device = deviceService.insert(device);
		System.out.println("-------------结果为:"+device.toString());
	}

}
