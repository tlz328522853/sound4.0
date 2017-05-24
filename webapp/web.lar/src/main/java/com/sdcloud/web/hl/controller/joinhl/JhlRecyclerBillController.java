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

import com.sdcloud.api.hl.entity.RecyclerBill;
import com.sdcloud.api.hl.service.RecyclerBillService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_recycler_bill 回收机用户保修单
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/recyclerBill")
public class JhlRecyclerBillController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecyclerBillService recyclerBillService;
	
		
	/**
	 * 添加数据
	 * @param recyclerBill 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecyclerBill recyclerBill){
		try {
			if(recyclerBill != null){
				if(recyclerBill.getId()!=null&&recyclerBill.getUserId()!=null
						&&StringUtils.isNotEmpty(recyclerBill.getMchid())){
					recyclerBill.setBillId(recyclerBill.getId().intValue());
					if(recyclerBill.getStatus()==null){
						recyclerBill.setStatus((byte)0);	
					}
					recyclerBill.setId(null);
					if(recyclerBillService.countByBillId(recyclerBill.getBillId())>0){
						return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = recyclerBillService.save(recyclerBill);
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
	 * @param recyclerBill 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  RecyclerBill recyclerBill){
		try {
			if(recyclerBill != null){
				if(recyclerBill.getId()!=null){
					recyclerBill.setBillId(recyclerBill.getId().intValue());
					recyclerBill.setId(null);
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				boolean b = recyclerBillService.update(recyclerBill);
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
