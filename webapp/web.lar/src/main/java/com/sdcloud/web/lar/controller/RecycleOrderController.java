package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.sdcloud.api.lar.entity.RecycleChildOrder;
import com.sdcloud.api.lar.entity.RecycleOrder;
import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.service.RecycleOrderService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.OrderUtils;

import io.jsonwebtoken.lang.Collections;

/**
 * 
 * @author luorongjie
 * 
 */
@RestController
@RequestMapping("/api/recycleOrder")
public class RecycleOrderController {

	@Autowired
	private RecycleOrderService recycleOrderService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmployeeService employeeService;

	// 业务员APP增加
	@RequestMapping(value = "/saveRecycleOrder", method = RequestMethod.POST)
	public ResultDTO save(@RequestBody(required = false) RecycleOrder recycleOrder, HttpServletRequest request) {
		try {
			if (recycleOrder != null) {
				if(recycleOrder.getIntegral()>0){//积分消费
					if(null == recycleOrder.getClientUser() || null ==recycleOrder.getClientUser().getId() ){
						return ResultDTO.getFailure(500, "参数有误!");
					}
				}
	
				recycleOrder.setId(UUIDUtil.getUUNum());
				recycleOrder.setOrderId(OrderUtils.generateNumber());

				boolean isSave = recycleOrderService.saveIncludChild(recycleOrder);
				if (isSave) {
					return ResultDTO.getSuccess(200, "订单添加成功！",null);
				} else {
					return ResultDTO.getFailure(500, "积分不足,请充值!");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "非法请求，请重新尝试！");
		}
	}

	@RequestMapping("/findByOrgIds")
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<RecycleOrder> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				String idStr = map.get("orgId").toString();
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != idStr) {
					// 是父节点再去查找
					if (isParentNode) { //mechanismId 如果有多个机构 ，使用"AAA"来隔开。
						String[] orgArr = idStr.split("AAA");
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", Long.valueOf(idStr));
						larPager.setParams(result);
						ids = null;
					}
				}
			}
			LarPager<RecycleOrder> pager = recycleOrderService.findByOrgIds(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	// 封装业务员与对账人姓名
	private List<RecycleOrder> convert(List<RecycleOrder> result) throws Exception {
		if (null != result && result.size() > 0) {
			List<Employee> emps = null;
			List<Long> empIds = new ArrayList<>();

			for (RecycleOrder order : result) {
				empIds.add(Long.parseLong(order.getSalesman().getPersonnelId()));
				empIds.add(order.getCheckMan());
			}
			emps = employeeService.findById(empIds);
			for (RecycleOrder order : result) {
				for (Employee emp : emps) {
					if (null != order.getSalesman().getPersonnelId()
							&& order.getSalesman().getPersonnelId().equals(emp.getEmployeeId() + "")) {
						order.setSalesmanEmployeeName(emp.getName());
					}
					if (null != order.getCheckMan() && order.getCheckMan().equals(emp.getEmployeeId())) {
						order.setCheckManName(emp.getName());
					}
				}
			}
		}
		return result;
	}

	// 保存或者更新对账信息
	@RequestMapping(value = "/checkOrder", method = RequestMethod.POST)
	public ResultDTO checkOrder(@RequestBody(required = false) RecycleOrder recycleOrder, HttpServletRequest request) {
		try {
			if (recycleOrder != null && recycleOrder.getId() != null) {
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				//如果对账人为空则是第一次保存对账,如果对账人是不为空,则是修改
				if(null == recycleOrder.getCheckMan()){
					recycleOrder.setCheckMan(user.getEmployee().getEmployeeId());
					recycleOrder.setCheckDate(new Date());
				}else {
					recycleOrder.setUpdateUser(user.getEmployee().getEmployeeId());
					recycleOrder.setUpdateDate(new Date());
				}
				
				Boolean checkOrder = recycleOrderService.checkOrder(recycleOrder);
				if (checkOrder)
					return ResultDTO.getSuccess(200, "操作成功！",null);
			}
			return ResultDTO.getFailure(500, "参数有误！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/export")
	public void export(@RequestBody(required = false) LarPager<RecycleOrder> larPager,HttpServletResponse response) {
		LarPager<RecycleOrder> pager = null;
		try {
			larPager.setPageSize(10000000);
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				String idStr = map.get("orgId").toString();
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != idStr) {
					// 是父节点再去查找
					if (isParentNode) {
						String[] orgArr = idStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", Long.valueOf(idStr));
						larPager.setParams(result);
						ids = null;
					}
				}
			}
			pager = recycleOrderService.findByOrgIds(larPager, ids);
			if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
				pager.setResult(this.convert(pager.getResult()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
			
			Workbook workbook = null;
			try {
				List<String> totalTitles = new ArrayList<>();
				totalTitles.add("收废品对账");
					totalTitles.add("收废品对账_子单");
				//父订单
				List<RecycleOrder> list = this.convert(pager.getResult());
				List<List<? extends Object>> contects = new ArrayList<>();
				contects.add(this.convertToExport(list));
				//子订单
				List<RecycleChildOrder> listChild = new ArrayList<>();
				listChild = this.getChild(this.convertToExport(list),listChild);
				contects.add(listChild);
				
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
	
	//根据父订单来获取所有子订单
	private List<RecycleChildOrder> getChild(List<RecycleOrder> list, List<RecycleChildOrder> listChild) {
		if(null !=list && list.size()>0){
			for(RecycleOrder order:list){
				if(null !=order.getChild() && order.getChild().size()>0){
					
					listChild.addAll(this.convertChild(order,order.getChild()));
				}
			}
		}
		return listChild;
	}

	//用于导出子单的转换
	private List<RecycleChildOrder> convertChild(RecycleOrder order, List<RecycleChildOrder> listChild) {
		int i=1;
		for(RecycleChildOrder childOrder:listChild){
			childOrder.setOrgName(order.getOrgName());
			childOrder.setChildNo(i++);
			childOrder.setAreaName(order.getAreaName());
			if(null !=childOrder.getMaterial()){
				if(null != childOrder.getMaterial().getRecyclingTypeId())
						childOrder.setTypeName(childOrder.getMaterial().getRecyclingTypeId().getTypeName());
				childOrder.setGoodsName(childOrder.getMaterial().getGoodsName());
				childOrder.setMeteringCompany(childOrder.getMaterial().getMeteringCompany());
			}
			childOrder.getOverproofName();
			
			//封装价格规格信息
			if(null != childOrder.getRecyclingSpec()){
				RecyclingSpec recyclingSpec = childOrder.getRecyclingSpec();
				childOrder.setSpecCompany(recyclingSpec.getSpecCompany());
				childOrder.setSpecName(recyclingSpec.getSpecName());
				childOrder.setPayablePrice(recyclingSpec.getPrice());
				if(null != recyclingSpec.getPrice())
					childOrder.setPayableTotalPrice(recyclingSpec.getPrice().multiply(childOrder.getNumber()));
				childOrder.setPaidTotalPrice(childOrder.getPaidTotalPrice());
				if(null != recyclingSpec.getPrice())
					childOrder.setPaidPrice(childOrder.getPaidTotalPrice().divide(childOrder.getNumber(),1,BigDecimal.ROUND_HALF_UP));
			}
		}
		return listChild;
	}

	//用于导出的转换方法
	private List<RecycleOrder> convertToExport(List<RecycleOrder> list) throws Exception {
		List<Long> orgIds = new ArrayList<>();
		if(null !=list && list.size()>0 ){
			for(RecycleOrder order :list){
				if(null != order.getClientUser()){
					order.setClientName(order.getClientUser().getName());
					order.setClientPhone(order.getClientUser().getPhone());
					order.setClientAddress(order.getClientUser().getAddress());
				}
				if(null !=order.getOrgId()){
					orgIds.add(order.getOrgId());
				}
				
			}
		}
		Map<Long, Org> orgMaps = orgService.findOrgMapByIds(orgIds,false);
		if(null !=list && list.size()>0 ){
			for(RecycleOrder order :list){
				if(null !=order.getOrgId() && null !=orgMaps.get(order.getOrgId())){
					order.setOrgName(orgMaps.get(order.getOrgId()).getName());
				}
			}
		}
		return list;
	}
	
	//查询回收物类型
	@RequestMapping(value = "/getRecyclingTypes")
	@ResponseBody
	public ResultDTO getRecyclingTypes() throws Exception {
		try {
			List<RecyclingType> recyclingTypes = recycleOrderService.getRecyclingTypes();
			if (recyclingTypes != null && recyclingTypes.size() > 0) {
				return ResultDTO.getSuccess(recyclingTypes);
			} else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	

	// 修改子单
	@RequestMapping(value = "/updateChildOrders", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) RecycleChildOrder childOrders) throws Exception {
		try {
			if (childOrders != null && childOrders.getId() != null) {
				boolean updateByExampleSelective = recycleOrderService.updateByExampleSelective(childOrders);
				if (updateByExampleSelective) {
					return ResultDTO.getSuccess(200,"修改成功!",null);
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
	
	//获取当前机构或者包含子机构已对过账订单的对账 人。
	@RequestMapping("/getCheckMens")
	public ResultDTO getCheckMens(@RequestBody(required = false) LarPager<RecycleOrder> larPager) {
		try {
			Map<String, Object> map = larPager.getParams();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				String idStr = map.get("orgId").toString();
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != idStr) {
					// 是父节点再去查找
					if (isParentNode) {
						String[] orgArr = idStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", Long.valueOf(idStr));
						larPager.setParams(result);
						ids = null;
					}
				}
			}
			List<Long> empIds = recycleOrderService.getCheckMens(larPager, ids);
			List<Employee> emps = new ArrayList<>();
			if(empIds !=null && empIds.size()>0)
				emps = employeeService.findById(empIds);
			return ResultDTO.getSuccess(200, emps);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
}
