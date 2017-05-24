package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.Elescale;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_elescale 电子秤信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface ElescaleDao extends BaseDao<Elescale> {

	int countByElescaleId(@Param("mchid")String mchid);
	
	Elescale selectByElescaleId(@Param("mchid")String mchid);
	
	
}
