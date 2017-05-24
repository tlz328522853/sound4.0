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

import com.sdcloud.api.envsanitation.entity.RpProjectContractInfo;
import com.sdcloud.api.envsanitation.service.RpProjectContractInfoService;
import com.sdcloud.biz.envsanitation.dao.RpProjectContractInfoDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpProjectContractInfoService")
public class RpProjectContractInfoServiceImpl implements RpProjectContractInfoService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpProjectContractInfoDao rpProjectContractInfoDao;

	@Transactional
	public List<RpProjectContractInfo> insert(List<RpProjectContractInfo> projectContractInfos) throws Exception {
		logger.info("start method: insert, arg projectContractInfos: " + projectContractInfos);
		if (projectContractInfos == null || projectContractInfos.size() == 0) {
			logger.warn("projectContractInfos is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpProjectContractInfo rpProjectContractInfo : projectContractInfos) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpProjectContractInfo.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpProjectContractInfoDao.insert(projectContractInfos);
					break;
				} catch (Exception se) {
					logger.error("err method, Exception: " + se);
					if (tryTime == 0)
						throw new RuntimeException("向数据库插入记录失败，请重试");
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

		logger.info("complete method, return ts: " + projectContractInfos);
		return projectContractInfos;
	}

	@Transactional
	public Pager<RpProjectContractInfo> findBy(Pager<RpProjectContractInfo> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpProjectContractInfo> findBy(Pager<RpProjectContractInfo> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpProjectContractInfo>();
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
				long totalCount = rpProjectContractInfoDao.getTotalCount(param);
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
			List<RpProjectContractInfo> projectContractInfos = rpProjectContractInfoDao.findAllBy(map);
			pager.setResult(projectContractInfos);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpProjectContractInfo projectContractInfo) throws Exception {
		logger.info("start method: void update(RpProjectContractInfo projectContractInfo), arg projectContractInfo: "
				+ projectContractInfo);
		if (projectContractInfo == null || projectContractInfo.getId() == null) {
			logger.warn("arg projectContractInfo is null or projectContractInfo 's id is null");
			throw new IllegalArgumentException("arg projectContractInfo is null or projectContractInfo 's id is null");
		}
		try {
			rpProjectContractInfoDao.update(projectContractInfo);
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
			rpProjectContractInfoDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Transactional
	public void file(RpProjectContractInfo projectContractInfo) throws Exception {
		logger.info("start method: void file(RpProjectContractInfo projectContractInfo), arg projectContractInfo: "
				+ projectContractInfo);
		if (projectContractInfo == null || projectContractInfo.getId() == null) {
			logger.warn("arg projectContractInfo is null or projectContractInfo 's id is null");
			throw new IllegalArgumentException("arg projectContractInfo is null or projectContractInfo 's id is null");
		}
		try {
			rpProjectContractInfoDao.file(projectContractInfo);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

}