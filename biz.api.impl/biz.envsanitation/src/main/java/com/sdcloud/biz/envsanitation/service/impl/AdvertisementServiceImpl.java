package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.Advertisement;
import com.sdcloud.api.envsanitation.entity.CarAddOil;
import com.sdcloud.api.envsanitation.service.AdvertisementService;
import com.sdcloud.biz.envsanitation.dao.AdvertisementDao;
import com.sdcloud.biz.envsanitation.dao.DeviceAdvertisementDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
@Service("advertisementService")
public class AdvertisementServiceImpl implements AdvertisementService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DeviceAdvertisementDao deviceAdvertisementDao;
	@Autowired
	private AdvertisementDao advertDao;
	
	@Transactional
	public Advertisement insert(Advertisement advert) throws Exception {
		logger.info("start method: CarOilAnomaly insert(Advertisement advert), arg advert: " + advert);
		
		if(advert == null){
			logger.warn("arg advert is null");
			throw new IllegalArgumentException("arg advert is null");
		}
		long id = -1;
		try {
			id = UUIDUtil.getUUNum();
			advert.setAdvertisementId(id);
			advertDao.insert(advert);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return advert: " + advert);
		return advert;
	}

	@Transactional
	public void delete(List<Long> advertIds) throws Exception {
		logger.info("start method: void delete(List<Long> advertIds), arg advertIds: " + advertIds);
		
		if(advertIds == null || advertIds.size() == 0 ){
			logger.warn("arg advert is null or size == 0");
			throw new IllegalArgumentException("arg advert is null or size == 0");
		}
		
		try {
			deviceAdvertisementDao.deleteByAdverts(advertIds);
			advertDao.delete(advertIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method");
	}

	@Transactional
	public void update(Advertisement advert) throws Exception {
		logger.info("start method: update(Advertisement advert), arg advert: " + advert);
		
		if(advert == null  ){
			logger.warn("arg advert is null ");
			throw new IllegalArgumentException("arg advert is null ");
		}
		
		try {
			advertDao.update(advert);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method");
	}

	@Transactional
	public Pager<Advertisement> findBy(Pager<Advertisement> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<CarAddOil> findBy(Pager<CarAddOil> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<Advertisement>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("advertisementId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = advertDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Advertisement> advert = advertDao.findAllBy(param);

			pager.setResult(advert);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.debug("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public Advertisement getAdvertisementById(Long advertId) throws Exception {
		logger.info("start method: getAdvertisementById(Long advertId), arg advertId: " + advertId);
		
		if(advertId == null  ){
			logger.warn("arg advert is null ");
			throw new IllegalArgumentException("arg advert is null ");
		}
		Advertisement advert = null;
		try {
			advert = advertDao.getAdvertisementById(advertId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method");
		return advert;
	}

}
