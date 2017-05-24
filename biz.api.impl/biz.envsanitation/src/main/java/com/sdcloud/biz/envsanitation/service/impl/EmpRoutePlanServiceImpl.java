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

import com.sdcloud.api.envsanitation.entity.EmpRoutePlan;
import com.sdcloud.api.envsanitation.service.EmpRoutePlanService;
import com.sdcloud.biz.envsanitation.dao.EmpRoutePlanDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

//@Service("empRoutePlanService")
public class EmpRoutePlanServiceImpl implements EmpRoutePlanService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpRoutePlanDao empRoutePlanDao;

	@Transactional
	public Pager<EmpRoutePlan> findAll(Map<String, Object> param, Pager<EmpRoutePlan> pager) throws Exception {

		logger.info("start method: Pager<EmpRoutePlan> findAll(Map<String, Object> param, "
				+ "Pager<EmpRoutePlan> pager), arg param: " + param + ", arg apger: " + pager);
		if (param == null) {
			logger.warn("arg params is null");
			throw new RuntimeException("arg params is null");
		}
		if (pager == null) {
			pager = new Pager<EmpRoutePlan>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("empRoutePlanId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}

			if (pager.isAutoCount()) {
				long totalCount = empRoutePlanDao.getTotalNum(param);
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
			List<EmpRoutePlan> empRoutePlans = empRoutePlanDao.findAll(map);
			pager.setResult(empRoutePlans);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;

	}
/*	public Pager<EmpRoutePlan> findAll(Map<String, Object> params, Pager<EmpRoutePlan> pager) throws Exception{
		
		logger.info("start method: EmpRoutePlanServiceImpl.findAll(), arg params: " + params);
		if(params == null){
			logger.warn("arg params is null");
			throw new IllegalArgumentException("arg params is null");
		}
		try {
			if(pager ==null){
				pager = new Pager<EmpRoutePlan>();
			}
			else{
				params.put("pager", pager);
			}
			List<EmpRoutePlan> empRoutePlans = empRoutePlanDao.findAll(params);
			Long count = empRoutePlanDao.getTotalNum(params);
			pager.setTotalCount(count);
			pager.setResult(empRoutePlans);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;
	}
*/
	@Transactional
	public long insert(EmpRoutePlan empRoutePlan) throws Exception{
		
		logger.info("start method: EmpRoutePlanServiceImpl.insert, empRoutePlan: "
				+ empRoutePlan);

		if (empRoutePlan == null) {
			logger.warn("empRoutePlan is null");
			throw new IllegalArgumentException("empRoutePlan is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new empRoutePlanId:" + id);
				empRoutePlan.setEmpRoutePlanId(id);
				try {
					empRoutePlanDao.insert(empRoutePlan);
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
	public void update(EmpRoutePlan empRoutePlan) throws Exception{
		
		logger.info("start method: void update, arg t: "
				+ empRoutePlan);
		if (empRoutePlan == null || empRoutePlan.getEmpRoutePlanId() == null) {
			logger.warn("arg empRoutePlan is null or EmpRoutePlanId is null");
			throw new IllegalArgumentException("arg empRoutePlan is null or  EmpRoutePlanId is null");
		}
		try {
			//两张表修改
			empRoutePlanDao.update(empRoutePlan);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, EmpRoutePlanServiceImpl.update");

	}

	@Transactional
	public void delete(List<Long> empRoutePlanIds) throws Exception{

		logger.info("start method, EmpRoutePlanServiceImpl.delete, empRoutePlanIds:" + empRoutePlanIds);
		
		if(empRoutePlanIds == null || empRoutePlanIds.size() == 0){
			logger.warn("empRoutePlanIds is null or empty");
			throw new IllegalArgumentException("empRoutePlanIds is null or empty");
		}
		
		try{
			empRoutePlanDao.delete(empRoutePlanIds);
		}catch(Exception e){
			logger.error("err when EmpRoutePlanServiceImpl.delete, empRoutePlanIds:" + empRoutePlanIds,e);
			throw e;
		}
		
		logger.info("complete method, EmpRoutePlanServiceImpl.delete");
	}

}
