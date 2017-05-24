package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface MaaDateSettingDao {
		// 查询总数
		@Select(value="select count(id) from lar_maadatesetting where enable = 0")
		int countById();

		// 根据id删除
		@Delete(value = "update lar_maadatesetting set enable=1 where id = #{id}")
		int deleteById(@Param("id") String id);
		
		// 根据条件查询所有信息
		List<MaaDateSetting> selectByExample(@Param("larPager") LarPager<MaaDateSetting> larPager);
		
		// 更新用户，指定字段(有条件的)
		@Update(value="update lar_maadatesetting set startDate=#{params.maaDateSetting.startDate},"
				+ "endDate=#{params.maaDateSetting.endDate},enable=#{params.maaDateSetting.enable},"
				+ "createDate=#{params.maaDateSetting.createDate} where id=#{params.maaDateSetting.id}")
		int updateByExampleSelective(@Param("params") Map<String, Object> params);
		
		@Insert(value="insert into lar_maadatesetting (`id`,`startDate`,`endDate`,`enable`,`createDate`) "
				+ "values (#{maaDateSetting.id},#{maaDateSetting.startDate},#{maaDateSetting.endDate},#{maaDateSetting.enable},#{maaDateSetting.createDate})")
		int insertSelective(@Param("maaDateSetting") MaaDateSetting maaDateSetting);

		@Select("SELECT o.`id`,`areaSettingId` AS \"areaSetting.id\",`typeId`,`osId`,`osName`,`abbreviation`,`osAddress`,`contactsName`,"
				+ "`contactsTel`,o.`createDate` FROM `lar_ownedsupplier` o LEFT OUTER JOIN `lar_areasettings` a ON(o.areaSettingId=a.id)"
				+ " WHERE `mechanismId` = #{id}")
		List<OwnedSupplier> getOwnedSuppliersById(@Param("id") String id);
}
