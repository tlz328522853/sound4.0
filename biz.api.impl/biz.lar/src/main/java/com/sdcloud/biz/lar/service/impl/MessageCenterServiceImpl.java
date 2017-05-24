package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.service.MessageCenterService;
import com.sdcloud.biz.lar.dao.MessageCenterDao;

@Service
public class MessageCenterServiceImpl extends BaseServiceImpl<MessageCenter> implements MessageCenterService{

	@Autowired
	private MessageCenterDao messageCenterDao;
	
	@Override
	@Transactional(readOnly=false)
	public boolean batchSave(List<MessageCenter> list) {
		return messageCenterDao.batchSave(list)>0;
	}
	
}
