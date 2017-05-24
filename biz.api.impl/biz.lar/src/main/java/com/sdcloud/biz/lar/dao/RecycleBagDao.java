package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleBag;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface RecycleBagDao {

	long countByIds(@Param("params") Map<String, Object> params);
	
	// 根据条件查询所有信息
	List<RecycleBag> selectByExample(@Param("larPager") LarPager<RecycleBag> larPager);
	
	@Update("update lar_recycle_bag set orderState=#{params.orderState},orderStateName=#{params.orderStateName},persionId=#{params.persionId},persionName=#{params.persionName},completeTime=#{params.completeTime} where id=#{params.id}")
	int completeOrders(@Param("params") Map<String, Object> updateParams);

	int insertSelective(RecycleBag recycleBag);
}
