package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.RecycleDetail;
import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.service.RecycleDetailService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;

import io.jsonwebtoken.lang.Collections;

/**
 * 出入库明细
 * 
 */
@RestController
@RequestMapping(value = "/api/RecycleDetail")
public class RecycleDetailController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RecycleDetailService recycleDetailService;
	@Autowired 
	private OrgService orgService;
//	@Autowired
//	private RecycleOutstockService recycleOutstockService;
	
	/**
	 * 根据机构查询所有
	 * @param pager
	 * @return
	 */
	@RequestMapping(value="/findByOrgIds",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByOrgIds(@RequestBody  LarPager<RecycleDetail> pager){
		try {
			if(pager != null){
				Map<String, Object> map = pager.getExtendMap();
	            Object orgObj = map.get("org");
	            Object includeSubObj = map.get("includeSub");
	            if(null ==orgObj )
	            	return ResultDTO.getFailure("参数有误！");
	            List<Long> orgList = getOrgList(orgObj, includeSubObj);
	            //判断type与业务名称不一致问题
	            Integer type = map.get("type")==null?null:Integer.parseInt(map.get("type").toString());
	            if(type!=null){
	            	boolean retailerNameFlag = map.get("retailerName")==null?
	            			false:StringUtils.isNotEmpty(map.get("retailerName").toString());//type 1
	            	boolean vendorNameFlag = map.get("vendorName")==null?
	            			false:StringUtils.isNotEmpty(map.get("vendorName").toString());//type 2
	            	if((retailerNameFlag&&!vendorNameFlag&&type==2)
	            			||(!retailerNameFlag&&vendorNameFlag&&type==1)){
	            		pager.setResult(null);
	            		pager.setTotalCount(0);
	            		return ResultDTO.getSuccess(pager);
	            	}
	            }
	            
	            recycleDetailService.findByOrgIds(pager, orgList);          
	            return ResultDTO.getSuccess(pager);
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
	 * 
	 * @param orgObj 机构 id 多个以“AAA”拼接
	 * @param includeSubObj 是否包含子机构 
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj,Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if(null != includeSubObj){
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}
		
		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for(String orgString:orgArr){
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds ;
	}
	
	/**
	 * 根据id查询出库
	 * @param pager
	 * @return
	 */

	@RequestMapping("/getByIds")
	@ResponseBody
	public  ResultDTO getByIds(@RequestBody RecycleOutstock recycleOutstock) throws Exception {
		try {
			Long id = recycleOutstock.getId();
			if(id != null){
				RecycleOutstock recycleOut = recycleDetailService.getByIds(id);
			if(recycleOut != null){
				return ResultDTO.getSuccess(recycleOut);
				} else {
					return ResultDTO.getFailure(500, "非法请求，请重新尝试！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 根据id查询入库
	 * @param pager
	 * @return
	 */
	@RequestMapping("/getInByIds")
	@ResponseBody
	public  ResultDTO getInByIds(@RequestBody RecycleInstock recycleInstock) throws Exception {
		try {
			Long id = recycleInstock.getId();
			if(id != null){
				recycleInstock = recycleDetailService.getInByIds(id);
			if(recycleInstock != null){
				return ResultDTO.getSuccess(recycleInstock);
				} else {
					return ResultDTO.getFailure(500, "非法请求，请重新尝试！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 封账
	 * updateCheck
	 * /lar/api/RecycleDetail/updateCheck
	 * RecycleDetail
	 */
	@RequestMapping(value="/updateCheck")
	@ResponseBody
	public ResultDTO updateCheck(@RequestBody(required = false)  RecycleDetail recycleDetail){
		Boolean b = null;
		try {
				
			 b = recycleDetailService.updateCheck(recycleDetail.getUpdateAccount());
			if(b =true){
				return ResultDTO.getSuccess(200,"操作成功","操作成功");
			}else{
				return ResultDTO.getFailure(400, "参数错误!");
			}
			
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}
	
	
		/**
		 * 包含机构的查询条件
		 * @param pager
		 * @return
		 */
		// 查询列表
		@RequestMapping("/findAll")
		@ResponseBody
		public  ResultDTO findAll(@RequestBody(required = false) LarPager<RecycleDetail> larPager) throws Exception {
			try {
				
				this.convertPrams(larPager);
				larPager = recycleDetailService.findAll(larPager);
	
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			return ResultDTO.getSuccess(larPager);
		}

		//用于做参数的转变,是不包含子机构
		private void convertPrams(LarPager<RecycleDetail> larPager) throws Exception {
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
	
		/**
		 * 出库导出
		 * @param response
		 * @param pager
		 */
		@RequestMapping("/export")
		public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleDetail> pager) {
			List<RecycleDetail> recycleDetail = null;
			try {
				pager.setPageSize(1000000);
				Map<String, Object> map = pager.getExtendMap();
	            Object orgObj = map.get("org");
	            Object includeSubObj = map.get("includeSub");
	            List<Long> orgList = getOrgList(orgObj, includeSubObj);
	            recycleDetailService.findByOrgIds(pager, orgList);              
	            recycleDetail = pager.getResult();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!Collections.isEmpty(recycleDetail)) {
				ExportExcelUtils<RecycleDetail> exportExcelUtils = new ExportExcelUtils<>("出入库明细");
				Workbook workbook = null;
				try {
					workbook = exportExcelUtils.writeContents("出入库明细", this.convert(recycleDetail));
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
		private List<RecycleDetail> convert(List<RecycleDetail> recycleDetail)throws Exception {
			List<Long> orgList = new ArrayList<>();
	        for (RecycleDetail outstock : recycleDetail) {
	            if (null != outstock.getOrgId()) {
	                orgList.add(Long.valueOf(outstock.getOrgId()));
	            }
	            
	            if(outstock.getAuditStatus().equals((byte)2)){
	            	outstock.setAuditStatusName("已审核");
	            	
	            }
	            if(outstock.getAuditStatus().equals((byte)1)){
	            	outstock.setAuditStatusName("未审核");
	            	
	            }
	            if(outstock.getType().equals("1")){
	            	outstock.setType("出库");
	            	if(outstock.getStockStatus().equals("1")){
	            		outstock.setStockStatusName("未出库");
	            	}
	            	if(outstock.getStockStatus().equals("2")){
	            		outstock.setStockStatusName("已出库");
	            	}
	            }else{
	            	outstock.setType("入库");
	            	if(outstock.getStockStatus().equals("1")){
	            		outstock.setStockStatusName("未入库");
	            	}
	            	if(outstock.getStockStatus().equals("2")){
	            		outstock.setStockStatusName("已入库");
	            	}
	            }
	            
	            outstock.getSpecNo();
	        }
	        Map<Long, Org> orgs = null;
	        if (orgList.size() > 0) {
	        	orgs = orgService.findOrgMapByIds(orgList, false);
	        }
	        for (RecycleDetail outstock : recycleDetail) {
	        	if(null !=orgs){
	        		if(null != outstock.getOrgId()){
	        			Org org = orgs.get(Long.valueOf(outstock.getOrgId()));
	                	if(null != org){
	                		outstock.setOrgName(org.getName());
	                	}
	        		}
	        	}
	        }
	        return recycleDetail;
		}
	
}
