package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.biz.lar.dao.ShipmentHelpMeBuyDao;
import com.sdcloud.biz.lar.dao.ShipmentOperationDao;
import com.sdcloud.biz.lar.dao.ShipmentTurnOrderDao;
import com.sdcloud.biz.lar.dao.TableLockDao;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/28.
 */
@Service
public class ShipmentHelpMeByServiceImpl extends BaseServiceImpl<ShipmentHelpMeBuy>
		implements ShipmentHelpMeBuyService {

	@Autowired
	private ShipmentHelpMeBuyDao shipmentHelpMeBuyDao;
	@Autowired
	private TableLockDao tableLockDao;
	@Autowired
	private ShipmentTurnOrderDao shipmentTurnOrderDao;
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
	@Autowired
	private XingeAppUtils xingeAppUtils;

	@Override
	@Transactional(readOnly = false)
	public Boolean updateState(Map<String, Object> params, List<Long> list, Boolean flag) {
		int count = shipmentHelpMeBuyDao.updateState(params, list);
		List<ShipmentTurnOrder> result = new ArrayList<>();
		if (count > 0) {
			if (flag) {
				// 说明是转单，给转单记录表添加数据
				List<ShipmentHelpMeBuy> dtos = shipmentHelpMeBuyDao.findByIds(list);
				if (dtos.size() > 0) {
					for (ShipmentHelpMeBuy item : dtos) {
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
	public List<ShipmentHelpMeBuy> getBalance() {
		return shipmentHelpMeBuyDao.getBalance();
	}

	@Override
	public List<ShipmentHelpMeBuy> getCount() {
		return shipmentHelpMeBuyDao.getCount();
	}

	@Transactional(readOnly = false)
	public Boolean save(ShipmentHelpMeBuy t) {
		Long uuid = UUIDUtil.getUUNum();
		Date date = new Date();
		t.setId(uuid);
		t.setCreateDate(date);
		t.setGrabOrder(10);
		if (null != t.getCoupon()) {
			Voucher voucher = voucherService.getById(t.getCoupon(), null);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			// 判断 该优惠券是否存在&&是否过期&&是否已使用
			if (voucher != null && voucher.getExpireDate() != null && cal.getTime().before(voucher.getExpireDate())
					&& !voucher.getUseType().equals("已使用")) {
				voucher.setUseType("已使用");
				voucherService.update(voucher);
			} else {
				logger.warn("warn:该优惠券 使用存在问题，或者不存在，或者过期，或者已使用！用户提交的优惠券ID：" + t.getCoupon());
				return false;
			}
		}
		// 看看是后台设置的 是什么模式 抢单 或自动 或手动
		Map<String, String> map = sysConfigService.findMap();
		String orderModel = map.get("orderModel");
		t.setGrabOrder(10);

		XingeEntity xingeEntity = new XingeEntity();
		xingeEntity.setTitle("有新的订单");
		xingeEntity.setContent("有新的订单");
		List<String> accounts = new ArrayList<>();
		// 默认是手动
		if ("自动派单".equals(orderModel)) {
			if (!"未付款".equals(t.getOrderState())) {
				// 1、根据机构 查找片区
				List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(),
						t.getReceiver() + "");
				List<ShipmentOperation> shipmentOperations = new ArrayList<>();
				if (null != shipmentAreas && shipmentAreas.size() > 0) {
					for (ShipmentArea shipmentArea : shipmentAreas) {
						Map<String, Object> param = new HashMap<>();
						param.put("area", shipmentArea.getId());
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
			}
		} else if ("抢单".equals(orderModel)) {
			t.setGrabOrder(31);
			List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(), t.getReceiver() + "");
			StringBuffer areaList = new StringBuffer();// 存放用于抢单的地区
			for (int i = 0; i < shipmentAreas.size(); i++) {
				if (i < shipmentAreas.size() - 1) {
					areaList.append(shipmentAreas.get(i).getId() + ",");
				} else {
					areaList.append(shipmentAreas.get(i).getId() + "");
				}
			}
			t.setAreaList(areaList.toString());
			if (!"未付款".equals(t.getOrderState())) {
				List<ShipmentOperation> operations = shipmentOperationDao.selectByShipmentAreas(shipmentAreas);
				for (ShipmentOperation operation : operations) {// 去重
					if (!accounts.contains(operation.getSysUser() + "")) {
						accounts.add(operation.getSysUser() + "");
					}
				}
				xingeEntity.setAccounts(accounts);
				xingeEntity.setGrabOrder(31);
			}
		}
		int count = shipmentHelpMeBuyDao.save(t);
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
	@Transactional(readOnly = false)
	public Boolean update(ShipmentHelpMeBuy t, Integer type) {
		
		XingeEntity xingeEntity = new XingeEntity();
		xingeEntity.setTitle("有新的订单");
		xingeEntity.setContent("有新的订单");
		List<String> accounts = new ArrayList<>();
		if (type != null) {
			/*
			 * if (null != t.getCoupon()) { Voucher voucher =
			 * voucherService.getById(t.getCoupon(), null);
			 * voucher.setUseType("已使用"); voucherService.update(voucher); }
			 */
			// 看看是后台设置的 是什么模式 抢单 或自动 或手动
			Map<String, String> map = sysConfigService.findMap();
			String orderModel = map.get("orderModel");
			t.setGrabOrder(10);
			// 默认是手动
			if ("自动派单".equals(orderModel)) {
				// 1、根据机构 查找片区
				List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(),
						t.getReceiver() + "");
				List<ShipmentOperation> shipmentOperations = new ArrayList<>();
				if (null != shipmentAreas && shipmentAreas.size() > 0) {
					for (ShipmentArea shipmentArea : shipmentAreas) {
						Map<String, Object> param = new HashMap<>();
						param.put("area", shipmentArea.getId());
						param.put("type", "业务员");
						shipmentOperations.addAll(shipmentOperationService.findByMap(param));
					}
					if (shipmentOperations.size() > 0) {
						Random random = new Random();
						int i = random.nextInt(shipmentOperations.size());
						ShipmentOperation shipmentOperation = shipmentOperations.get(i);
						t.setTakeTime(new Date());
						t.setTakeUser(shipmentOperation.getSysUser());
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
				if (!"未付款".equals(t.getOrderState())) {
					List<ShipmentArea> shipmentAreas = shipmentSendExpressService.getAreaList(t.getOrg(),
							t.getReceiver() + "");
					List<ShipmentOperation> operations = shipmentOperationDao.selectByShipmentAreas(shipmentAreas);
					for (ShipmentOperation operation : operations) {// 去重
						if (!accounts.contains(operation.getSysUser() + "")) {
							accounts.add(operation.getSysUser() + "");
						}
					}
					xingeEntity.setAccounts(accounts);
					xingeEntity.setGrabOrder(31);
				}
			}
		}
		Boolean update = super.update(t);
		if(update){
			if(t.getGrabOrder() != 22){
				xingeAppUtils.pushAccountList(xingeEntity,2);
				xingeAppUtils.pushAccountListIOS(xingeEntity,4);
			}
		}
		return update;
	}

	@Override
	public OrderDetailDTO orderDetail(Long id, Map<String, Object> map) {
		return shipmentHelpMeBuyDao.orderDetail(id, map);
	}

	@Override
	public ShipmentHelpMeBuy getByNo(String orderNo) {
		return shipmentHelpMeBuyDao.getByNo(orderNo);
	}

	@Override
	public List<OrderDetailDTO> getEvaluation(Long aLong) {
		return shipmentHelpMeBuyDao.getEvaluation(aLong);
	}

	@Override
	public LarPager<ShipmentHelpMeBuy> findByOrgIdsOne(LarPager<ShipmentHelpMeBuy> larPager, List<Long> list) {
		List<ShipmentHelpMeBuy> result = shipmentHelpMeBuyDao.findByOrgIdsOne(larPager, list);
		larPager.setResult(result);
		larPager.setTotalCount(shipmentHelpMeBuyDao.countByOrgIdsOne(larPager, list));
		return larPager;
	}

	@Override
	@Transactional(readOnly = false)
	public int updateGrabState(long time) {
		int bizType=3;
		logger.info("method:{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		int lockflag = tableLockDao.lock(bizType);// 获取锁
		if (lockflag == 0) {// 获取不到锁，直接返回
			logger.info("method:{},帮我买未获取到锁", Thread.currentThread().getStackTrace()[1].getMethodName());
			return 0;
		}else{
			logger.info("method:{},帮我买取得锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		try {
			int count = shipmentHelpMeBuyDao.updateGrabState(time);
			logger.info("method:{},更新:{}条数据状态", Thread.currentThread().getStackTrace()[1].getMethodName(), count);
			return count;
		} finally {
			tableLockDao.unLock(bizType);// 解锁
			logger.info("method:{},帮我买解锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}

	@Override
	public List<ShipmentHelpMeBuy> getByIds(List<Long> ids) {
		return shipmentHelpMeBuyDao.getByIds(ids);
	}
	@Override
	@Transactional(readOnly = false)
	public boolean backCoupon(List<Long> ids) {
		int count = shipmentHelpMeBuyDao.backCoupon(ids);
		return count>0;
	}
	@Override
	public List<ShipmentHelpMeBuy> findInvalidOrder(Long time) {
		return shipmentHelpMeBuyDao.findInvalidOrder(time, new Date());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean batchUpdate(List<ShipmentHelpMeBuy> list) {
		List<String> accounts = new ArrayList<>();
		for (ShipmentHelpMeBuy order : list) {
			if(null != order.getDistributeUser()){
				order.setDistributeTime(new Date());
				order.setGrabOrder(33);
				order.setOrderState("服务中");
				accounts.add(order.getDistributeUser()+"");
			}else{
				order.setGrabOrder(33);
			}
		}
		int update = shipmentHelpMeBuyDao.batchUpdate(list);
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
