package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.VerifyCode;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.VerifyCodeService;
import com.sdcloud.biz.lar.dao.BaseDao;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.VerifyCodeDao;
import com.sdcloud.framework.entity.LarPager;

/**
 * 
 * @author wrs<br/>
 *         服务城市service
 */
@Service
public class VerifyCodeServiceImpl extends BaseServiceImpl<VerifyCode> implements VerifyCodeService {
	@Autowired
	private VerifyCodeDao verifyCodeDao;

	@Override
	@Transactional
	public VerifyCode createCode(String phone) {
		verifyCodeDao.deleteByPhone(phone);
		int code = (int) ((Math.random() * 9 + 1) * 100000);
		Date createTime = new Date();
		Date endTime = new Date(createTime.getTime() + 1000 * 60 * 10);// 10分钟过期
		VerifyCode verifyCode = new VerifyCode(phone, code, createTime, endTime);
		int count = verifyCodeDao.insert(verifyCode);
		if (count == 0) {
			verifyCode = null;
		}
		return verifyCode;
	}
	@Override
	public VerifyCode findCode(String phone) {
		return verifyCodeDao.selectByPhone(phone);
	}
	public VerifyCodeDao getVerifyCodeDao() {
		return verifyCodeDao;
	}

	public void setVerifyCodeDao(VerifyCodeDao verifyCodeDao) {
		this.verifyCodeDao = verifyCodeDao;
	}

}
