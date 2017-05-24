package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.AppVersion;
import com.sdcloud.api.envsanitation.service.AppVersionService;
import com.sdcloud.biz.envsanitation.dao.AppVersionDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("appVersionService")
public class AppVersionServiceImpl implements AppVersionService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AppVersionDao appVersionDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<AppVersion> appVersions) throws Exception {
		logger.info("start method: long insert(AppVersion AppVersion), arg AppVersion: "
				+ appVersions);

		if (appVersions==null||appVersions.size()<=0) {
			logger.warn("arg AppVersion is null");
			throw new IllegalArgumentException("arg AppVersion is null");
		}
		for (AppVersion appVersion : appVersions) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			appVersion.setVersionId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					appVersionDao.insert(appVersions);
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
			logger.error("err method, Exception: " + e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> appVersions) throws Exception {
		logger.info("start method: void delete(List<Long> AppVersions), arg AppVersions: "
				+ appVersions);
		if (appVersions == null || appVersions.size() == 0) {
			logger.warn("arg AppVersions is null or size=0");
			throw new IllegalArgumentException("arg AppVersions is null or size=0");
		}
		try {
			
			appVersionDao.delete(appVersions);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(AppVersion appVersion) throws Exception {
		logger.info("start method: void update(AppVersion AppVersion), arg AppVersion: "
				+ appVersion);
		if (appVersion == null || appVersion.getVersionId() == null) {
			logger.warn("arg AppVersion is null or AppVersion 's VersionId is null");
			throw new IllegalArgumentException("arg AppVersion is null or AppVersion 's addOidId is null");
		}
		try {
			
			appVersionDao.update(appVersion);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public AppVersion findById(Long appVersionId) throws Exception {
		logger.info("start method: AppVersion findById(Long appVersionId), arg appVersionId: "
				+ appVersionId);
		AppVersion appversion;
		try {
			
			appversion = appVersionDao.findById(appVersionId);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method");
		return appversion;
	}
	
	@Transactional
	public Pager<AppVersion> findBy(Pager<AppVersion> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<AppVersion> findBy(Pager<AppVersion> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<AppVersion>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("versionId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = appVersionDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<AppVersion> AppVersions = appVersionDao.findAllBy(param);

			pager.setResult(AppVersions);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
