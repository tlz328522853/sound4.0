package com.sdcloud.biz.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.core.entity.Region;


@Repository
public interface RegionDao {

	//根据父ID查询
	@Select(value="select regionId,regionCode,regionName,parentId,regionLevel,regionOrder,regionNameEn,regionShortNameEn from pf_region where parentId=#{id}")
	public List<Region> getLarRegions(@Param("id") Integer id);
	/**
	 * 查询所有行政区划
	 * @return
	 */
	@Select(value="select regionId,regionName,regionNameEn,regionShortNameEn from pf_region ")
	public List<Region> findAll();
}