package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.RecyclingList;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface RecyclingItemsDao {
	// 查询总数
	@Select(value = "select count(id) from lar_recyclingmaterial where enable = 0")
	int countById();

	// 查询子单号
	@Select(value = "select count(id) from lar_recyclingtype  where enable = 0")
	int countByType();

	// 根据id删除
	@Delete(value = "update lar_recyclingmaterial set enable=1 where id = #{id}")
	int deleteById(@Param("id") String id);

	// 根据类型id删除
	@Delete(value = "update lar_recyclingmaterial set enable=1 where recyclingTypeId = #{id}")
	int deleteByTypeId(@Param("id") String id);

	// 根据id删除类型
	@Delete(value = "update lar_recyclingtype set enable=1 where id = #{id}")
	int deleteTypeById(@Param("id") String id);

	// 添加订单，指定字段
	int insertSelective(RecyclingMaterial recyclingMaterial);

	// 添加子单号
	int insertSelectiveType(RecyclingType recyclingType);

	// 根据条件查询所有信息
	List<RecyclingMaterial> selectByExample(@Param("larPager") LarPager<RecyclingMaterial> larPager);

	// 根据条件查询所有信息,查询子单号
	List<RecyclingType> selectTypeByExample(@Param("larPager") LarPager<RecyclingType> larPager);

	// 查询一个物品
	@Select(value = "SELECT r.`id`,`goodsId`,`goodsName`,`goodsDescribe`,`meteringCompany`,`imgUrl`,`startUsing`,"
			+ "`startUsingForApp`,r.`enable`,r.`createDate`,n.`id` AS \"recyclingTypeId.id\",`typeName` AS \"recyclingTypeId.typeName\","
			+ "n.`enable` AS \"recyclingTypeId.enable\",n.`createDate`  AS \"recyclingTypeId.createDate\" "
			+ "FROM `lar_recyclingmaterial` r LEFT OUTER JOIN `lar_recyclingtype` n ON(r.recyclingTypeId=n.id) where r.id=#{id}")
	RecyclingMaterial selectById(String id);

	// 更新用户，指定字段(有条件的)
	int updateByExampleSelective(@Param("params") Map<String, Object> params);

	// 更新子单号
	int updateTypeByExampleSelective(@Param("params") Map<String, Object> params);

	@Select(value = "select `id`,`typeName`,`enable`,createDate from lar_recyclingtype where enable=0")
	List<RecyclingType> getRecyclingTypes();

	@Select(value = "SELECT r.`id`,`recyclingTypeId`,`goodsId`,`goodsName`,`goodsDescribe`,`meteringCompany`,"
			+ "`imgUrl`,`startUsing`,`startUsingForApp`,r.`enable`,n.`id` AS \"recyclingTypeId.id\","
			+ "`typeName` AS \"recyclingTypeId.typeName\",n.`enable` AS \"recyclingTypeId.enable\" "
			+ "FROM `lar_recyclingmaterial` r LEFT OUTER JOIN `lar_recyclingtype` n ON(r.recyclingTypeId=n.id) "
			+ "WHERE recyclingTypeId=#{id} and r.enable=0 and r.startUsingForApp = 1 and r.startUsing = 1")
	List<RecyclingMaterial> getRecyclingNames(@Param("id") String id);
	
	List<RecyclingMaterial> getOrgRecyclingNames(@Param("orgs") List<Long> orgs,@Param("type")Long type,@Param("priceEnable")Boolean priceEnable);

	List<RecyclingMaterial> getOrgRecyclingAllNames(@Param("orgs") List<Long> orgs,@Param("type")Long type,@Param("priceEnable")Boolean priceEnable);

	@Update("update lar_recyclingmaterial set startUsing=#{status} where id=#{id}")
	int updateStartUsing(@Param("id") String id, @Param("status") String status);

	@Update("update lar_recyclingmaterial set startUsingForApp=#{status} where id=#{id}")
	int updateStartUsingForApp(@Param("id") String id, @Param("status") String status);

	List<RecyclingList> getRecyclingList();

	int deleteRecoveryBluesByIds(@Param("ids") List<String> ids);

	int saveRecoveryBlue(RecoveryBlue recoveryBlue);

	@Select(value = "SELECT r.id,`number`,`appUserId`,e.id AS \"recyclingMaterial.id\",`goodsId` AS \"recyclingMaterial.goodsId\","
			+ "`goodsName` AS \"recyclingMaterial.goodsName\",`goodsDescribe` AS \"recyclingMaterial.goodsDescribe\","
			+ "`meteringCompany` AS \"recyclingMaterial.meteringCompany\",`imgUrl` AS \"recyclingMaterial.imgUrl\","
			+ "t.id AS \"recyclingMaterial.recyclingTypeId.id\",t.`typeName` AS \"recyclingMaterial.recyclingTypeId.typeName\" "
			+ "FROM `lar_recoveryblue` r LEFT OUTER JOIN `lar_recyclingmaterial` e ON(r.`recyclingMaterial`=e.id) LEFT OUTER JOIN `lar_recyclingtype` t ON(e.`recyclingTypeId`=t.id) WHERE appUserId=#{userId}")
	List<RecoveryBlue> getRecoveryBlues(@Param("userId") String userId);

	@Delete("delete from lar_recoveryblue where appUserId=#{userId}")
	void deleteRecoveryBluesByUserId(@Param("userId") String userId);
	/**
	 * 根据用户id和回收物id查询回收篮物品id
	 * @param userId
	 * @param recyIds
	 * @return
	 */
	@Select("select id from lar_recoveryblue where appUserId=#{userId} and recyclingMaterial in(${recyIds})")
	List<String> findByUserAndRecy(@Param("userId") String userId,@Param("recyIds") String recyIds);
	@Insert("insert into lar_recyclingtype (id,typeName,enable,createDate,sequence) values (#{id},#{typeName},#{enable},#{createDate},#{sequence})")
	int saveRecyclingType(RecyclingType recyclingType);

	List<RecyclingList> getCityRecyclingList(@Param("orgId")Long orgId);

	List<RecyclingType> getOrgRecyclingTypes(@Param("orgs")List<Long> orgs,@Param("priceEnable")Boolean priceEnable);
	
	List<RecyclingType> getOrgRecyclingAllTypes(@Param("orgs")List<Long> orgs,@Param("priceEnable")Boolean priceEnable);
}
