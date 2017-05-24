package com.sdcloud.web.hl.controller.joinhl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.Hypay;
import com.sdcloud.api.hl.service.HypayService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * 水电缴费
 * hl_hypay 
 * @author jiazc
 * @date 2017-05-15
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/hypay")
public class JhlHypayController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HypayService hypayService;
	
		
	/**
	 * 添加数据
	 * @param hypay 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody Hypay hypay){
		try {
			if(hypay != null){
				if(hypay.getId()!=null
						){
					hypay.setPayId(hypay.getId().intValue());
					hypay.setId(null);
					if(hypayService.countByPayId(hypay.getPayId())>0){
						return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
					}
				}
				boolean b = hypayService.save(hypay);
				if(b){
					return ResultDTO.getSuccess(200,"成功");
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
	
	/**
	 * 更新数据
	 * @param hypay 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  Hypay hypay){
		try {
			if(hypay != null){
				boolean b = hypayService.update(hypay);
				if(b){
					return ResultDTO.getSuccess(200,"成功");
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
