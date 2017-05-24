package com.sdcloud.biz.lar.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.ItemPointStatistics;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class SalesmanServiceImpl implements SalesmanService {

	@Autowired
	private SalesmanDao salesmanDao;
	
	@Transactional(readOnly=true)
	public int countById(String mechanismId) throws Exception {
		int count = 0;
		try {
			count = salesmanDao.countById(mechanismId);
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional(readOnly=true)
	public LarPager<Salesman> selectByExample(LarPager<Salesman> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<Salesman>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if(params==null || params.size()<=0){
				throw new IllegalArgumentException("params is error");
			}
			if(!params.containsKey("mechanismId") && !params.containsKey("orgIds")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				//long totalCount = salesmanDao.countById(String.valueOf(params.get("mechanismId")));
				long totalCount = salesmanDao.countByIds(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<Salesman> result = salesmanDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
	@Transactional(readOnly=true)
	public LarPager<Salesman> salesManPointStatistics(LarPager<Salesman> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<Salesman>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if(params==null || params.size()<=0){
				throw new IllegalArgumentException("params is error");
			}
			if(!params.containsKey("mechanismId") && !params.containsKey("orgIds")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				//long totalCount = salesmanDao.countById(String.valueOf(params.get("mechanismId")));
				long totalCount = salesmanDao.countByIds(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<Salesman> result = salesmanDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
	@Transactional(readOnly=true)
	public LarPager<ItemPointStatistics> itemPointStatistics(LarPager<ItemPointStatistics> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<ItemPointStatistics>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if(params==null || params.size()<=0){
				throw new IllegalArgumentException("params is error");
			}
			if(!params.containsKey("mechanismId") && !params.containsKey("orgIds") ){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			
			List<Long> orgIds = new ArrayList<>();
			if(null != params.get("mechanismId")){
				orgIds.add((Long)params.get("mechanismId"));
			}
			if(null != params.get("orgIds")){
				List<Long> ids = (List<Long>) params.get("orgIds");
				orgIds.addAll(ids);
			}
			
			List<ItemPointStatistics> result = salesmanDao.itemPointStatistics(pager);
			List<Long> resultId= new ArrayList<>();

			//统计日期字段处理
			String date = null;//统计日期字段
			Date dt = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			if(params.get("firstDate")==null&&params.get("endDate")==null){
				date = "../"+formatter.format(dt);
			}
			if(params.get("firstDate")!=null&&params.get("endDate")==null){
				String startTime = params.get("firstDate").toString();
				Date start = formatter.parse(startTime);
				date = formatter.format(start)+"/"+formatter.format(dt);
			}
			if(params.get("firstDate")==null&&params.get("endDate")!=null){
				String endTime = params.get("endDate").toString();
				Date end = formatter.parse(endTime);
				Date dBefore = new Date();
				Calendar calendar = Calendar.getInstance(); //得到日历
				calendar.setTime(end);//把当前时间赋给日历
				calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
				dBefore = calendar.getTime();   //得到前一天的时间
				date = formatter.format(dBefore)+"/"+formatter.format(dt);
			}
			if(params.get("firstDate")!=null&&params.get("endDate")!=null){
				String startTime = params.get("firstDate").toString();
				String endTime = params.get("endDate").toString();
				Date start = formatter.parse(startTime);
				Date end = formatter.parse(endTime);
				if(startTime == endTime){
					date = formatter.format(start);
				}else{
					Date dBefore = new Date();
					Calendar calendar = Calendar.getInstance(); //得到日历
					calendar.setTime(end);//把当前时间赋给日历
					calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
					dBefore = calendar.getTime();   //得到前一天的时间
					date = formatter.format(start)+"/"+formatter.format(dBefore);
				}
			}
			
			if(null != result && result.size()>0){
				for (ItemPointStatistics item :result) {
					resultId.add(item.getMechanismId());
					item.setDate(date);
				}
			}
			for(Long id:orgIds){
				if(!resultId.contains(id)){
					ItemPointStatistics itemPointStatistics = new ItemPointStatistics();
					itemPointStatistics.setDate(date);
					itemPointStatistics.setMechanismId(id);
					itemPointStatistics.setMallPointInputSum(0L);
					itemPointStatistics.setSanitationPointOutSum(0L);
					itemPointStatistics.setRecyclePointOutSum(0L);
					itemPointStatistics.setItemPointDifferenceSum(0L);
					result.add(itemPointStatistics);
				}
			}
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
	@Transactional(readOnly=true)
	public LarPager<Salesman> getSalesmansByConditron(LarPager<Salesman> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<Salesman>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if(params==null || params.size()<=0){
				throw new IllegalArgumentException("params is error");
			}
			if(!params.containsKey("mechanismId") && !params.containsKey("orgIds")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = salesmanDao.countByConditron(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<Salesman> result = salesmanDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Override
	@Transactional(readOnly=true)
	public List<OwnedSupplier> getOwnedSuppliersById(String id) throws Exception {
		try {
			return salesmanDao.getOwnedSuppliersById(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Salesman> getareaNamesByOId(String id) throws Exception {
		try {
			return salesmanDao.getareaNamesByOId(id);
		} catch (Exception e) {
			throw e;
		}
	}
	

	@Override
	@Transactional
	public boolean insertSelective(Salesman salesman) throws Exception {
		if (salesman != null) {
			int count = 0;
			try {
				salesman.setId(String.valueOf(UUIDUtil.getUUNum()));
				salesman.setEnable(0);
				salesman.setIntegral(0);
				salesman.setRechargeIntegral(0);
				salesman.setGiveIntegral(0);
				count = salesmanDao.insertSelective(salesman);
				if(count>0){
					return true;
				}else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("Salesman is null");
		}
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		int count = 0;
		try {
			count = salesmanDao.deleteById(id);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional
	public boolean updateByExampleSelective(Salesman salesman)throws Exception {
		if (salesman != null && salesman.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("salesman", salesman);
			int count = salesmanDao.updateByExampleSelective(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("Salesman or id is error");
		}
	}

	@Override
	@Transactional(readOnly=true)
	public int getSalesmanPoints(String id) throws Exception {
		try {
			return salesmanDao.getSalesmanPoints(id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public int updateByEmployeeId(Salesman salesman)throws Exception {
		if (salesman != null && salesman.getPersonnelId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("salesman", salesman);
			return salesmanDao.updateByEmployeeId(params);
		} else {
			throw new IllegalArgumentException("Salesman or personnelId is error");
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Salesman> getByPersonnelId(String userId) throws Exception{
		try {
			return salesmanDao.getByPersonnelId(userId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Long selectByArea(String areaId) {
		try {
			return salesmanDao.selectByArea(areaId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<String> selectPersonnelIdList(Map<String, Object> map) {
		try {
			return salesmanDao.selectPersonnelIdList(map);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Salesman getSalesManById(String id) {
		// TODO Auto-generated method stub
		return salesmanDao.getSalesManById(id);
	}

	@Override
	public Integer getSalesScoreByEmp(Long empId) {
		List<Salesman> Salesmans = salesmanDao.getByPersonnelId(empId+"");
		int integral = 0;
		if(null !=Salesmans && Salesmans.size()>0){
			for(Salesman salesman:Salesmans){
				if(salesman.getIntegral()>integral)
					integral=salesman.getIntegral();
			}
		}
		return integral;
	}
	
	@Override
	public LarPager<Salesman> findEmploy(LarPager<Salesman> larPager, List<Long> ids) {
		List<Salesman> list = salesmanDao.findEmploy(larPager, ids);
		Long count = salesmanDao.countByEmploy(larPager, ids);
		larPager.setResult(list);
		larPager.setTotalCount(count);
		return larPager;
		
	}
	
}
