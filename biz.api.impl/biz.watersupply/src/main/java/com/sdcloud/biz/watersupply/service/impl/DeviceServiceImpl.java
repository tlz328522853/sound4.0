package com.sdcloud.biz.watersupply.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.watersupply.entity.Device;
import com.sdcloud.api.watersupply.service.DeviceService;
import com.sdcloud.biz.watersupply.dao.DeviceDao;

@Service
public class DeviceServiceImpl implements DeviceService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	DeviceDao deviceDao;
	@Override
	public Device insert(Device dev) {
		
		logger.info("start method: long insert(Device dic), arg dev: " + dev);
		if(dev == null){
			logger.warn("arg dev is null");
			throw new RuntimeException("arg dev is null");
		}
		try {
				
			deviceDao.insert(dev);
		} catch (Exception e) {
			logger.error("err method, Exception:{}" ,e);
			throw e;
		}
		
		logger.info("complete method, return device:{}",dev);
		return dev;
	}

}
