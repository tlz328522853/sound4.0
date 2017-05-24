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
import com.sdcloud.api.envsanitation.service.RpCostMonthService;
import com.sdcloud.biz.envsanitation.dao.RpCostMonthDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpCostMonthService")
public class RpCostMonthServiceImpl implements RpCostMonthService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpCostMonthDao rpCostMonthDao;

	@Transactional
	public List<RpCostMonth> insert(List<RpCostMonth> costMonths) throws Exception {
		logger.info("start method: insert, arg costMonths: " + costMonths);
		if (costMonths == null || costMonths.size() == 0) {
			logger.warn("costMonths is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpCostMonth rpCostMonth : costMonths) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpCostMonth.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpCostMonthDao.insert(costMonths);
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

		logger.info("complete method, return ts: " + costMonths);
		return costMonths;
	}

	@Transactional
	public Pager<RpCostMonth> findBy(Pager<RpCostMonth> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpCostMonth> findBy(Pager<RpCostMonth> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpCostMonth>();
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
				long totalCount = rpCostMonthDao.getTotalCount(param);
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
			List<RpCostMonth> costMonths = rpCostMonthDao.findAllBy(map);
			pager.setResult(costMonths);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpCostMonth costMonth) throws Exception {
		logger.info("start method: void update(RpCostMonth costMonth), arg costMonth: "
				+ costMonth);
		if (costMonth == null || costMonth.getId() == null) {
			logger.warn("arg costMonth is null or costMonth 's id is null");
			throw new IllegalArgumentException("arg costMonth is null or costMonth 's id is null");
		}
		try {
			rpCostMonthDao.update(costMonth);
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
			rpCostMonthDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Override
	public void file(RpCostMonth rpCostMonth) throws Exception {
		logger.info("start method: void file(RpCostMonth rpCostMonth), arg rpCostMonth: " + rpCostMonth);
		if (rpCostMonth == null || rpCostMonth.getId() == null) {
			logger.warn("arg rpCostMonth is null or rpCostMonth 's id is null");
			throw new IllegalArgumentException("arg rpCostMonth is null or rpCostMonth 's id is null");
		}
		try {
			rpCostMonthDao.file(rpCostMonth);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Override
	public List<RpCostMonth> findRpCostMonthByParam(Map<String, Object> param) {
		List<RpCostMonth> rpCostMonths = null;
		try {
			rpCostMonths = rpCostMonthDao.findRpCostMonthByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return rpCostMonths;
	}

}