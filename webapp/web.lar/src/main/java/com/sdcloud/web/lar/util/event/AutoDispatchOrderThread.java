package com.sdcloud.web.lar.util.event;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.biz.lar.dao.TableLockDao;

/**
 * 自动派单 异步执行 的线程
 * 
 * @author jzc 2016年8月24日
 */
@Component
public class AutoDispatchOrderThread {

	private static final Logger logger = LoggerFactory.getLogger(AutoDispatchOrderThread.class);

	@Autowired
	private TableLockDao tableLockDao;
	@Autowired
	private ShipmentSendExpressService shipmentSendExpressService;
	@Autowired
	private ShipmentCitySendService shipmentCitySendService;
	@Autowired
	private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
	@Autowired
	private OrderManagerService orderManagerService;
	@Autowired
	private SysConfigService sysConfigService;

	public static int DISPATCHE_INTERVAL = 60000;// 自动派单间隔 ms
	public static int LOCK_TYPE = 5;// 自动监测抢单失效操作 锁表类型

	/**
	 * 自动监测抢单失效的订单，并派单给对应的片区随机业务员
	 * 
	 * @author jzc 2016年8月24日
	 */
	@Async
	public void autoDispatchOrder() {
		logger.info("----------自动派单模块 成功启动！");
		while (true) {
			try {
				Thread.sleep(DISPATCHE_INTERVAL);
				int num = tableLockDao.lock(LOCK_TYPE);// 锁表
				if (num > 0) {// 锁表成功
					logger.debug("<锁表成功>:自动派单   开始执行.............." + LOCK_TYPE);

					Map<String, String> map = sysConfigService.findMap();
					//String orderModel = map.get("orderModel");
					String orderOutTime = map.get("orderOutTime");
					long outTime = Long.valueOf(orderOutTime)*60;
					//寄快递
					List<ShipmentSendExpress> sendExpress = shipmentSendExpressService.findInvalidOrder(outTime);
					//同城送
					List<ShipmentCitySend> citySend = shipmentCitySendService.findInvalidOrder(outTime);
					//帮我买
					List<ShipmentHelpMeBuy> helpMeBuy = shipmentHelpMeBuyService.findInvalidOrder(outTime);
					//卖废品
					List<OrderManager> orderManager = orderManagerService.findInvalidOrder(outTime);
					if(!CollectionUtils.isEmpty(sendExpress)){
						try {
							shipmentSendExpressService.batchUpdate(sendExpress);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
					if(!CollectionUtils.isEmpty(citySend)){
						try {
							shipmentCitySendService.batchUpdate(citySend);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
					if(!CollectionUtils.isEmpty(helpMeBuy)){
						try {
							shipmentHelpMeBuyService.batchUpdate(helpMeBuy);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
					if(!CollectionUtils.isEmpty(orderManager)){
						try {
							orderManagerService.batchUpdate(orderManager);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}

					num = tableLockDao.unLock(LOCK_TYPE);// 解锁
					if (num > 0) {// 解锁成功
						logger.debug("<解锁成功>:自动派单   执行完毕..........." + LOCK_TYPE);
					} else {
						logger.warn("<解锁失败>:自动派单   执行完毕..........." + LOCK_TYPE);
					}
				} else {
					logger.debug("锁表失败,放弃执行..." + LOCK_TYPE);
				}

			}catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}finally {
				tableLockDao.unLock(LOCK_TYPE);// 解锁
			}
		}

	}

}
