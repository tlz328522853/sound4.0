package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.sdcloud.api.envsanitation.entity.InventoryHistory;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryHistoryDao {
	void insert(@Param("inventoryHistorys")List<InventoryHistory> inventoryHistorys);
	
	void delete(@Param("inventoryHistoryIds") List<Long> inventoryHistoryIds);
	void deleteByOrdeId(@Param("orderIds") List<Long> orderIds);
	void update(InventoryHistory inventoryHistory);
	
	List<InventoryHistory> findAllBy(Map<String, Object> param);
	
	List<InventoryHistory> findOutAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
