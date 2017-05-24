package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.service.IntegralConsumptionService;
import com.sdcloud.api.lar.service.PointStatisticsService;
import com.sdcloud.biz.lar.dao.CommodityDao;
import com.sdcloud.biz.lar.dao.IntegralConsumptionDao;
import com.sdcloud.biz.lar.dao.PointStatisticsDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class PointStatisticsServiceImpl implements PointStatisticsService {

	@Autowired
	private PointStatisticsDao pointStatisticsDao;
	
	
	@Override
	@Transactional(readOnly=true)
	public LarPager<ExchangeInfo> selectByExample(LarPager<ExchangeInfo> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<ExchangeInfo>();
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
			if(!params.containsKey("mechanismId")&&!params.containsKey("orgIds")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = 0;
				totalCount = pointStatisticsDao.count(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<ExchangeInfo> result = pointStatisticsDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
}
