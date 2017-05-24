package com.sdcloud.biz.lar.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.LarClientUserAddressDao;
import com.sdcloud.biz.lar.dao.LarClientUserDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class LarClientUserServiceImpl implements LarClientUserService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LarClientUserDao larClientUserDao;
	@Autowired
	private CityDao cityDao;
	@Autowired
	private LarClientUserAddressDao larClientUserAddressDao;

	@Override
	@Transactional(readOnly = true)
	public int countById() throws Exception {
		int count = 0;
		try {
			count = larClientUserDao.countById();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				// 删除用户
				count = larClientUserDao.deleteById(id);
				// 删除用户对应的地址栏
				larClientUserAddressDao.deleteByUserId(id);
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

	@Override
	@Transactional
	public boolean insert(LarClientUser larClientUser) throws Exception {
		if (larClientUser != null) {
			int count = 0;
			try {
				count = larClientUserDao.insert(larClientUser);
			} catch (Exception e) {
				throw e;
			}
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}

	@Override
	@Transactional
	public boolean insertSelective(LarClientUser larClientUser) throws Exception {
		if (larClientUser != null) {
			int count = 0;
			try {
				larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
				larClientUser.setCustomerId(larClientUser.getId());
				larClientUser.setSex(0);
				larClientUser.setAge(18);
				larClientUser.setLevel(0);
				larClientUser.setNowPoints(0);
				larClientUser.setHistoryPoints(0);
				larClientUser.setQrCodeId("0");
				larClientUser.setQrCodeUrl("0");
				larClientUser.setCreateDate(new Date());
				larClientUser.setEnable(0);
				LarClientUserAddress larClientUserAddress = larClientUser.getLarClientUserAddress();
				if (larClientUserAddress != null) {
					larClientUserAddress.setUserName(larClientUser.getName());
					larClientUserAddress.setContact(larClientUser.getPhone());
					larClientUserAddress
							.setAddress(larClientUserAddress.getRegion() + larClientUserAddress.getDetail());
					larClientUserAddress.setDefaultEnable(1);
					larClientUserAddress.setEnable(0);
					larClientUserAddress.setLarClientUser(larClientUser);
				}
				count = larClientUserDao.insertSelective(larClientUser);
				if (larClientUserAddress != null) {
					larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
					larClientUserAddressDao.insertSelective(larClientUserAddress);
				}
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}
	
	/**
	 * app端  物流（快件揽收）业务员的客户接口
	 */
	@Override
	@Transactional
	public boolean insertFromSalesCustom(LarClientUser larClientUser) throws Exception {
		if (larClientUser != null) {
			int count = 0;
			try {
				larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
				larClientUser.setCustomerId(larClientUser.getId());
				larClientUser.setSex(0);
				larClientUser.setAge(18);
				larClientUser.setLevel(0);
				larClientUser.setNowPoints(0);
				larClientUser.setHistoryPoints(0);
				larClientUser.setQrCodeId("0");
				larClientUser.setQrCodeUrl("0");
				larClientUser.setCreateDate(new Date());
				larClientUser.setEnable(0);
				larClientUser.setFromType(1);//创建来源（0 , 1物流业务员，其它需要自己定义）
				//根据机构ID 查找对应的城市信息
				List<City> cities=cityDao.findByOrg(larClientUser.getOrg().toString());
				if(CollectionUtils.isNotEmpty(cities)){
					//设置城市信息
					larClientUser.setCityId(Integer.parseInt(cities.get(0).getRegionId().toString()));
					larClientUser.setCityName(cities.get(0).getRegionName());
					larClientUser.setRegCity(cities.get(0).getRegionId());
					larClientUser.setRegCityName(cities.get(0).getRegionName());
					logger.info("success:根据机构的ID："+larClientUser.getOrg()+">>找到对应的城市ID:"+
					    cities.get(0).getRegionId()+",城市："+cities.get(0).getRegionName());
				}else{
					logger.warn("faild:未根据机构的ID："+larClientUser.getOrg()+">>找到对应的城市");
				}
				count = larClientUserDao.insertSelective(larClientUser);
				if (count > 0) {
					logger.info("success:添加成功顾客信息id:"+larClientUser.getId());
					return true;
				} else {
					logger.warn("faild:添加顾客信息失败！");
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}

	@Override
	@Transactional
	public boolean insertUserGetId(LarClientUser larClientUser) throws Exception {
		if (larClientUser != null) {
			int count = 0;
			try {
				larClientUser.setId(String.valueOf(UUIDUtil.getUUNum()));
				count = larClientUserDao.insertUserGetId(larClientUser);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<LarClientUser> selectByExample(LarPager<LarClientUser> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<LarClientUser>();
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
				long totalCount = larClientUserDao.countById();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<LarClientUser> result = larClientUserDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<LarClientUser> getClientUsers(LarPager<LarClientUser> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<LarClientUser>();
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
			if (pager.isAutoCount()) {
				long totalCount = larClientUserDao.getClientUsersCount(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<LarClientUser> result = larClientUserDao.getClientUsers(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Override
	@Transactional
	public boolean deleteByExample(Map<String, Object> params) throws Exception {
		int count = 0;
		try {
			count = larClientUserDao.deleteByExample(params);
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
	public LarClientUser selectByPrimaryKey(String id) throws Exception {
		if (id != null && id.length() > 0) {
			try {
				LarClientUser larClientUser = larClientUserDao.selectByPrimaryKey(id);
				return larClientUser;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional
	public boolean updateByExampleSelective(LarClientUser larClientUser) throws Exception {
		if (larClientUser != null && larClientUser.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("larClientUser", larClientUser);
			// 修改用户信息
			int count = larClientUserDao.updateByExampleSelective(params);
			// 修改地址
			if (larClientUser.getLarClientUserAddress() != null
					&& larClientUser.getLarClientUserAddress().getId() != null) {
				params.clear();
				LarClientUserAddress larClientUserAddress = larClientUser.getLarClientUserAddress();
				larClientUserAddress.setUserName(larClientUser.getName());
				larClientUserAddress.setContact(larClientUser.getPhone());
				larClientUserAddress.setAddress(larClientUserAddress.getRegion() + larClientUserAddress.getDetail());
				params.put("larClientUserAddress", larClientUserAddress);
				larClientUserAddressDao.updateByExampleSelective(params);
			} else if (larClientUser.getLarClientUserAddress() != null) {
				LarClientUserAddress larClientUserAddress = larClientUser.getLarClientUserAddress();
				larClientUserAddress.setUserName(larClientUser.getName());
				larClientUserAddress.setContact(larClientUser.getPhone());
				larClientUserAddress.setAddress(larClientUserAddress.getRegion() + larClientUserAddress.getDetail());
				larClientUserAddress.setDefaultEnable(1);
				larClientUserAddress.setEnable(0);
				larClientUserAddress.setLarClientUser(larClientUser);
				larClientUserAddress.setId(larClientUser.getId());
				larClientUserAddressDao.insertSelective(larClientUserAddress);
			}
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("larClientUser or id is error");
		}
	}
	@Override
	@Transactional
	public boolean updateInfo(LarClientUser larClientUser) throws Exception {
			// 修改用户信息
			int count = larClientUserDao.updateInfo(larClientUser);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
	}

	@Override
	@Transactional
	public boolean updateById(LarClientUserAddress larClientUserAddress) throws Exception {
		if (larClientUserAddress != null && larClientUserAddress.getId() != null) {
			int count = larClientUserDao.updateById(larClientUserAddress);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("larClientUserDao or id is error");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarClientUser getMyPoints(String userId) throws Exception {
		try {
			LarClientUser larClientUser = larClientUserDao.getMyPoints(userId);
			return larClientUser;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public LarClientUser findByPhone(String phone) throws Exception {
		return larClientUserDao.findByPhone(phone);
	}

	@Override
	@Transactional
	public void updateUserPoints(double points, String appUserId) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("points", points);
			params.put("appUserId", appUserId);
			larClientUserDao.updateUserPoints(params);
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional(readOnly = false)
	@Override
	public int updateMsgFlag(String userId, Integer msgFlag) throws Exception {
		return larClientUserDao.updateMsgFlag(userId, msgFlag);
	}

	@Override
	public Integer findMsgFlag(String userId) throws Exception {
		return larClientUserDao.findMsgFlag(userId);
	}

	@Override
	public int countByPhone(String phone) {
		return larClientUserDao.countByPhone(phone);
	}

	/**
	 * 检索客户的信息（通过手机号）
	 * @author jzc 2016年6月20日
	 * @param larClientUser
	 * @return
	 */
	@Override
	public List<LarClientUser> matchLarClientUser(LarClientUser larClientUser) throws Exception {
		//根据机构org，获取城市的ID
		//根据机构ID 查找对应的城市信息
//		List<City> cities=cityDao.findByOrg(larClientUser.getOrg().toString());
//		if(CollectionUtils.isNotEmpty(cities)){
//			//设置城市信息
//			if(cities.get(0).getRegionId()!=null){
//				larClientUser.setCityId(Integer.parseInt(cities.get(0).getRegionId().toString()));
//			}
//			else{
//				return null;
//			}
//		}
		return larClientUserDao.matchLarClientUser(larClientUser);
	}
	

	@Override
	public List<LarClientUser> queryClientUser(Collection<Long> list) throws Exception {
		// TODO Auto-generated method stub
		return larClientUserDao.queryClientUser(list);
	}

	@Override
	public List<Long> queryClientUserLikeName(String name) throws Exception {
		// TODO Auto-generated method stub
		return larClientUserDao.queryClientUserByName(name);
	}
}
