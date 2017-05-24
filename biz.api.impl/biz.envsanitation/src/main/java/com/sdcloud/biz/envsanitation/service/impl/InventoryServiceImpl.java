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
import com.sdcloud.api.envsanitation.service.InventoryService;
import com.sdcloud.biz.envsanitation.dao.InventoryDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryDao inventoryDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<Inventory> inventorys) throws Exception {
		logger.info("start method: long insert(Inventory Inventory), arg Inventory: "
				+ inventorys);

		if (inventorys==null||inventorys.size()<=0) {
			logger.warn("arg Inventory is null");
			throw new IllegalArgumentException("arg Inventory is null");
		}
		for (Inventory inventory : inventorys) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventory.setInventoryId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryDao.insert(inventorys);
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
	public void delete(List<Long> inventorys) throws Exception {
		logger.info("start method: void delete(List<Long> Inventorys), arg Inventorys: "
				+ inventorys);
		if (inventorys == null || inventorys.size() == 0) {
			logger.warn("arg Inventorys is null or size=0");
			throw new IllegalArgumentException("arg Inventorys is null or size=0");
		}
		try {
			
			inventoryDao.delete(inventorys);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Inventory inventory) throws Exception {
		logger.info("start method: void update(Inventory Inventory), arg Inventory: "
				+ inventory);
		if (inventory == null || inventory.getInventoryId() == null) {
			logger.warn("arg Inventory is null or Inventory 's inventoryId is null");
			throw new IllegalArgumentException("arg Inventory is null or Inventory 's addOidId is null");
		}
		try {
			
			inventoryDao.update(inventory);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Inventory> findBy(Pager<Inventory> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<Inventory> findBy(Pager<Inventory> pager, Map<String, Object> param), arg pager: "
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
				long totalCount = inventoryDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Inventory> Inventorys = inventoryDao.findAllBy(param);

			pager.setResult(Inventorys);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
