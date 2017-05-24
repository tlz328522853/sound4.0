package com.sdcloud.biz.lar.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.ItemPointStatistics;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface SalesmanDao {
		// 查询总数
		@Select(value = "select count(s.id) from lar_salesman s left outer join lar_areasettings a on(s.areaSettingId=a.id) where s.enable = 0 and a.mechanismId=#{mechanismId}")
		int countById(@Param("mechanismId") String mechanismId);

		// 根据id删除
		@Delete(value = "update lar_salesman set enable=1 where id = #{id}")
		int deleteById(@Param("id") String id);
		
		@Select(value = "select `id`,`areaSettingId` as \"areaSetting.id\",`manId`,`manName`,`deviceId`,`ownedSupplierId` as \"ownedSupplier.id\",`manDescribe`,`personnelId`,`createDate`,`enable`,`integral`,`rechargeIntegral`,`giveIntegral` FROM `lar_salesman`  WHERE id=#{id} and enable=0")
		Salesman getSalesManById(@Param("id") String id);
		
		// 根据条件查询所有信息
		List<Salesman> selectByExample(@Param("larPager") LarPager<Salesman> larPager);
		
		// 根据条件统计业务员积分信息
		List<Salesman> salesManPointStatistics(@Param("larPager") LarPager<Salesman> larPager);
		
		// 根据条件统计公司积分信息
		List<ItemPointStatistics> itemPointStatistics(@Param("larPager") LarPager<ItemPointStatistics> larPager);
		
		// 更新用户，指定字段(有条件的)
		int updateByExampleSelective(@Param("params") Map<String, Object> params);
		//根据用户ID更新
		int updateByEmployeeId(@Param("params") Map<String, Object> params);
		
		@Select(value="select `id`,`typeName`,`enable` from lar_recyclingType where enable=0")
		List<RecyclingType> getRecyclingTypes();

		@Select(value="SELECT r.`id`,`recyclingTypeId`,`goodsId`,`goodsName`,`goodsDescribe`,`meteringCompany`,"
				+ "`imgUrl`,`startUsing`,`startUsingForApp`,r.`enable`,n.`id` AS \"recyclingTypeId.id\","
						+ "`typeName` AS \"recyclingTypeId.typeName\",n.`enable` AS \"recyclingTypeId.enable\" "
						+ "FROM `lar_recyclingmaterial` r LEFT OUTER JOIN `lar_recyclingtype` n ON(r.recyclingTypeId=n.id) WHERE recyclingTypeId=#{id} and r.enable=0")
		List<RecyclingMaterial> getRecyclingNames(@Param("id") String id);
		
		@Update("update lar_ordermanager set orderStatusId=#{orderStatusId},cancelDate=#{cancelDate} where id=#{id}")
		int cancelOrderById(@Param("id") String id, @Param("orderStatusId") int orderStatusId,@Param("cancelDate") Date date);

		@Update("update lar_ordermanager set orderStatusId=#{params.orderStatusId},takeDate=#{params.takeDate},userId=#{params.userId},userName=#{params.userName} where id=#{params.id}")
		int comeOrderById(@Param("params") Map<String, Object>updateParams);

		//根据片区ID查询业务员
		@Select("SELECT `id`,`areaSettingId` as \"areaSetting.id\",`manId`,`manName`,`deviceId`,`ownedSupplierId` as \"ownedSupplier.id\",`manDescribe`,`personnelId`,`createDate`,`enable` FROM `lar_salesman`  WHERE enable=0 and areaSettingId=#{id}")
		List<Salesman> getSalesmansByAreaId(@Param("id") String id);
		
		//根据片区ID查询业务员
		List<Salesman> getSalesmansByAreaIds(@Param("areaSettings") List<AreaSetting> areaSettings);
		
		@Update("update lar_ordermanager set areaSettingId=#{param.areaSetting},distributeDate=#{param.distributeDate},"
				+ "orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},salesmanId=#{param.salesman},"
				+ "distributeIllustrate=#{param.distributeIllustrate}  where id=#{param.id}")
		int updateDispatchOrder(@Param("param") Map<String, Object> params);

		@Update("update lar_ordermanager set cancelIllustrate=#{param.cancelIllustrate},cancelDate=#{param.cancelDate},orderStatusId=#{param.orderStatusId}  where id=#{param.id}")
		int cancelDispatchOrder(@Param("param") Map<String, Object> params);

		@Select("SELECT o.`id`,`areaSettingId` AS \"areaSetting.id\",`typeId`,`osId`,`osName`,`abbreviation`,`osAddress`,`contactsName`,"
				+ "`contactsTel`,o.`createDate` FROM `lar_ownedsupplier` o LEFT OUTER JOIN `lar_areasettings` a ON(o.areaSettingId=a.id)"
				+ " WHERE `mechanismId` = #{id}")
		List<OwnedSupplier> getOwnedSuppliersById(@Param("id") String id);

		int insertSelective(Salesman salesman);

		long countByConditron(@Param("params") Map<String, Object> params);

		List<Salesman> getSalesmansByConditron(@Param("larPager") LarPager<Salesman> pager);
		
		@Select(value="SELECT s.`id`,s.`personnelId`,`manId`,`manName` FROM `lar_salesman` s LEFT OUTER JOIN `lar_areasettings` a ON(s.areaSettingId=a.id) WHERE s.`enable` = 0 and a.mechanismId=#{id}")
		List<Salesman> getareaNamesByOId(@Param("id") String id);
		
		@Select(value="select integral from lar_salesman where id=#{id}")
		int getSalesmanPoints(@Param("id") String id);
		
		@Update(value="update lar_salesman set integral=integral-#{integral},expenditureIntegral=expenditureIntegral+#{integral} where id=#{id}")
		int updateSalesmanPoints(@Param("integral") String integral,@Param("id")String id);

		//@Select(value="SELECT `id`, `manId`,`manName` FROM `lar_salesman` where `personnelId` = #{personnelId}")
		List<Salesman> getByPersonnelId(@Param("personnelId") String personnelId);

		@Select("select count(id) "
				+ "from lar_salesman WHERE `areaSettingId` = #{areaId} and enable=0")
		Long selectByArea(@Param("areaId")String areaId);

		List<String> selectPersonnelIdList(Map<String, Object> params);

		long countByIds(@Param("params")Map<String, Object> params);

		Long countByEmploy(@Param("larPager") LarPager<Salesman> larPager,@Param("ids") List<Long> ids);

		List<Salesman> findEmploy(@Param("larPager") LarPager<Salesman> larPager, @Param("ids") List<Long> ids);
}
