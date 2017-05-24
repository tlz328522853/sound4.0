package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.CarAddWater;
import com.sdcloud.api.envsanitation.service.CarAddWaterService;
import com.sdcloud.biz.envsanitation.dao.CarAddWaterRelatedDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
public class CarAddWaterServiceImpl implements CarAddWaterService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CarAddWaterRelatedDao carAddWaterDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<CarAddWater> carAddWaters) throws Exception {
		logger.info("start method: long insert(CarAddOil carAddWater), arg carAddWater: "
				+ carAddWaters);

		if (carAddWaters == null||carAddWaters.size()<=0) {
			logger.warn("arg carAddWater is null");
			throw new IllegalArgumentException("arg carAddWater is null");
		}
		
		for (CarAddWater carAddWater : carAddWaters) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			carAddWater.setAddWaterId(id);
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					carAddWaterDao.insert(carAddWaters);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key carAddWaterId:");
						throw se;
						
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> carAddWaters) throws Exception {
		logger.info("start method: void delete(List<Long> carAddWaters), arg carAddWaters: "
				+ carAddWaters);
		if (carAddWaters == null || carAddWaters.size() == 0) {
			logger.warn("arg carAddWaters is null or size=0");
			throw new IllegalArgumentException("arg carAddWaters is null or size=0");
		}
		try {
			
			carAddWaterDao.delete(carAddWaters);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(CarAddWater carAddWater) throws Exception {
		logger.info("start method: void update(CarAddWater carAddWater), arg carAddWater: "
				+ carAddWater);
		if (carAddWater == null || carAddWater.getAddWaterId() == null) {
			logger.warn("arg carAddWater is null or carAddWater 's addWaterId is null");
			throw new IllegalArgumentException("arg carAddWater is null or carAddWater 's addWaterId is null");
		}
		try {
			
			carAddWaterDao.update(carAddWater);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<CarAddWater> findBy(Pager<CarAddWater> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<CarAddWater> findBy(Pager<CarAddWater> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<CarAddWater>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("assetId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = carAddWaterDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<CarAddWater> carAddWaters = carAddWaterDao.findAllBy(param);

			pager.setResult(carAddWaters);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
