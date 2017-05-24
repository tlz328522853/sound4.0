package com.sdcloud.web.lar.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.Detail;
import com.sdcloud.api.lar.service.DetailService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.WebViewUtils;

@RestController
@RequestMapping("/api/detail")
public class DetailController {

	@Autowired
	private DetailService detailService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<Detail> pager){
		try {
			pager = detailService.findAll(pager);
			return ResultDTO.getSuccess(200, detailService.findAll(pager));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) Detail detail) throws Exception{
		try {
			if(detail != null){
				boolean b = detailService.save(detail);
				if(b){
					return ResultDTO.getSuccess("添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) Detail detail){
		try {
			if(detail != null){
				boolean b = detailService.update(detail);
				if(b){
					return ResultDTO.getSuccess("修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/detail/{id}",method=RequestMethod.GET)
	@ResponseBody
	public void detail( @PathVariable Long id, HttpServletResponse response) {
		Detail detail = detailService.getById(id,null);
		WebViewUtils.printView(response, detail.getDetail());
	}
}
