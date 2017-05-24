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

import com.sdcloud.api.envsanitation.entity.RpCostMonth;
import com.sdcloud.api.envsanitation.entity.RpSecurityProductionMonth;
import com.sdcloud.api.envsanitation.service.RpSecurityProductionMonthService;
import com.sdcloud.biz.envsanitation.dao.RpSecurityProductionMonthDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpSecurityProductionMonthService")
public class RpSecurityProductionMonthServiceImpl implements RpSecurityProductionMonthService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpSecurityProductionMonthDao rpSecurityProductionMonthDao;

	@Transactional
	public List<RpSecurityProductionMonth> insert(List<RpSecurityProductionMonth> securityProductionMonths) throws Exception {
		logger.info("start method: insert, arg securityProductionMonths: " + securityProductionMonths);
		if (securityProductionMonths == null || securityProductionMonths.size() == 0) {
			logger.warn("securityProductionMonths is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpSecurityProductionMonth rpSecurityProductionMonth : securityProductionMonths) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpSecurityProductionMonth.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpSecurityProductionMonthDao.insert(securityProductionMonths);
					break;
				} catch (Exception se) {
					logger.error("err method, Exception: " + se);
					if (tryTime == 0)
						throw new RuntimeException("向数据库插入记录失败，主键重复，请重试");
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

		logger.info("complete method, return ts: " + securityProductionMonths);
		return securityProductionMonths;
	}

	@Transactional
	public Pager<RpSecurityProductionMonth> findBy(Pager<RpSecurityProductionMonth> pager, Map<String, Object> param) 
			throws Exception {
		logger.info("start method: Pager<RpSecurityProductionMonth> findBy(Pager<RpSecurityProductionMonth> pager, "
				+ "Map<String, Object> param), " + "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpSecurityProductionMonth>();
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
				long totalCount = rpSecurityProductionMonthDao.getTotalCount(param);
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
			List<RpSecurityProductionMonth> securityProductionMonths = rpSecurityProductionMonthDao.findAllBy(map);
			pager.setResult(securityProductionMonths);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpSecurityProductionMonth securityProductionMonth) throws Exception {
		logger.info("start method: void update(RpSecurityProductionMonth securityProductionMonth), arg securityProductionMonth: "
				+ securityProductionMonth);
		if (securityProductionMonth == null || securityProductionMonth.getId() == null) {
			logger.warn("arg securityProductionMonth is null or securityProductionMonth 's id is null");
			throw new IllegalArgumentException("arg securityProductionMonth is null or securityProductionMonth 's id is null");
		}
		try {
			rpSecurityProductionMonthDao.update(securityProductionMonth);
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
			rpSecurityProductionMonthDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Override
	public List<RpSecurityProductionMonth> findRpSecurityProductionMonthByParam(Map<String, Object> param) {
		List<RpSecurityProductionMonth> rpSecurityProductionMonths = null;
		try {
			rpSecurityProductionMonths = rpSecurityProductionMonthDao.findRpSecurityProductionMonthByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return rpSecurityProductionMonths;
	}

}