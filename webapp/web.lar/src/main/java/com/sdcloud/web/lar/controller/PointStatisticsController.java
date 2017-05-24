package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.CompanyScoreRecordExport;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.ReqParams;
import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.service.IntegralConsumptionService;
import com.sdcloud.api.lar.service.PointStatisticsService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/pointStatistics")
public class PointStatisticsController {

	@Autowired
	private PointStatisticsService pointStatiticsService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgService orgService;

	@RequestMapping("/getPointConsumption")
	@ResponseBody
	public ResultDTO getPointConsumption(@RequestBody(required = false) LarPager<ExchangeInfo> larPager)
			throws Exception {
		try {
			this.convertPrams(larPager);
			larPager = pointStatiticsService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<ExchangeInfo> larPager) throws Exception {
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

	// 商城积分入账 导出
	@RequestMapping("/export")
	public void export(HttpServletResponse response, @RequestBody(required = false) LarPager<ExchangeInfo> pager) {
		try {
			pager.setPageSize(1000000);
			convertPrams(pager);
			pager = pointStatiticsService.selectByExample(pager);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
			ExportExcelUtils<CompanyScoreRecordExport> exportExcelUtils = new ExportExcelUtils<>("商城积分入账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("商城积分入账", this.convert(pager.getResult()));
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

	// 用于导出购买记录的转换
	private List<CompanyScoreRecordExport> convert(List<ExchangeInfo> result) throws Exception {
		List<Long> orgList = new ArrayList<>();
		for (ExchangeInfo info : result) {
			if (null != info.getMechanismId()) {
				orgList.add(Long.valueOf(info.getMechanismId()));
			}
		}
		Map<Long, Org> map = orgService.findOrgMapByIds(orgList, false);

		List<CompanyScoreRecordExport> exports = new ArrayList<>();
		for (ExchangeInfo info : result) {
			if (null != map) {
				Org org = map.get(Long.valueOf(info.getMechanismId()));
				if (null != org) {
					info.setOrgName(org.getName());
				}
			}
			LarClientUserAddress address = info.getLarClientUserAddress();
			if (null != address) {
				LarClientUser user = address.getLarClientUser();
				info.setUserName(address.getUserName());
				info.setAddress(address.getAddress());
				if (null != user) {
					info.setPhone(user.getPhone());
					info.setClientUserName(user.getName());
					info.setClientUserPhone(user.getPhone());
					info.setClientUserAddress(user.getAddress());
					info.setClientUserAddressDetail(user.getAddressDetail());
				}
			}
			exports.add(this.convertExport(info));
		}
		return exports;
	}

	// 将ExchangeInfo类转换成CompanyScoreRecordExport用于导出功能
	private CompanyScoreRecordExport convertExport(ExchangeInfo info) {
		return new CompanyScoreRecordExport(info.getOrgName(), null, info.getOrderId(), "商品兑换", info.getCreateDate(),
				info.getMaaDate(), info.getLoginUserName(), info.getConfirmDate(), info.getAppUserId(),
				info.getClientUserName(), info.getClientUserPhone(), info.getClientUserAddressDetail(), "积分",
				info.getIntegral(), info.getIntegral(), info.getIntegral());
	}
}