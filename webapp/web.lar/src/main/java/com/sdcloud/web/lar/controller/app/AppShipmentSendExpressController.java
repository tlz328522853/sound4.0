package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.SelectParam;
import com.sdcloud.api.lar.entity.ShipmentSelectAged;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/25.
 */
@RestController
@RequestMapping("/app/express")
public class AppShipmentSendExpressController {
    @Autowired
    private ShipmentSendExpressService shipmentSendExpressService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;
    @Autowired
    private DicService dicService;
    @Autowired
    private CityService cityService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentSendExpress> larPager) {
        try {
        	larPager.setOrderBy("a.order_time,a.schedule_date");
        	larPager.setOrder("DESC,DESC");
        	larPager = shipmentSendExpressService.findAll(larPager);
        	List<Dic> dics = dicService.findByPid(Constant.COMPANY, null);
        	Map<Long, Dic> map = new HashMap<>();
        	for (Dic dic : dics) {
				map.put(dic.getDicId(), dic);
			}
        	for (ShipmentSendExpress sse : larPager.getResult()) {
				if(sse.getExpressCompany() != null && !map.isEmpty()){
					Dic dic = map.get(sse.getExpressCompany());
					if(null != dic){
						sse.setExpressCompanyName(dic.getName());
					}
				}
			}
            return ResultDTO.getSuccess(AppCode.SUCCESS, "获取寄快递列表成功", larPager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 保存寄快递信息
     *
     * @param orderCharge
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody(required = false) ShipmentSendExpress shipmentSendExpress) {
        try {
            City city = cityService.selectByCityId(shipmentSendExpress.getCity());
            shipmentSendExpress.setOrg(city.getOrg());
            shipmentSendExpress.setOrderNo(OrderUtils.generateNumber());
            shipmentSendExpress.setOrderTime(new Date());
            shipmentSendExpress.setOrderState("等待接单");
            shipmentSendExpress.setBizType("寄快递");
            boolean flag = shipmentSendExpressService.save(shipmentSendExpress);
            if (flag) {
                Map params = new HashMap();
                params.put("scheduleDate", shipmentSendExpress.getScheduleDate());
                params.put("scheduleTime", shipmentSendExpress.getScheduleTime());
                return ResultDTO.getSuccess(AppCode.SUCCESS, "订单提交成功", params);
            } else {
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单提交失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器异常！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) ShipmentSendExpress shipmentSendExpress) {
        try {
            return ResultDTO.getSuccess(200, shipmentSendExpressService.update(shipmentSendExpress));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/delete/{id}")
    public ResultDTO delete(@PathVariable("id") Long id) {
        try {
            return ResultDTO.getSuccess(200, shipmentSendExpressService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    private List<ShipmentSendExpress> convert(List<ShipmentSendExpress> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        List<Dic> dics = dicService.findByPid(Constant.COMPANY, null);
        Map<Long, Dic> dicMap = new HashMap<>();
        for (Dic item : dics) {
            dicMap.put(item.getDicId(), item);
        }
        for (ShipmentSendExpress sendExpress : list) {
            if (null != sendExpress.getCreateUser()) {
                empSet.add(sendExpress.getCreateUser());
            }
            if (null != sendExpress.getUpdateUser()) {
                empSet.add(sendExpress.getUpdateUser());
            }

            if (null != sendExpress.getCancelUser()) {
                empSet.add(sendExpress.getCancelUser());
            }
            if (null != sendExpress.getTakeUser()) {
                empSet.add(sendExpress.getTakeUser());
            }
            if (null != sendExpress.getDistributeUser()) {
                empSet.add(sendExpress.getDistributeUser());
            }
            if (null != sendExpress.getTurnOrderUser()) {
                empSet.add(sendExpress.getTurnOrderUser());
            }
            if (null != sendExpress.getCancelDistributeUser()) {
                empSet.add(sendExpress.getCancelDistributeUser());
            }
            if (null != sendExpress.getFinishUser()) {
                empSet.add(sendExpress.getFinishUser());
            }
            if (null != sendExpress.getCancelTakeUser()) {
                empSet.add(sendExpress.getCancelTakeUser());
            }
            if (null != sendExpress.getSalesMan()) {
                empSet.add(sendExpress.getSalesMan());
            }
            if (null != sendExpress.getPreviousSalesMan()) {
                empSet.add(sendExpress.getPreviousSalesMan());
            }
            if (null != sendExpress.getAccountUser()) {
                empSet.add(sendExpress.getAccountUser());
            }

            if (null != sendExpress.getOrg()) {
                orgList.add(sendExpress.getOrg());
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
            if (null != dicMap && dicMap.size() > 0) {
                if (null != item.getExpressCompany()) {
                    Dic dic = dicMap.get(item.getExpressCompany());
                    if (null != dic) {
                        item.setExpressCompanyName(dic.getName());
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/selectData")
    public ResultDTO selectData(@RequestBody(required = false) LarPager<ShipmentSendExpress> larPager) {
        try {
            Map<String, Object> map = larPager.getParams();
            List<SelectParam> list = (List<SelectParam>) map.get("params");
            return ResultDTO.getSuccess(200, shipmentSendExpressService.selectData(list));
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
                return ResultDTO.getSuccess(200, shipmentSendExpressService.updateState(result, list, flag));
            } else {
                return ResultDTO.getFailure(400, "参数错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/export")
    public void export(@RequestBody(required = false) LarPager<ShipmentSendExpress> pager, HttpServletResponse response) {
        pager.setPageSize(10000000);
        LarPager<ShipmentSendExpress> orderTimeLarPager = shipmentSendExpressService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentSendExpress> exportExcelUtils = new ExportExcelUtils<>("寄快递");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("寄快递", this.convert(orderTimeLarPager.getResult()));
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

    private List<ShipmentSelectAged> convertToSelectAged(List<ShipmentSendExpress> list) {
        List<ShipmentSelectAged> shipmentSelectAgeds = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ShipmentSendExpress item : list) {
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
    public void export1(@RequestBody(required = false) LarPager<ShipmentSendExpress> pager, HttpServletResponse response, HttpServletRequest request) {
        Map<String, String[]> aa = request.getParameterMap();
        pager.setPageSize(10000000);
        LarPager<ShipmentSendExpress> orderTimeLarPager = shipmentSendExpressService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentSelectAged> exportExcelUtils = new ExportExcelUtils<>("寄快递");
            Workbook workbook = null;
            try {
                List<ShipmentSendExpress> list = this.convert(orderTimeLarPager.getResult());
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

    /**
     * 根据机构id获取本机构及子机构的数据
     *
     * @param larPager
     * @return
     */
    @RequestMapping("/findByOrgIds")
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentSendExpress> larPager) {
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
            LarPager<ShipmentSendExpress> pager = shipmentSendExpressService.findByOrgIds(larPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                pager.setResult(this.convert(pager.getResult()));
            }
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
