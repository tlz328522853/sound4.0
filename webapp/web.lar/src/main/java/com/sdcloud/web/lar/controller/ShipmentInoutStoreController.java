package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentInoutStore;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.ShipmentInoutStoreService;
import com.sdcloud.api.lar.service.ShipmentSendExpressService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/5/11.
 */
@RestController
@RequestMapping("/api/inoutStore")
public class ShipmentInoutStoreController {
    @Autowired
    private ShipmentInoutStoreService shipmentInoutStoreService;

    @Autowired
    private ShipmentSendExpressService shipmentSendExpressService;
    @Autowired
    private ShipmentHelpMeBuyService shipmentHelpMeBuyService;
    @Autowired
    private ShipmentCitySendService shipmentCitySendService;
    @Autowired
    private OrgService orgService;

    /**
     * 列表
     *
     * @param pager
     * @return
     */
    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentInoutStore> pager) {
        try {
            return ResultDTO.getSuccess(200, shipmentInoutStoreService.findAll(pager));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentInoutStore> larPager) {
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
            LarPager<ShipmentInoutStore> pager = shipmentInoutStoreService.findByOrgIds(larPager, ids);
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
     * 添加
     *
     * @param shipmentOrderTime
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody ShipmentInoutStore shipmentOrderTime, HttpServletRequest request) {
        try {
            Object userId = request.getAttribute("token_userId");
            shipmentOrderTime.setId(UUIDUtil.getUUNum());
            shipmentOrderTime.setScanTime(new Date());
            if (null != userId && userId != "") {
                User user = userService.findByUesrId(Long.valueOf(userId + ""));
                shipmentOrderTime.setScanUser(user.getUserId());
            }
            ShipmentSendExpress sendExpress = shipmentSendExpressService.getByNo(shipmentOrderTime.getOrderNo());
            if (null != sendExpress) {
                shipmentOrderTime.setOrderTime(sendExpress.getOrderTime());
                shipmentOrderTime.setArea(sendExpress.getArea());
                shipmentOrderTime.setBizType(sendExpress.getBizType());
                shipmentOrderTime.setOrderState(sendExpress.getOrderState());
                shipmentOrderTime.setSalesMan(sendExpress.getSalesMan());
            } else {
                ShipmentCitySend shipmentCitySend = shipmentCitySendService.getByNo(shipmentOrderTime.getOrderNo());
                if (null != shipmentCitySend) {
                    shipmentOrderTime.setOrderTime(shipmentCitySend.getOrderTime());
                    shipmentOrderTime.setArea(shipmentCitySend.getArea());
                    shipmentOrderTime.setBizType(shipmentCitySend.getBizType());
                    shipmentOrderTime.setOrderState(shipmentCitySend.getOrderState());
                    shipmentOrderTime.setSalesMan(shipmentCitySend.getSalesMan());
                } else {
                    ShipmentHelpMeBuy shipmentHelpMeBuy = shipmentHelpMeBuyService.getByNo(shipmentOrderTime.getOrderNo());
                    if (null != shipmentHelpMeBuy) {
                        shipmentOrderTime.setOrderTime(shipmentHelpMeBuy.getOrderTime());
                        shipmentOrderTime.setArea(shipmentHelpMeBuy.getArea());
                        shipmentOrderTime.setBizType(shipmentHelpMeBuy.getBizType());
                        shipmentOrderTime.setOrderState(shipmentHelpMeBuy.getOrderState());
                        shipmentOrderTime.setSalesMan(shipmentHelpMeBuy.getSalesMan());
                    } else {
                        return ResultDTO.getFailure(500, "请输入正确的订单号");
                    }
                }
            }

            return ResultDTO.getSuccess(200, shipmentInoutStoreService.save(shipmentOrderTime));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 修改
     *
     * @param shipmentOrderTime
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO update(@RequestBody ShipmentInoutStore shipmentOrderTime, HttpServletRequest request) {
        try {
            return ResultDTO.getSuccess(200, shipmentInoutStoreService.update(shipmentOrderTime));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResultDTO delete(@PathVariable Long id) {
        try {
            return ResultDTO.getSuccess(200, shipmentInoutStoreService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @Autowired
    private UserService userService;

    private List<ShipmentInoutStore> convert(List<ShipmentInoutStore> list) {
        try {
            List<Long> empList = new ArrayList<>();
            Set<Long> empSet = new HashSet<>();
            List<Long> orgList = new ArrayList<>();
            for (ShipmentInoutStore sendExpress : list) {
                if (null != sendExpress.getScanUser()) {
                    empSet.add(sendExpress.getScanUser());
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
            for (ShipmentInoutStore item : list) {
                if (null != users && users.size() > 0) {
                    if (null != item.getScanUser()) {
                        User user = users.get(item.getScanUser());
                        if (null != user) {
                            item.setScanUserName(user.getName());
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
        } catch (Exception e) {
            return list;
        }
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<ShipmentInoutStore> pager = new LarPager<>();
        pager.setPageSize(1000000);
        Map<String, Object> map = new HashMap<>();
        map.put("scan_type", "入库");
        pager.setParams(map);
        LarPager<ShipmentInoutStore> orderTimeLarPager = shipmentInoutStoreService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
                ExportExcelUtils<ShipmentInoutStore> exportExcelUtils = new ExportExcelUtils<>("扫描入库");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("扫描入库", this.convert(orderTimeLarPager.getResult()));
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

    @RequestMapping("/export1")
    public void export1(HttpServletResponse response) {
        LarPager<ShipmentInoutStore> pager = new LarPager<>();
        pager.setPageSize(1000000);
        Map<String, Object> map = new HashMap<>();
        map.put("scan_type", "出库");
        pager.setParams(map);
        LarPager<ShipmentInoutStore> orderTimeLarPager = shipmentInoutStoreService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentInoutStore> exportExcelUtils = new ExportExcelUtils<>("扫描出库");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("扫描出库", this.convert(orderTimeLarPager.getResult()));
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
