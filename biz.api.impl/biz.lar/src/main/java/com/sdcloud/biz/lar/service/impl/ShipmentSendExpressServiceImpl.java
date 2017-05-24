package com.sdcloud.biz.lar.service.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.OrderServiceDTO;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.ShipmentAreaService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.biz.lar.dao.OrderManagerDao;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.biz.lar.dao.ShipmentCitySendDao;
import com.sdcloud.biz.lar.dao.ShipmentHelpMeBuyDao;
import com.sdcloud.biz.lar.dao.ShipmentOperationDao;
import com.sdcloud.biz.lar.dao.ShipmentSendExpressDao;
import com.sdcloud.biz.lar.dao.ShipmentTurnOrderDao;
import com.sdcloud.biz.lar.dao.TableLockDao;
import com.sdcloud.biz.lar.util.MapUtil;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/25.
 */
@Service
public class ShipmentSendExpressServiceImpl extends BaseServiceImpl<ShipmentSendExpress>
		implements ShipmentSendExpressService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ShipmentSendExpressDao shipmentSendExpressDao;
	@Autowired
	private TableLockDao tableLockDao;
	@Autowired
	private ShipmentTurnOrderDao shipmentTurnOrderDao;
	@Autowired
	private OrderManagerDao orderManagerDao;
	@Autowired
	private XingeAppUtils xingeAppUtils;

	@Override
	@Transactional(readOnly = false)
	public Boolean updateState(Map<String, Object> params, List<Long> list, Boolean flag) {
		int count = shipmentSendExpressDao.updateState(params, list);
		List<ShipmentTurnOrder> result = new ArrayList<>();
		if (count > 0) {
			if (flag) {
				// 说明是转单，给转单记录表添加数据
				List<ShipmentSendExpress> dtos = shipmentSendExpressDao.findByIds(list);
				if (dtos.size() > 0) {
					for (ShipmentSendExpress item : dtos) {
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

	@Autowired
	private ShipmentCitySendDao shipmentCitySendDao;

	@Autowired
	private ShipmentHelpMeBuyDao shipmentHelpMeBuyDao;

	@Autowired
	private OrderManagerService orderManagerService;

	@Override
	@Transactional(readOnly = false)
	public Boolean grabOrder(int terminal, int bizType, Map<String, Object> result, List<Long> list, Boolean flag) {

		String userId = result.get("userId") + "";
		result.remove("userId");
		result.put("grab_order", "32"); // 32 抢单成功 33 抢单失败
		//设置抢单时间
		Date grabDate = new Date();
		result.put("grab_order_time", grabDate);

		// 增加where条件
		Map<String, Object> condition = new HashMap<>();
		condition.put("grab_order", "31");
		condition.put("order_state", "等待接单");

		List<ShipmentOperation> operations = shipmentOperationDao.findListBySysId(Long.valueOf(userId));

		if (1 == bizType) {
			ShipmentSendExpress t = shipmentSendExpressDao.getById(list.get(0), null);
			if (CollectionUtils.isNotEmpty(operations) && t != null && t.getAreaList() != null) {
				for (ShipmentOperation so : operations) {
					if (t.getAreaList().contains(so.getArea() + "")) {
						result.put("area", so.getArea());
						result.put("sales_man", so.getId());
						break;
					}
				}
			}
			// 抢单时效计算
			if (null != t) {
				Long time = grabDate.getTime() - t.getOrderTime().getTime();
				Long timeShiXiao = time / 1000 / 60;
				result.put("grab_order_min", timeShiXiao);
			}
			return shipmentSendExpressDao.updateGrab(result, list, condition) > 0;
		} else if (2 == bizType) {
			ShipmentCitySend t = shipmentCitySendDao.getById(list.get(0), null);
			if (CollectionUtils.isNotEmpty(operations) && t != null && t.getAreaList() != null) {
				for (ShipmentOperation so : operations) {
					if (t.getAreaList().contains(so.getArea() + "")) {
						result.put("area", so.getArea());
						result.put("sales_man", so.getId());
						break;
					}
				}
			}
			// 抢单时效计算
			if (null != t) {
				Long time = grabDate.getTime() - t.getOrderTime().getTime();
				Long timeShiXiao = time / 1000 / 60;
				result.put("grab_order_min", timeShiXiao);
			}
			return shipmentCitySendDao.updateGrab(result, list, condition) > 0;
		} else if (3 == bizType) {
			ShipmentHelpMeBuy t = shipmentHelpMeBuyDao.getById(list.get(0), null);
			if (CollectionUtils.isNotEmpty(operations) && t != null && t.getAreaList() != null) {
				for (ShipmentOperation so : operations) {
					if (t.getAreaList().contains(so.getArea() + "")) {
						result.put("area", so.getArea());
						result.put("sales_man", so.getId());
						break;
					}
				}
			}
			// 抢单时效计算
			if (null != t) {
				Long time = grabDate.getTime() - t.getOrderTime().getTime();
				Long timeShiXiao = time / 1000 / 60;
				result.put("grab_order_min", timeShiXiao);
			}
			return shipmentHelpMeBuyDao.updateGrab(result, list, condition) > 0;
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("id", list.get(0));
			result.put("flag", "抢单");
			result.put("userId", userId);
			result.put("ids", list.get(0));

			condition.put("orderStatusName", condition.get("order_state"));
			condition.remove("order_state");
			try {
				Map<String, Object> param = new HashMap<>();
				param.put("id", list.get(0));
				OrderManager t = orderManagerDao.selectOrderByExample(param);
				if (CollectionUtils.isNotEmpty(operations) && t != null && t.getAreaList() != null) {
					for (ShipmentOperation so : operations) {
						if (t.getAreaList().contains(so.getArea() + "")) {
							result.put("area", so.getArea());
							result.put("sales_man", so.getId());
							break;
						}
					}
				}
				// 抢单时效计算
				if (null != t) {
					Long time = grabDate.getTime() - t.getPlaceOrder().getTime();
					Long timeShiXiao = time / 1000 / 60;
					result.put("grabOrderMin", timeShiXiao);
				}
				return orderManagerService.grabOrder(result, condition);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private void sendMessage(int terminal, List<Long> list, String title, String contentFail) {

		XingeEntity xingeEntity = new XingeEntity();
		xingeEntity.setTitle(title);
		xingeEntity.setContent(contentFail);
		xingeEntity.setAccount(list.get(0) + "");
		if (terminal == 2) {
			// android
			xingeAppUtils.pushSingleAccount(xingeEntity,2);
		} else {
			xingeAppUtils.pushSingleAccountIOS(xingeEntity,4);
		}
	}

	@Override
	public List<ShipmentSendExpress> getBalance() {
		return shipmentSendExpressDao.getBalance();
	}

	@Override
	public List<ShipmentSendExpress> getCount() {
		return shipmentSendExpressDao.getCount();
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<OrderServiceDTO> serviceList(LarPager<OrderServiceDTO> larPager, Long userId) {
		List<OrderServiceDTO> orderServiceDTOs = shipmentSendExpressDao.serviceList(larPager, userId);
		larPager.setResult(orderServiceDTOs);
		larPager.setTotalCount(shipmentSendExpressDao.serviceListCount(larPager, userId));
		return larPager;
	}

	@Autowired
	private SalesmanDao salesmanDao;

	@Override
	@Transactional(readOnly = false)
	public LarPager<OrderServiceDTO> grabOrderList(LarPager<OrderServiceDTO> larPager, Long userId, String orgId) {
		// 1,物流
		List<ShipmentOperation> ShipmentOperations = shipmentOperationDao.findListBySysId(userId);
		Integer shipmentFlag = 0;
		List<String> shipmentAreas = new ArrayList<>();
		if (null != ShipmentOperations && ShipmentOperations.size() > 0) {
			shipmentFlag = 1;
			for (ShipmentOperation shipmentOperation : ShipmentOperations) {
				shipmentAreas.add(shipmentOperation.getArea() + "");
			}
		}
		larPager.getParams().put("shipmentFlag", shipmentFlag);
		larPager.getParams().put("shipmentAreas", this.convertListToString(shipmentAreas));

		// 2,回收
		List<Salesman> salesmans = salesmanDao.getByPersonnelId(userId + "");
		Integer recovery = 0;
		List<String> areaSettings = null;
		if (null != salesmans && salesmans.size() > 0) {
			recovery = 1;
			areaSettings = new ArrayList<>();
			for (Salesman salesman : salesmans) {
				areaSettings.add(salesman.getAreaSetting().getId());
			}
		}
		larPager.getParams().put("recovery", recovery);
		larPager.getParams().put("areaSettings", this.convertListToString(areaSettings));

		List<OrderServiceDTO> orderServiceDTOs = shipmentSendExpressDao.grabOrderList(larPager, userId);
		larPager.setResult(orderServiceDTOs);
		larPager.setTotalCount(shipmentSendExpressDao.grabOrderListCount(larPager, userId));
		return larPager;
	}

	/**
	 * 将list集合转变主字符串,以"|"分隔
	 * 
	 * @param list
	 * @return
	 */
	private String convertListToString(List<String> list) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		for (int i = list.size() - 1; i >= 0; i--) {
			if (i != 0) {
				result.append(list.get(i) + "|");
			} else {
				result.append(list.get(i));
			}
		}
		return result.toString();
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<OrderServiceDTO> serviceHistory(LarPager<OrderServiceDTO> larPager, Long userId) {
		List<OrderServiceDTO> orderServiceDTOs = shipmentSendExpressDao.serviceHistory(larPager, userId);
		larPager.setResult(orderServiceDTOs);
		larPager.setTotalCount(shipmentSendExpressDao.serviceHistoryCount(larPager, userId));
		return larPager;
	}

	@Autowired
	private VoucherService voucherService;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private ShipmentAreaService shipmentAreaService;

	@Autowired
	private ShipmentOperationService shipmentOperationService;

	@Autowired
	private LarClientUserAddressService larClientUserAddressService;

	@Autowired
	private ShipmentOperationDao shipmentOperationDao;

	@Transactional(readOnly = false)
	public Boolean save(ShipmentSendExpress t) {
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
			logger.debug("start auto  distribute order sendExpress");
			List<ShipmentArea> shipmentAreas = this.getAreaList(t.getOrg(), t.getAddressId() + "");// 查询访地址上所有符合的片区
			logger.info("areaSize : {}",shipmentAreas);
			if (null != shipmentAreas && shipmentAreas.size() > 0) {
				List<ShipmentOperation> shipmentOperations = new ArrayList<>();
				for (ShipmentArea area : shipmentAreas) {
					Map<String, Object> param = new HashMap<>();
					param.put("area", area.getId());
					param.put("type", "业务员");
					shipmentOperations.addAll(shipmentOperationService.findByMap(param));
					logger.info("areaId : {} ",area.getId());
				}
				logger.info("Operations : {} ",shipmentOperations.size());
				if (shipmentOperations.size() > 0) {
					Random random = new Random();
					int j = random.nextInt(shipmentOperations.size());
					ShipmentOperation shipmentOperation = shipmentOperations.get(j);
					logger.info("randomOperation : {}" , shipmentOperation.getId());
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
			// 根据addressId 和org,查询片区
			List<ShipmentArea> shipmentAreas = this.getAreaList(t.getOrg(), t.getAddressId() + "");
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
			if (null != operations && operations.size() > 0) {
				for (ShipmentOperation operation : operations) {// 去重
					if (!accounts.contains(operation.getSysUser() + "")) {
						accounts.add(operation.getSysUser() + "");
					}
				}
			}
			xingeEntity.setAccounts(accounts);
			xingeEntity.setGrabOrder(31);

		}
		int count = shipmentSendExpressDao.save(t);
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
	public List<ShipmentArea> getAreaList(Long org, String addressId) {
		List<ShipmentArea> shipmentAreas = new ArrayList<>();
		LarPager<ShipmentArea> shipmentAreaLarPager = new LarPager<>();
		shipmentAreaLarPager.setPageSize(10000);
		List<Long> ids = new ArrayList<>();
		ids.add(org);
		LarPager<ShipmentArea> byOrgIds = shipmentAreaService.findByOrgIds(shipmentAreaLarPager, ids);
		LarClientUserAddress larClientUserAddress;
		try {
			larClientUserAddress = larClientUserAddressService.selectByPrimaryKey(addressId);
			if (null != larClientUserAddress) {
				String latitude = larClientUserAddress.getLatitude();
				String longitude = larClientUserAddress.getLongitude();
				Point2D.Double aDouble = new Point2D.Double(Double.parseDouble(latitude),
						Double.parseDouble(longitude));
				if (null != byOrgIds && null != byOrgIds.getResult() && byOrgIds.getResult().size() > 0) {
					for (ShipmentArea item : byOrgIds.getResult()) {
						String position = item.getPosition();
						if (StringUtils.isNotBlank(position)) {
							List<Map<String, Double>> parse = JSON.parse(position, List.class);
							List<Point2D.Double> doubleList = new ArrayList<>();
							if (null != parse && parse.size() > 0) {
								for (Map<String, Double> map1 : parse) {
									doubleList.add(new Point2D.Double(map1.get("lat"), map1.get("lng")));
								}
							}
							boolean flag = MapUtil.checkWithJdkGeneralPath(aDouble, doubleList);
							if (flag) {
								shipmentAreas.add(item);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return shipmentAreas;
	}

	@Override
	public OrderDetailDTO orderDetail(Long id, Map<String, Object> map) {
		return shipmentSendExpressDao.orderDetail(id, map);
	}

	@Override
	public List<OrderDetailDTO> getEvaluation(Long aLong) {
		return shipmentSendExpressDao.getEvaluation(aLong);
	}

	@Override
	public ShipmentSendExpress getByNo(String orderNo) {
		return shipmentSendExpressDao.getByNo(orderNo);
	}

	public static void main(String[] args) throws ParseException {
		String str = "[{\"lng\":116.412642,\"lat\":39.913713},{\"lng\":116.414798,\"lat\":39.907958},{\"lng\":116.424284,\"lat\":39.909397},{\"lng\":116.424212,\"lat\":39.913962}]";
		List<Map<String, Double>> parse = JSON.parse(str, List.class);
		System.out.println(parse.get(0).get("lng"));
		// System.out.println(Double.parseDouble(parse.get(0).get("lng")));
		System.out.println(parse);
	}

	@Override
	public LarPager<ShipmentSendExpress> findByOrgIdsOne(LarPager<ShipmentSendExpress> larPager, List<Long> list) {
		List<ShipmentSendExpress> result = shipmentSendExpressDao.findByOrgIdsOne(larPager, list);
		larPager.setResult(result);
		larPager.setTotalCount(shipmentSendExpressDao.countByOrgIdsOne(larPager, list));
		return larPager;
	}

	@Override
	@Transactional(readOnly = false)
	public int updateGrabState(long time) {
		int bizType = 1;
		logger.info("method:{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		int lockflag = tableLockDao.lock(bizType);// 获取锁
		if (lockflag == 0) {// 获取不到锁，直接返回
			logger.info("method:{},寄快递未获取到锁", Thread.currentThread().getStackTrace()[1].getMethodName());
			return 0;
		}else{
			logger.info("method:{},寄快递取得锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		try {
			int count = shipmentSendExpressDao.updateGrabState(time);
			logger.info("method:{},更新:{}条数据状态", Thread.currentThread().getStackTrace()[1].getMethodName(), count);
			return count;
		} finally {
			tableLockDao.unLock(bizType);// 解锁
			logger.info("method:{},寄快递解锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}

	}
	@Override
	@Transactional(readOnly = false)
	public boolean backCoupon(List<Long> ids) {
		int count = shipmentSendExpressDao.backCoupon(ids);
		return count>0;
	}
	@Override
	public List<ShipmentSendExpress> findInvalidOrder(Long time) {
		return shipmentSendExpressDao.findInvalidOrder(time, new Date());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean batchUpdate(List<ShipmentSendExpress> list) {
		logger.info("Enter the :{} method  batchUpdate:{}", Thread.currentThread()
				.getStackTrace()[1].getMethodName(),list);
		List<String> accounts = new ArrayList<>();
		for (ShipmentSendExpress order : list) {
			if(null != order.getSalesMan()){
				logger.info("success orders : {}",order.getOrderNo()+"-->"+order.getSalesMan());
				order.setDistributeTime(new Date());
				order.setGrabOrder(33);
				order.setOrderState("服务中");
				accounts.add(order.getDistributeUser()+"");
			}else{
				order.setGrabOrder(33);
				logger.info("fail orders : {}",order.getOrderNo());
			}
			
		}
		int update = shipmentSendExpressDao.batchUpdate(list);
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
	
	@Override
	@Transactional(readOnly = true)
	public int queryOrderCount(String employeeId) throws Exception {
		try {
			// 根据登陆人员的工号查询业务员
			List<String> ids = shipmentSendExpressDao.getSalesmansByCustomerIds(employeeId);
			if (ids == null || ids.size() < 1) {
				return 0;
			} else {
				// 根据业务员主键查询派单状态的订单数量
				return shipmentSendExpressDao.selectDisCount(ids);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
