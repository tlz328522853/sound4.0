package com.sdcloud.biz.lar.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleRetailer;
import com.sdcloud.api.lar.service.RecycleRetailerService;
import com.sdcloud.biz.lar.dao.RecycleRetailerDao;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * lar_recycle_retailer 销售客户管理
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@Service
@Transactional
public class RecycleRetailerServiceImpl extends BaseServiceImpl<RecycleRetailer> implements RecycleRetailerService{

	@Autowired
	private RecycleRetailerDao recycleRetailerDao;
	
	@Transactional(readOnly=false)
	public Boolean save(RecycleRetailer recycleRetailer)  {
		if (recycleRetailer != null) {
			int count = 0;
			try {
				recycleRetailer.setId(Long.valueOf(UUIDUtil.getUUNum()));
				recycleRetailer.setRetailerCoding("KH"+String.valueOf(UUIDUtil.getUUNum()).substring(0,5));
				recycleRetailer.setEnable(0);
				Date date = new Date();
				recycleRetailer.setCreateTime(date);
				
				count = recycleRetailerDao.save(recycleRetailer);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("recycleRetailer is null");
		}
		
	}

	@Override
	public boolean updateAccurlId(Long retailerId) {
		
		int b=0;
		try {
			b = recycleRetailerDao.updateAccurlId(retailerId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (b == 0) {
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public RecycleRetailer existByRetailer(Long mechanismId, String retailer) {
		// TODO Auto-generated method stub
		return recycleRetailerDao.existByRetailer(mechanismId,retailer);
	}

	@Override
	public RecycleRetailer existByRetailerShort(Long mechanismId, String retailerShort) {
		// TODO Auto-generated method stub
		return recycleRetailerDao.existByRetailerShort(mechanismId,retailerShort);
	}
}
