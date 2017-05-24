package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.RecycleRetailer;
import com.sdcloud.api.lar.entity.RecycleRetailerAccurl;
import com.sdcloud.api.lar.entity.RecycleVendor;
import com.sdcloud.api.lar.service.RecycleRetailerAccurlService;
import com.sdcloud.api.lar.service.RecycleRetailerService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.FileOperationsUtils;

/**
 * lar_recycle_retailer 销售客户管理
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleRetailer")
public class RecycleRetailerController{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RecycleRetailerService recycleRetailerService;
	@Autowired 
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private RecycleRetailerAccurlService recycleRetailerAccurlService;
	
	// 查询列表
	/*
	 * larPager.params.retailer 销售客户名称
	 * mechanismId 机构id
	 */
	@RequestMapping("/getRecycleRetailer")
	@ResponseBody
	public  ResultDTO getRecycleVendor(@RequestBody(required = false) LarPager<RecycleRetailer> larPager) throws Exception {
		try {
			
			this.convertPrams(larPager);
			larPager = recycleRetailerService.findAll(larPager);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<RecycleRetailer> larPager) throws Exception {
		if(null !=larPager.getParams().get("includeSub")){
			//添加查询子机构功能
			boolean includeSub = (boolean) larPager.getParams().get("includeSub");
			if(includeSub){
				//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
				String orgStr = larPager.getParams().get("mechanismId").toString();
				String[] orgArr = orgStr.split("AAA");
				List<Long> orgIds = new ArrayList<>();
				for(String orgString:orgArr){
					Long mechanismId = Long.parseLong(orgString);
					List<Org> list = orgService.findById(mechanismId, true);
					for (Org org : list) {
						orgIds.add(org.getOrgId());
					}
				}
			
				larPager.getParams().remove("mechanismId");
				larPager.getParams().put("orgIds", orgIds);
			}
			larPager.getParams().remove("includeSub");
		}
	}

		//查询所有数据的接口
		@RequestMapping("/findByOrgIds")
		@ResponseBody
		public  ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<RecycleRetailer> larPager) throws Exception {
			try {
				
				this.convertPrams(larPager);
				larPager = recycleRetailerService.findByOrgIds(larPager,null);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			return ResultDTO.getSuccess(larPager);
		}
	
	/**
	 * 添加数据
	 * @param recycleRetailer 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecycleRetailer recycleRetailer, HttpServletRequest request){
		try {
			RecycleRetailer existByRetailer=null;
			RecycleRetailer existByRetailerShort=null;
			if(recycleRetailer.getRetailer()!=null){
				
				String retailer=recycleRetailer.getRetailer().trim();
				long mechanismId=recycleRetailer.getOrgId();
				existByRetailer = recycleRetailerService.existByRetailer(mechanismId,retailer);
				if(existByRetailer!=null){
					return ResultDTO.getFailure(500, "客户名称重复无法保存！");
				}
			}
			if(recycleRetailer.getRetailerShort()!=null){
				
				String retailerShort=recycleRetailer.getRetailerShort().trim();
				long mechanismId=recycleRetailer.getOrgId();
				existByRetailerShort = recycleRetailerService.existByRetailerShort(mechanismId,retailerShort);
				if(existByRetailerShort!=null){
					return ResultDTO.getFailure(500, "客户简称重复无法保存！");
				}
			}
			if(recycleRetailer != null){
				Long id = UUIDUtil.getUUNum();
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				recycleRetailer.setCreateMen(user.getName());
				recycleRetailer.setAccurlId(id);
				Date date=new Date();
				recycleRetailer.setUpdateTime(date);
				recycleRetailer.setCreateTime(date);
				recycleRetailer.setUpdateMen(user.getName());
				if(recycleRetailer.getWeburls()!=null && recycleRetailer.getWeburls().length>0){
					RecycleRetailerAccurl recycleRetailerAccurl =new RecycleRetailerAccurl();
					String size = recycleRetailer.getWeburls()[0];
					String name = recycleRetailer.getWeburls()[1];
					String url = recycleRetailer.getWeburls()[2];
					
					recycleRetailerAccurl.setName(name);
					recycleRetailerAccurl.setUrl(url);
					if(recycleRetailer.getAccurlId()!=null){
						recycleRetailerAccurl.setRetailerId(recycleRetailer.getAccurlId());
					}
					if(recycleRetailer.getAccurlId()==null){
						recycleRetailerAccurl.setRetailerId(id);
					}
					
					Long of = Long.valueOf(size);
					
					recycleRetailerAccurl.setFileSize(of);
				
					
						
					recycleRetailerAccurlService.save(recycleRetailerAccurl);
					
					recycleRetailer.setAccessory("有");
				}
				
				
				boolean b = recycleRetailerService.save(recycleRetailer);
				if(b){
					return ResultDTO.getSuccess(null,"添加成功！");
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
			return ResultDTO.getFailure(500, "请正确输入客户名称和简称!");
		}
	}
	
		
	/**
	 * 启停用
	 * @param recycleVendor 
	 * @return
	 */
	@RequestMapping(value="/updateEnable")
	@ResponseBody
	public ResultDTO updateEnable(@RequestBody  RecycleRetailer recycleRetailer){
		try {
			if(recycleRetailer.getEnable()==0){
				recycleRetailer.setEnable((int)1);
			}else{
				recycleRetailer.setEnable((int)0);
			}
			recycleRetailerService.update(recycleRetailer);
			return ResultDTO.getSuccess(200,"操作成功！" );
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}
	
	/**
	 * 根据主键ID 删除数据
	 * @param recycleRetailer 删除数据
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO deleteByPrimary(@RequestBody RecycleRetailer recycleRetailer){
		try {
			if(recycleRetailer != null && recycleRetailer.getId()!=null){
			    boolean b = recycleRetailerService.delete(recycleRetailer.getId());
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
	 * @param recycleRetailer 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody RecycleRetailer recycleRetailer, HttpServletRequest request){
		try {
			RecycleRetailer existByVendor=null;
			RecycleRetailer existByRetailerShort=null;
			if(recycleRetailer.getRetailer()!=null){
				
				String retailer=recycleRetailer.getRetailer().trim();
				long mechanismId=recycleRetailer.getOrgId();
				existByVendor = recycleRetailerService.existByRetailer(mechanismId,retailer);
				if(existByVendor!=null&& !recycleRetailer.getId().equals(existByVendor.getId())){
					return ResultDTO.getFailure(500, "客户名称重复无法保存！");
				}
			}
			if(recycleRetailer.getRetailerShort()!=null){
				
				String retailerShort=recycleRetailer.getRetailerShort().trim();
				long mechanismId=recycleRetailer.getOrgId();
				existByRetailerShort = recycleRetailerService.existByRetailerShort(mechanismId,retailerShort);
				if(existByRetailerShort!=null && !recycleRetailer.getId().equals(existByRetailerShort.getId())){
					
					return ResultDTO.getFailure(500, "客户简称重复无法保存！");
				}
			}
			if(recycleRetailer != null){
				Long id = UUIDUtil.getUUNum();
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				recycleRetailer.setUpdateMen(user.getName());
				Date date=new Date();
				recycleRetailer.setUpdateTime(date);
				
				if(recycleRetailer.getWeburls()!=null && recycleRetailer.getWeburls().length>0){
					RecycleRetailerAccurl recycleRetailerAccurl =new RecycleRetailerAccurl();
					String size = recycleRetailer.getWeburls()[0];
					String name = recycleRetailer.getWeburls()[1];
					String url = recycleRetailer.getWeburls()[2];
					
					recycleRetailerAccurl.setName(name);
					recycleRetailerAccurl.setUrl(url);
					if(recycleRetailer.getAccurlId()!=null){
						recycleRetailerAccurl.setRetailerId(recycleRetailer.getAccurlId());
					}
					if(recycleRetailer.getAccurlId()==null){
						recycleRetailerAccurl.setRetailerId(id);
					}
					
					Long of = Long.valueOf(size);
					
					recycleRetailerAccurl.setFileSize(of);
				
					recycleRetailerAccurlService.save(recycleRetailerAccurl);
					
					recycleRetailer.setAccessory("有");
				}
				
				boolean b = recycleRetailerService.update(recycleRetailer);
				if(b){
					return ResultDTO.getSuccess(200,"修改成功！","修改成功！");
				}else{
					return ResultDTO.getFailure(500,"修改失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (RuntimeException e) {
			return ResultDTO.getFailure(500, "请正确输入客户名称和简称!");
		}catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "请正确输入客户名称和简称!");
		}	
		
	}

	
	/**
     * 导出
     * @param response缺
     * @param pager
	 * @throws Exception 
     */
    @RequestMapping("/export")
    public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleRetailer> pager) throws Exception {
    	
    	convertPrams(pager);
        pager.setPageSize(10000000);
        LarPager<RecycleRetailer> larPager = recycleRetailerService.findByOrgIds(pager,null);
        if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
            ExportExcelUtils<RecycleRetailer> exportExcelUtils = new ExportExcelUtils<>("销售客户管理");
            Workbook workbook = null;
            try {
            	 workbook = exportExcelUtils.writeContents("销售客户管理", this.convert(larPager.getResult()));
                String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
                String headStr = "attachment; filename=\"" + fileName + "\"";
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                OutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    //文件下载
  	@RequestMapping(value = "/fileDown",method=RequestMethod.POST)
  	@ResponseBody
  	public void fileDown(@RequestBody(required = false) LarPager<RecycleRetailer> pager, HttpServletRequest request, HttpServletResponse response){
  		try {
  			FileOperationsUtils.fileDown(pager.getParams(),request, response);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  	}
    
    private List<RecycleRetailer> convert(List<RecycleRetailer> list) throws Exception {
       for (RecycleRetailer recycleRetailer : list) {
    	   recycleRetailer.getFujisn();
	}
        return list;
    }
    
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
	
	
}
