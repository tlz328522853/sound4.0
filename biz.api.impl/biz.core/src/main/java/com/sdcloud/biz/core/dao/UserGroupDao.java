package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.UserGroup;

public interface UserGroupDao {

	void insert(UserGroup userGroup);

	Long findParent(long userGroupId);

	String findChildren(long parentId);
	List<Long> findChildren(@Param("parentIds")List<Long> groupIds);
	
	
	List<UserGroup> findByParam(Map<String,Object> param);

	void deleteById(long delUserGroupId);

	void update(UserGroup updateUserGroup);

	List<UserGroup> findById(@Param("groupIds")List<Long> userGroupIds);

	List<UserGroup> findAll();
	
	List<String> findOwnerCodeByParam(Map<String,Object> param);
	
	List<Long> findNotFunctionGroupId(@Param("functionId")Long functionId);
	List<Long> findNotTopicGroupId(@Param("topicId")Long topicId);
}
