package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.ExpressApply;
import com.sdcloud.api.hl.service.ExpressApplyService;
import com.sdcloud.biz.hl.dao.ExpressApplyDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_express_apply 快递申请记录
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class ExpressApplyServiceImpl extends BaseServiceImpl<ExpressApply> implements ExpressApplyService{

	@Autowired
	private ExpressApplyDao expressApplyDao;
	
	@Override
	public int countByApplyId(Integer applyId) {
		
		return expressApplyDao.countByApplyId(applyId);
	}


}
