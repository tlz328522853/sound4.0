package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.TakingExpressService;
import com.sdcloud.biz.lar.dao.TakingExpressDao;

@Service
@Transactional(readOnly = true)
public class TakingExpressServiceImpl extends BaseServiceImpl<TakingExpress> implements TakingExpressService{
	@Autowired
	private TakingExpressDao  takingExpressDao;
	
	@Override
	public List<TakingExpress> getByOrderNo(List<String> orderNos) {
		
		return takingExpressDao.getByOrderNo(orderNos);
	}

	@Override
	public List<String> queryByOrderNos(List<String> orderNos) throws Exception {
		// TODO Auto-generated method stub
		return takingExpressDao.queryByOrderNos(orderNos);
	}

}
