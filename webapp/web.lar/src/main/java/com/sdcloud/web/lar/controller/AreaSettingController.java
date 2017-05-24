package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.biz.lar.util.Constant;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/areaSetting")
public class AreaSettingController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AreaSettingService areaSettingService;
	@Autowired
	private SalesmanService salesmanService;
	@Autowired
	private OrgService orgService;
	
	// 增加
	@RequestMapping(value = "/saveAreaSetting", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) AreaSetting areaSetting)
			throws Exception {
		LarPager<AreaSetting> result = null;
		try {
			if (areaSetting != null && StringUtils.isEmpty(areaSetting.getId())
					&&StringUtils.isNotEmpty(areaSetting.getAreaPosition())
					&&areaSetting.getAreaPosition().contains("[{")) {
				boolean insertUserGetId = areaSettingService.insertSelective(areaSetting);
				if (insertUserGetId) {
					//此处修改的前提，认为机构ID（mechanismId）存在
					//异步：此处添加新区域成功，需要根据机构ID>cityId>联系人列表>进行区域联系人验证
					areaSettingService.diffAddrByAreaSetting(areaSetting,null,Constant.FROM_METHOD_INSERT);
					logger.info("异步执行>>回收区域添加成功>>重新修改联系人地址的areaType类型......id:"+areaSetting.getId().trim());
					
					LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", areaSetting.getMechanismId());
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(result,"片区保存成功!");
				} else {
					return ResultDTO.getFailure(500, "片区添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "片区添加失败!请检查信息是否完整,请重新添加");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 删除
	// 需要同时更改用户对应的地址表
	@RequestMapping(value = "/deleteAreaSetting/{id}/{orgId}", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id,@PathVariable(value="orgId") String orgId,
			@RequestBody(required = false) LarPager<AreaSetting> larPager) throws Exception {
		LarPager<AreaSetting> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				//查询该片区下是否还有业务
				
				Long count = salesmanService.selectByArea(id.trim());
				
				if(null != count && count>0){
					return ResultDTO.getFailure(500, "片区删除失败,请检查该片区下是否还有业务员！");
				}
				//获取删除前的区域对象
				AreaSetting area=areaSettingService.selectByPrimaryKey(id.trim());
				boolean deleteById = areaSettingService.deleteById(id.trim());
				if (deleteById) {
					//异步：此处删除区域成功，需要根据 区域ID>cityId>联系人列表>进行区域联系人验证
					areaSettingService.diffAddrByAreaSetting(area,null,Constant.FROM_METHOD_DELETE);
					logger.info("异步执行>>回收区域删除成功>>重新修改联系人地址的areaType类型......id:"+id.trim());
					
					//LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orgId);
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(200,"删除片区成功!",result);
				} else {
					return ResultDTO.getFailure(500, "片区删除失败!");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 修改
	@RequestMapping(value = "/updateAreaSetting", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) AreaSetting areaSetting) throws Exception {
		LarPager<AreaSetting> result = null;
		try {
			if (areaSetting != null && areaSetting.getId() != null && areaSetting.getId().trim().length() > 0) {
				//更新之前的区域
				AreaSetting areaSettingBefore=areaSettingService.selectByPrimaryKey(areaSetting.getId());
				
				boolean updateByExampleSelective = areaSettingService.updateByExampleSelective(areaSetting);
				if (updateByExampleSelective) {
					//异步：此处更新区域成功，需要根据 区域ID>cityId>联系人列表>进行区域联系人验证
					areaSettingService.diffAddrByAreaSetting(areaSetting,areaSettingBefore,Constant.FROM_METHOD_UPDATE);
					logger.info("异步执行>>回收区域修改成功>>重新修改联系人地址的areaType类型......id:"+areaSetting.getId().trim());
					
					LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", areaSetting.getMechanismId());
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(result,"片区修改成功!");
				} else {
					return ResultDTO.getFailure(500, "片区修改失败！");
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
	@RequestMapping("/getAreaSettings")
	@ResponseBody
	public ResultDTO getClientUsers(@RequestBody(required = false) LarPager<AreaSetting> larPager)throws Exception {
		try {
			this.convertPrams(larPager);
			larPager = areaSettingService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<AreaSetting> larPager) throws Exception {
		if(null !=larPager.getParams().get("includeSub")){
			//添加查询子机构功能
			boolean includeSub = (boolean) larPager.getParams().get("includeSub");
			if(includeSub){
				String orgStr = larPager.getParams().get("mechanismId").toString();
				String[] split = orgStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。		
				List<Long> orgIds = new ArrayList<>();
				for(String orgString :split){
					List<Org> list = orgService.findById(Long.valueOf(orgString), true);
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

	// 查询一个
	@RequestMapping(value = "/getAreaSettings/{id}")
	@ResponseBody
	public ResultDTO getClientUserById(@PathVariable(value = "id") String id)
			throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				AreaSetting areaSetting = areaSettingService.selectByPrimaryKey(id);
				if (areaSetting != null) {
					return ResultDTO.getSuccess(areaSetting);
				} else {
					return ResultDTO.getFailure(500, "没有这个片区的数据");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 根据机构id查询所有的片区
	@RequestMapping(value = "/getareaNames/{id}")
	@ResponseBody
	public ResultDTO getareaNames(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<AreaSetting> areaSettings = areaSettingService.selectAreaById(id);
				if (areaSettings != null && areaSettings.size()>0) {
					return ResultDTO.getSuccess(areaSettings);
				} else {
					return ResultDTO.getFailure(500, "没有这个片区的数据");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}