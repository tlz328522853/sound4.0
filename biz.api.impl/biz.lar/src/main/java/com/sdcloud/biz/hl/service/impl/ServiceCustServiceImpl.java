package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.ServiceCust;
import com.sdcloud.api.hl.service.ServiceCustService;
import com.sdcloud.biz.hl.dao.ServiceCustDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_service_cust 便民服务表
 * @author jiazc
 * @date 2017-05-16
 * @version 1.0
 */
@Service
public class ServiceCustServiceImpl extends BaseServiceImpl<ServiceCust> implements ServiceCustService{

	@Autowired
	private ServiceCustDao serviceCustDao;
	
	@Override
	public int countByScId(Integer scId) {
		
		return serviceCustDao.countByScId(scId);
	}

	@Override
	public ServiceCust selectByScId(Integer scId) {
		// TODO Auto-generated method stub
		return serviceCustDao.selectByScId(scId);
	}


}
