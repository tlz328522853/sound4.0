package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Role;

public interface RoleDao {

	long insert(Role role);

	/**
	 * 获取用户所在的角色组
	 * @param userId
	 * @return
	 */
	List<Role> findByUser(long userId);

	void deleteById(long roleId);

	void update(Role role);

	/**
	 * 设置角色下的用户，用于批量修改角色下的成员
	 * @param roleId
	 * @param userIds
	 */
	void setUsers(@Param("roleId")Long roleId, @Param("userIds")List<Long> userIds);

	/**
	 * 清空给定角色下的所有用户，用来批量修改角色下的用户
	 * @param roleId
	 */
	void clearUser(@Param("roleId")Long roleId);

	List<Role> findAll();
	
	List<Role> findByParam(Map<String,Object> param);
	
	Role findRoleParent(@Param("roleId")Long roleId);
	List<Role> findRoleChild(@Param("roleIds")List<Long> roleIds);
	List<Role> findRoleByUserGroup(@Param("userGroupId") Long userGroupId);
	List<Role> findRoleByUserGroupPid(@Param("userGroupPid") Long userGroupPid);
	List<Long> findRootRoleByGroup(@Param("groupIds") List<Long> groupIds);
	Role findRoleById(@Param("roleId")Long roleId);
	Long hasRoleByName(@Param("roleName")String roleName, @Param("groupId")Long groupId);
	List<Long> findNotFunctionRootRoleId(@Param("functionId")Long functionId);
	List<Long> findNotTopicRootRoleId(@Param("topicId")Long topicId);
	
}
