package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.biz.lar.dao.ShipmentCitySendDao;
import com.sdcloud.biz.lar.dao.ShipmentOperationDao;
import com.sdcloud.biz.lar.dao.ShipmentTurnOrderDao;
import com.sdcloud.biz.lar.dao.TableLockDao;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/27.
 */
@Service
public class ShipmentCitySendServiceImpl extends BaseServiceImpl<ShipmentCitySend> implements ShipmentCitySendService {
	@Autowired
	private ShipmentCitySendDao shipmentCitySendDao;
	@Autowired
	private TableLockDao tableLockDao;
	@Autowired
	private ShipmentTurnOrderDao shipmentTurnOrderDao;
	@Autowired
	private XingeAppUtils xingeAppUtils;

	@Override
	@Transactional(readOnly = false)
	public Boolean updateState(Map<String, Object> params, List<Long> list, Boolean flag) {
		int count = shipmentCitySendDao.updateState(params, list);
		List<ShipmentTurnOrder> result = new ArrayList<>();
		if (count > 0) {
			if (flag) {
				// 说明是转单，给转单记录表添加数据
				List<ShipmentCitySend> dtos = shipmentCitySendDao.findByIds(list);
				if (dtos.size() > 0) {
					for (ShipmentCitySend item : dtos) {
						ShipmentTurnOrder shipmentTurnOrder = new ShipmentTurnOrder();
						shipmentTurnOrder.setId(UUIDUtil.getUUNum());
						shipmentTurnOrder.setOrg(item.getOrg());
						shipmentTurnOrder.setArea(item.getArea());
						shipmentTurnOrder.setNextSalesMan(item.getSalesMan());
						shipmentTurnOrder.setSalesMan(item.getPreviousSalesMan());
						shipmentTurnOrder.setOrderNo(item.getOrderNo());
						shipmentTurnOrder.setOrderState(item.getOrderState());
						shipmentTurnOrder.setOrderTime(item.getOrderTime());
						shipmentTurnOrder.setOrderType(item.getBizType());
						shipmentTurnOrder.setTurnOrderTime(item.getTurnOrderTime());
						shipmentTurnOrder.setTurnOrderType(item.getTurnOrderType());
						shipmentTurnOrder.setTurnOrderUser(item.getTurnOrderUser());
						shipmentTurnOrder.setRemarks(item.getTurnOrderRemarks());
						shipmentTurnOrder.setCreateDate(new Date());
						shipmentTurnOrder.setCreateUser(UUIDUtil.getUUNum());
						result.add(shipmentTurnOrder);
					}
					shipmentTurnOrderDao.batchSave(result);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<ShipmentCitySend> getBalance() {
		return shipmentCitySendDao.getBalance();
	}

	@Override
	public List<ShipmentCitySend> getCount() {
		return shipmentCitySendDao.getCount();
	}

	@Autowired
	private VoucherService voucherService;
	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private ShipmentOperationService shipmentOperationService;

	@Autowired
	private ShipmentOperationDao shipmentOperationDao;

	@Autowired
	private ShipmentSendExpressService shipmentSendExpressService;

	@Transactional(readOnly = false)
	public Boolean save(ShipmentCitySend t) {
		Long uuid = UUIDUtil.getUUNum();
		Date date = new Date();
		t.setId(uuid);
		t.setCreateDate(date);
		t.setGrabOrder(10);
		if (null != t.getCoupon()) {
			Voucher voucher = voucherService.getById(t.getCoupon(), null);
			voucher.setUseType("已使用");
			voucherService.update(voucher);
		}

		// 看看是后台设置的 是什么模式 抢单 或自动 或手动
		Map<String, String> map = sysConfigService.findMap();
		String orderModel = map.get("orderModel");
		
		XingeEntity xingeEntity = new XingeEntity();
		xingeEntity.setTitle("有新的订单");
		xingeEntity.setContent("有新的订单");
		List<String> accounts = new ArrayList<>();
		// 默认是手动
		if ("自动派单".equals(orderModel)) {
			// 1、根据机构 查找片区
			List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(), t.getSender() + "");

			if (null != shipmentAreas && shipmentAreas.size() > 0) {
				List<ShipmentOperation> shipmentOperations = new ArrayList<>();
				for (ShipmentArea area : shipmentAreas) {
					Map<String, Object> param = new HashMap<>();
					param.put("area", area.getId());
					param.put("type", "业务员");
					shipmentOperations.addAll(shipmentOperationService.findByMap(param));
				}
				if (shipmentOperations.size() > 0) {
					Random random = new Random();
					int j = random.nextInt(shipmentOperations.size());
					ShipmentOperation shipmentOperation = shipmentOperations.get(j);
					t.setArea(shipmentOperation.getArea());
					/*t.setTakeTime(new Date());
					t.setTakeUser(shipmentOperation.getSysUser());*/
					t.setDistributeUser(shipmentOperation.getSysUser());
					t.setDistributeTime(new Date());
					t.setOrderState("服务中");
					t.setGrabOrder(21);
					t.setSalesMan(shipmentOperation.getId());
					// 推送
					String sysUser = shipmentOperation.getSysUser() + "";
					accounts.add(sysUser);
					xingeEntity.setAccounts(accounts);
					xingeEntity.setGrabOrder(11);
				} else {
					t.setGrabOrder(22);
				}
			} else {
				t.setGrabOrder(22);
			}

		} else if ("抢单".equals(orderModel)) {
			t.setGrabOrder(31);
			List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(), t.getSender() + "");
			StringBuffer areaList = new StringBuffer();// 存放用于抢单的地区
			for (int i = 0; i < shipmentAreas.size(); i++) {
				if (i < shipmentAreas.size() - 1) {
					areaList.append(shipmentAreas.get(i).getId() + ",");
				} else {
					areaList.append(shipmentAreas.get(i).getId() + "");
				}
			}
			t.setAreaList(areaList.toString());
			List<ShipmentOperation> operations = shipmentOperationDao.selectByShipmentAreas(shipmentAreas);
			for (ShipmentOperation operation : operations) {// 去重
				if (!accounts.contains(operation.getSysUser() + "")) {
					accounts.add(operation.getSysUser() + "");
				}
			}
			xingeEntity.setAccounts(accounts);
			xingeEntity.setGrabOrder(31);

		}
		int count = shipmentCitySendDao.save(t);
		if(count > 0){
			if(t.getGrabOrder() != 22){
				xingeAppUtils.pushAccountList(xingeEntity,2);
				xingeAppUtils.pushAccountListIOS(xingeEntity,4);
			}
			return true;
		}
		return false;
	}

	@Override
	public OrderDetailDTO orderDetail(Long id, Map<String, Object> map) {
		return shipmentCitySendDao.orderDetail(id, map);
	}

	@Override
	public ShipmentCitySend getByNo(String orderNo) {
		return shipmentCitySendDao.getByNo(orderNo);
	}

	@Override
	public LarPager<ShipmentCitySend> findByOrgIdsOne(LarPager<ShipmentCitySend> larPager, List<Long> list) {
		List<ShipmentCitySend> result = shipmentCitySendDao.findByOrgIdsOne(larPager, list);
		larPager.setResult(result);
		larPager.setTotalCount(shipmentCitySendDao.countByOrgIdsOne(larPager, list));
		return larPager;
	}

	@Override
	public List<OrderDetailDTO> getEvaluation(Long aLong) {
		return shipmentCitySendDao.getEvaluation(aLong);
	}

	@Override
	@Transactional(readOnly = false)
	public int updateGrabState(long time) {
		int bizType = 2;
		logger.info("method:{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		int lockflag = tableLockDao.lock(bizType);// 获取锁
		if (lockflag == 0) {// 获取不到锁，直接返回
			logger.info("method:{},同城送未获取到锁", Thread.currentThread().getStackTrace()[1].getMethodName());
			return 0;
		}else{
			logger.info("method:{},同城送取得锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		try {
			int count = shipmentCitySendDao.updateGrabState(time);
			logger.info("method:{},更新:{}条数据状态", Thread.currentThread().getStackTrace()[1].getMethodName(), count);
			return count;
		} finally {
			tableLockDao.unLock(bizType);// 解锁
			logger.info("method:{},同城送解锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}

	@Override
	@Transactional(readOnly = false)
	public boolean backCoupon(List<Long> ids) {
		int count = shipmentCitySendDao.backCoupon(ids);
		return count>0;
	}

	@Override
	public List<ShipmentCitySend> findInvalidOrder(Long time) {
		return shipmentCitySendDao.findInvalidOrder(time, new Date());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean batchUpdate(List<ShipmentCitySend> list) {
		List<String> accounts = new ArrayList<>();
		for (ShipmentCitySend order : list) {
			if(null != order.getDistributeUser()){
				order.setDistributeTime(new Date());
				order.setGrabOrder(33);
				order.setOrderState("服务中");
				accounts.add(order.getDistributeUser()+"");
			}else{
				order.setGrabOrder(33);
			}
		}
		int update = shipmentCitySendDao.batchUpdate(list);
		if(update > 0 && accounts.size()>0){
			XingeEntity xingeEntity = new XingeEntity();
			xingeEntity.setTitle("有新的订单");
			xingeEntity.setContent("有新的订单");
			xingeEntity.setGrabOrder(11);
			xingeEntity.setAccounts(accounts);
			xingeAppUtils.pushAccountList(xingeEntity,2);
			xingeAppUtils.pushAccountListIOS(xingeEntity,4);
		}
		return update > 0;
	}
}
