package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sdcloud.api.core.entity.OrgRight;
import com.sdcloud.api.core.service.OrgRightService;
import com.sdcloud.biz.core.dao.GroupRoleDao;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.biz.core.dao.OrgRightDao;
import com.sdcloud.biz.core.dao.UserGroupDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lihuiquan
 */
@Service
public class OrgRightServiceImpl implements OrgRightService{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrgRightDao orgRightDao;
	
	@Autowired
	private UserGroupDao userGroupDao;
	
	@Autowired
	private GroupRoleDao groupRoleDao;
	
	@Autowired
	private GroupUserDao groupUserDao;
	@Transactional
	public List<Long> insert(List<OrgRight> orgRights) throws Exception {
		logger.info("start method: void insert(List<OrgRight> OrgRights), arg OrgRights: " + orgRights);
		if(orgRights == null || orgRights.size() == 0){
			logger.warn("arg OrgRights is null or size = 0");
			throw new IllegalArgumentException("arg OrgRights is null or size = 0");
		}
		List<Long> ids=new ArrayList<Long>();
		//为对象的orgRightId属性赋值
		for (int i = 0; i < orgRights.size(); i++) {
			OrgRight right = orgRights.get(i);
			long id = -1;
			id = UUIDUtil.getUUNum();
			logger.info("create new OrgRightId:" + id);
			ids.add(id);
			right.setOrgRightId(id);
			
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					orgRightDao.insert(orgRights);
					break;
				} catch (Exception se) {
					ids.clear();
					if(tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key funcRightId:");
//						continue;
					}
				}
			}
		} catch (Exception e) {
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
			orgRightDao.deleteByOwnerId(ownerId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(List<OrgRight> OrgRights, Long ownerId,List<Long> roleIds) throws Exception{
		logger.info("start method: void update(List<OrgRight> OrgRights, Long ownerId), arg OrgRights: " + 
					OrgRights + ", arg ownerId: " + ownerId);
		
		// 先删除该ownerId的权限
		deleteByOwnerId(ownerId);
		//删除管理员角色权限
		if(roleIds!=null&&roleIds.size()>0){
			for (Long roleId : roleIds) {
				deleteByOwnerId(roleId);
			}
		}
		// 再添加新的权限
		insert(OrgRights);
		
		logger.info("complete method, return void");
	}
	
	@Transactional
	public List<OrgRight> findOrgRightByParam(
			Map<String, Object> param) throws Exception {
		List<OrgRight> frts=new ArrayList<OrgRight>();
		try {
			frts=orgRightDao.findOrgRight(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取机构权限失败");
		}
		return frts;
	}
	@Override
	public void deleteByIdList(List ids) throws Exception {
		
	}
	@Override
	public List<Long> findAuthenOrgIds(Long userId, boolean includeRole,
			boolean includeDepartment, boolean includeChild) throws Exception {
		try {
			logger.info(
					"Enter the :{} method  userId:{} includeRole:{} includeDepartment:{} includeChild:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole, includeDepartment, includeChild);

			List<Long> orgIds = orgRightDao.findAuthenOrgIds(userId,
					includeRole, includeDepartment);
			List<Long> result = new ArrayList<Long>();
			result.addAll(orgIds);
			if (includeChild) {
				getChild(result, orgIds, includeDepartment);
			}
			return result;
		} catch (Exception e) {
			logger.error(
					"method {} execute error, userId:{} includeRole:{} includeDepartment:{} includeChild:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole, includeDepartment, includeChild, e);
			throw e;
		}

	}
	//onlyDep 为true时，只查询部门和子部门; 为false时，查询所有子部门和子公司
	private void getChild(List<Long> orgIds, List<Long> parentIds, boolean includeDepartment) {
			if(parentIds == null || parentIds.size() == 0){
				return;
			}
			List<Long> results= orgRightDao.findChildById(parentIds, includeDepartment);
			if (results != null && results.size() > 0) {
				orgIds.addAll(results);
				getChild(orgIds, results, includeDepartment);
			}
	}
	@Override
	public List<Long> findAuthenOrgIdsByPId(Long userId, Long pId,
			boolean includeRole, boolean includeDepartment, boolean includeChild)
			throws Exception {
		try {
			logger.info(
					"Enter the :{} method  userId:{} pId:{} includeRole:{} includeDepartment:{} includeChild:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId,pId, includeRole, includeDepartment, includeChild);

			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(pId);
			List<Long> result = new ArrayList<Long>();
			if (includeChild) {
				result.addAll(orgIds);
				getChild(result, orgIds, includeDepartment);
			}
			return result;
		} catch (Exception e) {
			logger.error(
					"method {} execute error, userId:{} includeRole:{} includeDepartment:{} includeChild:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole, includeDepartment, includeChild, e);
			throw e;
		}
	}
	@Override
	public boolean hasAuthen(Long userId, Long orgId, boolean includeRole)
			throws Exception {
		try {
			logger.info("Enter the :{} method  userId:{} orgId:{} includeRole:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId,  orgId,  includeRole);

			Long hasOgrId = orgRightDao.hasAuthen(userId, orgId, includeRole);
			if (StringUtils.isEmpty(hasOgrId)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} orgId:{} includeRole:{}:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId,  orgId,  includeRole, e);
			throw e;
		}
		
	}
	
	@Async
	@Transactional
	@Override
	public void cleanAuthenOrg(Long groupId,List<Long> authenOrgs) throws Exception {
		try {
			logger.info("Enter the :{} method  groupId:{} authenOrgs:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),groupId,authenOrgs);
			Long begin=System.currentTimeMillis();
			List<Long> groupIds=new ArrayList<Long>();
			List<Long> pids=new  ArrayList<Long>();
			pids.add(groupId);
			getChild(groupIds,pids);
			groupIds.add(groupId);
			List<Long> roleIds = this.groupRoleDao
					.findAuthenRoleIdByGroup(groupIds);
			List<Long> userIds = this.groupUserDao.findUserIdByGroups(groupIds);
			if (authenOrgs != null && authenOrgs.size() > 0) {
				for (Long orgId : authenOrgs) {
					orgRightDao.deleteUserRoleRight(orgId,groupIds, roleIds, userIds);
				}
			}
			System.out.println("清空权限花销时间："+(System.currentTimeMillis()-begin));
		} catch (Exception e) {
			logger.error("method {} execute error, groupId:{} authenOrgs:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),groupId,authenOrgs, e);
			throw e;
		}
		
	}
	@Override
	public List<Long> findAuthenOrgIdsByGroupId(Long groupId,
			boolean includeChild) throws Exception {
		List<Long> result;
		try {
			logger.info("Enter the :{} method  groupId:{} includeChild:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),groupId,includeChild);

			List<Long> orgIds = orgRightDao.findOrgIdsByGroup(groupId);
			result = new ArrayList<Long>();
			if (includeChild) {
				result.addAll(orgIds);
				getChild(result, orgIds, false);
			}
		} catch (Exception e) {
			logger.error("method {} execute error, groupId:{} includeChild:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),groupId,includeChild, e);
			throw e;
		}
		return result;
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
}
