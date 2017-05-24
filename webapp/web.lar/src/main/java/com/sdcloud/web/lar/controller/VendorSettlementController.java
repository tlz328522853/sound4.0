package com.sdcloud.web.lar.controller;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
/**
 * createUser dingx
 * createDate 2016-12-09
 * function 供货商结算统计
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.GrossProfit;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.TransactionRecordExport;
import com.sdcloud.api.lar.entity.VendorSettlement;
import com.sdcloud.api.lar.service.VendorSettlementService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/vendorSettlement")
public class VendorSettlementController {
	
	@Autowired
	private VendorSettlementService vendorSettlementService;
	
	@Autowired
	private OrgService orgService;
	
	// 查询列表
	@RequestMapping("/getVendorSettlement")
	@ResponseBody
	public ResultDTO getVendorSettlement(@RequestBody(required = false) LarPager<VendorSettlement> larPager) throws Exception {
		try {
			this.convertPrams(larPager);//增加是否包含了机构
			larPager = vendorSettlementService.getVendorSettlement(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//用于做参数的转变,是不包含子机构
		private void convertPrams(LarPager<VendorSettlement> larPager) throws Exception {
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

	
		// 积分出账 _导出
		@RequestMapping("/vendorSettlementExport")
		@ResponseBody
		public void vendorSettlementExport(HttpServletResponse response,
				@RequestBody(required = false) LarPager<VendorSettlement> larPager) throws Exception {
			try {
				larPager.setPageSize(1000000);
				this.convertPrams(larPager);//是否包含子机构
				larPager = vendorSettlementService.getVendorSettlement(larPager);
			} catch (Exception e) {
				throw e;
			}
			if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
				ExportExcelUtils<VendorSettlement> exportExcelUtils = new ExportExcelUtils<>("供货商结算统计");
				Workbook workbook = null;
				try {
					workbook = exportExcelUtils.writeContents("供货商结算统计",this.convert(larPager.getResult()));
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
		
		// 用于导出供货商结算统计的转换
		private List<VendorSettlement> convert(List<VendorSettlement> result) throws Exception {
			List<Long> orgList = new ArrayList<>();
			for (VendorSettlement gp : result) {
				if (null != gp.getOrgId()) {
					orgList.add(Long.valueOf(gp.getOrgId()));
				}
			}
			Map<Long, Org> map = orgService.findOrgMapByIds(orgList, false);

			List<VendorSettlement> exports = new ArrayList<>();
			for (VendorSettlement gp : result) {
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
