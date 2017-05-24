package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleInstock;

/**
 * lar_recycle_instock 入库管理
 * @author luorongjie
 * @date 2016-12-05
 * @version 1.0
 */
@Repository
public interface RecycleInstockDao extends BaseDao<RecycleInstock> {
	
	int existByInstockNo(@Param("id")Long instockId, @Param("instock_no") String instockNo);
	
	
}
