package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.ExpressApply;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_express_apply 快递申请记录
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface ExpressApplyDao extends BaseDao<ExpressApply> {

	int countByApplyId(@Param("applyId") Integer applyId);
	
	
}
