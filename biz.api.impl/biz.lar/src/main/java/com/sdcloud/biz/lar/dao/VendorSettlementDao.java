package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 *   creatUser dingx
 *   createDate 2016-12-09
 *   function 供货商结算统计
 */
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.VendorSettlement;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface VendorSettlementDao {
	
	long countVendorSettlement(@Param("params") Map<String, Object> params);

	List<VendorSettlement> selectVendorSettlement(@Param("larPager") LarPager<VendorSettlement> larPager);
	
	
}
