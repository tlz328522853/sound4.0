package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.RecyclingList;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.service.RecyclingItemsService;
import com.sdcloud.biz.lar.dao.RecyclingItemsDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class RecyclingItemsServiceImpl implements RecyclingItemsService {

	@Autowired
	private RecyclingItemsDao recyclingItemsDao;

	@Transactional(readOnly = true)
	public int countById() throws Exception {
		int count = 0;
		try {
			count = recyclingItemsDao.countById();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	// 查询类型数量
	@Transactional(readOnly = true)
	public int countByType() throws Exception {
		int count = 0;
		try {
			count = recyclingItemsDao.countByType();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional
	public boolean deleteById(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				// 删除物品
				count = recyclingItemsDao.deleteById(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("orderManager is error");
		}
	}

	@Transactional
	public boolean deleteByTypeId(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				// 根据类型id删除物品
				count = recyclingItemsDao.deleteByTypeId(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	// 删除类型
	@Transactional
	public boolean deleteTypeById(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				count = recyclingItemsDao.deleteTypeById(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	// 添加一个物品
	@Transactional
	public boolean insertSelective(RecyclingMaterial recyclingMaterial) throws Exception {
		if (recyclingMaterial != null) {
			int count = 0;
			try {
				recyclingMaterial.setId(String.valueOf(UUIDUtil.getUUNum()));
				recyclingMaterial.setCreateDate(new Date());
				recyclingMaterial.setEnable(0);
				recyclingMaterial.setGoodsId(String.valueOf(UUIDUtil.getUUNum()));
				count = recyclingItemsDao.insertSelective(recyclingMaterial);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("OrderManager is null");
		}
	}

	// 添加类型
	@Transactional
	public boolean insertSelective(RecyclingType recyclingType) throws Exception {
		if (recyclingType != null) {
			int count = 0;
			try {
				recyclingType.setId(String.valueOf(UUIDUtil.getUUNum()));
				recyclingType.setCreateDate(new Date());
				recyclingType.setEnable(0);
				count = recyclingItemsDao.insertSelectiveType(recyclingType);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("childOrders is null");
		}
	}

	// 查询回收物
	@Transactional(readOnly = true)
	public LarPager<RecyclingMaterial> selectByExample(LarPager<RecyclingMaterial> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<RecyclingMaterial>();
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

			if (pager.isAutoCount()) {
				long totalCount = recyclingItemsDao.countById();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<RecyclingMaterial> result = recyclingItemsDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	// 查询类型
	@Transactional(readOnly = true)
	public LarPager<RecyclingType> selectTypeByExample(LarPager<RecyclingType> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<RecyclingType>();
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
			if (pager.isAutoCount()) {
				long totalCount = recyclingItemsDao.countByType();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<RecyclingType> result = recyclingItemsDao.selectTypeByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Transactional(readOnly = true)
	public RecyclingMaterial selectByPrimaryKey(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			try {
				RecyclingMaterial recyclingMaterial = recyclingItemsDao.selectById(id);
				return recyclingMaterial;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Transactional
	public boolean updateByExampleSelective(RecyclingMaterial recyclingMaterial) throws Exception {
		if (recyclingMaterial != null && recyclingMaterial.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("recyclingMaterial", recyclingMaterial);
			// 修改物品信息
			int count = recyclingItemsDao.updateByExampleSelective(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("orderManager or id is error");
		}
	}

	// 修改类型信息
	@Transactional
	public boolean updateByExampleSelective(RecyclingType recyclingType) throws Exception {
		try {
			if (recyclingType != null && recyclingType.getId() != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("recyclingType", recyclingType);
				// 修改子单号信息
				int count = recyclingItemsDao.updateTypeByExampleSelective(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new IllegalArgumentException("childOrders or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<RecyclingType> getRecyclingTypes() {
		return recyclingItemsDao.getRecyclingTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecyclingMaterial> getRecyclingNames(String id) {
		return recyclingItemsDao.getRecyclingNames(id);
	}

	@Override
	@Transactional
	public boolean updateStartUsing(String id, String status) throws Exception {
		try {
			if (id != null && id.trim().length() > 0 && status != null && status.trim().length() > 0) {
				int count = recyclingItemsDao.updateStartUsing(id, status);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new IllegalArgumentException("updateStartUsing or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public boolean updateStartUsingForApp(String id, String status) throws Exception {
		if (id != null && id.trim().length() > 0 && status != null && status.trim().length() > 0) {
			int count = recyclingItemsDao.updateStartUsingForApp(id, status);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("startUsingForApp or id is error");
		}
	}

	@Transactional(readOnly = true)
	public List<RecyclingList> getRecyclingList() throws Exception {
		try {
			List<RecyclingList> recyclingLists = recyclingItemsDao.getRecyclingList();
			return recyclingLists;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RecyclingList> getCityRecyclingList(Long orgId) throws Exception {
		try {
			List<RecyclingList> recyclingLists = recyclingItemsDao.getCityRecyclingList(orgId);
			return recyclingLists;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean saveRecoveryBlue(List<RecoveryBlue> recoveryBlues) throws Exception {
		try {
			String userId = null;
			int count = 0;
			List<String> recyList = new ArrayList<String>();//回收物物品id
			for (RecoveryBlue recoveryBlue : recoveryBlues) {
				if (recoveryBlue.getAppUserId() == null || recoveryBlue.getAppUserId().length() <= 0) {
					return false;
				}
				if (recoveryBlue.getRecyclingMaterial().getId() == null
						|| recoveryBlue.getRecyclingMaterial().getId().length() <= 0) {
					return false;
				}
				userId = recoveryBlue.getAppUserId();
				recyList.add(recoveryBlue.getRecyclingMaterial().getId());
			}
			if (recyList != null && recyList.size() > 0) {
				String recyIds=StringUtils.join(recyList,',');
				List<String> ids=recyclingItemsDao.findByUserAndRecy(userId,recyIds);
				recyclingItemsDao.deleteRecoveryBluesByIds(ids);
			}
			// 再添加记录到购物车
			for (RecoveryBlue recoveryBlue : recoveryBlues) {
				recoveryBlue.setId(String.valueOf(UUIDUtil.getUUNum()));
				count = recyclingItemsDao.saveRecoveryBlue(recoveryBlue);
			}
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
	@Transactional(readOnly = true)
	public List<RecoveryBlue> getRecoveryBlues(String userId) throws Exception {
		try {
			List<RecoveryBlue> recoveryBlues = recyclingItemsDao.getRecoveryBlues(userId);
			return recoveryBlues;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean deleteRecoveryBluesByIds(List<String> ids) throws Exception {
		try {
			int count = recyclingItemsDao.deleteRecoveryBluesByIds(ids);
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
	public void test1(List<String> ids) throws Exception {
		int count = 0;
		try {
			count = recyclingItemsDao.deleteRecoveryBluesByIds(ids);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 添加回收物品分类
	 * 
	 * @param recyclingType
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean saveRecyclingType(RecyclingType recyclingType) throws Exception{
		if (recyclingType == null) {
			return false;
		}else{
			recyclingItemsDao.saveRecyclingType(recyclingType);
			return true;
		}
	}

	@Override
	public List<RecyclingType> getOrgRecyclingTypes(List<Long> orgs,Boolean priceEnable)  throws Exception{
		
		return recyclingItemsDao.getOrgRecyclingTypes(orgs,priceEnable);
	}
	
	@Override
	public List<RecyclingType> getOrgRecyclingAllTypes(List<Long> orgs,Boolean priceEnable)  throws Exception{
		
		return recyclingItemsDao.getOrgRecyclingAllTypes(orgs,priceEnable);
	}

	@Override
	public List<RecyclingMaterial> getRecyclingNames(List<Long> orgs, Long type,Boolean priceEnable) {
		return recyclingItemsDao.getOrgRecyclingNames(orgs,type,priceEnable);
	}
	
	@Override
	public List<RecyclingMaterial> getRecyclingAllNames(List<Long> orgs, Long type,Boolean priceEnable) {
		return recyclingItemsDao.getOrgRecyclingAllNames(orgs,type,priceEnable);
	}
}
