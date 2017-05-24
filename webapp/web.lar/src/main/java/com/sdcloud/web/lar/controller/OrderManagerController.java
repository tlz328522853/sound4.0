package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.Evaluate;
import com.sdcloud.api.lar.entity.OrderCheck;
import com.sdcloud.api.lar.entity.OrderCheckExport;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.PrescriptionExport;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.TransactionRecordExport;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.OrderCheckService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExcelUtils;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/orderManager")
public class OrderManagerController {

	@Autowired
	private OrderManagerService orderManagerService;

	@Autowired
	private UserService userService;
	@Autowired
	private SalesmanService salesmanService;

	@Autowired
	private OrgService orgService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private XingeAppUtils xingeAppUtils;

	// 增加
	@RequestMapping(value = "/saveOrderManager", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) OrderManager orderManager) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && (orderManager.getId() == null || orderManager.getId().length() <= 0)) {
				boolean insertUserGetId = orderManagerService.insertSelective(orderManager);
				if (insertUserGetId) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "订单添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/saveChildOrders", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) ChildOrders childOrders) throws Exception {
		LarPager<ChildOrders> result = null;
		try {
			if (childOrders != null && (childOrders.getId() == null || childOrders.getId().length() <= 0)) {
				boolean insertUserGetId = orderManagerService.insertSelective(childOrders);
				if (insertUserGetId) {
					result = new LarPager<ChildOrders>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orderId", childOrders.getOrderManager().getId());
					params.put("confirmOrder", 1);
					result.setParams(params);
					result = orderManagerService.selectCildByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "子单添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 删除
	@RequestMapping(value = "/deleteOrderManager", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO delete(@RequestBody(required = false) OrderManager orderManager) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null
					&& orderManager.getLarClientUserAddress() != null) {
				boolean deleteById = orderManagerService.deleteById(orderManager);
				if (deleteById) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "订单删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	

	// 删除子单
	@RequestMapping(value = "/deleteChildOrders/{id}/{orderId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id, @PathVariable(value = "orderId") String orderId)
			throws Exception {
		LarPager<ChildOrders> result = null;
		try {
			if (id != null && id.trim().length() > 0 && orderId != null && orderId.trim().length() > 0) {
				boolean deleteById = orderManagerService.deleteByChildId(id.trim());
				if (deleteById) {
					result = new LarPager<ChildOrders>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orderId", orderId);
					params.put("confirmOrder", 1);
					result.setParams(params);
					result = orderManagerService.selectCildByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "子单删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 修改
	@RequestMapping(value = "/updateOrderManager", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) OrderManager orderManager) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				boolean updateByExampleSelective = orderManagerService.updateByExampleSelective(orderManager);
				if (updateByExampleSelective) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "订单修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 修改子单
	@RequestMapping(value = "/updateChildOrders", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) ChildOrders childOrders) {
		LarPager<ChildOrders> result = null;
		try {
			if (childOrders != null && childOrders.getId() != null && childOrders.getId().trim().length() > 0) {
				boolean updateByExampleSelective = orderManagerService.updateByExampleSelective(childOrders);
				if (updateByExampleSelective) {
					result = new LarPager<ChildOrders>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orderId", childOrders.getOrderManager().getId());
					params.put("confirmOrder", childOrders.getConfirmOrder());
					result.setParams(params);
					result = orderManagerService.selectCildByExample(result);
					return ResultDTO.getSuccess(result,"修改成功!");
				} else {
					return ResultDTO.getFailure(500, "子单修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误!");
		}
	}
	
	// 修改子单
	@RequestMapping(value = "/updateChildOrders2", method = RequestMethod.POST)
	public ResultDTO updateChildOrders(@RequestBody(required = false) ChildOrders childOrders) {
		try {
			if (childOrders != null && childOrders.getId() != null && childOrders.getId().trim().length() > 0) {
				boolean updateByExampleSelective = orderManagerService.updateByExampleSelective(childOrders);
				if(updateByExampleSelective){
					return ResultDTO.getSuccess(null,"修改成功!");
				}
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误!");
		}
	}


	// 查询列表
	@RequestMapping("/getOrderManagers")
	@ResponseBody
	public ResultDTO getOrderManagers(@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		try {
			//TODO 业务修改,注释代码
			/*Map<String, String> configMap = sysConfigService.findMap();
			if (configMap != null) {
				Long time = Long.valueOf(configMap.get("orderOutTime"));
				orderManagerService.updateGrabState(time * 60);
			}*/
			
			this.convertPrams(larPager);
			larPager = orderManagerService.selectByExample(larPager);
			List<OrderManager> orders = larPager.getResult();
			this.convert(orders);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<OrderManager> larPager) throws Exception {
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

	private void convert(List<OrderManager> orders) throws Exception {
		if (null != orders && orders.size() > 0) {
			List<Employee> emps = null;
			List<Long> empIds = new ArrayList<>();

			for (OrderManager order : orders) {
				if (null != order.getOrderCheck() && null != order.getOrderCheck().getCheckMan()) {
					empIds.add(order.getOrderCheck().getCheckMan());
				}
				
				if(null != order.getGrabOrderMin())
					order.setGrabOrdrMinName(order.getGrabOrderMin()+"分钟");
			}

			if (null != empIds && empIds.size() > 0) {
				emps = employeeService.findById(empIds);

				for (OrderManager order : orders) {
					for (Employee emp : emps) {
						if (null != order.getOrderCheck() && null != order.getOrderCheck().getCheckMan()
								&& order.getOrderCheck().getCheckMan().equals(emp.getEmployeeId())) {
							order.getOrderCheck().setCheckManName(emp.getName());
						}
					}
					
				}
			}
		}
	}

	// 查询列表
	@RequestMapping("/getTransactions")
	@ResponseBody
	public ResultDTO getTransactions(@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		try {
			this.convertPrams(larPager);//增加是否包含了机构
			larPager = orderManagerService.getTransactions(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 查询列表
	@RequestMapping("/getChildOrders")
	@ResponseBody
	public ResultDTO getChildOrders(@RequestBody(required = false) LarPager<ChildOrders> larPager) throws Exception {
		try {
			larPager.setOrderBy("c.createDate");
			larPager.setOrder("desc");
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
			if (recyclingTypes != null && recyclingTypes.size() > 0) {
				return ResultDTO.getSuccess(recyclingTypes);
			} else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/getRecyclingNames/{id}")
	@ResponseBody
	public ResultDTO getRecyclingNames(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<RecyclingMaterial> recyclingMaterials = orderManagerService.getRecyclingNames(id);
			if (recyclingMaterials != null && recyclingMaterials.size() > 0) {
				return ResultDTO.getSuccess(recyclingMaterials);
			} else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 接单
	@RequestMapping(value = "/comeOrder", method = RequestMethod.POST)
	public ResultDTO comeOrder(@RequestBody(required = false) OrderManager orderManager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 2);
				updateParams.put("userId", user.getUserId());
				updateParams.put("userName",
						user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
				updateParams.put("orderStatusName", "已接单");
				boolean cancelOrder = orderManagerService.comeOrderById(updateParams);
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
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

	// 批量接单
	@RequestMapping(value = "/comeOrders", method = RequestMethod.POST)
	public ResultDTO comeOrders(@RequestBody(required = false) LarPager<OrderManager> larPager,
			HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("checks") != null) {
				List<String> orderIds = (List<String>) larPager.getParams().get("checks");
				if (orderIds.size() <= 0) {
					return ResultDTO.getFailure(200, "接单失败！");
				}
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				boolean cancelOrder = false;
				for (String id : orderIds) {
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 2);
					updateParams.put("userId", user.getUserId());
					updateParams.put("userName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("orderStatusName", "已接单");
					cancelOrder = orderManagerService.comeOrderById(updateParams);
				}
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(1);
					params.put("orderStatusIds", ids);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "接单成功!");
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

	// 查询某一个片区的业务员
	@RequestMapping(value = "/getSalesmansByAreaId/{id}")
	@ResponseBody
	public ResultDTO getSalesmansByAreaId(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<Salesman> salesmans = orderManagerService.getSalesmansByAreaId(id);
			if (salesmans != null && salesmans.size() > 0) {
				return ResultDTO.getSuccess(salesmans);
			} else {
				return ResultDTO.getFailure(500, "该片区没有业务员");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 派单
	@RequestMapping(value = "/dispatchOrder")
	@ResponseBody
	public ResultDTO dispatchOrder(@RequestBody OrderManager orderManager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getSalesman() != null) {
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 3);
				updateParams.put("orderStatusName", "服务中");
				updateParams.put("sendSingleId", user.getUserId());
				updateParams.put("sendSingleName",
						user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
				updateParams.put("distributeIllustrate", orderManager.getDistributeIllustrate());
				updateParams.put("distributeDate", new Date());
				updateParams.put("salesman", orderManager.getSalesman().getId());
				boolean isNo = orderManagerService.dispatchOrder(updateParams);
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					String personnelId = orderManager.getSalesman().getPersonnelId();

					XingeEntity xingeEntity = new XingeEntity();
					xingeEntity.setTitle("有新的订单");
					xingeEntity.setContent("有新的订单");
					xingeEntity.setAccount(personnelId);
					xingeAppUtils.pushSingleAccount(xingeEntity,2);
					xingeAppUtils.pushSingleAccountIOS(xingeEntity,4);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "派单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/dispatchOrders")
	@ResponseBody
	public ResultDTO dispatchOrders(@RequestBody LarPager<OrderManager> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null && larPager.getParams().containsKey("checks")
					&& larPager.getParams().containsKey("salesmanId")
					&& larPager.getParams().containsKey("mechanismId")) {
				Map<String, Object> orderManagerMap = larPager.getParams();
				List<String> checkIds = (List<String>) orderManagerMap.get("checks");
				String distributeIllustrate = null;
				if (null != orderManagerMap.get("distributeIllustrate")) {
					distributeIllustrate = String.valueOf(orderManagerMap.get("distributeIllustrate"));
				}
				String salesmanId = String.valueOf(orderManagerMap.get("salesmanId"));
				String mechanismId = String.valueOf(orderManagerMap.get("mechanismId"));
				String personnelId = String.valueOf(orderManagerMap.get("personnelId"));
				orderManagerMap.remove("personnelId");
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				//获得salesman的片区
				Salesman salesman = salesmanService.getSalesManById(salesmanId);
				
				boolean isNo = false;
				for (String id : checkIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 3);
					updateParams.put("orderStatusName", "服务中");
					updateParams.put("sendSingleId", user.getUserId());
					updateParams.put("sendSingleName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("distributeIllustrate", distributeIllustrate);
					updateParams.put("distributeDate", new Date());
					updateParams.put("salesman", salesmanId);
					updateParams.put("areaSettingId", salesman.getAreaSetting().getId());
					isNo = orderManagerService.dispatchOrder(updateParams);
				}
				if (isNo) {
					// result = new LarPager<OrderManager>();
					// Map<String, Object> params = new HashMap<String,
					// Object>();
					// params.put("mechanismId", mechanismId);
					// params.put("orderStatusId", 2);
					// result.setParams(params);
					// result = orderManagerService.selectByExample(result);
					// xingeAppUtils.pushSingleAccount(2, personnelId, "有新的订单",
					// "有新的订单");
					// xingeAppUtils.pushSingleAccountIOS(4, personnelId,
					// "有新的订单");
					// return ResultDTO.getSuccess(result);
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);

					XingeEntity xingeEntity = new XingeEntity();
					xingeEntity.setTitle("有新的订单");
					xingeEntity.setContent("有新的订单");
					xingeEntity.setAccount(personnelId);
					xingeAppUtils.pushSingleAccount(xingeEntity,2);
					xingeAppUtils.pushSingleAccountIOS(xingeEntity,4);
					return ResultDTO.getSuccess(result, "派单成功!");
				} else {
					return ResultDTO.getFailure(500, "派单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 取消接单
	@RequestMapping(value = "/cancelDispatchOrder")
	@ResponseBody
	public ResultDTO cancelDispatchOrder(@RequestBody OrderManager orderManager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null) {
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 1);
				updateParams.put("orderStatusName", "等待接单");
				updateParams.put("cancelTakePersonId", user.getUserId());
				updateParams.put("cancelTakePersonName",
						user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
				updateParams.put("cancelTakeIllustrate", orderManager.getCancelTakeIllustrate());
				updateParams.put("cancelTakeDate", new Date());
				boolean isNo = orderManagerService.cancelDispatchOrder(updateParams);
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消订单成功!");
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

	@RequestMapping(value = "/cancelDispatchOrders")
	@ResponseBody
	public ResultDTO cancelDispatchOrders(@RequestBody LarPager<OrderManager> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null) {
				Map<String, Object> orderManagerMap = larPager.getParams();
				List<String> checkIds = (List<String>) orderManagerMap.get("checks");
				String cancelTakeIllustrate = null;
				if(null !=orderManagerMap.get("cancelTakeIllustrate"))
					cancelTakeIllustrate = String.valueOf(orderManagerMap.get("cancelTakeIllustrate"));
				String mechanismId = String.valueOf(orderManagerMap.get("mechanismId"));
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				boolean isNo = false;
				for (String id : checkIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 1);
					updateParams.put("orderStatusName", "等待接单");
					updateParams.put("cancelTakePersonId", user.getUserId());
					updateParams.put("cancelTakePersonName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("cancelTakeIllustrate", cancelTakeIllustrate);
					updateParams.put("cancelTakeDate", new Date());
					isNo = orderManagerService.cancelDispatchOrder(updateParams);
				}
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					params.put("orderStatusId", 2);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消接单成功!");
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

	// 查询评价列表
	// 查询列表
	@RequestMapping(value = "/getEvaluates", method = RequestMethod.GET)
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

	// 查询某一个机构下所有片区的业务员
	@RequestMapping(value = "/getSalesmansBymechanismId/{id}")
	@ResponseBody
	public ResultDTO getSalesmansBymechanismId(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<Salesman> salesmans = orderManagerService.getSalesmansBymechanismId(id);
			if (salesmans != null && salesmans.size() > 0) {
				return ResultDTO.getSuccess(salesmans);
			} else {
				return ResultDTO.getFailure(500, "该机构没有业务员");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 查询对应机构的所有确认人，包含子机构
	@RequestMapping(value = "/getSalesmansBymechanismId2/{id}/{includeSub}")
	@ResponseBody
	public ResultDTO getSalesmansBymechanismId2(@PathVariable(value = "id") String id,@PathVariable Boolean includeSub) throws Exception {
		try {
			if(includeSub){
				List<Long> orgIds = new ArrayList<>();
				String[] split = id.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
				for(String str:split){
					List<Org> list = orgService.findById(Long.parseLong(str), true);
					for (Org org : list) {
						orgIds.add(org.getOrgId());
					}
				}
				if (id != null && id.trim().length() > 0) {
					List<Salesman> salesmans = orderManagerService.getSalesmansBymechanismId2(orgIds);
					if (salesmans != null && salesmans.size() > 0) {
						return ResultDTO.getSuccess(salesmans);
					} else {
						return ResultDTO.getFailure(500, "该机构没有业务员");
					}
				} else {
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
				
			}
			
			if (id != null && id.trim().length() > 0) {
				List<Salesman> salesmans = orderManagerService.getSalesmansBymechanismId(id);
				if (salesmans != null && salesmans.size() > 0) {
					return ResultDTO.getSuccess(salesmans);
				} else {
					return ResultDTO.getFailure(500, "该机构没有业务员");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 完成订单确认
	@RequestMapping(value = "/confirmOrder")
	@ResponseBody
	public ResultDTO confirmOrder(@RequestBody OrderManager orderManager, HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		// 获得当前登录用户
		Object userId = request.getAttribute("token_userId");
		// 查询用户
		User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("orderId", orderManager.getOrderId());
		updateParams.put("orderStatusId", 4);
		updateParams.put("orderStatusName", "已完成");
		updateParams.put("confirmPersionId", user.getUserId());
		updateParams.put("confirmPersionName",
				user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
		updateParams.put("completionType", orderManager.getCompletionType());
		updateParams.put("completionName", orderManager.getCompletionName());
		updateParams.put("completionIllustrate", orderManager.getCompletionIllustrate());
		Date finishDate = new Date();
		updateParams.put("finishDate", finishDate);
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		Date placeOrder = orderManager.getPlaceOrder();
		long time = finishDate.getTime() - placeOrder.getTime();
		long hour = time % nd / nh;// 计算差多少小时
		updateParams.put("lengthService", hour);
		if (hour < 1) {
			updateParams.put("failureTypeId", 1);
			updateParams.put("failureTypeName", "【0~1】小时");
		}
		if (hour >= 1 && hour < 6) {
			updateParams.put("failureTypeId", 2);
			updateParams.put("failureTypeName", "【1~6】小时");
		}
		if (hour >= 6 && hour < 12) {
			updateParams.put("failureTypeId", 3);
			updateParams.put("failureTypeName", "【6~12】小时");
		}
		if (hour >= 12 && hour < 24) {
			updateParams.put("failureTypeId", 4);
			updateParams.put("failureTypeName", "【12~24】小时");
		}
		if (hour >= 24) {
			updateParams.put("failureTypeId", 5);
			updateParams.put("failureTypeName", "24小时以上");
		}
		try {
			if (orderManager != null) {
				boolean isNo = orderManagerService.confirmOrder(updateParams);
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds", status);
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

	// 完成订单确认
	@RequestMapping(value = "/confirmOrders")
	@ResponseBody
	public ResultDTO confirmOrders(@RequestBody LarPager<OrderManager> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			// 查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("coChecks") != null) {
				List<Map<String, String>> orderIds = (List<Map<String, String>>) larPager.getParams().get("coChecks");
				boolean isNo = false;
				for (Map<String, String> orderMap : orderIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", orderMap.get("id"));
					updateParams.put("orderStatusId", 4);
					updateParams.put("orderStatusName", "已完成");
					updateParams.put("confirmPersionId", user.getUserId());
					updateParams.put("confirmPersionName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("completionType", larPager.getParams().get("completionType"));
					updateParams.put("completionName", larPager.getParams().get("completionName"));
					updateParams.put("completionIllustrate", larPager.getParams().get("completionIllustrate"));
					Date finishDate = new Date();
					updateParams.put("finishDate", finishDate);
					long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
					long nh = 1000 * 60 * 60;// 一小时的毫秒数
					long string = Long.valueOf(String.valueOf(orderMap.get("placeOrder")));
					Date placeOrder = new Date(string);
					long time = finishDate.getTime() - placeOrder.getTime();
					long hour = time % nd / nh;// 计算差多少小时
					updateParams.put("lengthService", hour);
					if (hour < 1) {
						updateParams.put("failureTypeId", 1);
						updateParams.put("failureTypeName", "【0~1】小时");
					}
					if (hour >= 1 && hour < 6) {
						updateParams.put("failureTypeId", 2);
						updateParams.put("failureTypeName", "【1~6】小时");
					}
					if (hour >= 6 && hour < 12) {
						updateParams.put("failureTypeId", 3);
						updateParams.put("failureTypeName", "【6~12】小时");
					}
					if (hour >= 12 && hour < 24) {
						updateParams.put("failureTypeId", 4);
						updateParams.put("failureTypeName", "【12~24】小时");
					}
					if (hour >= 24) {
						updateParams.put("failureTypeId", 5);
						updateParams.put("failureTypeName", "24小时以上");
					}
					isNo = orderManagerService.confirmOrders(updateParams);
				}
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds", status);
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

	// 取消订单
	@RequestMapping(value = "/cancelOrder/{statusId}", method = RequestMethod.POST)
	public ResultDTO cancelOrder(@PathVariable(value = "statusId") String statusId,
			@RequestBody(required = false) OrderManager orderManager, HttpServletRequest request) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("id", orderManager.getId());
				updateParams.put("orderStatusId", 0);
				updateParams.put("orderStatusName", "已取消");
				updateParams.put("cancelOrderPersonId", user.getUserId());
				updateParams.put("cancelOrderPersonName",
						user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
				updateParams.put("cancelOrderIllustrate", orderManager.getCancelOrderIllustrate());
				updateParams.put("cancelDate", new Date());
				boolean cancelOrder = orderManagerService.cancelOrderById(updateParams);
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					if (statusId.equals("1")) {
						params.put("orderStatusId", 1);
					} else if (statusId.equals("2")) {
						params.put("orderStatusId", 2);
					} else if (statusId.equals("3")) {
						status.add(0);
						status.add(1);
						status.add(2);
						status.add(3);
						status.add(4);
						params.put("orderStatusIds", status);
					}
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消接单成功!");
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
	public ResultDTO cancelOrders(@PathVariable(value = "statusId") String statusId,
			@RequestBody(required = false) LarPager<OrderManager> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("checks") != null) {
				List<String> orderIds = (List<String>) larPager.getParams().get("checks");
				boolean cancelOrder = false;
				for (String orderId : orderIds) {
					// 获得当前登录用户
					Object userId = request.getAttribute("token_userId");
					// 查询用户
					User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", orderId);
					updateParams.put("orderStatusId", 0);
					updateParams.put("orderStatusName", "已取消");
					updateParams.put("cancelOrderPersonId", user.getUserId());
					updateParams.put("cancelOrderPersonName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("cancelOrderIllustrate", larPager.getParams().get("cancelOrderIllustrate"));
					updateParams.put("cancelDate", new Date());
					cancelOrder = orderManagerService.cancelOrderById(updateParams);
				}
				if (cancelOrder) {
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> status = new ArrayList<>();
					if (statusId.equals("1")) {
						params.put("orderStatusId", 1);
					} else if (statusId.equals("2")) {
						params.put("orderStatusId", 2);
					} else if (statusId.equals("3")) {
						status.add(0);
						status.add(1);
						status.add(2);
						status.add(3);
						status.add(4);
						params.put("orderStatusIds", status);
					}
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消订单成功!");
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

	// 取消派单
	@RequestMapping(value = "/cancelSendOrder")
	@ResponseBody
	public ResultDTO cancelSendOrder(@RequestBody OrderManager orderManager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			// 查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			Map<String, Object> updateParams = new HashMap<String, Object>();
			updateParams.put("id", orderManager.getId());
			updateParams.put("orderStatusId", 2);
			updateParams.put("orderStatusName", "已接单");
			updateParams.put("cancelSendPersonId", user.getUserId());
			updateParams.put("cancelSendPersonName",
					user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
			updateParams.put("cancelSendIllustrate", orderManager.getCancelSendIllustrate());
			updateParams.put("cancelSendDate", new Date());
			if (orderManager != null) {
				boolean isNo = orderManagerService.cancelSendOrder(updateParams);
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(20);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds", status);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消派单成功!");
				} else {
					return ResultDTO.getFailure(500, "取消派单失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 批量取消派单
	@RequestMapping(value = "/cancelSendOrders")
	@ResponseBody
	public ResultDTO cancelSendOrders(@RequestBody LarPager<OrderManager> larPager, HttpServletRequest request)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			// 查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			if (larPager != null && larPager.getParams() != null && larPager.getParams().get("checks") != null) {
				List<String> orderIds = (List<String>) larPager.getParams().get("checks");
				boolean isNo = false;
				for (String id : orderIds) {
					Map<String, Object> updateParams = new HashMap<String, Object>();
					updateParams.put("id", id);
					updateParams.put("orderStatusId", 2);
					updateParams.put("orderStatusName", "已接单");
					updateParams.put("cancelSendPersonId", user.getUserId());
					updateParams.put("cancelSendPersonName",
							user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
					updateParams.put("cancelSendIllustrate", larPager.getParams().get("cancelSendIllustrate"));
					updateParams.put("cancelSendDate", new Date());
					isNo = orderManagerService.cancelSendOrder(updateParams);
				}
				if (isNo) {
					result = new LarPager<OrderManager>();
					result.setPageSize(larPager.getPageSize());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", larPager.getParams().get("mechanismId"));
					List<Integer> status = new ArrayList<>();
					status.add(0);
					status.add(1);
					status.add(2);
					status.add(3);
					status.add(4);
					params.put("orderStatusIds", status);
					result.setParams(params);
					result = orderManagerService.selectByExample(result);
					return ResultDTO.getSuccess(result, "取消派单成功!");
				} else {
					return ResultDTO.getFailure(500, "取消派单失败！");
				}
			} else {
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
			if (childOrders != null && childOrders.getId() != null) {
				boolean deleteById = orderManagerService.childOrderConfirm(childOrders);
				if (deleteById) {
					return ResultDTO.getSuccess(200, "操作成功");
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

	// 查询对应机构的所有确认人
	@RequestMapping(value = "/getConfirmationPersonById/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getConfirmationPersonById(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<OrderManager> orderManagers = orderManagerService.getConfirmationPersonById(id);
				if (orderManagers != null && orderManagers.size() > 0) {
					return ResultDTO.getSuccess(orderManagers, "操作成功!");
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

	// 查询对应机构的所有确认人，包含子机构
	@RequestMapping(value = "/getConfirmationPersonById2/{id}/{includeSub}")
	@ResponseBody
	public ResultDTO getConfirmationPersonById2(@PathVariable(value = "id") String id,@PathVariable Boolean includeSub) throws Exception {
		try {
			if(includeSub){
				List<Long> orgIds = new ArrayList<>();
				
				//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
				String[] orgArr = id.split("AAA");
				for(String orgString:orgArr){
					Long mechanismId = Long.parseLong(orgString);
					List<Org> list = orgService.findById(mechanismId, true);
					for (Org org : list) {
						orgIds.add(org.getOrgId());
					}
				}
				/*List<Org> list = orgService.findById(Long.parseLong(id), true);
				for (Org org : list) {
					orgIds.add(org.getOrgId());
				}
				*/
				if (id != null && id.trim().length() > 0) {
					List<OrderManager> orderManagers = orderManagerService.getConfirmationPersonById2(orgIds);
					if (orderManagers != null && orderManagers.size() > 0) {
						return ResultDTO.getSuccess(orderManagers, "操作成功!");
					} else {
						return ResultDTO.getFailure(500, "子单确认失败！");
					}
				} else {
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
				
			}
			
			if (id != null && id.trim().length() > 0) {
				List<OrderManager> orderManagers = orderManagerService.getConfirmationPersonById(id);
				if (orderManagers != null && orderManagers.size() > 0) {
					return ResultDTO.getSuccess(orderManagers, "操作成功!");
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
	
	/**
	 * 业务员接口,根据机构人员编号查询业务员，再更具业务员查询服务中的订单数量
	 ***/
	@RequestMapping(value = "/getDispatchCount/{employeeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getDispatchCount(@PathVariable(value = "employeeId") String employeeId) throws Exception {
		try {
			if (employeeId != null && employeeId.trim().length() > 0) {
				int count = orderManagerService.queryOrderCount(employeeId);
				if (count > 0) {
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

	/**
	 * 等待服务列表的接口，根据业务员，查询服务中订单加子单号的接口,因为后台派单的话是有指派业务员的
	 */
	@RequestMapping("/getOrderByCustomerId/{customerId}")
	@ResponseBody
	public ResultDTO getOrderByCustomerId(@PathVariable(value = "customerId") String customerId) throws Exception {
		try {
			//TODO 业务修改,注释代码
			/*Map<String, String> map = sysConfigService.findMap();
			Long orderOutTime = Long.parseLong(map.get("orderOutTime")) * 60;// 抢单时效
			orderManagerService.updateGrabState(orderOutTime);*/

			if (customerId != null && customerId.trim().length() > 0) {
				List<OrderManager> orderManagers = orderManagerService.getOrderByCustomerId(customerId);
				if (orderManagers != null && orderManagers.size() > 0) {
					return ResultDTO.getSuccess(orderManagers);
				} else {
					return ResultDTO.getFailure(500, "没有派单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 服务历史，订单详情
	 */
	@RequestMapping("/getOrderDetails/{customerId}")
	@ResponseBody
	public ResultDTO getOrderDetails(@PathVariable(value = "customerId") String customerId) throws Exception {
		try {
			if (customerId != null && customerId.trim().length() > 0) {
				List<OrderManager> orderManagers = orderManagerService.getOrderDetails(customerId);
				// 查询每个订单的机构
				if (null == orderManagers || orderManagers.size() == 0) {
					return ResultDTO.getFailure(500, "没有找到记录！");
				}
				for (OrderManager orderManager : orderManagers) {
					List<ChildOrders> childOrders = orderManager.getChildOrders();
					if (childOrders != null && childOrders.size() > 0) {
						// 把子弹状态为1的删掉
						for (int i = 0; i < childOrders.size(); i++) {
							if (childOrders.get(i).getConfirmOrder() == 1) {
								childOrders.remove(i);
								i--;
							}
						}
					}
					// 转换机构ID为机构名称
					AreaSetting areaSetting = orderManager.getAreaSetting();
					String mechanismId = areaSetting.getMechanismId();
					if (mechanismId == null) {
						areaSetting.setMechanismId("没有机构");
					} else {
						List<Org> findById = orgService.findById(Long.valueOf(mechanismId), false);
						// 根据机构ID查询机构名称
						if (findById == null || findById.size() <= 0) {
							areaSetting.setMechanismId("没有机构");
						} else {
							Org org = findById.get(0);
							if (org == null) {
								areaSetting.setMechanismId("没有机构");
							} else {
								areaSetting.setMechanismId(org.getName());
							}
						}
					}
				}
				if (orderManagers != null && orderManagers.size() > 0) {
					return ResultDTO.getSuccess(orderManagers);
				} else {
					return ResultDTO.getFailure(500, "没有派单！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}

	/**
	 * 业务员APP完成确认接口
	 **/
	// 完成订单确认
	@RequestMapping(value = "/appConfirmOrder/{userId}")
	@ResponseBody
	public ResultDTO appConfirmOrder(@PathVariable(value = "userId") String userId,
			@RequestBody OrderManager orderManager) throws Exception {
		
		// 查询用户
		if (userId == null || userId.length() <= 0) {
			return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
		}
		if (orderManager.getOrderId() == null || orderManager.getOrderId().length() <= 0) {
			return ResultDTO.getFailure(500, "不是有效的订单!");
		}
		if (orderManager.getChildOrders() == null || orderManager.getChildOrders().size() <= 0) {
			return ResultDTO.getFailure(500, "订单不能没有子单!");
		}
		User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("orderId", orderManager.getOrderId());
		updateParams.put("orderStatusId", 4);
		updateParams.put("orderStatusName", "已完成");
		// 确认人
		updateParams.put("confirmPersionId", user.getEmployee()==null?user.getUserId():user.getEmployee().getEmployeeId());
		// 确认人姓名
		updateParams.put("confirmPersionName",
				user.getEmployee() == null ? user.getName() : user.getEmployee().getName());
		// 完成类型Id
		updateParams.put("completionType", 1);
		// 完成类型名称
		updateParams.put("completionName", "正常");
		// 完成确认备注
		updateParams.put("completionIllustrate", orderManager.getCompletionIllustrate());
		// 完成时间
		Date finishDate = new Date();
		updateParams.put("finishDate", finishDate);
		
		// 支付类型,1:积分,2：现金，3混合
		long integral = 0;
		double money = 0;
		if(orderManager.getIntegral()!=null && !"".equals(orderManager.getIntegral()))
			integral = Long.parseLong(orderManager.getIntegral());
		if(orderManager.getMoney() != null && !"".equals(orderManager.getMoney()))
			money = orderManager.getMoney().doubleValue();
		
		if(integral >0 && money>0){
			orderManager.setPaymentTypeId(3);
			orderManager.setPaymentTypeName("混合");
		} else if (money > 0) {
			orderManager.setPaymentTypeId(2);
			orderManager.setPaymentTypeName("现金");
		} else if (integral > 0) {
			orderManager.setPaymentTypeId(1);
			orderManager.setPaymentTypeName("积分");
		} else {
			return ResultDTO.getFailure(500, "请输入金额或者积分 !");
		}
		
		updateParams.put("paymentTypeId", orderManager.getPaymentTypeId());
		updateParams.put("paymentTypeName", orderManager.getPaymentTypeName());
		// 服务时长,查询下单时间
		double nh = 1000 * 60.0;// 一小时的毫秒数
		
		Date placeOrder = orderManagerService.selectPlaceOrder(orderManager.getOrderId());
		if (placeOrder == null) {
			return ResultDTO.getFailure(500, "没有下单时间!");
		}
		long time = finishDate.getTime() - placeOrder.getTime();
		long minutes = Math.round(time / nh);
		updateParams.put("lengthService", minutes);
		if (minutes < 60) {
			updateParams.put("failureTypeId", 1);
			updateParams.put("failureTypeName", "【0~1】小时");
		}
		if (minutes >= 60 && minutes < 360) {
			updateParams.put("failureTypeId", 2);
			updateParams.put("failureTypeName", "【1~6】小时");
		}
		if (minutes >= 360 && minutes < 720) {
			updateParams.put("failureTypeId", 3);
			updateParams.put("failureTypeName", "【6~12】小时");
		}
		if (minutes >= 720 && minutes < 1440) {
			updateParams.put("failureTypeId", 4);
			updateParams.put("failureTypeName", "【12~24】小时");
		}
		if (minutes >= 1440) {
			updateParams.put("failureTypeId", 5);
			updateParams.put("failureTypeName", "24小时以上");
		}
		if (orderManager.getMoney() != null && orderManager.getMoney().doubleValue() > 0) {
			updateParams.put("money", orderManager.getMoney());
		} 
		if (orderManager.getPayableTotalPrice() != null && orderManager.getPayableTotalPrice().doubleValue() > 0) {
			updateParams.put("payableTotalPrice", orderManager.getPayableTotalPrice());
		} 
		if (orderManager.getIntegral() != null && orderManager.getIntegral().length() > 0) {
			updateParams.put("integral", orderManager.getIntegral());
			// 验证订单消费积分数据
			Salesman salesman = orderManager.getSalesman();
			int points = 0;
			if (salesman != null && salesman.getId() != null) {
				points = salesmanService.getSalesmanPoints(salesman.getId());
			} else {
				return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
			}
			if (orderManager.getIntegral().length() > 9) {
				return ResultDTO.getFailure(500, "积分长度超过9位!");
			}
			/*if (points >= Integer.valueOf(orderManager.getIntegral())) {
				updateParams.put("integral", orderManager.getIntegral());
			} else {
				return ResultDTO.getFailure(500, "积分不足，请充值!");
			}*/
		} 
		try {
			if (orderManager.getSalesman() == null || orderManager.getSalesman().getId() == null) {
				return ResultDTO.getFailure(500, "当前操作人员不是业务员!");
			}
			boolean isNo = orderManagerService.appConfirmOrder(updateParams, orderManager);
			if (isNo) {
				return ResultDTO.getSuccess(200, "完成确认成功");
			} else {
				return ResultDTO.getFailure(500, "取消订单失败！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}

	// 定单详情
	@RequestMapping(value = "/getOrderManagerByOrderId", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getOrderManagerByOrderId(@RequestBody(required = false) Map<String, Object> map) {
		try {
			String orderId = map.get("orderId") + "";

			if (orderId != null && orderId.trim().length() > 0) {
				OrderManager orderManager = orderManagerService.getOrderManagerByOrderId(orderId);
				if (null != orderManager) {

					return ResultDTO.getSuccess(200, "获取订单成功", orderManager);
				} else {
					return ResultDTO.getFailure(500, "获取订单详情失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Autowired
	private OrderCheckService orderCheckService;

	// 保存或者修改对账
	@RequestMapping(value = "/checkOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO checkOrder(@RequestBody(required = false) OrderManager orderManager, HttpServletRequest request) {
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			// 查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			OrderCheck orderCheck = orderManager.getOrderCheck();
			
			if (null == orderCheck.getOrderId() && orderManager.getCheckStatus() == 0) { // 保存
				//判断是否超标
				orderCheck.setOverproof(1);
				orderCheck.setOverproofName("否");
				for(ChildOrders child:orderManager.getChildOrders()){
					if(null !=child.getPaidTotalPrice()&&null !=child.getPayableTotalPrice() &&child.getPaidTotalPrice().compareTo(child.getPayableTotalPrice()) >0){
						orderCheck.setOverproof(2);
						orderCheck.setOverproofName("是");
						break;
					}
				}
				
				//更新订单是否对账状态
				orderManager.setCheckStatus(1);
				//int updateCheckStatus = orderManagerService.updateCheckStatus(orderManager);
				
				orderCheck.setCheckMan(user.getEmployee().getEmployeeId());
				orderCheck.setCheckDate(new Date());
				orderCheck.setCreateUser(user.getEmployee().getEmployeeId());
				orderCheck.setOrderId(orderManager.getOrderId());
				Boolean save = orderCheckService.save(orderCheck,orderManager);
				if (save) {
					return ResultDTO.getSuccess(200, "保存对账成功", null);
				}
					
			} else { // 更新
				orderCheck.setUpdateUser(user.getEmployee().getEmployeeId());
				Boolean update = orderCheckService.update(orderCheck);
				if (update)
					return ResultDTO.getSuccess(200, "修改对账成功", null);
			}
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "非法请求，请重新尝试！");
		}
	}

	@RequestMapping("/export")
	public void export(@RequestBody(required = false) LarPager<OrderManager> pager, HttpServletResponse response) {
		pager.setPageSize(1000000);
		try {
			this.convertPrams(pager);
			pager = orderManagerService.selectByExample(pager);
			List<OrderManager> orders = pager.getResult();
			this.convert(orders);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
			Workbook workbook = null;
			try {
				List<String> totalTitles = new ArrayList<>();
				totalTitles.add("订单查询");
				List<OrderManager> list = pager.getResult();
				//List<ChildOrders> reserveChilds = new ArrayList<>();
				List<ChildOrders> confirmChilds = new ArrayList<>();
				this.getChildsFromOrders(list, null, confirmChilds);
				List<List<? extends Object>> contects = new ArrayList<>();
				contects.add(this.convertExport(list));// 父订单

				/*if (null != reserveChilds && reserveChilds.size() > 0) {// 预约子订单
					contects.add(reserveChilds);
					totalTitles.add("预约子单");
				}*/
				if (null !=confirmChilds && confirmChilds.size() > 0) {// 确认子单
					contects.add(confirmChilds);
					totalTitles.add("确认子单");
				}
				ExcelUtils exportExcelUtils = new ExcelUtils(totalTitles);
				workbook = exportExcelUtils.writeContents(totalTitles, contects);
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
	
	//用于卖废品对账的导出
	@RequestMapping("/orderCheckExport")
	public void orderCheckExport(@RequestBody(required = false) LarPager<OrderManager> pager, HttpServletResponse response) {
		pager.setPageSize(1000000);
		try {
			this.convertPrams(pager);
			pager = orderManagerService.selectByExample(pager);
			List<OrderManager> orders = pager.getResult();
			this.convert(orders);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
			Workbook workbook = null;
			try {
				List<String> totalTitles = new ArrayList<>();
				totalTitles.add("卖废品对账");
				List<OrderManager> list = pager.getResult();
				//List<ChildOrders> reserveChilds = new ArrayList<>();
				List<ChildOrders> confirmChilds = new ArrayList<>();
				this.getChildsFromOrders(list, null, confirmChilds);
				List<List<? extends Object>> contects = new ArrayList<>();
				contects.add(this.convertCheckExport(list));// 父订单
				/*if (reserveChilds.size() > 0) {// 预约子订单
					contects.add(reserveChilds);
					totalTitles.add("预约子单");
				}*/

				if (confirmChilds.size() > 0) {// 确认子单
					contects.add(confirmChilds);
					totalTitles.add("确认子单");
				}

				ExcelUtils exportExcelUtils = new ExcelUtils(totalTitles);
				workbook = exportExcelUtils.writeContents(totalTitles, contects);
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
	
	//导出卖废品对账的转换
	private List<OrderCheckExport> convertCheckExport(List<OrderManager> list) {
		List<OrderCheckExport> checkList = new ArrayList<>();
		List<OrderManager> convertExport = this.convertExport(list);
		for(OrderManager order:convertExport){
			checkList.add(new OrderCheckExport(order));
		}
		return checkList;
	}

	// 导出时根据你订单,获得子订单
	private void getChildsFromOrders(List<OrderManager> list, List<ChildOrders> reserveChilds,
			List<ChildOrders> confirmChilds) {
		if (null != list && list.size() > 0) {
			for (OrderManager order : list) {
				if (null != order.getChildOrders() && order.getChildOrders().size() > 0) {
					int i = 1;
					int j = 1;
					for (ChildOrders childOrder : order.getChildOrders()) {
						// 确认的子单,0确认，1预约
						if (null != reserveChilds &&childOrder.getConfirmOrder() == 1) {
							childOrder.setChildNo(i++);
							reserveChilds.add(childOrder);
						}
						if (null != confirmChilds && childOrder.getConfirmOrder() == 0) {
							childOrder.setChildNo(j++);
							confirmChilds.add(childOrder);
						}
						convertChildExport(order, childOrder);
					}
				}
			}
		}
	}

	// 导出子单的导出转换
	private void convertChildExport(OrderManager order, ChildOrders childOrder) {
		if (null != order.getAreaSetting()) {
			childOrder.setOrgName(order.getAreaSetting().getMechanism());
			childOrder.setAreaName(order.getAreaSetting().getAreaName());
		}
		childOrder.setOrderId(order.getOrderId());
		if (null != childOrder.getRecyclingMaterial()) {
			if (null != childOrder.getRecyclingMaterial().getRecyclingTypeId())
				childOrder.setTypeName(childOrder.getRecyclingMaterial().getRecyclingTypeId().getTypeName());
			childOrder.setGoodsName(childOrder.getRecyclingMaterial().getGoodsName());
			childOrder.setMeteringCompany(childOrder.getRecyclingMaterial().getMeteringCompany());
		}
		//封装价格规格信息
		if(null != childOrder.getRecyclingSpec()){
			RecyclingSpec recyclingSpec = childOrder.getRecyclingSpec();
			childOrder.setSpecCompany(recyclingSpec.getSpecCompany());
			childOrder.setSpecName(recyclingSpec.getSpecName());
			childOrder.setPayablePrice(recyclingSpec.getPrice());
			if(null != recyclingSpec.getPrice())
				childOrder.setPayableTotalPrice(recyclingSpec.getPrice().multiply(childOrder.getNumber()));
			childOrder.setPaidTotalPrice(childOrder.getTotalPrice());
			if(null != recyclingSpec.getPrice())
				childOrder.setPaidPrice(childOrder.getTotalPrice().divide(childOrder.getNumber(),1,BigDecimal.ROUND_HALF_UP));
		}
		childOrder.getOverproofName();
		
	}

	private static Map<Integer, String> grabMap;
	static {
		// {10:'手动派单',21:'自动派单',22:'自动派单',31:'抢单',32:'抢单',33:'抢单'};
		grabMap = new HashMap<>();
		grabMap.put(10, "手动派单");
		grabMap.put(21, "自动派单");
		grabMap.put(22, "自动派单");
		grabMap.put(31, "抢单");
		grabMap.put(32, "抢单");
		grabMap.put(33, "抢单");
	}

	// 用于导出功能的转换
	private List<OrderManager> convertExport(List<OrderManager> list) {
		for (OrderManager order : list) {
			if (null != order.getAreaSetting()) {
				order.setOrgName(order.getAreaSetting().getMechanism());
				order.setAreaName(order.getAreaSetting().getAreaName());
			}
			if (null != order.getLarClientUserAddress()) {
				order.setReserveMan(order.getLarClientUserAddress().getUserName());
				order.setReservePhone(order.getLarClientUserAddress().getContact());
				order.setReserveAddress(order.getLarClientUserAddress().getAddress());
				order.setReserveDetail(order.getLarClientUserAddress().getDetail());
			}

			if (null != order.getSalesman()) {
				order.setSalesmanName(order.getSalesman().getManName());
			}

			if (order.getGrabOrder() != 0) {
				order.setGrabOrderName(grabMap.get(order.getGrabOrder()));
				if (order.getGrabOrderName().equals("抢单"))
					order.setGrabMan(order.getSalesmanName());
			}
			if (order.getCheckStatus() == 0) {
				order.setCheckStatusName("未对账");
			}
			if (order.getCheckStatus() == 1) {
				order.setCheckStatusName("已对账");
				if (null != order.getOrderCheck()) {
					order.setCheckManName(order.getOrderCheck().getCheckManName());
					order.setCheckDate(order.getOrderCheck().getCheckDate());
					order.setOverproofName(order.getOrderCheck().getOverproofName());
					order.setCheckRemark(order.getOrderCheck().getCheckRemark());
					order.setTroubleName(order.getOrderCheck().getTroubleName());
				}
			}
		}
		return list;
	}

	// 回收积分出账 _导出
	@RequestMapping("/transactionRecordExport")
	@ResponseBody
	public void transactionRecordExport(HttpServletResponse response,
			@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		try {
			larPager.setPageSize(1000000);
			this.convertPrams(larPager);//是否包含子机构
			larPager = orderManagerService.getTransactions(larPager);
		} catch (Exception e) {
			throw e;
		}
		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<TransactionRecordExport> exportExcelUtils = new ExportExcelUtils<>("回收积分出账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("回收积分出账",
						this.convertExportTransactionRecord(larPager.getResult()));
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

	// 用于积分出账 导出功能的转换
	private List<TransactionRecordExport> convertExportTransactionRecord(List<OrderManager> result) {
		List<OrderManager> exports = this.convertExport(result);
		List<TransactionRecordExport> transactionRecords = new ArrayList<>();
		for (OrderManager order : exports) {
			transactionRecords.add(new TransactionRecordExport(
					null != order.getSalesman() ? order.getSalesman().getManId() : null, order.getSalesmanName(),
					order.getOrgName(), order.getAreaName(), order.getPaymentTypeName(), order.getIntegral(),
					order.getMoney(), order.getConfirmPersionName(), order.getFinishDate(), order.getOrderId(),
					order.getAppUserId(), null != order.getLarClientUser() ? order.getLarClientUser().getName() : null,
					null != order.getLarClientUser() ? order.getLarClientUser().getPhone() : null,
					null != order.getLarClientUser() ? order.getLarClientUser().getAddress() : null,
					order.getIntegral(), order.getCompletionIllustrate()));
		}
		return transactionRecords;
	}
	
	
	// 时效跟踪的导出功能
	@RequestMapping("/prescriptionExport")
	@ResponseBody
	public void prescriptionExport(HttpServletResponse response,
			@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		try {
			larPager.setPageSize(1000000);
			this.convertPrams(larPager);//封闭是否包含子机构的参数
			larPager = orderManagerService.selectByExample(larPager);
			List<OrderManager> orders = larPager.getResult();
			this.convert(orders);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<PrescriptionExport> exportExcelUtils = new ExportExcelUtils<>("时效跟踪");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("时效跟踪",
						this.convertExportPrescription(larPager.getResult()));
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
	
	//this.convertExportTransactionRecord(larPager.getResult())
	//用于时效跟踪导出的转换功能
	private List<PrescriptionExport> convertExportPrescription(List<OrderManager> result) {
		List<PrescriptionExport> prescriptionExports = new ArrayList<>();
		List<OrderManager> convertExport = this.convertExport(result);
		for(OrderManager order:convertExport){
			prescriptionExports.add(new PrescriptionExport(order));
		}
		return prescriptionExports;
	}
	
	// 查询对账人列表 .
	@RequestMapping("/getCheckMens")
	@ResponseBody
	public ResultDTO getCheckMens(@RequestBody(required = false) LarPager<OrderManager> larPager) throws Exception {
		List<Employee> emps = null;
		try {
			this.convertPrams(larPager);
			List<Long> empIds = orderManagerService.getCheckMens(larPager);
			if(empIds !=null && empIds.size()>0)
				emps = employeeService.findById(empIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(emps);
	}

}