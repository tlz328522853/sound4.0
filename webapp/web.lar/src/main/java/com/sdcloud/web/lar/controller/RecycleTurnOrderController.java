package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecycleTurnOrder;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.RecycleTurnOrderService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;

/**
 * 回收转单记录
 * @author 唐
 *
 */
@RestController
@RequestMapping("/api/recycleTurnOrder")
public class RecycleTurnOrderController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 @Autowired
	 private RecycleTurnOrderService turnOrderService;
	 @Autowired
	 private UserService userService;
	 @Autowired
	 private OrgService orgService;
	 @Autowired
	 private EmployeeService employeeService;
	 @Autowired
	 private OrderManagerService orderManagerService;
	 
	 /**
	  * 查询所有
	  * @param larPager
	  * @return
	  */
	    @RequestMapping("/findAll")
	    @ResponseBody
	    public ResultDTO findAll(@RequestBody(required = false) LarPager<RecycleTurnOrder> larPager) {
	        try {
	        	
	        	this.convertPrams(larPager);
	        	larPager =turnOrderService.findAll(larPager);
	            return ResultDTO.getSuccess(larPager);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResultDTO.getFailure(500, "服务器错误！");
	        }
	    }
	    
	  //用于做参数的转变,是不包含子机构
		private void convertPrams(LarPager<RecycleTurnOrder> larPager) throws Exception {
			if(null !=larPager.getParams().get("includeSub")){
				//添加查询子机构功能
				boolean includeSub = (boolean) larPager.getParams().get("includeSub");
				if(includeSub){
					//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
					String orgStr = larPager.getParams().get("mechanismId").toString();
					String[] orgArr = orgStr.split("AAA");
					List<Long> orgIds = new ArrayList<>();
					for(String orgString:orgArr){
						Long mechanismId = Long.parseLong(orgString);
						List<Org> list = orgService.findById(mechanismId, true);
						for (Org org : list) {
							orgIds.add(org.getOrgId());
						}
					}
				
					larPager.getParams().remove("mechanismId");
					larPager.getParams().put("orgIds", orgIds);
				}
				larPager.getParams().remove("includeSub");
			}
		}
	    
	    
	    
	    
	    /**
	     * @param map orderStateNo  1 转单  
	     *            改变订单状态
	     * @param
	     * @return
	     */
	    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
	    public ResultDTO updateState(@RequestBody(required = false) Map<String, Object> map) {
	        try {
	            List<Long> list = new ArrayList<>();
	            Long userId = null;
	            Integer orderStateNo = null;
	            String next_sales_man=null;
	            String sales_man = null;
	            try {
	                userId = Long.parseLong(map.get("userId") + "");
	                map.remove("userId");
	                
	                orderStateNo = (Integer) map.get("orderStateNo");
	                
	                next_sales_man =  (String) map.get("next_sales_man");
	                
	                sales_man =(String) map.get("sales_man");
	                
	                String token = map.get("token") + "";
	                if (null != token) {
	                    map.remove("token");
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            Boolean flag = false;
	            if (null == userId || null == orderStateNo) {
	                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
	            }
	            //如果有下一个业务员id改变业务员id
	            
	            if(null !=next_sales_man){
//	            	next_sales_man = orderManagerService.getByCustomerId(next_sales_man);
	            	map.put("next_sales_man", next_sales_man);
	            	orderManagerService.updateSalesmanId(map);
	            }
	            //调用了物流的 模板 的转换参数模板
	            Map<String, Object> result = OrderUtils.appParamConvert(map, userId, list);
	            if (1 == orderStateNo) {
	                flag = true;
	                result.put("turn_order_user", sales_man);
	                result.put("turn_order_time", new Date());
	            } else {
	            	 flag = true;
		             result.put("turn_order_user", sales_man);
		             result.put("turn_order_time", new Date());
	            }
	            if (null != result) {
	                    return ResultDTO.getSuccess(AppCode.SUCCESS, "操作成功",turnOrderService.updateState(result, list, flag));
	                
	            } else {
	                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误");
	            }
	        } catch (
	                Exception e
	                )

	        {
	            e.printStackTrace();
	            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
	        }

	    }
	 @RequestMapping("/save")
	    public ResultDTO save(@RequestBody(required = false) RecycleTurnOrder recycleTurnOrder, HttpServletRequest request) {
	        try {
	            Object userId=request.getAttribute("token_userId");
	            if(null!=userId&&userId!=""){
	                User user=userService.findByUesrId(Long.valueOf(userId+""));
	                recycleTurnOrder.setCreateUser(user.getUserId());
	            }
	            return ResultDTO.getSuccess(200, turnOrderService.save(recycleTurnOrder));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResultDTO.getFailure(500, "服务器错误！");
	        }
	    }
	 /**
	  * 修改
	  * @param recycleTurnOrder
	  * @param request
	  * @return
	  */
	  @RequestMapping("/update")
	    public ResultDTO update(@RequestBody(required = false) RecycleTurnOrder recycleTurnOrder, HttpServletRequest request) {
	        try {
	            Object userId=request.getAttribute("token_userId");
	            if(null!=userId&&userId!=""){
	                User user=userService.findByUesrId(Long.valueOf(userId+""));
	                recycleTurnOrder.setUpdateUser(user.getUserId());
	            }
	            return ResultDTO.getSuccess(200, turnOrderService.update(recycleTurnOrder));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResultDTO.getFailure(500, "服务器错误！");
	        }
	    }
	  /**
	   * 删除
	   * @param id
	   * @return
	   */
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
	    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<RecycleTurnOrder> larPager) {
	        try {
	            Map<String, Object> map = larPager.getExtendMap();
	            List<Long> ids = new ArrayList<>();
	            if (map != null && null != map.get("orgId")) {
	            	String orgStr = map.get("orgId").toString();
					String[] orgArr = orgStr.split("AAA");
					
					 Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
					for(String orgString:orgArr){
						Long mechanismId = Long.parseLong(orgString);
						List<Org> list = orgService.findById(mechanismId, isParentNode);
						for (Org org : list) {
							ids.add(org.getOrgId());
						}
					}
	            }
	            LarPager<RecycleTurnOrder> pager = turnOrderService.findByOrgIds(larPager, ids);
	            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
	                pager.setResult(this.convert(pager.getResult()));
	            }
	            return ResultDTO.getSuccess(200, pager);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResultDTO.getFailure(500, "服务器错误！");
	        }
	    }

	    private List<RecycleTurnOrder> convert(List<RecycleTurnOrder> list) throws Exception {
	        List<Long> empList = new ArrayList<>();
	        Set<Long> empSet = new HashSet<>();
	        List<Long> orgList = new ArrayList<>();
	        List<Employee> employeeList = new ArrayList<>();
	        for (RecycleTurnOrder citySend : list) {
	            if (null != citySend.getTurnOrderUser()) {
	                empSet.add(citySend.getTurnOrderUser());
	            }
	            if (null != citySend.getOrg()) {
	                orgList.add(citySend.getOrg());
	            }
	        }
	        Map<Long, Org> orgs = new HashMap<>();
	        if (empSet.size() > 0) {
	            empList.addAll(empSet);
	            employeeList=employeeService.findById(empList);
	        }
	        if (orgList.size() > 0) {
	            orgs = orgService.findOrgMapByIds(orgList, false);
	        }
	        for (RecycleTurnOrder citySend : list) {

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
	            
	            if (!CollectionUtils.isEmpty(orgs)&&citySend.getOrg()!=null) {
	                Org org = orgs.get(citySend.getOrg());
	                if (null != org) {
	                    citySend.setOrgName(org.getName());
	                }
	            }
	        }
	        return list;
	    }

	    /**
	     * 导出
	     * @param response
	     * @param pager
	     */
	    @RequestMapping("/export")
	    public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleTurnOrder> pager) {
	    		Map<String, Object> map = pager.getExtendMap();
	    		List<Long> ids = new ArrayList<>();
			    try {
					if (map != null && null != map.get("orgId")) {
						String orgStr = map.get("orgId").toString();
						String[] orgArr = orgStr.split("AAA");
						
						 Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, isParentNode);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    	
	        pager.setPageSize(10000000);
	        LarPager<RecycleTurnOrder> orderTimeLarPager = turnOrderService.findByOrgIds(pager, ids);
	        if (null != orderTimeLarPager && null != orderTimeLarPager.getResult() && orderTimeLarPager.getResult().size() > 0) {
	            ExportExcelUtils<RecycleTurnOrder> exportExcelUtils = new ExportExcelUtils<>("转单记录");
	            Workbook workbook = null;
	            try {
	                workbook = exportExcelUtils.writeContents("转单记录", this.convert(orderTimeLarPager.getResult()));
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
