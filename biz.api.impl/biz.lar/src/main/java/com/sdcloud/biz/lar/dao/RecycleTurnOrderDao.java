package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sdcloud.api.lar.entity.RecycleTurnOrder;

public interface RecycleTurnOrderDao extends BaseDao<RecycleTurnOrder>{
	 List<RecycleTurnOrder> getByOrderId(@Param("orderId") Long orderId);
	 int updateState(@Param("params") Map<String, Object> params, @Param("id") Long id);//, @Param("id") Long id
	 //根据订单号查询
	 @Select("select id from `lar_recycle_trunorder` where order_no=#{orderNo}")
	 Long getByOrderNo(@Param("orderNo") String orderNo);
}
