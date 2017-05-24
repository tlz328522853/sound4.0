package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.LjRecycler;
import com.sdcloud.api.hl.service.LjRecyclerService;
import com.sdcloud.biz.hl.dao.LjRecyclerDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_lj_recycler 浪尖回收机
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class LjRecyclerServiceImpl extends BaseServiceImpl<LjRecycler> implements LjRecyclerService{

	@Autowired
	private LjRecyclerDao ljRecyclerDao;
	
	@Override
	public int countByMchid(String mchid) {
		// TODO Auto-generated method stub
		return ljRecyclerDao.countByMchid(mchid);
	}

	@Override
	public LjRecycler selectByMchid(String mchid) {
		// TODO Auto-generated method stub
		return ljRecyclerDao.selectByMchid(mchid);
	}


}
