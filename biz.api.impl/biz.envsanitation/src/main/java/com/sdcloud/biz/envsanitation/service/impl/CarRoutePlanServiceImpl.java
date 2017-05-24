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
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.CarRoutePlan;
import com.sdcloud.api.envsanitation.service.CarRoutePlanService;
import com.sdcloud.biz.envsanitation.dao.CarRoutePlanDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

//@Service("carRoutePlanService")
public class CarRoutePlanServiceImpl implements CarRoutePlanService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CarRoutePlanDao carRoutePlanDao;

	@Transactional
	public Pager<CarRoutePlan> findAll(Map<String, Object> param, Pager<CarRoutePlan> pager) throws Exception {

		logger.info("start method: Pager<CarRoutePlan> findAll(Map<String, Object> param, "
				+ "Pager<CarRoutePlan> pager), arg param: " + param + ", arg apger: " + pager);
		if (param == null) {
			logger.warn("arg params is null");
			throw new RuntimeException("arg params is null");
		}
		if (pager == null) {
			pager = new Pager<CarRoutePlan>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("carRoutePlanId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}

			if (pager.isAutoCount()) {
				long totalCount = carRoutePlanDao.getTotalNum(param);
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
			List<CarRoutePlan> empRoutePlans = carRoutePlanDao.findAll(map);
			pager.setResult(empRoutePlans);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;
	}
	/*public Pager<CarRoutePlan> findAll(Map<String, Object> params, Pager<CarRoutePlan> pager) throws Exception{
		
		logger.info("start method: EmpRoutePlanServiceImpl.findAll(), arg params: " + params);
		if(params == null){
			logger.warn("arg params is null");
			throw new IllegalArgumentException("arg params is null");
		}
		try {
			if(pager ==null){
				pager = new Pager<CarRoutePlan>();
			}
			else{
				params.put("pager", pager);
			}
			List<CarRoutePlan> empRoutePlans = carRoutePlanDao.findAll(params);
			Long count = carRoutePlanDao.getTotalNum(params);
			pager.setTotalCount(count);
			pager.setResult(empRoutePlans);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;
	}*/


	@Transactional
	public void delete(List<Long> empRoutePlanIds) throws Exception{

		logger.info("start method, EmpRoutePlanServiceImpl.delete, empRoutePlanIds:" + empRoutePlanIds);
		
		if(empRoutePlanIds == null || empRoutePlanIds.size() == 0){
			logger.warn("empRoutePlanIds is null or empty");
			throw new IllegalArgumentException("empRoutePlanIds is null or empty");
		}
		
		try{
			carRoutePlanDao.delete(empRoutePlanIds);
		}catch(Exception e){
			logger.error("err when EmpRoutePlanServiceImpl.delete, empRoutePlanIds:" + empRoutePlanIds,e);
			throw e;
		}
		
		logger.info("complete method, EmpRoutePlanServiceImpl.delete");
	}

	@Transactional
	public long insert(CarRoutePlan carRoutePlan) throws Exception{
		logger.info("start method: EmpRoutePlanServiceImpl.insert, empRoutePlan: "
				+ carRoutePlan);

		if (carRoutePlan == null) {
			logger.warn("empRoutePlan is null");
			throw new IllegalArgumentException("empRoutePlan is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new empRoutePlanId:" + id);
				carRoutePlan.setCarRoutePlanId(id);
				try {
					carRoutePlanDao.insert(carRoutePlan);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key empRoutePlanId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return id: " + id);
		return id;
	}

	@Transactional
	public void update(CarRoutePlan carRoutePlan) throws Exception{

		logger.info("start method: void update, arg t: "
				+ carRoutePlan);
		if (carRoutePlan == null || carRoutePlan.getCarRoutePlanId() == null) {
			logger.warn("arg empRoutePlan is null or EmpRoutePlanId is null");
			throw new IllegalArgumentException("arg empRoutePlan is null or  EmpRoutePlanId is null");
		}
		try {
			//两张表修改
			carRoutePlanDao.update(carRoutePlan);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, EmpRoutePlanServiceImpl.update");

	}

}
