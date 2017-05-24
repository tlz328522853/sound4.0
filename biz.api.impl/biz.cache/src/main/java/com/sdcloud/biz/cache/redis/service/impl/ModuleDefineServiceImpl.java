package com.sdcloud.biz.cache.redis.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.cache.redis.service.ModuleDefineService;
import com.sdcloud.api.cache.util.RedisKeyGenerator;
import com.sdcloud.biz.cache.redis.dao.SetOperationsDao;

/**
 * @author jzc
 * @version 2016年9月18日 下午1:32:20 KeyServiceImpl描述:
 */
@Service(value = "moduleDefineService")
public class ModuleDefineServiceImpl implements ModuleDefineService {

	@Autowired
	private SetOperationsDao setOperationsDao;
	@Override
	public int addModuleCode(String code) {
		try {
			Long num = setOperationsDao.add(RedisKeyGenerator.MODULE_CODE, code);
			return Integer.parseInt(num.toString());
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public boolean deleteModuleCode(String code) {
		Long num = setOperationsDao.remove(RedisKeyGenerator.MODULE_CODE, code);
		if (num > 0) {
			return true;
		}
		return false;
	}
	@Override
	public boolean validateModuleCode(String code) {
		return setOperationsDao.isMember(RedisKeyGenerator.MODULE_CODE, code);
	}

	@Override
	public Set<String> queryModuleCodes() {
		return setOperationsDao.members(RedisKeyGenerator.MODULE_CODE);
	}

}
