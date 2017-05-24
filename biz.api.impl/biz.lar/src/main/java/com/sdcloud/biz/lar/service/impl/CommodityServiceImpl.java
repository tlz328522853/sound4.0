package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.Commodity;
import com.sdcloud.api.lar.entity.CommodityType;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.api.lar.service.CommodityService;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.CommodityDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class CommodityServiceImpl implements CommodityService {

	@Autowired
	private CommodityDao commodityDao;
	@Autowired
	private CityDao cityDao;

	// 查询类型数量
	@Transactional(readOnly = true)
	public int countByType() throws Exception {
		int count = 0;
		try {
			count = commodityDao.countByType();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional
	public boolean deleteById(String id) throws Exception {
		if (id != null && id.trim().length()>0) {
			int count = 0;
			try {
				// 删除物品
				count = commodityDao.deleteById(id);
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
				//根据类型id删除物品
				count = commodityDao.deleteByTypeId(id);
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

	//删除类型
	@Transactional
	public boolean deleteTypeById(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				count = commodityDao.deleteTypeById(id);
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

	//添加一个物品
	@Transactional
	public boolean insertSelective(Commodity commodity)
			throws Exception {
		if (commodity != null) {
			int count = 0;
			try {
				commodity.setId(String.valueOf(UUIDUtil.getUUNum()));
				commodity.setCreateDate(new Date());
				commodity.setEnable(0);
				commodity.setShopId(String.valueOf(UUIDUtil.getUUNum()));
				count = commodityDao.insertSelective(commodity);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("Commodity is null");
		}
	}

	// 添加类型
	@Transactional
	public boolean insertSelective(CommodityType commodityType)
			throws Exception {
		if (commodityType != null) {
			int count = 0;
			try {
				commodityType.setId(String.valueOf(UUIDUtil.getUUNum()));
				commodityType.setCreateDate(new Date());
				commodityType.setEnable(0);
				count = commodityDao.insertSelectiveType(commodityType);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("commodityType is null");
		}
	}

	//查询回收物
	@Transactional(readOnly = true)
	public LarPager<Commodity> selectByExample(LarPager<Commodity> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<Commodity>();
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
			if(!params.containsKey("mechanismId") && !params.containsKey("orgIds")){
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = commodityDao.countById(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<Commodity> result = commodityDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}
	
	
	@Transactional(readOnly = true)
	public Commodity getCommoditysById(Map<String, Object> params) throws Exception {
		try {
			Commodity commodity = commodityDao.getCommoditysById(params);
			return commodity;
		} catch (Exception e) {
			throw e;
		}
	}

	// 查询类型
	@Transactional(readOnly = true)
	public LarPager<CommodityType> selectTypeByExample(LarPager<CommodityType> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<CommodityType>();
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
				long totalCount = commodityDao.countByType();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<CommodityType> result = commodityDao.selectTypeByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Transactional(readOnly = true)
	public Commodity selectByPrimaryKey(String id)
			throws Exception {
		if (id != null && id.trim().length() > 0) {
			try {
				Commodity commodity = commodityDao.selectById(id);
				return commodity;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Transactional
	public boolean updateByExampleSelective(Commodity commodity)
			throws Exception {
		if (commodity != null && commodity.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("commodity", commodity);
			// 修改物品信息
			int count = commodityDao.updateByExampleSelective(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("commodity or id is error");
		}
	}

	// 修改类型信息
	@Transactional
	public boolean updateByExampleSelective(CommodityType commodityType)throws Exception {
		try {
			if (commodityType != null && commodityType.getId() != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("commodityType", commodityType);
				// 修改子单号信息
				int count = commodityDao.updateTypeByExampleSelective(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new IllegalArgumentException("commodityType or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<CommodityType> getRecyclingTypes(String id) throws Exception {
		try {
			return commodityDao.getRecyclingTypes(id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Commodity> getRecyclingNames(String id)throws Exception {
		try {
			return commodityDao.getRecyclingNames(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getBrands(String id) throws Exception {
		try {
			return commodityDao.getBrands(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getSpecificationsByOrgId(String id) throws Exception {
		try {
			return commodityDao.getSpecificationsByOrgId(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getBrandsByName(String id,String shopName) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mechanismId", id);
			params.put("shopName", shopName);
			return commodityDao.getBrandsByName(params);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getSpecifications(String id,String brand,String shopName) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mechanismId", id);
			params.put("brand", brand);
			params.put("shopName", shopName);
			return commodityDao.getSpecifications(params);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getGoods(String id) throws Exception {
		try {
			return commodityDao.getGoods(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<String> getGoodsByctId(String id,String commodityType) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mechanismId", id);
			params.put("commodityType", commodityType);
			return commodityDao.getGoodsByctId(params);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional
	public boolean updateShelfLife(Map<String, Object> params) throws Exception {
		try {
			if(params!=null && params.size()>0){
				int count = commodityDao.updateShelfLife(params);
				if(count>0){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	@Override
	public boolean updateDownShelfLife(Map<String, Object> params)
			throws Exception {
		try {
			if(params!=null && params.size()>0){
				int count = commodityDao.updateDownShelfLife(params);
				if(count>0){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	@Override
	@Transactional
	public boolean saveShoppingCart(List<ShoppingCart> shoppingCarts) throws Exception {
		try {
			String userId = shoppingCarts.get(0).getAppUserId();
			if(userId != null && userId.length()>0){
				List<ShoppingCart> carts = commodityDao.getShoppingCarts(userId);
				
				for (ShoppingCart shoppingCart : shoppingCarts) {
					if(shoppingCart.getCommodity().getId()==null || shoppingCart.getCommodity().getId().length()<=0){
						return false;
					}
					for (ShoppingCart car : carts) {
						String shopId = shoppingCart.getCommodity().getId();
						String carId = car.getCommodity().getId();
						if(shopId.equals(carId)){
							//添加时的数量
							int number = shoppingCart.getNumber();
							shoppingCart.setNumber(car.getNumber()+number);
							shoppingCart.setId(car.getId());
						}
					}
				}
			}else{
				return false;
			}
			int count  = 0;
			
			
			/*List<String> commodityIds = new ArrayList<String>();
			for (ShoppingCart shoppingCart : shoppingCarts) {
				if(shoppingCart.getAppUserId()==null || shoppingCart.getAppUserId().length()<=0){
					return false;
				}
				if(shoppingCart.getCommodity().getId()==null || shoppingCart.getCommodity().getId().length()<=0){
					return false;
				}
				commodityIds.add(shoppingCart.getCommodity().getId());
				userId = shoppingCart.getAppUserId();
				//保存id批量删除
			}*/
			
			
			/*//先删除原先购物车指定用户的记录
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("commodityIds", commodityIds);
			commodityDao.deleteShoppingCartsByCommodity(params);*/
			//再添加记录到购物车
			for (ShoppingCart shoppingCart : shoppingCarts) {
				if(shoppingCart.getId() == null){
					shoppingCart.setId(String.valueOf(UUIDUtil.getUUNum()));
					count = commodityDao.saveShoppingCart(shoppingCart);
				}else{
					count = commodityDao.updateShoppingCart(shoppingCart);
				}
			}
			if(count>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ShoppingCart> getShoppingCarts(String userId) throws Exception {
		try {
			List<ShoppingCart> shoppingCarts = commodityDao.getShoppingCarts(userId);
			return shoppingCarts;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ShoppingCart> getShoppingCartsByCityId(String userId, String cityId) throws Exception {
		try {
			City city = cityDao.selectByCityId(Long.valueOf(cityId));
			List<ShoppingCart> shoppingCarts = commodityDao.getShoppingCartsByCityId(userId, String.valueOf(city.getOrg()));
			return shoppingCarts;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 添加商品类型
	 * 
	 * @param recyclingType
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean saveCommodityType(CommodityType commodityType) throws Exception{
		if (commodityType == null) {
			return false;
		}else{
			commodityDao.saveCommodityType(commodityType);
			return true;
		}
	}
	
	@Override
	@Transactional
	public boolean deleteShoppingCartsById(List<String> ids) throws Exception {
		try {
			int count = commodityDao.deleteShoppingCartsById(ids);
			if(count>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int countById() throws Exception {
		return 0;
	}

	@Override
	public List<String> getGoodsByOrgs(List<Long> orgIds) {
		try {
			return commodityDao.getGoodsByOrgs(orgIds);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<String> getBrandsByOrgs(List<Long> orgIds) {
		try {
			return commodityDao.getBrandsByOrgs(orgIds);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deleteShopCartByCommodity(String id) {
		try {
			commodityDao.deleteShopCartByCommodity(id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public boolean isDownShelf(ExchangeInfo exchangeInfo) {
		List<ShoppingCart> shoppingCarts = exchangeInfo.getShoppingCarts();
		Set<String> ids = new HashSet<>();
		for(ShoppingCart shoppingCart:shoppingCarts){
			if(null != shoppingCart.getCommodity() && shoppingCart.getCommodity().getId() !=null ){
				ids.add(shoppingCart.getCommodity().getId());
			}
		}
		int count = commodityDao.getShoppingCartsCount(ids);
		return count==ids.size();
	}
}
