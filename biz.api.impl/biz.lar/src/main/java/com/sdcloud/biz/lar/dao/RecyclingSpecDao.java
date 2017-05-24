package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface RecyclingSpecDao extends BaseDao<RecyclingSpec>{

	List<RecyclingSpec> findPriceByOrgId(@Param("larPager")LarPager<RecyclingSpec> larPager, @Param("ids")List<Long> ids);

	long countPricByOrgId(@Param("larPager")LarPager<RecyclingSpec> larPager, @Param("ids")List<Long> ids);

	Boolean updatePrice(RecyclingSpec recyclingSpec);

	Boolean savePrice(RecyclingSpec recyclingSpec);

	void stopPriceBySpec(@Param("id")Long id);

	List<RecyclingSpec> findSpecByOrgId(@Param("larPager")LarPager<RecyclingSpec> larPager, @Param("ids")List<Long> ids);

	List<RecyclingSpec> findAllSpecByOrgId(@Param("larPager")LarPager<RecyclingSpec> larPager, @Param("ids")List<Long> ids);
	
	RecyclingSpec getSpecById(@Param("id")Long id);

}
