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

import com.sdcloud.api.envsanitation.entity.RpCarInsureInfo;
import com.sdcloud.api.envsanitation.service.RpCarInsureInfoService;
import com.sdcloud.biz.envsanitation.dao.RpCarInsureInfoDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpCarInsureInfoService")
public class RpCarInsureInfoServiceImpl implements RpCarInsureInfoService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpCarInsureInfoDao rpCarInsureInfoDao;

	@Transactional
	public List<RpCarInsureInfo> insert(List<RpCarInsureInfo> carInsureInfos) throws Exception {
		logger.info("start method: insert, arg carInsureInfos: " + carInsureInfos);
		if (carInsureInfos == null || carInsureInfos.size() == 0) {
			logger.warn("carInsureInfos is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpCarInsureInfo rpCarInsureInfo : carInsureInfos) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpCarInsureInfo.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpCarInsureInfoDao.insert(carInsureInfos);
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

		logger.info("complete method, return ts: " + carInsureInfos);
		return carInsureInfos;
	}

	@Transactional
	public Pager<RpCarInsureInfo> findBy(Pager<RpCarInsureInfo> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpCarInsureInfo> findBy(Pager<RpCarInsureInfo> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpCarInsureInfo>();
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
				long totalCount = rpCarInsureInfoDao.getTotalCount(param);
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
			List<RpCarInsureInfo> carInsureInfos = rpCarInsureInfoDao.findAllBy(map);
			pager.setResult(carInsureInfos);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpCarInsureInfo carInsureInfo) throws Exception {
		logger.info("start method: void update(RpCarInsureInfo carInsureInfo), arg carInsureInfo: "
				+ carInsureInfo);
		if (carInsureInfo == null || carInsureInfo.getId() == null) {
			logger.warn("arg carInsureInfo is null or carInsureInfo 's id is null");
			throw new IllegalArgumentException("arg carInsureInfo is null or carInsureInfo 's id is null");
		}
		try {
			rpCarInsureInfoDao.update(carInsureInfo);
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
			rpCarInsureInfoDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

}