package com.sdcloud.biz.envsanitation.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sdcloud.api.envsanitation.entity.OrgAssetCount;
import com.sdcloud.api.envsanitation.service.BizCompanyService;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;

public class BizCompanyServiceImpl implements BizCompanyService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AssetObjectDao assetObjectDao;
	
	@Override
	public List<OrgAssetCount> findOrgAssetCount() {
			logger.info("start method: T findOrgAssetCount()");
			List<OrgAssetCount> result = null;
			result = assetObjectDao.findOrgAssetCount();
			logger.info("complete method, return t: " + result);
			return result;
	}

}
