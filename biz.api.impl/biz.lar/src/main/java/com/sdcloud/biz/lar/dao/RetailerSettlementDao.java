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

import com.sdcloud.api.lar.entity.RetailerSettlement;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface RetailerSettlementDao {
	
	long countRetailerSettlement(@Param("params") Map<String, Object> params);

	List<RetailerSettlement> selectRetailerSettlement(@Param("larPager") LarPager<RetailerSettlement> larPager);
	
	
}
