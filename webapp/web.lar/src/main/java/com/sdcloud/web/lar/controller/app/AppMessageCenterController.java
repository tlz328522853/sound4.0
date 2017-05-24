package com.sdcloud.web.lar.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.service.MessageCenterService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/messageCenter")
public class AppMessageCenterController {

	@Autowired
	private MessageCenterService messageCenterService;
	
	//查询列表
	@RequestMapping(value="/finAllmessage",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO finAllmessage(@RequestBody(required=false) LarPager<MessageCenter> larPager){
		
		try {
			if(larPager != null){
				if(larPager.getOrder() == null){
					larPager.setOrder("desc");
				}
				LarPager<MessageCenter> result = messageCenterService.findAll(larPager);
				return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取消息", result);
			}else{
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求,请重新尝试!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常!");
		}
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) MessageCenter messageCenter){
		
		try {
			if(messageCenter != null){
				Boolean save = messageCenterService.save(messageCenter);
				if(save){
					return ResultDTO.getSuccess("保存成功");
				}
				return ResultDTO.getSuccess("保存失败!");
			}else{
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求,请重新尝试!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常!");
		}
	}
}
