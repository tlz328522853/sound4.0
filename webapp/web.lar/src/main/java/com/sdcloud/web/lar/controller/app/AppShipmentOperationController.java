package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/18.
 */
@RestController
@RequestMapping("/app/operation")
public class AppShipmentOperationController {
    @Autowired
    private ShipmentOperationService operationService;
    @Autowired
    private OrgService orgService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody ShipmentOperation shipmentOperationLarPager) {
        try {
            return ResultDTO.getSuccess(200, operationService.save(shipmentOperationLarPager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO update(@RequestBody ShipmentOperation shipmentOperation) {
        try {
            return ResultDTO.getSuccess(200, operationService.update(shipmentOperation));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResultDTO delete(@PathVariable Long id) {
        try {
            return ResultDTO.getSuccess(200, operationService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentOperation> larPager) {
        try {
            return ResultDTO.getSuccess(200, operationService.findAll(larPager));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentOperation> larPager) {
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
            LarPager<ShipmentOperation> pager = operationService.findByOrgIds(larPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                pager.setResult(this.convert(pager.getResult()));
            }
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @Autowired
    private UserService userService;

    private List<ShipmentOperation> convert(List<ShipmentOperation> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentOperation operation : list) {
            if (null != operation.getCreateUser()) {
                empSet.add(operation.getCreateUser());
            }
            if (null != operation.getUpdateUser()) {
                empSet.add(operation.getUpdateUser());
            }
            if (null != operation.getSysUser()) {
                empSet.add(operation.getSysUser());
            }
            if (null != operation.getOrg()) {
                orgList.add(operation.getOrg());
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
        for (ShipmentOperation operation : list) {
            if (null != users && users.size() > 0) {
                if (null != operation.getCreateUser()) {
                    User user = users.get(operation.getCreateUser());
                    if (null != user) {
                        operation.setCreateUserName(user.getName());
                    }
                }
                if (null != operation.getUpdateUser()) {
                    User user = users.get(operation.getUpdateUser());
                    if (null != user) {
                        operation.setUpdateUserName(user.getName());
                    }
                }
                if (null != operation.getSysUser()) {
                    User user = users.get(operation.getSysUser());
                    if (null != user) {
                        operation.setSysUserName(user.getName());
                    }
                }
            }
            if (null != orgs && orgs.size() > 0) {
                if (null != operation.getOrg()) {
                    Org org = orgs.get(operation.getOrg());
                    if (null != org) {
                        operation.setOrgName(org.getName());
                    }
                }
            }

        }
        return list;
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<ShipmentOperation> pager = new LarPager<>();
        pager.setPageSize(1000000);
        LarPager<ShipmentOperation> orderTimeLarPager = operationService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentOperation> exportExcelUtils = new ExportExcelUtils<>("操作点设置");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("操作点设置", this.convert(orderTimeLarPager.getResult()));
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
}
