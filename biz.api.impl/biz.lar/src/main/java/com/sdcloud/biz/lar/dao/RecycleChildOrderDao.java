package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleChildOrder;

@Repository
public interface RecycleChildOrderDao extends BaseDao<RecycleChildOrder>{
	
	Integer saveList(@Param("orders")List<RecycleChildOrder> orders);
}
