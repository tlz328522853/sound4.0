package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.entity.RecycleChildOrder;
import com.sdcloud.api.lar.entity.RecycleOrder;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.MessageCenterService;
import com.sdcloud.api.lar.service.RecycleOrderService;
import com.sdcloud.biz.lar.dao.LarClientUserDao;
import com.sdcloud.biz.lar.dao.RecycleChildOrderDao;
import com.sdcloud.biz.lar.dao.RecycleOrderDao;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

import io.jsonwebtoken.lang.Collections;

@Service
@Transactional
public class RecycleOrderServiceImpl extends BaseServiceImpl<RecycleOrder> implements RecycleOrderService {

	@Autowired
	private RecycleOrderDao recycleOrderDao;
	@Autowired
	private RecycleChildOrderDao recycleChildOrderDao;
	@Autowired
	private SalesmanDao salesmanDao;
	@Autowired
	private LarClientUserDao clientUserDao;
	@Autowired
	private MessageCenterService messageCenterService;
	@Autowired
	private XingeAppUtils xingeAppUtils;

	@Override
	@Transactional(readOnly=false)
	public Boolean saveIncludChild(RecycleOrder recycleOrder) {
		/*recycleOrder.setOverproof(1);
		recycleOrder.setOverproofName("否");*/

		if (recycleOrder.getIntegral() > 0 && recycleOrder.getMoney() > 0) {
			recycleOrder.setPayTypeId(3);
			recycleOrder.setPayTypeName("混合");
		} else if (recycleOrder.getMoney() > 0) {
			recycleOrder.setPayTypeId(2);
			recycleOrder.setPayTypeName("现金");
		} else if (recycleOrder.getIntegral() > 0) {
			recycleOrder.setPayTypeId(1);
			recycleOrder.setPayTypeName("积分");
		}

		recycleOrder.setCheckStatus(0);
		recycleOrder.setCheckStatusName("未对账");
		Date date = new Date();
		recycleOrder.setCreateDate(date);
		List<RecycleChildOrder> chileOrders = recycleOrder.getChild();

		if (null != chileOrders && chileOrders.size() > 0) {
			for (RecycleChildOrder order : chileOrders) {
				order.setId(UUIDUtil.getUUNum());
				order.setCreateUser(Long.parseLong(recycleOrder.getSalesman().getPersonnelId()));
				order.setCreateDate(date);
				order.setOrderId(recycleOrder.getOrderId());
				
			}
		}

		// 根据业务员EmployeeId,查询业务员
		List<Salesman> mans = salesmanDao.getByPersonnelId(recycleOrder.getSalesman().getPersonnelId());
		Salesman salesman = null;
		if (null == mans || mans.size() < 1) {
			throw new RuntimeException("没有匹配到业务员!");
		} else if (mans.size() == 1) {
			salesman = mans.get(0);
		} else {
			salesman = mans.get(0);
			for (int i = 1; i < mans.size(); i++) {
				if (mans.get(i).getIntegral() > salesman.getIntegral()) {
					salesman = mans.get(i);
				}
			}
		}
		// 业务员当前积分必须大于支付积分
		/*
		 * if(recycleOrder.getIntegral()> salesman.getIntegral()){ //throw new
		 * RuntimeException("业务员积分不够!"); return false; }
		 */

		recycleOrder.getSalesman().setId(salesman.getId());
		recycleOrder.setArea(Long.parseLong(salesman.getAreaSetting().getId()));

		Integer saveList = 0;
		if (recycleOrder.getChild() != null && recycleOrder.getChild().size() > 0) {
			saveList = recycleChildOrderDao.saveList(recycleOrder.getChild());
		}

		int save = recycleOrderDao.save(recycleOrder);
		if (save < 1 || saveList < 1) {
			throw new RuntimeException("保存订单失败!");
		}

		if (recycleOrder.getPayTypeId() != 2) {
			int updateSalesmanPoints = salesmanDao.updateSalesmanPoints(recycleOrder.getIntegral() + "",
					salesman.getId());
			LarClientUser clientUser = clientUserDao.selectByPrimaryKey(recycleOrder.getClientUser().getId());
			clientUser.setNowPoints(clientUser.getNowPoints() + recycleOrder.getIntegral());
			clientUser.setHistoryPoints(clientUser.getHistoryPoints() + recycleOrder.getIntegral());
			int updatePoints = clientUserDao.updatePoints(clientUser);
			if (updateSalesmanPoints < 1 || updatePoints < 1) {
				throw new RuntimeException("保存积分失败!");
			}

			String title = "回收消息";
			String content = "亲,您的回收已成功,恭喜您获得" + recycleOrder.getIntegral() + "积分";
			// 推送
			XingeEntity xingeEntity = new XingeEntity();
			xingeEntity.setTitle(title);
			xingeEntity.setContent(content);
			xingeEntity.setAccount(clientUser.getId());
			xingeAppUtils.pushSingleAccount(xingeEntity, 1);
			xingeAppUtils.pushSingleAccountIOS(xingeEntity, 3);
			// 保存消息中心
			MessageCenter t = new MessageCenter();
			t.setTitle(title);
			t.setDetail(content);
			t.setCustomerId(Long.parseLong(clientUser.getId()));
			messageCenterService.save(t);
		}
		return true;
	}

	@Override
	public Boolean checkOrder(RecycleOrder recycleOrder) {
		recycleOrder.setCheckStatus(1);
		recycleOrder.setCheckStatusName("已对账");
		
		/*if ("未超标".equals(recycleOrder.getOverproofName())) {
			recycleOrder.setOverproof(1);
		} else if ("超标".equals(recycleOrder.getOverproofName())) {
			recycleOrder.setOverproof(2);
		}*/
		List<RecycleChildOrder> chileOrders = recycleOrder.getChild();
		if (!Collections.isEmpty(chileOrders) && StringUtils.isBlank(recycleOrder.getOverproofName()) ) {//在对账时自动 判断 是否超标。
			for (RecycleChildOrder order : chileOrders) {
				recycleOrder.setOverproof(1);
				recycleOrder.setOverproofName("否");
				if (null != order.getPayableTotalPrice() && null != order.getPaidTotalPrice()
						&& order.getPaidTotalPrice().compareTo(order.getPayableTotalPrice()) > 0) {
					recycleOrder.setOverproof(2);
					recycleOrder.setOverproofName("是");
					break;
				}
			}
		}

		return recycleOrderDao.checkOrder(recycleOrder) > 0;
	}

	// 查询回收物类型
	@Override
	@Transactional(readOnly = false)
	public List<RecyclingType> getRecyclingTypes() {
		return recycleOrderDao.getRecyclingTypes();
	}

	/*
	 * @Override public LarPager<RecycleChildOrder>
	 * selectCildByExample(LarPager<RecycleChildOrder> larPager) { try {
	 * List<ChildOrders> result = recycleOrderDao.selectCildByExample(larPager);
	 * //larPager.setResult(result); } catch (Exception e) {
	 * e.printStackTrace(); } return larPager; }
	 */

	// 修改子单号
	@Transactional
	public boolean updateByExampleSelective(RecycleChildOrder childOrders) throws Exception {
		try {
			if (childOrders != null && childOrders.getId() != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("childOrders", childOrders);
				// 修改子单号信息
				int count = recycleOrderDao.updateChildByExampleSelective(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new IllegalArgumentException("childOrders or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Long> getCheckMens(LarPager<RecycleOrder> larPager, List<Long> ids) {

		return recycleOrderDao.getCheckMens(larPager, ids);
	}

}
