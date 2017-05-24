package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;



import com.sdcloud.api.envsanitation.entity.InventoryAccounting;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryAccountingDao {
	void insert(@Param("inventoryAccountings")List<InventoryAccounting> inventoryAccountings);
	void deleteByOrderId(@Param("orderIds") List<Long> orderIds);
	void delete(@Param("accountingIds") List<Long> inventoryAccountingIds);
	
	void update(InventoryAccounting inventoryAccounting);
	
	List<InventoryAccounting> findAllBy(Map<String, Object> param);
	List<InventoryAccounting> findByOrderId(@Param("orderId") Long orderId);
	List<InventoryAccounting> findByOrderIdHistory(@Param("orderId") Long orderId);
	long getTotalCountBeginning(Map<String, Object> param);
	List<InventoryAccounting> findAllByBeginning(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	
}
