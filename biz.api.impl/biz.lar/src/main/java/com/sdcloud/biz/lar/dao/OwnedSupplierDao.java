package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.Personnel;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface OwnedSupplierDao {
		// 查询总数
		@Select(value = "select count(s.id) from lar_ownedsupplier s left outer join lar_areasettings a on(s.areaSettingId=a.id) where s.enable = 0 and a.mechanismId=#{mechanismId}")
		int countById(@Param("mechanismId") String mechanismId);

		// 根据id删除
		@Delete(value = "update lar_ownedsupplier set enable=1 where id = #{id}")
		int deleteById(@Param("id") String id);
		
		// 根据条件查询所有信息
		List<OwnedSupplier> selectByExample(@Param("larPager") LarPager<OwnedSupplier> larPager);
		
		// 更新用户，指定字段(有条件的)
		int updateByExampleSelective(@Param("params") Map<String, Object> params);
		
		@Select("SELECT o.`id`,`areaSettingId` AS \"areaSetting.id\",`typeId`,`osId`,`osName`,`abbreviation`,`osAddress`,`contactsName`,"
				+ "`contactsTel`,o.`createDate` FROM `lar_ownedsupplier` o LEFT OUTER JOIN `lar_areasettings` a ON(o.areaSettingId=a.id)"
				+ " WHERE `mechanismId` = #{id}")
		List<OwnedSupplier> getOwnedSuppliersById(@Param("id") String id);
		
		/*//根据片区ID查询业务员
		@Select("SELECT `id`,`areaSettingId` as \"areaSetting.id\",`manId`,`manName`,`deviceId`,`ownedSupplierId` as \"ownedSupplier.id\",`manDescribe`,`personnelId` as \"personnel.id\",`createDate`,`enable` FROM `lar_salesman`  WHERE areaSettingId=#{id}")
		List<OwnedSupplier> getSalesmansByAreaId(@Param("id") String id);*/

		int insertSelective(OwnedSupplier ownedSupplier);
		/*@Select("SELECT p.`id`,`customerId`,`name`,p.`enable`,`areaSettingId` AS \"areaSetting.id\" "
				+ "FROM `lar_personnel` p LEFT OUTER JOIN `lar_areasettings` a ON(p.areaSettingId=a.id) WHERE `mechanismId` = #{id}")
		List<Personnel> getPersonnelsById(@Param("id") String id);*/


}
