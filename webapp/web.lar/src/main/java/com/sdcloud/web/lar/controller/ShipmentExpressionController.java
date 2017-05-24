package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentExpression;
import com.sdcloud.api.lar.service.ShipmentExpressionService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 物流--》公式设置
 * Created by 韩亚辉 on 2016/3/22.
 */
@RestController
@RequestMapping("/api/expression")
public class ShipmentExpressionController {
    @Autowired
    private ShipmentExpressionService shipmentExpressionService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentExpression> larPager) {
        try {
            return ResultDTO.getSuccess(200, shipmentExpressionService.findAll(larPager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/save")
    public ResultDTO save(@RequestBody(required = false) ShipmentExpression shipmentExpression, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentExpression.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "保存成功!",shipmentExpressionService.save(shipmentExpression));
        } catch (Exception e) {
        	e.printStackTrace();
        	Class clazz = e.getClass();
        	if("org.springframework.dao.DuplicateKeyException".equals(clazz.getName())){
        		return ResultDTO.getFailure("此业务类别已存在,不能重复添加!");
        	}
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) ShipmentExpression shipmentExpression, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentExpression.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "修改成功!",shipmentExpressionService.update(shipmentExpression));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentExpression> larPager) {
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
            LarPager<ShipmentExpression> pager = shipmentExpressionService.findByOrgIds(larPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                pager.setResult(this.convert(pager.getResult()));
            }
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    private List<ShipmentExpression> convert(List<ShipmentExpression> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        for (ShipmentExpression expression : list) {
            if (null != expression.getCreateUser()) {
                empSet.add(expression.getCreateUser());
            }
            if (null != expression.getUpdateUser()) {
                empSet.add(expression.getUpdateUser());
            }
            if (null != expression.getOrg()) {
                orgList.add(expression.getOrg());
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
        for (ShipmentExpression expression : list) {
            if (null != users && users.size() > 0) {
                if (null != expression.getCreateUser()) {
                    User user = users.get(expression.getCreateUser());
                    if (null != user) {
                        expression.setCreateUserName(user.getName());
                    }
                }
                if (null != expression.getUpdateUser()) {
                    User user = users.get(expression.getUpdateUser());
                    if (null != user) {
                        expression.setUpdateUserName(user.getName());
                    }
                }
            }
            if (null != orgs && orgs.size() > 0) {
                if (null != expression.getOrg()) {
                    Org org = orgs.get(expression.getOrg());
                    if (null != org) {
                        expression.setOrgName(org.getName());
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/delete/{id}")
    public ResultDTO delete(@PathVariable("id") Long id) {
        try {
            return ResultDTO.getSuccess(200, "删除成功!",shipmentExpressionService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
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
