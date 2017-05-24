package com.sdcloud.web.lar.controller.app;

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
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/app/areaSetting")
public class AppAreaSettingController {

	@Autowired
	private AreaSettingService areaSettingService;

	// 增加
	@RequestMapping(value = "/saveAreaSetting", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) AreaSetting areaSetting)
			throws Exception {
		LarPager<AreaSetting> result = null;
		try {
			if (areaSetting != null && (areaSetting.getId()==null || areaSetting.getId().length()<=0)) {
				boolean insertUserGetId = areaSettingService.insertSelective(areaSetting);
				if (insertUserGetId) {
					LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", areaSetting.getMechanismId());
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "片区添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 删除
	// 需要同时更改用户对应的地址表
	@RequestMapping(value = "/deleteAreaSetting/{id}/{orgId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id,@PathVariable(value="orgId") String orgId) throws Exception {
		LarPager<AreaSetting> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = areaSettingService.deleteById(id.trim());
				if (deleteById) {
					LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", orgId);
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "片区删除失败！");
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
				boolean updateByExampleSelective = areaSettingService.updateByExampleSelective(areaSetting);
				if (updateByExampleSelective) {
					LarPager<AreaSetting> larPager = new LarPager<AreaSetting>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", areaSetting.getMechanismId());
					larPager.setParams(params);
					result = areaSettingService.selectByExample(larPager);
					return ResultDTO.getSuccess(result);
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
			larPager = areaSettingService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
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