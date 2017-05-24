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

import com.sdcloud.api.envsanitation.entity.EmpInspecPlan;
import com.sdcloud.api.envsanitation.entity.EmpRoutePlan;
import com.sdcloud.api.envsanitation.service.EmpInspecPlanService;
import com.sdcloud.api.envsanitation.service.EmpInspecPlanService;
import com.sdcloud.biz.envsanitation.dao.EmpInspecPlanDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

//@Service("empInspecPlanService")
public class EmpInspecPlanServiceImpl implements EmpInspecPlanService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpInspecPlanDao empInspecPlanDao;

	@Transactional
	public Pager<EmpInspecPlan> findAll(Map<String, Object> param, Pager<EmpInspecPlan> pager) {

		logger.info("start method: Pager<EmpInspecPlan> findAll(Map<String, Object> param, "
				+ "Pager<EmpInspecPlan> pager), arg param: " + param + ", arg apger: " + pager);
		if (param == null) {
			logger.warn("arg params is null");
			throw new RuntimeException("arg params is null");
		}
		if (pager == null) {
			pager = new Pager<EmpInspecPlan>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("empInspecPlanId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}

			if (pager.isAutoCount()) {
				long totalCount = empInspecPlanDao.getTotalNum(param);
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
			List<EmpInspecPlan> empInspecPlans = empInspecPlanDao.findAll(map);
			pager.setResult(empInspecPlans);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;

	}
/*	public Pager<EmpInspecPlan> findAll(Map<String, Object> params, Pager<EmpInspecPlan> pager) {
		
		logger.info("start method: EmpInspecPlanServiceImpl.findAll(), arg params: " + params);
		if(params == null){
			logger.warn("arg params is null");
			throw new IllegalArgumentException("arg params is null");
		}
		try {
			if(pager ==null){
				pager = new Pager<EmpInspecPlan>();
			}
			else{
				params.put("pager", pager);
			}
			List<EmpInspecPlan> empInspecPlans = empInspecPlanDao.findAll(params);
			Long count = empInspecPlanDao.getTotalNum(params);
			pager.setTotalCount(count);
			pager.setResult(empInspecPlans);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, result: " + pager);
		return pager;
	}
*/
	@Transactional
	public long insert(EmpInspecPlan empInspecPlan) {
		
		logger.info("start method: EmpInspecPlanServiceImpl.insert, empInspecPlan: "
				+ empInspecPlan);

		if (empInspecPlan == null) {
			logger.warn("empInspecPlan is null");
			throw new IllegalArgumentException("empInspecPlan is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new empInspecPlanId:" + id);
				empInspecPlan.setEmpInspecPlanId(id);
				try {
					empInspecPlanDao.insert(empInspecPlan);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key empInspecPlanId:" + id);
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
	public void update(EmpInspecPlan empInspecPlan) {
		
		logger.info("start method: void update, arg t: "
				+ empInspecPlan);
		if (empInspecPlan == null || empInspecPlan.getEmpInspecPlanId() == null) {
			logger.warn("arg EmpInspecPlan is null or empInspecPlanId is null");
			throw new IllegalArgumentException("arg empInspecPlan is null or  empInspecPlanId is null");
		}
		try {
			//两张表修改
			empInspecPlanDao.update(empInspecPlan);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, EmpInspecPlanServiceImpl.update");

	}

	@Transactional
	public void delete(List<Long> EmpInspecPlanIds) {

		logger.info("start method, EmpInspecPlanServiceImpl.delete, empInspecPlanIds:" + EmpInspecPlanIds);
		
		if(EmpInspecPlanIds == null || EmpInspecPlanIds.size() == 0){
			logger.warn("EmpInspecPlanIds is null or empty");
			throw new IllegalArgumentException("empInspecPlanIds is null or empty");
		}
		
		try{
			empInspecPlanDao.delete(EmpInspecPlanIds);
		}catch(Exception e){
			logger.error("err when EmpInspecPlanServiceImpl.delete, EmpInspecPlanIds:" + EmpInspecPlanIds,e);
			throw e;
		}
		
		logger.info("complete method, EmpInspecPlanServiceImpl.delete");
	}

}
