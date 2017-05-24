package com.sdcloud.biz.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.SalesPackageModule;

/**
 * 
 * @author lms
 */
public interface SalesPackageModuleDao {

//	List<SalesPackageModule> findBySalesPackageId(Long salesPackageId);

	void insert(@Param("salesPackageModules") List<SalesPackageModule> salesPackageModules);

	void deleteBySalesPackageId(@Param("salesPackageId") Long salesPackageId);
	
	List<Long> findSalesPackageIdByModelId(@Param("moduleId")Long moduleId);
	void  deleteSalesPackageIdAndModelId(@Param("salesPackageId") Long salesPackageId,@Param("moduleIds")List<Long> moduleIds);
}
