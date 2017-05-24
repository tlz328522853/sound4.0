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

import com.sdcloud.api.envsanitation.entity.RpIncomePayMonth;
import com.sdcloud.api.envsanitation.service.RpIncomePayMonthService;
import com.sdcloud.biz.envsanitation.dao.RpIncomePayMonthDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpIncomePayMonthService")
public class RpIncomePayMonthServiceImpl implements RpIncomePayMonthService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpIncomePayMonthDao rpIncomePayMonthDao;

	@Transactional
	public List<RpIncomePayMonth> insert(List<RpIncomePayMonth> incomePayMonths) throws Exception {
		logger.info("start method: insert, arg incomePayMonths: " + incomePayMonths);
		if (incomePayMonths == null || incomePayMonths.size() == 0) {
			logger.warn("incomePayMonths is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpIncomePayMonth rpIncomePayMonth : incomePayMonths) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpIncomePayMonth.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpIncomePayMonthDao.insert(incomePayMonths);
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

		logger.info("complete method, return ts: " + incomePayMonths);
		return incomePayMonths;
	}

	@Transactional
	public Pager<RpIncomePayMonth> findBy(Pager<RpIncomePayMonth> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpIncomePayMonth> findBy(Pager<RpIncomePayMonth> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpIncomePayMonth>();
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
				long totalCount = rpIncomePayMonthDao.getTotalCount(param);
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
			List<RpIncomePayMonth> incomePayMonths = rpIncomePayMonthDao.findAllBy(map);
			pager.setResult(incomePayMonths);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpIncomePayMonth incomePayMonth) throws Exception {
		logger.info("start method: void update(RpIncomePayMonth incomePayMonth), arg incomePayMonth: "
				+ incomePayMonth);
		if (incomePayMonth == null || incomePayMonth.getId() == null) {
			logger.warn("arg incomePayMonth is null or incomePayMonth 's id is null");
			throw new IllegalArgumentException("arg incomePayMonth is null or incomePayMonth 's id is null");
		}
		try {
			rpIncomePayMonthDao.update(incomePayMonth);
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
			rpIncomePayMonthDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Override
	public void file(RpIncomePayMonth rpIncomePayMonth) throws Exception {
		logger.info("start method: void file(RpIncomePayMonth rpIncomePayMonth), arg rpIncomePayMonth: " + rpIncomePayMonth);
		if (rpIncomePayMonth == null || rpIncomePayMonth.getId() == null) {
			logger.warn("arg rpIncomePayMonth is null or rpIncomePayMonth 's id is null");
			throw new IllegalArgumentException("arg rpIncomePayMonth is null or rpIncomePayMonth 's id is null");
		}
		try {
			rpIncomePayMonthDao.file(rpIncomePayMonth);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Override
	public List<RpIncomePayMonth> findIncomePayMonthByParam(Map<String, Object> param) {
		List<RpIncomePayMonth> rpIncomePayMonths = null;
		try {
			rpIncomePayMonths = rpIncomePayMonthDao.findRpIncomePayMonthByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return rpIncomePayMonths;
	}

}