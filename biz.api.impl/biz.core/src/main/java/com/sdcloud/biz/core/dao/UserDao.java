package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author czz
 *
 */
public interface UserDao {

	

	List<User> findByEmployee(@Param("employeeIds")List<Long> employeeIds);
	List<Long> findUserIdByEmployee(@Param("employeeIds")List<Long> employeeIds);
	Long insert(User user);

	void deleteById(@Param("userIds") List<Long> userIds);

	void update(User user);

	/*
	 * 分页查询部门下的用户
	 */
	/**
	 * 通过组织部门的id列表，查找用户
	 * @param orgIds 组织id列表
	 * @return 用户列表
	 */
//	List<User> findByOrg(@Param("orgIds")List<Long> orgIds, @Param("pager")Pager<User> pager);

//	long countByOrg(@Param("orgIds")List<Long> orgIds);
	
	//map.put("roleId", roleId);
//	List<User> findByRole(Long roleId, Map<String, Object> map);
	List<User> findByRole(Map<String, Object> map);

	User findByPwd(@Param("userName")String userName, @Param("pwd")String pwd);


	User findByUserId(Long userId);
	
	User findByUser(Long userId);
	
	List<User> findUserByEmployee(Employee employee);
	
	User findByUserName(@Param("userName")String userName);

	Long isTenantRootUser(@Param("userId")Long userId);
	
	List<User> findByUserIds(@Param("userIds")List<Long> userIds);
	
	List<Long> findUserByTenantId(@Param("tenantIds")List<Long> tenantIds);
	/**
	 * 获取机构下的用户，如果是公司则包含机构下部门的用户
	 * @param orgIds
	 * @return
	 */
	List<Long> findUserByOrgId(@Param("orgIds")List<Long> orgIds);
	
	
}
