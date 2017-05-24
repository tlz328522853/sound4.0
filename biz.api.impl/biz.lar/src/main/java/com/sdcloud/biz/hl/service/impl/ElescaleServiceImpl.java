package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.Elescale;
import com.sdcloud.api.hl.service.ElescaleService;
import com.sdcloud.biz.hl.dao.ElescaleDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_elescale 电子秤信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class ElescaleServiceImpl extends BaseServiceImpl<Elescale> implements ElescaleService{

	@Autowired
	private ElescaleDao elescaleDao;
	
	@Override
	public int countByElescaleId(String mchid) {
		
		return elescaleDao.countByElescaleId(mchid);
	}

	@Override
	public Elescale selectByElescaleId(String mchid) {
		// TODO Auto-generated method stub
		return elescaleDao.selectByElescaleId(mchid);
	}


}
