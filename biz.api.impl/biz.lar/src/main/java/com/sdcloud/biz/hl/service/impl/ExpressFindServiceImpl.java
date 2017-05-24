package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.ExpressFind;
import com.sdcloud.api.hl.service.ExpressFindService;
import com.sdcloud.biz.hl.dao.ExpressFindDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_express_find 快递查询
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class ExpressFindServiceImpl extends BaseServiceImpl<ExpressFind> implements ExpressFindService{

	@Autowired
	private ExpressFindDao expressFindDao;
	@Override
	public int countByFindId(Integer findId) {
		// TODO Auto-generated method stub
		return expressFindDao.countByFindId(findId);
	}


}
