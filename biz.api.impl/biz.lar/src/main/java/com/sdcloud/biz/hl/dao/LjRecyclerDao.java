package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.LjRecycler;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_lj_recycler 浪尖回收机
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface LjRecyclerDao extends BaseDao<LjRecycler> {

	int countByMchid(@Param("mchid") String mchid);
	
	LjRecycler selectByMchid(@Param("mchid") String mchid);
	
	
}
