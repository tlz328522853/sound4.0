package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleVendorAccurl;
import com.sdcloud.api.lar.service.RecycleVendorAccurlService;
import com.sdcloud.biz.lar.dao.RecycleVendorAccurlDao;

/**
 * lar_recycle_vendor_accurl 供应商附件路径
 * @author TLZ
 * @date 2016-12-21
 * @version 1.0
 */
@Service
@Transactional(readOnly=true)
public class RecycleVendorAccurlServiceImpl extends BaseServiceImpl<RecycleVendorAccurl> implements RecycleVendorAccurlService{

	@Autowired
	private RecycleVendorAccurlDao recycleVendorAccurlDao;
	
	@Override
	@Transactional(readOnly=false)
	public boolean updateBatch(RecycleVendorAccurl recycleVendorAccurl) {
		int updateBatch =0;
		
		try {
			updateBatch =recycleVendorAccurlDao.updateBatch(recycleVendorAccurl);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		if(updateBatch != 0){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public List<RecycleVendorAccurl> findById(Long id) {
		List<RecycleVendorAccurl> list=null;
		try {
			list = recycleVendorAccurlDao.findById(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}


}
