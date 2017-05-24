package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.service.MaaDateSettingService;
import com.sdcloud.biz.lar.dao.MaaDateSettingDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class MaaDateSettingServiceImpl implements MaaDateSettingService {
	
	@Autowired
	private MaaDateSettingDao maaDateSettingDao;
	
	@Transactional(readOnly=true)
	public int countById() throws Exception {
		int count = 0;
		try {
			count = maaDateSettingDao.countById();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional(readOnly=true)
	public LarPager<MaaDateSetting> selectByExample(LarPager<MaaDateSetting> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<MaaDateSetting>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("startDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("asc");
			}
			if (pager.isAutoCount()) {
				long totalCount = maaDateSettingDao.countById();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<MaaDateSetting> result = maaDateSettingDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	/*@Override
	@Transactional(readOnly=true)
	public List<OwnedSupplier> getOwnedSuppliersById(String id) throws Exception {
		try {
			return ownedSupplierDao.getOwnedSuppliersById(id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<Personnel> getPersonnelsById(String id) throws Exception {
		try {
			//return ownedSupplierDao.getPersonnelsById(id);
			return null;
		} catch (Exception e) {
			throw e;
		}
	}*/

	@Override
	@Transactional
	public boolean insertSelective(MaaDateSetting maaDateSetting) throws Exception {
		if (maaDateSetting != null) {
			int count = 0;
			try {
				//根据机构id查询主键ID
				maaDateSetting.setId(String.valueOf(UUIDUtil.getUUNum()));
				maaDateSetting.setEnable(0);
				maaDateSetting.setCreateDate(new Date());
				count = maaDateSettingDao.insertSelective(maaDateSetting);
				if(count>0){
					return true;
				}else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("maaDateSetting is null");
		}
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		int count = 0;
		try {
			count = maaDateSettingDao.deleteById(id);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional
	public boolean updateByExampleSelective(MaaDateSetting maaDateSetting)throws Exception {
		if (maaDateSetting != null && maaDateSetting.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("maaDateSetting", maaDateSetting);
			int count = maaDateSettingDao.updateByExampleSelective(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("ownedSupplier or id is error");
		}
	}
}
