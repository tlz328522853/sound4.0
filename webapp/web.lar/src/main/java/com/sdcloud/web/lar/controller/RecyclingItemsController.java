package com.sdcloud.web.lar.controller;


import java.util.ArrayList;
import java.util.Date;
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

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.service.RecyclingItemsService;
import com.sdcloud.api.lar.service.RecyclingSpecService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

import io.jsonwebtoken.lang.Collections;

@RestController
@RequestMapping("/api/recyclingItems")
public class RecyclingItemsController {

	@Autowired
	private RecyclingItemsService recyclingItemsService;
	@Autowired
	private DicService dicService;
	@Autowired
	private OrgService orgService;

	// 增加
	@RequestMapping(value = "/saveRecyclingMaterial", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false)LarPager<RecyclingMaterial> result) throws Exception {
		//LarPager<RecyclingMaterial> result = null;
		try {
			RecyclingMaterial recyclingMaterial = (RecyclingMaterial) result.getResult().get(0);
			recyclingMaterial.setStartUsing(1);
			recyclingMaterial.setStartUsingForApp(1);
			result.setResult(null);
			if (recyclingMaterial != null
					&& (recyclingMaterial.getId() == null || recyclingMaterial.getId().length() <= 0)) {
				boolean insertUserGetId = recyclingItemsService.insertSelective(recyclingMaterial);
				if (insertUserGetId) {
					//result = new LarPager<RecyclingMaterial>();
					result = recyclingItemsService.selectByExample(result);
					return ResultDTO.getSuccess(200,"保存成功!",result);
				} else {
					return ResultDTO.getFailure(500, "物品添加失败！");
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
	public ResultDTO save(@RequestBody(required = false) RecyclingType recyclingType) throws Exception {
		LarPager<RecyclingType> result = null;
		try {
			if (recyclingType != null && (recyclingType.getId() == null || recyclingType.getId().length() <= 0)) {
				boolean insertUserGetId = recyclingItemsService.insertSelective(recyclingType);
				if (insertUserGetId) {
					result = new LarPager<RecyclingType>();
					result = recyclingItemsService.selectTypeByExample(result);
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
	@RequestMapping(value = "/deleteRecyclingMaterial/{id}/{goodsId}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id,
			@PathVariable(value = "goodsId") String goodsId,
			@RequestBody(required = false)LarPager<RecyclingMaterial> pager) throws Exception {	
		try {
			if (id != null && id.trim().length() > 0) {
				
				//修改前先判断
				LarPager<RecyclingSpec> larPager = new LarPager<>();
				//pager.getParams().put("goods_id", goodsId);
				larPager.getParams().put("goods_id", goodsId);
				recyclingSpecService.findAll(larPager);
				if(!Collections.isEmpty(larPager.getResult())){
					return ResultDTO.getFailure(500, "已添加规格的物品不允许删除！");
				}
				
				boolean deleteById = recyclingItemsService.deleteById(id);
				if (deleteById) {
					pager = recyclingItemsService.selectByExample(pager);
					return ResultDTO.getSuccess(pager,"删除成功!");
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

	// 删除
	@RequestMapping(value = "/deleteRecyclingMaterialByType/{typeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO deleteByTypeId(@PathVariable(value = "typeId") String id) throws Exception {
		LarPager<RecyclingMaterial> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = recyclingItemsService.deleteByTypeId(id);
				if (deleteById) {
					result = new LarPager<RecyclingMaterial>();
					result = recyclingItemsService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "物品删除失败！");
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
		LarPager<RecyclingType> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = recyclingItemsService.deleteTypeById(id);
				if (deleteById) {
					result = new LarPager<RecyclingType>();
					result = recyclingItemsService.selectTypeByExample(result);
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
	
	@Autowired
	private RecyclingSpecService recyclingSpecService;

	// 修改
	@RequestMapping(value = "/updateRecyclingMaterial", method = RequestMethod.POST)
	public ResultDTO update( @RequestBody(required = false)LarPager<RecyclingMaterial> result) throws Exception {
		//LarPager<RecyclingMaterial> result = null;
		try {
			RecyclingMaterial recyclingMaterial = (RecyclingMaterial) result.getResult().get(0);
			result.setResult(null);
	
			if (recyclingMaterial != null && recyclingMaterial.getId() != null
					&& recyclingMaterial.getId().trim().length() > 0) {
				
				//修改前先判断
				LarPager<RecyclingSpec> pager = new LarPager<>();
				pager.getParams().put("goods_id", recyclingMaterial.getGoodsId());
				recyclingSpecService.findAll(pager);
				if(!Collections.isEmpty(pager.getResult())){
					return ResultDTO.getFailure(500, "已添加规格的物品不允许修改！");
				}
			
				boolean updateByExampleSelective = recyclingItemsService.updateByExampleSelective(recyclingMaterial);
				if (updateByExampleSelective) {
					//result = new LarPager<RecyclingMaterial>();
					result = recyclingItemsService.selectByExample(result);
					return ResultDTO.getSuccess(result,"修改成功!");
				} else {
					return ResultDTO.getFailure(500, "物品修改失败！");
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
	public ResultDTO update(@RequestBody(required = false) RecyclingType recyclingType) throws Exception {
		LarPager<RecyclingType> result = null;
		try {
			if (recyclingType != null && recyclingType.getId() != null && recyclingType.getId().trim().length() > 0) {
				boolean updateByExampleSelective = recyclingItemsService.updateByExampleSelective(recyclingType);
				if (updateByExampleSelective) {
					result = new LarPager<RecyclingType>();
					result = recyclingItemsService.selectTypeByExample(result);
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

	// 查询列表
	@RequestMapping("/getRecyclingMaterials")
	@ResponseBody
	public ResultDTO getRecyclingMaterials(@RequestBody(required = false) LarPager<RecyclingMaterial> larPager)
			throws Exception {
		try {
			larPager = recyclingItemsService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 查询列表
	@RequestMapping("/getRecyclingTypes")
	@ResponseBody
	public ResultDTO getRecyclingTypes(@RequestBody(required = false) LarPager<RecyclingType> larPager)
			throws Exception {
		try {
			larPager = recyclingItemsService.selectTypeByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	@RequestMapping(value = "/getRecyclingTypesForList")
	@ResponseBody
	public ResultDTO getRecyclingTypes() throws Exception {
		try {
			List<Dic> dlist=dicService.findByPid(1646851684743481L, null);//回收物字典分类
			List<RecyclingType> recyclingTypes = recyclingItemsService.getRecyclingTypes();
			Map<String,String> rMap=new HashMap<String,String>();
			for(RecyclingType type: recyclingTypes){
				rMap.put(type.getId(), type.getId());
			}
			for(Dic dic: dlist){
				if(rMap.get(dic.getDicId().toString())==null){//不存在则新建
					RecyclingType r=new RecyclingType(dic.getDicId()+"",dic.getName(),0,new Date(),dic.getSequence()==null?0:dic.getSequence());
					recyclingItemsService.saveRecyclingType(r);
				}
			}
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(recyclingTypes);
			}else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构查询可用的回收物品类型
	@RequestMapping(value = "/getOrgRecyclingTypes", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getOrgRecyclingTypes(@RequestBody(required = false) Map<String, Object> orgMap) throws Exception {
		try {
			Object orgObj = orgMap.get("org");
			Object includeSubObj = orgMap.get("includeSub");
			if(null ==orgObj){
				return ResultDTO.getFailure(500, "参数有误！");
			}
			List<Long> orgIds = this.getOrgList(orgObj,includeSubObj);
			List<RecyclingType> types=null;
			
			Boolean priceEnable = true;
			if(null != orgMap.get("priceEnable") ){
				priceEnable = Boolean.parseBoolean(orgMap.get("priceEnable").toString());
			}
			
			if(!Collections.isEmpty(orgIds))
				types = recyclingItemsService.getOrgRecyclingTypes(orgIds,priceEnable);
			if(Collections.isEmpty(types)){
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
			return ResultDTO.getSuccess(types);
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构查询所有的回收物品类型
	@RequestMapping(value = "/getOrgRecyclingAllTypes", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getOrgRecyclingAllTypes(@RequestBody(required = false) Map<String, Object> orgMap) throws Exception {
		try {
			Object orgObj = orgMap.get("org");
			Object includeSubObj = orgMap.get("includeSub");
			if(null ==orgObj){
				return ResultDTO.getFailure(500, "参数有误！");
			}
			List<Long> orgIds = this.getOrgList(orgObj,includeSubObj);
			List<RecyclingType> types=null;
			
			Boolean priceEnable = true;
			if(null != orgMap.get("priceEnable") ){
				priceEnable = Boolean.parseBoolean(orgMap.get("priceEnable").toString());
			}
			
			if(!Collections.isEmpty(orgIds))
				types = recyclingItemsService.getOrgRecyclingAllTypes(orgIds,priceEnable);
			if(Collections.isEmpty(types)){
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
			return ResultDTO.getSuccess(types);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param orgObj 机构 id 多个以“AAA”拼接
	 * @param includeSubObj 是否包含子机构 
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj,Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if(null != includeSubObj){
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}
		
		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for(String orgString:orgArr){
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds ;
	}

	@RequestMapping(value = "/getRecyclingNames/{id}")
	@ResponseBody
	public ResultDTO getRecyclingNames(@PathVariable(value = "id") String id) throws Exception {
		try {
			List<RecyclingMaterial> recyclingMaterials = recyclingItemsService.getRecyclingNames(id);
			if (recyclingMaterials != null && recyclingMaterials.size() > 0) {
				return ResultDTO.getSuccess(recyclingMaterials);
			} else {
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构查询可用的回收物品
	@RequestMapping(value = "/getOrgRecyclingNames", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getOrgRecyclingNames(@RequestBody(required = false) Map<String, Object> orgMap) throws Exception {
		try {
			Object orgObj = orgMap.get("org");
			Object typeObj = orgMap.get("typeId");
			Object includeSubObj = orgMap.get("includeSub");
			
			if(null ==orgObj || null == typeObj){
				return ResultDTO.getFailure(500, "参数有误！");
			}
			List<Long> orgIds = this.getOrgList(orgObj,includeSubObj);
			Long type = Long.parseLong(typeObj.toString());
			List<RecyclingMaterial> recyclingMaterials=null;
			Boolean priceEnable = true;
			if(null != orgMap.get("priceEnable") ){
				priceEnable = Boolean.parseBoolean(orgMap.get("priceEnable").toString());
			}
			
			if(!Collections.isEmpty(orgIds))
				recyclingMaterials = recyclingItemsService.getRecyclingNames(orgIds,type,priceEnable);
			if(Collections.isEmpty(recyclingMaterials)){
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
			return ResultDTO.getSuccess(recyclingMaterials);
		} catch (Exception e) {
			throw e;
		}
	}
	
	//根据机构查询所有的回收物品
	@RequestMapping(value = "/getOrgRecyclingAllNames", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getOrgRecyclingAllNames(@RequestBody(required = false) Map<String, Object> orgMap) throws Exception {
		try {
			Object orgObj = orgMap.get("org");
			Object typeObj = orgMap.get("typeId");
			Object includeSubObj = orgMap.get("includeSub");
			
			if(null ==orgObj || null == typeObj){
				return ResultDTO.getFailure(500, "参数有误！");
			}
			List<Long> orgIds = this.getOrgList(orgObj,includeSubObj);
			Long type = Long.parseLong(typeObj.toString());
			List<RecyclingMaterial> recyclingMaterials=null;
			Boolean priceEnable = true;
			if(null != orgMap.get("priceEnable") ){
				priceEnable = Boolean.parseBoolean(orgMap.get("priceEnable").toString());
			}
			
			if(!Collections.isEmpty(orgIds))
				recyclingMaterials = recyclingItemsService.getRecyclingAllNames(orgIds,type,priceEnable);
			if(Collections.isEmpty(recyclingMaterials)){
				return ResultDTO.getFailure(500, "没有物品类型数据");
			}
			return ResultDTO.getSuccess(recyclingMaterials);
		} catch (Exception e) {
			throw e;
		}
	}

	// 更改启用状态
	@RequestMapping(value = "/updateStartUsing/{id}/{status}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateStartUsing(@PathVariable(value = "id") String id,
			@PathVariable(value = "status") String status) throws Exception {
		try {
			boolean isNo = recyclingItemsService.updateStartUsing(id, status);
			if (isNo) {
				return ResultDTO.getSuccess("启用状态修改成功");
			} else {
				return ResultDTO.getFailure(500, "启用状态修改失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 更改APP启用状态
	@RequestMapping(value = "/updateStartUsingForApp/{id}/{status}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO updateStartUsingForApp(@PathVariable(value = "id") String id,
			@PathVariable(value = "status") String status) throws Exception {
		try {
			boolean isNo = recyclingItemsService.updateStartUsingForApp(id, status);
			if (isNo) {
				return ResultDTO.getSuccess("APP启用状态修改成功");
			} else {
				return ResultDTO.getFailure(500, "APP启用状态修改失败");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}