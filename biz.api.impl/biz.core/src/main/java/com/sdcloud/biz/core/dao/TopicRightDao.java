package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.TopicRight;

/**
 * 
 * @author lihuiquan
 */
public interface TopicRightDao {

	List<Long> findTopicIds(Long ownerId);

	void insert(@Param("topicRights") List<TopicRight> topicRights);

	void deleteByOwnerId(@Param("ownerId") Long ownerId);
	
	void deleteByOwnerIds(@Param("ownerIds") List<Long> ownerId,@Param("ownerType") Integer type);
	
	List<TopicRight> findTopicRight(Map<String,Object> param);
	
	List<TopicRight>  hasTopicRight(Map<String,Object> param);
	
	void deleteOwnerIdAndTopic(@Param("ownerType") Long ownerType,@Param("funIds") List<Long>funIds,@Param("ownerIds") List<Long> ownerId);
	
	List<Long> needUpTopicByPackage(@Param("packageId")Long packageId, @Param("userId")Long userId);
	
	List<Long> needUpTopicByRole(@Param("packageId")Long packageId, @Param("roleId")Long roleId);
	
	List<Long> needUpTopicByGroup(@Param("packageId")Long packageId, @Param("groupId")Long groupId);
	
	List<Long> findAuthenTopicIds(@Param("userId")Long userId,@Param("includeRole")boolean includeRole);
	
	List<String> findAuthenTopicCodes(@Param("userId")Long userId,@Param("includeRole")boolean includeRole);

	
	List<Long> findGroupIdsByTopicId(@Param("topicId")Long topicId);
	
	List<Long> findRoleIdsByTopicId(@Param("topicId")Long topicId);
	
	List<Long> findUserIdsTopicId(@Param("topicId")Long topicId);
}
