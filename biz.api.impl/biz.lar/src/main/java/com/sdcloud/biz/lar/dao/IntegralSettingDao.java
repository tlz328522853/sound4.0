package com.sdcloud.biz.lar.dao;

import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.IntegralSetting;

@Repository
public interface IntegralSettingDao extends BaseDao<IntegralSetting>{

	//查询积分等级
	IntegralSetting getByLevel(Integer level);

}
