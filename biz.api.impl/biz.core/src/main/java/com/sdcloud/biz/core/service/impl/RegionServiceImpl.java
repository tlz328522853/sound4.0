package com.sdcloud.biz.core.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Region;
import com.sdcloud.api.core.service.RegionService;
import com.sdcloud.biz.core.dao.RegionDao;

@Service("regionService")
public class RegionServiceImpl implements RegionService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RegionDao regionDao;

	@Override
	@Transactional(readOnly = true)
	public List<Region> getRegions(Integer id) throws Exception {
		if (id != null && id > 0) {
			try {
				logger.info("Enter the :{} method  id:{}", Thread
						.currentThread().getStackTrace()[1].getMethodName(), id);

				List<Region> larRegions = regionDao.getLarRegions(id);
				return larRegions;
			} catch (Exception e) {
				logger.error("method {} execute error, id:{} Exception:{}",
						Thread.currentThread().getStackTrace()[1]
								.getMethodName(), id, e);
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Region> findAll() {
		try {
			logger.info("Enter the :{} method  ", Thread.currentThread()
					.getStackTrace()[1].getMethodName());

			List<Region> larRegions = regionDao.findAll();
			return larRegions;
		} catch (Exception e) {
			logger.error("method {} execute error,Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			throw e;
		}

	}
}
