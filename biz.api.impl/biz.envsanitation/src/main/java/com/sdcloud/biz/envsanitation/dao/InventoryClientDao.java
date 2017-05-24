package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.sdcloud.api.envsanitation.entity.InventoryClient;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryClientDao {
	void insert(@Param("inventoryClients")List<InventoryClient> inventoryClients);
	
	void delete(@Param("clientIds") List<Long> inventoryClientIds);
	
	void update(InventoryClient inventoryClient);
	
	List<InventoryClient> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
