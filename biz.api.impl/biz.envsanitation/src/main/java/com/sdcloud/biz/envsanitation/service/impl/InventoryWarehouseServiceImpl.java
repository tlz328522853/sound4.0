package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.Inventory;
import com.sdcloud.api.envsanitation.entity.InventoryWarehouse;
import com.sdcloud.api.envsanitation.service.InventoryWarehouseService;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.biz.envsanitation.dao.InventoryWarehouseDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryWarehouseService")
public class InventoryWarehouseServiceImpl implements InventoryWarehouseService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryWarehouseDao inventoryWarehouseDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<InventoryWarehouse> inventoryws) throws Exception {
		logger.info("start method: long insert(InventoryWarehouse InventoryWarehouse), arg InventoryWarehouse: "
				+ inventoryws);

		if (inventoryws==null||inventoryws.size()<=0) {
			logger.warn("arg InventoryWarehouse is null");
			throw new IllegalArgumentException("arg InventoryWarehouse is null");
		}
		for (InventoryWarehouse inventory : inventoryws) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventory.setInventorywId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryWarehouseDao.insert(inventoryws);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> inventoryws) throws Exception {
		logger.info("start method: void delete(List<Long> InventoryWarehouses), arg InventoryWarehouses: "
				+ inventoryws);
		if (inventoryws == null || inventoryws.size() == 0) {
			logger.warn("arg InventoryWarehouses is null or size=0");
			throw new IllegalArgumentException("arg InventoryWarehouses is null or size=0");
		}
		try {
			
			inventoryWarehouseDao.delete(inventoryws);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(InventoryWarehouse inventoryw) throws Exception {
		logger.info("start method: void update(InventoryWarehouse InventoryWarehouse), arg InventoryWarehouse: "
				+ inventoryw);
		if (inventoryw == null || inventoryw.getInventorywId() == null) {
			logger.warn("arg InventoryWarehouse is null or InventoryWarehouse 's inventoryId is null");
			throw new IllegalArgumentException("arg InventoryWarehouse is null or InventoryWarehouse 's addOidId is null");
		}
		try {
			
			inventoryWarehouseDao.update(inventoryw);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Inventory> findBy(Pager<Inventory> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<InventoryWarehouse> findBy(Pager<InventoryWarehouse> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<Inventory>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("assetId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = inventoryWarehouseDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Inventory> inventoryWarehouses = inventoryWarehouseDao.findAllBy(param);

			pager.setResult(inventoryWarehouses);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public void deletes(Long dicId, List<Long> inventoryIds) throws Exception {
		try {
			logger.info("Enter the :{} method  dicId:{}  inventoryIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),dicId,inventoryIds);

			inventoryWarehouseDao.deletes(dicId, inventoryIds);
		} catch (Exception e) {
			logger.error("method {} execute error, dicId:{}  inventoryIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),dicId,inventoryIds, e);
			throw e;
		}
	}

}
