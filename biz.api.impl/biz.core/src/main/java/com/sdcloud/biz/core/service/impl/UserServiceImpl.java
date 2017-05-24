package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.GroupUser;
import com.sdcloud.api.core.entity.Role;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.entity.UserRole;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.biz.core.dao.OrgDao;
import com.sdcloud.biz.core.dao.OrgRightDao;
import com.sdcloud.biz.core.dao.RoleDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.biz.core.dao.UserRoleDao;
import com.sdcloud.framework.common.HashGeneratorUtils;
import com.sdcloud.framework.common.RoleType;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author czz
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private FunctionRightDao functionRightDao;
	
	@Autowired
	private OrgRightDao orgRightDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private GroupUserDao groupUserDao;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private OrgDao orgDao;
	
	@Transactional
	public User insert(User user) throws Exception {
		
		logger.info("start insert a new user : "+ user);
		
		if(user == null){
			logger.warn("user is null");
			throw new IllegalAccessException("user is null");
		}
		
		Long userId  = -1L;
		try {
			//新建用户默认密码password
			
			String password=null;
			if(StringUtils.isEmpty(user.getPassword())){
				password= HashGeneratorUtils.generateMD5("password");
			}else{
				password= HashGeneratorUtils.generateMD5(user.getPassword());
			}
			userId = UUIDUtil.getUUNum();
			if(!StringUtils.isEmpty(user.getPassword())){
				String encryptedPassword = HashGeneratorUtils.generateMD5(user.getPassword());
				user.setPassword(encryptedPassword);
			}
			if(StringUtils.isEmpty(user.getUserId())){
				user.setUserId(userId);
			}
			user.setPassword(password);
			userDao.insert(user);
		} catch (Exception e) {
			logger.error("err when insert new user : " + user,e);
			throw e;
		}
		logger.info("complete insert new user : " + user);
		return user;
	}

	@Transactional
	public void update(User user,List<Long> roles) throws Exception {
		
		logger.info("start update user : " + user);
		
		if(user == null || user.getUserId() == null){
			logger.warn("user is null or user 's userId is null");
			throw new IllegalArgumentException("user is null or user 's userId is null");
		}
		
		try {
			
			userDao.update(user);
			List<Long> userIds=new ArrayList<Long>();
			userIds.add(user.getUserId());
			userRoleDao.deleteByUserId(userIds);
			if (roles!=null&&roles.size()>0) {
				for (Long roleId : roles) {
					UserRole userRole = new UserRole();
					userRole.setRoleId(roleId);
					userRole.setUserId(user.getUserId());
					userRole.setUserRoleId(UUIDUtil.getUUNum());
					userRoleDao.insert(userRole);
				}
			}
		} catch (Exception e) {
			logger.error("method {} execute error,User:{} roles:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),user,roles,e);
			throw e;
		}
		
		logger.info("complete update user : " + user );
		
	}

	@Transactional
	public void deleteById(List<Long> userIds) throws Exception {
		logger.info("start deleteById : " + userIds);
		
		try {
			userDao.deleteById(userIds);
			userRoleDao.deleteByUserId(userIds);//删除pf_user_role表中的关联关系
			groupUserDao.deleteByUserId(userIds);
			for (Long userId : userIds) {
				orgRightDao.deleteByOwnerId(userId);
				functionRightDao.deleteByOwnerId(userId);
			}
			
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		
		logger.info("complete deleteById : " + userIds);
	}

	/*@Transactional
	public Pager<User> findByOrg(Long orgId, boolean includeSub, Pager<User> pager) {
		
		logger.info("start findByOrg, OrgId : " + orgId +", includeSub : " + includeSub + ", pager ： "+pager);
		
		if(pager == null){
			pager = new Pager<User>();
		}
		
		try {
			logger.info("init default pager");
			if(StringUtils.isEmpty(pager.getOrderBy())){
				pager.setOrderBy("userId");
			}
			if(StringUtils.isEmpty(pager.getOrder())){
				pager.setOrder("desc");
			}
			
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(orgId);
			if(includeSub){
				logger.info("querying children orgs");
				getChild(orgIds, orgIds);
				logger.info("complete querying children orgs:{}", orgIds);
			}
			
			if(pager.isAutoCount()){
				logger.info("querying total count by orgIds : " + orgIds);
				long totalCount = userDao.countByOrg(orgIds);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			
			logger.info("querying by orgIds : " + orgIds);
			
			List<User> result = userDao.findByOrg(orgIds, pager);
			
			pager.setResult(result);
		} catch (Exception e) {
			logger.error("err when findByOrg, OrgId : " + orgId +", includeSub : " + includeSub + ", pager ： "+pager,e);
		}
		
		logger.info("complete findByOrg, OrgId : " + orgId +", includeSub : " + includeSub + ", pager ： "+pager);
		
		return pager;
	}*/
	

	private void getChild(List<Long> orgIds, List<Long> parentIds) {
		if(parentIds == null || parentIds.size() == 0){
			return;
		}
		List<Map<String,Object>> results = orgDao.findChildById(parentIds);
		if (results != null && results.size() > 0) {
			List<Long> pIds = new ArrayList<Long>();
			for (Map<String,Object> item : results) {
				pIds.add((Long)item.get("orgId"));
			}
			orgIds.addAll(pIds);
			getChild(orgIds, pIds);
		}
	}

	@Transactional
	public User verifyUser(String name, String password) throws Exception {
		logger.info("start findByPwd, name:" + name +", pwd:"+password);
		User user= null;
		
		try {
			password = HashGeneratorUtils.generateMD5(password);
			logger.info("encrypted pwd : " + password);
			user = userDao.findByPwd(name, password);
		} catch (Exception e) {
			logger.error("err when findByPwd, name:" + name+", pwd:"+password,e);
			throw e;
		}
		
		logger.info("complete findByPwd, user:" + user);
		return user;
	}

	public Pager<User> findByRole(Long roleId, Pager<User> pager) throws Exception{
		logger.info("start method: Pager<User> findByRole(Long roleId, Pager<User> pager), arg roleId: " + roleId + 
					", arg pager: " + pager);
		if(roleId == null){
			logger.warn("arg roleId is null");
			throw new IllegalArgumentException("arg roleId is null");
		}
		
		Integer roleType = null;
		
		if(pager ==  null){
			pager = new Pager<User>();
		}else{
			if(pager.getParams().get("roleType").toString().length()>0){
				roleType = Integer.valueOf(pager.getParams().get("roleType").toString());
			}
			
		}
		try {
			logger.info("init default pager");
			if(StringUtils.isEmpty(pager.getOrderBy())){
				pager.setOrderBy("userId");
			}
			if(StringUtils.isEmpty(pager.getOrder())){
				pager.setOrder("asc");
			}
			
			if(pager.isAutoCount()){
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("roleId", roleId);
				if(!StringUtils.isEmpty(roleType)&&roleType==RoleType.GENERAL){
					param.put("groupId",pager.getParams().get("groupId"));
				}
				long totalCount = userRoleDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			if(!StringUtils.isEmpty(roleType)&&roleType==RoleType.GENERAL){
				param.put("groupId", pager.getParams().get("groupId"));
			}
			param.put("roleId", roleId);
			param.put("pager", pager);
			
			List<User> roles = userDao.findByRole(param);
			if(!StringUtils.isEmpty(roles)){
				for (User user : roles) {
					List<Role> userRoles=roleDao.findByUser(user.getUserId());
					user.setRoles(userRoles);
				}
			}
			pager.getParams().remove("groupIds");
			pager.setResult(roles);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public User insertAndUserRole(User user, List<Long> roleIds, Long groupId)
			throws Exception {
		logger.info("start method: void insertAnd2UserRole(Long roleId, User user), arg roleId: "
				+ roleIds + ", arg user: " + user);

		/*
		 * userRole.setOwnerCode(null); userRole.setTenantId(null);
		 */
		User rtnUser = null;
		try {
			rtnUser = insert(user);
			if (!StringUtils.isEmpty(roleIds)) {
				for (Long roleId : roleIds) {
					UserRole userRole = new UserRole();
					userRole.setRoleId(roleId);
					userRole.setUserId(rtnUser.getUserId());
					userRole.setUserRoleId(UUIDUtil.getUUNum());
					userRoleDao.insert(userRole);
				}
			}
			if (!StringUtils.isEmpty(groupId)) {
				// 用户组用户
				GroupUser groupUser = new GroupUser();
				groupUser.setGroupId(groupId);
				groupUser.setUserId(user.getUserId());
				groupUser.setGroupUserId(UUIDUtil.getUUNum());
				List<Long> userIds=groupUserDao.findUserIdByGroup(groupId);
				if(!userIds.contains(user.getUserId())){
					groupUserDao.insert(groupUser);
				}
				
			}

		} catch (Exception e) {
			logger.error("err method, Exception:{} ", e);
			throw e;
		}
		logger.info("complete method, return void");
		return rtnUser;
	}

	@Override
	public User findByUesrId(Long userId) throws Exception {
		logger.info("start method UserServiceImpl.findByUserId, userId:" + userId);
		User user = null;
		try{
			user = userDao.findByUserId(userId);
		}
		catch(Exception e){
			logger.error("err when UserServiceImpl.findByUserId, userId:" + userId, e);
			throw e;
		}
		logger.info("complete method UserServiceImpl.findByUserId");
		return user;
	}

	@Override
	public Pager<User> findByRoleParam(Map<String, Object> param,
			Pager<User> pager) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
		if(param.get("roleId") == null){
			logger.warn("method {} execute warn arg roleId is null,param:{} ",Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
			throw new IllegalArgumentException("arg roleId is null");
		}
		if(pager ==  null){
			pager = new Pager<User>();
		}
		try {
			logger.info("init default pager");
			if(StringUtils.isEmpty(pager.getOrderBy())){
				pager.setOrderBy("userId");
			}
			if(StringUtils.isEmpty(pager.getOrder())){
				pager.setOrder("asc");
			}
			if(pager.isAutoCount()){
				long totalCount = userRoleDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				logger.info("method {}  execute querying total count result : {}", Thread.currentThread() .getStackTrace()[1].getMethodName(),totalCount);
			}
			param.put("pager", pager);
			List<User> roles = userDao.findByRole(param);
			
			pager.setResult(roles);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
		return pager;
	}


	@Override
	public User findByUesr(Long userId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);

		try {
			return userDao.findByUser(userId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}

	@Override
	public Pager<User> findByGroup(Pager<User> pager, Map<String,Object> param)
			throws Exception {
		
		if(pager ==  null){
			pager = new Pager<User>();
		}
		try {
			logger.info("init default pager");
			if(StringUtils.isEmpty(pager.getOrderBy())){
				pager.setOrderBy("userId");
			}
			if(StringUtils.isEmpty(pager.getOrder())){
				pager.setOrder("asc");
			}
			
			if(pager.isAutoCount()){
				long totalCount = groupUserDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				
				logger.info("querying total count result :{} " , totalCount);
			}
			param.put("pager", pager);
			
			List<User> roles = userDao.findByRole(param);
			if(!StringUtils.isEmpty(roles)){
				for (User user : roles) {
					List<Role> userRoles=roleDao.findByUser(user.getUserId());
					user.setRoles(userRoles);
				}
			}
			
			
			pager.setResult(roles);
			
		} catch (Exception e) {
			logger.error("err method, Exception:{} ", e);
			throw e;
		}
		logger.info("complete method, return pager:{} ",pager);
		return pager;
	}

	@Transactional
	public void insertUserRole(Long userId, List<Long> roles) throws Exception {
		logger.info("Enter the :{} method ,userId:{}, roles:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roles);
		try {
			for (Long roleId : roles) {
				UserRole userRole =new UserRole();
				userRole.setRoleId(roleId);
				userRole.setUserId(userId);
				userRole.setUserRoleId(UUIDUtil.getUUNum());
				userRoleDao.insert(userRole);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,userId:{}, roles:{},Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roles,e);
			throw e;
		}
	}

	@Override
	public Map<Long, User> findUserMapByIds(List<Long> userIds) throws Exception {
		
		logger.info("start method UserServiceImpl.findUserMapByIds, userIds:" + userIds);
		Map<Long,User> result = new HashMap<>();
		List<User> userList = null;
		try{
			userList = userDao.findByUserIds(userIds);
			if(userList==null || userList.isEmpty()){
				return null;
			}
			for(User user: userList){
				result.put(user.getUserId(), user);
			}
		}
		catch(Exception e){
			logger.error("err when UserServiceImpl.findUserMapByIds, userIds:" + userIds, e);
			throw e;
		}
		logger.info("complete method UserServiceImpl.findUserMapByIds");
		return result;

	}

	@Override
	public User findByUesrName(String name) throws Exception {
		logger.info("Enter the :{} method ,name:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),name);
		try {
				return  userDao.findByUserName(name);
		} catch (Exception e) {
			logger.error("method {} execute error,name:{},Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),name,e);
			throw e;
		}
	}

	@Override
	public boolean isTenantRootUser(Long userId) throws Exception {
		try {
			logger.info("Enter the :{} method  userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId);

			Long groupId = userDao.isTenantRootUser(userId);
			if (!StringUtils.isEmpty(groupId)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId, e);
			throw e;
		}
		return false;
	}

	@Override
	public void updatePwd(User user,String password) throws Exception {

		logger.info("start update user : " + user);
		
		if(user == null || user.getUserId() == null){
			logger.warn("user is null or user 's userId is null");
			throw new IllegalArgumentException("user is null or user 's userId is null");
		}
		
		try {
			if(!StringUtils.isEmpty(password)){
				String encryptedPassword = HashGeneratorUtils.generateMD5(password);
				user.setPassword(encryptedPassword);
			}
			userDao.update(user);
		} catch (Exception e) {
			logger.error("",e);
			throw e;
		}
		
		logger.info("complete update user : " + user );
		
	}

	@Override
	public List<User> findByEmployee(Employee employee) throws Exception {
		try {
			logger.info("Enter the :{} method  employee:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),employee);

			return userDao.findUserByEmployee(employee);
		} catch (Exception e) {
			logger.error("method {} execute error, employee:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),employee,e);
			throw e;
		}
	}

}
