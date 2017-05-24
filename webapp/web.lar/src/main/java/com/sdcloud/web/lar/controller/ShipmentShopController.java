package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentShop;
import com.sdcloud.api.lar.service.ShipmentShopService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/23.
 */
@RestController
@RequestMapping("/api/shop")
public class ShipmentShopController {
    @Autowired
    private ShipmentShopService shipmentShopService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;

    @Autowired
    private DicService dicService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentShop> larPager) {
        try {
            return ResultDTO.getSuccess(200, shipmentShopService.findAll(larPager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/save")
    public ResultDTO save(@RequestBody(required = false) ShipmentShop shipmentShop, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentShop.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, shipmentShopService.save(shipmentShop));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) ShipmentShop shipmentShop, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentShop.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, shipmentShopService.update(shipmentShop));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/delete/{id}")
    public ResultDTO delete(@PathVariable("id") Long id) {
        try {
            return ResultDTO.getSuccess(200, shipmentShopService.delete(id));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentShop> larPager) {
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
            return ResultDTO.getSuccess(200, shipmentShopService.findByOrgIds(larPager, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    private List<ShipmentShop> convert(List<ShipmentShop> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        List<Dic> dics = dicService.findByPid(Constant.SHOP_TYPE_PID,null);
        Map<Long, Dic> dicMap = new HashMap<>();
        for (Dic item : dics) {
            dicMap.put(item.getDicId(), item);
        }
        for (ShipmentShop shop : list) {
            if (null != shop.getCreateUser()) {
                empSet.add(shop.getCreateUser());
            }
            if (null != shop.getUpdateUser()) {
                empSet.add(shop.getUpdateUser());
            }
            if (null != shop.getOrg()) {
                orgList.add(shop.getOrg());
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
        for (ShipmentShop shop : list) {
            if (null != users && users.size() > 0) {
                if (null != shop.getCreateUser()) {
                    User user = users.get(shop.getCreateUser());
                    if (null != user) {
                        shop.setCreateUserName(user.getName());
                    }
                }
                if (null != shop.getUpdateUser()) {
                    User user = users.get(shop.getUpdateUser());
                    if (null != user) {
                        shop.setUpdateUserName(user.getName());
                    }
                }
            }
            if (null != orgs && orgs.size() > 0) {
                if (null != shop.getOrg()) {
                    Org org = orgs.get(shop.getOrg());
                    if (null != org) {
                        shop.setOrgName(org.getName());
                    }
                }
            }
            if (null != dicMap && dicMap.size() > 0) {
                if (null != shop.getType()) {
                    Dic dic = dicMap.get(shop.getType());
                    if (null != dic) {
                        shop.setTypeName(dic.getName());
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/export")
    public void export(@RequestBody(required=false) LarPager<ShipmentShop> pager, HttpServletResponse response) throws Exception {
        pager.setPageSize(10000000);
        Map<String, Object> map = pager.getExtendMap();
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
                    Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
                    result.put("org", id);
                    pager.setParams(result);
                    ids = null;
                }
            }
        }
        LarPager<ShipmentShop> orderTimeLarPager = shipmentShopService.findByOrgIds(pager, ids);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentShop> exportExcelUtils = new ExportExcelUtils<>("商店定义");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("商店定义", this.convert(orderTimeLarPager.getResult()));
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

    @RequestMapping("/exist/{id}")
    public ResultDTO exist(@RequestBody Map<String, String> map, @PathVariable("id") Long id) {
        try {
            if (id == 0) {
                id = null;
            }
//            Boolean flag=shipmentShopService.exist(map, id);
            return ResultDTO.getSuccess(200, false);
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
