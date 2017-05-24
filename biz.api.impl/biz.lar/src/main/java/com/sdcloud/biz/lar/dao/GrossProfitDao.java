package com.sdcloud.biz.lar.dao;

/**
 * createUser dingx 
 * createDate 2016-12-14
 * fucntion 毛利润统计 
 */
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.GrossProfit;
import com.sdcloud.api.lar.entity.GrossProfitDetails;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface GrossProfitDao {
	
	long countGrossProfit(@Param("params") Map<String, Object> params);

	List<GrossProfit> selectGrossProfit(@Param("larPager") LarPager<GrossProfit> larPager);
	
	long countGrossProfitDetail(@Param("params") Map<String, Object> params);
	
	List<GrossProfitDetails> selectGrossProfitDetail(@Param("larPager") LarPager<GrossProfitDetails> larPager);

}
