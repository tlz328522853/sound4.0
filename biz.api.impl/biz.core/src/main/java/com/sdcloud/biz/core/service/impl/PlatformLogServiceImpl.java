package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.PlatformLog;
import com.sdcloud.api.core.service.PlatformLogService;
import com.sdcloud.biz.core.dao.PlatformLogDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("platformLogService")
public class PlatformLogServiceImpl implements PlatformLogService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PlatformLogDao platformLogDao;
	
	
	@Async
	@Transactional
	public void insert(PlatformLog platformLog) throws Exception {
		logger.info("start method: long insert(PlatformLog PlatformLog), arg PlatformLog: "
				+ platformLog);

		if (platformLog==null) {
			logger.warn("arg PlatformLog is null");
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					platformLogDao.insert(platformLog);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),platformLog,e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> platformLogs) throws Exception {
		logger.info("start method: void delete(List<Long> PlatformLogs), arg PlatformLogs: "
				+ platformLogs);
		if (platformLogs == null || platformLogs.size() == 0) {
			logger.warn("arg PlatformLogs is null or size=0");
			throw new IllegalArgumentException("arg PlatformLogs is null or size=0");
		}
		try {
			
			platformLogDao.delete(platformLogs);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(PlatformLog platformLog) throws Exception {
		logger.info("start method: void update(PlatformLog PlatformLog), arg PlatformLog: "
				+ platformLog);
		if (platformLog == null || platformLog.getId()== null) {
			logger.warn("arg PlatformLog is null or PlatformLog 's platformLogId is null");
			throw new IllegalArgumentException("arg PlatformLog is null or PlatformLog 's addOidId is null");
		}
		try {
			
			platformLogDao.update(platformLog);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<PlatformLog> findBy(Pager<PlatformLog> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<PlatformLog> findBy(Pager<PlatformLog> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<PlatformLog>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("assetId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = platformLogDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<PlatformLog> PlatformLogs = platformLogDao.findAllBy(param);

			pager.setResult(PlatformLogs);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
