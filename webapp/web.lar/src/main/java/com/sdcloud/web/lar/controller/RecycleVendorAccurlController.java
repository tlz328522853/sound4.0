package com.sdcloud.web.lar.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.entity.RecycleVendor;
import com.sdcloud.api.lar.entity.RecycleVendorAccurl;
import com.sdcloud.api.lar.service.RecycleVendorAccurlService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * lar_recycle_vendor_accurl 供应商附件路径
 * @author TLZ
 * @date 2016-12-21
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleVendorAccurl")
public class RecycleVendorAccurlController{

	@Autowired
	private RecycleVendorAccurlService recycleVendorAccurlService;
	@Autowired 
	private OrgService orgService;
	Logger logger = LoggerFactory.getLogger(this.getClass());
		
	
	@RequestMapping(value="/findById",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findById(@RequestBody RecycleVendor recycleVendor){
		
		try {
			if(recycleVendor != null){
				Long id = recycleVendor.getAccurlId();
				List<RecycleVendorAccurl> recycleVendorAccurl = recycleVendorAccurlService.findById(id); 
	            return ResultDTO.getSuccess(recycleVendorAccurl);
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	

	}
	
	
	
	/**
	 * 添加数据
	 * @param recycleVendorAccurl 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecycleVendorAccurl recycleVendorAccurl){
		try {
			if(recycleVendorAccurl != null){
				Long id = UUIDUtil.getUUNum();
				recycleVendorAccurl.setId(id);
				boolean b = recycleVendorAccurlService.save(recycleVendorAccurl);
				if(b){
					return ResultDTO.getSuccess(id,"添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}
	
		
	/**
	 * 根据主键ID 删除数据
	 * @param recycleVendorAccurl 删除数据
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO deleteByPrimary(@RequestBody RecycleVendorAccurl recycleVendorAccurl){
		try {
			if(recycleVendorAccurl != null && recycleVendorAccurl.getId()!=null){
			    boolean b = recycleVendorAccurlService.delete(recycleVendorAccurl.getId());
				if(b){
					return ResultDTO.getSuccess(200,"删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}
	
	
	/**
	 * 更新数据
	 * @param recycleVendorAccurl 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  RecycleVendorAccurl recycleVendorAccurl){
		try {
			if(recycleVendorAccurl != null){
				boolean b = recycleVendorAccurlService.update(recycleVendorAccurl);
				if(b){
					return ResultDTO.getSuccess(200,"修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}

	
}
