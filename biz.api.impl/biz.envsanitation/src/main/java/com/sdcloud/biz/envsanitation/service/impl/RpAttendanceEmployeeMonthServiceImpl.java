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

import com.sdcloud.api.envsanitation.entity.RpAttendanceEmployeeMonth;
import com.sdcloud.api.envsanitation.entity.RpCostMonth;
import com.sdcloud.api.envsanitation.service.RpAttendanceEmployeeMonthService;
import com.sdcloud.biz.envsanitation.dao.RpAttendanceEmployeeMonthDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpAttendanceEmployeeMonthService")
public class RpAttendanceEmployeeMonthServiceImpl implements RpAttendanceEmployeeMonthService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpAttendanceEmployeeMonthDao rpAttendanceEmployeeMonthDao;

	@Transactional
	public List<RpAttendanceEmployeeMonth> insert(List<RpAttendanceEmployeeMonth> attendanceEmployeeMonths) throws Exception {
		logger.info("start method: insert, arg attendanceEmployeeMonths: " + attendanceEmployeeMonths);
		if (attendanceEmployeeMonths == null || attendanceEmployeeMonths.size() == 0) {
			logger.warn("attendanceEmployeeMonths is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpAttendanceEmployeeMonth rpAttendanceEmployeeMonth : attendanceEmployeeMonths) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpAttendanceEmployeeMonth.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpAttendanceEmployeeMonthDao.insert(attendanceEmployeeMonths);
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

		logger.info("complete method, return ts: " + attendanceEmployeeMonths);
		return attendanceEmployeeMonths;
	}

	@Transactional
	public Pager<RpAttendanceEmployeeMonth> findBy(Pager<RpAttendanceEmployeeMonth> pager, Map<String, Object> param) 
			throws Exception {
		logger.info("start method: Pager<RpAttendanceEmployeeMonth> findBy(Pager<RpAttendanceEmployeeMonth> pager, "
				+ "Map<String, Object> param), " + "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpAttendanceEmployeeMonth>();
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
				long totalCount = rpAttendanceEmployeeMonthDao.getTotalCount(param);
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
			List<RpAttendanceEmployeeMonth> attendanceEmployeeMonths = rpAttendanceEmployeeMonthDao.findAllBy(map);
			pager.setResult(attendanceEmployeeMonths);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpAttendanceEmployeeMonth attendanceEmployeeMonth) throws Exception {
		logger.info("start method: void update(RpAttendanceEmployeeMonth attendanceEmployeeMonth), arg attendanceEmployeeMonth: "
				+ attendanceEmployeeMonth);
		if (attendanceEmployeeMonth == null || attendanceEmployeeMonth.getId() == null) {
			logger.warn("arg attendanceEmployeeMonth is null or attendanceEmployeeMonth 's id is null");
			throw new IllegalArgumentException("arg attendanceEmployeeMonth is null or attendanceEmployeeMonth 's id is null");
		}
		try {
			rpAttendanceEmployeeMonthDao.update(attendanceEmployeeMonth);
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
			rpAttendanceEmployeeMonthDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Override
	public List<RpAttendanceEmployeeMonth> findRpAttendanceEmployeeMonthByParam(Map<String, Object> param) {
		List<RpAttendanceEmployeeMonth> rpAttendanceEmployeeMonths = null;
		try {
			rpAttendanceEmployeeMonths = rpAttendanceEmployeeMonthDao.findRpAttendanceEmployeeMonthByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return rpAttendanceEmployeeMonths;
	}

}