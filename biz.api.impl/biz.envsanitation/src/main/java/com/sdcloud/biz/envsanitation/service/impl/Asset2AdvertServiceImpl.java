package com.sdcloud.biz.envsanitation.service.impl;

import com.sdcloud.api.envsanitation.entity.Asset2Advert;
import com.sdcloud.api.envsanitation.service.Asset2AdvertService;
import com.sdcloud.biz.envsanitation.dao.Asset2AdvertDao;
import com.sdcloud.framework.entity.Pager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 广告模块中用于关联设备
 * 
 * @author lms
 *
 */
@Service("asset2AdvertService")
public class Asset2AdvertServiceImpl implements Asset2AdvertService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Asset2AdvertDao asset2AdvertDao;

	@Override
	public Pager<Asset2Advert> findAllCar(Pager<Asset2Advert> pager, Map<String, Object> param) throws Exception {
		logger.info(
				"start Pager<BusiMgr> findByOrg(Pager<BusiMgr> pager, Map<String, Object> param), arg pager: {}, arg param: {}",
				pager, param);
		if (param == null) {
			param = new HashMap<>();
		}

		// 无分页信息执行
		if (pager == null) {

			/*
			 * pager = new Pager<>(); List<BusiMgr> ts;
			 * 
			 * try { ts = mgrDao.findByMap(param); } catch (Exception e) {
			 * logger.error("err method, Exception: " + e); throw e; }
			 * pager.setResult(ts);
			 */

		} else {

			try {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("assetId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
				if (pager.isAutoCount()) {
					long count = asset2AdvertDao.count(param);
					pager.setTotalCount(count);
				}

				param.put("pager", pager);
				List<Asset2Advert> result = asset2AdvertDao.findAllCar(param);
				pager.setResult(result);

			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}

		}
		logger.info("complete method, return pager: {}", pager);
		return pager;
	}
	
	@Override
	public Pager<Asset2Advert> findAllStation(Pager<Asset2Advert> pager, Map<String, Object> param) throws Exception {
		logger.info(
				"start Pager<BusiMgr> findByOrg(Pager<BusiMgr> pager, Map<String, Object> param), arg pager: {}, arg param: {}",
				pager, param);
		if (param == null) {
			param = new HashMap<>();
		}
		
		// 无分页信息执行
		if (pager == null) {
			
			/*
			 * pager = new Pager<>(); List<BusiMgr> ts;
			 * 
			 * try { ts = mgrDao.findByMap(param); } catch (Exception e) {
			 * logger.error("err method, Exception: " + e); throw e; }
			 * pager.setResult(ts);
			 */
			
		} else {
			
			try {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("assetId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
				if (pager.isAutoCount()) {
					long count = asset2AdvertDao.count(param);
					pager.setTotalCount(count);
				}
				
				param.put("pager", pager);
				List<Asset2Advert> result = asset2AdvertDao.findAllStation(param);
				pager.setResult(result);
				
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			
		}
		logger.info("complete method, return pager: {}", pager);
		return pager;
	}
	
	@Override
	public Pager<Asset2Advert> findAllWc(Pager<Asset2Advert> pager, Map<String, Object> param) throws Exception {
		logger.info(
				"start Pager<BusiMgr> findByOrg(Pager<BusiMgr> pager, Map<String, Object> param), arg pager: {}, arg param: {}",
				pager, param);
		if (param == null) {
			param = new HashMap<>();
		}
		
		// 无分页信息执行
		if (pager == null) {
			
			/*
			 * pager = new Pager<>(); List<BusiMgr> ts;
			 * 
			 * try { ts = mgrDao.findByMap(param); } catch (Exception e) {
			 * logger.error("err method, Exception: " + e); throw e; }
			 * pager.setResult(ts);
			 */
			
		} else {
			
			try {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("assetId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
				if (pager.isAutoCount()) {
					long count = asset2AdvertDao.count(param);
					pager.setTotalCount(count);
				}
				
				param.put("pager", pager);
				List<Asset2Advert> result = asset2AdvertDao.findAllWc(param);
				pager.setResult(result);
				
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			
		}
		logger.info("complete method, return pager: {}", pager);
		return pager;
	}

}