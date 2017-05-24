package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;




import com.sdcloud.api.envsanitation.entity.Inventory;
import com.sdcloud.api.envsanitation.entity.InventoryWarehouse;
/**
 * 
 * @author lihuiquan
 *
 */
public interface InventoryWarehouseDao {
	void insert(@Param("inventoryws")List<InventoryWarehouse> inventoryws);
	
	void delete(@Param("inventorywIds") List<Long> inventorywIds);
	
	void update(InventoryWarehouse inventoryw);
	
	List<Inventory> findAllBy(Map<String, Object> param);
	
	void deletes(@Param("dicId")Long dicId,@Param("inventoryIds")List<Long> inventoryIds) ;
	
	long getTotalCount(Map<String, Object> param);
}
