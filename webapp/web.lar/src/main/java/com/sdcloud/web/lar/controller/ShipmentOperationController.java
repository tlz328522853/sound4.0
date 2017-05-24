package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by 韩亚辉 on 2016/3/18.
 */
@RestController
@RequestMapping("/api/operation")
public class ShipmentOperationController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private ShipmentOperationService operationService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody ShipmentOperation shipmentOperationLarPager, HttpServletRequest request) {
        try {
        	logger.info("Enter the :{} method  shipmentOperationLarPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),shipmentOperationLarPager);
        	
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentOperationLarPager.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "保存成功!",operationService.save(shipmentOperationLarPager));
        } catch (Exception e) {
        	logger.error("method {} execute error, shipmentOperationLarPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), shipmentOperationLarPager, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO update(@RequestBody ShipmentOperation shipmentOperation, HttpServletRequest request) {
        try {
        	logger.info("Enter the :{} method  shipmentOperation:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),shipmentOperation);
        	
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentOperation.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "修改成功!",operationService.update(shipmentOperation));
        } catch (Exception e) {
        	logger.error("method {} execute error, shipmentOperation:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), shipmentOperation, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResultDTO delete(@PathVariable Long id) {
        try {
        	logger.info("Enter the :{} method  id:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),id);
        	
            return ResultDTO.getSuccess(200, "删除成功!",operationService.delete(id));
        } catch (Exception e) {
        	logger.error("method {} execute error, id:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), id, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentOperation> larPager) {
        try {
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
        	
            return ResultDTO.getSuccess(200, operationService.findAll(larPager));
        } catch (Exception e) {
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
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
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
        	
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
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
    
    /**
     * 根据机构id获取本机构及子机构的员工数据
     *
     * @param larPager
     * @return
     */
    @RequestMapping(value="/findEmpByOrg",method=RequestMethod.POST)
    @ResponseBody
    public ResultDTO findEmpByOrg(@RequestBody(required=false) LarPager<ShipmentOperation> larPager) {
        try {
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
        	Map<String, Object> empParam = new HashMap<>();
        
            Map<String, Object> param = larPager.getParams();
            //备份参数
            Set<Entry<String,Object>> set = param.entrySet();
            for (Entry<String,Object> entry : set) {
				empParam.put(entry.getKey(), entry.getValue());
			}
            List<Long> ids = new ArrayList<>();
            if (!param.isEmpty()) {
                Long id = Long.valueOf(param.get("orgId") + "");
                Boolean includeSub = Boolean.valueOf(param.get("includeSub") + "");
                
                List<Org> list = orgService.findById(id, includeSub);
                for (Org org : list) {
                    ids.add(org.getOrgId());
                }
                param.clear();
            }
            LarPager<ShipmentOperation> pager = operationService.findEmploy(larPager, ids);
            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
                List<Employee> emps = this.convertToEmp(pager.getResult(),empParam);
                Field field = pager.getClass().getDeclaredField("result");
                field.setAccessible(true);
                field.set(pager, emps);
                pager.setTotalCount(emps.size());
            }
            pager.setParams(empParam);
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
    
    private List<Employee> convertToEmp(List<ShipmentOperation> result,Map<String, Object> param) throws Exception {
		List<Long> list = new ArrayList<>();
    	for (ShipmentOperation so : result) {
			if(null != so.getSysUser()){
				list.add(so.getSysUser());
			}
		}
    	List<Employee> empList = new ArrayList<>();
    	List<Employee> emps = employeeService.findById(list);
    	
    	Object name = param.get("name");
		Object mobileMac = param.get("mobileMac");
    	if(null == name && null == mobileMac){
    		return emps;
    	}
    	
    	for (int i = 0; i < emps.size(); i++) {
    		Employee emp = emps.get(i);
			if( (null != name && null == mobileMac && null != emp.getName())
					&& emp.getName().contains(name+"") && !empList.contains(emp)){
				empList.add(emp);
				continue;
			}
			if(null != mobileMac && null == name && (mobileMac+"").equals(emp.getMobileMac()) 
					&& !empList.contains(emp)){
				empList.add(emp);
				continue;
			}
			if(null != mobileMac && null != name && (mobileMac+"").equals(emp.getMobileMac()) 
					&& emp.getName().contains(name+"") && !empList.contains(emp)){
				empList.add(emp);
			}
		}
		return empList;
	}
    
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
        Map<Long, Employee> emps = new HashMap<>();
        if (empSet.size() > 0) {
            empList.addAll(empSet);
            Map<Long, User> map = userService.findUserMapByIds(empList);
            if(null != map){
            	users = map;
            }
            List<Employee> findById = employeeService.findById(empList);
            if(null != findById){
            	for (Employee employee : findById) {
    				emps.put(employee.getEmployeeId(), employee);
    			}
            }
        }
        if (orgList.size() > 0) {
            orgs = orgService.findOrgMapByIds(orgList, false);
        }
        for (ShipmentOperation operation : list) {
            if (null != operation.getCreateUser()) {
                User user = users.get(operation.getCreateUser());
                if (null != user) {
                    operation.setCreateUserName(user.getName());
                }
            }
            User user = users.get(operation.getUpdateUser());
            if (null != user) {
                operation.setUpdateUserName(user.getName());
            }
            Employee emp = emps.get(operation.getSysUser());
            if (null != emp) {
                operation.setSysUserName(emp.getName());
            }
            if (null != orgs && orgs.size() > 0) {
                Org org = orgs.get(operation.getOrg());
                if (null != org) {
                    operation.setOrgName(org.getName());
                }
            }
        }
        return list;
    }

    @RequestMapping("/export")
    public void export(@RequestBody(required=false) LarPager<ShipmentOperation> larPager,HttpServletResponse response) throws Exception {
    	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
				.getStackTrace()[1].getMethodName(),larPager);
    	
    	Map<String, Object> map = larPager.getExtendMap();
    	larPager.pageSize(100000);
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
        LarPager<ShipmentOperation> orderTimeLarPager = operationService.findByOrgIds(larPager, ids);
    	
        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentOperation> exportExcelUtils = new ExportExcelUtils<>("业务员设置");
            Workbook workbook = null;
            try {
                workbook = exportExcelUtils.writeContents("业务员设置", this.convert(orderTimeLarPager.getResult()));
                String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
                String headStr = "attachment; filename=\"" + fileName + "\"";
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                OutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
            } catch (Exception e) {
            	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
    					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
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
        	logger.info("Enter the :{} method  map:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),map);
        	
            if (id == 0) {
                id = null;
            }
//            Boolean flag=shipmentShopService.exist(map, id);
            return ResultDTO.getSuccess(200, false);
        } catch (Exception e) {
        	logger.error("method {} execute error, map:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), map, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
