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

import com.sdcloud.api.hl.entity.ExpressApply;
import com.sdcloud.api.hl.service.ExpressApplyService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_express_apply 快递申请记录
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/expressApply")
public class JhlExpressApplyController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ExpressApplyService expressApplyService;
	
		
	/**
	 * 添加数据
	 * @param expressApply 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody ExpressApply expressApply){
		try {
			if(expressApply != null){
				if(expressApply.getId()!=null
						&&expressApply.getUserId()!=null
						&&StringUtils.isNotEmpty(expressApply.getOrderNo())){
					expressApply.setApplyId(expressApply.getId().intValue());
					expressApply.setId(null);
					if(expressApplyService.countByApplyId(expressApply.getApplyId())>0){
						return ResultDTO.getSuccess(201, "重复添加，数据已存在",null);
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = expressApplyService.save(expressApply);
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
