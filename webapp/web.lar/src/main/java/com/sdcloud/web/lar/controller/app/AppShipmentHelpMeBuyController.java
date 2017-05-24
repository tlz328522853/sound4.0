package com.sdcloud.web.lar.controller.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.controller.pingpp.PingppHooksController;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;

/**
 * Created by 韩亚辉 on 2016/3/25.
 */
@RestController
@RequestMapping("/app/buy")
public class AppShipmentHelpMeBuyController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private CityService cityService;

	private static Map<String, String> chanalMap = new HashMap<String, String>() {
		{
			put("微信", "wx");
			put("支付宝", "alipay");
			put("微信公众号", "wx_pub");
		}
	};

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentHelpMeBuy> larPager) throws Exception {
		try {
			//帮我买,我的订单,排序
			larPager.setOrderBy("a.create_date,a.update_date");
			larPager.setOrder("desc,desc");
			return ResultDTO.getSuccess(AppCode.SUCCESS, "获取帮我买列表成功", shipmentHelpMeBuyService.findAll(larPager));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}

	/**
	 * 保存帮我买订单(支付金额为0)
	 *
	 * @param orderCharge
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveNoAmount", method = RequestMethod.POST)
	public ResultDTO saveNoAmount(@RequestBody(required = false) ShipmentHelpMeBuy shipmentHelpMeBuy,
			HttpServletRequest request) {
		
		return ResultDTO.getFailure(AppCode.BIZ_ERROR, "提交失败! 该功能暂未开放! 请下载新版本。");
		
		/*if (shipmentHelpMeBuy.getTotalCost() > 0) {
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "合计金额大于零需要支付！");
		}
		try {
			shipmentHelpMeBuy.setOrderNo(OrderUtils.generateNumber());
			City city = cityService.selectByCityId(shipmentHelpMeBuy.getCity());
			shipmentHelpMeBuy.setOrg(city.getOrg());
			shipmentHelpMeBuy.setOrderTime(new Date());
			shipmentHelpMeBuy.setOrderState("等待接单");
			shipmentHelpMeBuy.setBizType("帮我买");
			shipmentHelpMeBuy.setPaidStatus("已付款");
			boolean flag = shipmentHelpMeBuyService.save(shipmentHelpMeBuy);
			if (!flag) {
				return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
			} else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "订单提交成功", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器异常！");
		}*/
	}

	/**
	 * 保存帮我买订单，返回支付凭据
	 *
	 * @param orderCharge
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResultDTO save(@RequestBody(required = false) ShipmentHelpMeBuy shipmentHelpMeBuy,
			HttpServletRequest request) {
		logger.info("help buy start");
		
		return ResultDTO.getFailure(AppCode.BIZ_ERROR, "提交失败! 该功能暂未开放! 请下载新版本。");
		
		/*if (shipmentHelpMeBuy.getTotalCost() <= 0) {
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "合计金额小于零无需支付！");
		}
		shipmentHelpMeBuy.setOrderNo(OrderUtils.generateNumber());
		Pingpp.apiKey = "sk_live_v5W1KK48Su98vfznjTzHqbPC";
		String clientIp = request.getRemoteAddr();
		Charge c = null;
		try {
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("order_no", shipmentHelpMeBuy.getOrderNo());
			BigDecimal b1 = new BigDecimal(shipmentHelpMeBuy.getTotalCost());
			BigDecimal b2 = new BigDecimal(100.00);
			int cost = b1.multiply(b2).intValue();
			chargeParams.put("amount", cost);
			Map<String, String> app = new HashMap<String, String>();
			app.put("id", "app_9ePuv9q1OCqHbrD8");
			chargeParams.put("app", app);
			String openid = shipmentHelpMeBuy.getOpenid();
			logger.info("wx openid is:" + openid);
			if (openid != null && !"".equals(openid)) {
				Map<String, String> extra = new HashMap<>();
				extra.put("open_id", openid);
				chargeParams.put("extra", extra);
			}
			chargeParams.put("channel", chanalMap.get(shipmentHelpMeBuy.getPayType()));
			chargeParams.put("currency", "cny");
			chargeParams.put("client_ip", clientIp);
			String subject = shipmentHelpMeBuy.getGoodsName() == null ? "" : shipmentHelpMeBuy.getGoodsName();
			subject=subject.replaceAll(" ", "").replaceAll("\\s*", "");
			if (subject.length() > 32) {
				subject = subject.substring(0, 31);
			}
			chargeParams.put("subject", subject);// 最大32位
			chargeParams.put("body", PingppHooksController.SUBJECT_HELP_ME_BUY+":"+subject);
			chargeParams.put("description", PingppHooksController.SUBJECT_HELP_ME_BUY);//订单附加说明

			c = Charge.create(chargeParams);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("help me buy create charge error");
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "支付异常");
		}
		try {
			City city = cityService.selectByCityId(shipmentHelpMeBuy.getCity());
			shipmentHelpMeBuy.setOrg(city.getOrg());
			shipmentHelpMeBuy.setOrderTime(new Date());
			shipmentHelpMeBuy.setChargeId(c.getId());
			shipmentHelpMeBuy.setOrderState("未付款");
			shipmentHelpMeBuy.setBizType("帮我买");
			boolean flag = shipmentHelpMeBuyService.save(shipmentHelpMeBuy);
			if (!flag) {
				return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
			} else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "订单提交成功", c);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器异常！");
		}*/
	}

	/**
	 * 帮我买支付接口（返回支付凭证）
	 *
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/payOrder", method = RequestMethod.POST)
	public ResultDTO payOrder(@RequestBody(required = false) ShipmentHelpMeBuy helpMeBuy, HttpServletRequest request) {
		logger.info("help buy start pay order");
		String orderNo = helpMeBuy.getOrderNo();
		String payType = helpMeBuy.getPayType();
		String openid = helpMeBuy.getOpenid();
		// 验证请求参数是否有误
		if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(payType)
				|| (payType.equals("微信公众号") && StringUtils.isEmpty(openid))) {
			logger.error("help me buy request data error");
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "请求参数有误！");
		}
		// 根据订单号查询订单
		ShipmentHelpMeBuy shipmentHelpMeBuy = shipmentHelpMeBuyService.getByNo(orderNo);
		if (shipmentHelpMeBuy == null) {
			// 从数据库未获取到该条数据
			logger.error("help me buy get order faild");
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
		}
		if (shipmentHelpMeBuy.getTotalCost() <= 0) {
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "合计金额小于零无需支付！");
		}
		Pingpp.apiKey = "sk_live_v5W1KK48Su98vfznjTzHqbPC";
		String clientIp = request.getRemoteAddr();
		Charge c = null;
		try {
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("order_no", shipmentHelpMeBuy.getOrderNo());
			BigDecimal b1 = new BigDecimal(shipmentHelpMeBuy.getTotalCost());
			BigDecimal b2 = new BigDecimal(100.00);
			int cost = b1.multiply(b2).intValue();
			chargeParams.put("amount", cost);
			Map<String, String> app = new HashMap<String, String>();
			app.put("id", "app_9ePuv9q1OCqHbrD8");
			chargeParams.put("app", app);

			if (payType.equals("微信公众号") && StringUtils.isNotEmpty(openid)) {// 微信公众号
				logger.info("wx openid is:" + openid);
				Map<String, String> extra = new HashMap<>();
				extra.put("open_id", openid);
				chargeParams.put("extra", extra);
			}
			chargeParams.put("channel", chanalMap.get(payType));
			chargeParams.put("currency", "cny");
			chargeParams.put("client_ip", clientIp);
			String subject = shipmentHelpMeBuy.getGoodsName() == null ? "" : shipmentHelpMeBuy.getGoodsName();
			subject=subject.replaceAll(" ", "").replaceAll("\\s*", "");
			if (subject.length() > 32) {
				subject = subject.substring(0, 31);
			}
			chargeParams.put("subject", subject);// 最大32位
			chargeParams.put("body", PingppHooksController.SUBJECT_HELP_ME_BUY+":"+subject);
			chargeParams.put("description", PingppHooksController.SUBJECT_HELP_ME_BUY);//订单附加说明
			c = Charge.create(chargeParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("help me buy create charge error");
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "支付异常");
		}
		try {
			if (!payType.equals(shipmentHelpMeBuy.getPayType())) {// 支付方式修改
				shipmentHelpMeBuy.setPayType(payType);
			}
			shipmentHelpMeBuy.setChargeId(c.getId());// 支付凭证id
			boolean flag = shipmentHelpMeBuyService.update(shipmentHelpMeBuy);
			if (!flag) {
				return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "订单提交成功", c);// 返回支付凭证
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器异常！");
		}
	}

	/**
	 * 验证订单是否付款
	 *
	 * @param orderCharge
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST)
	public ResultDTO retrieve(@RequestBody(required = false) ShipmentHelpMeBuy shipmentHelpMeBuy) {
		logger.info("help me buy retrieve charge");
		shipmentHelpMeBuy = shipmentHelpMeBuyService.getByNo(shipmentHelpMeBuy.getOrderNo());// 根据订单号查询订单
		if (null == shipmentHelpMeBuy) {
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "订单不存在");
		}
		String chargeId = shipmentHelpMeBuy.getChargeId();// 查询付款信息
		if (null == chargeId || "".equals(chargeId)) {
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "该订单没有付款操作");
		}
		Pingpp.apiKey = "sk_live_v5W1KK48Su98vfznjTzHqbPC";
		Charge c = null;
		try {
			c = Charge.retrieve(chargeId);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException e) {
			logger.error("help me buy retrieve charge fail");
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "验证付款信息异常");
		}
		if (c.getPaid()) {// 已付款
			String paidStatus=shipmentHelpMeBuy.getPaidStatus();
			//数据库订单 还未进行更改状态，那么 派单并且更改数据库订单状态
			if(StringUtils.isEmpty(paidStatus)||!paidStatus.equals("已付款")){
				BigDecimal b1 = new BigDecimal(c.getAmount());
				BigDecimal b2 = new BigDecimal(100.00);
				Double paid = b1.divide(b2).doubleValue();
				shipmentHelpMeBuy.setPaid(paid);
				shipmentHelpMeBuy.setOrderState("等待接单");
				shipmentHelpMeBuy.setPaidStatus("已付款");
				shipmentHelpMeBuyService.update(shipmentHelpMeBuy, 0);
				logger.info("custom：订单编号："+c.getOrderNo()+">>>>支付成功！");
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "付款成功", shipmentHelpMeBuy.getOrderNo());
		} else {// 未付款
			return ResultDTO.getSuccess(AppCode.BIZ_DATA, "未付款", shipmentHelpMeBuy.getOrderNo());
		}
	}

	public static void main(String args[]) {
		Pingpp.apiKey = "sk_live_v5W1KK48Su98vfznjTzHqbPC";
		// 付款支付凭证
		// Map<String, Object> chargeParams = new HashMap<String, Object>();
		// chargeParams.put("order_no", "123456789");
		// chargeParams.put("amount", 100);
		// Map<String, String> app = new HashMap<String, String>();
		// app.put("id", "app_9ePuv9q1OCqHbrD8");
		// chargeParams.put("app", app);
		// chargeParams.put("channel", "alipay");
		// chargeParams.put("currency", "cny");
		// chargeParams.put("client_ip", "127.0.0.1");
		// chargeParams.put("subject", "Your Subject");
		// chargeParams.put("body", "Your Body");
		// try {
		// System.out.println(Charge.create(chargeParams));
		// } catch (AuthenticationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvalidRequestException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (APIConnectionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (APIException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		try {
			System.out.println(Charge.retrieve("ch_qPKGO4DqvjLG0yv1OSjTmz1"));
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(100000 / 29);
	}

	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) ShipmentHelpMeBuy shipmentHelpMeBuy) {
		try {
			return ResultDTO.getSuccess(200, shipmentHelpMeBuyService.update(shipmentHelpMeBuy));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			return ResultDTO.getSuccess(200, shipmentHelpMeBuyService.delete(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/updateState")
	public ResultDTO updateState(@RequestBody(required = false) Map<String, Object> map) {
		try {
			List<Long> list = new ArrayList<>();
			String orderStateNo = map.get("orderStateNo") + "";
			Boolean flag = false;
			if ("5".equals(orderStateNo)) {
				flag = true;
			}
			Map<String, Object> result = OrderUtils.paramConvert(map, list, null);
			if (null != result) {
				return ResultDTO.getSuccess(200, shipmentHelpMeBuyService.updateState(result, list, flag));
			} else {
				return ResultDTO.getFailure(400, "参数错误！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	/**
	 * 根据机构id获取本机构及子机构的数据
	 *
	 * @param larPager
	 * @return
	 */
	@RequestMapping("/findByOrgIds")
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentHelpMeBuy> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						List<Org> list = orgService.findById(id, true);
						for (Org org : list) {
							ids.add(org.getOrgId());
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", id);
						larPager.setParams(result);
						ids = null;
					}
				}
			}
			LarPager<ShipmentHelpMeBuy> pager = shipmentHelpMeBuyService.findByOrgIds(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	private List<ShipmentHelpMeBuy> convert(List<ShipmentHelpMeBuy> list) throws Exception {
		List<Long> empList = new ArrayList<>();
		Set<Long> empSet = new HashSet<>();
		List<Long> orgList = new ArrayList<>();
		for (ShipmentHelpMeBuy helpMeBuy : list) {
			if (null != helpMeBuy.getCreateUser()) {
				empSet.add(helpMeBuy.getCreateUser());
			}
			if (null != helpMeBuy.getUpdateUser()) {
				empSet.add(helpMeBuy.getUpdateUser());
			}
			if (null != helpMeBuy.getCancelUser()) {
				empSet.add(helpMeBuy.getCancelUser());
			}
			if (null != helpMeBuy.getTakeUser()) {
				empSet.add(helpMeBuy.getTakeUser());
			}
			if (null != helpMeBuy.getDistributeUser()) {
				empSet.add(helpMeBuy.getDistributeUser());
			}
			if (null != helpMeBuy.getTurnOrderUser()) {
				empSet.add(helpMeBuy.getTurnOrderUser());
			}
			if (null != helpMeBuy.getCancelDistributeUser()) {
				empSet.add(helpMeBuy.getCancelDistributeUser());
			}
			if (null != helpMeBuy.getFinishUser()) {
				empSet.add(helpMeBuy.getFinishUser());
			}
			if (null != helpMeBuy.getCancelTakeUser()) {
				empSet.add(helpMeBuy.getCancelTakeUser());
			}
			if (null != helpMeBuy.getSalesMan()) {
				empSet.add(helpMeBuy.getSalesMan());
			}
			if (null != helpMeBuy.getPreviousSalesMan()) {
				empSet.add(helpMeBuy.getPreviousSalesMan());
			}
			if (null != helpMeBuy.getAccountUser()) {
				empSet.add(helpMeBuy.getAccountUser());
			}
			if (null != helpMeBuy.getOrg()) {
				orgList.add(helpMeBuy.getOrg());
			}
		}
		Map<Long, User> users = new HashMap<>();
		Map<Long, Org> orgs = new HashMap<>();
		if (empSet.size() > 0) {
			empList.addAll(empSet);
			users = userService.findUserMapByIds(empList);
		}
		if (orgList.size() > 0) {
			orgs = orgService.findOrgMapByIds(orgList, false);
		}
		for (ShipmentHelpMeBuy item : list) {
			if (null != users && users.size() > 0) {
				if (null != item.getCreateUser()) {
					User user = users.get(item.getCreateUser());
					if (null != user) {
						item.setCreateUserName(user.getName());
					}
				}
				if (null != item.getUpdateUser()) {
					User user = users.get(item.getUpdateUser());
					if (null != user) {
						item.setUpdateUserName(user.getName());
					}
				}
				if (null != item.getCancelUser()) {
					User user = users.get(item.getCancelUser());
					if (null != user) {
						item.setCancelUserName(user.getName());
					}
				}
				if (null != item.getTakeUser()) {
					User user = users.get(item.getTakeUser());
					if (null != user) {
						item.setTakeUserName(user.getName());
					}
				}
				if (null != item.getDistributeUser()) {
					User user = users.get(item.getDistributeUser());
					if (null != user) {
						item.setDistributeUserName(user.getName());
					}
				}
				if (null != item.getTurnOrderUser()) {
					User user = users.get(item.getTurnOrderUser());
					if (null != user) {
						item.setTurnOrderUserName(user.getName());
					}
				}
				if (null != item.getCancelDistributeUser()) {
					User user = users.get(item.getCancelDistributeUser());
					if (null != user) {
						item.setCancelDistributeUserName(user.getName());
					}
				}
				if (null != item.getFinishUser()) {
					User user = users.get(item.getFinishUser());
					if (null != user) {
						item.setFinishUserName(user.getName());
					}
				}
				if (null != item.getCancelTakeUser()) {
					User user = users.get(item.getCancelTakeUser());
					if (null != user) {
						item.setCancelTakeUserName(user.getName());
					}
				}
				if (null != item.getSalesMan()) {
					User user = users.get(item.getSalesMan());
					if (null != user) {
						item.setSalesManName(user.getName());
					}
				}
				if (null != item.getPreviousSalesMan()) {
					User user = users.get(item.getPreviousSalesMan());
					if (null != user) {
						item.setPreviousSalesManName(user.getName());
					}
				}
				if (null != item.getAccountUser()) {
					User user = users.get(item.getAccountUser());
					if (null != user) {
						item.setAccountUserName(user.getName());
					}
				}
			}
			if (null != orgs && orgs.size() > 0) {
				if (null != item.getOrg()) {
					Org org = orgs.get(item.getOrg());
					if (null != org) {
						item.setOrgName(org.getName());
					}
				}
			}
		}
		return list;
	}
}
