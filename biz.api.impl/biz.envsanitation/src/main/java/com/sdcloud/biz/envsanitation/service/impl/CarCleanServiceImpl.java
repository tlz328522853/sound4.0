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

import com.sdcloud.api.envsanitation.entity.CarClean;
import com.sdcloud.api.envsanitation.service.CarCleanService;
import com.sdcloud.biz.envsanitation.dao.CarCleanRelatedDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
public class CarCleanServiceImpl implements CarCleanService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CarCleanRelatedDao carCleanDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<CarClean> carCleans) throws Exception {
		logger.info("start method: long insert(CarClean carClean), arg carClean: "
				+ carCleans);

		if (carCleans == null||carCleans.size()<=0) {
			logger.warn("arg carClean is null");
			throw new IllegalArgumentException("arg carClean is null");
		}
		for (CarClean carClean : carCleans) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			carClean.setCleanId(id);
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					carCleanDao.insert(carCleans);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key carCleanId:" + se);
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
	public void delete(List<Long> carCleans) throws Exception {
		logger.info("start method: void delete(List<Long> carCleans), arg carCleans: "
				+ carCleans);
		if (carCleans == null || carCleans.size() == 0) {
			logger.warn("arg carAddOils is null or size=0");
			throw new IllegalArgumentException("arg carCleans is null or size=0");
		}
		try {
			
			carCleanDao.delete(carCleans);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(CarClean carClean) throws Exception {
		logger.info("start method: void update(CarClean carClean), arg carClean: "
				+ carClean);
		if (carClean == null || carClean.getCleanId() == null) {
			logger.warn("arg carClean is null or carClean 's cleanId is null");
			throw new IllegalArgumentException("arg carClean is null or carClean 's cleanId is null");
		}
		try {
			
			carCleanDao.update(carClean);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<CarClean> findBy(Pager<CarClean> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<CarClean> findBy(Pager<CarClean> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<CarClean>();
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
				long totalCount = carCleanDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<CarClean> carCleans = carCleanDao.findAllBy(param);

			pager.setResult(carCleans);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
