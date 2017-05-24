package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.biz.lar.dao.VoucherDao;
import com.sdcloud.framework.entity.LarPager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩亚辉 on 2016/4/7.
 */
@Service
public class VoucherServiceImlp extends BaseServiceImpl<Voucher> implements VoucherService {
	@Autowired
	private VoucherDao voucherDao;

	@Override
	@Transactional(readOnly = false)
	public Boolean batchSave(List<Voucher> list) {
		int count = voucherDao.batchSave(list);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据客户ID查找优惠券
	 * 
	 * @param customerId
	 * @return
	 */
	public List<Voucher> findByCus(Long customerId) {
		return voucherDao.findByCus(customerId);

	}
    
	/**
	 * 获取优惠券发放人用户列表
	 */
	@Override
	public List<Long> getRelUsers() throws Exception{
		// TODO Auto-generated method stub
		return voucherDao.getRelUsers();
	}

}
