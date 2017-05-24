package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.sdcloud.api.core.entity.GroupRole;
import com.sdcloud.api.core.entity.Role;
import com.sdcloud.api.core.entity.UserGroup;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserGroupService;
import com.sdcloud.biz.core.dao.EmployeeDao;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.biz.core.dao.GroupRoleDao;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.biz.core.dao.OrgRightDao;
import com.sdcloud.biz.core.dao.RoleDao;
import com.sdcloud.biz.core.dao.UserGroupDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.biz.core.dao.UserRoleDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.RoleType;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lihuiquan
 * 
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserGroupDao userGroupDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private FunctionRightDao functionRightDao;
	
	@Autowired
	private OrgRightDao  orgRightDao;
	
	@Autowired
	private GroupUserDao groupUserDao;
	
	@Autowired
	private GroupRoleDao groupRoleDao;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Transactional
	public UserGroup insert(UserGroup userGroup) throws IllegalArgumentException, Exception {
		logger.info("start insert a new UserGroupanization : " + userGroup);
		long userGroupId = -1;
		if (userGroup == null) {
			logger.warn("userGroup is null");
			throw new IllegalArgumentException("userGroup is null");
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				userGroupId = UUIDUtil.getUUNum();
				logger.info("create new userGroupId:" + userGroupId);
				if(StringUtils.isEmpty(userGroup.getGroupId())){
					userGroup.setGroupId(userGroupId);
				}
				
				try {
					userGroupDao.insert(userGroup);
					if(userGroup.getRole()==null||StringUtils.isEmpty(userGroup.getRole().getRoleId())){
						Role role=new Role();
						role.setpId(userGroup.getGroupId());
						role.setBeginTime(userGroup.getCreateTime());
						role.setCreateTime(userGroup.getCreateTime());
						role.setRoleType(RoleType.ADMIN);
						role.setRoleName("管理员");
						role.setTenantId(userGroup.getTenantId());
						role.setRoleId(UUIDUtil.getUUNum());
						roleDao.insert(role);
						GroupRole groupRole=new GroupRole();
						groupRole.setGroupId(userGroup.getGroupId());
						groupRole.setGroupRoleId(UUIDUtil.getUUNum());
						groupRole.setRoleId(role.getRoleId());
						groupRole.setTenantId(userGroup.getGroupId());
						groupRoleDao.insert(groupRole);
						userGroup.setRole(role);
					}
					
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key userGroupId:" + userGroupId);
						continue;
					}
				}
			}

		} catch (Exception e) {
			logger.error("err when insert a UserGroupanization : " + userGroup, e);
			throw e;
		}
		logger.info("complete insert a new UserGroupanization : " + userGroup);
		return userGroup;
	}
	
	
	public String getLastCode(){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("parentId",-1);
		int max = -1;
		List<String> codes=userGroupDao.findOwnerCodeByParam(param);
		for (String code : codes) {
			String[] spcode=code.split("userGroup");
			if(spcode!=null&&spcode.length>1){
				if(Integer.valueOf(spcode[1])>max){
					max=Integer.valueOf(spcode[1]);
				}
			}
		}
		return "userGroup0"+(max+1);
	}

	@Transactional
	public void deleteById(long delUserGroupId, boolean isSubCompany)
			throws Exception {
		logger.info("start deleteById a UserGroupanization : " + delUserGroupId);
		// 用于查找用户组下的用户
		List<Long> userGroupIds = new ArrayList<Long>();
		userGroupIds.add(delUserGroupId);

		try {
			if (isSubCompany) {
				// 检查是否是根节点
				logger.info("checking if it is root node");
				Long pUserGroupId = userGroupDao.findParent(delUserGroupId);
				if (pUserGroupId == 0) {
					logger.warn("delUserGroupId " + delUserGroupId
							+ " cannot be deleted due to it's root");
					throw new Exception("根节点，无法删除");
				}
				// 检查是否有子用户组
				logger.info("checking if it has children userGroup");
				List<Long> groupIds=new ArrayList<Long>();
				groupIds.add(delUserGroupId);
				List<Long> childUserGroupIds = userGroupDao.findChildren(groupIds);
				if (childUserGroupIds != null && childUserGroupIds.size()> 0) {
					logger.warn("delUserGroupId " + delUserGroupId
							+ " cannot be deleted due to it has children userGroup");
					throw new Exception("包含子用户组，无法删除");
				}

				// 检查是否有用户
				logger.info("checking if it has user");
				List<Long> users =groupUserDao.findUserIdByGroup(delUserGroupId);
				if (users!=null&&users.size() > 0) {
					logger.warn("delUserGroupId " + delUserGroupId
							+ " cannot be deleted due to it has user");
					throw new Exception("包含用户，无法删除");
				}

			}
			// 检查是否有角色
			logger.info("checking if it has employee");
			List<Long> roleIds=new ArrayList<Long>();
			roleIds= groupRoleDao.findRoleIdByGroup(delUserGroupId);
			List<Long> groupIds=new ArrayList<Long>();
			groupIds.add(delUserGroupId);
			List<Long> roleRootIds=new ArrayList<Long>();
			roleRootIds=roleDao.findRootRoleByGroup(groupIds);
			roleIds.removeAll(roleRootIds);
			if (roleIds!=null&&roleIds.size()>0) {
				logger.warn("delUserGroupId " + delUserGroupId
						+ " cannot be deleted due to it has employee");
				throw new Exception("包含角色，无法删除");
				
			}
			for (Long roleId : roleRootIds) {
				roleDao.deleteById(roleId);
				orgRightDao.deleteByOwnerId(roleId);
				functionRightDao.deleteByOwnerId(roleId);
				groupRoleDao.deleteGroupRole(delUserGroupId, roleId);
			}
			
			// 删除
			logger.info("deleting it");
			orgRightDao.deleteByOwnerId(delUserGroupId);
			functionRightDao.deleteByOwnerId(delUserGroupId);
			userGroupDao.deleteById(delUserGroupId);
		} catch (Exception e) {
			logger.error("err when deleteById:" + delUserGroupId, e);
			throw e;
		}
		logger.info("complete deleteById a UserGroupanization : " + delUserGroupId);
	}

	@Transactional
	public UserGroup update(UserGroup userGroup) throws IllegalAccessException, Exception {
		logger.info("start update userGroup : " + userGroup);
		if (userGroup == null) {
			logger.warn("updateUserGroup is null");
			throw new IllegalAccessException("updateUserGroup is null");
		}
		try {
			userGroupDao.update(userGroup);
			userGroup = userGroupDao.findById(Arrays.asList(userGroup.getGroupId())).get(0);
		} catch (Exception e) {
			logger.error("err when update userGroup:" + userGroup.getGroupId(), e);
			throw e;
		}
		logger.info("complete update userGroup : " + userGroup);
		return userGroup;
	}

	@Transactional
	public List<UserGroup> findByOwnerCode(long ownerCode) {
		return null;
	}

	@Transactional
	public List<UserGroup> findById(long userGroupId, boolean includeChild)
			throws Exception {
		logger.info("start findById userGroupId : " + userGroupId);
		List<UserGroup> userGroups = null;
		try {
			List<Long> userGroupIds = new ArrayList<Long>();

			if (includeChild) {
				logger.info("quering children userGroup : ");
				// 查询子部门id集合
				String idStringList = userGroupDao.findChildren(userGroupId);
				logger.info("query children userGroup result : " + idStringList);
				for (String id : idStringList.split(",")) {
					userGroupIds.add(Long.parseLong(id));
				}
			}
			userGroupIds.add(userGroupId);
			logger.info("quering userGroup by ids : " + userGroupIds);
			userGroups = userGroupDao.findById(userGroupIds);
		} catch (Exception e) {
			logger.error("err when findByIds : " + userGroupId, e);
			throw e;
		}
		logger.info("complete findById userGroupId : " + userGroupId);
		return userGroups;
	}
	
	@Transactional
	public List<UserGroup> findAll() throws Exception {
		logger.info("start method: List<UserGroup> findAll()");
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		try {
			userGroups = userGroupDao.findAll();
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return userGroups: " + userGroups);
		return userGroups;
	}

	@Override
	public List<UserGroup> findUserGroupByParam(Map<String, Object> param) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread()
				.getStackTrace()[1].getMethodName(), param);
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		try {
			List<Long> userGroupIds = new ArrayList<Long>();
			
			userGroups = userGroupDao.findByParam(param);
			if (param.get("includeChild")!=null&&(boolean) param.get("includeChild")) {
				logger.info("quering children userGroup : ");
				for (UserGroup userGroup : userGroups) {

					// 查询子部门id集合
					String ownerCode=null;//userGroup.getOwnerCode();
					List<Long> idStringList =null;// userGroupDao.findNewChildren(ownerCode);
					logger.info("query children userGroup result :{} ", idStringList);
					
					param.remove("userGroupId");
					param.remove("parentId");
					param.remove("tenanId");
					userGroupIds.addAll(idStringList);
				}
				param.put("userGroupIds", userGroupIds);
			}else{
				return userGroups;
			}
			userGroups = userGroupDao.findByParam(param);
		} catch (Exception e) {
			logger.error("method {} execute error ,param:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					param, e);
			throw e;
		}
		return userGroups;
	}


	@Override
	public List<UserGroup> findChildAndSelf(long userGroupId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupId);
		List<UserGroup> userGroups=null;
		try {
			List<Long> parentIds=new ArrayList<Long>();
			parentIds.add(userGroupId);
			List<Long> groupIds=new ArrayList<Long>();
			groupIds.add(userGroupId);
			getChild(groupIds,parentIds);
			userGroups=userGroupDao.findById(groupIds);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userGroupId,e);
			throw e;
		}
		
		
		return userGroups;
	}
	private void getChild(List<Long> groupIds,List<Long> parentIds){
		if(parentIds == null || parentIds.size() == 0){
			return;
		}
		parentIds=userGroupDao.findChildren(parentIds);
		if(parentIds!=null&&parentIds.size()>0){
			groupIds.addAll(parentIds);
			getChild(groupIds,parentIds);
		}
	}


	@Override
	public List<Long> findNotFunctionGroupId(Long functionId)
			throws Exception {
		try {
			logger.info("Enter the :{} method functionId :{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),functionId);
			return this.userGroupDao.findNotFunctionGroupId(functionId);
		} catch (Exception e) {
			logger.error("method {} execute error, functionId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),functionId, e);
			throw e;
		}
	}
	@Override
	public List<Long> findNotTopicGroupId(Long topicId)
			throws Exception {
		try {
			logger.info("Enter the :{} method topicId :{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),topicId);
			return this.userGroupDao.findNotTopicGroupId(topicId);
		} catch (Exception e) {
			logger.error("method {} execute error, topicId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),topicId, e);
			throw e;
		}
	}

}
