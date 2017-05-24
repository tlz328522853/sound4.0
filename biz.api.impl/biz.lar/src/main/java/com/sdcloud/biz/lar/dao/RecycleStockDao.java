package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleStock;
import com.sdcloud.framework.entity.LarPager;

/**
 * lar_recycle_stock 
 * @author luorongjie
 * @date 2016-12-02
 * @version 1.0
 */
@Repository
public interface RecycleStockDao extends BaseDao<RecycleStock> {

	List<RecycleStock> findStockByOrgIds(@Param("larPager")LarPager<RecycleStock> larPager, @Param("ids")List<Long> ids);

	long countStockByOrgIds(@Param("larPager")LarPager<RecycleStock> larPager, @Param("ids")List<Long> ids);
	
	
}
