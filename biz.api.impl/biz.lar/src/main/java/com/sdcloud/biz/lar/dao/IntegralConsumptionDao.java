package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.IntegralConsumption;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface IntegralConsumptionDao {
	
		int insertSelective(Map<String, Object> paramsMap);

		long count(@Param("params") Map<String, Object> params);

		List<IntegralConsumption> selectByExample(@Param("larPager") LarPager<IntegralConsumption> larPager);
		
		@Select(value="SELECT `operatorName` FROM `lar_integralconsumption` WHERE ENABLE=0 AND `mechanismId`=#{id}")
		List<String> getOperators(String id);
		
		@Update("update lar_integralconsumption set enable=1 where id=#{id}")
		int deleteById(String id);
		
		@Select(value="SELECT  i.`id`,`clientUserId`,`consumptionQuantity`,`totalIntegral`,`commodityUnit`,i.`createDate`,"
				+ "c.paymentMethod as \"commodity.paymentMethod\",c.id as \"commodity.id\",`shopId` \"commodity.shopId\",`shopName` as \"commodity.shopName\",`brand` as \"commodity.brand\","
				+ "`specifications` as \"commodity.specifications\",t.id as \"commodity.commodityType.id\",`typeName` as \"commodity.commodityType.typeName\" "
				+ "FROM `lar_integralconsumption` i LEFT OUTER JOIN `lar_commodity` c ON(i.commodity=c.id) LEFT OUTER JOIN `lar_commoditytype` t "
				+ "ON(c.`commodityType`=t.id) WHERE i.enable=0 and i.clientUserId = #{userId} order by i.`createDate` desc")
		List<IntegralConsumption> getExchangeInfo(String userId);
}
