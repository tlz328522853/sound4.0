package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.ExpressFind;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_express_find 快递查询
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface ExpressFindDao extends BaseDao<ExpressFind> {

	int countByFindId(@Param("findId") Integer findId);
	
	
}
