package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.LarRegion;

@Repository
public interface LarRegionDao {

	//根据父ID查询
	@Select(value="select regionId,regionCode,regionName,parentId,regionLevel,regionOrder,regionNameEn,regionShortNameEn from lar_region where parentId=#{id}")
	public List<LarRegion> getLarRegions(@Param("id") Integer id);
	/**
	 * 查询所有行政区划
	 * @return
	 */
	@Select(value="select regionId,regionName,regionNameEn,regionShortNameEn from lar_region ")
	public List<LarRegion> findAll();
}