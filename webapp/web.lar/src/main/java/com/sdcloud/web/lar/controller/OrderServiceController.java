package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.OrderServiceDTO;
import com.sdcloud.api.lar.entity.SelectParam;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.api.lar.service.ShipmentTurnOrderService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.OrderUtils;

/**
 * 物流 订单服务 相关
 * Created by 韩亚辉 on 2016/4/13.
 */
@RequestMapping("/api/order")
@RestController
public class OrderServiceController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShipmentCitySendService citySendService;
    @Autowired
    private ShipmentSendExpressService shipmentSendExpressService;
    @Autowired
    private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
    @Autowired
    private DicService dicService;
    @Autowired
    private ShipmentTurnOrderService turnOrderService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;

    ///////////////////////////////////////////////////业务员端/////////////////////////////////////////////////////////////////

    /**
     * 等待服务列表
     *
     * @param larPager
     * @return
     */
    @RequestMapping(value = "/serviceList", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO serviceList(@RequestBody(required = false) LarPager<OrderServiceDTO> larPager) {
        try {
            Map<String, Object> map = larPager.getParams();
            Object id = map.get("userId");
            if (null == id) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            } else {
                Long userId = Long.parseLong(id + "");
                map.remove("userId");
                map.put("order_state", "服务中");
                larPager.setParams(map);
                LarPager<OrderServiceDTO> pager = shipmentSendExpressService.serviceList(larPager, userId);
                if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
                    pager.setResult(this.convert(pager.getResult()));
                }
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取等待列表成功", pager);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return ResultDTO.getSuccess(AppCode.SYSTEM_ERROR, "获取等待列表失败",null);
        }
    }

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private OrderManagerService orderManagerService;

    /**
     * 抢单列表
     *
     * @param larPager
     * @return
     */
    @RequestMapping(value = "/grabOrderList", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO grabOrderList(@RequestBody(required = false) LarPager<OrderServiceDTO> larPager) {
        try {
            //Map<String, String> map1 = sysConfigService.findMap();
            Map<String, Object> map = larPager.getParams();
            Object id = map.get("userId");
            String orgId = map.get("orgId")+"";
            
            if (null == id) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            } else {
                Long userId = Long.parseLong(id + "");
                map.remove("userId");
                map.remove("token");
                map.remove("orgId");
                map.put("grab_order", "31");
                larPager.setParams(map);
                //TODO 业务修改,注释代码
                /*Long orderOutTime = Long.parseLong(map1.get("orderOutTime"))*60;//抢单时效
                shipmentSendExpressService.updateGrabState(orderOutTime);//修改过期的状态
                citySendService.updateGrabState(orderOutTime);
                shipmentHelpMeBuyService.updateGrabState(orderOutTime);
                orderManagerService.updateGrabState(orderOutTime);*/
                LarPager<OrderServiceDTO> pager = shipmentSendExpressService.grabOrderList(larPager, userId,orgId);
                if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
                    pager.setResult(this.convert(pager.getResult()));
                }
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取抢单列表成功", pager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getSuccess(AppCode.SYSTEM_ERROR,"获取抢单列表失败",null);
        }
    }

    @Autowired
    private ShipmentOperationService shipmentOperationService;

    /**
     * 抢单   ids
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/grabOrder", method = RequestMethod.POST)
    public ResultDTO grabOrder(@RequestBody(required = false) Map<String, Object> map) {
        try {
            List<Long> list = new ArrayList<>();
            map.put("orderStateNo", 8);
            Long userId = null;
            Integer bizType = null;
            Integer terminal = null;
            try {
                userId = Long.parseLong(map.get("userId") + "");
                map.remove("token");
                map.remove("userId");
                bizType = (Integer) map.get("bizType");
                map.remove("bizType");
                terminal = (Integer) map.get("terminal");
                map.remove("terminal");
                String token = map.get("token") + "";
                if (null != token) {
                    map.remove("token");
                }
            } catch (Exception e) {
            	 e.printStackTrace();
            }
            Boolean flag = false;
            if (null == userId || null == bizType) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
            Map<String, Object> result = OrderUtils.appParamConvert(map, userId, list);

            /*ShipmentOperation operation = shipmentOperationService.findBySysId(userId);
            if (operation != null) {
                result.put("area", operation.getArea());
                result.put("sales_man", operation.getId());
            }*/
            result.put("userId", userId);
            Boolean aBoolean = shipmentSendExpressService.grabOrder(terminal, bizType, result, list, flag);
            if (aBoolean) {
                return ResultDTO.getSuccess(AppCode.SUCCESS, "抢单成功");
            } else {
                return ResultDTO.getSuccess(AppCode.BIZ_GRB_FAIL, "抢单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getSuccess(AppCode.SYSTEM_ERROR, "服务器错误");
        }
    }

    /**
     * 等待服务列表
     *
     * @param larPager
     * @return
     */
    @RequestMapping(value = "/serviceHistory", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO serviceHistory(@RequestBody(required = false) LarPager<OrderServiceDTO> larPager) {
        try {
            Map<String, Object> map = larPager.getParams();
            Object id = map.get("userId");
            if (null == id) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            } else {
                Long userId = Long.parseLong(id + "");
                map.remove("userId");
                SelectParam selectParam = new SelectParam();
                selectParam.setKey("order_state");
                selectParam.setOperation("=");
                selectParam.setValue("已完成");
                SelectParam selectParam1 = new SelectParam();
                selectParam1.setKey("order_state");
                selectParam1.setOperation("=");
                selectParam1.setValue("已取消");
                List<Object> list = new ArrayList<>();
                list.add(selectParam);
                list.add(selectParam1);
                larPager.setExtendList(list);
                larPager.setParams(map);
                LarPager<OrderServiceDTO> pager = shipmentSendExpressService.serviceHistory(larPager, userId);
                if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
                    pager.setResult(this.convert(pager.getResult()));
                }
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取等待列表成功", pager);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }

    private OrderDetailDTO convertDetail(OrderDetailDTO detailDTO) {
        try {
            if (null == detailDTO) {
                return detailDTO;
            } else {
                List<Dic> buyTypes = dicService.findByPid(Constant.SHOP_TYPE_PID, null);
                List<Dic> companys = dicService.findByPid(Constant.COMPANY, null);
                if (buyTypes != null && buyTypes.size() > 0) {
                    for (Dic dic : buyTypes) {
                        if (dic.getDicId() == detailDTO.getBuyType() || dic.getDicId().equals(detailDTO.getBuyType())) {
                            detailDTO.setBuyTypeName(dic.getName());
                        }
                    }
                }
                if (companys != null && companys.size() > 0) {
                    for (Dic dic : companys) {
                        if (dic.getDicId() == detailDTO.getExpressCompany() || dic.getDicId().equals(detailDTO.getExpressCompany())) {
                            detailDTO.setExpressCompanyName(dic.getName());
                        }
                        if (dic.getDicId() == detailDTO.getExpressCompanyOne() || dic.getDicId().equals(detailDTO.getExpressCompanyOne())) {
                            detailDTO.setExpressCompanyOneName(dic.getName());
                        }
                    }
                }
                if (detailDTO.getOrg() != null) {
                    List<Long> org = new ArrayList<>();
                    org.add(detailDTO.getOrg());
                    Map<Long, Org> longOrgMap = orgService.findOrgMapByIds(org, false);
                    detailDTO.setOrgName(longOrgMap.get(detailDTO.getOrg()).getName());
                }
                List<Long> empList = new ArrayList<>();
                Set<Long> empSet = new HashSet<>();
                if (null != detailDTO.getDistributeUser()) {
                    empSet.add(detailDTO.getDistributeUser());
                }
                Map<Long, User> users = new HashMap<>();
                if (empSet.size() > 0) {
                    empList.addAll(empSet);
                    users = userService.findUserMapByIds(empList);
                }
                if (null != detailDTO.getDistributeUser()) {
                    User user = users.get(detailDTO.getDistributeUser());
                    detailDTO.setDistributeUserName(user.getName());
                }
            }
            return detailDTO;
        } catch (Exception e) {
            return detailDTO;
        }
    }

    /**
     * 转换 机构和商店和商店类型
     *
     * @param list
     * @return
     * @throws Exception
     */
    private List<OrderServiceDTO> convert(List<OrderServiceDTO> list) throws Exception {
        try {
        	List<Dic> buyTypes = dicService.findByPid(Constant.SHOP_TYPE_PID, null);
            List<Dic> companys = dicService.findByPid(Constant.COMPANY, null);
            List<Long> orgId = new ArrayList<>();
            Set<Long> orgIds = new HashSet<>();
            for (OrderServiceDTO item : list) {
                if (null != item.getOrg()) {
                    orgIds.add(item.getOrg());
                }
            }
            orgId.addAll(orgIds);
            Map<Long, Org> longOrgMap = orgService.findOrgMapByIds(orgId, false);
            for (OrderServiceDTO item : list) {
                if (null != item.getOrg()) {
                    item.setOrgName(longOrgMap.get(item.getOrg()).getName());
                }
                if (null != item.getBuyType()) {
                    for (Dic dic : buyTypes) {
                        if (dic.getDicId() == item.getBuyType() || dic.getDicId().equals(item.getBuyType())) {
                            item.setBuyTypeName(dic.getName());
                        }
                    }
                }
            }
            for(OrderServiceDTO serviceDTO:list){
            	if(StringUtils.isEmpty(serviceDTO.getBizType())
            			||!serviceDTO.getBizType().equals("寄快递")){
            		continue;
            	}
            	if (companys != null && companys.size() > 0) {
                    for (Dic dic : companys) {
                        if (dic.getDicId() == serviceDTO.getExpress() || dic.getDicId().equals(serviceDTO.getExpress())) {
                        	serviceDTO.setExpressName(dic.getName());
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            return list;
        }
       
        return list;
    }


    /**
     * 订单详情  1  寄快递 2 同城送 3 帮我买
     *
     * @return
     */
    @RequestMapping(value = "/orderDetail", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO orderDetail(@RequestBody(required = false) Map paramMap) {
        Map<String, Object> map = new HashMap<>();
        Long orderNo = null;
        Integer type = null;
        try {
            orderNo = (Long) paramMap.get("orderNo");
            type = (Integer) paramMap.get("type");
        } catch (Exception e) {
        }
        if (null == orderNo || null == type) {
            return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
        }
        List<ShipmentTurnOrder> list = turnOrderService.getByOrderId(orderNo);
        switch (type) {
            case 1:
                map.put("biz_type", "寄快递");
                OrderDetailDTO orderDetail = shipmentSendExpressService.orderDetail(orderNo, map);
                orderDetail.setTurnOrders(list);
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", this.convertDetail(orderDetail));
            case 2:
                map.put("biz_type", "同城送");
                OrderDetailDTO orderDetail1 = citySendService.orderDetail(orderNo, map);
                orderDetail1.setTurnOrders(list);
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", this.convertDetail(orderDetail1));
            case 3:
                map.put("biz_type", "帮我买");
                OrderDetailDTO orderDetail2 = shipmentHelpMeBuyService.orderDetail(orderNo, map);
                orderDetail2.setTurnOrders(list);
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", this.convertDetail(orderDetail2));
            default:
                map.put("biz_type", "寄快递");
                OrderDetailDTO orderDetail3 = shipmentSendExpressService.orderDetail(orderNo, map);
                orderDetail3.setTurnOrders(list);
                return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", this.convertDetail(orderDetail3));
        }
    }

    /**
     * @param map orderStateNo  1 转单   2  确认订单
     *            bizType 1  寄快递 2 同城送 3 帮我买
     * @param
     * @return
     */
    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
    public ResultDTO updateState(@RequestBody(required = false) Map<String, Object> map) {
        try {
            List<Long> list = new ArrayList<>();
            Long userId = null;
            Integer orderStateNo = null;
            Integer bizType = null;
            try {
                userId = Long.parseLong(map.get("userId") + "");
                map.remove("userId");
                orderStateNo = (Integer) map.get("orderStateNo");
                bizType = (Integer) map.get("bizType");
                map.remove("bizType");
                String token = map.get("token") + "";
                if (null != token) {
                    map.remove("token");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Boolean flag = false;
            if (null == userId || null == orderStateNo) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
            Map<String, Object> result = OrderUtils.appParamConvert(map, userId, list);
            if (1 == orderStateNo) {
                flag = true;
                result.put("turn_order_user", userId);
                result.put("turn_order_time", new Date());
            } else {
                result.put("order_state", "已完成");
                result.put("finish_time", new Date());
                result.put("finish_user", userId);
                String date = "";
                Date endTime = null;
                Date startTime = null;
                if (1 == bizType) {
                    ShipmentSendExpress shipmentCitySend = shipmentSendExpressService.getById(list.get(0), null);
                    if (null != shipmentCitySend) {
                        endTime = (Date) result.get("finish_time");
                        startTime = shipmentCitySend.getOrderTime();
                    }
                } else if (2 == bizType) {
                    ShipmentCitySend shipmentCitySend = citySendService.getById(list.get(0), null);
                    if (null != shipmentCitySend) {
                        endTime = (Date) result.get("finish_time");
                        startTime = shipmentCitySend.getOrderTime();
                    }
                } else {
                    ShipmentHelpMeBuy shipmentCitySend = shipmentHelpMeBuyService.getById(list.get(0), null);
                    if (null != shipmentCitySend) {
                        endTime = (Date) result.get("finish_time");
                        startTime = shipmentCitySend.getOrderTime();
                    }
                }
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
            if (null != result) {
                if (1 == bizType) {
                    return ResultDTO.getSuccess(AppCode.SUCCESS, "操作成功", shipmentSendExpressService.updateState(result, list, flag));
                } else if (2 == bizType) {
                    return ResultDTO.getSuccess(AppCode.SUCCESS, "操作成功", citySendService.updateState(result, list, flag));
                } else {
                    return ResultDTO.getSuccess(AppCode.SUCCESS, "操作成功", shipmentHelpMeBuyService.updateState(result, list, flag));
                }
            } else {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }

    }

    @RequestMapping(value = "/getDicList/{pid}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO getDicList(@PathVariable("pid") Long pid) {
        List<Dic> list = new ArrayList<Dic>();
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("orderBy", "sequence");
            list = dicService.findByPid(pid, param);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.BIZ_ERROR, "获取字典失败");
        }
        return ResultDTO.getSuccess(AppCode.SUCCESS, "获取字典成功", list);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResultDTO handlerException(Exception ex) {
    	logger.error("系统处理异常:method {}, Exception:{}",
    			Thread.currentThread().getStackTrace()[1].getMethodName(),ex,ex); 
    	return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
    }
    
	/**
	 * 业务员端 使用 等待服务列表 中的 总数
	 * 物流服务中订单数量
	 * 业务员接口,根据机构人员编号查询业务员，再更具业务员查询物流服务中的订单数量
	 * @author jzc 2016年11月15日
	 * @param employeeId
	 * @return
	 * @throws Exception
	 */
/*	@RequestMapping(value = "/getDispatchCount/{employeeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getDispatchCount(@PathVariable(value = "employeeId") String employeeId) throws Exception {
		try {
			if (employeeId != null && employeeId.trim().length() > 0) {
				int count = shipmentSendExpressService.queryOrderCount(employeeId);
				if (count > 0) {
					return ResultDTO.getSuccess(count);
				} else {
					return ResultDTO.getFailure(500, "该业务员没有新的订单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/

}
