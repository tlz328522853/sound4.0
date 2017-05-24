package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.sdcloud.api.envsanitation.entity.InventoryOrder;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryOrderDao {
	void insert(@Param("inventoryOrders")List<InventoryOrder> inventoryOrders);
	
	void delete(@Param("inventoryOrderIds") List<Long> inventoryOrderIds);
	
	void update(InventoryOrder inventoryOrder);
	
	List<InventoryOrder> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	List<InventoryOrder> findInventoryOrderByIds(@Param("orderIds")List<Long> orderIds);
}
