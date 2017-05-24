package com.sdcloud.biz.core.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;



import com.sdcloud.api.core.entity.Tenant;
import com.sdcloud.api.core.service.TenantService;
import com.sdcloud.biz.core.dao.TenantDao;
import com.sdcloud.framework.common.UUIDUtil;

@Service("tenantService")
public class TenantServiceImpl implements TenantService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	@Autowired
	private TenantDao tenantDao;

	public long insert(Tenant tenant) throws Exception {
		
		logger.info("start insert tenant: " + tenant);
		
		if(tenant == null){
			logger.warn("tenant is null");
			throw new IllegalArgumentException("tenant is null");
		}
		long tenantId = -1;
		try {
			tenantId = UUIDUtil.getUUNum();
			if(StringUtils.isEmpty(tenant.getTenantId())){
				tenant.setTenantId(tenantId);
			}
			
			logger.info("create uuid for tenant, tenantId: " + tenantId);
			tenantDao.insert(tenant);
		} catch (Exception e) {
			logger.error("err when insert tenant: " + tenant,e);
			throw e;
		}
		
		logger.info("complete insert tenant: " + tenant);
		return tenantId;
	}

	public void update(Tenant tenant) throws Exception {
		logger.info("");
		
		if(tenant == null){
			logger.warn("tenant is null");
			throw new IllegalArgumentException("tenant is null");
		}
		
		try {
			tenantDao.update(tenant);
		} catch (Exception e) {
			logger.error("err when update tenant: "+ tenant,e);
			throw e;
		}
		
		logger.info("");
		
	}

	@Override
	public List<Tenant> findTenantByParam(Map<String, Object> param)
			throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
		try {
			return tenantDao.findTenantByParam(param);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
	}

	@Override
	public void delete(Long tenantId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),tenantId);
		try {
			tenantDao.delete(tenantId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),tenantId,e);
			throw e;
		}
		
	}

	@Override
	public void upgradePackag(Long tenantId, Long packageId) throws Exception {
		try {
			logger.info("Enter the :{} method  tenantId:{} packageId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),tenantId,packageId);
			tenantDao.upgradePackag(tenantId, packageId);
		} catch (Exception e) {
			logger.error("method {} execute error,  tenantId:{} packageId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), tenantId,packageId,e);
			throw e;
		}
	}

}
