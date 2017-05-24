package com.sdcloud.biz.lar.service.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * cereateUser dingx
 * createDate 2016-12-13
 * function 客户结算统计
 */
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.RetailerSettlement;
import com.sdcloud.api.lar.service.RetailerSettlementService;
import com.sdcloud.biz.lar.dao.RetailerSettlementDao;
import com.sdcloud.framework.entity.LarPager;

@Service
public class RetailerSettlementServiceImpl implements RetailerSettlementService{
	
	@Autowired
	private RetailerSettlementDao retailerSettlementDao;

	@Override
	public LarPager<RetailerSettlement> getRetailerSettlement(LarPager<RetailerSettlement> larPager) throws Exception {
		// TODO Auto-generated method stub
		
		if (larPager == null) {
			larPager = new LarPager<RetailerSettlement>();
		}
		try {
			if (StringUtils.isEmpty(larPager.getOrderBy())) {
				// 多个用逗号
				larPager.setOrderBy("audit_date");//默认按审核时间排序
			}
			if (StringUtils.isEmpty(larPager.getOrder())) {
				// 多个用逗号
				larPager.setOrder("desc");
			}
			Map<String, Object> params = larPager.getParams();
			if (params == null || params.size() <= 0) {
				throw new IllegalArgumentException("params is error");
			}
			if (!params.containsKey("mechanismId") && !params.containsKey("orgIds")) {
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (larPager.isAutoCount()) {
				long totalCount = 0;
				totalCount = retailerSettlementDao.countRetailerSettlement(params);
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<RetailerSettlement> result = retailerSettlementDao.selectRetailerSettlement(larPager);
			larPager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return larPager;
	}


}
