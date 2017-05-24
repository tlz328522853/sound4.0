package com.sdcloud.biz.lar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.SysConfig;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.biz.lar.dao.SysConfigDao;

/**
 * 
 * @author luorongjie
 *
 */


@Service
@Transactional(readOnly = true)
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig> implements SysConfigService{

	@Autowired
	private SysConfigDao sysConfigDao;

	@Override
	public Map<String, String> findMap() {
		
		Map<String, String> result = new HashMap<>();
		List<SysConfig> list = sysConfigDao.findList();
		for (SysConfig sysConfig:list) {
			result.put(sysConfig.getType(), sysConfig.getValue());
		}
		
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateMap(Map<String, String> sysConfigMap) {

		if(null != sysConfigMap){
			Set<Entry<String, String>> entrySet = sysConfigMap.entrySet();
			for(Entry<String, String> entry:entrySet){
				SysConfig sysConfig = new SysConfig(entry.getKey(), entry.getValue());
				sysConfigDao.update(sysConfig);
			}
		}
		return true;
		
	}
}
