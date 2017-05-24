package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.fabric.xmlrpc.base.Array;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.RecycleDetail;
import com.sdcloud.api.lar.entity.RecycleVendor;
import com.sdcloud.api.lar.entity.RecycleVendorAccurl;
import com.sdcloud.api.lar.entity.ShipExportRecord;
import com.sdcloud.api.lar.entity.ShipmentExpress;
import com.sdcloud.api.lar.service.RecycleVendorAccurlService;
import com.sdcloud.api.lar.service.RecycleVendorService;
import com.sdcloud.api.lar.util.ExportReadInfo;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.Pager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.FileOperationsUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import com.sdcloud.web.lar.util.SardineUtil;

/**
 * lar_recycle_vendor 供应商
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleVendor")
public class RecycleVendorController{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RecycleVendorService recycleVendorService;
	@Autowired 
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private RecycleVendorAccurlService recycleVendorAccurlService;

		
	// 上传文件
	@RequestMapping(value = "/fileUploader")
	@ResponseBody
	public ResultDTO UserIconUpload(@RequestParam("file") MultipartFile[] multipartFiles,HttpServletRequest request)throws Exception {
		String webUrl = null;
		String filename =null;
		String path = null;
		//创建sardine
		for (MultipartFile file : multipartFiles) {
			try {
				InputStream is = null;
				if (!file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();
				
				filename = originalFilename;
				
				String dateFileName=getDatePath(new Date());
				long size = file.getSize();
				if(size>10485760){
					return null;
				}
				path = size + "/";
				is = file.getInputStream();
				
				webUrl=FileOperationsUtils.fileIsUpload(is,filename);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
	    }
		String SizeName=path+filename;
		return ResultDTO.getSuccess(200,webUrl,SizeName);
	}
	private String getDatePath(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String strDate = simpleDateFormat.format(date);
		return strDate;
	}
	// 提供供应商的接口
	@RequestMapping("/getRecycleVendor")
	@ResponseBody
	public  ResultDTO getRecycleVendor(@RequestBody(required = false) LarPager<RecycleVendor> larPager) throws Exception {
		try {
			
			this.convertPrams(larPager);
			larPager = recycleVendorService.findAll(larPager);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<RecycleVendor> larPager) throws Exception {
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
	public  ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<RecycleVendor> larPager) throws Exception {
		try {
			
			this.convertPrams(larPager);
			larPager = recycleVendorService.findByOrgIds(larPager,null);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	/**
	 * 
	 * @param orgObj
	 *            机构 id 多个以“AAA”拼接
	 * @param includeSubObj
	 *            是否包含子机构
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj, Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if (null != includeSubObj) {
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}

		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for (String orgString : orgArr) {
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds;
	}

	
	/**
	 * 添加数据
	 * @param recycleVendor 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecycleVendor recycleVendor,HttpServletRequest request){
		try {
			RecycleVendor existByVendorShort =null;
			RecycleVendor existByVendor=null;
			if(recycleVendor.getVendor()!=null){
				
				String vendor=recycleVendor.getVendor().trim();
				long mechanismId=recycleVendor.getOrgId();
				existByVendor = recycleVendorService.existByVendor(mechanismId,vendor);
				
				if(existByVendor!=null){
					
					return ResultDTO.getFailure(500, "供应商名称重复无法保存！");
				}
			}
			if(recycleVendor.getVendorShort()!=null){
				
				String getVendorShort=recycleVendor.getVendorShort().trim();
				long mechanismId=recycleVendor.getOrgId();
				existByVendorShort = recycleVendorService.existByVendorShort(mechanismId,getVendorShort);
				
				if(existByVendorShort!=null){
					if(getVendorShort.equals(existByVendorShort.getVendorShort())){
						return ResultDTO.getFailure(500, "供应商简称重复无法保存！");
					}
					return ResultDTO.getFailure(500, "供应商简称重复无法保存！");
				}
			}
			if(recycleVendor != null){
				Long id = UUIDUtil.getUUNum();
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				recycleVendor.setCreateMen(user.getName());
				recycleVendor.setAccurlId(id);
				recycleVendor.setId(id);
				Date date=new Date();
				
				recycleVendor.setCreateTime(date);
				recycleVendor.setUpdateMen(user.getName());
				recycleVendor.setUpdateTime(date);
				if(recycleVendor.getWeburls()!=null &&recycleVendor.getWeburls().length >0){
					RecycleVendorAccurl recycleVendorAccurl =new RecycleVendorAccurl();
					String size = recycleVendor.getWeburls()[0];
					String name = recycleVendor.getWeburls()[1];
					String url = recycleVendor.getWeburls()[2];
					
					recycleVendorAccurl.setName(name);
					recycleVendorAccurl.setUrl(url);
					if(recycleVendor.getAccurlId()!=null){
						recycleVendorAccurl.setVendorId(recycleVendor.getAccurlId());
					}
					if(recycleVendor.getAccurlId()==null){
						recycleVendorAccurl.setVendorId(id);
					}
					
					Long of = Long.valueOf(size);
					
					recycleVendorAccurl.setFileSize(of);
				
				    recycleVendorAccurlService.save(recycleVendorAccurl);
					
					recycleVendor.setAccessory("有");
				}
				
				
				boolean b = recycleVendorService.save(recycleVendor);
				
				if(b){
					return ResultDTO.getSuccess(null,"添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求！");
		} catch (Exception e) {
				logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(400, "非法请求！");
		}
	}
	
		
	/**
	 * 根据主键ID 删除数据
	 * @param recycleVendor 删除数据
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO deleteByPrimary(@RequestBody RecycleVendor recycleVendor){
		try {
			if(recycleVendor != null && recycleVendor.getId()!=null){
			    boolean b = recycleVendorService.delete(recycleVendor.getId());
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
	 * 启停用
	 * @param recycleVendor 
	 * @return
	 */
	@RequestMapping(value="/updateEnable")
	@ResponseBody
	public ResultDTO updateEnable(@RequestBody  RecycleVendor recycleVendor){
		try {
			if(recycleVendor.getEnable()==0){
				recycleVendor.setEnable((int)1);
			}else{
				recycleVendor.setEnable((int)0);
			}
			return ResultDTO.getSuccess(200, recycleVendorService.update(recycleVendor));
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}
	
	
	
	/**
	 * 更新数据
	 * @param recycleVendor 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody RecycleVendor recycleVendor,HttpServletRequest request){
		try {
			RecycleVendor existByVendorShort =null;
			RecycleVendor existByVendor =null;
			if(recycleVendor.getVendor()!=null){
				
				String vendor=recycleVendor.getVendor().trim();
				long mechanismId=recycleVendor.getOrgId();
				existByVendor = recycleVendorService.existByVendor(mechanismId,vendor);
				
				if(existByVendor!=null&& !recycleVendor.getId().equals(existByVendor.getId())){
					return ResultDTO.getFailure(500, "供应商名称重复无法保存！");
				}
			}
			if(recycleVendor.getVendorShort()!=null){
				
				String getVendorShort=recycleVendor.getVendorShort().trim();
				long mechanismId=recycleVendor.getOrgId();
				existByVendorShort = recycleVendorService.existByVendorShort(mechanismId,getVendorShort);
				if(existByVendorShort!=null && !recycleVendor.getId().equals(existByVendorShort.getId())){
					return ResultDTO.getFailure(500, "供应商简称重复无法保存！");
				}
			}
			if(recycleVendor != null){
				Long id = UUIDUtil.getUUNum();
				// 获得当前登录用户
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				recycleVendor.setUpdateMen(user.getName());
				Date date =new Date();
				recycleVendor.setUpdateTime(date);
				
				if(recycleVendor.getWeburls()!=null && recycleVendor.getWeburls().length > 0){
					RecycleVendorAccurl recycleVendorAccurl =new RecycleVendorAccurl();
					String size = recycleVendor.getWeburls()[0];
					String name = recycleVendor.getWeburls()[1];
					String url = recycleVendor.getWeburls()[2];
					
					recycleVendorAccurl.setName(name);
					recycleVendorAccurl.setUrl(url);
					if(recycleVendor.getAccurlId()!=null){
						recycleVendorAccurl.setVendorId(recycleVendor.getAccurlId());
					}
					if(recycleVendor.getAccurlId()==null){
						recycleVendorAccurl.setVendorId(id);
					}
					
					Long of = Long.valueOf(size);
					
					recycleVendorAccurl.setFileSize(of);
			
						
					recycleVendorAccurlService.save(recycleVendorAccurl);
					
				}
				
				boolean b = recycleVendorService.update(recycleVendor);
				if(b){
					return ResultDTO.getSuccess(200,"修改成功！","修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		}catch (RuntimeException e) {
			return ResultDTO.getFailure(500, "请正确输入供应商名称和简称!");
		}catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "请正确输入供应商名称和简称!");
		}	
		
	}

	/**
     * 导出
     * @param response缺
     * @param pager
     */
    @RequestMapping("/export")
    public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleVendor> pager) {
    		Map<String, Object> map = pager.getExtendMap();
    		List<Long> ids = new ArrayList<>();
		    try {
				if (map != null && null != map.get("mechanismId")) {
				    Long id = Long.valueOf(map.get("mechanismId") + "");
				    Boolean isParentNode = Boolean.valueOf(map.get("includeSub") + "");
				    if (null != id) {
				        //是父节点再去查找
				        if (isParentNode) {
				            List<Org> list = orgService.findById(id, true);
				            for (Org org : list) {
				                ids.add(org.getOrgId());
				            }
				        } else {
				            Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
				            result.put("org", id);
				            pager.setParams(result);
				            ids = null;
				        }
				    }
				}
			convertPrams(pager);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		  
        pager.setPageSize(10000000);
        LarPager<RecycleVendor> larPager = recycleVendorService.findByOrgIds(pager,null);
        if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
            ExportExcelUtils<RecycleVendor> exportExcelUtils = new ExportExcelUtils<>("供应商管理");
            Workbook workbook = null;
            try {
            	 workbook = exportExcelUtils.writeContents("供应商管理", this.convert(larPager.getResult()));
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
    
  //导出时封装orgName
  		private List<RecycleVendor> convert(List<RecycleVendor> recycleDetail)throws Exception {
  			List<Long> orgList = new ArrayList<>();
  	        for (RecycleVendor outstock : recycleDetail) {
  	            if (null != outstock.getOrgId()) {
  	                orgList.add(Long.valueOf(outstock.getOrgId()));
  	            }
  	          
  	        }
  	        Map<Long, Org> orgs = null;
  	        if (orgList.size() > 0) {
  	        	orgs = orgService.findOrgMapByIds(orgList, false);
  	        }
  	        for (RecycleVendor outstock : recycleDetail) {
  	        	if(null !=orgs){
  	        		if(null != outstock.getOrgId()){
  	        			Org org = orgs.get(Long.valueOf(outstock.getOrgId()));
  	                	if(null != org){
  	                		outstock.setOrgName(org.getName());
  	                	}
  	        		}
  	        	}
  	        	outstock.getFujisn();
  	        }
  	        return recycleDetail;
  		}
    
   
    
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
	
  
}
