package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ShipmentExpress;
@Repository
public interface ShipmentExpressDao extends BaseDao<ShipmentExpress>{
	
	Long getCountByExpressAndOrgId(@Param("id")Long id,@Param("express")Long express, @Param("org")Long org);

}
