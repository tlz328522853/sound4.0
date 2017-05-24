package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.PortalInfo;

public interface PortalInfoDao {

	PortalInfo find(Map<String, Object> param);
	
	List<PortalInfo> findAll(@Param("orgIds") List<Long> orgIds);

}
