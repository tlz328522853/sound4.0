package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.service.LarRegionService;
import com.sdcloud.biz.lar.dao.LarRegionDao;

@Service
public class LarRegionServiceImpl implements LarRegionService {

	@Autowired
	private LarRegionDao larRegionDao;

	@Override
	@Transactional(readOnly = true)
	public List<LarRegion> getLarRegions(Integer id) throws Exception {
		if (id != null && id > 0) {
			try {
				List<LarRegion> larRegions = larRegionDao.getLarRegions(id);
				return larRegions;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<LarRegion> findAll() {
		try {
			List<LarRegion> larRegions = larRegionDao.findAll();
			return larRegions;
		} catch (Exception e) {
			throw e;
		}
	}
}
