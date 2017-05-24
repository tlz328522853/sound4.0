package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.EmployeeChageReport;
import com.sdcloud.api.core.entity.EmployeeInnerReport;
import com.sdcloud.api.core.entity.HistoryRecord;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.EmployeeChageReportService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.biz.core.dao.EmployeeChageReportDao;
import com.sdcloud.biz.core.dao.HistoryRecordDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.constants.TransferTypeConstant;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("employeeChageReportService")
public class EmployeeChageReportServiceImpl implements EmployeeChageReportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmployeeChageReportDao employeeChageReportDao;
	
	@Autowired
	private HistoryRecordDao historyRecordDao;
	
	@Autowired
	private OrgService orgService;
	

	@Transactional
	public void insert(List<EmployeeChageReport> employeeChageReports) throws Exception {
		logger.info("start method: long insert(EmployeeChageReport EmployeeChageReport), arg EmployeeChageReport: "
				+ employeeChageReports);

		if (employeeChageReports==null||employeeChageReports.size()<=0) {
			logger.warn("arg EmployeeChageReport is null");
			throw new IllegalArgumentException("arg EmployeeChageReport is null");
		}
		for (EmployeeChageReport employeeChageReport : employeeChageReports) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			employeeChageReport.setEmployeeChageId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					employeeChageReportDao.insert(employeeChageReports);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		try {
			HistoryRecord historyRecord=new HistoryRecord();
			historyRecord.setRecordId(UUIDUtil.getUUNum());
			historyRecord.setEmployeeId(employeeChageReports.get(0).getEmployeeId()+"");
			List<Org> org=orgService.findById(employeeChageReports.get(0).getOutOrgId(), false);
			historyRecord.setOrgName(org.get(0).getName());
			historyRecord.setName(employeeChageReports.get(0).getEmployeeName());
			historyRecord.setTransferType(TransferTypeConstant.CALL_OUT);
			historyRecord.setStartDate(employeeChageReports.get(0).getOutDate());
			historyRecord.setOperate(employeeChageReports.get(0).getOperate());
			historyRecord.setOperateTime(new Date());
			//插入人员历史记录信息
			historyRecordDao.insert(historyRecord);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
	}

	@Transactional
	public void delete(List<Long> employeeChageReports) throws Exception {
		logger.info("start method: void delete(List<Long> EmployeeChageReports), arg EmployeeChageReports: "
				+ employeeChageReports);
		if (employeeChageReports == null || employeeChageReports.size() == 0) {
			logger.warn("arg EmployeeChageReports is null or size=0");
			throw new IllegalArgumentException("arg EmployeeChageReports is null or size=0");
		}
		try {
			
			employeeChageReportDao.delete(employeeChageReports);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(EmployeeChageReport employeeChageReport) throws Exception {
		logger.info("start method: void update(EmployeeChageReport EmployeeChageReport), arg EmployeeChageReport: "
				+ employeeChageReport);
		if (employeeChageReport == null || employeeChageReport.getEmployeeChageId()== null) {
			logger.warn("arg EmployeeChageReport is null or EmployeeChageReport 's employeeChageReportId is null");
			throw new IllegalArgumentException("arg EmployeeChageReport is null or EmployeeChageReport 's addOidId is null");
		}
		try {
			
			employeeChageReportDao.update(employeeChageReport);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		try {
			
			HistoryRecord historyRecord=new HistoryRecord();
			historyRecord.setEmployeeId(employeeChageReport.getEmployeeId()+"");
			if("1".equals(employeeChageReport.getType())){
				historyRecord.setTransferType(TransferTypeConstant.CALL_IN);
				historyRecord.setStartDate(employeeChageReport.getInDate());
			}
			else{
				historyRecord.setTransferType(TransferTypeConstant.CALL_OUT);
				historyRecord.setStartDate(employeeChageReport.getOutDate());
			}
			
			historyRecord.setOperateTime(new Date());
			//插入人员历史记录信息
			historyRecordDao.update(historyRecord);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<EmployeeChageReport> findBy(Pager<EmployeeChageReport> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<EmployeeChageReport> findBy(Pager<EmployeeChageReport> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<EmployeeChageReport>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("c.employeeChageId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = employeeChageReportDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			param.put("pager", pager); //将pager装入到map中
			List<EmployeeChageReport> EmployeeChageReports = employeeChageReportDao.findAllBy(param);
			pager.setResult(EmployeeChageReports);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public void updateParam(List<Long> empchangeIds, Map<String, Object> param)
			throws Exception {
		try {
			logger.info("Enter the :{} method  empchangeIds:{} param:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),empchangeIds,param);
			param.put("empChageIds", empchangeIds);
			param.put("employeeChageIds", empchangeIds);
			employeeChageReportDao.updateParam(param);
		} catch (Exception e) {
			logger.error("method {} execute error, empchangeIds:{} param:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), empchangeIds,param,e);
			throw e;
		}
		try {
			List<EmployeeChageReport> empChanges = new ArrayList<EmployeeChageReport>();
			empChanges=employeeChageReportDao.findAllBy(param);
			for (EmployeeChageReport employeeChageReport : empChanges) {
				HistoryRecord historyRecord=new HistoryRecord();
				historyRecord.setRecordId(UUIDUtil.getUUNum());
				historyRecord.setEmployeeId(employeeChageReport.getEmployeeId()+"");
				List<Org> org=orgService.findById(employeeChageReport.getOutOrgId(), false);
				historyRecord.setOrgName(org.get(0).getName());
				historyRecord.setName(employeeChageReport.getEmployeeName());
				historyRecord.setTransferType(TransferTypeConstant.CALL_IN);
				historyRecord.setStartDate(employeeChageReport.getOutDate());
				historyRecord.setOperate((String)param.get("operate"));
				historyRecord.setOperateTime(new Date());
				//插入人员历史记录信息
				historyRecordDao.insert(historyRecord);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void insertInner(List<EmployeeChageReport> employeeChageReports) throws Exception {
		logger.info("start method: long insertInner(EmployeeChageReport EmployeeChageReport), arg EmployeeChageReport: "
				+ employeeChageReports);

		if (employeeChageReports==null||employeeChageReports.size()<=0) {
			logger.warn("arg EmployeeChageReport is null");
			throw new IllegalArgumentException("arg EmployeeChageReport is null");
		}
		for (EmployeeChageReport employeeChageReport : employeeChageReports) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			employeeChageReport.setEmployeeChageId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					employeeChageReportDao.insertInner(employeeChageReports);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
	}

	@Override
	public Pager<EmployeeInnerReport> findAllInnerBy(Pager<EmployeeInnerReport> pager, Map<String, Object> param)
			throws Exception {
		logger.info("start method: Pager<EmployeeInnerReport> findAllInnerBy(Pager<EmployeeInnerReport> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<EmployeeInnerReport>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("c.employeeChageId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = employeeChageReportDao.getInnerTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			param.put("pager", pager); //将pager装入到map中
			List<EmployeeInnerReport> EmployeeChageReports = employeeChageReportDao.findAllInnerBy(param);
			pager.setResult(EmployeeChageReports);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public void updateReason(EmployeeChageReport employeeChageReport) throws Exception {
		logger.info("start method: void updateReason(EmployeeChageReport EmployeeChageReport), arg EmployeeChageReport: "
				+ employeeChageReport);
		if (employeeChageReport == null || employeeChageReport.getEmployeeChageId()== null) {
			logger.warn("arg EmployeeChageReport is null or EmployeeChageReport 's employeeChageReportId is null");
			throw new IllegalArgumentException("arg EmployeeChageReport is null or EmployeeChageReport 's addOidId is null");
		}
		try {
			employeeChageReportDao.updateReason(employeeChageReport);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method updateReason, return void");
	}

}
