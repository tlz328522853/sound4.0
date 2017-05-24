package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.service.*;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by 韩亚辉 on 2016/4/19.
 */
@RestController
@RequestMapping("/app/order")
public class AppOrderServiceController {
    @Autowired
    private ShipmentCitySendService citySendService;
    @Autowired
    private ShipmentSendExpressService shipmentSendExpressService;
    @Autowired
    private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderManagerService orderManagerService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private DicService dicService;
    @Autowired
    private ShipmentOperationService shipmentOperationService;
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/wasteDetail", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO wasteDetail(@RequestBody(required = false) Map paramMap) {
        Map<String, Object> map = new HashMap<>();
        Long orderNo = null;
        Long userId = null;
        try {
            orderNo = (Long) paramMap.get("orderNo");
            userId = Long.parseLong(paramMap.get("userId") + "");
        } catch (Exception e) {
        }
        if (null == orderNo || null == userId) {
            return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
        }
        map.put("orderId", orderNo);
        map.put("confirmOrder", 1);
        List<OrderManager> orderManagers = orderManagerService.detail(map);
        OrderManager orderManager = null;
        if (null != orderManagers && orderManagers.size() > 0) {
            orderManager = orderManagers.get(0);
        }
        Salesman salesman = orderManager.getSalesman();
        //订单详情里的业务员名字,原来是显示salesman的名字,现在改为显示employee的名字
        try {
        	if(null !=salesman &&null !=salesman.getPersonnelId() && !"".equals(salesman.getPersonnelId())){
			salesman.setManName(employeeService.findById(Long.parseLong(salesman.getPersonnelId())).getName());
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        map.clear();
        List<OrderManager> orderManagers1;
        
        if(null != salesman && null != salesman.getId()){
        	map.put("salesman", orderManager.getSalesman().getId());
        	//evaluateId 的值不能为null和0
        	map.put("evaluateId",true);
        	orderManagers1 = orderManagerService.detail(map);
        }else{
        	orderManagers1 = new ArrayList<>();
        }
        //计算评价
        return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", this.calculateEvaluation(orderManagers1, orderManager));
    }

    @RequestMapping(value = "/wasteDetail1", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO wasteDetail1(@RequestBody(required = false) Map paramMap) {
        Map<String, Object> map = new HashMap<>();
        Long orderNo = null;
        Long userId = null;
        try {
            orderNo = (Long) paramMap.get("orderNo");
            userId = Long.parseLong(paramMap.get("userId") + "");
        } catch (Exception e) {
        }
        if (null == orderNo || null == userId) {
            return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
        }
        map.put("orderId", orderNo);
        map.put("confirmOrder", 1);
        List<OrderManager> orderManagers = orderManagerService.detail(map);
        OrderManager orderManager = null;
        if (null != orderManagers && orderManagers.size() > 0) {
            orderManager = orderManagers.get(0);
        }
        map.clear();
        map.put("customerId", userId);
        List<OrderManager> orderManagers1 = orderManagerService.detail(map);
        //计算评价
        OrderManager orderManager2 = this.calculateEvaluation(orderManagers1, orderManager);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderManager", orderManager2);
        params.put("childOrders", orderManager2.getChildOrders());
        return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", params);
    }

    @RequestMapping(value = "/evaluate", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO evaluate(@RequestBody(required = false) Map paramMap) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Long orderNo = null;
        Integer type = null;
        Long userId = null;
        try {
            orderNo = (Long) paramMap.get("orderNo");
            type = (Integer) paramMap.get("type");
            userId = Long.parseLong(paramMap.get("userId") + "");
        } catch (Exception e) {
        }
        if (null == orderNo || null == type || null == userId) {
            return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
        }
        OrderDetailDTO orderDetailDTO = null;
        OrderManager orderManager = null;
        switch (type) {
            case 2:
                map.put("biz_type", "同城送");
                orderDetailDTO = citySendService.orderDetail(orderNo, map);
                break;
            case 3:
                map.put("biz_type", "帮我买");
                orderDetailDTO = shipmentHelpMeBuyService.orderDetail(orderNo, map);
                break;
            case 1:
                map.put("biz_type", "寄快递");
                orderDetailDTO = shipmentSendExpressService.orderDetail(orderNo, map);
                break;
            default:
                map.put("orderId", orderNo);
                List<OrderManager> orderManagers = orderManagerService.detail(map);
                if (null != orderManagers && orderManagers.size() > 0) {
                	for (OrderManager order : orderManagers) {
						if(Long.valueOf(order.getOrderId()) == orderNo){
							orderManager = order;
							break;
						}
					}
                	if(orderManager == null){
                		orderManager = orderManagers.get(0);
                	}
                }
                map.clear();
                map.put("salesman", orderManager.getSalesman().getId());
                //evaluateId 的值不能为null和0
                map.put("evaluateId",true);
                List<OrderManager> orderManagers1 = orderManagerService.detail(map);
                orderManager = this.calculateEvaluation(orderManagers1, orderManager);

        }
        if (null != orderDetailDTO && null != orderDetailDTO.getSalesMan()) {
            orderDetailDTO = this.calculateEvaluation(orderDetailDTO.getSalesMan(), orderDetailDTO);
        }
        map.clear();
        if (null != orderDetailDTO) {
            orderDetailDTO = this.transform(orderDetailDTO);
            map.put("salesMan", orderDetailDTO.getSalesMan());
            map.put("salesManName", orderDetailDTO.getSalesManName());
            map.put("avatar", "");
            map.put("tel", orderDetailDTO.getTel());
            map.put("orderNo", orderNo);
            map.put("grade", orderDetailDTO.getGrade());
            map.put("evaluate", orderDetailDTO.getEvaluate()==null?"100":orderDetailDTO.getEvaluate());
            map.put("orderState", orderDetailDTO.getOrderState());
            map.put("finishTime", orderDetailDTO.getFinishTime());
        }
        if (null != orderManager) {
            Salesman salesman = orderManager.getSalesman();
            map.put("salesMan", salesman.getId());
            map.put("salesManName", salesman.getManName());
            map.put("avatar", "");
            map.put("tel", salesman.getDeviceId());
            map.put("orderNo", orderNo);
            map.put("grade", orderManager.getGrade());
            String evalue = String.valueOf(orderManager.getEvaluateId());
            map.put("evaluate", "0".equals(evalue)?"100":evalue);
            map.put("orderState", orderManager.getOrderStatusName());
            map.put("finishTime", orderManager.getFinishDate());
        }

        return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单信息成功", map);
    }


    /**
     * 订单详情  1  寄快递 2 同城送 3 帮我买  4  卖废品
     *
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO detail(@RequestBody(required = false) Map paramMap) {
        Map<String, Object> map = new HashMap<>();
        Long orderNo = null;
        Integer type = null;
        Long userId = null;
        try {
            orderNo = (Long) paramMap.get("orderNo");
            type = (Integer) paramMap.get("type");
            userId = Long.parseLong(paramMap.get("userId") + "");
        } catch (Exception e) {
        }
        if (null == orderNo || null == type || null == userId) {
            return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
        }
        OrderDetailDTO orderDetailDTO = null;
        switch (type) {
            case 2:
                map.put("biz_type", "同城送");
                orderDetailDTO = citySendService.orderDetail(orderNo, map);
                break;
            case 3:
                map.put("biz_type", "帮我买");
                orderDetailDTO = shipmentHelpMeBuyService.orderDetail(orderNo, map);
                break;
            default:
                map.put("biz_type", "寄快递");
                orderDetailDTO = shipmentSendExpressService.orderDetail(orderNo, map);
        }
        if (null != orderDetailDTO && null != orderDetailDTO.getSalesMan()) {
            orderDetailDTO = this.calculateEvaluation(orderDetailDTO.getSalesMan(), orderDetailDTO);
        }
        orderDetailDTO = this.convert(orderDetailDTO);
        orderDetailDTO = this.transform(orderDetailDTO);
        return ResultDTO.getSuccess(AppCode.SUCCESS, "获取订单详情成功", orderDetailDTO);
    }

    private OrderManager calculateEvaluation(List<OrderManager> detailDTO, OrderManager orderDetail) {
        int evaluate = 0;
        orderDetail.setGrade(String.valueOf(orderDetail.getEvaluateId()));
        if (null != detailDTO && detailDTO.size() > 0) {
            try {
                for (OrderManager orderDetailDTO : detailDTO) {
                    evaluate += orderDetailDTO.getEvaluateId();
                }
                orderDetail.setEvaluateId(evaluate * 20 / detailDTO.size());
            } catch (Exception e) {

            }
        }
        orderDetail.setEvaluateId(orderDetail.getEvaluateId()==0?100:orderDetail.getEvaluateId());
        return orderDetail;
    }
    
    /**
     * 获取该业务员的平均评分
     * @param salesMan
     * @param orderDetail
     * @return
     */
    private OrderDetailDTO calculateEvaluation(Long salesMan, OrderDetailDTO orderDetail) {
        List<OrderDetailDTO> list = new ArrayList<>();
        List<OrderDetailDTO> orderDetailDTOs = shipmentSendExpressService.getEvaluation(salesMan);
        List<OrderDetailDTO> orderDetailDTOs1 = shipmentHelpMeBuyService.getEvaluation(salesMan);
        List<OrderDetailDTO> orderDetailDTOs2 = citySendService.getEvaluation(salesMan);
        list.addAll(orderDetailDTOs);
        list.addAll(orderDetailDTOs1);
        list.addAll(orderDetailDTOs2);
        Long evaluate = 0l;
        orderDetail.setGrade(orderDetail.getEvaluate());
        if (null != list && list.size() > 0) {
            try {
                for (OrderDetailDTO orderDetailDTO : list) {
                    evaluate += Long.parseLong(orderDetailDTO.getEvaluate());
                }
                orderDetail.setEvaluate(evaluate * 20 / list.size() + "");
            } catch (Exception e) {
                return orderDetail;
            }
        }
        orderDetail.setEvaluate(evaluate==0L?"100":orderDetail.getEvaluate());
        return orderDetail;
    }

    private OrderDetailDTO transform(OrderDetailDTO detailDTO) {
        if (null != detailDTO && null != detailDTO.getSalesMan()) {
            String state = detailDTO.getOrderState();
            Long salesManId = detailDTO.getSalesMan();
            Date orderTime = detailDTO.getOrderTime();
            Employee emp = null;
            try {
                ShipmentOperation o = shipmentOperationService.getById(salesManId, null);
                try {
                    if (null != o) {
                        emp = employeeService.findById(o.getSysUser());
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (null != emp) {
                    switch (state) {
                        case "服务中":
                            detailDTO.setOrderTime(detailDTO.getTakeTime());
                        case "已取消":
                            detailDTO.setOrderTime(detailDTO.getCancelTime());
                        case "已完成":
                            detailDTO.setOrderTime(detailDTO.getFinishTime());
                            if(detailDTO.getExpressCompanyOne()!=null){
                            	 Dic dic=dicService.findById(detailDTO.getExpressCompanyOne());
                            	 if(dic!=null){
                            		 detailDTO.setExpressCompanyOneName(dic.getName());
                            	 }
                            }
                        default:
                            detailDTO.setOrderTime(detailDTO.getTakeTime());
                    }
                    if (null == detailDTO.getOrderTime()) {
                        detailDTO.setOrderTime(orderTime);
                    }
                    detailDTO.setSalesManName(emp.getName());
                    detailDTO.setTel(emp.getMobile());
                    detailDTO.setAvatar("");
                }
                return detailDTO;
            } catch (Exception e) {
                e.printStackTrace();
                return detailDTO;
            }
        } else {
            return detailDTO;
        }

    }

    private OrderDetailDTO convert(OrderDetailDTO detailDTO) {
        try {
            List<Long> orgId = new ArrayList<>();
            if (null != detailDTO) {
                if (null != detailDTO.getOrg()) {
                    orgId.add(detailDTO.getOrg());
                    Map<Long, Org> longOrgMap = orgService.findOrgMapByIds(orgId, false);
                    detailDTO.setOrgName(longOrgMap.get(detailDTO.getOrg()).getName());
                }
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
                    }
                }
                if (companys != null && companys.size() > 0) {
                    for (Dic dic : companys) {
                        if (dic.getDicId() == detailDTO.getExpressCompanyOne() || dic.getDicId().equals(detailDTO.getExpressCompanyOne())) {
                            detailDTO.setExpressCompanyOneName(dic.getName());
                        }
                    }
                }
                return detailDTO;
            } else {
                return detailDTO;
            }
        } catch (Exception e) {
            return detailDTO;
        }
    }

    /**
     * orderId
     * Evaluation 分数  1-5 分
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/updateEvaluation", method = RequestMethod.POST)
    public ResultDTO updateEvaluation(@RequestBody(required = false) Map<String, Object> map) {
        try {
            List<Long> list = new ArrayList<>();
            Long orderId = null;
            Integer bizType = null;
            Integer evaluate = null;
            try {
                bizType = (Integer) map.get("bizType");
                orderId = (Long) map.get("orderId");
                evaluate = (Integer) map.get("evaluate");
                Object token = map.get("token");
                if (null != token) {
                    map.remove("token");
                }
                map.remove("bizType");
                map.remove("orderId");
            } catch (Exception e) {
            }
            Boolean flag = false;
            if (null == orderId || null == bizType) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
            if (null != map) {
                list.add(orderId);
                boolean sFlag=true;
                if (1 == bizType) {
                	sFlag = shipmentSendExpressService.updateState(map, list, flag);
                } else if (2 == bizType) {
                	sFlag = citySendService.updateState(map, list, flag);
                } else if (3 == bizType) {
                	sFlag = shipmentHelpMeBuyService.updateState(map, list, flag);
                } else {
                	sFlag = orderManagerService.updateEvaluation(orderId, evaluate);
                }
                if(sFlag){
                	return ResultDTO.getSuccess(AppCode.SUCCESS, "评价成功",sFlag);
                }else{
                	return ResultDTO.getFailure(AppCode.BIZ_DATA, "评价失败!");
                }
            } else {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * @param map type 1  寄快递 2 同城送 3 帮我买  4  卖废品
     *            orderId 订单编号
     * @return
     */
    @RequestMapping("/cancelOrder")
    public ResultDTO cancelOrder(@RequestBody(required = false) Map<String, Object> map) {
        try {
            Long orderId = Long.parseLong(map.get("orderId") + "");
            Long userId = Long.parseLong(map.get("userId") + "");
            Integer type = Integer.parseInt(map.get("type") + "");
            String remarks = map.get("remarks") + "";
            if (null == orderId || null == type || null == userId) {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
            }
            List<Long> list = new ArrayList<>();
            list.add(orderId);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("order_state", "已取消");
            objectMap.put("cancel_time", new Date());
            objectMap.put("cancel_user", userId);
            objectMap.put("cancel_reason", remarks);
            switch (type) {
                case 1:
                    Boolean flag = shipmentSendExpressService.updateState(objectMap, list, false);
                    if (flag) {
                    	ShipmentSendExpress t = shipmentSendExpressService.getById(orderId,null);
                    	if (null != t.getCoupon()) {
                			Voucher voucher = voucherService.getById(t.getCoupon(), null);
                			voucher.setUseType("可用");
                			voucherService.update(voucher);
                		}
                        return ResultDTO.getSuccess(AppCode.SUCCESS, "取消订单成功");
                    } else {
                        return ResultDTO.getSuccess(AppCode.BIZ_ERROR, "取消订单失败");
                    }
                case 2:
                    Boolean flag1 = citySendService.updateState(objectMap, list, false);
                    if (flag1) {
                    	ShipmentCitySend t = citySendService.getById(orderId,null);
                    	if (null != t.getCoupon()) {
                			Voucher voucher = voucherService.getById(t.getCoupon(), null);
                			voucher.setUseType("可用");
                			voucherService.update(voucher);
                		}
                        return ResultDTO.getSuccess(AppCode.SUCCESS, "取消订单成功");
                    } else {
                        return ResultDTO.getSuccess(AppCode.BIZ_ERROR, "取消订单失败");
                    }
                case 3:
                    Boolean flag2 = shipmentHelpMeBuyService.updateState(objectMap, list, false);
                    if (flag2) {
                    	ShipmentHelpMeBuy t = shipmentHelpMeBuyService.getById(orderId,null);
                    	if (null != t.getCoupon()) {
                			Voucher voucher = voucherService.getById(t.getCoupon(), null);
                			voucher.setUseType("可用");
                			voucherService.update(voucher);
                		}
                        return ResultDTO.getSuccess(AppCode.SUCCESS, "取消订单成功");
                    } else {
                        return ResultDTO.getSuccess(AppCode.BIZ_ERROR, "取消订单失败");
                    }
                case 4:
                    User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
                    Map<String, Object> updateParams = new HashMap<String, Object>();
                    updateParams.put("id", orderId);
                    updateParams.put("orderStatusId", 0);
                    updateParams.put("orderStatusName", "已取消");
                    updateParams.put("cancelTakePersonId", userId);
//                    updateParams.put("cancelTakePersonName", user.getEmployee().getName());
                    updateParams.put("cancelTakeIllustrate", remarks);
                    updateParams.put("cancelTakeDate", new Date());
                    boolean flag3 = orderManagerService.cancelDispatchOrder(updateParams);
                    if (flag3) {
                        return ResultDTO.getSuccess(AppCode.SUCCESS, "取消订单成功");
                    } else {
                        return ResultDTO.getSuccess(AppCode.BIZ_ERROR, "取消订单失败");
                    }
                default:
                    Boolean flag4 = shipmentSendExpressService.updateState(objectMap, list, false);
                    if (flag4) {
                        return ResultDTO.getSuccess(AppCode.SUCCESS, "取消订单成功");
                    } else {
                        return ResultDTO.getSuccess(AppCode.BIZ_ERROR, "取消订单失败");
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }
}
