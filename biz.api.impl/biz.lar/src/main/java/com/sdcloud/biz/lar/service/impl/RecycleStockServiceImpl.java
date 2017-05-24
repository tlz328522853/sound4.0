package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleStock;
import com.sdcloud.api.lar.service.RecycleStockService;
import com.sdcloud.biz.lar.dao.RecycleStockDao;
import com.sdcloud.framework.entity.LarPager;

/**
 * lar_recycle_stock 
 * @author luorongjie
 * @date 2016-12-02
 * @version 1.0
 */
@Service
@Transactional(readOnly=true)
public class RecycleStockServiceImpl extends BaseServiceImpl<RecycleStock> implements RecycleStockService{
	
	@Autowired
	private RecycleStockDao recycleStockDao;

	@Override
	public void findStockByOrgIds(LarPager<RecycleStock> larPager, List<Long> ids) {
		
		List<RecycleStock> result = recycleStockDao.findStockByOrgIds(larPager, ids);
        larPager.setResult(result);
        larPager.setTotalCount(recycleStockDao.countStockByOrgIds(larPager, ids));
	}
}
