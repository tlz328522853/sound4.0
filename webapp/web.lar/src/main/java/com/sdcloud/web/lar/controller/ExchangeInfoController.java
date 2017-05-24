package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.api.lar.entity.ShoppingCartExport;
import com.sdcloud.api.lar.service.ExchangeInfoService;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExcelUtils;
import com.sdcloud.web.lar.util.SendPhoneMessage;

@RestController
@RequestMapping("/api/exchangeInfo")
public class ExchangeInfoController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ExchangeInfoService exchangeInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private LarClientUserService larClientUserService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private LarClientUserAddressService userAddressService;
	@Autowired
	private EmployeeService employeeService;

	// 增加
	@RequestMapping(value = "/saveExchangeInfo", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) ExchangeInfo exchangeInfo) throws Exception {
		try {
			logger.info(
					"start method: save(@RequestBody(required = false) ExchangeInfo exchangeInfo) "
					+ "exchangeInfo :"+exchangeInfo
					);
			if (exchangeInfo != null && StringUtils.isEmpty(exchangeInfo.getId())) {
				double points = 0;// 积分
				double money = 0;
				List<ShoppingCart> shoppingCarts = exchangeInfo.getShoppingCarts();
				if (shoppingCarts == null || shoppingCarts.size() <= 0) {
					logger.error("err method, no shoppingCarts ");
					return ResultDTO.getSuccess(AppCode.SUCCESS, "没有兑换商品！");
				}
				for (ShoppingCart shoppingCart : shoppingCarts) {
					// 如果是积分支付就计算所有商品的总积分
					if (shoppingCart.getCommodity().getMoneyUnit() == 1) {
						points += shoppingCart.getNumber() * shoppingCart.getCommodity().getUnitPrice();
					} else {// 计算钱
						money += shoppingCart.getNumber() * shoppingCart.getCommodity().getUnitPrice();
					}
				}
				// 消费积分与消费金额关联
				exchangeInfo.setMoney(new BigDecimal(money));
				exchangeInfo.setIntegral(points + "");
				boolean insertUserGetId = false;
				if ((points > 0 && money <= 0) || (points > 0 && money > 0)) {
					LarClientUser myPoints = larClientUserService.getMyPoints(exchangeInfo.getAppUserId());
					// 先判断用户积分是否足够
					if (myPoints.getNowPoints() >= points) {
						insertUserGetId = exchangeInfoService.insertSelective(exchangeInfo);
						// 减掉用户积分
						if (insertUserGetId) {
							larClientUserService.updateUserPoints(points, exchangeInfo.getAppUserId());
						}
						return ResultDTO.getSuccess(AppCode.SUCCESS, "恭喜您成功兑换以下商品！共消费" + points + "积分。");
					} else {
						logger.error("err method, : 积分不足" );
						return ResultDTO.getFailure(AppCode.BIZ_ERROR, "积分不足！");
					}
				}
				if (points <= 0 && money > 0) {
					insertUserGetId = exchangeInfoService.insertSelective(exchangeInfo);
					return ResultDTO.getSuccess(AppCode.SUCCESS, "恭喜您成功兑换以下商品！共消费" + points + "元。");
				}
				if (insertUserGetId = false) {
					logger.error("err method, insertUserGetId: false" );
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "预约兑换失败！");
				}
			} else {
				logger.error("err method, exchangeInfo.getId is empty");
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("err method, Exception: " + e.getMessage());
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
		return ResultDTO.getFailure(AppCode.BIZ_ERROR, "预约兑换失败！");
	}

	@RequestMapping("/selectByExample")
	@ResponseBody
	public ResultDTO selectByExample(@RequestBody(required = false) LarPager<ExchangeInfo> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			// 默认排序
			larPager.setOrderBy("createDate");
			larPager.setOrder("desc");

			List<Long> orgIds = new ArrayList<>();
			if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
//				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
//				if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
//						List<Org> list = orgService.findById(id, true);
						
						String orgStr = map.get("orgId") + "";
						String[] orgArr = orgStr.split("AAA");
//						List<Long> orgIds = new ArrayList<>();
						for(String orgString:orgArr){
						   Long	id = Long.parseLong(orgString);
							List<Org> list = orgService.findById(id, true);
							for (Org org : list) {
								orgIds.add(org.getOrgId());
							}
							larPager.getParams().remove("id");
							larPager.getParams().put("orgIds", orgIds);
//						}
//						for (Org org : list) {
//							ids.add(org.getOrgId());
						}
						
					} else {
						String id = map.get("orgId") + "";
						larPager.getParams().put("mechanismId", id);
//						larPager.getParams().remove("isParentNode");
//						ids = null;
					}
//				}
			}
			LarPager<ExchangeInfo> result = exchangeInfoService.selectByExample(larPager, orgIds);
			return ResultDTO.getSuccess(200, result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping(value = "/cancelExchangeInfos/{statusId}", method = RequestMethod.POST)
	public ResultDTO cancelOrders(@PathVariable(value = "statusId") String statusId,
			@RequestBody(required = false) LarPager<ExchangeInfo> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<ExchangeInfo> result = null;
		try {
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("checks") != null) {
				List<String> orderIds = (List<String>) larPager.getParams().get("checks");
				Map<String,Object> cancelOrder = null;
				for (String orderId : orderIds) {
					// 获得当前登录用户
					Object userId = request.getAttribute("token_userId");
					// 查询用户
					User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", orderId);
					updateParams.put("orderStatusId", 0);
					updateParams.put("orderStatusName", "已取消");
					updateParams.put("cancelOrderPersonId", user.getUserId());
					updateParams.put("cancelOrderPersonName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("cancelOrderIllustrate", larPager.getParams().get("cancelOrderIllustrate"));
					updateParams.put("cancelDate", new Date());
					cancelOrder = exchangeInfoService.cancelOrderById(updateParams);
					if(cancelOrder.get("phone")!=null&&cancelOrder.get("phone").toString().length()==11&&cancelOrder.get("points")!=null){
						StringBuffer returnPointsMess=new StringBuffer();
						returnPointsMess.append( "尊敬的用户，已返还给您:");
						returnPointsMess.append(cancelOrder.get("points").toString());
						returnPointsMess.append("积分,谢谢您的参与！");
						SendPhoneMessage.sendPhoneMessage(cancelOrder.get("phone").toString(),returnPointsMess.toString());
					}
				}
				if ((Boolean)cancelOrder.get("resultFlag")) {
					result = new LarPager<ExchangeInfo>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> status = new ArrayList<>();
					if (statusId.equals("1")) {
						params.put("OrderStatus", 1);
					} else if (statusId.equals("2")) {
						params.put("OrderStatus", 2);
					} else if (statusId.equals("3")) {
						status.add(0);
						status.add(1);
						status.add(2);
						status.add(3);
						status.add(4);
						params.put("OrderStatus", status);
					}
					result.setParams(params);
					LarPager<ExchangeInfo> result1 = exchangeInfoService.selectByExample(result, null);
					return ResultDTO.getSuccess(result1,"取消订单成功!");

				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping("/update")
	@ResponseBody
	public ResultDTO update(@RequestBody(required = false) ExchangeInfo exchangeInfo, HttpServletRequest request) {
		try {
			String userId = (String) request.getAttribute(Constant.TOKEN_USERID);
			if (userId != null) {
				User user = userService.findByUesrId(Long.valueOf(userId));
				Employee employee = user.getEmployee();
				if(null !=employee){
					exchangeInfo.setLoginUserId(Long.valueOf(employee.getEmployeeId()));//完成确认人ID
					exchangeInfo.setLoginUserName(employee.getName()); //完成确认人姓名
				}else{
					return ResultDTO.getFailure("修改失败,该用户没有绑定员工!");
				}
			}
			exchangeInfo.setOrderStatus(4);
			boolean b = exchangeInfoService.updateBySelect(exchangeInfo);
			if (b) {
				return ResultDTO.getSuccess("修改成功");
			} else {
				return ResultDTO.getFailure("修改失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	//导出购买记录
	@RequestMapping("/export")
	public void export(HttpServletResponse response, @RequestBody(required = false)LarPager<ExchangeInfo> pager) throws Exception {
		pager.setOrderBy("createDate");
		pager.setOrder("desc");
		pager.setPageSize(1000000);
		List<Long> ids = new ArrayList<>();
		String orgId = pager.getExtendMap().get("orgId").toString();
		Boolean isParentNode =  (Boolean) pager.getExtendMap().get("isParentNode") ;
		pager.getExtendMap().clear();
		if (null != orgId) {
			// 是父节点再去查找
			if (isParentNode) {
				String[] split = orgId.split("AAA");
				for(String orgStr:split){
					List<Org> list = orgService.findById(Long.valueOf(orgStr), true);
					for (Org org : list) {
						ids.add(org.getOrgId());
					}
				}
			} else {
				pager.getParams().put("mechanismId", orgId);
				ids = null;
			}
		}
		LarPager<ExchangeInfo> contractLarPager = exchangeInfoService.selectByExample(pager, ids);

		if (null != contractLarPager && null != contractLarPager.getResult()
				&& contractLarPager.getResult().size() > 0) {
			List<String> totalTitles = new ArrayList<>();//标题
			totalTitles.add("购买记录");
			totalTitles.add("购买记录_子单");
			
			List<List<? extends Object>> contects = new ArrayList<>();//内容
			List<ExchangeInfo> list = this.convert(contractLarPager.getResult());
			contects.add(list);
			contects.add(this.getChildsFromOrders(list));
			
			ExcelUtils exportExcelUtils = new ExcelUtils(totalTitles);
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents(totalTitles, contects);
				response.setContentType("APPLICATION/OCTET-STREAM");
				OutputStream outputStream = response.getOutputStream();
				workbook.write(outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (workbook != null) {
					try {
						workbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static Map<Integer, String> paymentMap;
	private static Map<Integer, String> moneyUnitMap;
	static {
		paymentMap = new HashMap<>();
		paymentMap.put(1,"积分支付");
		paymentMap.put(2,"货到付款");
		moneyUnitMap = new HashMap<>();
		moneyUnitMap.put(1, "分");
		moneyUnitMap.put(2, "元");
	}
	
	//用于获得子订单的导出类的集合
	private List<ShoppingCartExport> getChildsFromOrders(List<ExchangeInfo> list) {
		List<ShoppingCartExport> shoppingCartExports = new ArrayList<>();
		for(ExchangeInfo info :list){
			List<ShoppingCart> carts = info.getShoppingCarts();
			if(null !=carts && carts.size()>0){
				int i=1;//序号
				for(ShoppingCart cart:carts){
					Commodity commodity = cart.getCommodity();
					if(null !=commodity){
						if(null!=commodity.getCommodityType()){
							commodity.setTypeId(commodity.getCommodityType().getId());
							commodity.setTypeName(commodity.getCommodityType().getTypeName());
						}
						//支付方式
						if(commodity.getPaymentMethod() !=0){
							commodity.setPaymentName(paymentMap.get(commodity.getPaymentMethod()));
						}
						//计量单位
						if(commodity.getMoneyUnit() !=0){
							commodity.setMoneyUnitName(moneyUnitMap.get(commodity.getMoneyUnit()));
						}
					}
					ShoppingCartExport shoppingCartExport = new ShoppingCartExport(info,cart);
					shoppingCartExport.setNo(i++);
					shoppingCartExports.add(shoppingCartExport);
				}
			}
		}
		return shoppingCartExports;
	}

	private static  Map<Integer, String> statusMap;
	static{
		statusMap = new HashMap<>();
		statusMap.put(0, "已取消");
		statusMap.put(1, "未完成");
		statusMap.put(2, "未完成");
		statusMap.put(3, "未完成");
		statusMap.put(4, "已完成");
	}

	//用于导出购买记录的转换
	private List<ExchangeInfo> convert(List<ExchangeInfo> result) throws Exception {
		List<Long> orgList = new ArrayList<>();
		for (ExchangeInfo info : result) {
			if (null != info.getMechanismId()) {
				orgList.add(Long.valueOf(info.getMechanismId()));
			}
		}
		Map<Long, Org> map = orgService.findOrgMapByIds(orgList, false);
		for (ExchangeInfo info : result) {
			if (null != map) {
				Org org = map.get(Long.valueOf(info.getMechanismId()));
				if (null != org) {
					info.setOrgName(org.getName());
				}
			}
			LarClientUserAddress address = info.getLarClientUserAddress();
			if (null != address) {
				LarClientUser user = address.getLarClientUser();
				if (null != user) {
					info.setClientUserName(user.getName());
					info.setClientUserPhone(user.getPhone());
					info.setClientUserAddress(info.getAddress());
					info.setClientUserAddressDetail(user.getAddressDetail());
				}
				
				info.setUserName(address.getUserName());
				info.setAddress(address.getAddress());
				info.setPhone(address.getContact());
			}
			info.setStatusName(statusMap.get(info.getOrderStatus()));
			if(null ==info.getMoney())
				info.setMoney(new BigDecimal(0));
			if(null == info.getIntegral())
				info.setIntegral("0");
		}
		return result;
	}
}
