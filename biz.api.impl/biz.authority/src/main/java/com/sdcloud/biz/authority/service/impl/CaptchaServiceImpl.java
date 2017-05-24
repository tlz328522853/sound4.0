package com.sdcloud.biz.authority.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.authority.service.CaptchaService;
import com.sdcloud.biz.authority.dao.CaptchaDao;

/**
 * 
 * @author lms
 */
@Service("captchaService")
public class CaptchaServiceImpl implements CaptchaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CaptchaDao captchaDao;

	@Override
	public void add(String key, String value) throws Exception {

		try {
			captchaDao.add(key, value);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
	}

	@Override
	public String get(String key) throws Exception {
		String value = null;
		try {
			value = captchaDao.get(key);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return value;
	}

	@Override
	public void remove(String key) throws Exception {

		try {
			captchaDao.remove(key);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
	}

}
