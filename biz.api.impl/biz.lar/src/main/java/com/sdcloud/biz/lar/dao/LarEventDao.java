package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.LarEvent;
import com.sdcloud.api.lar.entity.MyPoints;

@Repository
public interface LarEventDao extends BaseDao<LarEvent> {

	List<MyPoints> getOrderByUserId(@Param("params")Map<String, Object> params);

}
