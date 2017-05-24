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

import com.sdcloud.api.envsanitation.entity.InventoryClient;
import com.sdcloud.api.envsanitation.service.InventoryClientService;
import com.sdcloud.biz.envsanitation.dao.InventoryClientDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryClientService")
public class InventoryClientServiceImpl implements InventoryClientService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryClientDao inventoryClientDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<InventoryClient> inventoryClients) throws Exception {
		logger.info("start method: long insert(InventoryClient InventoryClient), arg InventoryClient: "
				+ inventoryClients);

		if (inventoryClients==null||inventoryClients.size()<=0) {
			logger.warn("arg InventoryClient is null");
			throw new IllegalArgumentException("arg InventoryClient is null");
		}
		for (InventoryClient inventoryClient : inventoryClients) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventoryClient.setClientId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryClientDao.insert(inventoryClients);
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
	public void delete(List<Long> inventoryClients) throws Exception {
		logger.info("start method: void delete(List<Long> InventoryClients), arg InventoryClients: "
				+ inventoryClients);
		if (inventoryClients == null || inventoryClients.size() == 0) {
			logger.warn("arg InventoryClients is null or size=0");
			throw new IllegalArgumentException("arg InventoryClients is null or size=0");
		}
		try {
			
			inventoryClientDao.delete(inventoryClients);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(InventoryClient inventoryClient) throws Exception {
		logger.info("start method: void update(InventoryClient InventoryClient), arg InventoryClient: "
				+ inventoryClient);
		if (inventoryClient == null || inventoryClient.getClientId() == null) {
			logger.warn("arg InventoryClient is null or InventoryClient 's inventoryClientId is null");
			throw new IllegalArgumentException("arg InventoryClient is null or InventoryClient 's addOidId is null");
		}
		try {
			
			inventoryClientDao.update(inventoryClient);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<InventoryClient> findBy(Pager<InventoryClient> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<InventoryClient> findBy(Pager<InventoryClient> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<InventoryClient>();
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
				long totalCount = inventoryClientDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<InventoryClient> InventoryClients = inventoryClientDao.findAllBy(param);

			pager.setResult(InventoryClients);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
