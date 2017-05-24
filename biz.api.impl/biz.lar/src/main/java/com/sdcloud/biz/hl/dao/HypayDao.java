package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.Hypay;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_hypay 
 * @author jiazc
 * @date 2017-05-15
 * @version 1.0
 */
@Repository
public interface HypayDao extends BaseDao<Hypay> {

	int countByPayId(@Param("payId") int payId);
	
	
}
