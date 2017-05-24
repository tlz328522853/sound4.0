package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.service.ShipmentTurnOrderService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.KeyToNameUtil;
import com.sdcloud.web.lar.util.LarPagerUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 转单记录表
 * Created by 韩亚辉 on 2016/4/5.
 */
@RestController
@RequestMapping("/api/turnOrder")
public class ShipmentTurnOrderController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private ShipmentTurnOrderService turnOrderService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UserService userService;
    @Autowired
    private DicService dicService;
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/findAll")
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentTurnOrder> larPager) {
        try {
            return ResultDTO.getSuccess(200, turnOrderService.findAll(larPager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/save")
    public ResultDTO save(@RequestBody(required = false) ShipmentTurnOrder shipmentCitySend, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentCitySend.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, turnOrderService.save(shipmentCitySend));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/update")
    public ResultDTO update(@RequestBody(required = false) ShipmentTurnOrder shipmentCitySend, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentCitySend.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, turnOrderService.update(shipmentCitySend));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping("/delete/{id}")
    public ResultDTO delete(@PathVariable("id") Long id) {
        try {
            return ResultDTO.getSuccess(200, turnOrderService.delete(id));
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipmentTurnOrder> larPager) {
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
            LarPager<ShipmentTurnOrder> pager = turnOrderService.findByOrgIds(larPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                pager.setResult(this.convert(pager.getResult()));
            }
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    private List<ShipmentTurnOrder> convert(List<ShipmentTurnOrder> list) throws Exception {
        List<Long> empList = new ArrayList<>();
        Set<Long> empSet = new HashSet<>();
        List<Long> orgList = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>();
        for (ShipmentTurnOrder citySend : list) {
            if (null != citySend.getTurnOrderUser()) {
                empSet.add(citySend.getTurnOrderUser());
            }
            if (null != citySend.getOrg()) {
                orgList.add(citySend.getOrg());
            }
        }
//        Map<Long, User> users = new HashMap<>();
        Map<Long, Org> orgs = new HashMap<>();
        if (empSet.size() > 0) {
            empList.addAll(empSet);
            employeeList=employeeService.findById(empList);
//            users = userService.findUserMapByIds(empList);
        }
        if (orgList.size() > 0) {
            orgs = orgService.findOrgMapByIds(orgList, false);
        }
        for (ShipmentTurnOrder citySend : list) {
//        	boolean flag=false;
//        	
//            if (!CollectionUtils.isEmpty(users)&&citySend.getTurnOrderUser()!=null) {
//                User user = users.get(citySend.getTurnOrderUser());
//                if (null != user) {
//                	flag=true;
//                	if(user.getEmployee()!=null){
//                		//显示员工的名字
//                		citySend.setTurnOrderUserName(
//                		        StringUtils.isNotEmpty(user.getEmployee().getName())?
//                				user.getEmployee().getName():"员工名空"
//                		);
//                	}
//                }
//            }
            
//            if(!flag){
            	for(Employee emp:employeeList){
            		if(citySend.getTurnOrderUser().equals(emp.getEmployeeId())){
            			//显示员工的名字
                		citySend.setTurnOrderUserName(
                		        StringUtils.isNotEmpty(emp.getName())?
                		        		emp.getName():"员工名空"
                		);
            			break;
            		}
            	}
//            }
            
            if (!CollectionUtils.isEmpty(orgs)&&citySend.getOrg()!=null) {
                Org org = orgs.get(citySend.getOrg());
                if (null != org) {
                    citySend.setOrgName(org.getName());
                }
            }
        }
        return list;
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<ShipmentTurnOrder> pager) {
    		Map<String, Object> map = pager.getExtendMap();
    		List<Long> ids = new ArrayList<>();
		    try {
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
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	
       // LarPager<ShipmentTurnOrder> pager = new LarPager<>();
        pager.setPageSize(10000000);
        LarPager<ShipmentTurnOrder> orderTimeLarPager = turnOrderService.findByOrgIds(pager, ids);
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentTurnOrder> exportExcelUtils = new ExportExcelUtils<>("同城送");
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
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
