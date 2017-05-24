package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Org;

public interface OrgDao {

	void insert(Org org);

	Long findParent(long orgId);

	//String findChildren(long parentId); 

	List<Long> findNewChildren(@Param("owner") String owner);

	List<Org> findByParam(Map<String, Object> param);

	void deleteById(long delOrgId);

	void update(Org updateOrg);

	List<Org> findById(Map<String, Object> param);
	// List<Org> findById(@Param("orgIds")List<Long> orgIds);

	/**
	 * 返回子公司orgId，isSubCompany
	 * @param orgIds
	 * @return
	 */
	List<Map<String,Object>> findChildById(@Param("orgIds") List<Long> orgIds);

	List<Org> findAll(Map<String, Object> param);

	List<String> findOwnerCodeByParam(Map<String, Object> param);

	List<Org> findMonitorOrg(@Param("orgIds") List<Long> orgIds);

	/**
	 * 获取用户授权机构Id列表
	 * 
	 * @param userId
	 * @return
	 */
	List<Long> findAuthenOrgIds(@Param("userId") Long userId);
	/**
	 * 将部门orgId 设置为项目公司，原部门下的所有机构（包括自己）的companyId修改为自己的orgId
	 * @param orgId 原部门orgId
	 * @param ownerCode 原部门ownerCode
	 */
	void setOrgAsCompany(@Param("orgId")Long orgId, @Param("ownerCode")String ownerCode);
	/**
	 * 将项目公司orgId 设置为部门, orgId下的所有子机构原来是以orgId为公司Id的，修改为orgId修改后的公司Id
	 * @param orgId 原项目公司的orgId
	 * @param companyId 原项目公司的
	 */
	void setOrgAsDepart(@Param("orgId")Long orgId, @Param("companyId")Long companyId);
	long getTotalCount(Map<String, Object> param);
	List<Org> findAllBy(Map<String, Object> param);
	
	int findTotalProjectByOrgId(@Param("orgId") Long orgId);
}
