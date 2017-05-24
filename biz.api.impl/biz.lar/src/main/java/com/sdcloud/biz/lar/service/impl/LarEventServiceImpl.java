package com.sdcloud.biz.lar.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarEvent;
import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.LarEventService;
import com.sdcloud.api.lar.service.MessageCenterService;
import com.sdcloud.biz.lar.dao.LarClientUserDao;
import com.sdcloud.biz.lar.dao.LarEventDao;
import com.sdcloud.biz.lar.util.XingeAppUtils;

@Service
@Transactional
public class LarEventServiceImpl extends BaseServiceImpl<LarEvent> implements LarEventService{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LarClientUserDao clientUserDao ;
	@Autowired
	private MessageCenterService messageCenterService;
	@Autowired
	private XingeAppUtils xingeAppUtils;
	@Autowired
	private LarEventDao  larEventDao;
	
	//执行处理事件,派发积分
	@Override
	public boolean deal(LarEvent larEvent,int level) {
		try {
			logger.info(
					"start method: boolean deal(LarEvent larEvent,int level, arg larEvent: "
							+ larEvent +", arg level: "+level);
			
			larEvent.setUpdateDate(new Date());
			larEventDao.update(larEvent);
			
			LarClientUser clientUser = clientUserDao.selectByPrimaryKey(larEvent.getClientUserId()+"");
			if(null == clientUser){
				logger.info("client is null, clientId is: " + larEvent.getClientUserId());
				return false;
			}
			clientUser.setNowPoints(clientUser.getNowPoints()+larEvent.getIntegral().intValue());
			clientUser.setHistoryPoints(clientUser.getHistoryPoints()+larEvent.getIntegral().intValue());
			int updatePoints = clientUserDao.updatePoints(clientUser);
			if(updatePoints<1){
				throw new RuntimeException("保存积分失败!");
			}
			
			String title = "环卫信息";
			String content = "亲，您上报的事件已经处理完毕，恭喜您获得了"+larEvent.getIntegral()+"积分奖励，期待您再次参与，为环境、无止境。";
			//推送
			XingeEntity xingeEntity = new XingeEntity();
			xingeEntity.setTitle(title);
			xingeEntity.setContent(content);
			xingeEntity.setAccount(clientUser.getId());
			xingeAppUtils.pushSingleAccount(xingeEntity,1);
			xingeAppUtils.pushSingleAccountIOS(xingeEntity,3);
			
			//保存消息中心
			MessageCenter t = new MessageCenter();
			t.setTitle(title);
			t.setDetail(content);
			t.setCustomerId(Long.parseLong(clientUser.getId()));
			messageCenterService.save(t );
			
			return true;
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			return false;
		}
	}

}
