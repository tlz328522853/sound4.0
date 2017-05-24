package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.OrgRight;

/**
 * 
 * @author lms
 */
public interface OrgRightDao {

	List<Long> findOrgIds(Long ownerId);
	
	List<Long> findOrgIdsByGroup(@Param("groupId")Long groupId);
	
	void insert(@Param("orgRights") List<OrgRight> orgRights);

	void deleteByOwnerId(@Param("ownerId") Long ownerId);
	
	void deleteById(@Param("orgRightIds") List Ids);
	
	void deleteUserRoleRight(@Param("orgId") Long orgId,@Param("groupIds") List<Long> groupIds,@Param("roleIds") List<Long> roleIds, @Param("userIds")List<Long> userIds);
	
	List<OrgRight> findOrgRight(Map<String,Object> param);
	
	List<Long> findAuthenOrgIds(@Param("userId")Long userId, @Param("includeRole")boolean includeRole,
			@Param("includeDepartment")boolean includeDepartment);
	List<Long> findChildById(@Param("orgIds") List<Long> orgIds,@Param("includeDepartment")boolean includeDepartment);
	
	Long hasAuthen(@Param("userId") Long userId,@Param("orgId") Long orgId, @Param("includeRole")boolean includeRole);
}
