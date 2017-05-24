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

import com.sdcloud.api.envsanitation.entity.CarOilAnomaly;
import com.sdcloud.api.envsanitation.service.CarOilAnomalyService;
import com.sdcloud.biz.envsanitation.dao.CarOilAnomalyDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service(value="anomalyService")
public class CarOilAnomalyImpl implements CarOilAnomalyService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CarOilAnomalyDao carOilAnomalyDao;
	
	@Transactional
	public CarOilAnomaly insert(CarOilAnomaly anomaly) throws Exception {
		logger.info("start method: CarOilAnomaly insert(CarOilAnomaly anomaly), arg anomaly: " + anomaly);
		
		if(anomaly == null){
			logger.warn("arg anomaly is null");
			throw new IllegalArgumentException("arg anomaly is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				anomaly.setOilAnomalyId(id);
				
				try {
					carOilAnomalyDao.insert(anomaly);
					break;
				} catch (Exception se) {
					if(tryTime == 0)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key id:" + id);
						continue;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return anomaly: " + anomaly);
		return anomaly;
	}

	@Transactional
	public void delete(List<Long> oilAnomalyIds) throws Exception {
		
		logger.info("start method: void delete(List<Long> oilAnomalyIds), arg oilAnomalyIds: " + oilAnomalyIds);
		
		if(oilAnomalyIds == null || oilAnomalyIds.size() == 0){
			logger.warn("arg oilAnomalyIds is null or size=0");
			throw new IllegalArgumentException("arg oilAnomalyIds is null or size=0");
		}
		try {
			carOilAnomalyDao.delete(oilAnomalyIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return void");
	}

	@Transactional
	public CarOilAnomaly update(CarOilAnomaly anomaly) throws Exception {
		logger.info("start method: void update(CarOilAnomaly anomaly), arg anomaly: " + anomaly);
		if(anomaly == null || anomaly.getOilAnomalyId() == null){
			logger.warn("arg anomaly is null or anomaly 's funcId is null");
			throw new IllegalArgumentException("arg anomaly is null or anomaly 's funcId is null");
		}
		try {
			carOilAnomalyDao.update(anomaly);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			anomaly = findById(anomaly.getOilAnomalyId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return anomaly: " + anomaly);
		return anomaly;
	}

	@Transactional
	public CarOilAnomaly findById(Long tblId) throws Exception {
		logger.info("start method: CarOilAnomaly findById(Long tblId), arg tblId: " + tblId);
		if(tblId == null){
			logger.warn("arg tblId is null");
			throw new IllegalArgumentException("arg tblId is null");
		}
		CarOilAnomaly anomaly = null;
		try {
			anomaly = carOilAnomalyDao.findById(tblId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return anomaly: " + anomaly);
		return anomaly;
	}
	
	@Transactional
	public Pager<CarOilAnomaly> findAll(Pager<CarOilAnomaly> pager, Map<String, Object> param) {
		logger.info("start method: Pager<CarOilAnomaly> findAll(Pager<CarOilAnomaly> pager), arg pager: "
				+ pager + ", arg param:" + param);
		
		//不需要分页信息执行
		if (pager == null) {
			pager = new Pager<CarOilAnomaly>();
			List<CarOilAnomaly> dics;
			try {
				dics = carOilAnomalyDao.findAll(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			pager.setResult(dics);
			
			logger.info("complete method, return pager: " + pager);
			return pager;
			
		} else {
			
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("oilAnomalyId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
	
				if (pager.isAutoCount()) {
					long totalCount = carOilAnomalyDao.getTotalCount(param);
					pager.setTotalCount(totalCount);
					logger.info("querying total count result : " + totalCount);
				}
	
				Map<String, Object> map = new HashMap<String, Object>();
				if (param != null && param.size() > 0) {
					for (Entry<String, Object> entry : param.entrySet()) {
						map.put(entry.getKey(), entry.getValue());
					}
				}
				map.put("pager", pager);
				List<CarOilAnomaly> anomalys = carOilAnomalyDao.findAll(map);
				pager.setResult(anomalys);
	
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			logger.info("complete method, return pager: " + pager);
			return pager;
		}
	}
}
