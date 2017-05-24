package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.UserOrderCount;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.entity.VoucherCondition;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.api.lar.service.MessageCenterService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.VoucherConditionService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;

/**
 * Created by 韩亚辉 on 2016/4/7.
 */
@RequestMapping("/api/voucher")
@RestController
public class VoucherController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VoucherService voucherService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private LarClientUserService larClientUserService;
	@Autowired
	private ShipmentSendExpressService shipmentSendExpressService;
	@Autowired
	private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
	@Autowired
	private ShipmentCitySendService shipmentCitySendService;
	@Autowired
	private OrderManagerService orderManagerService;
	@Autowired
	private VoucherConditionService voucherConditionService;
	@Autowired
	private MessageCenterService messageCenterService;
	@Autowired
	private XingeAppUtils xingeAppUtils;

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<Voucher> larPager) {
		try {
			return ResultDTO.getSuccess(200, voucherService.findAll(larPager));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) Voucher shipmentShop, HttpServletRequest request) {
		try {
			logger.info("Enter the :{} method  shipmentShop:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(), shipmentShop);

			Object userId = request.getAttribute("token_userId");
			User user = null;
			if (null != userId && userId != "") {
				user = userService.findByUesrId(Long.valueOf(userId + ""));
				if (user == null || user.getUserId() == null) {
					return ResultDTO.getSuccess(400, "该用户不存在", null);
				}
				shipmentShop.setCreateUser(user.getUserId());
			} else {
				return ResultDTO.getSuccess(400, "非法请求用户", null);
			}
			shipmentShop.setReleaseDate(new Date());
			shipmentShop.setReleaseUser(user.getUserId());
			shipmentShop.setUseType("可用");
			Boolean save = voucherService.save(shipmentShop);
			if (save) {
				MessageCenter mc = new MessageCenter();
				mc.setCustomerId(shipmentShop.getCustomerId());
				mc.setTitle("优惠券");
				String detail = "亲，恭喜您获得" + shipmentShop.getAmount() + "元优惠券，请您及时使用";
				mc.setDetail(detail);
				messageCenterService.save(mc);

				XingeEntity xingeEntity = new XingeEntity();
				xingeEntity.setTitle("优惠券");
				xingeEntity.setContent(detail);
				xingeEntity.setAccount(shipmentShop.getCustomerId() + "");
				xingeAppUtils.pushSingleAccount(xingeEntity,1);
				xingeAppUtils.pushSingleAccountIOS(xingeEntity,3);
			}
			return ResultDTO.getSuccess(200, save);
		} catch (Exception e) {
			logger.error("method {} execute error, shipmentShop:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(), shipmentShop, e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/batchSave")
	public ResultDTO batchSave(@RequestBody(required = false) Voucher voucher, HttpServletRequest request) {
		try {
			logger.info("Enter the :{} method  voucher:{}", Thread.currentThread().getStackTrace()[1].getMethodName(),
					voucher);

			Long id = 0l;
			User user = null;
			Object userId = request.getAttribute("token_userId");
			if (null != userId && userId != "") {
				user = userService.findByUesrId(Long.valueOf(userId + ""));
				if (user == null || user.getUserId() == null) {
					return ResultDTO.getSuccess(400, "该用户不存在", null);
				}
				id = user.getUserId();
			} else {
				return ResultDTO.getSuccess(400, "非法请求用户", null);
			}
			voucher.setUseType("可用");
			List<Voucher> list = new ArrayList<>();
			LarPager<LarClientUser> larClientUserLarPager = new LarPager<>();
			larClientUserLarPager.setPageSize(1000000000);
			LarPager<LarClientUser> userLarPager = larClientUserService.selectByExample(larClientUserLarPager);
			// 客户id的集合
			List<String> account = new ArrayList<>();
			if (null != voucher) {
				if (voucher.getType().equals("全体客户")) {
					if (null != userLarPager && userLarPager.getResult() != null
							&& userLarPager.getResult().size() > 0) {
						for (LarClientUser item : userLarPager.getResult()) {
							Voucher obj = (Voucher) voucher.clone();
							obj.setId(UUIDUtil.getUUNum());
							obj.setReleaseDate(new Date());
							obj.setReleaseUser(id);
							obj.setCustomerId(Long.parseLong(item.getId()));
							obj.setCreateUser(id);
							list.add(obj);
							account.add(item.getCustomerId() + "");
						}
					}
				} else {
					VoucherCondition voucherCondition = voucherConditionService.getById(voucher.getCondition(), null);
					// 部分用户
					if ("1".equals(voucherCondition.getType())) {
						// 1 总金额
						List<ShipmentHelpMeBuy> helpMeBuyLarPager1 = shipmentHelpMeBuyService.getBalance();
						List<ShipmentCitySend> shipmentCitySends = shipmentCitySendService.getBalance();
						List<ShipmentSendExpress> shipmentSendExpresses = shipmentSendExpressService.getBalance();
						Map<Long, Double> map = new HashMap<>();
						if (null != helpMeBuyLarPager1 && helpMeBuyLarPager1.size() > 0) {
							for (ShipmentHelpMeBuy item : helpMeBuyLarPager1) {
								// if (null != item.getTotalCost()) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getBalance());
								} else {
									map.put(item.getCustomerId(), item.getBalance() != null ? item.getBalance() : 0.0);
								}
								// }
							}
						}
						if (null != shipmentCitySends && shipmentCitySends.size() > 0) {
							for (ShipmentCitySend item : shipmentCitySends) {
								// if (null != item.getFactServiceCost()) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(),
											(map.get(item.getCustomerId()) != null ? map.get(item.getCustomerId())
													: 0.0) + (item.getBalance() != null ? item.getBalance() : 0.0));
								} else {
									map.put(item.getCustomerId(), item.getBalance() != null ? item.getBalance() : 0.0);
								}
								// }
							}
						}
						if (null != shipmentSendExpresses && shipmentSendExpresses.size() > 0) {
							for (ShipmentSendExpress item : shipmentSendExpresses) {
								// if (null != item.getMoneyOne()) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(),
											(map.get(item.getCustomerId()) != null ? map.get(item.getCustomerId())
													: 0.0) + (item.getBalance() != null ? item.getBalance() : 0.0));
								} else {
									map.put(item.getCustomerId(), item.getBalance() != null ? item.getBalance() : 0.0);
								}
								// }
							}
						}
						
						for (Long item : map.keySet()) {
							account.add(item + "");
							Double value = map.get(item);
							if (null == value) {
								value = 0.0;
							}
							Voucher obj = (Voucher) voucher.clone();
							obj.setId(UUIDUtil.getUUNum());
							obj.setReleaseDate(new Date());
							obj.setReleaseUser(id);
							obj.setCreateUser(id);
							if (null == voucherCondition.getTotalStart() && null != voucherCondition.getTotalEnd()
									&& voucherCondition.getTotalEnd() != 0) {
								if (value <= voucherCondition.getTotalEnd()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
							if (null != voucherCondition.getTotalStart() && voucherCondition.getTotalStart() != 0
									&& null == voucherCondition.getTotalEnd()) {
								if (value >= voucherCondition.getTotalStart()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
							if (null != voucherCondition.getTotalStart() && null != voucherCondition.getTotalEnd()
									&& voucherCondition.getTotalEnd() != 0) {
								if (value >= voucherCondition.getTotalStart()
										&& value <= voucherCondition.getTotalEnd()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
						}
					} else {
						// 2 总次数
						List<ShipmentHelpMeBuy> helpMeBuyLarPager1 = shipmentHelpMeBuyService.getCount();
						List<ShipmentCitySend> shipmentCitySends = shipmentCitySendService.getCount();
						List<ShipmentSendExpress> shipmentSendExpresses = shipmentSendExpressService.getCount();
						List<UserOrderCount> orderByAppUserId = orderManagerService.getOrderByAppUserId();
						Map<Long, Integer> map = new HashMap<>();
						if (null != helpMeBuyLarPager1 && helpMeBuyLarPager1.size() > 0) {
							for (ShipmentHelpMeBuy item : helpMeBuyLarPager1) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getCount());
								} else {
									map.put(item.getCustomerId(), item.getCount());
								}
							}
						}
						if (null != shipmentCitySends && shipmentCitySends.size() > 0) {
							for (ShipmentCitySend item : shipmentCitySends) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getCount());
								} else {
									map.put(item.getCustomerId(), item.getCount());
								}
							}
						}
						if (null != shipmentSendExpresses && shipmentSendExpresses.size() > 0) {
							for (ShipmentSendExpress item : shipmentSendExpresses) {
								if (map.keySet().contains(item.getCustomerId())) {
									map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getCount());
								} else {
									map.put(item.getCustomerId(), item.getCount());
								}
							}
						}
						if (null != orderByAppUserId && orderByAppUserId.size() > 0) {
							for (UserOrderCount item : orderByAppUserId) {
								if (null != item.getAppUserId() && null != map.get(Long.parseLong(item.getAppUserId()))
										&& map.keySet().contains(Long.parseLong(item.getAppUserId()))) {
									map.put(Long.parseLong(item.getAppUserId()),
											map.get(Long.parseLong(item.getAppUserId())) + item.getOrderCount());
								} else if (null != item.getAppUserId()) {
									map.put(Long.parseLong(item.getAppUserId()), item.getOrderCount());
								}
							}
						}
						for (Long item : map.keySet()) {
							account.add(item + "");
							Integer value = map.get(item);
							Voucher obj = (Voucher) voucher.clone();
							obj.setId(UUIDUtil.getUUNum());
							obj.setReleaseDate(new Date());
							obj.setReleaseUser(id);
							obj.setCreateUser(id);
							if (null == voucherCondition.getServiceCountStart()
									&& null != voucherCondition.getServiceCountEnd()
									&& voucherCondition.getServiceCountEnd() != 0) {
								if (value <= voucherCondition.getServiceCountEnd()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
							if (null != voucherCondition.getServiceCountStart()
									&& voucherCondition.getServiceCountStart() != 0
									&& null == voucherCondition.getServiceCountEnd()) {
								if (value >= voucherCondition.getServiceCountStart()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
							if (null != voucherCondition.getServiceCountStart()
									&& null != voucherCondition.getServiceCountEnd()
									&& voucherCondition.getServiceCountStart() != 0
									&& voucherCondition.getServiceCountEnd() != 0) {
								if (value >= voucherCondition.getServiceCountStart()
										&& value <= voucherCondition.getServiceCountEnd()) {
									obj.setCustomerId(item);
									list.add(obj);
								}
							}
						}
					}
				}
			}
			if (list != null && list.size() > 0) {
				boolean b = voucherService.batchSave(list);
				if (b) {
					String detail = "亲，恭喜您获得" + list.get(0).getAmount() + "元优惠券，请您及时使用";
					List<MessageCenter> mcList = new ArrayList<>();
					for (int i = 0; i < account.size(); i++) {
						MessageCenter mc = new MessageCenter();
						mc.setId(UUIDUtil.getUUNum());
						mc.setCreateDate(new Date());
						mc.setCustomerId(Long.valueOf(account.get(i)));
						mc.setTitle("优惠券");
						mc.setDetail(detail);
						mcList.add(mc);
						if ((i + 1) % 1000 == 0) {
							messageCenterService.batchSave(mcList);
							mcList.clear();
						}
					}
					if (account.size() % 1000 != 0) {
						messageCenterService.batchSave(mcList);
					}
					// whs TODO 异步执行,需要优化!!!!
					XingeEntity xingeEntity = new XingeEntity();
					xingeEntity.setTitle("优惠券");
					xingeEntity.setContent(detail);
					xingeEntity.setAccounts(account);
					xingeAppUtils.pushAccountList(xingeEntity,1);
					xingeAppUtils.pushAccountListIOS(xingeEntity,3);
				}
				return ResultDTO.getSuccess(200, "优惠券发放成功!", b);
			} else {
				return ResultDTO.getSuccess(200, "优惠券发放成功!", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("method {} execute error, voucher:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(), voucher, e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) Voucher voucher, HttpServletRequest request) {
		try {
			logger.info("Enter the :{} method  voucher:{}", Thread.currentThread().getStackTrace()[1].getMethodName(),
					voucher);
			Object userId = request.getAttribute("token_userId");
			if (null != userId && userId != "") {
				User user = userService.findByUesrId(Long.valueOf(userId + ""));
				voucher.setUpdateUser(user.getUserId());
			}
			return ResultDTO.getSuccess(200, voucherService.update(voucher));
		} catch (Exception e) {
			logger.error("method {} execute error, voucher:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(), voucher, e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			logger.info("Enter the :{} method  id:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), id);
			return ResultDTO.getSuccess(200, voucherService.delete(id));
		} catch (Exception e) {
			logger.error("method {} execute error, id:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(), id, e);
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
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Voucher> larPager) {
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
			LarPager<Voucher> pager = voucherService.findByOrgIds(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	private List<Voucher> convert(List<Voucher> list) throws Exception {
		List<Long> empList = new ArrayList<>();
		Set<Long> empSet = new HashSet<>();
		for (Voucher sendExpress : list) {
			if (null != sendExpress.getReleaseUser()) {
				empSet.add(sendExpress.getReleaseUser());
			}
		}
		Map<Long, User> users = new HashMap<>();
		if (empSet.size() > 0) {
			empList.addAll(empSet);
			users = userService.findUserMapByIds(empList);
		}
		for (Voucher item : list) {
			if (null != users && users.size() > 0) {
				if (null != item.getReleaseUser()) {
					User user = users.get(item.getReleaseUser());
					if (null != user) {
						// item.setReleaseUserName(user.getName());
						item.setReleaseUserName(user.getEmployee().getName());
					}
				}
			}
		}
		return list;
	}

	/**
	 * 获取优惠券发放人用户列表
	 * 
	 * @author jzc 2016年6月25日
	 * @param shipmentShop
	 * @param request
	 * @return
	 */
	@RequestMapping("/getRelUsers")
	public ResultDTO getRelUsers(@RequestBody(required = false) LarPager<User> larPager) {
		try {

			List<Long> relUsers = voucherService.getRelUsers();
			Map<Long, User> users = userService.findUserMapByIds(relUsers);
			List<User> userList = new ArrayList<>();
			for (Map.Entry<Long, User> entry : users.entrySet()) {
				entry.getValue().setPassword(null);
				userList.add(entry.getValue());
			}
			return ResultDTO.getSuccess(200, userList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	/**
	 * 导出全部的优惠券
	 * 
	 * @author jzc 2016年6月25日
	 * @param response
	 */
	@RequestMapping("/export")
	public void export(@RequestBody(required = false) LarPager<Voucher> larPager,HttpServletResponse response) {
		larPager.setPageSize(1000000);
		LarPager<Voucher> voucherLarPager = voucherService.findByOrgIds(larPager, null);
		if (null != voucherLarPager && null != voucherLarPager.getResult() && voucherLarPager.getResult().size() > 0) {
			ExportExcelUtils<Voucher> exportExcelUtils = new ExportExcelUtils<>("优惠券");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("优惠券", this.convert(voucherLarPager.getResult()));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setDateHeader("Expires", 0);
				OutputStream outputStream = response.getOutputStream();
				workbook.write(outputStream);
				response.getOutputStream().flush();
				response.getOutputStream().close();
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

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}
}
