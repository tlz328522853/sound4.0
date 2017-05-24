package com.sdcloud.biz.hbase.service.impl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envmapdata.service.RecordService;
import com.sdcloud.biz.envmapdata.service.impl.RecordServiceImpl;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bean-context.xml")
public class CarRecordServiceImplTest {
	
	@Autowired
	private RecordService recordService;
	
	@Test
	public void getCarRecordsTest() throws Exception{
		String carMobileNumber = "112798405100";
		carMobileNumber = StringUtils.reverse(carMobileNumber);
		Long from = 1458721670000L;
		Long to = 1458721690000L;
		String type = "01001G01";
		List<Map<String, String>> carRecords = recordService.findHistoryRecords(carMobileNumber, type, from, to);
		Assert.assertTrue(carRecords.size() > 100);
	}
	
	@Test
	public void getCarRecordsOneDayTest() throws Exception{
		String carMobileNumber = "112798405100";
		carMobileNumber = StringUtils.reverse(carMobileNumber);
		Long wholeDay = 1458722940000L;
		String type = "01001G01";
		List<Map<String, String>> carRecords = recordService.findHistoryRecordsOneDay(carMobileNumber, type, wholeDay);
		Assert.assertTrue(carRecords.size() > 100);
	}
	
	@Test
	public void getRealtimeCarRecords() throws Exception{
		List<String> carMobileNumbers = new ArrayList<String>(0);
		carMobileNumbers.add("001504897211");
		String type = "DEV01001G01";
		List<Map<String, String>> carRecords = recordService.findRealTimeRecords(carMobileNumbers, type);
		Assert.assertTrue(carRecords.size() > 0);
	}
}
