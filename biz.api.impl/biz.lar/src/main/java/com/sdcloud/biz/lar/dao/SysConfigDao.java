package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.SysConfig;

@Repository
public interface SysConfigDao extends BaseDao<SysConfig> {

	@Select("select type,value "
			+ "from lar_sys_config ")
	List<SysConfig> findList();
	
	
	@Update("update lar_sys_config "
			+ "set value=#{value} "
			+ "where type=#{type} ")
	@Override
	int update(SysConfig t);

}
