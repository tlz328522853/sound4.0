package com.sdcloud.biz.authority.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.authority.service.CarService;
import com.sdcloud.biz.authority.dao.CarDao;
@Service("carService")
public class CarServiceImpl implements CarService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CarDao carDao;
	@Override
	public void addCarChassisType(Map<String, String> cardNumber_chassisType) throws Exception {
		try {
			logger.info("Enter the :{} method  cardNumber_chassisType:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),cardNumber_chassisType);

			carDao.addCarChassisType(cardNumber_chassisType);
		} catch (Exception e) {
			logger.error("method {} execute error, cardNumber_chassisType:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),cardNumber_chassisType, e);
			throw e;
		}
	}
	@Override
	public void updateCarCardNumber(String oldCardNumber, String newCardNumber)
			throws Exception {
		try {
			logger.info("Enter the :{} method  oldCardNumber:{} newCardNumber:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),oldCardNumber,newCardNumber);
			carDao.updateCarCardNumber(oldCardNumber, newCardNumber);
		} catch (Exception e) {
			logger.error("method {} execute error, oldCardNumber:{} newCardNumber:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), oldCardNumber, newCardNumber,e);
			throw e;
		}
		
	}
	@Override
	public void updateCarChassisType(String cardNumber,
			String newCarChassisType) throws Exception {
		try {
			logger.info("Enter the :{} method  cardNumber:{} newCarChassisType:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),cardNumber,newCarChassisType);
			carDao.updateCarChassisType(cardNumber, newCarChassisType);
		} catch (Exception e) {
			logger.error("method {} execute error, cardNumber:{} newCarChassisType:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),cardNumber, newCarChassisType,e);
			throw e;
		}
		
	}
	@Override
	public void removeCarChassisType(String cardNumber)
			throws Exception {
		try {
			logger.info("Enter the :{} method  cardNumber:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),cardNumber);
			carDao.removeCarChassisType(cardNumber);
		} catch (Exception e) {
			logger.error("method {} execute error, cardNumber:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), cardNumber,e);
			throw e;
		}
		
	}

}
