package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.framework.mqtt.MessageUnreadUser;


/**
 * 
 * @author lihuiquan
 *
 */
public interface MessageUnreadUserDao {
	void insert(@Param("messageUnreadUsers")List<MessageUnreadUser> messageUnreadUsers);
	
	void delete(@Param("muuserIds") List<Long> messageUnreadUserIds);
	
	void update(MessageUnreadUser messageUnreadUser);
	
	List<MessageUnreadUser> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	int findUnreadMessageCount(@Param("userId")Long userId);
	
	
	void redMessage(@Param("userId")Long userId,@Param("messageIds") List<Long> messageIds);
	void delMessage(@Param("userId")Long userId,@Param("messageIds") List<Long> messageIds);
}
