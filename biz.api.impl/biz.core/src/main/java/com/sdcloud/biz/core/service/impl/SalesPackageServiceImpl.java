package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sdcloud.api.core.entity.Function;
import com.sdcloud.api.core.entity.FunctionRight;
import com.sdcloud.api.core.entity.SalesPackageModule;
import com.sdcloud.api.core.entity.SalesPackage;
import com.sdcloud.api.core.entity.Tenant;
import com.sdcloud.api.core.entity.UserGroup;
import com.sdcloud.api.core.service.SalesPackageService;
import com.sdcloud.biz.core.dao.FunctionDao;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.biz.core.dao.GroupRoleDao;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.biz.core.dao.ModuleDao;
import com.sdcloud.biz.core.dao.SalesPackageDao;
import com.sdcloud.biz.core.dao.SalesPackageModuleDao;
import com.sdcloud.biz.core.dao.TenantDao;
import com.sdcloud.biz.core.dao.UserGroupDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lms
 */
@Service("packageService")
public class SalesPackageServiceImpl implements SalesPackageService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SalesPackageDao salesPackageDao;
	
	@Autowired
	private SalesPackageModuleDao salesPackageModuleDao;
	
	@Autowired
	TenantDao tenantDao;
	@Autowired
	UserGroupDao userGroupDao;
	@Autowired
	private ModuleDao moduleDao;
	@Autowired
	private GroupUserDao groupUserDao;
	@Autowired
	private FunctionRightDao functionRightDao;
	@Autowired
	private FunctionDao functionDao;
	@Autowired
	private GroupRoleDao groupRoleDao;
	@Transactional
	public long insert(SalesPackage salesPackage) throws Exception {
		logger.info("start method: long insert(SalesPackage salesPackage), arg salesPackage: " + salesPackage);
		if(salesPackage == null){
			logger.warn("arg salesPackage is null");
			throw new IllegalArgumentException("arg salesPackage is null");
		}
		
		long id = -1;
		try {
						
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new moduleId:" + id);
				salesPackage.setSalesPackageId(id);
				try {
					salesPackageDao.insert(salesPackage);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key moudle:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return id;
	}

	@Transactional
	public void setSalesPackageModule(List<SalesPackageModule> salesPackageModules, Long salesPackageId) throws Exception {
		logger.info("start method: void setSalesPackageModule(List<SalesPackageModule> salesPackageModules, Long salesPackageId), arg salesPackageModules: " + 
					salesPackageModules + ", arg salesPackageId: " + salesPackageId);
		
		if(salesPackageId == null){
			logger.warn("arg salesPackageId is null");
			throw new IllegalArgumentException("arg salesPackageId is null");
		}
		if(salesPackageModules == null || salesPackageModules.size() == 0){
			logger.warn("arg salesPackageModules is null or size=0");
			throw new IllegalArgumentException("arg salesPackageModules is null or size=0");
		}
		if(salesPackageModules.get(0).getSalesPackageId() != salesPackageId){
			logger.warn("arg salesPackageModules.get(0) 's salesPackageId is illegal");
			throw new IllegalArgumentException("arg salesPackageModules.get(0) 's salesPackageId is illegal");
		}
		
		try {
			// 先删除关系
			salesPackageModuleDao.deleteBySalesPackageId(salesPackageId);
			
			// 再设置销售包与模块的关联关系
			salesPackageModuleDao.insert(salesPackageModules);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(SalesPackage salesPackage) throws Exception {
		logger.info("start method: void update(SalesPackage salesPackage), arg salesPackage: " + salesPackage);
		if(salesPackage == null || salesPackage.getSalesPackageId() == null){
			logger.warn("arg salesPackage is null or salesPackage 's salesPackageId is null");
			throw new IllegalArgumentException("arg salesPackage is null or salesPackage 's salesPackageId is null");
		}
		
		try {
			// 仅是更新销售包基本信息，若须更新销售包关联的模块须调用setSalesPackageModule方法
			salesPackageDao.update(salesPackage);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void delete(Long salesPackageId) throws Exception {
		logger.info("start method: void delete(Long salesPackageId), arg salesPackageId: " + salesPackageId);
		if(salesPackageId == null){
			logger.warn("arg salesPackageId is null");
			throw new IllegalArgumentException("arg salesPackageId is null");
		}
		
		try {
			// 删除销售包自身
			salesPackageDao.delete(salesPackageId);
			
			// 删除销售包关联的模块ID
			salesPackageModuleDao.deleteBySalesPackageId(salesPackageId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public SalesPackage findById(Long salesPackageId) throws Exception {
		logger.info("start method: SalesPackage findById(Long salesPackageId), arg salesPackageId: " + salesPackageId);
		if(salesPackageId == null){
			logger.warn("arg salesPackageId is null");
			throw new IllegalArgumentException("arg salesPackageId is null");
		}
		SalesPackage salesPackage = null;
		try {
			salesPackage = salesPackageDao.findById(salesPackageId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return salesPackage: " + salesPackage);
		return salesPackage;
	}
	
	@Transactional
	public List<SalesPackage> findAll() throws Exception {
		logger.info("start method: List<SalesPackage> findAll()" );
		List<SalesPackage> packages = new ArrayList<SalesPackage>();
		try {
			packages = salesPackageDao.findAll();
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return packages: " + packages);
		return packages;
	}

	@Override
	public List<Long> findSalesByModule(Long moduleId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),moduleId);
		List<Long> salesPackage=null;
		try {
			salesPackage = salesPackageModuleDao.findSalesPackageIdByModelId(moduleId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),moduleId,e);
			throw e;
		}
		return salesPackage;
	}

	@Transactional
	public void uninstallModule(Long salesPackageId, List<Long> moduleIds)
			throws Exception {
		logger.info("Enter the :{} method salesPackageId:{} moduleIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),salesPackageId,moduleIds);
		try {
			salesPackageModuleDao.deleteSalesPackageIdAndModelId(salesPackageId, moduleIds);;
			
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),moduleIds,e);
			throw e;
		}
		
	
	
		
	}
	private void getGroupChild(List<Long> groupIds,List<Long> parentIds){
		parentIds=userGroupDao.findChildren(parentIds);
		if(parentIds!=null&&parentIds.size()>0){
			groupIds.addAll(parentIds);
			getGroupChild(groupIds,parentIds);
		}
	}
	@Override
	public void installModule(Long salesPackageId, List<Long> moduleIds)
			throws Exception {
		logger.info("Enter the :{} method salesPackageId:{} moduleIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),salesPackageId,moduleIds);
		try {
			List<SalesPackageModule> salesPackageModules=new ArrayList<SalesPackageModule>();
			for (Long moduleId : moduleIds) {
				SalesPackageModule salesPackageModule=new SalesPackageModule();
				salesPackageModule.setSalesPackageModuleId(UUIDUtil.getUUNum());
				salesPackageModule.setModuleId(moduleId);
				salesPackageModule.setSalesPackageId(salesPackageId);
				salesPackageModule.setCreatTime(new Date());
				salesPackageModules.add(salesPackageModule);
				
			}
			salesPackageModuleDao.insert(salesPackageModules);
			
		} catch (Exception e) {
			logger.error("method {} execute error,salesPackageId:{} moduleIds:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),salesPackageId,moduleIds,e);
			throw e;
		}
		
		
	}
	private  List<Long> insertFunctionRight(List<FunctionRight> functionRights) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),functionRights);
		if(functionRights == null || functionRights.size() == 0){
			logger.warn("arg functionRights is null or size = 0");
			throw new IllegalArgumentException("arg functionRights is null or size = 0");
		}
		List<Long> ids=new ArrayList<Long>();
		
		//为对象的funcRightId属性赋值
		for (int i = 0; i < functionRights.size(); i++) {
			FunctionRight right = functionRights.get(i);
			long id = -1;
			id = UUIDUtil.getUUNum();
			logger.info("create new functionRightId:" + id);
			right.setFuncRightId(id);
			ids.add(id);
			
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					functionRightDao.insert(functionRights);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key funcRightId:");
//						continue;
					}
				}
			}
		} catch (Exception e) {
			ids.clear();
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),functionRights,e);
			throw e;
		}
		logger.info("complete method, return void");
		return ids;
	}

	@Override
	public Long findUserSalesPackage(Long userId) throws Exception {
		try {
			logger.info("Enter the :{} method  userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId);
			return this.salesPackageDao.findUserSalesPackage(userId);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId, e);
			throw e;
		}
	}
}
