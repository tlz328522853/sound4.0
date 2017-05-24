package com.sdcloud.biz.lar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.IntegralSetting;
import com.sdcloud.api.lar.service.IntegralSettingService;
import com.sdcloud.biz.lar.dao.IntegralSettingDao;
import com.sdcloud.framework.entity.ResultDTO;

@Service
@Transactional
public class IntegralSettingServiceImpl extends BaseServiceImpl<IntegralSetting> implements IntegralSettingService{

	
	@Autowired
	private IntegralSettingDao integralSettingDao;
	
	@Override
	/**
	 * 查询积分等级
	 */
	public IntegralSetting getByLevel(Integer level) {
		IntegralSetting byLevel = null;
		try {
			byLevel = integralSettingDao.getByLevel(level);
		} catch (Exception e) {
			throw e;
		}
		
		return byLevel;
	}

	
}
