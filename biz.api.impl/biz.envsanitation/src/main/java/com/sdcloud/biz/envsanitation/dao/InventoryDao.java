package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.sdcloud.api.envsanitation.entity.Inventory;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryDao {
	void insert(@Param("inventorys")List<Inventory> inventorys);
	
	void delete(@Param("inventoryIds") List<Long> inventoryIds);
	
	void update(Inventory inventory);
	
	List<Inventory> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
