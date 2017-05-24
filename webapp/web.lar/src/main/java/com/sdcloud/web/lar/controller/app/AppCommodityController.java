package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.CommodityType;
import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.CommodityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/commodity")
public class AppCommodityController {

	@Autowired
	private CommodityService commodityService;
	
	@Autowired
	private CityService cityService;

	@ExceptionHandler(value={Exception.class})
	public void handlerException(Exception ex){
		System.out.println(ex);
	}

	//APP端查询商品,带分页
	@RequestMapping("/getCommoditys/{cityId}")
	@ResponseBody
	public ResultDTO getCommoditys(@PathVariable(value="cityId") String cityId,@RequestBody(required = false) LarPager<Commodity> larPager) throws Exception {
		//存放数据的map
    	Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(cityId==null || cityId.trim().length()<=0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"请选择所在城市!");
			}
			//根据服务城市ID查询机构ID
			City city = cityService.selectByCityId(Long.valueOf(cityId));
        	if(city==null || city.getId()==null){
        		return ResultDTO.getSuccess(AppCode.SUCCESS,"请选择所在城市!");
        	}
        	//把机构ID放入larPager中
        	Map<String, Object> params = new HashMap<String, Object>();
        	//params.put("app", "app");
        	params.put("mechanismId", city.getOrg());
        	//查询积分的商品
        	params.put("moneyUnit", 1);
        	larPager.setParams(params);
			larPager = commodityService.selectByExample(larPager);
			result.put("pointsList", larPager.getResult());
			//查询花钱的商品
			params.put("moneyUnit", 2);
        	larPager.setParams(params);
        	larPager.setResult(new ArrayList<Commodity>());
			larPager = commodityService.selectByExample(larPager);
			result.put("moneysList", larPager.getResult());
			/*if(result.get("pointsList")==null && result.get("moneysList")==null){
				return ResultDTO.getSuccess(400,"该城市还没消费商品信息!");
			}*/
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(result);
	}
	
	//根据ID查询商品
	@RequestMapping("/getCommoditysById/{id}")
	@ResponseBody
	public ResultDTO getCommoditysById(@PathVariable(value="id") String id) throws Exception {
		try {
			if(id==null || id.trim().length()<=0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"请选择要查看的商品!");
			}
        	//把机构ID放入larPager中
        	Map<String, Object> params = new HashMap<String, Object>();
        	//查询积分的商品
        	params.put("id", id);
			Commodity commodity = commodityService.getCommoditysById(params);
			return ResultDTO.getSuccess(AppCode.SUCCESS,"商品返回成功!",commodity);
		} catch (Exception e) {
			throw e;
		}
	}
	
	//保存购物车物品的接口
	@RequestMapping(value="/saveShoppingCart")
	@ResponseBody
	public ResultDTO saveShoppingCart(@RequestBody(required=false) List<ShoppingCart> shoppingCarts){
		try {
			if(shoppingCarts !=null && shoppingCarts.size() > 0){
				boolean save = commodityService.saveShoppingCart(shoppingCarts);
				if(save){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"购物车添加成功");
				}else{
					return ResultDTO.getSuccess(AppCode.BIZ_DATA, "购物车添加失败");
				}
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "购物车添加失败",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//根据用户id查询购物车列表
	@RequestMapping(value={"/getShoppingCarts/{userId}"},method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getShoppingCarts(@PathVariable(value="userId") String userId){
		try {
			if(userId!=null && userId.trim().length()>0){
				List<ShoppingCart> shoppingCarts = commodityService.getShoppingCarts(userId);
				return ResultDTO.getSuccess(AppCode.SUCCESS,"购物车有数据",shoppingCarts);
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//根据用户id查询购物车列表
		@RequestMapping(value={"/getShoppingCartsByCityId/{userId}/{cityId}"},method=RequestMethod.GET)
		@ResponseBody
		public ResultDTO getShoppingCartsByCityId(@PathVariable(value="userId") String userId, @PathVariable(value="cityId") String cityId){
			try {
				if(userId!=null && userId.trim().length()>0){
					List<ShoppingCart> shoppingCarts = commodityService.getShoppingCartsByCityId(userId, cityId);
					return ResultDTO.getSuccess(AppCode.SUCCESS,"购物车有数据",shoppingCarts);
				}else{
					return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
			}
		}
	
	//根据用户id查询购物车列表
	@RequestMapping(value="/getShoppingCarts/",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getShoppingCarts1(){
		try {
			return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在",null);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//清空购物车,根据id批量清空
	@RequestMapping(value="deleteShoppingCartsById/{ids}",method=RequestMethod.GET)
	public ResultDTO deleteShoppingCartsById(@PathVariable(value="ids") List<String> ids){
		try {
			if(ids!=null && ids.size()>0){
				boolean delete = commodityService.deleteShoppingCartsById(ids);
				if(delete){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"购物车清空成功");
				}else{
					return ResultDTO.getSuccess(AppCode.SUCCESS,"购物车清空失败");
				}
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
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
	
	//获取所属机构的规格型号
	@RequestMapping(value = "/getSpecificationsByOrgId/{id}")
	@ResponseBody
	public ResultDTO getSpecificationsByOrgId(@PathVariable(value="id") String id) throws Exception {
		try {
			List<String> specifications = commodityService.getSpecificationsByOrgId(id);
			if(specifications!=null && specifications.size()>0){
				return ResultDTO.getSuccess(specifications);
			}else {
				return ResultDTO.getFailure(500, "没有规格型号");
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
				return ResultDTO.getFailure(500, "没有规格型号");
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
}