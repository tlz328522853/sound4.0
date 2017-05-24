package com.sdcloud.biz.cache.redis.dao;

import org.springframework.data.redis.core.ListOperations;

/**
* @author jzc
* @version 2016年9月13日 下午4:40:43
* ListOperationsDao描述:
*/
public interface ListOperationsDao extends ListOperations<String, String> {

}
