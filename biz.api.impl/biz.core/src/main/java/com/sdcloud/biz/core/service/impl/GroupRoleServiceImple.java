package com.sdcloud.biz.core.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sdcloud.api.core.entity.GroupRole;
import com.sdcloud.api.core.entity.Role;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.GroupRoleService;
import com.sdcloud.biz.core.dao.GroupRoleDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.framework.common.UUIDUtil;
@Service("groupRoleService")
public class GroupRoleServiceImple implements GroupRoleService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	GroupRoleDao groupRoleDao;
	@Autowired
	UserDao userDao;
	
	@Override
	public long getTotalCount(Map<String, Object> param)throws Exception{
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);

		try {
			return groupRoleDao.getTotalCount(param);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;

		}
	}

	@Override
	public GroupRole insert(GroupRole groupRole) throws Exception{
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupRole);

		try {
			if(StringUtils.isEmpty(groupRole.getGroupRoleId())){
				long id = -1;
				id = UUIDUtil.getUUNum();
				groupRole.setGroupRoleId(id);
			}
			groupRoleDao.insert(groupRole);
			
			return groupRole;
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupRole,e);
			throw e;
		}
	}

	@Override
	public void deleteByRoleId(List<Long> roleIds) throws Exception{
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleIds);

		try {
			
			groupRoleDao.deleteByRoleId(roleIds);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleIds,e);
			throw e;
		}
	}

	@Override
	public GroupRole hasGroupRole(Map<String, Object> param)throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
		try {
			List<GroupRole> groupRoles=groupRoleDao.hasGroupRole(param);
			if(groupRoles!=null&&groupRoles.size()>0){
				return groupRoles.get(0);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
		return null;
	}

	@Override
	public void deleteGroupRole(Long groupId, Long roleId) throws Exception {
		
		try {
			if(StringUtils.isEmpty(groupId)||StringUtils.isEmpty(roleId)){
				logger.error("method {} execute error,groupId:{},roleId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,roleId,"参数有空值");
				throw new Exception("groupId or userId not be null!");
			}
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("roleId", roleId);
			param.put("groupId", groupId);
			List<User> users = userDao.findByRole(param);
			if (users.size() > 0) {
				logger.warn("roleId " + roleId
						+ "cannot be deleted due to there is user under it");
				throw new Exception("包含用户，无法删除");
			}
			groupRoleDao.deleteGroupRole(groupId, roleId);
		} catch (Exception e) {
			logger.error("method {} execute error,groupId:{},roleId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,roleId,e);
			throw e;
		}
		
	}

	@Override
	public void deleteByRoleIdAndGroup(Long groupId, Long roleId)
			throws Exception {
		logger.info("Enter the :{} method param:{},groupId:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,groupId);
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("roleId", roleId);
			param.put("groupId", groupId);
			List<User> users = userDao.findByRole(param);
			if (users.size() > 0) {
				logger.warn("roleId " + roleId
						+ "cannot be deleted due to there is user under it");
				throw new Exception("包含用户，无法删除");
			}
		
			groupRoleDao.deleteGroupRole(groupId, roleId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,e);
			throw e;
		}
	}

}
