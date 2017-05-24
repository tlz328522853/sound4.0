package com.sdcloud.biz.envsanitation.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.envsanitation.entity.AssetCar;
import com.sdcloud.api.envsanitation.service.AssetCarService;
import com.sdcloud.biz.envsanitation.dao.AssetCarDao;

/**
 * 
 * @author lms
 */
@Service("assetCarService")
public class AssetCarServciceImpl extends AssetServciceImpl<AssetCar>
		implements AssetCarService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	AssetCarDao assetCarDao;
	@Override
	public AssetCar findAssetCarByCarNo(String carNumber) throws Exception {
		try {
			logger.info("Enter the :{} method  carNumber:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),carNumber);

			AssetCar car=assetCarDao.findAssetCarByCarNo(carNumber);
			return car;
		} catch (Exception e) {
			logger.error("method {} execute error, carNumber:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),carNumber, e);
			throw e;
		}
	}
	@Override
	public Map<String, String> findAllCarChassisNumber() throws Exception {
		Map<String, String> car_chassis=new HashMap<String,String>();;
		try {
			logger.info("Enter the :{} method", Thread.currentThread()
					.getStackTrace()[1].getMethodName());
			List<Map<String, String>> result=assetCarDao.findAllCarChassisNumber();
			for (Map<String, String> maprel : result) {
				String key=null;
				String value=null;
				for (Entry<String, String> mapv : maprel.entrySet()) {
					if(mapv.getKey().equals("cardNumber")){
						key=mapv.getValue();
					}else{
						value=mapv.getValue();
					}
					
				}
				if(key.trim().length()<12){
					int length=12-key.trim().length();
					for (int i = 0; i <length; i++) {
						key="0"+key;
					}
					
				}
				car_chassis.put(key.trim(),value);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			throw e;
		}
		return car_chassis;
	}
	@Override
	public List<AssetCar> findAssetCarByCarNos(Map<String, Object> param)
			throws Exception {
		try {
			logger.info("Enter the :{} param  :{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),param);

			return assetCarDao.findAssetCarByCarNos(param);
		} catch (Exception e) {
			logger.error("method {} execute error, param:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),param, e);
			throw e;
		}
	}
	@Override
	public List<AssetCar> findAssetCarByCompanyIds(Map<String, Object> param) throws Exception {
		try {
			logger.info("Enter the :{} param  :{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(), param);

			return assetCarDao.findAssetCarByCompanyIds(param);
		} catch (Exception e) {
			logger.error("method {} execute error, param:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), param, e);
			throw e;
		}
	}
	@Override
	public List<AssetCar> findAssetCarByParam(Map<String, Object> param) {
		List<AssetCar> assetCars = null;
		try {
			assetCars = assetCarDao.findAssetCarByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return assetCars;
	}
}