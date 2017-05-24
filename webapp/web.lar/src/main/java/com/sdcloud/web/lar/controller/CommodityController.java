package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.CommodityType;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.service.CommodityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

import net.sf.ehcache.statistics.sampled.SampledCacheStatistics;

@RestController
@RequestMapping("/api/commodity")
public class CommodityController {

	@Autowired
	private CommodityService commodityService;
	@Autowired
	private DicService dicService;
	@Autowired
	private OrgService orgService;

	// 增加
	@RequestMapping(value = "/saveRecyclingMaterial", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) Commodity commodity) throws Exception {
		LarPager<Commodity> result = null;
		try {
			if (commodity != null && (commodity.getId() == null || commodity.getId().length() <= 0)) {
				boolean insertUserGetId = commodityService.insertSelective(commodity);
				if (insertUserGetId) {
					result = new LarPager<Commodity>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", commodity.getMechanismId());
					result.setParams(params);
					result = commodityService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "商品添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/saveRecyclingType", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) CommodityType commodityType) throws Exception {
		LarPager<CommodityType> result = null;
		try {
			if (commodityType != null && (commodityType.getId() == null || commodityType.getId().length() <= 0)) {
				boolean insertUserGetId = commodityService.insertSelective(commodityType);
				if (insertUserGetId) {
					result = new LarPager<CommodityType>();
					result = commodityService.selectTypeByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "类型添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 删除
	@RequestMapping(value = "/deleteRecyclingMaterial/{id}/{oId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id, @PathVariable(value = "oId") String oId)
			throws Exception {
		LarPager<Commodity> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = commodityService.deleteById(id);
				if (deleteById) {
					// 删除购物车里面的该商品
					commodityService.deleteShopCartByCommodity(id);

					result = new LarPager<Commodity>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", oId);
					result.setParams(params);
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

	// 根据类型ID 删除
	@RequestMapping(value = "/deleteRecyclingMaterialByType/{typeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO deleteByTypeId(@PathVariable(value = "typeId") String id) throws Exception {
		LarPager<Commodity> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
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
	public ResultDTO deleteTypeById(@PathVariable(value = "id") String id) throws Exception {
		LarPager<CommodityType> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
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
	public ResultDTO update(@RequestBody(required = false) Commodity commodity) throws Exception {
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
					return ResultDTO.getSuccess(result, "商品信息修改成功");
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

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}

	// 查询商品,带分页
	@RequestMapping("/getCommoditys")
	@ResponseBody
	public ResultDTO getCommoditys(@RequestBody(required = false) LarPager<Commodity> larPager) throws Exception {
		try {
			this.convertPrams(larPager);
			larPager = commodityService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<Commodity> larPager) throws Exception {
		if (null != larPager.getParams().get("includeSub")) {
			// 添加查询子机构功能
			boolean includeSub = (boolean) larPager.getParams().get("includeSub");
			if (includeSub) {
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

	// 查询类型列表，带分页
	@RequestMapping("/getRecyclingTypes")
	@ResponseBody
	public ResultDTO getRecyclingTypes(@RequestBody(required = false) LarPager<CommodityType> larPager)
			throws Exception {
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
	public ResultDTO getCommoditysForList(@PathVariable(value = "id") String id) throws Exception {
		try {

			List<Dic> dlist = dicService.findByPid(6033097168499235L, null);// 商品类型
			List<CommodityType> recyclingTypes = commodityService.getRecyclingTypes(id);// 机构ID目前为无效参数
			Map<String, String> rMap = new HashMap<String, String>();
			for (CommodityType type : recyclingTypes) {
				rMap.put(type.getId(), type.getId());
			}
			for (Dic dic : dlist) {
				if (rMap.get(dic.getDicId().toString()) == null) {// 不存在则新建
					CommodityType r = new CommodityType(dic.getDicId() + "", dic.getName(), 0, new Date(),
							dic.getSequence() == null ? 0 : dic.getSequence());
					commodityService.saveCommodityType(r);
				}
			}
			if (recyclingTypes != null && recyclingTypes.size() > 0) {
				return ResultDTO.getSuccess(recyclingTypes);
			} else {
				return ResultDTO.getFailure(500, "没有商品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据类型id查询物品列表,无分页
	@RequestMapping(value = "/getRecyclingNames/{id}")
	@ResponseBody
	public ResultDTO getRecyclingNames(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<Commodity> recyclingMaterials = commodityService.getRecyclingNames(id);
			if (recyclingMaterials != null && recyclingMaterials.size() > 0) {
				return ResultDTO.getSuccess(recyclingMaterials);
			} else {
				return ResultDTO.getFailure(500, "没有该类型的商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 获得所属机构所有商品的品牌
	@RequestMapping(value = "/getBrands/{id}/{includeSub}")
	@ResponseBody
	public ResultDTO getBrands(@PathVariable(value = "id") String id, @PathVariable Boolean includeSub)
			throws Exception {
		try {
			if (includeSub) {
				List<Long> orgIds = new ArrayList<>();
				List<Org> list = orgService.findById(Long.parseLong(id), true);
				for (Org org : list) {
					orgIds.add(org.getOrgId());
				}
				List<String> brands = commodityService.getBrandsByOrgs(orgIds);
				if (brands != null && brands.size() > 0) {
					return ResultDTO.getSuccess(brands);
				} else {
					return ResultDTO.getFailure(500, "没有品牌数据");
				}

			}

			List<String> brands = commodityService.getBrands(id);
			if (brands != null && brands.size() > 0) {
				return ResultDTO.getSuccess(brands);
			} else {
				return ResultDTO.getFailure(500, "没有品牌数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 获取所属机构的规格型号
	@RequestMapping(value = "/getSpecificationsByOrgId/{id}")
	@ResponseBody
	public ResultDTO getSpecificationsByOrgId(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<String> specifications = commodityService.getSpecificationsByOrgId(id);
			if (specifications != null && specifications.size() > 0) {
				return ResultDTO.getSuccess(specifications);
			} else {
				return ResultDTO.getFailure(500, "没有规格型号");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 获得所属机构所有商品的品牌
	@RequestMapping(value = "/getBrandsByName/{id}/{shopName}")
	@ResponseBody
	public ResultDTO getBrandsByName(@PathVariable(value = "id") String id,
			@PathVariable(value = "shopName") String shopName) throws Exception {
		try {
			List<String> brands = commodityService.getBrandsByName(id, shopName);
			if (brands != null && brands.size() > 0) {
				return ResultDTO.getSuccess(brands);
			} else {
				return ResultDTO.getFailure(500, "没有品牌数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据机构和品牌和商品获取对应的规格型号
	@RequestMapping(value = "/getSpecifications/{id}/{brand}/{shopName}")
	@ResponseBody
	public ResultDTO getSpecifications(@PathVariable(value = "id") String id,
			@PathVariable(value = "brand") String brand, @PathVariable(value = "shopName") String shopName)
			throws Exception {
		try {
			List<String> brands = commodityService.getSpecifications(id, brand, shopName);
			if (brands != null && brands.size() > 0) {
				return ResultDTO.getSuccess(brands);
			} else {
				return ResultDTO.getFailure(500, "没有规格型号");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 获得所属机构所有商品的名称
	@RequestMapping(value = "/getGoods/{id}/{includeSub}")
	@ResponseBody
	public ResultDTO getGoods(@PathVariable String id, @PathVariable Boolean includeSub) throws Exception {
		try {
			if (includeSub) {
				List<Long> orgIds = new ArrayList<>();
				List<Org> list = orgService.findById(Long.parseLong(id), true);
				for (Org org : list) {
					orgIds.add(org.getOrgId());
				}
				List<String> goods = commodityService.getGoodsByOrgs(orgIds);
				if (goods != null && goods.size() > 0) {
					return ResultDTO.getSuccess(goods);
				} else {
					return ResultDTO.getFailure(500, "没有商品数据");
				}

			}
			List<String> goods = commodityService.getGoods(id);
			if (goods != null && goods.size() > 0) {
				return ResultDTO.getSuccess(goods);
			} else {
				return ResultDTO.getFailure(500, "没有商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据机构和商品类型加载商品
	@RequestMapping(value = "/getGoodsByctId/{id}/{commodityType}")
	@ResponseBody
	public ResultDTO getGoods(@PathVariable(value = "id") String id,
			@PathVariable(value = "commodityType") String commodityType) throws Exception {
		try {
			List<String> goods = commodityService.getGoodsByctId(id, commodityType);
			if (goods != null && goods.size() > 0) {
				return ResultDTO.getSuccess(goods);
			} else {
				return ResultDTO.getFailure(500, "没有商品数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 商品上架
	@RequestMapping(value = "/updateShelfLife/{paymentMethod}/{unitPrice}/{moneyUnit}/{mechanismId}/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateShelfLife(@PathVariable(value = "paymentMethod") String paymentMethod,
			@PathVariable(value = "unitPrice") String unitPrice, @PathVariable(value = "moneyUnit") String moneyUnit,
			@PathVariable(value = "mechanismId") String mechanismId, @PathVariable(value = "id") String id)
			throws Exception {
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
			if (isNo) {
				return ResultDTO.getSuccess(result);
			} else {
				return ResultDTO.getFailure(500, "商品上架失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/updateDownShelfLife/{id}/{mechanismId}/{date}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateDownShelfLife(@PathVariable(value = "id") String id,
			@PathVariable(value = "mechanismId") String mechanismId, @PathVariable(value = "date") String date)
			throws Exception {
		LarPager<Commodity> result = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", id);
			Date date2 = new Date();
			date2.setTime(Long.valueOf(date));
			params.put("shelfTime", date2);
			boolean isNo = commodityService.updateDownShelfLife(params);
			result = new LarPager<Commodity>();
			params.clear();
			params.put("mechanismId", mechanismId);
			result.setParams(params);
			result = commodityService.selectByExample(result);
			if (isNo) {
				// 如果下架成功,删除购物车内该商品
				// commodityService.deleteShopCartByCommodity(id);
				return ResultDTO.getSuccess(result);
			} else {
				return ResultDTO.getFailure(500, "商品下架失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 导出全部
	 * 
	 * @author lrj 2016年8月1日
	 * @param response
	 */
	@RequestMapping("/export")
	public void export(HttpServletResponse response, @RequestBody(required = false) LarPager<Commodity> larPager) {
		String titleName = (String) larPager.getExtendMap().get("name");
		if (null == titleName || "".equals(titleName)) {
			titleName = "商城商品定义";
		}
		try {
			larPager.setPageSize(1000000);
			this.convertPrams(larPager);
			larPager = commodityService.selectByExample(larPager);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<Commodity> exportExcelUtils = new ExportExcelUtils<>("商城商品定义");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents(titleName, this.convert(larPager.getResult()));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				response.setDateHeader("Expires", 0);
				OutputStream outputStream = response.getOutputStream();
				workbook.write(outputStream);
				response.getOutputStream().flush();
				response.getOutputStream().close();
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

	private static Map<Integer, String> paymentMap;
	private static Map<Integer, String> moneyUnitMap;
	static {
		paymentMap = new HashMap<>();
		paymentMap.put(1, "积分支付");
		paymentMap.put(2, "货到付款");
		moneyUnitMap = new HashMap<>();
		moneyUnitMap.put(1, "分");
		moneyUnitMap.put(2, "元");
	}

	// 用于导出的转换
	private List<Commodity> convert(List<Commodity> result) throws Exception {
		if (null != result && result.size() > 0) {
			List<Long> orgIds = new ArrayList<>();
			for (Commodity commodity : result) {
				if (null != commodity.getCommodityType()) {
					commodity.setTypeId(commodity.getCommodityType().getId());
					commodity.setTypeName(commodity.getCommodityType().getTypeName());
				}
				// 支付方式
				if (commodity.getPaymentMethod() != 0) {
					commodity.setPaymentName(paymentMap.get(commodity.getPaymentMethod()));
				}
				// 计量单位
				if (commodity.getMoneyUnit() != 0) {
					commodity.setMoneyUnitName(moneyUnitMap.get(commodity.getMoneyUnit()));
				}
				orgIds.add(Long.parseLong(commodity.getMechanismId()));
			}
			
			Map<Long, Org> findOrgMapByIds = orgService.findOrgMapByIds(orgIds, false);
			
			for (Commodity commodity : result) {
				if(commodity.getMechanismId()!=null &&findOrgMapByIds.get(Long.parseLong(commodity.getMechanismId())) !=null )
					commodity.setOrgName(findOrgMapByIds.get(Long.parseLong(commodity.getMechanismId())).getName());
			}
		}
		return result;
	}
}