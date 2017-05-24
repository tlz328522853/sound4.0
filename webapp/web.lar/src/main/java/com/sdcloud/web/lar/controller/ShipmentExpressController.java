package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentExpress;
import com.sdcloud.api.lar.service.ShipmentExpressService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
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
 * Created by 夏文柱 on 2016/5/13.
 */
@RestController
@RequestMapping("/api/express")
public class ShipmentExpressController {
    @Autowired
    private ShipmentExpressService expressService;
    @Autowired
    private OrgService orgService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody ShipmentExpress shipmentExpressLarPager, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentExpressLarPager.setCreateUser(user.getUserId());
            }
            
            //同一个机构不能有相同的快递公司
            Long count= expressService.getCountByExpressAndOrgId(null,shipmentExpressLarPager.getExpress(),shipmentExpressLarPager.getOrg());
            if(null!=count && count>0){
            	return ResultDTO.getFailure(AppCode.BIZ_ERROR, "该快递公司已被添加！");
            }
            
            return ResultDTO.getSuccess(200,"保存成功!",expressService.save(shipmentExpressLarPager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO update(@RequestBody ShipmentExpress shipmentExpress, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentExpress.setUpdateUser(user.getUserId());
            }
          //同一个机构不能有相同的快递公司
            Long count= expressService.getCountByExpressAndOrgId(shipmentExpress.getId(),shipmentExpress.getExpress(),shipmentExpress.getOrg());
            if(null!=count && count>0){
            	return ResultDTO.getFailure(AppCode.BIZ_ERROR, "此机构已经有该快递公司！");
            }
         
            return ResultDTO.getSuccess(200, "修改成功!", expressService.update(shipmentExpress));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResultDTO delete(@PathVariable Long id) {
        try {
            return ResultDTO.getSuccess(200, "删除成功!",expressService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentExpress> larPager) {
        try {
            return ResultDTO.getSuccess(200, expressService.findAll(larPager));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentExpress> larPager) {
    	larPager.setOrderBy("sequence");
    	larPager.setOrder("asc");
    	
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
            LarPager<ShipmentExpress> pager = expressService.findByOrgIds(larPager, ids);
            
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

    private List<ShipmentExpress> convert(List<ShipmentExpress> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentExpress express : list) {
            if (null != express.getCreateUser()) {
                empSet.add(express.getCreateUser());
            }
            if (null != express.getUpdateUser()) {
                empSet.add(express.getUpdateUser());
            }
            
            if (null != express.getOrg()) {
                orgList.add(express.getOrg());
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
        for (ShipmentExpress express : list) {
            if (null != users && users.size() > 0) {
                if (null != express.getCreateUser()) {
                    User user = users.get(express.getCreateUser());
                    if (null != user) {
                    	express.setCreateUserName(user.getName());
                    }
                }
                if (null != express.getUpdateUser()) {
                    User user = users.get(express.getUpdateUser());
                    if (null != user) {
                    	express.setUpdateUserName(user.getName());
                    }
                }
                
            }
            if (null != orgs && orgs.size() > 0) {
                if (null != express.getOrg()) {
                    Org org = orgs.get(express.getOrg());
                   
                }
            }

        }
        return list;
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<ShipmentExpress> pager = new LarPager<>();
        pager.setPageSize(10000);
        LarPager<ShipmentExpress> orderTimeLarPager = expressService.findByOrgIds(pager, null);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentExpress> exportExcelUtils = new ExportExcelUtils<>("操作点设置");
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
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
