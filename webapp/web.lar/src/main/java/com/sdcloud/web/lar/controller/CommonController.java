package com.sdcloud.web.lar.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.Pager;
import com.sdcloud.framework.entity.ResultDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 一些调别人的公共的方法放在这里
 * Created by 韩亚辉 on 2016/3/25.
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserService userService;
    
    /**
     * pager必须传入分页的pagesize 如果不想分页需要把pagesize传的足够大
     *
     * @param pager        分页对象
     * @param orgId        机构id
     * @param needChildren 是否查询子节点
     * @return
     * @throws Exception 
     */
    @RequestMapping("/findEmpByOrgId/{needChildren}/{orgId}")
    @ResponseBody
    public ResultDTO findEmpByOrgId(@RequestBody(required = false) Pager<Employee> pager, @PathVariable("needChildren") Boolean needChildren, @PathVariable("orgId") Long orgId) throws Exception {
//        Pager<Employee> employeePager = employeeService.findByOrg(orgId, needChildren, pager);
    	Map<String, Object> param = new HashMap<>();
    	param.put("orgId", orgId);
		param.put("includeSub", needChildren);
        Pager<Employee> employeePager = employeeService.findByOrg(pager, param);
        return ResultDTO.getSuccess(employeePager);
    }

    /**
     * 根据机构查找员工(过滤掉没有账号的员工)
     * 目前对账人使用
     * pager必须传入分页的pagesize 如果不想分页需要把pagesize传的足够大
     * @param pager        分页对象
     * @param orgId        机构id
     * @param needChildren 是否查询子节点
     * @return
     * @throws Exception 
     */
    @RequestMapping("/findUsersByOrgId/{needChildren}/{orgId}")
    @ResponseBody
    public ResultDTO findUsersByOrgId(@RequestBody(required = false) Pager<Employee> pager, @PathVariable("needChildren") Boolean needChildren, @PathVariable("orgId") Long orgId) throws Exception {
    	Map<String, Object> param = new HashMap<>();
    	param.put("orgId", orgId);
		param.put("includeSub", needChildren);
        Pager<Employee> employeePager = employeeService.findByOrg(pager, param);
        employeePager.setResult(this.corvert(employeePager.getResult()));
        return ResultDTO.getSuccess(employeePager);
    }
    
    /**
     * 根据业务员找到登陆账号的userId,并把userId放入业务员的weChat字段中使用
     * @author jzc 2016年7月13日
     * @param employees
     * @return
     * @throws Exception
     */
    private List<Employee> corvert(List<Employee> employees) throws Exception{
    	List<Employee> employees2=new ArrayList<>(employees.size());
    	Employee newEmp=new Employee();
    	List<User> users=null;
    	for(Employee emp:employees){
    		newEmp.setEmployeeId(emp.getEmployeeId());
    		users=userService.findByEmployee(newEmp);
    		if(CollectionUtils.isNotEmpty(users)){
    			emp.setWeChat(users.get(0).getUserId().toString());
    			employees2.add(emp);
    		}
    	}
    	return employees2;
    }
    
    @ExceptionHandler(value={Exception.class})
    public void handlerException(Exception ex){
        System.out.println(ex);
    }
}
