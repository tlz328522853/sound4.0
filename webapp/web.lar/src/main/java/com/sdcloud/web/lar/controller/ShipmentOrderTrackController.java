package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentSelectAged;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/4/6.
 */
@RestController
@RequestMapping("/api/orderTrack")
public class ShipmentOrderTrackController {
    @Autowired
    private ShipmentSendExpressService shipmentSendExpressService;
    @Autowired
    private ShipmentCitySendService shipmentCitySendService;
    @Autowired
    private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager larPager) {
        LarPager<ShipmentSendExpress> sendExpressLarPager = new LarPager<>();
        sendExpressLarPager.setExtendMap(larPager.getExtendMap());
        sendExpressLarPager.setExtendList(larPager.getExtendList());
        sendExpressLarPager.setParams(larPager.getParams());
        LarPager<ShipmentCitySend> citySendLarPager = new LarPager<>();
        citySendLarPager.setExtendMap(larPager.getExtendMap());
        citySendLarPager.setExtendList(larPager.getExtendList());
        citySendLarPager.setParams(larPager.getParams());
        LarPager<ShipmentHelpMeBuy> helpMeBuyLarPager = new LarPager<>();
        helpMeBuyLarPager.setExtendMap(larPager.getExtendMap());
        helpMeBuyLarPager.setExtendList(larPager.getExtendList());
        helpMeBuyLarPager.setParams(larPager.getParams());
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
            LarPager<ShipmentSelectAged> result = new LarPager<>();
            List<ShipmentSelectAged> list = new ArrayList<>();
            LarPager<ShipmentSendExpress> pager = shipmentSendExpressService.findByOrgIds(sendExpressLarPager, ids);
            LarPager<ShipmentCitySend> pager1 = shipmentCitySendService.findByOrgIds(citySendLarPager, ids);
            LarPager<ShipmentHelpMeBuy> pager2 = shipmentHelpMeBuyService.findByOrgIds(helpMeBuyLarPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                list.addAll(this.convertToSelectAged1(this.convert1(pager.getResult())));
            }
            if (null != pager1 && pager1.getResult() != null && pager1.getResult().size() > 0) {
                list.addAll(this.convertToSelectAged2(this.convert2(pager1.getResult())));
            }
            if (null != pager2 && pager2.getResult() != null && pager2.getResult().size() > 0) {
                list.addAll(this.convertToSelectAged3(this.convert3(pager2.getResult())));
            }
            result.setResult(list);
            return ResultDTO.getSuccess(200, result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    private List<ShipmentSendExpress> convert1(List<ShipmentSendExpress> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentSendExpress item : list) {
            if (null != item.getCreateUser()) {
                empSet.add(item.getCreateUser());
            }
            if (null != item.getUpdateUser()) {
                empSet.add(item.getUpdateUser());
            }
            if (null != item.getCancelUser()) {
                empSet.add(item.getCancelUser());
            }
            if (null != item.getTakeUser()) {
                empSet.add(item.getTakeUser());
            }
            if (null != item.getDistributeUser()) {
                empSet.add(item.getDistributeUser());
            }
            if (null != item.getTurnOrderUser()) {
                empSet.add(item.getTurnOrderUser());
            }
            if (null != item.getCancelDistributeUser()) {
                empSet.add(item.getCancelDistributeUser());
            }
            if (null != item.getFinishUser()) {
                empSet.add(item.getFinishUser());
            }
            if (null != item.getCancelTakeUser()) {
                empSet.add(item.getCancelTakeUser());
            }
            if (null != item.getOrg()) {
                orgList.add(item.getOrg());
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
        for (ShipmentSendExpress item : list) {
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

    private List<ShipmentSelectAged> convertToSelectAged1(List<ShipmentSendExpress> list) {
        List<ShipmentSelectAged> shipmentSelectAgeds = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ShipmentSendExpress item : list) {
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

    private List<ShipmentCitySend> convert2(List<ShipmentCitySend> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentCitySend item : list) {
            if (null != item.getCreateUser()) {
                empSet.add(item.getCreateUser());
            }
            if (null != item.getUpdateUser()) {
                empSet.add(item.getUpdateUser());
            }
            if (null != item.getCancelUser()) {
                empSet.add(item.getCancelUser());
            }
            if (null != item.getTakeUser()) {
                empSet.add(item.getTakeUser());
            }
            if (null != item.getDistributeUser()) {
                empSet.add(item.getDistributeUser());
            }
            if (null != item.getTurnOrderUser()) {
                empSet.add(item.getTurnOrderUser());
            }
            if (null != item.getCancelDistributeUser()) {
                empSet.add(item.getCancelDistributeUser());
            }
            if (null != item.getFinishUser()) {
                empSet.add(item.getFinishUser());
            }
            if (null != item.getCancelTakeUser()) {
                empSet.add(item.getCancelTakeUser());
            }
            if (null != item.getOrg()) {
                orgList.add(item.getOrg());
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

    private List<ShipmentSelectAged> convertToSelectAged2(List<ShipmentCitySend> list) {
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

    private List<ShipmentHelpMeBuy> convert3(List<ShipmentHelpMeBuy> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentHelpMeBuy item : list) {
            if (null != item.getCreateUser()) {
                empSet.add(item.getCreateUser());
            }
            if (null != item.getUpdateUser()) {
                empSet.add(item.getUpdateUser());
            }
            if (null != item.getCancelUser()) {
                empSet.add(item.getCancelUser());
            }
            if (null != item.getTakeUser()) {
                empSet.add(item.getTakeUser());
            }
            if (null != item.getDistributeUser()) {
                empSet.add(item.getDistributeUser());
            }
            if (null != item.getTurnOrderUser()) {
                empSet.add(item.getTurnOrderUser());
            }
            if (null != item.getCancelDistributeUser()) {
                empSet.add(item.getCancelDistributeUser());
            }
            if (null != item.getFinishUser()) {
                empSet.add(item.getFinishUser());
            }
            if (null != item.getCancelTakeUser()) {
                empSet.add(item.getCancelTakeUser());
            }
            if (null != item.getOrg()) {
                orgList.add(item.getOrg());
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

    private List<ShipmentSelectAged> convertToSelectAged3(List<ShipmentHelpMeBuy> list) {
        List<ShipmentSelectAged> shipmentSelectAgeds = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ShipmentHelpMeBuy item : list) {
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

    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }

    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        Long org = Long.parseLong(request.getParameter("orgId").toString());
        Long orderNo = Long.parseLong(request.getParameter("orderNo").toString());
        int type = Integer.parseInt(request.getParameter("type").toString());
        boolean isParentNode = Boolean.parseBoolean(request.getParameter("isParentNode").toString());
        if(type==1){

        }else if(type==2){

        }else if(type==3){

        }


    }
}
