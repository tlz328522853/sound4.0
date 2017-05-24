package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.PortalInfo;
import com.sdcloud.api.core.service.PortalInfoService;
import com.sdcloud.biz.core.dao.PortalInfoDao;

@Service("portalInfoService")
public class PortalInfoServiceImpl implements PortalInfoService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PortalInfoDao portalInfoDao;

	@Transactional
	public PortalInfo find(Map<String, Object> param) throws Exception {
		logger.info("start method: PortalInfo find(Map<String, Object> param), arg param: " + param);
		PortalInfo portalInfo = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			portalInfo = portalInfoDao.find(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("查询时发生错误，请检查");
		}
		logger.info("complete method, return portalInfo: " + portalInfo);
		return portalInfo;
	}
	
	@Transactional
	public List<PortalInfo> findAll(List<Long> orgIds) throws Exception {
		logger.info("start method: List<PortalInfo> findAll(List<String> orgIds), arg param: " + orgIds);
		List<PortalInfo> result = null;
		try {
			
			result = portalInfoDao.findAll(orgIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new Exception("查询时发生错误，请检查");
		}
		logger.info("complete method, return List<PortalInfo>: " + result);
		return result;
	}

}
