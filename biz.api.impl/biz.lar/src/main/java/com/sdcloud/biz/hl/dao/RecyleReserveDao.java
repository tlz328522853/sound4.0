package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.RecyleReserve;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_recyle_reserve 上门回收预约
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface RecyleReserveDao extends BaseDao<RecyleReserve> {

	/**
	 * 根据回收预约reserveId统计数量
	 * @author jzc 2017年5月11日
	 * @param reserveId
	 * @return
	 */
	long countByReserveId(@Param("reserveId") Integer reserveId);
	
	
}
