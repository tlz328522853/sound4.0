package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Topic;

/**
 * 
 * @author lihuiquan
 */
public interface TopicDao {
	
	void insert(Topic topic);
	
	void update(Topic topic);
	
	void delete(@Param("topicIds") List<Long> topicIds);

	void deleteByModuleId(@Param("moduleIds") List<Long> moduleIds);

	List<Topic> findByModuleId(Map<String, Object> params);
	
	List<Topic> findByPid(@Param("pid") Long pid);

	List<Topic> findById(@Param("topicIds") List<Long> topicIds);
	
	List<Topic> findAll(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	
	List<Long> findTopicIdByModuleId(Map<String, Object> params);
	
	List<Topic> findAuthenTopic(@Param("moduleId")Long moduleId);
}
