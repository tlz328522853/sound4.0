package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecycleTurnOrder;
import com.sdcloud.api.lar.service.RecycleTurnOrderService;
import com.sdcloud.biz.lar.dao.OrderManagerDao;
import com.sdcloud.biz.lar.dao.RecycleTurnOrderDao;
import com.sdcloud.framework.common.UUIDUtil;
@Service
public class RecycleTurnOrderServiceImpl extends BaseServiceImpl<RecycleTurnOrder> implements RecycleTurnOrderService{
	
	@Autowired
	private RecycleTurnOrderDao recycleTurnOrderDao;
	
	@Autowired
	private OrderManagerDao orderManagerDao;
	
	@Override
	public List<RecycleTurnOrder> getByOrderId(Long orderId) {
		return recycleTurnOrderDao.getByOrderId(orderId);
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean updateState(Map<String, Object> params, List<Long> list, Boolean flag) {
		
		List<RecycleTurnOrder> result = new ArrayList<>();
		String orderId = String.valueOf(list.get(0));
		// 说明是转单，给转单记录表添加数据
		List<OrderManager> dtos = orderManagerDao.getByOrderId(orderId);

		RecycleTurnOrder recycleTurnOrder = new RecycleTurnOrder();
//		Long orderNo = null;
		for (OrderManager item : dtos) {
			
			recycleTurnOrder.setOrderNo(item.getOrderId());
			recycleTurnOrder.setOrderType("卖废品");
			recycleTurnOrder.setOrderTime(item.getPlaceOrder());
			recycleTurnOrder.setOrderState(item.getOrderStatusName());
			recycleTurnOrder.setOrg(Long.valueOf(item.getAreaSetting().getMechanismId()));
			recycleTurnOrder.setArea(item.getAreaSetting().getId());

			recycleTurnOrder.setId(UUIDUtil.getUUNum());
			recycleTurnOrder.setCreateDate(new Date());
			recycleTurnOrder.setCreateUser(UUIDUtil.getUUNum());
			result.add(recycleTurnOrder);
		}
//		orderNo = recycleTurnOrderDao.getByOrderNo(recycleTurnOrder.getOrderNo());
		// 判断订单存在不存在，不存在添加
//		if (orderNo == null && dtos.size()>0) {
		int count = recycleTurnOrderDao.batchSave(result);
//		}

		
		Long id = null;
//		if (orderNo != null) {
//			id = orderNo;
//		} else {
			id = recycleTurnOrder.getId();
//		}
			params.put("turn_order_time", new Date());
			params.put("update_date", new Date());
			count = recycleTurnOrderDao.updateState(params,id);
		
		if (count != 0) {
			return true;
		} else {
			return false;
		}
	}
}
