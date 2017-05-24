package com.sdcloud.web.lar.controller.pingpp;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Summary;
import com.pingplusplus.model.Webhooks;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.framework.entity.ResultDTO;

/**
* @author jzc
* @version 2016年6月27日 上午10:27:03
* PingppHooksController描述:pingpp支付平台的 webhooks回调控制层接口
*/

@RestController
@RequestMapping("/pingxx/webhooks")
public class PingppHooksController {
	
		Logger logger = LoggerFactory.getLogger(this.getClass());
		/**帮我买 主题***/
		public static String SUBJECT_HELP_ME_BUY="help_me_buy";
		
		@Autowired
		private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
	
		/**
		 * 验证订单是否付款
		 *
		 * @param orderCharge
		 * @return
		 */
		@ResponseBody
		@RequestMapping(value = "/retrieve", method = RequestMethod.POST)
		public ResultDTO retrieve(HttpServletRequest request) {
			String eventData=(String)request.getAttribute("eventData");
			Event hooksEventData = Webhooks.eventParse(eventData);
			logger.info("method:event-data:"+hooksEventData.toString());
			Object obj=Webhooks.parseEvnet(eventData);
			boolean flag=true;
			String type=hooksEventData.getType();
			if(obj instanceof Charge){//支付成功事件
				
				Charge charge = (Charge) obj;
				logger.info("---------支付成功的回调事件!--付款状态：" + charge.getPaid() + " 订单号：" + charge.getOrderNo());
				if(StringUtils.isNotEmpty(charge.getDescription())
					 &&charge.getDescription().equals(SUBJECT_HELP_ME_BUY)){//帮我买
					logger.info("支付：帮我买支付凭证!");
					flag=shipmentHelpMeBuyEvent(charge);
				}
				else{
					logger.info("支付：其它业务支付凭证!暂未开通！");
				}
			}
			else if (obj instanceof Refund) {
				logger.info("退款：webhooks 发送了 Refund!暂未开通！");
			}
			else if (obj instanceof Summary) {
				logger.info("汇总：webhooks 发送了 Summary!暂未开通！");
			}
			else if("transfer.succeeded".equals(type)){
				logger.info("企业付款成功：webhooks 发送了 Transfer!暂未开通！");
			}
			else if("red_envelope.sent".equals(type)){
				logger.info("红包发送成功：webhooks 发送了 RedEnvelope!暂未开通！");
			}
			else if("red_envelope.received".equals(type)){
				logger.info("红包领取成功：webhooks 发送了 RedEnvelope!暂未开通！");
			}
			else{
				logger.info("未知：webhooks 发送了 未知请求数据！");
			}
	
			if(flag){
				logger.info(200+",---webhooks("+type+")：回调函数执行成功!");
				return ResultDTO.getFailure(200, "webhooks：success！");
			}
			else{
				logger.info(500+",---webhooks("+type+")：回调函数执行失败!");
				return ResultDTO.getFailure(500, "webhooks：faild！");
			} 
				
		}

		
		/**
		 * 帮我买的 支付成功 回调的执行函数
		 * @author jzc 2016年6月27日
		 * @param charge
		 * @return
		 */
		private boolean shipmentHelpMeBuyEvent(Charge charge){
			logger.info("webhooks：help me buy retrieve charge");
			if(StringUtils.isEmpty(charge.getOrderNo())){
				logger.warn("webhooks：orderNo is empty!");
				return false;
			}
			ShipmentHelpMeBuy shipmentHelpMeBuy = shipmentHelpMeBuyService.getByNo(charge.getOrderNo());// 根据订单号查询订单
			if (null == shipmentHelpMeBuy) {
				logger.warn("webhooks：订单不存在!");
				return false;
			}
			String chargeId = shipmentHelpMeBuy.getChargeId();// 查询付款信息
			if (null == chargeId || "".equals(chargeId)) {
				logger.warn("webhooks：该订单没有付款操作!");
				return false;
			}
			if (!chargeId.equals(charge.getId())) {
				logger.warn("webhooks：该订单中支付凭证ID 与 请求的不一致!");
				return false;
			}
			
			String paidStatus=shipmentHelpMeBuy.getPaidStatus();//数据库订单状态
			//数据库订单 还未进行更改状态，那么 派单并且更改数据库订单状态
			if(StringUtils.isEmpty(paidStatus)||!paidStatus.equals("已付款")){
				BigDecimal b1 = new BigDecimal(charge.getAmount());
				BigDecimal b2 = new BigDecimal(100.00);
				Double paid = b1.divide(b2).doubleValue();
				shipmentHelpMeBuy.setPaid(paid);
				shipmentHelpMeBuy.setOrderState("等待接单");
				shipmentHelpMeBuy.setPaidStatus("已付款");
				shipmentHelpMeBuyService.update(shipmentHelpMeBuy, 0);
				logger.info("webhooks：订单编号："+charge.getOrderNo()+">>>>支付成功！");
			}
			return true;
		}

}
