package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.RpAccidentCar;
import com.sdcloud.api.envsanitation.service.RpAccidentCarService;
import com.sdcloud.biz.envsanitation.dao.RpAccidentCarDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpAccidentCarService")
public class RpAccidentCarServiceImpl implements RpAccidentCarService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpAccidentCarDao rpAccidentCarDao;

	@Transactional
	public List<RpAccidentCar> insert(List<RpAccidentCar> accidentCars) throws Exception {
		logger.info("start method: insert, arg accidentCars: " + accidentCars);
		if (accidentCars == null || accidentCars.size() == 0) {
			logger.warn("accidentCars is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpAccidentCar rpAccidentCar : accidentCars) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpAccidentCar.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpAccidentCarDao.insert(accidentCars);
					break;
				} catch (Exception se) {
					logger.error("err method, Exception: " + se);
					if (tryTime == 0)
						throw new RuntimeException("向数据库插入记录失败，请重试");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key assetId: ");
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return ts: " + accidentCars);
		return accidentCars;
	}

	@Transactional
	public Pager<RpAccidentCar> findBy(Pager<RpAccidentCar> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpAccidentCar> findBy(Pager<RpAccidentCar> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpAccidentCar>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("updateTime");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("desc");
			}

			if (pager.isAutoCount()) {
				long totalCount = rpAccidentCarDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("pager", pager); // 将pager装入到map中
			List<RpAccidentCar> accidentCars = rpAccidentCarDao.findAllBy(map);
			pager.setResult(accidentCars);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpAccidentCar accidentCar) throws Exception {
		logger.info("start method: void update(RpAccidentCar accidentCar), arg accidentCar: "
				+ accidentCar);
		if (accidentCar == null || accidentCar.getId() == null) {
			logger.warn("arg accidentCar is null or accidentCar 's id is null");
			throw new IllegalArgumentException("arg accidentCar is null or accidentCar 's id is null");
		}
		try {
			rpAccidentCarDao.update(accidentCar);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void delete(List<Long> ids) throws Exception {
		logger.info("start method: void delete(List<Long> ids), arg ids:" + ids);
		if (ids == null || ids.size() == 0) {
			logger.warn("arg id is null or size = 0");
			throw new IllegalArgumentException("arg id is null");
		}
		try {
			rpAccidentCarDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

}