package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.LyRecycler;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_ly_recycler 联运回收机信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface LyRecyclerDao extends BaseDao<LyRecycler> {

	/**
	 * 根据联运回收机信息mchid统计数量
	 * @author jzc 2017年5月11日
	 * @param reserveId
	 * @return
	 */
	long countByMchid(@Param("mchid") String mchid);
	
	LyRecycler selectByMchid(@Param("mchid") String mchid);
	
	
}
