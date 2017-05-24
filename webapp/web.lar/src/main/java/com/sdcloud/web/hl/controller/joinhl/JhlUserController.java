package com.sdcloud.web.hl.controller.joinhl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.User;
import com.sdcloud.api.hl.service.UserService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_user 用户基本数据
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/user")
public class JhlUserController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	
		
	/**
	 * 添加数据
	 * @param user 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody User user){
		try {
			if(user != null){
				if(user.getId()!=null
						&&StringUtils.isNotEmpty(user.getMobile())){
					user.setUserId(user.getId().intValue());
					user.setId(null);
					if(userService.countByUserId(user.getUserId())>0){
						return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
					}
				}
				else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = userService.save(user);
				if(b){
					return ResultDTO.getSuccess(200,"成功",null);
				}else{
					return ResultDTO.getFailure(401, "失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "数据异常，系统繁忙");
		}
	}
	
}
