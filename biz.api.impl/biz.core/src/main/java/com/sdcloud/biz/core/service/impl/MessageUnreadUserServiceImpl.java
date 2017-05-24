package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;









import com.sdcloud.framework.mqtt.MessageUnreadUser;
import com.sdcloud.api.core.service.MessageUnreadUserService;
import com.sdcloud.biz.core.dao.MessageUnreadUserDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("messageUnreadUserService")
public class MessageUnreadUserServiceImpl implements MessageUnreadUserService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageUnreadUserDao messageUnreadUserDao;
	
	
	@Transactional
	public void insert(List<MessageUnreadUser> messageUnreadUsers) throws Exception {
		logger.info("start method: long insert(MessageUnreadUser MessageUnreadUser), arg MessageUnreadUser: "
				+ messageUnreadUsers);

		if (messageUnreadUsers==null||messageUnreadUsers.size()<=0) {
			logger.warn("arg MessageUnreadUser is null");
			throw new IllegalArgumentException("arg MessageUnreadUser is null");
		}
		for (MessageUnreadUser messageUnreadUser : messageUnreadUsers) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			messageUnreadUser.setMuuserId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					messageUnreadUserDao.insert(messageUnreadUsers);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> messageUnreadUsers) throws Exception {
		logger.info("start method: void delete(List<Long> MessageUnreadUsers), arg MessageUnreadUsers: "
				+ messageUnreadUsers);
		if (messageUnreadUsers == null || messageUnreadUsers.size() == 0) {
			logger.warn("arg MessageUnreadUsers is null or size=0");
			throw new IllegalArgumentException("arg MessageUnreadUsers is null or size=0");
		}
		try {
			
			messageUnreadUserDao.delete(messageUnreadUsers);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(MessageUnreadUser messageUnreadUser) throws Exception {
		logger.info("start method: void update(MessageUnreadUser MessageUnreadUser), arg MessageUnreadUser: "
				+ messageUnreadUser);
		if (messageUnreadUser == null || messageUnreadUser.getMuuserId() == null) {
			logger.warn("arg MessageUnreadUser is null or MessageUnreadUser 's messageUnreadUserId is null");
			throw new IllegalArgumentException("arg MessageUnreadUser is null or MessageUnreadUser 's addOidId is null");
		}
		try {
			
			messageUnreadUserDao.update(messageUnreadUser);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<MessageUnreadUser> findBy(Pager<MessageUnreadUser> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<MessageUnreadUser> findBy(Pager<MessageUnreadUser> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<MessageUnreadUser>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("assetId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = messageUnreadUserDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<MessageUnreadUser> MessageUnreadUsers = messageUnreadUserDao.findAllBy(param);

			pager.setResult(MessageUnreadUsers);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public int findUnreadMessageCount(Long userId) throws Exception {
		int count;
		try {
			logger.info("Enter the :{} method  userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId);
			count = this.messageUnreadUserDao.findUnreadMessageCount(userId);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			throw e;
		}
		return count;
	}

	@Override
	public void redMessage(Long userId,List<Long> messageIds) throws Exception {
		
		try {
			logger.info("Enter the :{} method  userId:{} messageIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId,messageIds);
			messageUnreadUserDao.redMessage(userId,messageIds);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} messageIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId,messageIds, e);
			throw e;
		}
		
	}

	@Override
	public void delMessage(Long userId, List<Long> messageIds) throws Exception {
		try {
			logger.info("Enter the :{} method  userId:{} messageIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId,messageIds);
			messageUnreadUserDao.delMessage(userId,messageIds);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} messageIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId,messageIds, e);
			throw e;
		}
		
	}

}
