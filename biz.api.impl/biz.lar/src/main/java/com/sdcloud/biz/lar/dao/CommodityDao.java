package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.CommodityType;
import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface CommodityDao {
		// 查询总数
		long countById(@Param("params") Map<String, Object> params);

		//查询子单号
		@Select(value = "select count(id) from lar_recyclingtype  where enable = 0")
		int countByType();
		
		// 根据id删除
		@Delete(value = "update lar_commodity set enable=1 where id = #{id}")
		int deleteById(@Param("id") String id);
		
		// 根据类型id删除
		@Delete(value = "update lar_recyclingmaterial set enable=1 where recyclingTypeId = #{id}")
		int deleteByTypeId(@Param("id") String id);
		
		// 根据id删除类型
		@Delete(value = "update lar_recyclingtype set enable=1 where id = #{id}")
		int deleteTypeById(@Param("id") String id);
		
		// 添加订单，指定字段
		int insertSelective(Commodity commodity);
		
		//添加子单号
		int insertSelectiveType(CommodityType commodityType);

		// 根据条件查询所有信息
		List<Commodity> selectByExample(@Param("larPager") LarPager<Commodity> larPager);
		
		// 根据条件查询所有信息,查询子单号
		List<CommodityType> selectTypeByExample(@Param("larPager") LarPager<CommodityType> larPager);
		
		//查询一个物品
		@Select(value="SELECT r.`id`,`goodsId`,`goodsName`,`goodsDescribe`,`meteringCompany`,`imgUrl`,`startUsing`,"
				+ "`startUsingForApp`,r.`enable`,r.`createDate`,n.`id` AS \"recyclingTypeId.id\",`typeName` AS \"recyclingTypeId.typeName\","
				+ "n.`enable` AS \"recyclingTypeId.enable\",n.`createDate`  AS \"recyclingTypeId.createDate\" "
				+ "FROM `lar_recyclingmaterial` r LEFT OUTER JOIN `lar_recyclingtype` n ON(r.recyclingTypeId=n.id) where r.id=#{id}")
		Commodity selectById(String id);

		int updateByExampleSelective(@Param("params") Map<String, Object> params);
		
		int updateTypeByExampleSelective(@Param("params") Map<String, Object> params);

		@Select(value="select `id`,`typeName`,`enable`,`createDate`,`mechanismId` from `lar_commoditytype` where `enable`=0 ")
		List<CommodityType> getRecyclingTypes(@Param("mechanismId") String mechanismId);

		@Select(value="SELECT c.`id`,`shopId`,`shopName`,`shopDescribe`,`brand`,`specifications`,`shopImg`,c.`createDate`,"
				+ "c.`enable`,c.`mechanismId`,`shelfLife`,`shelfTime`,`paymentMethod`,`unitPrice`,`moneyUnit`,"
				+ "t.`id` AS \"commodityType.id\",`typeName` AS \"commodityType.typeName\","
				+ "t.`enable` AS \"commodityType.enable\",t.`createDate` AS \"commodityType.createDate\","
				+ "t.`mechanismId` AS \"commodityType.mechanismId\" "
				+ "FROM `lar_commodity` c LEFT OUTER JOIN `lar_commoditytype` t "
				+ "ON(c.`commodityType`=t.`id`) WHERE c.`enable`=0 AND t.`id`=#{id}")
		List<Commodity> getRecyclingNames(@Param("id") String id);

		@Select(value="SELECT `brand` FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{id} GROUP BY brand")
		List<String> getBrands(String id);

		@Select(value="SELECT shopName FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{id} GROUP BY shopName")
		List<String> getGoods(String id);
		
		@Update(value="update lar_commodity set paymentMethod=#{paymentMethod},unitPrice=#{unitPrice},moneyUnit=#{moneyUnit},shelfLife=#{shelfLife} where id=#{id}")
		int updateShelfLife(Map<String, Object> params);

		@Update(value="update lar_commodity set shelfTime=#{shelfTime} where id=#{id}")
		int updateDownShelfLife(Map<String, Object> params);

		@Select(value="SELECT shopName FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{mechanismId} and commodityType=#{commodityType}")
		List<String> getGoodsByctId(Map<String, Object> params);

		@Select(value="SELECT `brand` FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{mechanismId} and shopName=#{shopName}")
		List<String> getBrandsByName(Map<String, Object> params);

		@Select(value="SELECT `specifications` FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{mechanismId} and shopName=#{shopName} and brand=#{brand}")
		List<String> getSpecifications(Map<String, Object> params);
		
		@Select(value="select id from lar_commodity where enable=0 and mechanismId=#{mechanismId} and commodityType=#{commodityType} and shopName=#{shopName} and brand=#{brand} and specifications=#{specification}")
		String getCommodityId(Map<String, Object> paramsMap);

		@Select(value="SELECT `specifications` FROM `lar_commodity` WHERE ENABLE=0 AND `mechanismId`=#{id} GROUP BY specifications")
		List<String> getSpecificationsByOrgId(String id);
		
		@Delete("delete from lar_shoppingCart where appUserId=#{userId}")
		int deleteShoppingCartByUserId(@Param("userId") String userId);
		
		int saveShoppingCart(ShoppingCart shoppingCart);

		int updateShoppingCart(ShoppingCart shoppingCart);

		@Select(value="select s.id,number,appUserId,c.id as \"commodity.id\",shopId as \"commodity.shopId\",shopName as \"commodity.shopName\", "
				+ "shopDescribe as \"commodity.shopDescribe\",brand as \"commodity.brand\",specifications as \"commodity.specifications\","
				+ "shopImg as \"commodity.shopImg\",c.createDate as \"commodity.createDate\",c.enable as \"commodity.enable\",c.mechanismId as \"commodity.mechanismId\","
				+ "shelfLife as \"commodity.shelfLife\",shelfTime as \"commodity.shelfTime\",paymentMethod as \"commodity.paymentMethod\","
				+ "unitPrice as \"commodity.unitPrice\",moneyUnit as \"commodity.moneyUnit\",t.id as \"commodity.commoditytype.id\","
				+ "typeName as \"commodity.commoditytype.typeName\",t.enable as \"commodity.commoditytype.enable\",t.createDate as \"commodity.commoditytype.createDate\","
				+ "t.mechanismId as \"commodity.commoditytype.mechanismId\" "
				+ "from lar_shoppingcart s "
				+ "left outer join `lar_commodity` c on(s.commodity=c.id) "
				+ "left outer join `lar_commoditytype` t on(t.id=c.commoditytype) "
				+ "WHERE appUserId=#{userId} AND NOW() BETWEEN TIMESTAMP(c.shelfLife) AND TIMESTAMP(c.shelfTime) ")
		List<ShoppingCart> getShoppingCarts(@Param("userId") String userId);

		@Select(value="select s.id,number,appUserId,c.id as \"commodity.id\",shopId as \"commodity.shopId\",shopName as \"commodity.shopName\", "
				+ "shopDescribe as \"commodity.shopDescribe\",brand as \"commodity.brand\",specifications as \"commodity.specifications\","
				+ "shopImg as \"commodity.shopImg\",c.createDate as \"commodity.createDate\",c.enable as \"commodity.enable\",c.mechanismId as \"commodity.mechanismId\","
				+ "shelfLife as \"commodity.shelfLife\",shelfTime as \"commodity.shelfTime\",paymentMethod as \"commodity.paymentMethod\","
				+ "unitPrice as \"commodity.unitPrice\",moneyUnit as \"commodity.moneyUnit\",t.id as \"commodity.commoditytype.id\","
				+ "typeName as \"commodity.commoditytype.typeName\",t.enable as \"commodity.commoditytype.enable\",t.createDate as \"commodity.commoditytype.createDate\","
				+ "t.mechanismId as \"commodity.commoditytype.mechanismId\" "
				+ "from lar_shoppingcart s "
				+ "left outer join `lar_commodity` c on(s.commodity=c.id) "
				+ "left outer join `lar_commoditytype` t on(t.id=c.commoditytype) "
				+ "WHERE appUserId=#{userId} AND c.mechanismId=#{mechanismId} AND NOW() BETWEEN TIMESTAMP(c.shelfLife) AND TIMESTAMP(c.shelfTime) ")
		List<ShoppingCart> getShoppingCartsByCityId(@Param("userId") String userId,@Param("mechanismId") String mechanismId);

		
		int deleteShoppingCartsById(@Param("ids") List<String> ids);

		int deleteShoppingCartsByCommodity(Map<String, Object> params);

		Commodity getCommoditysById(@Param("params") Map<String, Object> params);
		
		@Insert("insert into lar_commoditytype (id,typeName,enable,createDate,sequence) values (#{id},#{typeName},#{enable},#{createDate},#{sequence})")
		int saveCommodityType(CommodityType commodityType);

		List<String> getGoodsByOrgs(@Param("orgIds")List<Long> orgIds);

		List<String> getBrandsByOrgs(@Param("orgIds")List<Long> orgIds);

		@Delete("delete from lar_shoppingcart where commodity = #{id}")
		int deleteShopCartByCommodity(@Param("id")String id);

		int getShoppingCartsCount(@Param("ids")Set<String> ids);
}
