package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.ExchangeOrders;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface ExchangeInfoDao {

	int insertSelective(ExchangeInfo exchangeInfo);

	int insertSelectiveChild(ExchangeOrders exchangeOrders);

	List<ExchangeInfo> getExchangeInfoByUserId(@Param("userId") String userId);
	
	List<ExchangeInfo> selectByExample(@Param("larPager") LarPager<ExchangeInfo> larPager, @Param("ids") List<Long> ids);
	
	Long totalCount(@Param("larPager") LarPager<ExchangeInfo> larPager, @Param("ids") List<Long> list);
	//updateBySelect
	int updateBySelect(ExchangeInfo exchangeInfo);
	
	@Update("update lar_exchangeinfo set OrderStatus=#{params.orderStatusId},"
			+ "cancelOrderPersonId=#{params.cancelOrderPersonId},cancelOrderPersonName=#{params.cancelOrderPersonName},"
			+ "cancelOrderIllustrate=#{params.cancelOrderIllustrate},cancelDate=#{params.cancelDate} where id=#{params.id}")
	int cancelOrderById(@Param("params") Map<String, Object> params);
	
	
	// 根据条件查询所有信息,查询子单号
	List<ChildOrders> selectCildByExample(
			@Param("larPager") LarPager<ChildOrders> larPager);
	
	
	// 查询一个exchange记录
	List<ExchangeInfo> selectByExampleByOrderId(@Param("params") Map<String, Object> params);
	
}
