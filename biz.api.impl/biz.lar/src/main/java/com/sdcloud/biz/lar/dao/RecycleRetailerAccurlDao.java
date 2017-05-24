package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleRetailerAccurl;

/**
 * lar_recycle_retailer_accurl 销售商附件
 * @author TLZ
 * @date 2016-12-28
 * @version 1.0
 */
@Repository
public interface RecycleRetailerAccurlDao extends BaseDao<RecycleRetailerAccurl> {

	List<RecycleRetailerAccurl> findById(Long id);
	
	
}
