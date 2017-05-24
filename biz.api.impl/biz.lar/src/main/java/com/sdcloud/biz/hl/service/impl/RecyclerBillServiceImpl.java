package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.RecyclerBill;
import com.sdcloud.api.hl.service.RecyclerBillService;
import com.sdcloud.biz.hl.dao.RecyclerBillDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_recycler_bill 回收机用户保修单
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class RecyclerBillServiceImpl extends BaseServiceImpl<RecyclerBill> implements RecyclerBillService{

	@Autowired
	private RecyclerBillDao recyclerBillDao;
	
	@Override
	public long countByBillId(Integer billId) {
		// TODO Auto-generated method stub
		return recyclerBillDao.countByBillId(billId);
	}


}
