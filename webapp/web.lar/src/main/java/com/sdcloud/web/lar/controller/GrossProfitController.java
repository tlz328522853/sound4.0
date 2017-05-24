package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.CompanyScoreRecordExport;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.GrossProfit;
import com.sdcloud.api.lar.entity.GrossProfitDetails;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.service.GrossProfitService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

/**
 * 
 * @author dingx
 * createDate 2016-12-14
 * function 毛利润统计
 *
 */
@RestController
@RequestMapping("/api/grossProfit")
public class GrossProfitController {

	@Autowired
	private GrossProfitService grossProfitService;
	
	@Autowired
	private OrgService orgService;
	
	// 毛利润明细统计
	@RequestMapping("/getGrossProfitDetail")
	@ResponseBody
	public ResultDTO getGrossProfitDetail(@RequestBody(required = false) LarPager<GrossProfitDetails> larPager) throws Exception {
		try {
			larPager.getParams().remove("orgIds");
			larPager = grossProfitService.getGrossProfitDetail(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	// 查询列表
	@RequestMapping("/getGrossProfit")
	@ResponseBody
	public ResultDTO getGrossProfit(@RequestBody(required = false) LarPager<GrossProfit> larPager) throws Exception {
		try {
			this.convertPrams(larPager);//增加是否包含了机构
			larPager = grossProfitService.getGrossProfit(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//用于做参数的转变,是不包含子机构
		private void convertPrams(LarPager<GrossProfit> larPager) throws Exception {
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

	
		// 毛利润统计 _导出
		@RequestMapping("/grossProfitExport")
		@ResponseBody
		public void grossProfitExport(HttpServletResponse response,
				@RequestBody(required = false) LarPager<GrossProfit> larPager) throws Exception {
			try {
				larPager.setPageSize(1000000);
				this.convertPrams(larPager);//是否包含子机构
				larPager = grossProfitService.getGrossProfit(larPager);
			} catch (Exception e) {
				throw e;
			}
			if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
				ExportExcelUtils<GrossProfit> exportExcelUtils = new ExportExcelUtils<>("毛利润统计");
				Workbook workbook = null;
				try {
					workbook = exportExcelUtils.writeContents("毛利润统计",this.convert(larPager.getResult()));
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
		
		//用于明细统计做参数的转变,是不包含子机构
		private void convertPramsDetail(LarPager<GrossProfitDetails> larPager) throws Exception {
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
		
		// 毛利润明细统计 _导出
		@RequestMapping("/grossProfitDetailsExport")
		@ResponseBody
		public void grossProfitDetailsExport(HttpServletResponse response,
				@RequestBody(required = false) LarPager<GrossProfitDetails> larPager) throws Exception {
			try {
				larPager.setPageSize(1000000);
				this.convertPramsDetail(larPager);//是否包含子机构
				larPager = grossProfitService.getGrossProfitDetail(larPager);
			} catch (Exception e) {
				throw e;
			}
			if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
				ExportExcelUtils<GrossProfitDetails> exportExcelUtils = new ExportExcelUtils<>("毛利润明细统计");
				Workbook workbook = null;
				try {
					workbook = exportExcelUtils.writeContents("毛利润明细统计",this.convertDetail(larPager.getResult()));
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
		
		// 用于导出毛利润明细统计的转换
				private List<GrossProfitDetails> convertDetail(List<GrossProfitDetails> result) throws Exception {
					List<Long> orgList = new ArrayList<>();
					for (GrossProfitDetails gpd : result) {
						if (null != gpd.getOrgId()) {
							orgList.add(Long.valueOf(gpd.getOrgId()));
						}
					}
					Map<Long, Org> map = orgService.findOrgMapByIds(orgList, false);

					List<GrossProfitDetails> exports = new ArrayList<>();
					for (GrossProfitDetails gpd : result) {
						if (null != map) {
							Org org = map.get(Long.valueOf(gpd.getOrgId()));
							if (null != org) {
								gpd.setOrgName(org.getName());
							}
						}
						
						exports.add(gpd);
					}
					return exports;
				}
		
		// 用于导出毛利润统计的转换
		private List<GrossProfit> convert(List<GrossProfit> result) throws Exception {
			List<Long> orgList = new ArrayList<>();
			for (GrossProfit gp : result) {
				if (null != gp.getOrgId()) {
					orgList.add(Long.valueOf(gp.getOrgId()));
				}
			}
			Map<Long, Org> map = orgService.findOrgMapByIds(orgList, false);

			List<GrossProfit> exports = new ArrayList<>();
			for (GrossProfit gp : result) {
				if (null != map) {
					Org org = map.get(Long.valueOf(gp.getOrgId()));
					if (null != org) {
						gp.setOrgName(org.getName());
					}
				}
				
				exports.add(gp);
			}
			return exports;
		}

}
