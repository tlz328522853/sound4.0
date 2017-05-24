package com.sdcloud.biz.lar.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleDetail;
import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.service.RecycleDetailService;
import com.sdcloud.biz.lar.dao.RecycleDetailDao;

/**
 *  出入库明细
 * 
 *
 */
@Service
@Transactional(readOnly = true)
public class RecycleDetailServiceImpl extends BaseServiceImpl<RecycleDetail> implements RecycleDetailService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecycleDetailDao recycleDetailDao;
	
	
	@Override
	public RecycleOutstock getByIds(Long id) {
		
		RecycleOutstock recycleOutstock =null;
		
		try {
			
			recycleOutstock =recycleDetailDao.getByIds(id);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return recycleOutstock;
	}


	@Override
	public RecycleInstock getInByIds(Long id) {
		RecycleInstock recycleInstock =null;
		
		try {
			
			recycleInstock =recycleDetailDao.getInByIds(id);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return recycleInstock;
	}


	@Override
	@Transactional(readOnly = false)
	public Boolean updateCheck(Date updateAccount) {
		int updateCheck =0;
		try {
			
			updateCheck = recycleDetailDao.updateCheck(updateAccount);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		if(updateCheck ==0){
			return false;
		}else{
			return true;
		}
	}

	
}
