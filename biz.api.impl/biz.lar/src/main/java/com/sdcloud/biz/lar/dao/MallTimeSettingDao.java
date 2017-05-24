package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.entity.MallTimeSetting;
import com.sdcloud.framework.entity.LarPager;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Created by dingx on 2016/7/11.
 */
@Repository
public interface MallTimeSettingDao extends BaseDao<MallTimeSetting>{
	
	// 根据条件查询所有信息
	List<MallTimeSetting> selectByExample(@Param("larPager") LarPager<MallTimeSetting> pager);
	
	// 查询总数
	@Select(value="select count(id) from lar_mallTimeSetting where enable = 0")
	int countById();
}
