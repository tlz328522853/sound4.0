package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleChenkExport;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.framework.entity.LarPager;

/**
 * lar_recycle_outstock 出库管理
 * @author luorongjie
 * @date 2016-12-07
 * @version 1.0
 */
@Repository
public interface RecycleOutstockDao extends BaseDao<RecycleOutstock> {

	List<RecycleChenkExport> chenkExport(@Param("larPager")LarPager<RecycleChenkExport> larPager, @Param("ids")List<Long> list);

	long countChenkExport(@Param("larPager")LarPager<RecycleChenkExport> larPager, @Param("ids")List<Long> list);

	int existByOutstockNo(@Param("id")Long outstockId,@Param("outstock_no")String outstockNo);

	int updateImg(@Param("id") Long id);
	
	
}
