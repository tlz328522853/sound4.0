package com.sdcloud.biz.lar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.AdPosition;
import com.sdcloud.api.lar.service.AdPositionService;
import com.sdcloud.biz.lar.dao.AdPositionDao;

@Service
public class AdPositionServiceImpl extends BaseServiceImpl<AdPosition> implements AdPositionService{

	@Autowired
	private AdPositionDao adPositionDao;

	@Override
	public Map<Long, AdPosition> findMapByIds(List<Long> ids) {
		Map<Long, AdPosition> map = new HashMap<>();
		List<AdPosition> list = adPositionDao.findByIds(ids);
		for (AdPosition adPosition : list) {
			map.put(adPosition.getId(), adPosition);
		}
		return map;
	}
}
