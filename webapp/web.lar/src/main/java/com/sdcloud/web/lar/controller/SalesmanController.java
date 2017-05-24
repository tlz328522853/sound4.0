package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.ItemPointStatistics;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.SalesmanIntegralExport;
import com.sdcloud.api.lar.entity.SalesmanPointsStandingExport;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/salesman")
public class SalesmanController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SalesmanService salesmanService;

	// 查询列表
	@RequestMapping("/getSalesmans")
	@ResponseBody
	public ResultDTO getSalesmans(@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
		try {
			this.convertPrams(larPager);// 增加是否包含了机构
			larPager = salesmanService.selectByExample(larPager);
			larPager.setResult(this.convertExport(larPager.getResult()));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 用于做参数的转变,是不包含子机构
	private void convertPrams(LarPager<? extends Object> larPager) throws Exception {
		if (null != larPager.getParams().get("includeSub")) {
			// 添加查询子机构功能
			boolean includeSub = (boolean) larPager.getParams().get("includeSub");
			if (includeSub) {
				String orgStr = larPager.getParams().get("mechanismId").toString();
				String[] split = orgStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。		
				List<Long> orgIds = new ArrayList<>();
				for(String orgString :split){
					List<Org> list = orgService.findById(Long.valueOf(orgString), true);
					
					for (Org org : list) {
						//if(new Integer(1).equals(org.getIsSubCompany()))
						orgIds.add(org.getOrgId());
					}
				}
				
				larPager.getParams().remove("mechanismId");
				larPager.getParams().put("orgIds", orgIds);
			}
			larPager.getParams().remove("includeSub");
		}
	}
	
	// 用于做参数的转变,是不包含子机构
		private void convertPrams2(LarPager<? extends Object> larPager) throws Exception {
			if (null != larPager.getParams().get("includeSub")) {
				// 添加查询子机构功能
				boolean includeSub = (boolean) larPager.getParams().get("includeSub");
				if (includeSub) {
					String orgStr = larPager.getParams().get("mechanismId").toString();
					String[] split = orgStr.split("AAA");//mechanismId 如果有多个机构 ，使用"AAA"来隔开。		
					List<Long> orgIds = new ArrayList<>();
					for(String orgString :split){
						List<Org> list = orgService.findById(Long.valueOf(orgString), true);
						
						for (Org org : list) {
							if(org.getIsSubCompany() == 1){
								orgIds.add(org.getOrgId());
							}
							
						}
					}
					
					larPager.getParams().remove("mechanismId");
					larPager.getParams().put("orgIds", orgIds);
				}
				larPager.getParams().remove("includeSub");
			}
		}

	// 业务员积分统计
	@RequestMapping("/salesManPointStatistics")
	@ResponseBody
	public ResultDTO salesManPointStatistics(@RequestBody(required = false) LarPager<Salesman> larPager)
			throws Exception {
		try {

			this.convertPrams(larPager);// 增加是否包含了机构
			larPager = salesmanService.salesManPointStatistics(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 公司积分统计
	@RequestMapping("/itemPointStatistics")
	@ResponseBody
	public ResultDTO itemPointStatistics(@RequestBody(required = false) LarPager<ItemPointStatistics> larPager)
			throws Exception {
		try {
			this.convertPrams2(larPager);
			larPager = salesmanService.itemPointStatistics(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 查询列表
	@RequestMapping("/findByOrg")
	@ResponseBody
	public ResultDTO findByOrg(@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
		try {
			larPager = salesmanService.selectByExample(larPager);
			List<Salesman> result = larPager.getResult();
			if (CollectionUtils.isNotEmpty(result)) {
				convert(result);
			}
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	private void convert(List<Salesman> result) throws Exception {
		List<Long> employeeIds = new ArrayList<>();
		for (Salesman salesman : result) {
			if (null != salesman.getPersonnelId()) {
				employeeIds.add(Long.valueOf(salesman.getPersonnelId()));
			}
		}

		Map<String, Employee> map = new HashMap<>();
		if (CollectionUtils.isNotEmpty(employeeIds)) {
			List<Employee> list = employeeService.findById(employeeIds);
			for (Employee employee : list) {
				map.put(employee.getEmployeeId() + "", employee);
			}
		}
		if (!map.isEmpty()) {
			for (Salesman salesman : result) {
				Employee emp = map.get(salesman.getPersonnelId());
				if (null == emp) {
					continue;
				}
				String device = emp == null ? null : emp.getMobileMac();
				salesman.setMobileMac(device);
				salesman.setJobTitle(emp.getJobTitle());
			}
		}
	}

	 /**
     * 根据机构id获取本机构及子机构的员工数据
     *
     * @param larPager
     * @return
     */
		@RequestMapping("/findEmpByOrg")
		@ResponseBody
		public ResultDTO findEmpByOrg(@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
			 try {
		        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
							.getStackTrace()[1].getMethodName(),larPager);
		        	Map<String, Object> empParam = new HashMap<>();
		        
		            Map<String, Object> param = larPager.getParams();
		            //备份参数
		            Set<Entry<String,Object>> set = param.entrySet();
		            for (Entry<String,Object> entry : set) {
						empParam.put(entry.getKey(), entry.getValue());
					}
		            List<Long> ids = new ArrayList<>();
		            if (!param.isEmpty()) {
		                Long id = Long.valueOf(param.get("mechanismId") + "");
		                Boolean includeSub = Boolean.valueOf(param.get("includeSub") + "");
		                
		                List<Org> list = orgService.findById(id, includeSub);
		                for (Org org : list) {
		                    ids.add(org.getOrgId());
		                }
		                param.clear();
		            }
		            LarPager<Salesman> pager = salesmanService.findEmploy(larPager, ids);
		            if (null != pager && pager.getResult() != null && pager.getResult().size() > 0) {
		                List<Employee> emps = this.convertToEmp(pager.getResult(),empParam);
		                Field field = pager.getClass().getDeclaredField("result");
		                field.setAccessible(true);
		                field.set(pager, emps);
		                pager.setTotalCount(emps.size());
		            }
		            pager.setParams(empParam);
		            return ResultDTO.getSuccess(200, pager);
		        } catch (Exception e) {
		        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
							.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
		            return ResultDTO.getFailure(500, "服务器错误！");
		        }
		}

		 private List<Employee> convertToEmp(List<Salesman> result,Map<String, Object> param) throws Exception {
				List<Long> list = new ArrayList<>();
		    	for (Salesman so : result) {
					if(null != so.getPersonnelId()){
						list.add(Long.valueOf(so.getPersonnelId()));
					}
				}
		    	List<Employee> empList = new ArrayList<>();
		    	List<Employee> emps = employeeService.findById(list);
		    	
		    	Object name = param.get("name");
				Object mobileMac = param.get("mobileMac");
		    	if(null == name && null == mobileMac){
		    		return emps;
		    	}
		    	
		    	for (int i = 0; i < emps.size(); i++) {
		    		Employee emp = emps.get(i);
					if( (null != name && null == mobileMac && null != emp.getName())
							&& emp.getName().contains(name+"") && !empList.contains(emp)){
						empList.add(emp);
						continue;
					}
					if(null != mobileMac && null == name && (mobileMac+"").equals(emp.getMobileMac()) 
							&& !empList.contains(emp)){
						empList.add(emp);
						continue;
					}
					if(null != mobileMac && null != name && (mobileMac+"").equals(emp.getMobileMac()) 
							&& emp.getName().contains(name+"") && !empList.contains(emp)){
						empList.add(emp);
					}
				}
				return empList;
			}
	
   
	// 查询列表
	@RequestMapping("/getSalesmansByConditron")
	@ResponseBody
	public ResultDTO getSalesmansByConditron(@RequestBody(required = false) LarPager<Salesman> larPager)
			throws Exception {
		try {
			this.convertPrams(larPager);// 增加是否包含了机构
			larPager = salesmanService.getSalesmansByConditron(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 根据机构获取业务员列表
	@RequestMapping("/getareaNamesByOId/{id}")
	@ResponseBody
	public ResultDTO getareaNamesByOId(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<Salesman> salesmans = salesmanService.getareaNamesByOId(id);
				if (salesmans != null && salesmans.size() > 0) {
					return ResultDTO.getSuccess(salesmans);
				} else {
					return ResultDTO.getFailure(500, "该机构没有业务员！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据机构ID查询供应商列表
	@RequestMapping("/getOwnedSuppliersById/{id}")
	@ResponseBody
	public ResultDTO getOwnedSuppliersById(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<OwnedSupplier> ownedSuppliers = salesmanService.getOwnedSuppliersById(id);
				if (ownedSuppliers != null && ownedSuppliers.size() > 0) {
					return ResultDTO.getSuccess(ownedSuppliers);
				} else {
					return ResultDTO.getFailure(500, "该机构没有供货商！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/saveSalesman", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) Salesman salesman) throws Exception {
		LarPager<Salesman> result = null;
		try {
			if (salesman != null && (salesman.getId() == null || salesman.getId().length() <= 0)) {
				boolean insertUserGetId = salesmanService.insertSelective(salesman);
				if (insertUserGetId) {
					result = new LarPager<Salesman>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", salesman.getAreaSetting().getMechanismId());
					result.setParams(params);
					result.setPageSize(20);
					result = salesmanService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "业务员添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/deleteSalesman/{id}/{mechanismId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id,
			@PathVariable(value = "mechanismId") String mechanismId) throws Exception {
		LarPager<Salesman> result = null;
		try {
			if (id != null && id.trim().length() > 0 && mechanismId != null && mechanismId.trim().length() > 0) {
				boolean deleteById = salesmanService.deleteById(id);
				if (deleteById) {
					result = new LarPager<Salesman>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					result.setParams(params);
					result.setPageSize(20);
					result = salesmanService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "人员删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/updateSalesman", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) Salesman salesman) throws Exception {
		LarPager<Salesman> result = null;
		try {
			if (salesman != null && salesman.getId() != null && salesman.getId().trim().length() > 0) {
				boolean updateByExampleSelective = salesmanService.updateByExampleSelective(salesman);
				if (updateByExampleSelective) {
					result = new LarPager<Salesman>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", salesman.getAreaSetting().getMechanismId());
					result.setParams(params);
					result.setPageSize(20);
					result = salesmanService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "人员修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 根据用户ID修改
	@RequestMapping(value = "/updateByEmployeeId", method = RequestMethod.POST)
	public ResultDTO updateByEmployeeId(@RequestBody(required = false) Salesman salesman) throws Exception {
		try {
			if (salesman != null && salesman.getPersonnelId() != null
					&& salesman.getPersonnelId().trim().length() > 0) {
				int count = salesmanService.updateByEmployeeId(salesman);
				if (count > 0) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "成功修改" + count + "条数据");
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "修改失败！");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}

	// 导出
	@RequestMapping("/export")
	public void export(HttpServletResponse response, @RequestBody(required = false) LarPager<Salesman> pager) {
		pager.setPageSize(1000000);
		LarPager<Salesman> orderTimeLarPager = null;
		try {
			this.convertPrams(pager);
			orderTimeLarPager = salesmanService.selectByExample(pager);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<Salesman> exportExcelUtils = new ExportExcelUtils<>("操作点设置");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("业务员设置", this.convertExport(orderTimeLarPager.getResult()));
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

	// 用于导出的转换
	private List<Salesman> convertExport(List<Salesman> result) throws Exception {
		if (null != result && result.size() > 0) {
			List<Long> empIds = new ArrayList<>();
			for (Salesman salesman : result) {
				if (null != salesman.getAreaSetting()) {
					salesman.setOrgName(salesman.getAreaSetting().getMechanism());
					salesman.setAreaName(salesman.getAreaSetting().getAreaName());
				}
				if (!StringUtils.isEmpty(salesman.getPersonnelId())) {
					empIds.add(Long.parseLong(salesman.getPersonnelId()));
				}
			}
			List<Employee> emps = employeeService.findById(empIds);
			for (Salesman salesman : result) {
				for (Employee emp : emps) {
					if (!StringUtils.isEmpty(salesman.getPersonnelId()) && emp.getEmployeeId() == Long.parseLong(salesman.getPersonnelId())) {
						salesman.setEmpName(emp.getName());
					}
				}
			}
		}
		return result;
	}

	// 公司积分统计
	@RequestMapping("/exportPointStatistics")
	@ResponseBody
	public void exportPointStatistics(HttpServletResponse response,
			@RequestBody(required = false) LarPager<ItemPointStatistics> larPager) throws Exception {
		try {
			larPager.setPageSize(1000000);
			this.convertPrams2(larPager);
			larPager = salesmanService.itemPointStatistics(larPager);
			List<ItemPointStatistics> result = larPager.getResult();
			if(result !=null && result.size()>0){
				for(ItemPointStatistics  item :result){
					if(null ==item.getMallPointInputSum()){
						item.setMallPointInputSum(0L);
					}
					if(null == item.getSanitationPointOutSum()){
						item.setSanitationPointOutSum(0L);
					}
					if(null == item.getRecyclePointOutSum()){
						item.setRecyclePointOutSum(0L);
					}
					item.setItemPointDifferenceSum((item.getSanitationPointOutSum()+item.getRecyclePointOutSum())-item.getMallPointInputSum());
				}
			}
		} catch (Exception e) {
			throw e;
		}

		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<ItemPointStatistics> exportExcelUtils = new ExportExcelUtils<>("公司积分统计");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("公司积分统计",
						this.convertExportPointStatistics(larPager.getResult()));
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

	@Autowired
	private OrgService orgService;

	// 导出公司积分统计 的转换
	private List<ItemPointStatistics> convertExportPointStatistics(List<ItemPointStatistics> result) throws Exception {
		for (ItemPointStatistics item : result) {
			List<Org> orgs = orgService.findById(item.getMechanismId(), false);
			if (null == item.getMallPointInputSum())
				item.setMallPointInputSum(0L);
			if (null == item.getSanitationPointOutSum())
				item.setSanitationPointOutSum(0L);
			if (null == item.getRecyclePointOutSum())
				item.setRecyclePointOutSum(0L);
			if (null != orgs && orgs.size() > 0)
				item.setOrgName(orgs.get(0).getName());
			item.setItemPointDifferenceSum((item.getSanitationPointOutSum()+item.getRecyclePointOutSum()) - item.getMallPointInputSum());
		}
		return result;
	}

	// 业务员积分统计导出
	@RequestMapping("/integralStatisticsExport ")
	@ResponseBody
	public void integralStatisticsExport(HttpServletResponse response,
			@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
		try {
			larPager.setPageSize(1000000);
			this.convertPrams(larPager);
			larPager = salesmanService.getSalesmansByConditron(larPager);
		} catch (Exception e) {
			throw e;
		}
		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<SalesmanIntegralExport> exportExcelUtils = new ExportExcelUtils<>("业务员积分统计");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("业务员积分统计",
						this.convertExportIntegralStatistics(larPager.getResult()));
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

	// 用于业务员积分统计的转换
	private List<SalesmanIntegralExport> convertExportIntegralStatistics(List<Salesman> result) throws Exception {
		result = this.convertExport(result);
		List<SalesmanIntegralExport> integrals = new ArrayList<>();
		for (Salesman man : result) {
			integrals.add(new SalesmanIntegralExport(man.getManId(), man.getManName(), man.getOrgName(),
					man.getAreaName(), man.getAccountSum(), man.getRechargeIntegral(), man.getExpenditureIntegral(),
					man.getCount()));
		}
		return integrals;
	}

	// pointsStandingExport 导出积分台账
	@RequestMapping("/pointsStandingExport")
	@ResponseBody
	public void pointsStandingExport(HttpServletResponse response,
			@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
		try {
			larPager.setPageSize(1000000);
			this.convertPrams(larPager);// 增加是否包含了机构
			larPager = salesmanService.getSalesmansByConditron(larPager);
		} catch (Exception e) {
			throw e;
		}
		if (null != larPager && null != larPager.getResult() && larPager.getResult().size() > 0) {
			ExportExcelUtils<SalesmanPointsStandingExport> exportExcelUtils = new ExportExcelUtils<>("业务员积分台账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("业务员积分台账",
						this.convertExportPointsStanding(larPager.getResult()));
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

	// 用于业务员积分台账的转换
	private List<SalesmanPointsStandingExport> convertExportPointsStanding(List<Salesman> result) throws Exception {
		result = this.convertExport(result);
		List<SalesmanPointsStandingExport> integrals = new ArrayList<>();
		for (Salesman man : result) {
			integrals.add(new SalesmanPointsStandingExport(man.getManId(), man.getManName(), man.getOrgName(),
					man.getAreaName(), man.getAccountSum(), man.getRechargeIntegral(), man.getGiveSum(),
					man.getExpenditureIntegral(), man.getIntegral()));
		}
		return integrals;
	}
	
	//根据员工的Id获取业务员积分,返回该员工下业务员积分最多积分.
	@RequestMapping("/getSalesScoreByEmp/{empId}")
	public ResultDTO getSalesScoreByEmp(@PathVariable("empId") Long empId){
		if(empId>0)
			return ResultDTO.getSuccess(salesmanService.getSalesScoreByEmp(empId));
		return ResultDTO.getFailure(500, "参数有误！");
	}
	
}