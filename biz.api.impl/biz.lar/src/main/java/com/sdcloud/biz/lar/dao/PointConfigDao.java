package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.PointConfig;
/**
 * 
 * @author dingx
 *
 */
public interface PointConfigDao{
	
	@Select("select id,org,pointRate,pointLine "
			+ "from lar_point_config WHERE org=#{org}")
	PointConfig findByOrg(@Param("org") String org);
	
	@Update("update lar_point_config "
			+ "set pointRate=#{pointRate},pointLine=#{pointLine} "
			+ "where org=#{org} ")
	int updatePointConfig(PointConfig t);
	
	@Insert(value = "insert into lar_point_config (id,org, pointRate, pointLine, createUser, createDate) "
			+ "values (#{id},#{org},#{pointRate},#{pointLine},#{createUser},#{createDate})")
	Boolean savePointConfig(PointConfig pointConfig);
	

}
