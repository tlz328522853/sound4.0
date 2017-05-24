package com.sdcloud.biz.envmapdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sdcloud.api.envmapdata.service.DeviceStatusService;
import com.sdcloud.biz.envmapdata.dao.RecordDao;
import com.sdcloud.biz.envmapdata.util.PropertiesUtils;
import com.sdcloud.framework.common.TimeCalculateUtil;

public class DeviceStatusServiceImpl implements DeviceStatusService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RecordDao recordDao;
	
	@Autowired
	private PropertiesUtils systemConfigProperties;

	@Override
	public List<Map<String, String>> findStatus(String deviceId, String deviceTypeCode, Date from, Date to)
			throws Exception {
		logger.info("start the method DeviceStatusServiceImpl.findStatus, deviceId:{}, deviceTypeCode:{}, from:{}, to:{}", 
				deviceId, deviceTypeCode, from, to);
		try{
			
			if(StringUtils.isEmpty(deviceId)){
				logger.warn("deviceId is null");
				throw new Exception("deviceId is null");
			}
			
			if(StringUtils.isEmpty(deviceTypeCode)){
				logger.warn("deviceId is null");
				throw new Exception("deviceId is null");
			}
			
			if(to.before(from)){
				logger.warn("Date from is after Date to");
				throw new IllegalArgumentException("Date from is after Date to");
			}
			
			String tableName = systemConfigProperties.getProperty(deviceTypeCode);//type2TableMap.get(type);
			if(StringUtils.isEmpty(tableName)){
				logger.warn("deviceTypeCode [{}] is not set datatable",deviceTypeCode);
				tableName = systemConfigProperties.getProperty("01001G01");
			}
			ResultScanner rs = recordDao.queryHbaseByRange(tableName, deviceId, deviceTypeCode, from.getTime(), to.getTime());
			Map item;
			List<Map<String, String>> res = new ArrayList<Map<String, String>>();
			for (Result r : rs) {
				List<Cell> cells = r.listCells();
				item = new HashMap<String, String>();
				for(Cell c : cells){
					item.put(Bytes.toString(c.getQualifier()),
							Bytes.toString(c.getValue()));						
				}
				res.add(item);
			}
			logger.debug("result: {}", res);
			logger.info("complete the method DeviceStatusServiceImpl.findStatus");
			return res;
			
		}catch(Exception e){
			logger.error("err DeviceStatusServiceImpl.findStatus,{}",e);
			throw e;
		}
	}

	@Override
	public List<Map<String, String>> findStatusOneDay(String deviceId, String deviceTypeCode, Date wholeDay)
			throws Exception {
		
		Long from = TimeCalculateUtil.getDayStartTime(wholeDay.getTime());
		Long to = TimeCalculateUtil.getDayEndTime(wholeDay.getTime());
		
		logger.info("complete DeviceStatusServiceImpl.findStatusOneDay");
		return findStatus(deviceId, deviceTypeCode, new Date(from), new Date(to));
		
	}

	@Override
	public List<Map<String, String>> findRealTimeStatus(List<String> deviceIds, String deviceTypeCode)
			throws Exception {
		
		logger.info("start query redis, deviceTypeCode:{},deviceIds：{}", deviceTypeCode, deviceIds);
		
		if(deviceIds == null){
			logger.warn("deviceIds is null");
			throw new Exception("deviceIds is null");
		}
		
		if(deviceTypeCode == null || deviceTypeCode.length() == 0){
			logger.warn("deviceTypeCode is empty");
			throw new Exception("deviceTypeCode is empty");
		}
		try{
			List<Map<String, String>> entries = recordDao.queryStatusRedis(deviceIds, deviceTypeCode);
			logger.debug("result: {}", entries);
			logger.info("complete query redis");
			return entries;
		}
		catch(Exception e){
			logger.error("err findRealTimeStatus:{}",e);
			throw e;
		}
		
	}

}
