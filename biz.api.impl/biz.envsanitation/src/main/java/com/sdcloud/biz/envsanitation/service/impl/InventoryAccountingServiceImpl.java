package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
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

import com.sdcloud.api.envsanitation.entity.InventoryAccounting;
import com.sdcloud.api.envsanitation.entity.InventoryHistory;
import com.sdcloud.api.envsanitation.entity.InventoryOrder;
import com.sdcloud.api.envsanitation.service.InventoryAccountingService;
import com.sdcloud.biz.envsanitation.dao.InventoryAccountingDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.biz.envsanitation.dao.InventoryHistoryDao;
import com.sdcloud.biz.envsanitation.dao.InventoryOrderDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.InventoryOrderStatus;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryAccountingService")
public class InventoryAccountingServiceImpl implements InventoryAccountingService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryAccountingDao inventoryAccountingDao;
	@Autowired
	private InventoryHistoryDao inventoryHistoryDao;
	@Autowired
	private InventoryOrderDao inventoryOrderDao;
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<InventoryAccounting> inventoryAccountings) throws Exception {
		logger.info("start method: long insert(InventoryAccounting InventoryAccounting), arg InventoryAccounting: "
				+ inventoryAccountings);

		if (inventoryAccountings==null||inventoryAccountings.size()<=0) {
			logger.warn("arg InventoryAccounting is null");
			throw new IllegalArgumentException("arg InventoryAccounting is null");
		}
		for (InventoryAccounting inventoryAccounting : inventoryAccountings) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventoryAccounting.setAccountingId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryAccountingDao.insert(inventoryAccountings);
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
	public void delete(List<Long> inventoryAccountings) throws Exception {
		logger.info("start method: void delete(List<Long> InventoryAccountings), arg InventoryAccountings: "
				+ inventoryAccountings);
		if (inventoryAccountings == null || inventoryAccountings.size() == 0) {
			logger.warn("arg InventoryAccountings is null or size=0");
			throw new IllegalArgumentException("arg InventoryAccountings is null or size=0");
		}
		try {
			for (Long accountingId : inventoryAccountings) {
				Map<String, Object> param=new HashMap<String,Object>();
				param.put("accountingId", accountingId);
				List<InventoryHistory> inventoryHistorys= inventoryHistoryDao.findOutAllBy(param);
				if(inventoryHistorys!=null&&inventoryHistorys.size()>0){
					logger.error("method {} execute error,accountingId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),accountingId,"批次库存删除失败,已经在使用");
					throw new RuntimeException("{"+accountingId+"}该批次库存已经在使用，无法删除");
				}
			}
			
			inventoryAccountingDao.delete(inventoryAccountings);
			
		} catch (Exception e) {
			logger.error("method {} execute error,inventoryAccountings:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),inventoryAccountings,e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(InventoryAccounting inventoryAccounting) throws Exception {
		logger.info("start method: void update(InventoryAccounting InventoryAccounting), arg InventoryAccounting: "
				+ inventoryAccounting);
		if (inventoryAccounting == null || inventoryAccounting.getAccountingId() == null) {
			logger.warn("arg InventoryAccounting is null or InventoryAccounting 's inventoryAccountingId is null");
			throw new IllegalArgumentException("arg InventoryAccounting is null or InventoryAccounting 's addOidId is null");
		}
		try {
			
			inventoryAccountingDao.update(inventoryAccounting);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<InventoryAccounting> findBy(Pager<InventoryAccounting> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<InventoryAccounting> findBy(Pager<InventoryAccounting> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<InventoryAccounting>();
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
				long totalCount = inventoryAccountingDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<InventoryAccounting> InventoryAccountings = inventoryAccountingDao.findAllBy(param);

			pager.setResult(InventoryAccountings);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public List<InventoryAccounting> findByOrderId(Long orderId)
			throws Exception {
		List<InventoryAccounting> inventoryAccountings = new ArrayList<InventoryAccounting>();
		try {
			logger.info("Enter the :{} method  orderId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),orderId);
			List<Long> orderIds=new ArrayList<Long>();
			orderIds.add(orderId);
			List<InventoryOrder> inventoryOrders=inventoryOrderDao.findInventoryOrderByIds(orderIds);
			if(inventoryOrders!=null&&inventoryOrders.size()>0){
				int type=inventoryOrders.get(0).getType();
				int status=inventoryOrders.get(0).getStatus();
				//入库
				if(type==1&&status!=InventoryOrderStatus.chargeAccount&&status!=InventoryOrderStatus.beginning){
					inventoryAccountings = inventoryAccountingDao.findByOrderId(orderId);
				}else{
					inventoryAccountings = inventoryAccountingDao.findByOrderIdHistory(orderId);
				}
			}
			
		} catch (Exception e) {
			logger.error("method {} execute error, orderId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),orderId, e);
			throw e;
		}
		return inventoryAccountings;
	}

	@Override
	public Pager<InventoryAccounting> findByBeginning(
			Pager<InventoryAccounting> pager, Map<String, Object> param)
			throws Exception {
		logger.info("start method: Pager<InventoryAccounting> findBy(Pager<InventoryAccounting> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<InventoryAccounting>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("c.accountingId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = inventoryAccountingDao.getTotalCountBeginning(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<InventoryAccounting> InventoryAccountings = inventoryAccountingDao.findAllByBeginning(param);

			pager.setResult(InventoryAccountings);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

}
