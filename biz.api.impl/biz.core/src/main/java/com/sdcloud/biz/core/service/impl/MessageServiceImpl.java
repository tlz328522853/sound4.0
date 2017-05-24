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





import com.sdcloud.framework.mqtt.Message;
import com.sdcloud.api.core.service.MessageService;
import com.sdcloud.biz.core.dao.MessageDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageDao messageDao;
	
	

	@Transactional
	public void insert(List<Message> messages) throws Exception {
		logger.info("start method: long insert(Message Message), arg Message: "
				+ messages);

		if (messages==null||messages.size()<=0) {
			logger.warn("arg Message is null");
			throw new IllegalArgumentException("arg Message is null");
		}
		for (Message message : messages) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			message.setMessageId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					messageDao.insert(messages);
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
	public void delete(List<Long> messages) throws Exception {
		logger.info("start method: void delete(List<Long> Messages), arg Messages: "
				+ messages);
		if (messages == null || messages.size() == 0) {
			logger.warn("arg Messages is null or size=0");
			throw new IllegalArgumentException("arg Messages is null or size=0");
		}
		try {
			
			messageDao.delete(messages);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Message message) throws Exception {
		logger.info("start method: void update(Message Message), arg Message: "
				+ message);
		if (message == null || message.getMessageId() == null) {
			logger.warn("arg Message is null or Message 's messageId is null");
			throw new IllegalArgumentException("arg Message is null or Message 's addOidId is null");
		}
		try {
			
			messageDao.update(message);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Message> findBy(Pager<Message> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<Message> findBy(Pager<Message> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<Message>();
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
				long totalCount = messageDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Message> Messages = messageDao.findAllBy(param);

			pager.setResult(Messages);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
