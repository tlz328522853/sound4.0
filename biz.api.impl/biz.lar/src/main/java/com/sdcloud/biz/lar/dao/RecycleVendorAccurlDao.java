package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleVendorAccurl;

/**
 * lar_recycle_vendor_accurl 供应商附件路径
 * @author TLZ
 * @date 2016-12-21
 * @version 1.0
 */
@Repository
public interface RecycleVendorAccurlDao extends BaseDao<RecycleVendorAccurl> {
	
	int updateBatch(RecycleVendorAccurl recycleVendorAccurl);

	List<RecycleVendorAccurl> findById(@Param("id")Long id);
}
