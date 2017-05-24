package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.api.lar.service.RecyclingSpecService;
import com.sdcloud.biz.lar.dao.RecyclingSpecDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class RecyclingSpecServiceImpl extends BaseServiceImpl<RecyclingSpec> implements  RecyclingSpecService{
	
	@Autowired
	private RecyclingSpecDao recyclingSpecDao;
	
	@Override
	@Transactional(readOnly = false)
    public Boolean save(RecyclingSpec t) {
        Date date = new Date();
        t.setCreateDate(date);
        int count = recyclingSpecDao.save(t);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

	@Override
	@Transactional(readOnly = false)
	public Boolean enableSpec(RecyclingSpec recyclingSpec) {
		Integer status = recyclingSpec.getStatus();
		if(status.equals(1) || status.equals(3)){
			recyclingSpec.setStatus(2);
		}else if(status.equals(2)){
			recyclingSpec.setStatus(3);
			this.stopPriceBySpec(recyclingSpec.getId());	
		}
		Boolean update = this.update(recyclingSpec);
		return update;
	}
	
	@Override
	@Transactional(readOnly = false)
	public void stopPriceBySpec(Long id) {
		recyclingSpecDao.stopPriceBySpec(id);
	}

	@Override
	public void findPriceByOrgId(LarPager<RecyclingSpec> larPager, List<Long> ids) {
		List<RecyclingSpec> result = recyclingSpecDao.findPriceByOrgId(larPager, ids);
        larPager.setResult(result);
        larPager.setTotalCount(this.countByOrgId(larPager, ids));
       
	}

	@Override
	public long countByOrgId(LarPager<RecyclingSpec> larPager, List<Long> ids) {		
		return recyclingSpecDao.countPricByOrgId(larPager, ids);
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean updatePrice(RecyclingSpec recyclingSpec) {
		recyclingSpec.setUpdateDate(new Date());
		return recyclingSpecDao.updatePrice(recyclingSpec);
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean savePrice(RecyclingSpec recyclingSpec) {
		recyclingSpec.setPriceId(UUIDUtil.getUUNum());
		recyclingSpec.setCreateDate(new Date());
		recyclingSpec.setUpdateDate(new Date());
		return recyclingSpecDao.savePrice(recyclingSpec);
	}

	@Override
	public void findSpecByOrgId(LarPager<RecyclingSpec> larPager, List<Long> orgIds) {
		List<RecyclingSpec> result = recyclingSpecDao.findSpecByOrgId(larPager, orgIds);
        larPager.setResult(result);
        larPager.setTotalCount(this.countByOrgId(larPager, orgIds));
		
	}
	
	@Override
	public void findAllSpecByOrgId(LarPager<RecyclingSpec> larPager, List<Long> orgIds) {
		List<RecyclingSpec> result = recyclingSpecDao.findAllSpecByOrgId(larPager, orgIds);
        larPager.setResult(result);
        larPager.setTotalCount(this.countByOrgId(larPager, orgIds));
		
	}

	@Override
	public RecyclingSpec getSpecById(Long id) {
		
		return recyclingSpecDao.getSpecById(id);
	}
}
