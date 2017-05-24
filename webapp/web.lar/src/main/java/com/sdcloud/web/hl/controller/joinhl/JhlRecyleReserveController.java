package com.sdcloud.web.hl.controller.joinhl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.RecyleReserve;
import com.sdcloud.api.hl.service.RecyleReserveService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_recyle_reserve 上门回收预约
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/recyleReserve")
public class JhlRecyleReserveController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecyleReserveService recyleReserveService;
	
		
	/**
	 * 添加数据
	 * @param recyleReserve 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecyleReserve recyleReserve){
		try {
			if(recyleReserve != null){
				if(recyleReserve.getId()!=null){
					recyleReserve.setReserveId(recyleReserve.getId().intValue());
					recyleReserve.setId(null);
					if(recyleReserveService.countByReserveId(recyleReserve.getReserveId())>0){
						return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = recyleReserveService.save(recyleReserve);
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
	
	/**
	 * 更新数据
	 * @param recyleReserve 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  RecyleReserve recyleReserve){
		try {
			if(recyleReserve != null){
				if(recyleReserve.getId()!=null){
					recyleReserve.setReserveId(recyleReserve.getId().intValue());
					recyleReserve.setId(null);
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = recyleReserveService.update(recyleReserve);
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
