package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.framework.mqtt.Message;


/**
 * 
 * @author lihuiquan
 *
 */
public interface MessageDao {
	void insert(@Param("messages")List<Message> messages);
	
	void delete(@Param("messageIds") List<Long> messageIds);
	
	void update(Message message);
	
	List<Message> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
