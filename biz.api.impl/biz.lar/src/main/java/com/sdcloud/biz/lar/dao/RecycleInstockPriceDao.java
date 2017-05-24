package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleInstockPrice;

@Repository
public interface RecycleInstockPriceDao extends BaseDao<RecycleInstockPrice>{

	@Select("select id,org_id AS \"orgId\",month,spec_id AS \"specId\",price_id AS \"priceId\",price "
			+ " from lar_recycle_instock_price "
			+ " where org_id=#{orgId} and month = #{month}  and spec_id = #{specId}" )
	List<RecycleInstockPrice> getPrice(@Param("orgId")Long orgId, @Param("month")Long month, @Param("specId")Long specId);

	int copyHistory(RecycleInstockPrice recycleInstockPrice);
}
