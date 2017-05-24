package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.HistoryRecord;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.biz.core.dao.EmployeeDao;
import com.sdcloud.biz.core.dao.HistoryRecordDao;
import com.sdcloud.biz.core.dao.OrgDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.constants.TransferTypeConstant;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author czz
 *
 */
@Service("employeeService")
public class EmployeeSerivceImpl implements EmployeeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OrgDao orgDao;
	
	@Autowired
	private OrgService orgService;
	@Autowired
	private HistoryRecordDao historyRecordDao;

	@Transactional
	public List<Employee> insert(List<Employee> employees) throws Exception {
		logger.info("start method: List<Employee> insert(List<Employee> employees), arg employees: " + employees);
		if (employees == null || employees.size() == 0) {
			logger.warn("employees is empty");
			throw new RuntimeException("employees is empty");
		}
		for (Employee e : employees) {
			long employeeId = -1;
			employeeId = UUIDUtil.getUUNum();
			e.setEmployeeId(employeeId);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		while (tryTime-- > 0) {
			try {
				employeeDao.insert(employees);
				break;
			} catch (Exception se) {
				logger.error("err method, Exception: " + se);
				if (tryTime == 0)
					throw new RuntimeException("向数据库插入记录失败，请检查");
				if (se instanceof DuplicateKeyException) {
					continue;
				}
			}
		}
		
		try {
			HistoryRecord historyRecord=new HistoryRecord();
			historyRecord.setRecordId(UUIDUtil.getUUNum());
			historyRecord.setEmployeeId(employees.get(0).getEmployeeId()+"");
			List<Org> org=orgService.findById(employees.get(0).getOrgId(), false);
			historyRecord.setOrgName(org.get(0).getName());
			historyRecord.setStartDate(employees.get(0).getEntryDate());
			if("2".equals(employees.get(0).getStatus())){
				historyRecord.setEndDate(employees.get(0).getDepartDate());
			}
			historyRecord.setName(employees.get(0).getName());
			if("1".equals(employees.get(0).getStatus())){
				historyRecord.setTransferType(TransferTypeConstant.ENTRY);
			}
			else if("2".equals(employees.get(0).getStatus())){
				historyRecord.setTransferType(TransferTypeConstant.QUIT);
			}
			historyRecord.setOperate(employees.get(0).getOperate());
			historyRecord.setOperateTime(new Date());
			//插入人员历史记录信息
			historyRecordDao.insert(historyRecord);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		logger.info("complete method, return employees: " + employees);
		return employees;
	}
/*	@Transactional
	public Employee insert(Employee employee) throws Exception {
		logger.info("start insert a new employee : " + employee);
		if (employee == null) {
			logger.warn("employee is null");
			throw new RuntimeException("employee is null");
		}
		long employeeId = -1;
		try {
			employeeId = UUIDUtil.getUUNum();
			employee.setEmployeeId(employeeId);
			
			employeeDao.insert(employee);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("新增失败");
		}
		
		logger.info("complete insert a new employee ： " + employee);
		return employee;
	}
*/
	@Transactional
	public void delete(List<Long> employeeIds) throws Exception {

		logger.info("start delete a employeeIds : " + employeeIds);
		if (employeeIds == null || employeeIds.size() == 0) {
			logger.warn("employeeIds is null or empty");
			throw new RuntimeException("employeeIds is null or empty");
		}

		try {
			logger.info("check if there is user bind");
			List<User> users = userDao.findByEmployee(employeeIds);
			if (users != null && users.size() > 0) {
				logger.warn("there is user bind to employeeIds ： " + employeeIds);
				throw new RuntimeException("存在员工被绑定到用户上，无法删除");
			}
			logger.info("deleting all : " + employeeIds);
			try {
				employeeDao.delete(employeeIds);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("删除失败");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete delete employeeIds ： " + employeeIds);
	}

	@Transactional
	public Employee updateInfo(Employee employee) throws Exception {
		logger.info("start updateInfo : " + employee);

		if (employee == null) {
			logger.warn("employee is null");
			throw new RuntimeException("employee is null");
		}

		Employee employee2 = null;
		try {
			if(employee.isHireFlag()){
				employee.setStatus("1");
				employeeDao.updateHireInfo(employee);
				
				HistoryRecord historyRecord=new HistoryRecord();
				historyRecord.setRecordId(UUIDUtil.getUUNum());
				historyRecord.setEmployeeId(employee.getEmployeeId()+"");
				historyRecord.setName(employee.getName());
				historyRecord.setStartDate(employee.getEntryDate());
				historyRecord.setOrgName(employee.getOrgName());
				historyRecord.setTransferType(TransferTypeConstant.RE_HIRE);
				historyRecord.setOperate(employee.getOperate());
				historyRecord.setOperateTime(new Date());
				//插入人员历史记录信息
				historyRecordDao.insert(historyRecord);
			}
			else{
				employeeDao.updateInfo(employee);
				if("2".equals(employee.getStatus())){
					HistoryRecord historyRecord=new HistoryRecord();
					historyRecord.setRecordId(UUIDUtil.getUUNum());
					historyRecord.setEmployeeId(employee.getEmployeeId()+"");
					historyRecord.setName(employee.getName());
					historyRecord.setStartDate(employee.getEntryDate());
					historyRecord.setEndDate(employee.getDepartDate());
					historyRecord.setOrgName(employee.getOrgName());
					historyRecord.setTransferType(TransferTypeConstant.QUIT);
					historyRecord.setOperate(employee.getOperate());
					historyRecord.setOperateTime(new Date());
					historyRecordDao.insert(historyRecord);
				}
				
			}
			
			employee2 = findById(employee.getEmployeeId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改失败"+e);
		}

		logger.info("complete updateInfo ：" + employee);

		return employee2;
	}

	@Transactional
	public void updateOrg(List<Long> employeeIds, long newOrgId) throws Exception {
		logger.info("start updateOrg, employeeIds: " + employeeIds + ", newOrgId: " + newOrgId);

		if (employeeIds == null || employeeIds.size() == 0) {
			logger.warn("employeeIds is null");
			throw new RuntimeException("employeeIds is null");
		}
		try {
			logger.info("checking if the newOrg exist : " + newOrgId);
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(newOrgId);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orgIds", orgIds);
			List<Org> orgs = orgDao.findById(param);

			if (orgs == null || orgs.size() == 0) {
				logger.error("new org does not exist : " + newOrgId);
				throw new RuntimeException("新组织不存在");
			}
			logger.info("newOrgId exist, updating to new org");
			try {
				employeeDao.updateOrg(employeeIds, newOrgId);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("修改失败");
			}
		} catch (Exception e) {
			logger.error("err when updateOrg, employeeIds: " + employeeIds + ", newOrgId: " + newOrgId);
			throw e;
		}

		logger.info("complete updateOrg, employeeIds: " + employeeIds + ", newOrgId: " + newOrgId);
	}
	
	@Transactional
	public Pager<Employee> findByOrg(Pager<Employee> pager, Map<String, Object> param) throws Exception {

		logger.info("start method: Pager<Employee> findByOrg(Pager<Employee> pager, Map<String, Object> param), arg pager: "
						+ pager + ", arg param:" + param);
		
		if (pager == null) {
			pager = new Pager<Employee>();
		}
		
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("employeeId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}

			// 拷贝
			Map<String, Object> map = new HashMap<String, Object>();
			
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			
			Object isMonitor = map.get("Monitor");
			if (isMonitor != null) {
				map.put("Monitor", isMonitor.toString());
				
			}
			Object includeSub = map.get("includeSub");
			if (includeSub != null) {
				map.put("includeSub", includeSub.toString());
				
			}
			
			if (pager.isAutoCount()) {
				long totalCount = employeeDao.countByOrg(map);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			map.put("pager", pager);
			
			List<Employee> result;
			
			//若为导出
			
			Object for_method_exportData = param.get("for_method_exportData");
			if (for_method_exportData != null && "true".equals(for_method_exportData.toString())) {
				result = employeeDao.findAll(map);
				
			} else {
				result = employeeDao.findByOrg(map);
			}
			
			pager.setResult(result);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}

		return pager;
	}
/*
	@Transactional
	public Pager<Employee> findByOrg(Pager<Employee> pager, Map<String, Object> param) throws Exception {

		logger.info("start method: Pager<Employee> findByOrg(Pager<Employee> pager, Map<String, Object> param), arg pager: "
						+ pager + ", arg param:" + param);

		if (pager == null) {
			pager = new Pager<Employee>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("employeeId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}
			// orgId集合
			List<Long> orgIds = new ArrayList<Long>();
			Object orgId = param.get("orgId");
			if (orgId != null) {
				orgIds.add(Long.valueOf(orgId.toString()));
			}

			// 若包含子机构
			Object includeSub = param.get("includeSub");
			if (includeSub != null && "true".equals(includeSub.toString())) {
				logger.info("querying children orgs");
				getChild(orgIds, orgIds);
				logger.info("complete querying children orgs:{}", orgIds);
			}

			// 拷贝
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("orgIds", orgIds);
			// 查询监控人员，监控人员只查询子部门的人员，不查询子公司的人员
			Object isMonitor = param.get("Monitor");
			if (isMonitor != null && "true".equals(isMonitor.toString())) {
				
				Map<String, Object> map2 = new HashMap<String, Object>();
				if (param != null && param.size() > 0) {
					for (Entry<String, Object> entry : param.entrySet()) {
						map2.put(entry.getKey(), entry.getValue());
					}
				}
				map2.put("onlyDep", "true");
				List<Org> orgs = orgService.findChildOrgs(orgIds, map2);
				for (Org org : orgs) {
					orgIds.add(org.getOrgId());
				}
			}
			
			if (pager.isAutoCount()) {
				logger.info("querying total count by orgIds : " + orgIds);
				long totalCount = employeeDao.countByOrg(map);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			logger.info("querying by orgIds : " + orgIds);
			map.put("pager", pager);
			
			List<Employee> result;
			
			//若为导出
			
			Object for_method_exportData = param.get("for_method_exportData");
			if (for_method_exportData != null && "true".equals(for_method_exportData.toString())) {
				result = employeeDao.findAll(map);
				
			} else {
				result = employeeDao.findByOrg(map);
			}
			pager.setResult(result);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}

//		logger.info("complete method, pager: " + pager);
		return pager;
	}
	
	@Transactional
	public Pager<Employee> findByOrg(long orgId, boolean includeSub, Pager<Employee> pager) throws Exception {
		
		logger.info("start findByOrg, OrgId : " + orgId + ", includeSub : " + includeSub + ", pager ： " + pager);
		
		if (pager == null) {
			pager = new Pager<Employee>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("employeeId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("asc");
			}
			
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(orgId);
			// 若包含子机构
			if (includeSub) {
				logger.info("querying children orgs");
				getChild(orgIds, orgIds);
				logger.info("complete querying children orgs:{}",orgIds);
			}
			
			// 拷贝param
			Map<String, Object> params = pager.getParams();
			Map<String, Object> map = pager.getParams();
			if (params != null && params.size() > 0) {
				for (Entry<String, Object> entry : params.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("orgIds", orgIds);
			
			if (pager.isAutoCount()) {
				logger.info("querying total count by orgIds : " + orgIds);
//				long totalCount = employeeDao.countByOrg(orgIds);
				long totalCount = employeeDao.countByOrg(map);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			//-----------------------查询监控人员，监控人员只查询子部门的人员，不查询子公司的人员
			String isMonitor = (String)pager.getParams().get("Monitor");
			if(!StringUtils.isEmpty(isMonitor)&&isMonitor.equals("true")){
				
				Map<String, Object> p = pager.getParams();
				p.put("onlyDep", "true");
				List<Org> orgs = orgService.findChildOrgs(orgIds, p);
				logger.info("Child departments:{}",orgIds);
				for(Org org : orgs){
					orgIds.add(org.getOrgId());
				}
				pager.getParams().put("orgIds", orgIds);
			}
			//---------------------------
			logger.info("querying by orgIds : " + orgIds);
			
//			List<Employee> result = employeeDao.findByOrg(orgIds, pager);
			map.put("pager", pager);
			List<Employee> result = employeeDao.findByOrg(map);
			
			pager.setResult(result);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		
		logger.info("complete method, pager: " + pager);
		return pager;
	}
*/
	@Transactional
	public Employee findById(Long employeeId) throws Exception {

		logger.info("start method: Employee findById(long employeeId), arg employeeId: " + employeeId);
		if (employeeId == null) {
			logger.warn("arg employeeId is null");
			throw new RuntimeException("arg employeeId is null");
		}
		Employee employee = null;
		try {
			employee  = employeeDao.findById(employeeId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return employee: " + employee);
		return employee;
	}
	
	private void getChild(List<Long> orgIds, List<Long> parentIds) {
		if(parentIds == null || parentIds.size() == 0){
			return;
		}
		List<Map<String,Object>> results = orgDao.findChildById(parentIds);
		if (results != null && results.size() > 0) {
			List<Long> pIds = new ArrayList<Long>();
			for (Map<String,Object> item : results) {
				pIds.add((Long)item.get("orgId"));
			}
			orgIds.addAll(pIds);
			getChild(orgIds, pIds);
		}
	}


	@Override
	public List<Employee> findById(List<Long> employeeIds) throws Exception {
		logger.info("start method: Employee findById(long employeeId), arg employeeIds: " + employeeIds);
		if (employeeIds == null || employeeIds.size() == 0) {
			logger.warn("arg employeeId is null");
			throw new RuntimeException("arg employeeId is null");
		}
		List<Employee> employees = null;
		try {
			employees = employeeDao.findByIds(employeeIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return employeeIds: " + employeeIds);
		return employees;
	}

	@Override
	public Employee findByEmployeeNo(String employeeNo) throws Exception {
		try {
			logger.info("Enter the :{} method  employeeNo:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),employeeNo);

			Employee employee=employeeDao.findByEmployeeNo(employeeNo);
			return employee;
		} catch (Exception e) {
			logger.error("method {} execute error, employeeNo:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), employeeNo,e);
			throw e;
		}
		
	}
	
	@Override
	public List<Employee> findByCompanyIds(List<Long> companyIds) throws Exception {
		logger.info("start method: Employee findByCompanyIds((List<Long> companyIds), arg companyIds: " + companyIds);
		if (companyIds == null || companyIds.size() == 0) {
			logger.warn("arg companyIds is null");
			throw new RuntimeException("arg companyIds is null");
		}
		List<Employee> employees = null;
		try {
			employees = employeeDao.findByCompanyIds(companyIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return employees: " + employees);
		return employees;
	}
	
	
	@Override
	@Transactional
	public void batchInsertEmployee(List<Employee> employees) {
		
		logger.info("start method: batchInsertEmployee(List<Employee> employees), arg companyIds: " + employees);
		try {
			List<Employee> batchInsertEmps = new ArrayList<>();
			List<Employee> batchUpdateEmps = new ArrayList<>();
			Map<String,Employee> map = new HashMap<>();
			
			for (Employee employee : employees) {
				Org o = new Org();
				o.setOrgId(employee.getOrgId());
				employee.setOrg(o);
				String identNo = employee.getIdentNo().trim();
				map.put(identNo, employee);
			}
			
			List<String> idents = new ArrayList<>(map.keySet());
			idents = employeeDao.findByIdentNo(idents);
			
			if(idents != null && idents.size() > 0) {
				for (String ident : idents) {
					batchUpdateEmps.add(map.get(ident));
					map.remove(ident);
				}
			}
			
			if(map.size() > 0) {
				batchInsertEmps.addAll(map.values());
				insert(batchInsertEmps);
			}
			
			if(batchUpdateEmps.size() > 0) {
				employeeDao.batchUpdateEmps(batchUpdateEmps);
			}
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
	}
	
	@Override
	@Transactional
	public List<Employee> findEmployeeByParam(Map<String, Object> param) throws Exception {
		List<Employee> employees = null;
		try {
			employees = employeeDao.findEmployeeByParam(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return employees;
	}
	@Override
	public Employee updateInnerInfo(Employee employee) throws Exception {
		logger.info("start updateOrg, employeeIds: ");

		if (employee == null) {
			logger.warn("employee is null");
			throw new RuntimeException("employee is null");
		}
		try {
			logger.info("start method updateInnerInfo: ");

			if (employee.getOrgId() == null) {
				logger.error("new org does not exist : " + employee.getOrgId());
				throw new RuntimeException("新组织不存在");
			}
			logger.info("newOrgId exist, updating to new org");
			try {
				employeeDao.updateInnerInfo(employee);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("修改失败");
			}
		} catch (Exception e) {
			throw e;
		}

		logger.info("complete method, updateInnerInfo: ");
		return null;
	}
	
}