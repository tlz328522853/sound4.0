package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleRetailer;

/**
 * lar_recycle_retailer 销售客户管理
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@Repository
public interface RecycleRetailerDao extends BaseDao<RecycleRetailer> {

	int updateAccurlId(Long retailerId);


	RecycleRetailer existByRetailer(@Param("mechanismId") Long mechanismId,@Param("retailer") String retailer);


	RecycleRetailer existByRetailerShort(@Param("mechanismId") Long mechanismId,@Param("retailerShort") String retailerShort);
	
}
