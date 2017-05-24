package com.sdcloud.biz.lar.service.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * creeateUser dingx
 * createDate 2016-12-09
 * function 供货商结算统计
 */
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.VendorSettlement;
import com.sdcloud.api.lar.service.VendorSettlementService;
import com.sdcloud.biz.lar.dao.VendorSettlementDao;
import com.sdcloud.framework.entity.LarPager;

@Service
public class VendorSettlementServiceImpl implements VendorSettlementService{
	
	@Autowired
	private VendorSettlementDao vendorSettlementDao;

	@Override
	public LarPager<VendorSettlement> getVendorSettlement(LarPager<VendorSettlement> larPager) throws Exception {
		// TODO Auto-generated method stub
		
		if (larPager == null) {
			larPager = new LarPager<VendorSettlement>();
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
				totalCount = vendorSettlementDao.countVendorSettlement(params);
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<VendorSettlement> result = vendorSettlementDao.selectVendorSettlement(larPager);
			/*//补全结算时间
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String time = sd.format(date);
			for (int i = 0; i < result.size(); i++) {
				result.get(i).setTime(time);
			}*/
			larPager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return larPager;
	}

}
