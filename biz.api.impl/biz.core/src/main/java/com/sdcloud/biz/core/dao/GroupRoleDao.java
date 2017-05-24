package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.GroupRole;

/**
 * 
 * @author lihuiquan
 */
public interface GroupRoleDao {

	long getTotalCount(Map<String, Object> param);

	void insert(GroupRole groupRole);

	void deleteByRoleId(@Param("roleIds") List<Long> roleIds);
	
	Long findGroupIdByRole(@Param("roleId")Long roleId);
	
	List<Long> findRoleIdByGroup(@Param("groupId")Long groupId);
	
	List<Long> findAuthenRoleIdByGroup(@Param("groupIds")List<Long> groupIds);
	
	List<GroupRole> hasGroupRole(Map<String, Object> param);
	
	void deleteGroupRole(@Param("groupId")Long groupId, @Param("roleId")Long roleId);

}
