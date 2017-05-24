package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.MyPoints;
import com.sdcloud.api.lar.entity.RecycleChildOrder;
import com.sdcloud.api.lar.entity.RecycleOrder;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface RecycleOrderDao extends BaseDao<RecycleOrder>{

	int checkOrder(@Param("recycleOrder")RecycleOrder recycleOrder);

	List<MyPoints> getOrderByUserId(@Param("params")Map<String, Object> params);
	//查询回收物类型
	@Select(value = "select `id`,`typeName`,`enable` from lar_recyclingtype where enable=0")
	List<RecyclingType> getRecyclingTypes();

	int updateChildByExampleSelective(@Param("params")Map<String, Object> params);

	List<ChildOrders> selectCildByExample(LarPager<RecycleChildOrder> larPager);

	List<Long> getCheckMens(@Param("larPager")LarPager<RecycleOrder> larPager, @Param("ids")List<Long> ids);

}
