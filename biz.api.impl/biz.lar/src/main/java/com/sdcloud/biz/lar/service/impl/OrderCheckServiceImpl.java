package com.sdcloud.biz.lar.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.OrderCheck;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.service.OrderCheckService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.biz.lar.dao.OrderCheckDao;
import com.sdcloud.framework.common.UUIDUtil;

@Service
public class OrderCheckServiceImpl extends BaseServiceImpl<OrderCheck> implements OrderCheckService{

	@Autowired
	private OrderCheckDao orderCheckDao;
	@Autowired
	private OrderManagerService orderManagerService;
	
	@Override
	@Transactional(readOnly=false)
	public Boolean save(OrderCheck orderCheck, OrderManager orderManager){
		int updateCheckStatus = orderManagerService.updateCheckStatus(orderManager);
		if(updateCheckStatus>0){
			Long uuid = UUIDUtil.getUUNum();
	        Date date = new Date();
	        if (orderCheck.getId() == null) {
	        	orderCheck.setId(uuid);
	        }
	        if (orderCheck.getCreateUser() == null) {
	        	orderCheck.setCreateUser(uuid);
	        }
	        orderCheck.setCreateDate(date);
	        int count = orderCheckDao.save(orderCheck);
	        if (count > 0) {
	            return true;
	        } else {
	            return false;
	        }
		}else{
			return false;
		}
	}
}
