package com.sdcloud.biz.lar.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.GrossProfit;
import com.sdcloud.api.lar.entity.GrossProfitDetails;
import com.sdcloud.api.lar.service.GrossProfitService;
import com.sdcloud.biz.lar.dao.GrossProfitDao;
import com.sdcloud.framework.entity.LarPager;

@Service
public class GrossProfitServiceImpl implements GrossProfitService{
	
	@Autowired
	private GrossProfitDao grossProfitDao;

	@Override
	public LarPager<GrossProfit> getGrossProfit(LarPager<GrossProfit> larPager) throws Exception {
		// TODO Auto-generated method stub
		
		if (larPager == null) {
			larPager = new LarPager<GrossProfit>();
		}
		try {
			//暂时没有用上
			/*if (StringUtils.isEmpty(larPager.getOrderBy())) {
				// 多个用逗号
				larPager.setOrderBy("check_date");//默认按审核时间排序
			}
			if (StringUtils.isEmpty(larPager.getOrder())) {
				// 多个用逗号
				larPager.setOrder("desc");
			}*/
			Map<String, Object> params = larPager.getParams();
			if (params == null || params.size() <= 0) {
				throw new IllegalArgumentException("params is error");
			}
			if (!params.containsKey("mechanismId") && !params.containsKey("orgIds")) {
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (larPager.isAutoCount()) {
				long totalCount = 20;
				totalCount = grossProfitDao.countGrossProfit(params);
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<GrossProfit> result = grossProfitDao.selectGrossProfit(larPager);
			//补全结算时间
			/*Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
			String time = sd.format(date);
			for (int i = 0; i < result.size(); i++) {
				result.get(i).setMonth(time);;
			}*/
			larPager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return larPager;
	}

	@Override
	public LarPager<GrossProfitDetails> getGrossProfitDetail(LarPager<GrossProfitDetails> larPager) throws Exception {
		
		if (larPager == null) {
			larPager = new LarPager<GrossProfitDetails>();
		}
		try {
			Map<String, Object> params = larPager.getParams();
			if (params == null || params.size() <= 0) {
				throw new IllegalArgumentException("params is error");
			}
			if (!params.containsKey("mechanismId") && !params.containsKey("orgIds")) {
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (larPager.isAutoCount()) {
				long totalCount = 20;
				totalCount = grossProfitDao.countGrossProfitDetail(params);
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<GrossProfitDetails> result = grossProfitDao.selectGrossProfitDetail(larPager);
			larPager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return larPager;
	}

}
