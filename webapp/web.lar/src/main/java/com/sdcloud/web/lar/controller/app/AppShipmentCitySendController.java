package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentSelectAged;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/27.
 */
@RestController
@RequestMapping("/app/send")
public class AppShipmentCitySendController {
    @Autowired
    private ShipmentCitySendService shipmentCitySendService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;
    @Autowired
    private DicService dicService;
    @Autowired
    private CityService cityService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentCitySend> larPager) {
        try {
            LarPager<ShipmentCitySend> citySendLarPager = shipmentCitySendService.findAll(larPager);
            return ResultDTO.getSuccess(AppCode.SUCCESS, "获取同城送列表成功",citySendLarPager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 同城送保存订单
     *
     * @param orderCharge
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody(required = false) ShipmentCitySend shipmentCitySend) {
        try {
            City city = cityService.selectByCityId(shipmentCitySend.getCity());
            shipmentCitySend.setOrg(city.getOrg());
            shipmentCitySend.setOrderNo(OrderUtils.generateNumber());
            shipmentCitySend.setOrderTime(new Date());
            shipmentCitySend.setOrderState("等待接单");
            shipmentCitySend.setBizType("同城送");
            boolean flag = shipmentCitySendService.save(shipmentCitySend);
            if (flag) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("scheduleDate", shipmentCitySend.getScheduleDate());
                params.put("scheduleTime", shipmentCitySend.getScheduleTime());
                return ResultDTO.getSuccess(AppCode.SUCCESS, "订单提交成功", params);
            } else {
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) ShipmentCitySend shipmentCitySend) {
        try {
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
                return ResultDTO.getSuccess(200, shipmentCitySendService.updateState(result, list, flag));
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

    private List<ShipmentSelectAged> convertToSelectAged(List<ShipmentCitySend> list) {
        List<ShipmentSelectAged> shipmentSelectAgeds = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ShipmentCitySend item : list) {
                ShipmentSelectAged selectAged = new ShipmentSelectAged();
                selectAged.setOrderNo(item.getOrderNo());
                selectAged.setAccountTime(item.getAccountTime());
                selectAged.setSalesMan(item.getSalesMan());
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

    @RequestMapping("/export1")
    public void export1(HttpServletResponse response) {
        LarPager<ShipmentCitySend> pager = new LarPager<>();
        pager.setPageSize(10000000);
        LarPager<ShipmentCitySend> orderTimeLarPager = shipmentCitySendService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentSelectAged> exportExcelUtils = new ExportExcelUtils<>("寄快递");
            Workbook workbook = null;
            try {
                List<ShipmentCitySend> list = this.convert(orderTimeLarPager.getResult());
                workbook = exportExcelUtils.writeContents("寄快递", this.convertToSelectAged(list));
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

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<ShipmentCitySend> pager = new LarPager<>();
        pager.setPageSize(10000000);
        LarPager<ShipmentCitySend> orderTimeLarPager = shipmentCitySendService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
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
}
