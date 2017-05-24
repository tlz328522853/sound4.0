package com.sdcloud.web.lar.controller.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.Evaluate;
import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.MyPoints;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.service.IntegralConsumptionService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.util.DataValidUtl;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.FileOperationsUtils;

@RestController
@RequestMapping("/app/orderManager")
public class AppOrderManagerController {
	private static final Logger logger = LoggerFactory.getLogger(AppTokenInterceptor.class);
	@Autowired
	private OrderManagerService orderManagerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LarClientUserService larClientUserService;
	
	@Autowired
	private IntegralConsumptionService integralConsumptionService;
	
	// APP增加订单
	@RequestMapping(value = "/saveOrderManager", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) OrderManager orderManager) throws Exception {
		try {
			if (orderManager != null && (orderManager.getId()==null || orderManager.getId().length()<=0)) {
				boolean insertUserGetId = orderManagerService.insertSelective(orderManager);
				logger.info("schedue recycle save order ："+orderManager.getOrderId());
				if (insertUserGetId) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "预约成功！");
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "订单添加失败！");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	/**
	 * 通过base64格式上传图片字符串
	 * @author jzc 2016年11月4日
	 * @param maps key:base64
	 * @return
	 */
	@RequestMapping(value = "/saveImage64", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO saveImage64(@RequestBody Map<String, String> maps) {
		logger.info("image64:接收base64图片数据成功！");
		String webUrl="";
		String base64Image = maps.get("base64");
		try {
			InputStream input=DataValidUtl.decodeBase64(base64Image);
			logger.info("image64:解析base64图片数据成功！");
			webUrl = FileOperationsUtils.fileIsUpload(input,"123.png");
			logger.info("image64:图片上传到文件资源库成功!URL:"+webUrl);
			return  ResultDTO.getSuccess(AppCode.SUCCESS,"成功获取图片RUL",webUrl);
		} catch (IOException e) {
			logger.error("image64:base64 解析异常！",e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "base64解析错误！");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
		}
	}
	
	@RequestMapping(value = "/saveImage")
	@ResponseBody
	public ResultDTO saveImage(@RequestParam("file") MultipartFile file) {
		String webUrl = null;
		try {
			webUrl = FileOperationsUtils.fileUpload(file);
			return  ResultDTO.getSuccess(AppCode.SUCCESS,"成功获取图片RUL",webUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
		}
	}
	
	@RequestMapping(value = "/saveImages")
	@ResponseBody
	public ResultDTO saveImages(@RequestParam("files") List<MultipartFile> files) {
		List<String> webUrls = new ArrayList<>();
		try {
			for(MultipartFile file:files){
				webUrls.add(FileOperationsUtils.fileUpload(file));
			}
			return  ResultDTO.getSuccess(AppCode.SUCCESS,"成功获取图片RUL",webUrls);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
		}
	}
	
	
	// 订单列表
	@RequestMapping("/getOrderByCustomerId")
	@ResponseBody
	public ResultDTO getOrderByCustomerId(@RequestBody(required = false) LarPager<OrderManager> larPager)
			throws Exception {
		try {
			Map<String, Object> params = larPager.getParams();
			if(params != null && !params.isEmpty()){
				larPager = orderManagerService.selectOrderByCustomerId(larPager);
				return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取订单列表",larPager);
			}else{
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	// 增加
	/*@RequestMapping(value = "/saveOrderManager", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) OrderManager orderManager) throws Exception {
		try {
			if (orderManager != null && (orderManager.getId()==null || orderManager.getId().length()<=0)) {
				boolean insertUserGetId = orderManagerService.insertSelective(orderManager);
				if (insertUserGetId) {
					return ResultDTO.getSuccess("预约成功！");
				} else {
					return ResultDTO.getFailure(500, "订单添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/
	
	@RequestMapping(value = "/saveChildOrders", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) ChildOrders childOrders)
			throws Exception {
		LarPager<ChildOrders> result = null;
		try {
			if (childOrders != null && (childOrders.getId()==null || childOrders.getId().length()<=0)) {
				boolean insertUserGetId = orderManagerService.insertSelective(childOrders);
				if (insertUserGetId) {
					result = new LarPager<ChildOrders>();
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
	public ResultDTO delete(@RequestBody(required=false) OrderManager orderManager) throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId()!=null && orderManager.getLarClientUserAddress()!=null) {
				boolean deleteById = orderManagerService.deleteById(orderManager);
				if (deleteById) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(0);
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
	public ResultDTO delete(@PathVariable(value = "id") String id,@PathVariable(value="orderId") String orderId) throws Exception {
		LarPager<ChildOrders> result = null;
		try {
			if (id != null && id.trim().length() > 0 && orderId!=null && orderId.trim().length()>0) {
				boolean deleteById = orderManagerService.deleteByChildId(id.trim());
				if (deleteById) {
					result = new LarPager<ChildOrders>();
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
	public ResultDTO update(@RequestBody(required = false) OrderManager orderManager)
			throws Exception {
		LarPager<OrderManager> result = null;
		try {
			if (orderManager != null && orderManager.getId() != null && orderManager.getId().trim().length() > 0) {
				boolean updateByExampleSelective = orderManagerService.updateByExampleSelective(orderManager);
				if (updateByExampleSelective) {
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(0);
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
	public ResultDTO update(@RequestBody(required = false) ChildOrders childOrders) throws Exception {
		LarPager<ChildOrders> result = null;
		try {
			if (childOrders != null && childOrders.getId() != null && childOrders.getId().trim().length() > 0) {
				boolean updateByExampleSelective = orderManagerService.updateByExampleSelective(childOrders);
				if (updateByExampleSelective) {
					result = new LarPager<ChildOrders>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orderId", childOrders.getOrderManager().getId());
					params.put("confirmOrder", childOrders.getConfirmOrder());
					result.setParams(params);
					result = orderManagerService.selectCildByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "子单修改失败！");
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

	// 查询列表
	@RequestMapping("/getOrderManagers")
	@ResponseBody
	public ResultDTO getOrderManagers(@RequestBody(required = false) LarPager<OrderManager> larPager)
			throws Exception {
		try {
			larPager = orderManagerService.selectByExample(larPager);
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
					ids.add(0);
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
		updateParams.put("finishDate",new Date());
		try {
			if(orderManager!=null){
				boolean isNo = orderManagerService.confirmOrder(updateParams);
				if(isNo){
					result = new LarPager<OrderManager>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orderManager.getAreaSetting().getMechanismId());
					List<Integer> status = new ArrayList<>();
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
	@RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
	public ResultDTO cancelOrder(@RequestBody(required = false) OrderManager orderManager,HttpServletRequest request) throws Exception {
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
					if(orderManager.getOrderStatusId()==3 || orderManager.getOrderStatusId()==4){
						status.add(3);
						status.add(4);
						params.put("orderStatusIds",status);
					}else if(orderManager.getOrderStatusId()==1 || orderManager.getOrderStatusId()==0){
						status.add(0);
						status.add(1);
						params.put("orderStatusIds", status);
					}else if(orderManager.getOrderStatusId()==2){
						params.put("orderStatusId", 2);
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
	
	/**子单确认**/
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
	
	/**我的积分接口**/
	@RequestMapping(value = "/getMyPoints/{userId}")
	@ResponseBody
	public ResultDTO getMyPoints(@PathVariable(value="userId") String userId) throws Exception{
		try {
			if(userId!=null && userId.trim().length()>0){
				//查询用户积分
				LarClientUser larClientUser = larClientUserService.getMyPoints(userId);
				if(larClientUser==null || larClientUser.getId()==null){
					return ResultDTO.getFailure(500, "没有该用户的积分信息!");
				}
				//查询用户所有的回收完成订单
				List<MyPoints> orderManagers = orderManagerService.getOrderByUserId(larClientUser.getId());
				//查询用户商城兑换记录
				List<IntegralConsumption> integralConsumptions = integralConsumptionService.getExchangeInfo(userId);
				//过滤掉订单支付方式为2:现金的  & 过滤掉2:货到付款的
				convert(orderManagers,integralConsumptions);
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("larClientUser", larClientUser);
				params.put("orderManagers", orderManagers);
				params.put("integralConsumptions", integralConsumptions);
				return ResultDTO.getSuccess(200, "获取信息成功",params);
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void convert(List<MyPoints> orderManagers, List<IntegralConsumption> integrals) {
//		Iterator<MyPoints> iterator = orderManagers.iterator();
//		while(iterator.hasNext()){
//			 MyPoints next = iterator.next();
//			 //TODO whs设置为1,表示预约回收,  以后会改动
//			 next.setOrderSourceId(1);
//			 if(next.getPaymentTypeId() == 2){
//				 iterator.remove();
//			 }
//		}
		
		Iterator<IntegralConsumption> integral = integrals.iterator();
		while(integral.hasNext()){
			Commodity commodity = integral.next().getCommodity();
			if(commodity != null && commodity.getPaymentMethod() == 2){
				integral.remove();
			}
		}
	}
}