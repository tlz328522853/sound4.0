package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.*;
import com.sdcloud.api.lar.service.*;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by 韩亚辉 on 2016/4/7.
 */
@RequestMapping("/app/voucher")
@RestController
public class AppVoucherController {
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
    public ResultDTO save(@RequestBody(required = false) Voucher shipmentShop) {
        try {
            shipmentShop.setReleaseDate(new Date());
            shipmentShop.setReleaseUser(UUIDUtil.getUUNum());
            shipmentShop.setUseType("可用");
            return ResultDTO.getSuccess(200, voucherService.save(shipmentShop));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/batchSave")
    public ResultDTO batchSave(@RequestBody(required = false) Voucher voucher) {
        try {
            voucher.setUseType("可用");
            List<Voucher> list = new ArrayList<>();
            LarPager<LarClientUser> larClientUserLarPager = new LarPager<>();
            larClientUserLarPager.setPageSize(1000000000);
            LarPager<LarClientUser> userLarPager = larClientUserService.selectByExample(larClientUserLarPager);
            if (null != voucher) {
                if (voucher.getType().equals("全体客户")) {
                    if (null != userLarPager && userLarPager.getResult() != null && userLarPager.getResult().size() > 0) {
                        for (LarClientUser item : userLarPager.getResult()) {
                            Voucher obj = (Voucher) voucher.clone();
                            obj.setId(UUIDUtil.getUUNum());
                            obj.setReleaseDate(new Date());
                            obj.setReleaseUser(UUIDUtil.getUUNum());
                            obj.setCustomerId(Long.parseLong(item.getId()));
                            list.add(obj);
                        }
                    }
                } else {
                    VoucherCondition voucherCondition = voucherConditionService.getById(voucher.getCondition(),null);
                    //部分用户
                    if ("1".equals(voucherCondition.getType())) {
                        //1 总金额
                        List<ShipmentHelpMeBuy> helpMeBuyLarPager1 = shipmentHelpMeBuyService.getBalance();
                        List<ShipmentCitySend> shipmentCitySends = shipmentCitySendService.getBalance();
                        List<ShipmentSendExpress> shipmentSendExpresses = shipmentSendExpressService.getBalance();
                        Map<Long, Double> map = new HashMap<>();
                        if (null != helpMeBuyLarPager1 && helpMeBuyLarPager1.size() > 0) {
                            for (ShipmentHelpMeBuy item : helpMeBuyLarPager1) {
                                if (null != item.getTotalCost()) {
                                    if (map.keySet().contains(item.getCustomerId())) {
                                        map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getTotalCost());
                                    } else {
                                        map.put(item.getCustomerId(), item.getTotalCost());
                                    }
                                }
                            }
                        }
                        if (null != shipmentCitySends && shipmentCitySends.size() > 0) {
                            for (ShipmentCitySend item : shipmentCitySends) {
                                if (null != item.getFactServiceCost()) {
                                    if (map.keySet().contains(item.getCustomerId())) {
                                        map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getFactServiceCost());
                                    } else {
                                        map.put(item.getCustomerId(), item.getFactServiceCost());
                                    }
                                }
                            }
                        }
                        if (null != shipmentSendExpresses && shipmentSendExpresses.size() > 0) {
                            for (ShipmentSendExpress item : shipmentSendExpresses) {
                                if (null != item.getMoneyOne()) {
                                    if (map.keySet().contains(item.getCustomerId())) {
                                        map.put(item.getCustomerId(), map.get(item.getCustomerId()) + item.getMoneyOne());
                                    } else {
                                        map.put(item.getCustomerId(), item.getMoneyOne());
                                    }
                                }
                            }
                        }
                        for (Long item : map.keySet()) {
                            Double value = map.get(item);
                            Voucher obj = (Voucher) voucher.clone();
                            obj.setId(UUIDUtil.getUUNum());
                            obj.setReleaseDate(new Date());
                            obj.setReleaseUser(UUIDUtil.getUUNum());
                            if (null == voucherCondition.getTotalStart() && null != voucherCondition.getTotalEnd() && voucherCondition.getTotalEnd() != 0) {
                                if (value <= voucherCondition.getTotalEnd()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                            if (null != voucherCondition.getTotalStart() && voucherCondition.getTotalStart() != 0 && null == voucherCondition.getTotalEnd()) {
                                if (value >= voucherCondition.getTotalStart()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                            if (null != voucherCondition.getTotalStart() && null != voucherCondition.getTotalEnd() && voucherCondition.getTotalStart() != 0 && voucherCondition.getTotalEnd() != 0) {
                                if (value >= voucherCondition.getTotalStart() && value <= voucherCondition.getTotalEnd()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                        }
                    } else {
                        //2 总次数
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
                                if (map.keySet().contains(item.getAppUserId())) {
                                    map.put(Long.parseLong(item.getAppUserId()), map.get(item.getAppUserId()) + item.getOrderCount());
                                } else {
                                    map.put(Long.parseLong(item.getAppUserId()), item.getOrderCount());
                                }
                            }
                        }
                        for (Long item : map.keySet()) {
                            Integer value = map.get(item);
                            Voucher obj = (Voucher) voucher.clone();
                            obj.setId(UUIDUtil.getUUNum());
                            obj.setReleaseDate(new Date());
                            obj.setReleaseUser(UUIDUtil.getUUNum());
                            if (null == voucherCondition.getServiceCountStart() && null != voucherCondition.getServiceCountEnd() && voucherCondition.getServiceCountEnd() != 0) {
                                if (value <= voucherCondition.getServiceCountEnd()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                            if (null != voucherCondition.getServiceCountStart() && voucherCondition.getServiceCountStart() != 0 && null == voucherCondition.getServiceCountEnd()) {
                                if (value >= voucherCondition.getServiceCountStart()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                            if (null != voucherCondition.getServiceCountStart() && null != voucherCondition.getServiceCountEnd() && voucherCondition.getServiceCountStart() != 0 && voucherCondition.getServiceCountEnd() != 0) {
                                if (value >= voucherCondition.getServiceCountStart() && value <= voucherCondition.getServiceCountEnd()) {
                                    obj.setCustomerId(item);
                                    list.add(obj);
                                }
                            }
                        }
                    }
                }
            }
            if (list != null && list.size() > 0) {
                return ResultDTO.getSuccess(200, voucherService.batchSave(list));
            } else {
                return ResultDTO.getSuccess(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) Voucher shipmentShop) {
        try {
            return ResultDTO.getSuccess(200, voucherService.update(shipmentShop));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/delete/{id}")
    public ResultDTO delete(@PathVariable("id") Long id) {
        try {
            return ResultDTO.getSuccess(200, voucherService.delete(id));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Voucher> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
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
                        item.setReleaseUserName(user.getName());
                    }
                }
            }
        }
        return list;
    }

    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
