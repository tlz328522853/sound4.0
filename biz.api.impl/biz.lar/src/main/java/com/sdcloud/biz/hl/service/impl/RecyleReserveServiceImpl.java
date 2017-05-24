package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.RecyleReserve;
import com.sdcloud.api.hl.service.RecyleReserveService;
import com.sdcloud.biz.hl.dao.RecyleReserveDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_recyle_reserve 上门回收预约
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class RecyleReserveServiceImpl extends BaseServiceImpl<RecyleReserve> implements RecyleReserveService{

	@Autowired
	private RecyleReserveDao recyleReserveDao;
	@Override
	public long countByReserveId(Integer reserveId) {
		// TODO Auto-generated method stub
		return recyleReserveDao.countByReserveId(reserveId);
	}


}
