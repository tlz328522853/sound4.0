package com.sdcloud.biz.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.SalesPackage;

/**
 * 
 * @author lms
 */
public interface SalesPackageDao {

	void insert(SalesPackage salesPackage);
	
	void delete(Long salesPackageId);

	void update(SalesPackage salesPackage);
	
	SalesPackage findById(@Param("salesPackageId") Long salesPackageId);

	List<SalesPackage> findAll();
	
	Long findUserSalesPackage(@Param("userId")Long userId);

}
