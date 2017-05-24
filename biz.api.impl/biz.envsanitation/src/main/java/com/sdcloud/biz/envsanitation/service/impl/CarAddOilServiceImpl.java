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

import com.sdcloud.api.envsanitation.entity.CarAddOil;
import com.sdcloud.api.envsanitation.service.CarAddOilService;
import com.sdcloud.biz.envsanitation.dao.CarAddOilRelatedDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
public class CarAddOilServiceImpl implements CarAddOilService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CarAddOilRelatedDao carAddOilDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<CarAddOil> carAddOils) throws Exception {
		logger.info("start method: long insert(CarAddOil carAddOil), arg carAddOil: "
				+ carAddOils);

		if (carAddOils==null||carAddOils.size()<=0) {
			logger.warn("arg carAddOil is null");
			throw new IllegalArgumentException("arg carAddOil is null");
		}
		for (CarAddOil carAddOil : carAddOils) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			carAddOil.setAddOidId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					carAddOilDao.insert(carAddOils);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
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
	public void delete(List<Long> carAddOils) throws Exception {
		logger.info("start method: void delete(List<Long> carAddOils), arg carAddOils: "
				+ carAddOils);
		if (carAddOils == null || carAddOils.size() == 0) {
			logger.warn("arg carAddOils is null or size=0");
			throw new IllegalArgumentException("arg carAddOils is null or size=0");
		}
		try {
			
			carAddOilDao.delete(carAddOils);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(CarAddOil carAddOil) throws Exception {
		logger.info("start method: void update(CarAddOil carAddOil), arg carAddOil: "
				+ carAddOil);
		if (carAddOil == null || carAddOil.getAddOidId() == null) {
			logger.warn("arg carAddOil is null or carAddOil 's addOidId is null");
			throw new IllegalArgumentException("arg carAddOil is null or carAddOil 's addOidId is null");
		}
		try {
			
			carAddOilDao.update(carAddOil);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<CarAddOil> findBy(Pager<CarAddOil> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<CarAddOil> findBy(Pager<CarAddOil> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<CarAddOil>();
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
				long totalCount = carAddOilDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<CarAddOil> carAddOils = carAddOilDao.findAllBy(param);

			pager.setResult(carAddOils);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
