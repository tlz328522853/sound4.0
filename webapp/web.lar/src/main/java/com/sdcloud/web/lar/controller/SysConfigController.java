package com.sdcloud.web.lar.controller;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.SysConfig;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * 
 * @author luorongjie
 *
 */
@RestController
@RequestMapping("/api/sysConfig")
public class SysConfigController {
	
	@Autowired
	private SysConfigService sysConfigService;
		
	@RequestMapping(value="/findMap",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findMap(){
		Map<String,String> result = new HashMap<String, String>();
		try {
			result=  sysConfigService.findMap();
		} catch (Exception e) {
			throw e;
		}
		
		return ResultDTO.getSuccess(result);
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false)Map<String, String> sysConfigMap){
		
		try {
			if(sysConfigMap != null){
				boolean b = sysConfigService.updateMap(sysConfigMap);
				
				if(b){
					return ResultDTO.getSuccess("修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
				
			}else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
