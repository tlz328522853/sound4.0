package com.sdcloud.biz.lar.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.framework.entity.LarPager;

/**
 * 积分入账
 * @author dingx
 *
 */
@Repository
public interface PointStatisticsDao {

		long count(@Param("params") Map<String, Object> params);

		List<ExchangeInfo> selectByExample(@Param("larPager") LarPager<ExchangeInfo> larPager);
		
}
