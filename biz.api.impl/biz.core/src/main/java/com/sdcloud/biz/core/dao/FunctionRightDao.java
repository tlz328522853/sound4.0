package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.FunctionRight;

/**
 * 
 * @author lms
 */
public interface FunctionRightDao {

	List<Long> findFuncIds(Long ownerId);

	void insert(@Param("functionRights") List<FunctionRight> functionRights);

	void deleteByOwnerId(@Param("ownerId") Long ownerId);
	
	List<FunctionRight> findFunctionRight(Map<String,Object> param);
	
	List<FunctionRight>  hasFunctionRight(Map<String,Object> param);
	
	void deleteOwnerIdAndFunction(@Param("ownerType") Long ownerType,@Param("funIds") List<Long>funIds,@Param("ownerIds") List<Long> ownerId);
	
	List<Long> needUpFunctionByPackage(@Param("packageId")Long packageId, @Param("userId")Long userId);
	
	List<Long> needUpFunctionByRole(@Param("packageId")Long packageId, @Param("roleId")Long roleId);
	
	List<Long> needUpFunctionByGroup(@Param("packageId")Long packageId, @Param("groupId")Long groupId);
	
	List<FunctionRight> checkGroupRoleAuthrity(@Param("ownerType")Integer ownerType,
			@Param("ownerIds")List<Long> ownerIds, @Param("funcId")Long funcId);
}
