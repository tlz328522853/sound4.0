package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sdcloud.api.envsanitation.entity.InventoryAccounting;
import com.sdcloud.api.envsanitation.entity.InventoryHistory;
import com.sdcloud.api.envsanitation.entity.InventoryOrder;
import com.sdcloud.api.envsanitation.service.InventoryOrderService;
import com.sdcloud.biz.envsanitation.dao.InventoryAccountingDao;
import com.sdcloud.biz.envsanitation.dao.InventoryHistoryDao;
import com.sdcloud.biz.envsanitation.dao.InventoryOrderDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.InventoryOrderStatus;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("inventoryOrderService")
public class InventoryOrderServiceImpl implements InventoryOrderService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryOrderDao inventoryOrderDao;
	
	@Autowired
	private InventoryAccountingDao inventoryAccountingDao;
	
	@Autowired
	private InventoryHistoryDao inventoryHistoryDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public List<InventoryOrder> insert(List<InventoryOrder> inventoryOrders) throws Exception {
		logger.info("start method: long insert(InventoryOrder InventoryOrder), arg InventoryOrder: "
				+ inventoryOrders);
		if (inventoryOrders == null || inventoryOrders.size() <= 0) {
			logger.warn("arg InventoryOrder is null");
			throw new IllegalArgumentException("arg InventoryOrder is null");
		}
		List<InventoryAccounting> saveInventoryAs = new ArrayList<InventoryAccounting>();
		List<InventoryHistory> inventoryHistorys = new ArrayList<InventoryHistory>();
		for (InventoryOrder inventoryOrder : inventoryOrders) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			inventoryOrder.setOrderId(id);
			List<InventoryAccounting> inventoryAs = inventoryOrder
					.getInventoryAs();
			if (inventoryAs != null && inventoryAs.size() > 0) {
				List<Long> updateAIds = new ArrayList<Long>();
				for (InventoryAccounting inventoryA : inventoryAs) {
					//生产日期
					Date prodDate=inventoryA.getProdDate();
					//保质期
					int month=inventoryA.getMonth();
					//过期时间
					if(inventoryOrder.getType() == 1&&prodDate!=null&&month>0){
						Calendar cal = Calendar.getInstance();
						cal.setTime(prodDate);
						cal.add(Calendar.MONTH, month);
						inventoryA.setExpiry(cal.getTime());
					}
					if (inventoryOrder.getType() == 1&&inventoryOrder.getStatus()!=InventoryOrderStatus.beginning) {
						// 入库
						inventoryA.setAmount(getTotalPrice(inventoryA.getCount(),inventoryA.getPrice()));
						inventoryA.setAccountingId(UUIDUtil.getUUNum());
						inventoryA.setOrderId(inventoryOrder.getOrderId());
						
						saveInventoryAs.add(inventoryA);
					}else if(inventoryOrder.getType() == 1&&inventoryOrder.getStatus()==InventoryOrderStatus.beginning){
						//库存期初
						inventoryA.setOrderId(inventoryOrder.getOrderId());
						inventoryA.setAccountingId(UUIDUtil.getUUNum());
						double newCount = inventoryA.getCount();
						inventoryA.setCountAfterAccounting(newCount);
						double newPrice =getTotalPrice(newCount,inventoryA.getPrice());
						inventoryA.setAmountAfterAccounting(newPrice);
						inventoryA.setAmount(getTotalPrice(inventoryA.getCount(),inventoryA.getPrice()));
						InventoryHistory ith = new InventoryHistory();
						ith.setAccountingId(inventoryA.getAccountingId());
						ith.setCreateTime(new Date());
						ith.setHistoryId(UUIDUtil.getUUNum());
						ith.setNumber(inventoryA.getCount());
						ith.setPrice(getTotalPrice(inventoryA.getCount(),inventoryA.getPrice()));
						ith.setOrderId(inventoryOrder.getOrderId());
						ith.setTenantId(inventoryOrder.getTenantId());
						saveInventoryAs.add(inventoryA);
						inventoryHistorys.add(ith);
					}else if (inventoryOrder.getType() == 0&& inventoryA.getAccountingId() != null) {
						updateAIds.add(inventoryA.getAccountingId());
					}
				}
				if (updateAIds.size() > 0) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("accountingIds", updateAIds);
					List<InventoryAccounting> inventoryAupdate = inventoryAccountingDao
							.findAllBy(param);
					if (inventoryAupdate != null && inventoryAupdate.size() > 0) {
						Map<Long, InventoryAccounting> updateMap = new HashMap<Long, InventoryAccounting>();
						for (InventoryAccounting inventoryAccounting : inventoryAupdate) {
							updateMap.put(
									inventoryAccounting.getAccountingId(),
									inventoryAccounting);
						}
						for (InventoryAccounting inventoryA : inventoryAs) {
							// 变更数量
							// 出库
							InventoryAccounting create = updateMap.get(inventoryA.getAccountingId());
							double newCount=create.getCount()- inventoryA.getCount();
							if(newCount<0){
								throw new RuntimeException("Number has error");
							}
							create.setCount(newCount);
							create.setAmount(getTotalPrice(newCount,create.getPrice()));
							//库存期初
							if(inventoryOrder.getStatus()==InventoryOrderStatus.beginning){
								inventoryA.setCountAfterAccounting(newCount);
								inventoryA.setAmountAfterAccounting(getTotalPrice(newCount,create.getPrice()));
							}
							inventoryAccountingDao.update(create);
							// 流水
							InventoryHistory ith = new InventoryHistory();
							ith.setAccountingId(inventoryA.getAccountingId());
							ith.setCreateTime(new Date());
							ith.setHistoryId(UUIDUtil.getUUNum());
							ith.setNumber(inventoryA.getCount());
							ith.setPrice(getTotalPrice(inventoryA.getCount()
									,create.getPrice()));
							ith.setOrderId(inventoryOrder.getOrderId());
							inventoryHistorys.add(ith);
						}
					}
				}
			}
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					inventoryOrderDao.insert(inventoryOrders);
					if (saveInventoryAs.size() > 0) {
						inventoryAccountingDao.insert(saveInventoryAs);
					}
					if (inventoryHistorys.size() > 0) {
						inventoryHistoryDao.insert(inventoryHistorys);
					}
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
		return inventoryOrders;
	}

	@Transactional
	public void delete(List<Long> inventoryOrders) throws Exception {
		logger.info("start method: void delete(List<Long> InventoryOrders), arg InventoryOrders: "
				+ inventoryOrders);
		if (inventoryOrders == null || inventoryOrders.size() == 0) {
			logger.warn("arg InventoryOrders is null or size=0");
			throw new IllegalArgumentException("arg InventoryOrders is null or size=0");
		}
		try {
			List<InventoryOrder> inventoryOrderDeletes= inventoryOrderDao.findInventoryOrderByIds(inventoryOrders);
			for (InventoryOrder delete : inventoryOrderDeletes) {
				//出库做数据还原
				if(delete.getType()==0){
					Map<String,Object> param=new HashMap<String,Object>();
					param.put("orderId",delete.getOrderId());
					List<InventoryHistory> inventoryHistorys= inventoryHistoryDao.findAllBy(param);
					List<Long> inventoryAIds=new ArrayList<Long>();
					for (InventoryHistory inventoryHistory : inventoryHistorys) {
						inventoryAIds.add(inventoryHistory.getAccountingId());
					}
					param.clear();
					param.put("accountingIds", inventoryAIds);
					List<InventoryAccounting> inventoryAUpdates= inventoryAccountingDao.findAllBy(param);
					Map<Long,InventoryAccounting> mapUpdate=new HashMap<Long,InventoryAccounting>();
					for (InventoryAccounting inventoryAupdate : inventoryAUpdates) {
						mapUpdate.put(inventoryAupdate.getAccountingId(), inventoryAupdate);
					}
					for (InventoryHistory inventoryHistory : inventoryHistorys) {
						InventoryAccounting inventoryAccounting=mapUpdate.get(inventoryHistory.getAccountingId());
						double newCount=inventoryAccounting.getCount()+inventoryHistory.getNumber();
						inventoryAccounting.setCount(newCount);
						double newAmount=getTotalPrice(newCount,inventoryAccounting.getPrice());
						inventoryAccounting.setAmount(newAmount);
						inventoryAccountingDao.update(inventoryAccounting);

					}
				}
				
			}
			
			
			//删除之前的流水
			inventoryHistoryDao.deleteByOrdeId(inventoryOrders);
			//删除所有批次库存
			inventoryAccountingDao.deleteByOrderId(inventoryOrders);

			inventoryOrderDao.delete(inventoryOrders);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(InventoryOrder inventoryOrder) throws Exception {
		logger.info("start method: void update(InventoryOrder InventoryOrder), arg InventoryOrder: "
				+ inventoryOrder);
		if (inventoryOrder == null || inventoryOrder.getOrderId() == null) {
			logger.warn("arg InventoryOrder is null or InventoryOrder 's inventoryOrderId is null");
			throw new IllegalArgumentException(
					"arg InventoryOrder is null or InventoryOrder 's addOidId is null");
		}
		try {
			List<Long> orderIds = new ArrayList<Long>();
			orderIds.add(inventoryOrder.getOrderId());
			List<InventoryAccounting> inventoryAs = inventoryOrder
					.getInventoryAs();
			Map<Long, InventoryAccounting> mapUpdate = new HashMap<Long, InventoryAccounting>();
			List<InventoryHistory> inventoryHs = new ArrayList<InventoryHistory>();
			if (inventoryAs != null && inventoryAs.size() > 0) {
				//出库，删除前先还原原来的数量，然后新建一批出库
				if (inventoryOrder.getType() == 0) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("orderId", inventoryOrder.getOrderId());
					List<InventoryHistory> inventoryHistorys = inventoryHistoryDao
							.findAllBy(param);
					List<Long> inventoryAIds = new ArrayList<Long>();
					for (InventoryHistory inventoryHistory : inventoryHistorys) {
						inventoryAIds.add(inventoryHistory.getAccountingId());
					}
					param.clear();
					param.put("accountingIds", inventoryAIds);
					List<InventoryAccounting> inventoryAUpdates = inventoryAccountingDao
							.findAllBy(param);
					for (InventoryAccounting inventoryAupdate : inventoryAUpdates) {
						mapUpdate.put(inventoryAupdate.getAccountingId(),
								inventoryAupdate);
					}
					//还原旧的批次数量
					for (InventoryHistory inventoryHistory : inventoryHistorys) {
						InventoryAccounting inventoryAccounting = mapUpdate
								.get(inventoryHistory.getAccountingId());
						double newCount = inventoryAccounting.getCount()
								+ inventoryHistory.getNumber();
						inventoryAccounting.setCount(newCount);
						double newAmount = getTotalPrice(newCount,
								inventoryAccounting.getPrice());
						inventoryAccounting.setAmount(newAmount);
						inventoryAccountingDao.update(inventoryAccounting);

					}
					// 删除之前的流水
					inventoryHistoryDao.deleteByOrdeId(orderIds);
					//新建批次出库
					for (InventoryAccounting inventoryAccounting : inventoryAs) {
						// 出库减去数量
						if (inventoryAccounting.getAccountingId() != null) {
							// 变更数量
							InventoryAccounting create = mapUpdate
									.get(inventoryAccounting.getAccountingId());
							double newCount = create.getCount()
									- inventoryAccounting.getCount();
							if (newCount < 0) {
								throw new RuntimeException("Number has error");
							}
							create.setCount(newCount);
							create.setAmount(getTotalPrice(newCount,
									create.getPrice()));
							// 库存期初
							inventoryAccountingDao.update(create);
							// 出库
							InventoryHistory ith = new InventoryHistory();
							ith.setAccountingId(inventoryAccounting
									.getAccountingId());
							ith.setCreateTime(new Date());
							ith.setHistoryId(UUIDUtil.getUUNum());
							ith.setNumber(inventoryAccounting.getCount());
							ith.setPrice(getTotalPrice(
									inventoryAccounting.getCount(),
									create.getPrice()));
							ith.setOrderId(inventoryOrder.getOrderId());
							inventoryHs.add(ith);
						}

					}
				}
				//入库单更新
				if (inventoryAs != null && inventoryAs.size() > 0
						&& inventoryOrder.getType() == 1) {
					//根据订单号删除批次
					inventoryAccountingDao.deleteByOrderId(orderIds);
					for (InventoryAccounting inventoryA : inventoryAs) {
						inventoryA.setOrderId(inventoryOrder
								.getOrderId());
						inventoryA.setAccountingId(UUIDUtil.getUUNum());
					}
					inventoryAccountingDao.insert(inventoryAs);
				}
				if (inventoryHs != null && inventoryHs.size() > 0) {
					inventoryHistoryDao.insert(inventoryHs);
				}

			}
			inventoryOrder.setModifiedTime(new Date());
			inventoryOrderDao.update(inventoryOrder);

		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					inventoryOrder.getOrderId(), e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<InventoryOrder> findBy(Pager<InventoryOrder> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<InventoryOrder> findBy(Pager<InventoryOrder> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<InventoryOrder>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("c.inOutDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = inventoryOrderDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<InventoryOrder> InventoryOrders = inventoryOrderDao.findAllBy(param);

			pager.setResult(InventoryOrders);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public void operationInventoryOrder(int optionType,int orderType,
			InventoryOrder inventoryOrder) throws Exception {
		
		try {
			logger.info("Enter the :{} method  optionType:{} orderType:{} inventoryOrder:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),optionType,orderType,inventoryOrder);

			
			if(optionType==InventoryOrderStatus.chargeAccount){
				if(orderType==1){
					//入库
					inInventoryOrder(orderType, inventoryOrder);
				}else{
					outInventoryOrder(orderType, inventoryOrder);
				}
			}else{
				if(optionType==InventoryOrderStatus.audit){
					inventoryOrder.setAuditDate(new Date());
				}
				inventoryOrderDao.update(inventoryOrder);
			}
		} catch (Exception e) {
			logger.error("method {} execute error, optionType:{} orderType:{} inventoryOrder:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), optionType, orderType, inventoryOrder,e);
			throw e;
		}
		
	}

	/**
	 * 入库
	 * 
	 * @param orderType
	 * @param inventoryOrder
	 */
	private void inInventoryOrder(int orderType, InventoryOrder inventoryOrder) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderId", inventoryOrder.getOrderId());
		List<InventoryAccounting> inventoryAs = inventoryAccountingDao
				.findAllBy(param);
		if (inventoryAs != null && inventoryAs.size() > 0) {
			List<InventoryHistory> inventoryHistorys = new ArrayList<InventoryHistory>();
			for (InventoryAccounting inventoryA : inventoryAs) {
				double newCount = inventoryA.getCount();
				inventoryA.setCountAfterAccounting(newCount);
				double newPrice =getTotalPrice(newCount,inventoryA.getPrice());
				inventoryA.setAmountAfterAccounting(newPrice);
				// 增加记账
				inventoryAccountingDao.update(inventoryA);
				InventoryHistory ith = new InventoryHistory();
				ith.setAccountingId(inventoryA.getAccountingId());
				ith.setCreateTime(new Date());
				ith.setHistoryId(UUIDUtil.getUUNum());
				ith.setNumber(inventoryA.getCount());
				ith.setPrice(getTotalPrice(inventoryA.getCount(),inventoryA.getPrice()));
				ith.setOrderId(inventoryOrder.getOrderId());
				inventoryHistorys.add(ith);
			}
			inventoryHistoryDao.insert(inventoryHistorys);

		}
		
		// 入库单记账
		inventoryOrder.setStatus(InventoryOrderStatus.chargeAccount);
		inventoryOrder.setAccountDate(new Date());
		inventoryOrderDao.update(inventoryOrder);
	}

	/**
	 * 出库
	 * 
	 * @param orderType
	 * @param inventoryOrder
	 */
	private void outInventoryOrder(int orderType, InventoryOrder inventoryOrder) {
		Map<String, Object> paramHistory = new HashMap<String, Object>();
		paramHistory.put("orderId", inventoryOrder.getOrderId());
		List<InventoryHistory> inventoryHistorys = inventoryHistoryDao
				.findAllBy(paramHistory);
		List<Long> inventoryAIds = new ArrayList<Long>();
		if (inventoryHistorys != null && inventoryHistorys.size() > 0) {
			//减少数据库访问次数，一起查询
			for (InventoryHistory inventoryH : inventoryHistorys) {
				inventoryAIds.add(inventoryH.getAccountingId());
			}
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("accountingIds", inventoryAIds);
			List<InventoryAccounting> inventoryAs = inventoryAccountingDao
					.findAllBy(param);
			Map<Long,InventoryAccounting> inventoryMaps=new HashMap<Long,InventoryAccounting>();
			for (InventoryAccounting inventoryAccounting : inventoryAs) {
				inventoryMaps.put(inventoryAccounting.getAccountingId(), inventoryAccounting);
			}
			//批次记账，记账数量和记账金额更新
			for (InventoryHistory inventoryH : inventoryHistorys) {
				InventoryAccounting inventoryAccounting=inventoryMaps.get(inventoryH.getAccountingId());
				double newCount=inventoryAccounting.getCountAfterAccounting()-inventoryH.getNumber();
				if(newCount<0){
					throw new RuntimeException("Number has error");
				}
				inventoryAccounting.setCountAfterAccounting(newCount);
				double newPrice=getTotalPrice(newCount,inventoryAccounting.getPrice());
				inventoryAccounting.setAmountAfterAccounting(newPrice);
				inventoryAccountingDao.update(inventoryAccounting);
				
			}
			inventoryOrder.setStatus(InventoryOrderStatus.chargeAccount);
			inventoryOrder.setAccountDate(new Date());
			inventoryOrderDao.update(inventoryOrder);
		}
	}
	/**
	 * 价格运算,方便统一修改返回格式
	 * @param count
	 * @param price
	 * @return
	 */
	private double getTotalPrice(double count,double price){
		return Double.valueOf(String.format("%.2f",count*price));
	}
	
	/*public static void  main(String[] string){
		String result = String.format("%.2f",5.004*2);
		System.out.println(Math.ceil( 5.444 * 100 )/ 100.0);
		System.out.println(result);
	}*/

	@Override
	public List<InventoryOrder> findInventoryOrderByIds(List<Long> orderIds) {
		List<InventoryOrder> inventoryOrders;
		try {
			logger.info("Enter the :{} method  orderIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),orderIds);

			inventoryOrders = inventoryOrderDao
					.findInventoryOrderByIds(orderIds);
		} catch (Exception e) {
			logger.error("method {} execute error, orderIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),orderIds, e);
			throw e;
		}
		return inventoryOrders;
	}

}
