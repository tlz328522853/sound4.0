package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleVendor;

/**
 * lar_recycle_vendor 供应商
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@Repository
public interface RecycleVendorDao extends BaseDao<RecycleVendor> {
	
	void updateEnable();

	RecycleVendor existByVendor(@Param("mechanismId") Long mechanismId,@Param("vendor") String vendor);

	RecycleVendor existByVendorShort(@Param("mechanismId") Long mechanismId,@Param("vendorShort") String vendorShort);

	int selectById(Long id);


}
