package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.FunctionRight;
import com.sdcloud.api.core.service.FunctionRightService;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lms
 */
@Service("rightService")
public class FunctionRightServiceImpl implements FunctionRightService{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FunctionRightDao functionRightDao;

	@Transactional
	public List<Long> insert(List<FunctionRight> functionRights) throws Exception {
		logger.info("start method: void insert(List<FunctionRight> functionRights), arg functionRights: " + functionRights);
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
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
		return ids;
	}
	@Transactional
	public void deleteByOwnerId(Long ownerId) throws Exception {
		logger.info("start method: void deleteByOwnerId(Long ownerId), arg ownerId: " + ownerId);
		if(ownerId == null){
			logger.warn("arg ownerId is null");
			throw new IllegalArgumentException("arg ownerId is null");
		}
		try {
			functionRightDao.deleteByOwnerId(ownerId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(List<FunctionRight> functionRights, Long ownerId,List<Long> roleIds) throws Exception{
		logger.info("start method: void update(List<FunctionRight> functionRights, Long ownerId), arg functionRights: " + 
					functionRights + ", arg ownerId: " + ownerId);
		
		// 先删除该ownerId的权限
		deleteByOwnerId(ownerId);
		//删除管理员角色权限
		if(roleIds!=null&&roleIds.size()>0){
			for (Long roleId : roleIds) {
				deleteByOwnerId(roleId);
			}
		}
		
		// 再添加新的权限
		insert(functionRights);
		
		logger.info("complete method, return void");
	}
	@Override
	public List<FunctionRight> findFunctionRightByParam(
			Map<String, Object> param) throws Exception {
		List<FunctionRight> frts=new ArrayList<FunctionRight>();
		try {
			frts=functionRightDao.findFunctionRight(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return frts;
	}
	@Override
	public FunctionRight hasFunctionRight(Map<String, Object> param) {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
		FunctionRight functionRight=null;
		try {
			
			List<FunctionRight> frs= functionRightDao.hasFunctionRight(param);
			if(frs!=null&&frs.size()>0){
				functionRight=frs.get(0);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
		return functionRight;
	}
	@Override
	public List<Long> needUpFunctionByPackage(Long packageId, Long userId)
			throws Exception {
		List<Long> functionIds=new ArrayList<Long>();
        try {
			logger.info("Enter the :{} method  packageId:{} userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,userId);

			 functionIds = functionRightDao.needUpFunctionByPackage(
					packageId, userId);
		} catch (Exception e) {
			logger.error("method {} execute error,  packageId:{} userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,userId, e);
			throw e;
		}
		return functionIds;
	}
	@Override
	public List<Long> needUpFunctionByRole(Long packageId, Long roleId)
			throws Exception {
		List<Long> functionIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  packageId:{} roleId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,roleId);

			functionIds = functionRightDao.needUpFunctionByRole(packageId,
					roleId);
		} catch (Exception e) {
			logger.error("method {} execute error, packageId:{} roleId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,roleId,e);
			throw e;
		}
		return functionIds;
	}
	@Override
	public List<Long> needUpFunctionByGroup(Long packageId, Long groupId)
			throws Exception {
		List<Long> functionIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  packageId:{} groupId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,groupId);

			functionIds=functionRightDao.needUpFunctionByGroup(packageId, groupId);
		} catch (Exception e) {
			logger.error("method {} execute error, packageId:{} groupId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,groupId, e);
			throw e;
		}
		return functionIds;
	}
	@Override
	public List<FunctionRight> checkGroupRoleAuthrity(Integer ownerType,
			List<Long> ownerIds, Long funcId) throws Exception {
		List<FunctionRight> functionRights=new ArrayList<FunctionRight>();
		try {
			logger.info("Enter the :{} method  ownerType:{} ownerIds:{} funcId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),ownerType,ownerIds,funcId);
			if(ownerIds.size()>0){
				functionRights = functionRightDao.checkGroupRoleAuthrity(ownerType,
						ownerIds, funcId);
			}
			
		} catch (Exception e) {
			logger.error("method {} execute error, ownerType:{} ownerIds:{} funcId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),ownerType,ownerIds,funcId, e);
			throw e;
		}
		return functionRights;
	}

}
