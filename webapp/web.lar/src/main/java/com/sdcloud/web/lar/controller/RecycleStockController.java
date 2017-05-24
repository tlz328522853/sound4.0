package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.RecycleBag;
import com.sdcloud.api.lar.entity.RecycleStock;
import com.sdcloud.api.lar.entity.Score;
import com.sdcloud.api.lar.service.RecycleStockService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;

import io.jsonwebtoken.lang.Collections;

/**
 * lar_recycle_stock 
 * @author luorongjie
 * @date 2016-12-02
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleStock")
public class RecycleStockController{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecycleStockService recycleStockService;
	@Autowired 
	private OrgService orgService;

	/**
	 * 根据机构查询所有
	 * @param pager
	 * @return
	 */
	@RequestMapping(value="/findByOrgIds",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByOrgIds(@RequestBody  LarPager<RecycleStock> pager){
		try {
			if(pager != null){
				Map<String, Object> map = pager.getExtendMap();
	            Object orgObj = map.get("org");
	            Object includeSubObj = map.get("includeSub");
	            if(null ==orgObj )
	            	return ResultDTO.getFailure("参数有误！");
	            List<Long> orgList = getOrgList(orgObj, includeSubObj);
	            recycleStockService.findByOrgIds(pager, orgList);          
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
	
	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleStock> pager) {
		List<RecycleStock> stocks = null;
		Long orgId = null;
		try {
			pager.setPageSize(1000000);
			Map<String, Object> map = pager.getExtendMap();
            Object orgObj = map.get("org");
            Object includeSubObj = map.get("includeSub");
            List<Long> orgList = getOrgList(orgObj, includeSubObj);
            recycleStockService.findByOrgIds(pager, orgList);          
            stocks = pager.getResult();
            if(null !=orgObj){
            	if(orgObj.toString().contains("AAA")){
            		orgId = Long .valueOf(orgObj.toString().split("AAA")[0]);
            	}else{
            		orgId=Long.valueOf(orgObj.toString());
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		if (!Collections.isEmpty(stocks)) {
			ExportExcelUtils<RecycleStock> exportExcelUtils = new ExportExcelUtils<>("库存台账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("库存台账", this.convert(stocks,orgId));
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
	private List<RecycleStock> convert(List<RecycleStock> stocks,Long orgId)throws Exception {
		List<Long> orgList = new ArrayList<>();
		orgList.add(orgId);
        for (RecycleStock stock : stocks) {
            if (null != stock.getOrgId()) {
                orgList.add(Long.valueOf(stock.getOrgId()));
            }
        }
        Map<Long, Org> orgs = null;
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        for (RecycleStock stock : stocks) {
        	if(null !=orgs){
        		if(null != stock.getOrgId()){
        			Org org = orgs.get(Long.valueOf(stock.getOrgId()));
                	if(null != org){
                		stock.setOrgName(org.getName());
                	}
        		}else{
        			Org org = orgs.get(orgId);
                	if(null != org){
                		stock.setOrgName(org.getName());
                	}
        		}	
        	}
        }
        return stocks;
	}
}
