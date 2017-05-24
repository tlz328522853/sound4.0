package com.sdcloud.biz.core.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sdcloud.api.core.entity.GroupUser;
import com.sdcloud.api.core.service.GroupUserService;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.framework.common.UUIDUtil;
@Service("groupUserService")
public class GroupUserServiceImple implements GroupUserService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	GroupUserDao groupUserDao;
	@Override
	public long getTotalCount(Map<String, Object> param)throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);

		try {
			return groupUserDao.getTotalCount(param);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
	}

	@Override
	public GroupUser insert(GroupUser groupUser)throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupUser);
		try {
			if(StringUtils.isEmpty(groupUser.getGroupUserId())){
				long id = -1;
				id = UUIDUtil.getUUNum();
				groupUser.setGroupUserId(id);;
			}
			groupUserDao.insert(groupUser);
			return groupUser;
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupUser,e);
			throw e;
		}
	}

	@Override
	public void deleteByUserId(List<Long> userIds)throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds);

		try {
			groupUserDao.deleteByUserId(userIds);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds,e);
			throw e;
		}
	}

	@Override
	public Long findGroupIdByUser(Long userId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);

		try {
			return groupUserDao.findGroupIdByUser(userId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}

	@Override
	public List<Long> findUserIdByGroup(Long groupId) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId);

		try {
			return groupUserDao.findUserIdByGroup(groupId);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,e);
			throw e;
		}
	}

	@Override
	public GroupUser hasGroupUser(Map<String, Object> param)throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),param);

		try {
			List<GroupUser> groupUsers=	groupUserDao.hasGroupUser(param);
			if(groupUsers!=null&&groupUsers.size()>0){
				return groupUsers.get(0);
			}
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),param,e);
			throw e;
		}
		return null;
	}


	public void deleteGroupUser(Long groupId, Long userId) throws Exception {
		logger.info("Enter the :{} method groupId:{},userId:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,userId);
		try {
			if(StringUtils.isEmpty(groupId)||StringUtils.isEmpty(userId)){
				logger.error("method {} execute error,groupId:{},userId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,userId,"参数有空值");
				throw new Exception("groupId or userId not be null!");
			}
			groupUserDao.deleteGroupUser(groupId, userId);
		} catch (Exception e) {
			logger.error("method {} execute error,groupId:{},userId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,userId,e);
			throw e;
		}
		
	}

}
