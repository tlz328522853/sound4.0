package com.sdcloud.web.lar.controller.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.service.LarRegionService;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/app/larRegion")
public class AppRegionController {

	@Autowired
	private LarRegionService larRegionService;
	
	@RequestMapping(value="/getRegions/{id}",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getRegions(@PathVariable(value="id") String id) throws Exception{
		try {
			if(id!=null && id.trim().length()>0){
				List<LarRegion> larRegions = larRegionService.getLarRegions(Integer.valueOf(id));
				if(larRegions!=null && larRegions.size()>0){
					return ResultDTO.getSuccess(larRegions);
				}else{
					return ResultDTO.getFailure(500, "没有数据");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	@RequestMapping(value="/findAll",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO findAll(){
		try {
				List<LarRegion> larRegions = larRegionService.findAll();
				if(larRegions!=null && larRegions.size()>0){
					return ResultDTO.getSuccess(larRegions);
				}else{
					return ResultDTO.getFailure(500, "没有数据");
				}
				
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "后台处理错误！");
			
		}
	}
}
