package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.ReportEntity;
import com.sdcloud.api.envsanitation.service.AnalysisReportService;
import com.sdcloud.biz.envsanitation.dao.AnalysisReportDao;

public class AnalysisReportServiceImpl implements AnalysisReportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AnalysisReportDao reportDao;

	@Transactional(readOnly = true)
	@Override
	public List<ReportEntity> getEmployeeGenderReport(List<Long> orgIds) {
		logger.info("start method List<ReportEntity> getEmployeeGenderReport()");
		List<ReportEntity> result = null;
		try {
			result = reportDao.getEmployeeGenderReport(orgIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method getEmployeeGenderReport");
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ReportEntity> getEmployeeAgeReport(List<Long> orgIds) {
		logger.info("start method List<ReportEntity> getEmployeeAgeReport()");
		List<ReportEntity> result = null;
		try {
			List<ReportEntity> list = reportDao.getEmployeeAgeReport(orgIds);
			if (list != null && list.size() > 0) {
				result = new ArrayList<>();
				for (ReportEntity entity : list) {
					entity.setName(entity.getName() + "0~" + entity.getName() + "9岁");
					result.add(entity);
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method getEmployeeAgeReport");
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ReportEntity> getFacilityCountReport(List<Long> orgIds) {
		logger.info("start method List<ReportEntity> getFacilityCountReport()");
		List<ReportEntity> result = null;
		try {
			result = reportDao.getFacilityCountReport(orgIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method getFacilityCountReport");
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ReportEntity> getCarCountReport(List<Long> orgIds) {
		logger.info("start method List<ReportEntity> getCarCountReport()");
		List<ReportEntity> result = null;
		try {
			result = reportDao.getCarCountReport(orgIds);
			if (result != null && result.size() > 0) {
				for (ReportEntity reportEntity : result) {
					String name = reportEntity.getName();
					if (name == null || name.trim().equals("")) {
						name = "未指定类型";
						reportEntity.setName(name);
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method getCarCountReport");
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ReportEntity> getCarCostReport(List<Long> orgIds) {
		logger.info("start method List<ReportEntity> getCarCostReport()");
		List<ReportEntity> result = null;
		try {
			result = reportDao.getCarCountReport(orgIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method getCarCostReport");
		return result;
	}
}
