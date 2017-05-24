package com.sdcloud.biz.lar.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleVendor;
import com.sdcloud.api.lar.service.RecycleVendorService;
import com.sdcloud.biz.lar.dao.RecycleVendorDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * lar_recycle_vendor 供应商
 * @author TLZ
 * @date 2016-12-02
 * @version 1.0
 */
@Service
@Transactional
public class RecycleVendorServiceImpl extends BaseServiceImpl<RecycleVendor> implements RecycleVendorService{

	@Autowired
	private RecycleVendorDao recycleVendordao;
	
	@Override
	public LarPager<RecycleVendor> selectByExample(LarPager<RecycleVendor> larPager) {
		
		return null;
	}
	
	@Transactional(readOnly=false)
	public Boolean save(RecycleVendor recycleVendor)  {
		if (recycleVendor != null) {
			int count = 0;
			try {
				recycleVendor.setId(Long.valueOf(UUIDUtil.getUUNum()));
				recycleVendor.setVendorCoding("GYS"+String.valueOf(UUIDUtil.getUUNum()).substring(0,5));
				recycleVendor.setEnable(0);
				Date date = new Date();
				recycleVendor.setCreateTime(date);
				
				count = recycleVendordao.save(recycleVendor);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("recycleVendor is null");
		}
		
	}

	@Override
	public RecycleVendor existByVendor(Long mechanismId, String vendor) {
		// TODO Auto-generated method stub
		return recycleVendordao.existByVendor(mechanismId, vendor);
	}

	@Override
	public RecycleVendor existByVendorShort(Long mechanismId, String vendorShort) {
		// TODO Auto-generated method stub
		return recycleVendordao.existByVendorShort(mechanismId, vendorShort);
	}

	@Override
	public boolean selectById(Long id) {
		
		return recycleVendordao.selectById(id)>0;
	}
}
