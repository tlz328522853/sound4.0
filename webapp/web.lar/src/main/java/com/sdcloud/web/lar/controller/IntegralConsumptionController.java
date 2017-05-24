package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.api.lar.entity.ReqParams;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.service.IntegralConsumptionService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/integralConsumption")
public class IntegralConsumptionController {

	@Autowired
	private IntegralConsumptionService integralConsumptionService;
	@Autowired
	private UserService userService;
	// 增加
	@RequestMapping(value = "/addIntegralConsumption", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) ReqParams reqParams,HttpServletRequest request)throws Exception {
		LarPager<IntegralConsumption> result = null;
		try {
			if (reqParams != null && (reqParams.getParamsMap()!=null && reqParams.getParamsMap().size()>0)) {
				Map<String, Object> paramsMap = reqParams.getParamsMap();
				//获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				//查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				paramsMap.put("operatorId", user.getUserId());
				paramsMap.put("operatorName", user.getEmployee()==null?user.getName():user.getEmployee().getName());
				boolean insertUserGetId = integralConsumptionService.addIntegralConsumption(paramsMap);
				if (insertUserGetId) {
					result = new LarPager<IntegralConsumption>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", paramsMap.get("mechanismId"));
					result.setParams(params);
					result = integralConsumptionService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "消费记录添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/getIntegralConsumptions")
	@ResponseBody
	public ResultDTO getIntegralConsumptions(@RequestBody(required = false) LarPager<IntegralConsumption> larPager) throws Exception {
		try {
			larPager = integralConsumptionService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	@RequestMapping(value = "/getOperators/{id}")
	@ResponseBody
	public ResultDTO getOperators(@PathVariable(value="id") String id) throws Exception {
		try {
			List<String> specifications = integralConsumptionService.getOperators(id);
			if(specifications!=null && specifications.size()>0){
				return ResultDTO.getSuccess(specifications);
			}else {
				return ResultDTO.getFailure(500, "没有操作人");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 删除
	@RequestMapping(value = "/deleteIntegralConsumption/{id}/{oId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") String id,@PathVariable(value="oId") String oId) throws Exception {
		LarPager<IntegralConsumption> result = null;
		try {
			if (id!=null &&  id.trim().length()>0) {
				boolean deleteById = integralConsumptionService.deleteById(id);
				if (deleteById) {
					result = new LarPager<IntegralConsumption>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", oId);
					result.setParams(params);
					result = integralConsumptionService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "记录删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	//导出  
	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<IntegralConsumption> pager) {
		try {
			pager.setPageSize(1000000);
			pager = integralConsumptionService.selectByExample(pager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   
	   // LarPager<IntegralConsumption> orderTimeLarPager = integralConsumptionService.findByOrgIds(pager, null);
	    if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
	        ExportExcelUtils<IntegralConsumption> exportExcelUtils = new ExportExcelUtils<>("积分消费");
	        Workbook workbook = null;
	        try {
	            workbook = exportExcelUtils.writeContents("积分消费", this.convert(pager.getResult()));
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
	 
	 @Autowired
	 private OrgService orgService;
	 
	private List<IntegralConsumption> convert(List<IntegralConsumption> result) throws Exception {
		List<Org> orgs = null;
		for(IntegralConsumption consumption :result){
			if(null !=consumption.getCommodity()){
				if(null !=consumption.getCommodity().getCommodityType()){
					consumption.setTypeName(consumption.getCommodity().getCommodityType().getTypeName());
				}
				consumption.setShopName(consumption.getCommodity().getShopName());
				consumption.setBrand(consumption.getCommodity().getBrand());
				consumption.setSpecifications(consumption.getCommodity().getSpecifications());
			}
			if(null !=consumption.getMechanismId() && !"".equals(consumption.getMechanismId())){
				orgs = orgService.findById(Long.parseLong(consumption.getMechanismId()), false);
				if(null != orgs && orgs.size()>0){
					consumption.setMechanismName(orgs.get(0).getName());
				}
				orgs = null;
			}
		}
		return result;
	}
	/*//根据类型ID 删除
	@RequestMapping(value = "/deleteRecyclingMaterialByType/{typeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO deleteByTypeId(@PathVariable(value="typeId") String id) throws Exception {
		LarPager<Commodity> result = null;
		try {
			if (id!=null &&  id.trim().length()>0) {
				boolean deleteById = commodityService.deleteByTypeId(id);
				if (deleteById) {
					result = new LarPager<Commodity>();
					result = commodityService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "商品删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 删除
	@RequestMapping(value = "/deleteRecyclingType/{id}", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO deleteTypeById(@PathVariable(value="id") String id) throws Exception {
		LarPager<CommodityType> result = null;
		try {
			if (id!=null &&  id.trim().length()>0) {
				boolean deleteById = commodityService.deleteTypeById(id);
				if (deleteById) {
					result = new LarPager<CommodityType>();
					result = commodityService.selectTypeByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "类型删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 修改
	@RequestMapping(value = "/updateRecyclingMaterial", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) Commodity commodity)
			throws Exception {
		LarPager<Commodity> result = null;
		try {
			if (commodity != null && commodity.getId() != null && commodity.getId().trim().length() > 0) {
				boolean updateByExampleSelective = commodityService.updateByExampleSelective(commodity);
				if (updateByExampleSelective) {
					result = new LarPager<Commodity>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", commodity.getMechanismId());
					result.setParams(params);
					result = commodityService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "商品修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 修改子单
	@RequestMapping(value = "/updateRecyclingType", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) CommodityType commodityType) throws Exception {
		LarPager<CommodityType> result = null;
		try {
			if (commodityType != null && commodityType.getId() != null && commodityType.getId().trim().length() > 0) {
				boolean updateByExampleSelective = commodityService.updateByExampleSelective(commodityType);
				if (updateByExampleSelective) {
					result = new LarPager<CommodityType>();
					result = commodityService.selectTypeByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "类型修改失败！");
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

	// 查询商品,带分页
	
	//查询类型列表，带分页
	@RequestMapping("/getRecyclingTypes")
	@ResponseBody
	public ResultDTO getRecyclingTypes(@RequestBody(required = false) LarPager<CommodityType> larPager)throws Exception {
		try {
			larPager = commodityService.selectTypeByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	// 查询类型列表，无分页
	@RequestMapping(value = "/getCommoditysForList/{id}")
	@ResponseBody
	public ResultDTO getCommoditysForList(@PathVariable(value="id") String id) throws Exception {
		try {
			List<CommodityType> recyclingTypes = commodityService.getRecyclingTypes(id);
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(recyclingTypes);
			}else {
				return ResultDTO.getFailure(500, "没有商品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据类型id查询物品列表,无分页
	@RequestMapping(value = "/getRecyclingNames/{id}")
	@ResponseBody
	public ResultDTO getRecyclingNames(@PathVariable(value="id") String id) throws Exception {
		try {
			List<Commodity> recyclingMaterials = commodityService.getRecyclingNames(id);
			if(recyclingMaterials!=null && recyclingMaterials.size()>0){
				return ResultDTO.getSuccess(recyclingMaterials);
			}else {
				return ResultDTO.getFailure(500, "没有该类型的商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//获得所属机构所有商品的品牌
	@RequestMapping(value = "/getBrands/{id}")
	@ResponseBody
	public ResultDTO getBrands(@PathVariable(value="id") String id) throws Exception {
		try {
			List<String> brands = commodityService.getBrands(id);
			if(brands!=null && brands.size()>0){
				return ResultDTO.getSuccess(brands);
			}else {
				return ResultDTO.getFailure(500, "没有品牌数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//获得所属机构所有商品的品牌
	@RequestMapping(value = "/getBrandsByName/{id}/{shopName}")
	@ResponseBody
	public ResultDTO getBrandsByName(@PathVariable(value="id") String id,@PathVariable(value="shopName") String shopName) throws Exception {
		try {
			List<String> brands = commodityService.getBrandsByName(id,shopName);
			if(brands!=null && brands.size()>0){
				return ResultDTO.getSuccess(brands);
			}else {
				return ResultDTO.getFailure(500, "没有品牌数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构和品牌和商品获取对应的规格型号
	@RequestMapping(value = "/getSpecifications/{id}/{brand}/{shopName}")
	@ResponseBody
	public ResultDTO getSpecifications(@PathVariable(value="id") String id,
			@PathVariable(value="brand") String brand,@PathVariable(value="shopName") String shopName) throws Exception {
		try {
			List<String> brands = commodityService.getSpecifications(id,brand,shopName);
			if(brands!=null && brands.size()>0){
				return ResultDTO.getSuccess(brands);
			}else {
				return ResultDTO.getFailure(500, "没有品牌数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
		
	//获得所属机构所有商品的名称
	@RequestMapping(value = "/getGoods/{id}")
	@ResponseBody
	public ResultDTO getGoods(@PathVariable(value="id") String id) throws Exception {
		try {
			List<String> goods = commodityService.getGoods(id);
			if(goods!=null && goods.size()>0){
				return ResultDTO.getSuccess(goods);
			}else {
				return ResultDTO.getFailure(500, "没有商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构和商品类型加载商品
	@RequestMapping(value = "/getGoodsByctId/{id}/{commodityType}")
	@ResponseBody
	public ResultDTO getGoods(@PathVariable(value="id") String id,@PathVariable(value="commodityType") String commodityType) throws Exception {
		try {
			List<String> goods = commodityService.getGoodsByctId(id,commodityType);
			if(goods!=null && goods.size()>0){
				return ResultDTO.getSuccess(goods);
			}else {
				return ResultDTO.getFailure(500, "没有商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//商品上架
	@RequestMapping(value = "/updateShelfLife/{paymentMethod}/{unitPrice}/{moneyUnit}/{mechanismId}/{id}",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateShelfLife(@PathVariable(value="paymentMethod") String paymentMethod,
			@PathVariable(value="unitPrice") String unitPrice,
			@PathVariable(value="moneyUnit") String moneyUnit,
			@PathVariable(value="mechanismId") String mechanismId,
			@PathVariable(value="id") String id) throws Exception {
		LarPager<Commodity> result = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("paymentMethod", paymentMethod);
			params.put("unitPrice", unitPrice);
			params.put("moneyUnit", moneyUnit);
			params.put("shelfLife", new Date());
			params.put("id", id);
			boolean isNo = commodityService.updateShelfLife(params);
			result = new LarPager<Commodity>();
			params.clear();
			params.put("mechanismId", mechanismId);
			result.setParams(params);
			result = commodityService.selectByExample(result);
			if(isNo){
				return ResultDTO.getSuccess(result);
			}else {
				return ResultDTO.getFailure(500, "启用状态修改失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/updateDownShelfLife/{id}/{mechanismId}",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateDownShelfLife(@PathVariable(value="id") String id,
			@PathVariable(value="mechanismId") String mechanismId) throws Exception {
		LarPager<Commodity> result = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", id);
			params.put("shelfTime", new Date());
			boolean isNo = commodityService.updateDownShelfLife(params);
			result = new LarPager<Commodity>();
			params.clear();
			params.put("mechanismId", mechanismId);
			result.setParams(params);
			result = commodityService.selectByExample(result);
			if(isNo){
				return ResultDTO.getSuccess(result);
			}else {
				return ResultDTO.getFailure(500, "启用状态修改失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//更改APP启用状态
	@RequestMapping(value = "/updateStartUsingForApp/{id}/{status}",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateStartUsingForApp(@PathVariable(value="id") String id,@PathVariable(value="status") String status) throws Exception {
		try {
			boolean isNo = commodityService.updateStartUsingForApp(id,status);
			if(isNo){
				return ResultDTO.getSuccess("APP启用状态修改成功");
			}else {
				return ResultDTO.getFailure(500, "APP启用状态修改失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/

}