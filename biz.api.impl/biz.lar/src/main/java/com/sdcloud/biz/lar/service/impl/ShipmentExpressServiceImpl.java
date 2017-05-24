package com.sdcloud.biz.lar.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.ShipmentExpress;
import com.sdcloud.api.lar.service.ShipmentExpressService;
import com.sdcloud.biz.lar.dao.ShipmentExpressDao;

@Service
public class ShipmentExpressServiceImpl extends BaseServiceImpl<ShipmentExpress> implements ShipmentExpressService{
	
	@Autowired
	private ShipmentExpressDao  shipmentExpressDao;
	@Override
	public Long getCountByExpressAndOrgId(Long id,Long express, Long org) {
		
		return shipmentExpressDao.getCountByExpressAndOrgId(id,express,org);
	}

}
