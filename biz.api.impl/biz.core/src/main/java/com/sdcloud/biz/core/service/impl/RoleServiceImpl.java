package com.sdcloud.biz.core.service.impl;


import java.util.ArrayList;
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

import com.sdcloud.api.core.entity.Module;
import com.sdcloud.api.core.entity.Role;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.RoleService;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.biz.core.dao.GroupRoleDao;
import com.sdcloud.biz.core.dao.OrgRightDao;
import com.sdcloud.biz.core.dao.RoleDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.biz.core.dao.UserRoleDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * @author czz
 * 
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private FunctionRightDao functionRightDao;
	
	@Autowired
	private OrgRightDao orgRightDao;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private GroupRoleDao groupRoleDao;
	
	@Autowired
	private UserDao userDao;

	@Transactional
	public long insert(Role role) throws Exception {

		logger.info("start insert a new role : " + role);
		if (role == null) {
			logger.warn("role is null");
			throw new IllegalArgumentException("role is null");
		}
		long roleId = -1;

		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				roleId = UUIDUtil.getUUNum();
				logger.info("create orleId:" + roleId);
				role.setRoleId(roleId);
				try {
					roleDao.insert(role);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key roleId:" + roleId);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err when insert role:", e);
			throw e;
		}
		logger.info("complete insert a role : " + role);
		return roleId;
	}

	@Transactional
	public List<Role> findByUser(long userId) throws Exception {

		logger.info("start findByUser, userId : " + userId);
		List<Role> roles;
		try {
			roles = roleDao.findByUser(userId);
		} catch (Exception e) {
			logger.error("err when findByUser, userId : " + userId, e);
			throw e;
		}
		logger.info("complete findByUser, userId : " + userId + ", roles : "
				+ roles);
		return roles;
	}

	@Transactional
	public List<Role> findByOwnerCode(long ownerCode) {
		
		return null;
	}

	@Transactional
	public void update(Role role) throws Exception {

		logger.info("start update role : " + role);

		try {
			roleDao.update(role);
		} catch (Exception e) {
			logger.error("err when update role : " + role, e);
			throw e;
		}

		logger.info("complete update role : " + role);

	}

	@Transactional
	public void deleteById(long roleId) throws Exception {

		logger.info("start deleteById, orleId : " + roleId);

		try {
			logger.info("checking if there is user under roleId: " + roleId);
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("roleId", roleId);
			List<User> users = userDao.findByRole(param);
			if (users.size() > 0) {
				logger.warn("roleId " + roleId
						+ "cannot be deleted due to there is user under it");
				throw new IllegalArgumentException("包含用户，无法删除");
			}
			logger.info("deleting roleId : " + roleId);
			List<Long> roleIds=new ArrayList<Long>();
			roleIds.add(roleId);
			groupRoleDao.deleteByRoleId(roleIds);
			orgRightDao.deleteByOwnerId(roleId);
			functionRightDao.deleteByOwnerId(roleId);
			roleDao.deleteById(roleId);
		} catch (Exception e) {
			logger.error("err when deleteById, orleid : " + roleId, e);
			throw e;
		}

		logger.info("complete deleteById, orleid : " + roleId);
	}

	@Transactional
	public void updateRoleUser(Long roleId, List<Long> userIds)
			throws Exception {

		logger.info("start updateRoleUser, roleId : " + roleId + ", userIds"
				+ userIds);

		if (userIds == null) {
			logger.warn("userIds is null");
			throw new IllegalArgumentException("userIds null");
		}

		try {
			logger.info("clear all users under role : " + roleId);
			roleDao.clearUser(roleId);

			if (userIds.size() > 0) {
				logger.info("set users : " + userIds + " under user : "
						+ roleId);
				roleDao.setUsers(roleId, userIds);
			} else {
				logger.info("there are no user under role : " + roleId);
			}
		} catch (Exception e) {
			logger.error("err when updateRoleUser, roleId : " + roleId
					+ ", userIds" + userIds, e);
			throw e;
		}

		logger.info("complete updateRoleUser, roleId : " + roleId + ", userIds"
				+ userIds);

	}

	public List<Role> findAll() throws Exception {
		logger.info("start method: List<Role> findAll()");
		List<Role> roles = new ArrayList<Role>();
		try {
			roles = roleDao.findAll();
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return roles: " + roles);
		return roles;
	}

	public List<Role> findRolesByParam(Map<String,Object>param) throws Exception {
		logger.info("start findByParam, param :{} ", param);
		List<Role> roles;
		try {
			roles = roleDao.findByParam(param);
		} catch (Exception e) {
			logger.error("err when findByParam, userId : " + param, e);
			throw e;
		}
		logger.info("complete findByParam, userId : " + param + ", roles : "
				+ roles);
		return roles;
	}

	@Override
	public List<Role> findRoleParent(Long roleId) {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId);
		List<Role> roles = null;
		try {
			roles = new ArrayList<Role>();
			getParent(roles,roleId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,e);
			throw e;
		}
		return roles;
	}
	public void getParent(List<Role> roles,Long roleId){
		Role role =roleDao.findRoleParent(roleId);
		if(role!=null){
			roles.add(role);
			getParent(roles,roleId);
		}
	}

	@Override
	public List<Role> findRoleChild(List<Long> roleIds) {
		// TODO Auto-generated method stub
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleIds);
		List<Role> roles = null;
		try {
			roles = new ArrayList<Role>();
			getChild(roles,roleIds);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleIds,e);
			throw e;
		}
		return roles;
	}
	
	public void getChild(List<Role> roles,List<Long> roleIds){
		List<Role> rolesChilds=roleDao.findRoleChild(roleIds);
		if(rolesChilds!=null&&rolesChilds.size()>0){
			roles.addAll(rolesChilds);
			roleIds.clear();
			for (Role role : rolesChilds) {
				roleIds.add(role.getRoleId());
			}
			getChild(roles,roleIds);
		}
	}

	@Override
	public List<Role> findRoleByUserGroup(Long userGroupId)throws Exception  {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupId);
		List<Role> roles=null;
		try {
			roles=roleDao.findRoleByUserGroup(userGroupId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupId,e);
			throw e;
		}
		return roles;
	}
	
	@Override
	public List<Role> findRoleByUserGroupPid(Long userGroupPid) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupPid);
		List<Role> roles=null;
		try {
			roles=roleDao.findRoleByUserGroupPid(userGroupPid);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupPid,e);
			throw e;
		}
		return roles;
	}

	@Override
	public Role findRoleById(Long roleId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId);

		Role role=null;
		try {
			 role=roleDao.findRoleById(roleId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,e);
			throw e;
		}
		return role;
	}

	@Override
	public List<Long> findRootRoleByGroup(List<Long> groupIds) throws Exception {
		logger.info("Enter the :{} method groupId:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupIds);
		try {
			return roleDao.findRootRoleByGroup(groupIds);
		} catch (Exception e) {
			logger.error("method {} execute error,groupId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupIds,e);
			throw e;
		}
	}

	@Override
	public Long hasRoleByName(String roleName, Long groupId) throws Exception {
		logger.info("Enter the :{} method roleName:{},groupId:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleName,groupId);
		try {
			return roleDao.hasRoleByName(roleName, groupId);
		} catch (Exception e) {
			logger.error("method {} execute error,roleName:{},groupId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleName,groupId,e);
			throw e;
		}
	}

	@Override
	public List<Long> findNotFunctionRootRoleId(Long functionId)
			throws Exception {
		List<Long> roleIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  functionId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),functionId);

			roleIds= roleDao.findNotFunctionRootRoleId(functionId);
		} catch (Exception e) {
			logger.error("method {} execute error, functionId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),functionId, e);
			throw e;
		}
		return roleIds;
	}
	@Override
	public List<Long> findNotTopicRootRoleId(Long topicId)
			throws Exception {
		List<Long> roleIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  topicId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),topicId);

			roleIds= roleDao.findNotTopicRootRoleId(topicId);
		} catch (Exception e) {
			logger.error("method {} execute error, topicId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),topicId, e);
			throw e;
		}
		return roleIds;
	}


}
