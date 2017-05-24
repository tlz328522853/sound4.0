package com.sdcloud.biz.lar.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.AdCustomer;
import com.sdcloud.api.lar.service.AdCustomerService;
import com.sdcloud.biz.lar.dao.AdCustomeDao;

@Service
public class AdCustomerServiceImpl extends BaseServiceImpl<AdCustomer> implements AdCustomerService{

	@Autowired
	private AdCustomeDao adCustomeDao;

	@Override
	public Map<Long, AdCustomer> findMapByIds(List<Long> ids) {
		Map<Long, AdCustomer> map = new HashMap<>();
		List<AdCustomer> list = adCustomeDao.findByIds(ids);
		for (AdCustomer adCustomer : list) {
			map.put(adCustomer.getId(), adCustomer);
		}
		return map;
	}
}
