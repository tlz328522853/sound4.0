package com.sdcloud.biz.lar.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.RecycleDetail;
import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.entity.RecycleOutstock;

/**
 * 出入库明细
 * 
 */
@Repository
public interface RecycleDetailDao extends BaseDao<RecycleDetail> {

	RecycleOutstock getByIds(Long id);

	RecycleInstock getInByIds(Long id);

	int updateCheck(Date updateAccount);
	
	
}
