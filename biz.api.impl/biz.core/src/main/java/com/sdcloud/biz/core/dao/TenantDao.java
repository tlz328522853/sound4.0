package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Tenant;

public interface TenantDao {

	void insert(Tenant tenant);

	void update(Tenant tenant);
	
	void delete(@Param("tenantId")Long tenantId);
	
	List<Tenant> findTenantByParam(Map<String, Object> param);
	void upgradePackag(@Param("tenantId")Long tenantId, @Param("packageId")Long packageId);

}
