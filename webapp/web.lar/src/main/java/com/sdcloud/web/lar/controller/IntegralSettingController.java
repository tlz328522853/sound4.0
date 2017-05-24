package com.sdcloud.web.lar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.IntegralSetting;
import com.sdcloud.api.lar.service.IntegralSettingService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/integralSetting")
public class IntegralSettingController {

	@Autowired
	private IntegralSettingService integralSettingService;

	// 查询列表
	@RequestMapping("/getIntegralSetting")
	@ResponseBody
	public ResultDTO getMaaDateSettings(@RequestBody(required = false) LarPager<IntegralSetting> larPager)
			throws Exception {
		try {
			larPager = integralSettingService.findAll(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	//等级查询接口
	@RequestMapping("/getByLevel")
	@ResponseBody
	public ResultDTO getByLevel(@RequestBody(required = false) Integer level)
			throws Exception {
		IntegralSetting byLevel = null;
		try {
			byLevel = integralSettingService.getByLevel(level);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(byLevel);
	}
	
	// 提供查询接口
	@RequestMapping("/getList")
	@ResponseBody
	public ResultDTO getList() throws Exception {
		LarPager<IntegralSetting> larPager = new LarPager<>();
		try {
			larPager.setPageSize(1000000);
			larPager = integralSettingService.findAll(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager.getResult());
	}

	// 新增
	@RequestMapping(value = "/saveIntegralSetting", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) IntegralSetting integralSetting) throws Exception {

		IntegralSetting byLevel =null;
		if(integralSetting.getLevel() == null || integralSetting.getIntegralNum()==null){
			return ResultDTO.getSuccess(400, "请输入正确等级或积分数量!", "请输入正确等级或积分数量!");
		}
		try {
			byLevel = integralSettingService.getByLevel(integralSetting.getLevel());
			
			if(byLevel != null && (integralSetting.getId() == null)){
				return ResultDTO.getSuccess(400, "亲，等级重复啦!", "亲，等级重复啦!");
			}
			
			if (byLevel == null&&integralSetting != null && (integralSetting.getId() == null)) {
				boolean insertUserGetId = integralSettingService.save(integralSetting);
				if (insertUserGetId) {

					return ResultDTO.getSuccess(200, "保存成功!", "保存成功!");
				} else {
					return ResultDTO.getFailure(500, "保存失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/deleteIntegralSetting/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") Long id) throws Exception {

		try {
			if (id != null) {
				boolean deleteById = integralSettingService.delete(id);
				if (deleteById) {
					return ResultDTO.getSuccess(200,"删除成功!");
				} else {
					return ResultDTO.getFailure(500, "删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 修改
	@RequestMapping(value = "/updateIntegralSetting", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) IntegralSetting integralSetting) throws Exception {
		if(integralSetting.getLevel() == null || integralSetting.getIntegralNum()==null){
			return ResultDTO.getSuccess(400, "请输入正确等级或积分数量!", "请输入正确等级或积分数量!");
		}
		try {
			
			if ( integralSetting != null && integralSetting.getId() != null) {
				boolean updateByExampleSelective = integralSettingService.update(integralSetting);
				if (updateByExampleSelective) {
					return ResultDTO.getSuccess(200, "修改成功!","修改成功!");
				} else {
					return ResultDTO.getFailure(500, "修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
