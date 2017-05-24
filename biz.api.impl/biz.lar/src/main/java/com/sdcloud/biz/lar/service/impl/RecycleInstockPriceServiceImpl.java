package com.sdcloud.biz.lar.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleInstockPrice;
import com.sdcloud.api.lar.service.RecycleInstockPriceService;
import com.sdcloud.biz.lar.dao.RecycleInstockPriceDao;
import com.sdcloud.framework.common.UUIDUtil;

import io.jsonwebtoken.lang.Collections;

@Service
@Transactional(readOnly=true)
public class RecycleInstockPriceServiceImpl extends BaseServiceImpl<RecycleInstockPrice> implements RecycleInstockPriceService{
	
	@Autowired
	private RecycleInstockPriceDao recycleInstockPriceDao;

	
	@Override
	public boolean checkPriceForInstock(Long orgId, Date date, Long specId, Long priceId) {
		//查询该机构某个规格是否已经维护了平均价格，没有的话，新增，有的话更新。
		List<RecycleInstockPrice> prices = getPrice( orgId, getMonth(date),  specId);
		boolean success = false;
		if(Collections.isEmpty(prices)){
			success = this.save(new RecycleInstockPrice(orgId, getMonth(date), specId, priceId));
		}else if(prices.size()<2){
			success = this.update(prices.get(0));
		}	
		return success;
	}

	@Override
	public boolean checkPriceForOutstock(Long orgId, Date date, Long specId, Long priceId) {
		//查询该机构某个规格是否已经维护了平均价格，有的话，不做任何操作，没有的话，去复制已经维护过的。
		List<RecycleInstockPrice> prices = getPrice( orgId, getMonth(date),  specId);
		if(Collections.isEmpty(prices)){
			return recycleInstockPriceDao.copyHistory(new RecycleInstockPrice(UUIDUtil.getUUNum(),orgId, getMonth(date), specId, priceId ,date))>0;
		}
		return true;
	}

	@Override
	public List<RecycleInstockPrice> getPrice(Long orgId, Long month, Long specId) {
		
		return recycleInstockPriceDao.getPrice( orgId, month, specId);
	}

	Long getMonth(Date date){
		String format = new SimpleDateFormat("yyyyMM").format(date);
		return Long.parseLong(format);
	};
	
}
