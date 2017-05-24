package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.Hypay;
import com.sdcloud.api.hl.service.HypayService;
import com.sdcloud.biz.hl.dao.HypayDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_hypay 
 * @author jiazc
 * @date 2017-05-15
 * @version 1.0
 */
@Service
public class HypayServiceImpl extends BaseServiceImpl<Hypay> implements HypayService{

	@Autowired
	private HypayDao hypayDao;
	@Override
	public int countByPayId(int payId) {
		// TODO Auto-generated method stub
		return hypayDao.countByPayId(payId);
	}


}
