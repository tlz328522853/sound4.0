package com.sdcloud.biz.core.dao;

import org.apache.ibatis.annotations.Param;

public interface RegionCodeDupDao {

	String findOne();

	void update(@Param("regionIndex") String regionIndex);

	void insert(@Param("regionIndex") String regionIndex);

}
