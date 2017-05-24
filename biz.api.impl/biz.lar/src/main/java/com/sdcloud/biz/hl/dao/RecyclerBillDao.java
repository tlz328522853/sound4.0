package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.RecyclerBill;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_recycler_bill 回收机用户保修单
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface RecyclerBillDao extends BaseDao<RecyclerBill> {

	/**
	 * 根据回收机用户保修单billId统计数量
	 * @author jzc 2017年5月11日
	 * @param reserveId
	 * @return
	 */
	long countByBillId(@Param("billId") Integer billId);
	
	
}
