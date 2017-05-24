package com.sdcloud.web.lar.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentCitySendAcc;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.entity.ShipmentSelectAged;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.ShipmentSendExpressAcc;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/27.
 */
@RestController
@RequestMapping("/api/citySend")
public class ShipmentCitySendController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ShipmentCitySendService shipmentCitySendService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private ShipmentOperationService operationService;
	@Autowired
	private XingeAppUtils xingeAppUtils;
	@Autowired
	private SysConfigService sysConfigService;

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentCitySend> larPager) {
		try {
			//TODO 业务修改,注释代码
			/*Map<String, String> map = sysConfigService.findMap();
			Long orderOutTime = Long.parseLong(map.get("orderOutTime")) * 60;// 抢单时效
			shipmentCitySendService.updateGrabState(orderOutTime);*/

			LarPager<ShipmentCitySend> larPager1 = shipmentCitySendService.findAll(larPager);
			if (null != larPager1 && larPager1.getResult() != null && larPager1.getResult().size() > 0) {
				larPager1.setResult(this.convert(larPager1.getResult()));
			}
			return ResultDTO.getSuccess(200, larPager1);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) ShipmentCitySend shipmentCitySend,
			HttpServletRequest request) {
		try {
			Object userId = request.getAttribute("token_userId");
			if (null != userId && userId != "") {
				User user = userService.findByUesrId(Long.valueOf(userId + ""));
				shipmentCitySend.setCreateUser(user.getUserId());
				// shipmentCitySend.setAccountUser(user.getUserId());
			}
			shipmentCitySend.setAccountState("未完");
			shipmentCitySend.setOrderNo(OrderUtils.generateNumber());
			shipmentCitySend.setOrderTime(new Date());
			shipmentCitySend.setOrderState("等待接单");
			shipmentCitySend.setBizType("同城送");
			shipmentCitySend.setSource("PC");
			if (null != shipmentCitySend.getSaveMoney()) {
				shipmentCitySend.setTotalMoney(shipmentCitySend.getServiceCost() - shipmentCitySend.getSaveMoney());
			} else {
				shipmentCitySend.setTotalMoney(shipmentCitySend.getServiceCost());
			}
			return ResultDTO.getSuccess(200, shipmentCitySendService.save(shipmentCitySend));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) ShipmentCitySend shipmentCitySend,
			HttpServletRequest request) {
		try {
			Object userId = request.getAttribute("token_userId");
			if (null != userId && userId != "") {
				User user = userService.findByUesrId(Long.valueOf(userId + ""));
				shipmentCitySend.setUpdateUser(user.getUserId());
			}
			if (null != shipmentCitySend.getSaveMoney()) {
				shipmentCitySend.setTotalMoney(shipmentCitySend.getServiceCost() - shipmentCitySend.getSaveMoney());
			} else {
				shipmentCitySend.setTotalMoney(shipmentCitySend.getServiceCost());
			}
			return ResultDTO.getSuccess(200, shipmentCitySendService.update(shipmentCitySend));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			return ResultDTO.getSuccess(200, shipmentCitySendService.delete(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/updateState")
	public ResultDTO updateState(@RequestBody(required = false) Map<String, Object> map, HttpServletRequest request) {
		try {
			Object userId = request.getAttribute("token_userId");
			List<Long> list = new ArrayList<>();
			String orderStateNo = map.get("orderStateNo") + "";
			Boolean flag = false;
			if ("5".equals(orderStateNo)) {//转单人ID 存储 employeeId
				flag = true;
				User user= userService.findByUesrId(Long.parseLong(userId.toString()));
	            if(user!=null&&user.getEmployee()!=null){
	            	userId=user.getEmployee().getEmployeeId();
	            }else{
	            	logger.warn("转单人-业务员 为空！登录账号的userId:"+userId);
	            }
			}
			
			// 如果订单完成，计算服务时长和服务失效
			Map<String, Object> result = OrderUtils.paramConvert(map, list, userId);
			if ("6".equals(orderStateNo)) {
				String date = "";
				if (list.size() > 0) {
					ShipmentCitySend shipmentCitySend = shipmentCitySendService.getById(list.get(0), null);
					if (null != shipmentCitySend) {
						Date endTime = (Date) map.get("finish_time");
						Date startTime = shipmentCitySend.getOrderTime();
						if (null != endTime && null != startTime) {
							Long time = endTime.getTime() - startTime.getTime();
							Long timeShiXiao = time / 1000 / 60;
							date += timeShiXiao + "分钟";
							result.put("server_time", date);
							if (timeShiXiao >= 0 && timeShiXiao < 60) {
								result.put("time_type", "(0-1]小时");
							} else if (timeShiXiao >= 60 && timeShiXiao < 360) {
								result.put("time_type", "(1-6]小时");
							} else if (timeShiXiao >= 360 && timeShiXiao < 720) {
								result.put("time_type", "(6-12]小时");
							} else if (timeShiXiao >= 720 && timeShiXiao < 1440) {
								result.put("time_type", "(12-24]小时");
							} else if (timeShiXiao >= 1440) {
								result.put("time_type", "24小时以上");
							}
						}
					}
				}
			}

			if (null != result) {
				Boolean b = shipmentCitySendService.updateState(result, list, flag);
				//取消订单, 返还优惠券
				if("0".equals(orderStateNo) && b){
					shipmentCitySendService.backCoupon(list);
				}
				//派单
				if ("3".equals(orderStateNo) && b) {
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("id", result.get("sales_man"));
					List<ShipmentOperation> so = operationService.findByMap(paramMap);
					if (CollectionUtils.isNotEmpty(so)) {
						ShipmentOperation operation = so.get(0);
						
            			XingeEntity xingeEntity = new XingeEntity();
    					xingeEntity.setTitle("有新的订单");
    					xingeEntity.setContent("有新的订单");
    					xingeEntity.setAccount(operation.getSysUser()+"");
    					xingeEntity.setGrabOrder(11);
            			xingeAppUtils.pushSingleAccount(xingeEntity,2);
        				xingeAppUtils.pushSingleAccountIOS(xingeEntity,4);
					}
				}
				return ResultDTO.getSuccess(200, b);
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
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentCitySend> larPager) {
		try {

			//TODO 业务修改,注释代码
			/*Map<String, String> map1 = sysConfigService.findMap();
			Long orderOutTime = Long.parseLong(map1.get("orderOutTime")) * 60;// 抢单时效
			shipmentCitySendService.updateGrabState(orderOutTime);*/

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
			LarPager<ShipmentCitySend> pager = shipmentCitySendService.findByOrgIds(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
			return ResultDTO.getSuccess(200, pager);
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
	@RequestMapping("/findByOrgIdsOne")
	public ResultDTO findByOrgIdsOne(@RequestBody(required = false) LarPager<ShipmentCitySend> larPager) {
		try {
			//TODO 业务修改,注释代码
			/*Map<String, String> map1 = sysConfigService.findMap();
			Long orderOutTime = Long.parseLong(map1.get("orderOutTime")) * 60;// 抢单时效
			shipmentCitySendService.updateGrabState(orderOutTime);*/

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
			LarPager<ShipmentCitySend> pager = shipmentCitySendService.findByOrgIdsOne(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	private List<ShipmentCitySend> convert(List<ShipmentCitySend> list) throws Exception {
		List<Long> empList = new ArrayList<>();
		Set<Long> empSet = new HashSet<>();
		List<Long> orgList = new ArrayList<>();
		for (ShipmentCitySend citySend : list) {
			if (null != citySend.getCreateUser()) {
				empSet.add(citySend.getCreateUser());
			}
			if (null != citySend.getUpdateUser()) {
				empSet.add(citySend.getUpdateUser());
			}
			if (null != citySend.getCancelUser()) {
				empSet.add(citySend.getCancelUser());
			}
			if (null != citySend.getTakeUser()) {
				empSet.add(citySend.getTakeUser());
			}
			if (null != citySend.getDistributeUser()) {
				empSet.add(citySend.getDistributeUser());
			}
			if (null != citySend.getTurnOrderUser()) {
				empSet.add(citySend.getTurnOrderUser());
			}
			if (null != citySend.getCancelDistributeUser()) {
				empSet.add(citySend.getCancelDistributeUser());
			}
			if (null != citySend.getFinishUser()) {
				empSet.add(citySend.getFinishUser());
			}
			if (null != citySend.getCancelTakeUser()) {
				empSet.add(citySend.getCancelTakeUser());
			}
			if (null != citySend.getSalesMan()) {
				empSet.add(citySend.getSalesMan());
			}
			if (null != citySend.getPreviousSalesMan()) {
				empSet.add(citySend.getPreviousSalesMan());
			}
			if (null != citySend.getAccountUser()) {
				empSet.add(citySend.getAccountUser());
			}
			if (null != citySend.getOrg()) {
				orgList.add(citySend.getOrg());
			}
            //抢单人员
			citySend.setGrabOrderManName(citySend.getSalesManName());
            //抢单类型    10:'手动派单',21:'自动派单',22:'自动派单',31:'抢单',32:'抢单',33:'抢单'
            String grabOrderStr="";
            switch(citySend.getGrabOrder()){
                case 10:grabOrderStr="手动派单";
                	break;
                case 21:grabOrderStr="自动派单";
                	break;
                case 22:grabOrderStr="自动派单";
                	break;
                case 31:grabOrderStr="抢单";
                	break;
                case 32:grabOrderStr="抢单";
                	break;
                case 33:grabOrderStr="抢单";
                	break;
                default:grabOrderStr="未知";
                    break;
            }
            citySend.setGrabOrderStr(grabOrderStr);
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
		for (ShipmentCitySend item : list) {
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
					if(null != user){
                    	if (user.getEmployee()!=null) {
                        	//改为显示员工的名字
                            item.setAccountUserName(user.getEmployee().getName());
                        }
                        else{
                        	//显示登陆账户的名字
                        	item.setAccountUserName(user.getName());
                        }
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

	private List<ShipmentSelectAged> convertToSelectAged(List<ShipmentCitySend> list) {
		List<ShipmentSelectAged> shipmentSelectAgeds = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (ShipmentCitySend item : list) {
				ShipmentSelectAged selectAged = new ShipmentSelectAged();
				selectAged.setOrderNo(item.getOrderNo());
				selectAged.setAccountTime(item.getAccountTime());
				selectAged.setSalesMan(item.getSalesMan());
				selectAged.setSalesManName(item.getSalesManName());
				selectAged.setArea(item.getArea());
				selectAged.setAreaName(item.getAreaName());
				selectAged.setBizType(item.getBizType());
				selectAged.setCancelTime(item.getCancelTime());
				selectAged.setTimeType(item.getTimeType());
				selectAged.setTakeTime(item.getTakeTime());
				selectAged.setServerTime(item.getServerTime());
				selectAged.setOrg(item.getOrg());
				selectAged.setOrgName(item.getOrgName());
				selectAged.setOrderTime(item.getOrderTime());
				selectAged.setOrderState(item.getOrderState());
				selectAged.setFinishTime(item.getFinishTime());
				selectAged.setDistributeTime(item.getDistributeTime());
				shipmentSelectAgeds.add(selectAged);
			}
		}
		return shipmentSelectAgeds;
	}
    
	/**
	 * 时效跟踪 导出
	 * @author jzc 2016年12月19日
	 * @param response
	 * @param pager
	 */
	@RequestMapping("/export1")
	public void export1(HttpServletResponse response,@RequestBody(required = false) LarPager<ShipmentCitySend> pager) {
		
		Map<String, Object> map = pager.getExtendMap();
        List<Long> ids = new ArrayList<>();
        
        try{
	         if (map != null && null != map.get("orgId")) {
	             Long id = Long.valueOf(map.get("orgId") + "");
	             Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
	             if (null != id) {
	                 //是父节点再去查找
	                 if (isParentNode) {
	                     List<Org> list = orgService.findById(id, true);
	                     for (Org org : list) {
	                         ids.add(org.getOrgId());
	                     }
	                 } else {
	                     Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
	                     result.put("org", id);
	                     pager.setParams(result);
	                     ids = null;
	                 }
	             }
	         }
        }catch(Exception e){
       	 e.printStackTrace();
        }
		//LarPager<ShipmentCitySend> pager = new LarPager<>();
		pager.setPageSize(10000000);
		LarPager<ShipmentCitySend> orderTimeLarPager = shipmentCitySendService.findByOrgIds(pager, ids);
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<ShipmentSelectAged> exportExcelUtils = new ExportExcelUtils<>("同城送");
			Workbook workbook = null;
			try {
				List<ShipmentCitySend> list = this.convert(orderTimeLarPager.getResult());
				workbook = exportExcelUtils.writeContents("同城送", this.convertToSelectAged(list));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
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
    
	/**
	 * 订单查询 导出
	 * @author jzc 2016年12月19日
	 * @param response
	 * @param pager
	 */
	@RequestMapping(value="/export",method=RequestMethod.POST)
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<ShipmentCitySend> pager) {
		 Map<String, Object> map = pager.getExtendMap();
         List<Long> ids = new ArrayList<>();
         
         try{
	         if (map != null && null != map.get("orgId")) {
	             Long id = Long.valueOf(map.get("orgId") + "");
	             Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
	             if (null != id) {
	                 //是父节点再去查找
	                 if (isParentNode) {
	                     List<Org> list = orgService.findById(id, true);
	                     for (Org org : list) {
	                         ids.add(org.getOrgId());
	                     }
	                 } else {
	                     Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
	                     result.put("org", id);
	                     pager.setParams(result);
	                     ids = null;
	                 }
	             }
	         }
         }catch(Exception e){
        	 e.printStackTrace();
         }
		
		//LarPager<ShipmentCitySend> pager = new LarPager<>();
		pager.setPageSize(10000000);
		LarPager<ShipmentCitySend> orderTimeLarPager = shipmentCitySendService.findByOrgIds(pager, ids);
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<ShipmentCitySend> exportExcelUtils = new ExportExcelUtils<>("同城送");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("同城送", this.convert(orderTimeLarPager.getResult()));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
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
	
	/**
	 * 	对账登记 导出
	 * @author jzc 2016年12月19日
	 * @param response
	 * @param pager
	 */
	@RequestMapping(value="/export2",method=RequestMethod.POST)
	public void export2(HttpServletResponse response,@RequestBody(required = false) LarPager<ShipmentCitySend> pager) {
		 Map<String, Object> map = pager.getExtendMap();
         List<Long> ids = new ArrayList<>();
         
         try{
	         if (map != null && null != map.get("orgId")) {
	             Long id = Long.valueOf(map.get("orgId") + "");
	             Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
	             if (null != id) {
	                 //是父节点再去查找
	                 if (isParentNode) {
	                     List<Org> list = orgService.findById(id, true);
	                     for (Org org : list) {
	                         ids.add(org.getOrgId());
	                     }
	                 } else {
	                     Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
	                     result.put("org", id);
	                     pager.setParams(result);
	                     ids = null;
	                 }
	             }
	         }
         }catch(Exception e){
        	 e.printStackTrace();
         }
		
		//LarPager<ShipmentCitySend> pager = new LarPager<>();
		pager.setPageSize(10000000);
		LarPager<ShipmentCitySend> orderTimeLarPager = shipmentCitySendService.findByOrgIds(pager, ids);
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<ShipmentCitySendAcc> exportExcelUtils = new ExportExcelUtils<>("同城送");
			Workbook workbook = null;
			try {
				List<ShipmentCitySend> list = this.convert(orderTimeLarPager.getResult());
				workbook = exportExcelUtils.writeContents("同城送", this.convertToSelectAcc(list));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
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
	
    /**
     * 导出转换为对账人字段
     * @author jzc 2016年12月19日
     * @param list
     * @return
     */
    private List<ShipmentCitySendAcc> convertToSelectAcc(List<ShipmentCitySend> list) {
        List<ShipmentCitySendAcc> shipmentSelectAcc = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ShipmentCitySend item : list) {
            	ShipmentCitySendAcc selectAcc = new ShipmentCitySendAcc();
            	selectAcc.setOrderNo(item.getOrderNo());
            	selectAcc.setBizType(item.getBizType());
            	selectAcc.setOrderTime(item.getOrderTime());
                selectAcc.setOrderState(item.getOrderState());
                selectAcc.setOrgName(item.getOrgName());
                selectAcc.setAreaName(item.getAreaName());
                selectAcc.setSalesManName(item.getSalesManName());
                selectAcc.setFactWeight(item.getFactWeight());
                selectAcc.setFactDistance(item.getFactDistance());
                selectAcc.setSaveMoney(item.getSaveMoney());
                selectAcc.setReceivableService(item.getReceivableService());
                selectAcc.setFactServiceCost(item.getFactServiceCost());
                selectAcc.setFinishRemarks(item.getFinishRemarks());
                selectAcc.setAccountUserName(item.getAccountUserName());
                selectAcc.setAccountState(item.getAccountState());
                selectAcc.setAccountRemarks(item.getAccountRemarks());
                selectAcc.setAccountTime(item.getAccountTime());
                selectAcc.setBalance(item.getBalance());
                shipmentSelectAcc.add(selectAcc);
            }
        }
        return shipmentSelectAcc;
    }

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}
}
