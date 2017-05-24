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

import com.sdcloud.api.envsanitation.entity.InventoryHistory;
import com.sdcloud.api.envsanitation.service.InventoryHistoryService;
import com.sdcloud.biz.envsanitation.dao.InventoryHistoryDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryHistoryService")
public class InventoryHistoryServiceImpl implements InventoryHistoryService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryHistoryDao inventoryHistoryDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<InventoryHistory> inventoryHistorys) throws Exception {
		logger.info("start method: long insert(InventoryHistory InventoryHistory), arg InventoryHistory: "
				+ inventoryHistorys);

		if (inventoryHistorys==null||inventoryHistorys.size()<=0) {
			logger.warn("arg InventoryHistory is null");
			throw new IllegalArgumentException("arg InventoryHistory is null");
		}
		for (InventoryHistory inventoryHistory : inventoryHistorys) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventoryHistory.setHistoryId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryHistoryDao.insert(inventoryHistorys);
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
	public void delete(List<Long> inventoryHistorys) throws Exception {
		logger.info("start method: void delete(List<Long> InventoryHistorys), arg InventoryHistorys: "
				+ inventoryHistorys);
		if (inventoryHistorys == null || inventoryHistorys.size() == 0) {
			logger.warn("arg InventoryHistorys is null or size=0");
			throw new IllegalArgumentException("arg InventoryHistorys is null or size=0");
		}
		try {
			
			inventoryHistoryDao.delete(inventoryHistorys);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(InventoryHistory inventoryHistory) throws Exception {
		logger.info("start method: void update(InventoryHistory InventoryHistory), arg InventoryHistory: "
				+ inventoryHistory);
		if (inventoryHistory == null || inventoryHistory.getHistoryId()== null) {
			logger.warn("arg InventoryHistory is null or InventoryHistory 's inventoryHistoryId is null");
			throw new IllegalArgumentException("arg InventoryHistory is null or InventoryHistory 's addOidId is null");
		}
		try {
			
			inventoryHistoryDao.update(inventoryHistory);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<InventoryHistory> findBy(Pager<InventoryHistory> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<InventoryHistory> findBy(Pager<InventoryHistory> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<InventoryHistory>();
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
				long totalCount = inventoryHistoryDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<InventoryHistory> InventoryHistorys = inventoryHistoryDao.findAllBy(param);

			pager.setResult(InventoryHistorys);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
