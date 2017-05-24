package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.framework.mqtt.Message;
import com.sdcloud.framework.mqtt.MessageUnreadUser;
import com.sdcloud.api.core.service.SendMessageService;
import com.sdcloud.biz.core.dao.GroupUserDao;
import com.sdcloud.biz.core.dao.MessageDao;
import com.sdcloud.biz.core.dao.MessageUnreadUserDao;
import com.sdcloud.biz.core.dao.TopicRightDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.biz.core.dao.UserRoleDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.util.MqttMessageTool;
import com.sdcloud.framework.util.MqttMessageValue;

/**
 * 
 * @author lihuiquan
 */
@Service("sendMessageService")
public class SendMessageServiceImpl implements SendMessageService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private MessageUnreadUserDao messageUnreadUserDao;
	
	@Autowired
	private TopicRightDao topicRightDao;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	GroupUserDao groupUserDao;
	
	@Autowired
	private MqttMessageTool mqttMessageTool;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public void sendTopicMessage(Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);

			Long topicId = Long.valueOf(message.getTopic());
			List<Long> groupIds = topicRightDao.findGroupIdsByTopicId(topicId);
			logger.info("Subscribe to topic groupIds:{}",groupIds);
			List<Long> roleIds = this.topicRightDao
					.findRoleIdsByTopicId(topicId);
			logger.info("Subscribe to topic roleIds:{}",roleIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.topicRightDao.findUserIdsTopicId(topicId);
			List<Long> userIdby = groupUserDao.findUserIdByGroup(groupIds);
			if (userIdby != null && userIdby.size() > 0) {
				userIds.addAll(userIdby);
				userIdby.clear();
			}
			userIdby = this.userRoleDao.findUserIdsByRoleIds(roleIds);
			if (userIdby != null && userIdby.size() > 0) {
				userIds.addAll(userIdby);
			}
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				mqttMessageTool.sendMessage(message.getTopic(), MqttMessageValue.hasMessage);
			}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		
	}
	@Transactional
	public void insertMessageUnreadUser(List<Long> userIds,Message message){
		try {
			List<MessageUnreadUser> messageUnreadUsers=new ArrayList<MessageUnreadUser>();
			long messageId = -1;
			messageId = UUIDUtil.getUUNum();
			message.setMessageId(messageId);
			message.setCreateTime(new Date());
			List<Message> messages=new  ArrayList<Message>();
			messages.add(message);
		
			logger.info("Enter the :{} method  userIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userIds);

			this.messageDao.insert(messages);
			logger.info("Enter the :{} method  insert message successful", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			if(userIds!=null){
				for (Long userId : userIds) {
					MessageUnreadUser mu=new MessageUnreadUser();
					mu.setMessageId(messageId);
					mu.setMuuserId( UUIDUtil.getUUNum());
					mu.setRedState(1);
					mu.setTenantId(message.getTenantId());
					mu.setUserId(userId);
					messageUnreadUsers.add(mu);
				}
				this.messageUnreadUserDao.insert(messageUnreadUsers);
			}
			
		} catch (Exception e) {
			logger.error("method {} execute error, userIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userIds, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendUserMessage(List<Long> userIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);

			if (userIds.size() > 0) {
				insertMessageUnreadUser(userIds, message);
				for (Long userId : userIds) {
					mqttMessageTool.sendMessage(userId.toString(),
							MqttMessageValue.hasMessage);
				}
				
			}
		} catch (Exception e) {
			logger.error("method {} execute error, :{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendRoleMessage(List<Long> roleIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			logger.info("Subscribe to topic roleIds:{}",roleIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.userRoleDao.findUserIdsByRoleIds(roleIds);
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				for (Long roleId : roleIds) {
					mqttMessageTool.sendMessage(roleId.toString(), MqttMessageValue.hasMessage);
				}
							}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendTenantIdMessage(List<Long> tenantIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			logger.info("Subscribe to topic tenantIds:{}",tenantIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.userDao.findUserByTenantId(tenantIds);
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				for (Long tenantId : tenantIds) {
					mqttMessageTool.sendMessage(tenantId.toString(), MqttMessageValue.hasMessage);
				}
							}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendUserGroupMessage(List<Long> groupIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			logger.info("Subscribe to topic groupIds:{}",groupIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.groupUserDao.findUserIdByGroup(groupIds);
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				for (Long groupId : groupIds) {
					mqttMessageTool.sendMessage(groupId.toString(), MqttMessageValue.hasMessage);
				}
							}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendOrgMessage(List<Long> orgIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			logger.info("Subscribe to topic orgIds:{}",orgIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.userDao.findUserByOrgId(orgIds);
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				for (Long orgId : orgIds) {
					mqttMessageTool.sendMessage(orgId.toString(), MqttMessageValue.hasMessage);
				}
							}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendEmployeeMessage(List<Long> employeeIds, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			logger.info("Subscribe to topic employeeIds:{}",employeeIds);
			List<Long> userIds = new ArrayList<Long>();
			userIds = this.userDao.findUserIdByEmployee(employeeIds);
			if(userIds.size()>0){
				insertMessageUnreadUser(userIds,message);
				for (Long employeeId : employeeIds) {
					mqttMessageTool.sendMessage(employeeId.toString(), MqttMessageValue.hasMessage);
				}
							}
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

	@Override
	public void sendOnlineMessage(int type, Message message) {
		try {
			logger.info("Enter the :{} method  message:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),message);
			mqttMessageTool.sendMessage("online", MqttMessageValue.hasMessage);
			message.setTopic("online");
			if(type==1){
				insertMessageUnreadUser(null, message);
			}
			
		} catch (Exception e) {
			logger.error("method {} execute error, message:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),message, e);
			throw e;
		}
		logger.info("Enter the :{} method  end", Thread.currentThread()
				.getStackTrace()[1].getMethodName());
	}

}
