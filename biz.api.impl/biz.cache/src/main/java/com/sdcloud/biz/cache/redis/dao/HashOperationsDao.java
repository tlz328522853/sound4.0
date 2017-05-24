package com.sdcloud.biz.cache.redis.dao;

import org.springframework.data.redis.core.HashOperations;

/**
* @author jzc
* @version 2016年9月13日 下午1:50:09
* HashOperationsService描述:
*/
public interface HashOperationsDao extends HashOperations<String, String, String> {

}
