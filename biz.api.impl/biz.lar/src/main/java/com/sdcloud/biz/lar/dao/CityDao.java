package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.framework.entity.LarPager;

/**
 * 
 * @author wrs
 */
@Repository
public interface CityDao extends BaseDao<City> {
	/**
	 * 服务城市
	 */
	List<City> serviceCity();
	City selectByCityId(@Param("id") Long id);
	/**
	 * 根据城市名称获取城市
	 * @param regionName
	 * @return
	 */
	@Select("SELECT id,regionId,regionName,cityOrder,org FROM `lar_city` WHERE locate(regionName,#{regionName})!=0 ")
	List<City> findByRegionName(@Param("regionName") String regionName);
	/**
	 * 根据机构ID获取城市
	 * @param org
	 * @return
	 */
	@Select("SELECT id,regionId,regionName,cityOrder,org FROM `lar_city` WHERE org=#{org}")
	List<City> findByOrg(@Param("org") String org);
	
	Long countByOrgIds(@Param("larPager") LarPager<City> larPager, @Param("ids") List<Long> list);
	List<City> findByOrgIds(@Param("larPager") LarPager<City> larPager, @Param("ids") List<Long> ids);
	
	@Select("SELECT id,regionId,regionName,cityOrder,org FROM `lar_city` WHERE id=#{id}")
	City selectById(@Param("id")Long id);
}
