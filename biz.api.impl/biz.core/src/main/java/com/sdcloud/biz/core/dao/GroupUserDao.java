package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.GroupUser;

/**
 * 
 * @author lihuiquan
 */
public interface GroupUserDao {

	long getTotalCount(Map<String, Object> param);

	void insert(GroupUser groupUser);

	void deleteByUserId(@Param("userIds") List<Long> userIds);
	
	List<Long> findUserIdByGroups(@Param("groupIds")List<Long> groupIds);
	
	Long findGroupIdByUser(@Param("userId")Long userId);
	
	List<Long> findUserIdByGroup(@Param("groupIds")List<Long> groupIds);
	List<Long> findUserIdByGroup(@Param("groupId")Long groupId);
	
	List<GroupUser> hasGroupUser(Map<String, Object> param);
	
	void deleteGroupUser(@Param("groupId")Long groupId, @Param("userId")Long userId);

}
