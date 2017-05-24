package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.api.lar.service.IntegralConsumptionService;
import com.sdcloud.biz.lar.dao.CommodityDao;
import com.sdcloud.biz.lar.dao.IntegralConsumptionDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class IntegralConsumptionServiceImpl implements IntegralConsumptionService {

	@Autowired
	private IntegralConsumptionDao integralConsumptionDao;
	
	@Autowired
	private CommodityDao commodityDao;
	
	@Override
	@Transactional
	public boolean addIntegralConsumption(Map<String, Object> paramsMap) throws Exception {
		try {
			//根据商品类型，商品名称，商品品牌，商品规格，商品机构查询商品ID
			String commodityId = commodityDao.getCommodityId(paramsMap);
			if(commodityId==null || commodityId.length()<=0){
				return false;
			}
			paramsMap.put("id", String.valueOf(UUIDUtil.getUUNum()));
			paramsMap.put("commodity", commodityId);
			paramsMap.put("exchangeTime", new Date());
			paramsMap.put("enable", 0);
			paramsMap.put("createDate", new Date());
			int count = integralConsumptionDao.insertSelective(paramsMap);
			if(count>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public LarPager<IntegralConsumption> selectByExample(LarPager<IntegralConsumption> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<IntegralConsumption>();
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
			if(!params.containsKey("mechanismId")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = 0;
				totalCount = integralConsumptionDao.count(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<IntegralConsumption> result = integralConsumptionDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getOperators(String id) throws Exception {
		try {
			return integralConsumptionDao.getOperators(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		int count = 0;
		try {
			count = integralConsumptionDao.deleteById(id);
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
	@Transactional(readOnly=true)
	public List<IntegralConsumption> getExchangeInfo(String userId) throws Exception {
		try {
			List<IntegralConsumption> integralConsumptions = integralConsumptionDao.getExchangeInfo(userId);
			return integralConsumptions;
		} catch (Exception e) {
			throw e;
		}
	}
}
