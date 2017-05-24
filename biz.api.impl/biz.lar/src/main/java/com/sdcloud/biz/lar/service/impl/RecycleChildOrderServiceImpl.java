package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleChildOrder;
import com.sdcloud.api.lar.service.RecycleChildOrderService;
import com.sdcloud.biz.lar.dao.RecycleChildOrderDao;

@Service
@Transactional
public class RecycleChildOrderServiceImpl extends BaseServiceImpl<RecycleChildOrder> implements RecycleChildOrderService{
	
	@Autowired
	private RecycleChildOrderDao recycleChildOrderDao;
	@Override
	public Boolean saveList(List<RecycleChildOrder> orders) {
		
		return recycleChildOrderDao.saveList(orders)>0;
	}
	
}
