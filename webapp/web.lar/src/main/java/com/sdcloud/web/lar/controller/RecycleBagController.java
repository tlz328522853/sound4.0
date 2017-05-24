package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.AdCustomer;
import com.sdcloud.api.lar.entity.Contract;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecycleBag;
import com.sdcloud.api.lar.service.RecycleBagService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/recycleBag")
public class RecycleBagController {

	@Autowired
	private UserService userService;
	@Autowired
	private DicService dicService;
	@Autowired
	private OrgService orgService;
	
	@Autowired
	private RecycleBagService recycleBagService;
	
	// 查询列表
	@RequestMapping("/getRecycleBags")
	@ResponseBody
	public ResultDTO getRecycleBags(@RequestBody(required = false) LarPager<RecycleBag> larPager) throws Exception {
		try {
			this.convertPrams(larPager);//参数转换,判断是否包含子机构
			larPager = recycleBagService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<RecycleBag> larPager) throws Exception {
		if(null !=larPager.getParams().get("includeSub")){
			//添加查询子机构功能
			boolean includeSub = (boolean) larPager.getParams().get("includeSub");
			if(includeSub){
				String orgStr = larPager.getParams().get("org").toString();
				String[] orgArr = orgStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
				List<Long> orgIds = new ArrayList<>();
				for(String orgString:orgArr){
					Long mechanismId = Long.parseLong(orgString);
					List<Org> list = orgService.findById(mechanismId, true);
					for (Org org : list) {
						orgIds.add(org.getOrgId());
					}
				}		
				larPager.getParams().remove("org");
				larPager.getParams().put("orgIds", orgIds);
			}
			larPager.getParams().remove("includeSub");
		}
	}

	@RequestMapping(value = "/completeOrders")
	@ResponseBody
	public ResultDTO completeOrders(@RequestBody LarPager<RecycleBag> larPager,HttpServletRequest request) throws Exception {
		LarPager<RecycleBag> result = null;
		try {
			int pageSize = larPager.getPageSize();
			if(larPager!=null && larPager.getParams()!=null){
				Map<String, Object> rMap = larPager.getParams();
				List<String> checkIds = (List<String>) rMap.get("checks");
				String org = String.valueOf(rMap.get("org"));
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				boolean isNo=false;
				for (String id : checkIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderState", 1);
					updateParams.put("orderStateName", "已完成");
					updateParams.put("persionId", user.getUserId());
					updateParams.put("persionName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
					updateParams.put("completeTime",new Date());
					isNo = recycleBagService.completeOrders(updateParams);
				}
				if(isNo){
					result = new LarPager<RecycleBag>();
					result.setPageSize(pageSize);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("org", org);
					result.setParams(params);
					result = recycleBagService.selectByExample(result);
					return ResultDTO.getSuccess(result,"完成成功!");
				} else {
					return ResultDTO.getFailure(500, "完成失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/export")
    public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleBag> larPager) throws Exception {
        
        larPager.setPageSize(1000000);
        this.convertPrams(larPager);//参数转换,判断是否包含子机构
        larPager = recycleBagService.selectByExample(larPager);
        
        if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
            ExportExcelUtils<RecycleBag> exportExcelUtils = new ExportExcelUtils<>("回收袋");
            Workbook workbook = null;
            try {
            	List<RecycleBag> list = this.convert(this.convert(larPager.getResult()));
            	 workbook = exportExcelUtils.writeContents("回收袋", list);
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
	//转换
	private List<RecycleBag> convert(List<RecycleBag> list) throws Exception {
		
		List<Long> orgList = new ArrayList<>();
        for (RecycleBag bag : list) {
            if (null != bag.getOrg()) {
                orgList.add(Long.valueOf(bag.getOrg()));
            }
        }
        Map<Long, Org> orgs = null;
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        for (RecycleBag bag : list) {
            if(null != orgs && null != bag.getOrg()){
            	Org org = orgs.get(Long.valueOf(bag.getOrg()));
            	if(null != org){
            		bag.setOrgName(org.getName());
            	}
            }
            LarClientUserAddress address = bag.getLarClientUserAddressId();
            if(address !=null){
            	bag.setUserName(address.getUserName());
            	bag.setContact(address.getContact());
            	bag.setAddress(address.getAddress());
            	bag.setDetail(address.getDetail());
            }
        }
        return list;
    }
	/*// 查询列表
	@RequestMapping("/getTransactions")
	@ResponseBody
	public ResultDTO getTransactions(@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		try {
			larPager = orderManagerService.getTransactions(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	// 查询列表
	@RequestMapping("/getChildOrders")
	@ResponseBody
	public ResultDTO getChildOrders(@RequestBody(required = false) LarPager<ChildOrders> larPager)throws Exception {
		try {
			larPager = orderManagerService.selectCildByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	@RequestMapping(value = "/getRecyclingTypes")
	@ResponseBody
	public ResultDTO getRecyclingTypes() throws Exception {
		try {
			List<RecyclingType> recyclingTypes = orderManagerService.getRecyclingTypes();
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(recyclingTypes);
			}else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/getRecyclingNames/{id}")
	@ResponseBody
	public ResultDTO getRecyclingNames(@PathVariable(value="id") String id) throws Exception {
		try {
			List<RecyclingMaterial> recyclingMaterials = orderManagerService.getRecyclingNames(id);
			if(recyclingMaterials!=null && recyclingMaterials.size()>0){
				return ResultDTO.getSuccess(recyclingMaterials);
			}else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//接单
	@RequestMapping(value = "/comeOrder", method = RequestMethod.POST)
	public ResultDTO comeOrder(@RequestBody(required = false) OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 2);
				updateParams.put("userId", user.getUserId());
				updateParams.put("userName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
				updateParams.put("orderStatusName", "已接单");
				boolean cancelOrder = orderManagerService.comeOrderById(updateParams);
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "接单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//批量接单
	@RequestMapping(value = "/comeOrders", method = RequestMethod.POST)
	public ResultDTO comeOrders(@RequestBody(required = false) LarPager<OrderManager> larPager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null &&  larPager.getParams().get("checks") !=null) {
				List<String> orderIds =  (List<String>) larPager.getParams().get("checks");
				if(orderIds.size()<=0){
					return ResultDTO.getFailure(200, "接单失败！");
				}
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				boolean cancelOrder=false;
				for (String id : orderIds) {
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 2);
					updateParams.put("userId", user.getUserId());
					updateParams.put("userName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
					updateParams.put("orderStatusName", "已接单");
					cancelOrder = orderManagerService.comeOrderById(updateParams);
				}
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId",larPager.getParams().get("mechanismId"));
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "接单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//查询某一个片区的业务员
	@RequestMapping(value = "/getSalesmansByAreaId/{id}")
	@ResponseBody
	public ResultDTO getSalesmansByAreaId(@PathVariable(value="id") String id) throws Exception {
		try {
			List<Salesman> salesmans = orderManagerService.getSalesmansByAreaId(id);
			if(salesmans!=null && salesmans.size()>0){
				return ResultDTO.getSuccess(salesmans);
			}else {
				return ResultDTO.getFailure(500, "该片区没有业务员");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//派单
	@RequestMapping(value = "/dispatchOrder")
	@ResponseBody
	public ResultDTO dispatchOrder(@RequestBody OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if(orderManager!=null && orderManager.getSalesman()!=null){
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 3);
				updateParams.put("orderStatusName", "服务中");
				updateParams.put("sendSingleId", user.getUserId());
				updateParams.put("sendSingleName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
				updateParams.put("distributeIllustrate",orderManager.getDistributeIllustrate());
				updateParams.put("distributeDate",new Date());
				updateParams.put("salesman", orderManager.getSalesman().getId());
				boolean isNo = orderManagerService.dispatchOrder(updateParams);
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "派单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/dispatchOrders")
	@ResponseBody
	public ResultDTO dispatchOrders(@RequestBody LarPager<OrderManager> larPager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if(larPager!=null && larPager.getParams()!=null && larPager.getParams().containsKey("checks") && larPager.getParams().containsKey("salesmanId") && larPager.getParams().containsKey("mechanismId")){
				Map<String, Object> orderManagerMap = larPager.getParams();
				List<String> checkIds = (List<String>) orderManagerMap.get("checks");
				String distributeIllustrate = String.valueOf(orderManagerMap.get("distributeIllustrate"));
				String salesmanId = String.valueOf(orderManagerMap.get("salesmanId"));
				String mechanismId = String.valueOf(orderManagerMap.get("mechanismId"));
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				boolean isNo=false;
				for (String id : checkIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 3);
					updateParams.put("orderStatusName", "服务中");
					updateParams.put("sendSingleId", user.getUserId());
					updateParams.put("sendSingleName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
					updateParams.put("distributeIllustrate",distributeIllustrate);
					updateParams.put("distributeDate",new Date());
					updateParams.put("salesman", salesmanId);
					isNo = orderManagerService.dispatchOrder(updateParams);
				}
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "派单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//取消接单
	@RequestMapping(value = "/cancelDispatchOrder")
	@ResponseBody
	public ResultDTO cancelDispatchOrder(@RequestBody OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if(orderManager!=null){
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 1);
				updateParams.put("orderStatusName", "等待接单");
				updateParams.put("cancelTakePersonId", user.getUserId());
				updateParams.put("cancelTakePersonName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
				updateParams.put("cancelTakeIllustrate",orderManager.getCancelTakeIllustrate());
				updateParams.put("cancelTakeDate",new Date());
				boolean isNo = orderManagerService.cancelDispatchOrder(updateParams);
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/cancelDispatchOrders")
	@ResponseBody
	public ResultDTO cancelDispatchOrders(@RequestBody LarPager<OrderManager> larPager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if(larPager!=null && larPager.getParams()!=null){
				Map<String, Object> orderManagerMap = larPager.getParams();
				List<String> checkIds = (List<String>) orderManagerMap.get("checks");
				String cancelTakeIllustrate = String.valueOf(orderManagerMap.get("cancelTakeIllustrate"));
				String mechanismId = String.valueOf(orderManagerMap.get("mechanismId"));
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				boolean isNo=false;
				for (String id : checkIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 1);
					updateParams.put("orderStatusName", "等待接单");
					updateParams.put("cancelTakePersonId", user.getUserId());
					updateParams.put("cancelTakePersonName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
					updateParams.put("cancelTakeIllustrate",cancelTakeIllustrate);
					updateParams.put("cancelTakeDate",new Date());
					isNo = orderManagerService.cancelDispatchOrder(updateParams);
				}
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//查询评价列表
	// 查询列表
	@RequestMapping(value="/getEvaluates",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getEvaluates() throws Exception {
		List<Evaluate> evaluates = null;
		try {
			evaluates = orderManagerService.getEvaluates();
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(evaluates);
	}
	
	//查询某一个机构下所有片区的业务员
	@RequestMapping(value = "/getSalesmansBymechanismId/{id}")
	@ResponseBody
	public ResultDTO getSalesmansBymechanismId(@PathVariable(value="id") String id) throws Exception {
		try {
			List<Salesman> salesmans = orderManagerService.getSalesmansBymechanismId(id);
			if(salesmans!=null && salesmans.size()>0){
				return ResultDTO.getSuccess(salesmans);
			}else {
				return ResultDTO.getFailure(500, "该机构没有业务员");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//完成订单确认
	@RequestMapping(value = "/confirmOrder")
	@ResponseBody
	public ResultDTO confirmOrder(@RequestBody OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		//获得当前登录用户
		Object userId = request.getAttribute("token_userId");
		//查询用户
		User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("orderId", orderManager.getOrderId());
		updateParams.put("orderStatusId", 4);
		updateParams.put("orderStatusName", "已完成");
		updateParams.put("confirmPersionId", user.getUserId());
		updateParams.put("confirmPersionName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
		updateParams.put("completionType", orderManager.getCompletionType());
		updateParams.put("completionName", orderManager.getCompletionName());
		updateParams.put("completionIllustrate", orderManager.getCompletionIllustrate());
		Date finishDate = new Date();
		updateParams.put("finishDate",finishDate);
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		Date placeOrder = orderManager.getPlaceOrder();
		long time = finishDate.getTime()-placeOrder.getTime();
		long hour = time%nd/nh;//计算差多少小时
		updateParams.put("lengthService",hour);
		if(hour<1){
			updateParams.put("failureTypeId",1);
			updateParams.put("failureTypeName","【0~1】小时");
		}
        if(hour>=1 && hour<6){
			updateParams.put("failureTypeId",2);
			updateParams.put("failureTypeName","【1~6】小时");
		}
        if(hour>=6 && hour <12){
			updateParams.put("failureTypeId",3);
			updateParams.put("failureTypeName","【6~12】小时");
		}
        if(hour>=12 && hour<24){
			updateParams.put("failureTypeId",4);
			updateParams.put("failureTypeName","【12~24】小时");
		}
        if(hour>=24){
			updateParams.put("failureTypeId",5);
			updateParams.put("failureTypeName","24小时以上");
		}
		try {
			if(orderManager!=null){
				boolean isNo = orderManagerService.confirmOrder(updateParams);
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds",status);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//取消订单
	@RequestMapping(value = "/cancelOrder/{statusId}", method = RequestMethod.POST)
	public ResultDTO cancelOrder(@PathVariable(value="statusId") String statusId,@RequestBody(required = false) OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 0);
				updateParams.put("orderStatusName", "已取消");
				updateParams.put("cancelOrderPersonId", user.getUserId());
				updateParams.put("cancelOrderPersonName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
				updateParams.put("cancelOrderIllustrate",orderManager.getCancelOrderIllustrate());
				updateParams.put("cancelDate",new Date());
				boolean cancelOrder = orderManagerService.cancelOrderById(updateParams);
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					if(statusId.equals("1")){
						params.put("orderStatusId", 1);
					}else if(statusId.equals("2")){
						params.put("orderStatusId", 2);
					}else if(statusId.equals("3")){
						status.add(0);
						status.add(1);
						status.add(2);
						status.add(3);
						status.add(4);
						params.put("orderStatusIds",status);
					}
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/cancelOrders/{statusId}", method = RequestMethod.POST)
	public ResultDTO cancelOrders(@PathVariable(value="statusId") String statusId,@RequestBody(required = false) LarPager<OrderManager> larPager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("checks") !=null) {
				List<String> orderIds = (List<String>) larPager.getParams().get("checks");
				boolean cancelOrder = false;
				for (String orderId : orderIds) {
					//获得当前登录用户
					Object userId = request.getAttribute("token_userId");
					//查询用户
					User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", orderId);
					updateParams.put("orderStatusId", 0);
					updateParams.put("orderStatusName", "已取消");
					updateParams.put("cancelOrderPersonId", user.getUserId());
					updateParams.put("cancelOrderPersonName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
					updateParams.put("cancelOrderIllustrate",larPager.getParams().get("cancelOrderIllustrate"));
					updateParams.put("cancelDate",new Date());
					cancelOrder = orderManagerService.cancelOrderById(updateParams);
				}
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> status = new ArrayList<>();
					if(statusId.equals("1")){
						params.put("orderStatusId", 1);
					}else if(statusId.equals("2")){
						params.put("orderStatusId", 2);
					}else if(statusId.equals("3")){
						status.add(0);
						status.add(1);
						status.add(2);
						status.add(3);
						status.add(4);
						params.put("orderStatusIds",status);
					}
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//取消派单
	@RequestMapping(value = "/cancelSendOrder")
	@ResponseBody
	public ResultDTO cancelSendOrder(@RequestBody OrderManager orderManager,HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			//获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			//查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			Map<String, Object> updateParams = new HashMap<String, Object>();
			updateParams.put("id", orderManager.getId());
			updateParams.put("orderStatusId", 2);
			updateParams.put("orderStatusName", "已接单");
			updateParams.put("cancelSendPersonId", user.getUserId());
			updateParams.put("cancelSendPersonName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
			updateParams.put("cancelSendIllustrate", orderManager.getCancelSendIllustrate());
			updateParams.put("cancelSendDate",new Date());
			if(orderManager!=null){
				boolean isNo = orderManagerService.cancelSendOrder(updateParams);
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds",status);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "取消派单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 子单确认
	@RequestMapping(value = "/childOrderConfirm", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO childOrderConfirm(@RequestBody ChildOrders childOrders) throws Exception {
		try {
			if (childOrders != null && childOrders.getId()!=null) {
				boolean deleteById = orderManagerService.childOrderConfirm(childOrders);
				if(deleteById){
					return ResultDTO.getSuccess(200,"操作成功");
				} else {
					return ResultDTO.getFailure(500, "子单确认失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//查询对应机构的所有确认人
	@RequestMapping(value = "/getConfirmationPersonById/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getConfirmationPersonById(@PathVariable(value="id") String id) throws Exception {
		try {
			if (id != null && id.trim().length()>0) {
				List<OrderManager> orderManagers = orderManagerService.getConfirmationPersonById(id);
				if(orderManagers!=null && orderManagers.size()>0){
					return ResultDTO.getSuccess(orderManagers);
				} else {
					return ResultDTO.getFailure(500, "子单确认失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	*//**业务员接口,根据机构人员编号查询业务员，再更具业务员查询服务中的订单数量***//*
	@RequestMapping(value = "/getDispatchCount/{employeeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getDispatchCount(@PathVariable(value="employeeId") String employeeId) throws Exception {
		try {
			if(employeeId!=null && employeeId.trim().length()>0){
				int count = orderManagerService.queryOrderCount(employeeId);
				if (count>0) {
					return ResultDTO.getSuccess(count);
				} else {
					return ResultDTO.getFailure(500, "该业务员没有新的订单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	*//**等待服务列表的接口，根据业务员，查询服务中订单加子单号的接口,因为后台派单的话是有指派业务员的 *//*
	@RequestMapping("/getOrderByCustomerId/{customerId}")
	@ResponseBody
	public ResultDTO getOrderByCustomerId(@PathVariable(value="customerId") String customerId) throws Exception {
		try {
			if(customerId!=null && customerId.trim().length()>0){
				List<OrderManager> orderManagers = orderManagerService.getOrderByCustomerId(customerId);
				if (orderManagers!=null && orderManagers.size()>0) {
					return ResultDTO.getSuccess(orderManagers);
				} else {
					return ResultDTO.getFailure(500, "没有派单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	*//**服务历史，订单详情 *//*
	@RequestMapping("/getOrderDetails/{customerId}")
	@ResponseBody
	public ResultDTO getOrderDetails(@PathVariable(value="customerId") String customerId) throws Exception {
		try {
			if(customerId!=null && customerId.trim().length()>0){
				List<OrderManager> orderManagers = orderManagerService.getOrderDetails(customerId);
				//查询每个订单的机构
				for (OrderManager orderManager : orderManagers) {
					List<ChildOrders> childOrders = orderManager.getChildOrders();
					if(childOrders!=null && childOrders.size()>0){
						//把子弹状态为1的删掉
						for (int i = 0; i < childOrders.size(); i++) {
							if(childOrders.get(i).getConfirmOrder()==1){
								childOrders.remove(i);
							}
						}
					}
					//转换机构ID为机构名称
					AreaSetting areaSetting = orderManager.getAreaSetting();
					String mechanismId = areaSetting.getMechanismId();
					if(mechanismId==null){
						areaSetting.setMechanismId("没有机构");
					}else{
						List<Org> findById = orgService.findById(Long.valueOf(mechanismId), false);
						//根据机构ID查询机构名称
						if(findById==null || findById.size()<=0){
							areaSetting.setMechanismId("没有机构");
						}else{
							Org org = findById.get(0);
							if(org==null){
								areaSetting.setMechanismId("没有机构");
							}else{
								areaSetting.setMechanismId(org.getName());
							}
						}
					}
				}
				if (orderManagers!=null && orderManagers.size()>0) {
					return ResultDTO.getSuccess(orderManagers);
				} else {
					return ResultDTO.getFailure(500, "没有派单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@ExceptionHandler(value={Exception.class})
	public void handlerException(Exception ex){
		System.out.println(ex);
	}
	
	*//**业务员APP完成确认接口**//*
	//完成订单确认
	@RequestMapping(value = "/appConfirmOrder/{userId}")
	@ResponseBody
	public ResultDTO appConfirmOrder(@PathVariable(value="userId") String userId,@RequestBody OrderManager orderManager) throws Exception {
		LarPager<OrderManager> result = null;
		//查询用户
		if(userId==null || userId.length()<=0){
			return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
		}
		if(orderManager.getOrderId()==null || orderManager.getOrderId().length()<=0){
			return ResultDTO.getFailure(500, "不是有效的订单!");
		}
		if(orderManager.getChildOrders()==null || orderManager.getChildOrders().size()<=0){
			return ResultDTO.getFailure(500, "订单不能没有子单!");
		}
		User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("orderId", orderManager.getOrderId());
		updateParams.put("orderStatusId", 4);
		updateParams.put("orderStatusName", "已完成");
		//确认人
		updateParams.put("confirmPersionId", user.getUserId());
		//确认人姓名
		updateParams.put("confirmPersionName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
		// 完成类型Id
		updateParams.put("completionType", 1);
		//完成类型名称
		updateParams.put("completionName", "正常");
		//完成类型说明
		updateParams.put("completionIllustrate", orderManager.getCompletionIllustrate());
		//完成时间
		Date finishDate = new Date();
		updateParams.put("finishDate",finishDate);
		//支付类型,1:积分,2：现金
		updateParams.put("paymentTypeId",orderManager.getPaymentTypeId());
		updateParams.put("paymentTypeName",orderManager.getPaymentTypeName());
		//服务时长,查询下单时间
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		Date placeOrder = orderManager.getPlaceOrder();
		if(placeOrder==null){
			return ResultDTO.getFailure(500, "没有下单时间!");
		}
		long time = finishDate.getTime()-placeOrder.getTime();
		long hour = time%nd/nh;//计算差多少小时
		updateParams.put("lengthService",hour);
		if(hour<1){
			updateParams.put("failureTypeId",1);
			updateParams.put("failureTypeName","【0~1】小时");
		}
        if(hour>=1 && hour<6){
			updateParams.put("failureTypeId",2);
			updateParams.put("failureTypeName","【1~6】小时");
		}
        if(hour>=6 && hour <12){
			updateParams.put("failureTypeId",3);
			updateParams.put("failureTypeName","【6~12】小时");
		}
        if(hour>=12 && hour<24){
			updateParams.put("failureTypeId",4);
			updateParams.put("failureTypeName","【12~24】小时");
		}
        if(hour>=24){
			updateParams.put("failureTypeId",5);
			updateParams.put("failureTypeName","24小时以上");
		}
		if(orderManager.getMoney()!=null && orderManager.getMoney().length()>0){
			updateParams.put("money",orderManager.getMoney());
		}else if(orderManager.getIntegral()!=null && orderManager.getIntegral().length()>0){
			//先查询业务员的积分是否足够
			Salesman salesman = orderManager.getSalesman();
			int points=0;
			if(salesman!=null && salesman.getId()!=null){
				points = salesmanService.getSalesmanPoints(salesman.getId());
			}else{
				return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
			}
			if(points>=Integer.valueOf(orderManager.getIntegral())){
				updateParams.put("integral",orderManager.getIntegral());
			}else{
				return ResultDTO.getFailure(500, "积分不足，请充值!");
			}
		}else{
			return ResultDTO.getFailure(500, "请支付积分或现金!");
		}
		try {
			if(orderManager!=null){
				if(orderManager.getSalesman()==null || orderManager.getSalesman().getId()==null){
					return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
				}
				boolean isNo = orderManagerService.appConfirmOrder(updateParams,orderManager);
				if(isNo){
					return ResultDTO.getSuccess(200,"完成确认成功");
				} else {
					return ResultDTO.getFailure(500, "取消订单失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/
}