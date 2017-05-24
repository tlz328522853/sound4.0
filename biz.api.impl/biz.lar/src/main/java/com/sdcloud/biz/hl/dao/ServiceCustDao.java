package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.ServiceCust;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_service_cust 便民服务表
 * @author jiazc
 * @date 2017-05-16
 * @version 1.0
 */
@Repository
public interface ServiceCustDao extends BaseDao<ServiceCust> {

	int countByScId(@Param("scId") Integer scId);
	
	ServiceCust selectByScId(@Param("scId") Integer scId);
	
	
}
