package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface AreaSettingDao {
	// 查询总数
	@Select(value = "select count(id) from lar_areasettings  where enable = 0 and mechanismId=#{mechanismId}")
	int countById(@Param("mechanismId") String mechanismId);

	// 根据id删除
	@Delete(value = "update lar_areasettings set enable=1 where id = #{id}")
	int deleteById(@Param("id") String id);

	// 添加用户，指定字段
	int insertSelective(AreaSetting areaSetting);

	// 添加用户，指定字段，获得ID
	int insertUserGetId(AreaSetting areaSetting);

	// 根据条件查询所有信息
	List<AreaSetting> selectByExample(@Param("larPager") LarPager<AreaSetting> larPager);

	// 根据条件更改删除状态
	int deleteByExample(@Param("params") Map<String, Object> params);

	// 根据id查询
	@Select(value = "select id,mechanism, taskTime, areaName, acreage, areaPosition, startDate, endDate, remarks, createDate, enable,mechanismId from lar_areasettings  where id = #{id} and enable=0")
	AreaSetting selectByPrimaryKey(@Param("id") String id);
	
	// 根据id查询
	@Select(value = "select id,mechanism, taskTime, areaName, acreage, areaPosition, startDate, endDate, remarks, createDate, enable,mechanismId from lar_areasettings  where mechanismId = #{id} and enable=0")
	AreaSetting selectByMechanismId(@Param("id") String id);

	// 更新用户，指定字段(有条件的)
	int updateByExampleSelective(@Param("params") Map<String, Object> params);
	
	/**
	 * 修改：根据机构ID 查找改机构下的 回收片区
	 * @author jzc 2016年6月16日
	 * @param mechanismId
	 * @return
	 */
	@Select(value="select id,mechanism, taskTime, areaName, acreage, areaPosition, startDate, endDate, remarks, createDate, enable,mechanismId from lar_areasettings  where mechanismId = #{mechanismId} and enable=0 order by createDate ASC")
	List<AreaSetting> selectAreaById(@Param("mechanismId") String mechanismId);

	long countByIds(@Param("params")Map<String, Object> params);

}
