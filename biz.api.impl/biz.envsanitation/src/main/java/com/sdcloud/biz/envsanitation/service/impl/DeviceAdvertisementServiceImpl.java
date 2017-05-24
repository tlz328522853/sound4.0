package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.Advertisement;
import com.sdcloud.api.envsanitation.entity.DeviceAdvertisement;
import com.sdcloud.api.envsanitation.service.DeviceAdvertisementService;
import com.sdcloud.biz.envsanitation.dao.DeviceAdvertisementDao;
import com.sdcloud.framework.common.UUIDUtil;
@Service("deviceAdvertisementService")
public class DeviceAdvertisementServiceImpl implements DeviceAdvertisementService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DeviceAdvertisementDao deviceAdvertisementDao;
	
	
	@Transactional
	public List<DeviceAdvertisement> insert(List<DeviceAdvertisement> deviceAdvertisements) throws Exception {
		logger.info("start method: void insert(List<DeviceAdvertisement> deviceAdvertisements), arg deviceAdvertisements: " + deviceAdvertisements);
		
		if(deviceAdvertisements == null || deviceAdvertisements.size() == 0 ){
			logger.warn("arg deviceAdvertisements is null or size == 0");
			throw new IllegalArgumentException("arg deviceAdvertisements is null or size == 0");
		}
		for (DeviceAdvertisement deviceAdvertisement : deviceAdvertisements) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			deviceAdvertisement.setDeviceAdvertisementId(id);
		}
		
		try {
			deviceAdvertisementDao.insert(deviceAdvertisements);
			logger.info("complete method");
			return deviceAdvertisements;
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
	}

	@Transactional
	public void delete(List<Long> deviceAdvertisementIds) throws Exception {
		logger.info("start method: void delete(List<Long> deviceAdvertisementIds), arg deviceAdvertisementIds: " + deviceAdvertisementIds);
		
		if(deviceAdvertisementIds == null || deviceAdvertisementIds.size() == 0 ){
			logger.warn("arg deviceAdvertisementIds is null or size == 0");
			throw new IllegalArgumentException("arg deviceAdvertisementIds is null or size == 0");
		}
		
		try {
			deviceAdvertisementDao.delete(deviceAdvertisementIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method");
	}

	@Override
	public void deleteAll(String deviceMac) throws Exception {
		try {
			logger.info("Enter the :{} method  deviceMac:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac);

			deviceAdvertisementDao.deleteAll(deviceMac);
		} catch (Exception e) {
			logger.error("method {} execute error, deviceMac:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceMac, e);
			throw e;
		}
		
	}

	@Override
	public List<Advertisement> findAdvertisementByDeviceMac(String deviceMac)
			throws Exception {
		List<Advertisement> advertisements=new ArrayList<Advertisement>();
		try {
			logger.info("Enter the :{} method  deviceMac:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac);
			
			advertisements=deviceAdvertisementDao.findAdvertisementByDeviceMac(deviceMac);
		} catch (Exception e) {
			logger.error("method {} execute error, :{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceMac,e);
			throw e;
		}
		return advertisements;
	}

	@Override
	public void update(DeviceAdvertisement deviceAdvertisement) throws Exception {
		try {
			logger.info("Enter the :{} method  deviceAdvertisement:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceAdvertisement);

			deviceAdvertisementDao.update(deviceAdvertisement);
		} catch (Exception e) {
			logger.error("method {} execute error, deviceAdvertisement:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceAdvertisement, e);
			throw e;
		}
		
	}

	@Override
	public DeviceAdvertisement getDeviceAdvertisementById(Long deviceAdvertisementId) throws Exception {
		DeviceAdvertisement deviceAdvertisement= null;
		try {
			logger.info("Enter the :{} method  deviceMac:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceAdvertisementId);
			
			deviceAdvertisement=deviceAdvertisementDao.getDeviceAdvertisementById(deviceAdvertisementId);
		} catch (Exception e) {
			logger.error("method {} execute error, :{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceAdvertisementId,e);
			throw e;
		}
		return deviceAdvertisement;
	}

}
