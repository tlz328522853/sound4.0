package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.ShipExportNotice;
import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.ShipExportNoticeService;
import com.sdcloud.api.lar.service.TakingExpressService;
import com.sdcloud.api.lar.util.DataValidUtl;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;

/**
 * 揽件
 * 
 * @author luorongjie
 *
 */
@RestController
@RequestMapping(value = "/api/takingExpress")
public class TakingExpressController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TakingExpressService takingExpressService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ShipExportNoticeService shipExportNoticeService;

	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public ResultDTO findAll(@RequestBody(required = false) LarPager<TakingExpress> larPager) {
		try {
			logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
			
			return ResultDTO.getSuccess(200, "保存成功!", takingExpressService.findAll(larPager));
		} catch (Exception e) {
			logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) TakingExpress takingExpress,HttpServletRequest request){
		try {
			logger.info("Enter the :{} method  takingExpress:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),takingExpress);

			TakingExpress taking = takingExpressService.getById(takingExpress.getId(), null);
			List<String> orderNos = new ArrayList<>();
			orderNos.add(takingExpress.getOrderNo());
			List<TakingExpress> orders = takingExpressService.getByOrderNo(orderNos);
			if(null != taking && !CollectionUtils.isEmpty(orders)){
				TakingExpress order = orders.get(0);
				if(takingExpress.getId().equals(taking.getId()) && !taking.getOrderNo().equals(takingExpress.getOrderNo())){
					return ResultDTO.getFailure(AppCode.BIZ_DATA,"订单号重复,请重新填写!");
				}
			}
			Boolean update = takingExpressService.update(takingExpress);
			if(update){
				List<TakingExpress> data = new ArrayList<>();
				data.add(taking);
				Map<String, ShipExportNotice> notices = mapAddNoticesDisExp(data);
				shipExportNoticeService.batchSave(new ArrayList<ShipExportNotice>(notices.values()));
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "修改成功", update);
		} catch (Exception e) {
			logger.error("method {} execute error, takingExpress:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), takingExpress, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	@RequestMapping("/findByOrgIds")
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<TakingExpress> larPager) {
		try {
			logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
			
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						List<Org> list = orgService.findById(id, true);
						larPager.getParams().remove("org");
						for (Org org : list) {
							ids.add(org.getOrgId());
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", id);
						larPager.setParams(result);
						ids = null;
					}
				}
			}
			LarPager<TakingExpress> pager = takingExpressService.findByOrgIds(larPager, ids);

			// 封装takingManName
			List<TakingExpress> takingExpresses = pager.getResult();
			if (takingExpresses != null && takingExpresses.size() > 0) {
				List<Long> takingManId = new ArrayList<>();
				for (TakingExpress takingExpress : takingExpresses) {
					takingManId.add(takingExpress.getTakingMan());
				}
				List<Employee> employees = employeeService.findById(takingManId);
				for (TakingExpress takingExpress : takingExpresses) {
					if(takingExpress.getTakingMan()==null){//没有揽收员
						break;
					}
					for (Employee employee : employees) {
						if (takingExpress.getTakingMan().equals(employee.getEmployeeId())) {
							takingExpress.setTakingManName(employee.getName());
							break;
						}
					}
				}
			}
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<TakingExpress> larPager ) {

		List<Long> ids =null;
		LarPager<TakingExpress> orderTimeLarPager = null;
		try {
			larPager.setPageSize(1000000);
			Map<String, Object> map = larPager.getExtendMap();
			 ids= new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						List<Org> list;
						list = orgService.findById(id, true);
						larPager.getParams().remove("org");
						for (Org org : list) {
							ids.add(org.getOrgId());
						}
					} else {
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", id);
						larPager.setParams(result);
						ids = null;
					}
				}
			}
		
			orderTimeLarPager = takingExpressService.findByOrgIds(larPager, ids);
			
			// 封装takingManName
			List<TakingExpress> takingExpresses = orderTimeLarPager.getResult();
			if (takingExpresses != null && takingExpresses.size() > 0) {
				List<Long> takingManId = new ArrayList<>();
				for (TakingExpress takingExpress : takingExpresses) {
					takingManId.add(takingExpress.getTakingMan());
				}
				List<Employee> employees = employeeService.findById(takingManId);
				for (TakingExpress takingExpress : takingExpresses) {
					for (Employee employee : employees) {
						if (takingExpress.getTakingMan().equals(employee.getEmployeeId())) {
							takingExpress.setTakingManName(employee.getName());
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<TakingExpress> exportExcelUtils = new ExportExcelUtils<>("揽件");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("揽件", this.convert(orderTimeLarPager.getResult()));
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
	//付款方式 1,寄付,2到付货款,3到付运费,4到付款(运费+货款)
	private String[] arr = {"","寄付","到付货款","到付运费","到付款(运费+货款)"};
	
	private List<TakingExpress> convert(List<TakingExpress> result) {
		for(TakingExpress takingExpress:result){
			LarClientUser custom = takingExpress.getCustom();
			if(null != custom){
				takingExpress.setCustomName(custom.getName());
				takingExpress.setCustomPhone(custom.getPhone());
				takingExpress.setCustomAddress(custom.getAddress());
				takingExpress.setCustomDetail(custom.getAddressDetail());
			}
			
			if(4>=takingExpress.getPayWay() && takingExpress.getPayWay()>=1){
				takingExpress.setPayWayString(arr[takingExpress.getPayWay()]);
			}
		}
		return result;
	}
	
	/**
	 * 添加数据到通知
	 * @param data
	 * @return
	 */
	private Map<String, ShipExportNotice> mapAddNoticesDisExp(List<TakingExpress> data) {
		Map<String, ShipExportNotice> notices = new HashMap<>();
		String curDate=DataValidUtl.parstDateToStr(new Date());
		for(TakingExpress te: data){
			String dateStr=DataValidUtl.parstDateToStr(te.getTakingDate());
			if(te.getTakingDate() != null && !curDate.equals(dateStr)){
				ShipExportNotice exportNotice=new ShipExportNotice();
				exportNotice.setId(UUIDUtil.getUUNum());
				exportNotice.setOrg(te.getOrg());
				exportNotice.setDataDate(dateStr);
				exportNotice.setUniqueNo(System.nanoTime()+"");//批次号
				exportNotice.setType((byte)0);
				notices.put(te.getOrg()+":"+dateStr, exportNotice);
			}
		}
		return notices;
	}
}
