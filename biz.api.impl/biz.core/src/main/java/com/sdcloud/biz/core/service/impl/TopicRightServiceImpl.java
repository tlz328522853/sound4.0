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

import com.sdcloud.api.core.entity.TopicRight;
import com.sdcloud.api.core.service.TopicRightService;
import com.sdcloud.biz.core.dao.TopicRightDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lihuiquan
 */
@Service("topicRightService")
public class TopicRightServiceImpl implements TopicRightService{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TopicRightDao topicRightDao;

	@Transactional
	public List<Long> insert(List<TopicRight> topicRights) throws Exception {
		logger.info("start method: void insert(List<TopicRight> topicRights), arg topicRights: " + topicRights);
		if(topicRights == null || topicRights.size() == 0){
			logger.warn("arg topicRights is null or size = 0");
			throw new IllegalArgumentException("arg topicRights is null or size = 0");
		}
		List<Long> ids=new ArrayList<Long>();
		
		//为对象的funcRightId属性赋值
		for (int i = 0; i < topicRights.size(); i++) {
			TopicRight right = topicRights.get(i);
			long id = -1;
			id = UUIDUtil.getUUNum();
			logger.info("create new topicRightId:" + id);
			right.setTopicRightId(id);
			ids.add(id);
			
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					topicRightDao.insert(topicRights);
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
			topicRightDao.deleteByOwnerId(ownerId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(List<TopicRight> topicRights, Long ownerId,List<Long> roleIds) throws Exception{
		logger.info("start method: void update(List<TopicRight> topicRights, Long ownerId), arg topicRights: " + 
					topicRights + ", arg ownerId: " + ownerId);
		
		// 先删除该ownerId的权限
		deleteByOwnerId(ownerId);
		//删除管理员角色权限
		if(roleIds!=null&&roleIds.size()>0){
			deleteByOwnerIds(roleIds, 1);
		}
		
		// 再添加新的权限
		insert(topicRights);
		
		logger.info("complete method, return void");
	}
	@Override
	public List<TopicRight> findTopicRightByParam(
			Map<String, Object> param) throws Exception {
		List<TopicRight> frts=new ArrayList<TopicRight>();
		try {
			frts=topicRightDao.findTopicRight(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return frts;
	}
	@Override
	public TopicRight hasTopicRight(Map<String, Object> param) {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);
		TopicRight topicRight=null;
		try {
			
			List<TopicRight> frs= topicRightDao.hasTopicRight(param);
			if(frs!=null&&frs.size()>0){
				topicRight=frs.get(0);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
		return topicRight;
	}
	@Override
	public List<Long> needUpTopicByPackage(Long packageId, Long userId)
			throws Exception {
		List<Long> topicIds=new ArrayList<Long>();
        try {
			logger.info("Enter the :{} method  packageId:{} userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,userId);

			 topicIds = topicRightDao.needUpTopicByPackage(
					packageId, userId);
		} catch (Exception e) {
			logger.error("method {} execute error,  packageId:{} userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,userId, e);
			throw e;
		}
		return topicIds;
	}
	@Override
	public List<Long> needUpTopicByRole(Long packageId, Long roleId)
			throws Exception {
		List<Long> topicIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  packageId:{} roleId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,roleId);

			topicIds = topicRightDao.needUpTopicByRole(packageId,
					roleId);
		} catch (Exception e) {
			logger.error("method {} execute error, packageId:{} roleId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,roleId,e);
			throw e;
		}
		return topicIds;
	}
	@Override
	public List<Long> needUpTopicByGroup(Long packageId, Long groupId)
			throws Exception {
		List<Long> topicIds=new ArrayList<Long>();
		try {
			logger.info("Enter the :{} method  packageId:{} groupId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),packageId,groupId);

			topicIds=topicRightDao.needUpTopicByGroup(packageId, groupId);
		} catch (Exception e) {
			logger.error("method {} execute error, packageId:{} groupId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),packageId,groupId, e);
			throw e;
		}
		return topicIds;
	}
	@Transactional
	public void deleteByOwnerIds(List<Long> ownerIds, Integer type) throws Exception {
		logger.info("Enter the :{} method ownerId:{} type:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),ownerIds,type);
		if(ownerIds == null||type==null){
			logger.warn("arg ownerId is null");
			throw new IllegalArgumentException("arg ownerId is null");
		}
		try {
			topicRightDao.deleteByOwnerIds(ownerIds, type);
		} catch (Exception e) {
			logger.error("method {} execute error,ownerId:{} type:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),ownerIds,type,e);
			throw e;
		}
		logger.info("complete method, return void");
	}
	@Override
	public List<Long> findAuthenTopicIds(Long userId, boolean includeRole)
			throws Exception {
		try {
			logger.info(
					"Enter the :{} method  userId:{} includeRole:{} ",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole);
			List<Long> orgIds = topicRightDao.findAuthenTopicIds(userId,
					includeRole);
			List<Long> result = new ArrayList<Long>();
			result.addAll(orgIds);
			return result;
		} catch (Exception e) {
			logger.error(
					"method {} execute error, userId:{} includeRole:{}  Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole, e);
			throw e;
		}
	}
	@Override
	public List<String> findAuthenTopicCodes(Long userId, boolean includeRole)
			throws Exception {
		try {
			logger.info(
					"Enter the :{} method  userId:{} includeRole:{} ",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole);
			List<String> orgIds = topicRightDao.findAuthenTopicCodes(userId,
					includeRole);
			List<String> result = new ArrayList<String>();
			result.addAll(orgIds);
			return result;
		} catch (Exception e) {
			logger.error(
					"method {} execute error, userId:{} includeRole:{}  Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					userId, includeRole, e);
			throw e;
		}
	}

}
