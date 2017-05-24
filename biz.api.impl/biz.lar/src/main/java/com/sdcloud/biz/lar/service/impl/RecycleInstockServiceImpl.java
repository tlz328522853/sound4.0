package com.sdcloud.biz.lar.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.entity.RecycleStock;
import com.sdcloud.api.lar.service.RecycleInstockPriceService;
import com.sdcloud.api.lar.service.RecycleInstockService;
import com.sdcloud.api.lar.service.RecycleStockService;
import com.sdcloud.biz.lar.dao.RecycleInstockDao;
import com.sdcloud.framework.entity.LarPager;

import io.jsonwebtoken.lang.Collections;

/**
 * lar_recycle_instock 入库管理
 * 
 * @author luorongjie
 * @date 2016-12-05
 * @version 1.0
 */
@Service
@Transactional
public class RecycleInstockServiceImpl extends BaseServiceImpl<RecycleInstock> implements RecycleInstockService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecycleStockService recycleStockService;
	
	@Autowired
	private RecycleInstockDao recycleInstockDao;
	
	@Autowired
	private RecycleInstockPriceService recycleInstockPriceService;

	/**
	 * 审核入库
	 * 1，更新入库单为已审核，已入库
	 * 2，查看该机构下，该条规格是否已有过库存，如果有，则修改库存，如果没有则新增一条库存记录。
	 */
	@Override
	@Transactional(readOnly = false)
	public boolean audit(RecycleInstock recycleInstock) {

		Boolean update = this.update(recycleInstock);
		if(!update){
			return false;
		}

		logger.info("method {},result update:{}",Thread.currentThread().getStackTrace()[1].getMethodName(),update);
		
		LarPager<RecycleStock> larPager = new LarPager<>();
		larPager.getExtendMap().put("spec_id", recycleInstock.getSpecId());
		List<Long> ids = new ArrayList<>();
		ids.add(recycleInstock.getOrgId());
		recycleStockService.findStockByOrgIds(larPager, ids);
		Boolean isSuccess = false;
		if (!Collections.isEmpty(larPager.getResult()) && larPager.getResult().size() == 1) {
			logger.info("method {},result to update",Thread.currentThread().getStackTrace()[1].getMethodName());
			// 修改
			RecycleStock recycleStock = larPager.getResult().get(0);
			recycleStock.setSumInstockNum(recycleStock.getSumInstockNum().add(recycleInstock.getNum()) );
			recycleStock.setStockNum(recycleStock.getStockNum() .add(recycleInstock.getNum()) );
			
			isSuccess = recycleStockService.update(recycleStock);
			logger.info("method {},result to upeate, result :{}",Thread.currentThread().getStackTrace()[1].getMethodName(),isSuccess);
		} else {
			logger.info("method {},result to save",Thread.currentThread().getStackTrace()[1].getMethodName());
			// 新增
			RecycleStock stock = new RecycleStock(recycleInstock.getOrgId(), recycleInstock.getPriceId(),
					recycleInstock.getSpecId(),recycleInstock.getNum(), new BigDecimal(0L), recycleInstock.getNum(), new BigDecimal(0L));
			stock.setVersion(0L);
			isSuccess =recycleStockService.save(stock);
			logger.info("method {},result to save, result :{}",Thread.currentThread().getStackTrace()[1].getMethodName(),isSuccess);
		}

		//维护月平均销售单价
		boolean checkPriceForInstock = recycleInstockPriceService.checkPriceForInstock(recycleInstock.getOrgId(), recycleInstock.getAuditDate(), recycleInstock.getSpecId(), recycleInstock.getPriceId());
		if(!(checkPriceForInstock && isSuccess)){
			throw new RuntimeException("审核失败！");
		}
		
		return  checkPriceForInstock && isSuccess;
	}

	@Override
	public Boolean existByInstockNo(Long instockId,String instockNo) {
		return recycleInstockDao.existByInstockNo(instockId,instockNo)>0;
		
	}
	
	@Override
    @Transactional(readOnly = false)
    public Boolean update(RecycleInstock t) {
        int count = recycleInstockDao.update(t);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

}
