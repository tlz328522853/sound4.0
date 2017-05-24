package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.Score;
import com.sdcloud.framework.entity.LarPager;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by 韩亚辉 on 2016/4/11.
 */
@Repository
public interface SalesManScoreDao extends BaseDao<Score>{

	List<Long> getEchargeUser(@Param("larPager")LarPager<Score> larPager, @Param("ids")List<Long> ids);
}
