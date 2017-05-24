package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleRetailerAccurl;
import com.sdcloud.api.lar.entity.RecycleVendorAccurl;
import com.sdcloud.api.lar.service.RecycleRetailerAccurlService;
import com.sdcloud.biz.lar.dao.RecycleRetailerAccurlDao;

/**
 * lar_recycle_retailer_accurl 销售商附件
 * @author TLZ
 * @date 2016-12-28
 * @version 1.0
 */
@Service
@Transactional(readOnly=true)
public class RecycleRetailerAccurlServiceImpl extends BaseServiceImpl<RecycleRetailerAccurl> implements RecycleRetailerAccurlService{

	@Autowired
	private RecycleRetailerAccurlDao recycleRetailerAccurlDao;
	
	@Override
	public List<RecycleRetailerAccurl> findById(Long id) {
		List<RecycleRetailerAccurl> list=null;
		try {
			list = recycleRetailerAccurlDao.findById(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}


}
