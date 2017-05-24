package com.sdcloud.biz.lar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.Personnel;
import com.sdcloud.api.lar.service.OwnedSupplierService;
import com.sdcloud.biz.lar.dao.AreaSettingDao;
import com.sdcloud.biz.lar.dao.OwnedSupplierDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class OwnedSupplierServiceImpl implements OwnedSupplierService {

	@Autowired
	private OwnedSupplierDao ownedSupplierDao;
	
	@Autowired
	private AreaSettingDao areaSettingDao;
	
	@Transactional(readOnly=true)
	public int countById(String mechanismId) throws Exception {
		int count = 0;
		try {
			count = ownedSupplierDao.countById(mechanismId);
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional(readOnly=true)
	public LarPager<OwnedSupplier> selectByExample(LarPager<OwnedSupplier> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<OwnedSupplier>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if(params==null || params.size()<=0){
				throw new IllegalArgumentException("params is error");
			}
			if(!params.containsKey("mechanismId")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = ownedSupplierDao.countById(String.valueOf(params.get("mechanismId")));
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<OwnedSupplier> result = ownedSupplierDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Override
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
	}

	@Override
	@Transactional
	public boolean insertSelective(OwnedSupplier ownedSupplier) throws Exception {
		if (ownedSupplier != null) {
			int count = 0;
			try {
				//根据机构id查询主键ID
				ownedSupplier.setId(String.valueOf(UUIDUtil.getUUNum()));
				ownedSupplier.setEnable(0);
				count = ownedSupplierDao.insertSelective(ownedSupplier);
				if(count>0){
					return true;
				}else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("ownedSupplier is null");
		}
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		int count = 0;
		try {
			count = ownedSupplierDao.deleteById(id);
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
	public boolean updateByExampleSelective(OwnedSupplier ownedSupplier)throws Exception {
		if (ownedSupplier != null && ownedSupplier.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ownedSupplier", ownedSupplier);
			int count = ownedSupplierDao.updateByExampleSelective(params);
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
