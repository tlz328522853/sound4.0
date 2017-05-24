package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.UserRole;

/**
 * 
 * @author lms
 */
public interface UserRoleDao {

	long getTotalCount(Map<String, Object> param);

	void insert(UserRole userRole);

	void deleteByUserId(@Param("userIds") List<Long> userIds);
	
	List<Long> findUserIdsByRoleIds(@Param("roleIds")List<Long> roleIds);

}
