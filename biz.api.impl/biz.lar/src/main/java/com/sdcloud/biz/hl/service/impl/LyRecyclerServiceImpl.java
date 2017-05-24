package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.LyRecycler;
import com.sdcloud.api.hl.service.LyRecyclerService;
import com.sdcloud.biz.hl.dao.LyRecyclerDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_ly_recycler 联运回收机信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class LyRecyclerServiceImpl extends BaseServiceImpl<LyRecycler> implements LyRecyclerService{

	@Autowired
	private LyRecyclerDao lyRecyclerDao;
	
	@Override
	public long countByMchid(String mchid) {
		// TODO Auto-generated method stub
		return lyRecyclerDao.countByMchid(mchid);
	}

	@Override
	public LyRecycler selectByMchid(String mchid) {
		// TODO Auto-generated method stub
		return lyRecyclerDao.selectByMchid(mchid);
	}


}
