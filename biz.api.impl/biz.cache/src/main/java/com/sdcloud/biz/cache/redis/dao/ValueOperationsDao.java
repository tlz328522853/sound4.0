package com.sdcloud.biz.cache.redis.dao;

import org.springframework.data.redis.core.ValueOperations;

/**
* @author jzc
* @version 2016年9月13日 上午11:55:11
* ValueOperationsService描述:
*/
public interface ValueOperationsDao extends ValueOperations<String,String> {

}
