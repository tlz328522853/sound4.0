package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.ExchangeOrders;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.api.lar.service.ExchangeInfoService;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.CommodityDao;
import com.sdcloud.biz.lar.dao.ExchangeInfoDao;
import com.sdcloud.biz.lar.dao.IntegralConsumptionDao;
import com.sdcloud.biz.lar.dao.LarClientUserAddressDao;
import com.sdcloud.biz.lar.dao.LarClientUserDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class ExchangeInfoServiceImpl implements ExchangeInfoService {

	@Autowired
	private CityDao cityDao;
	@Autowired
	private LarClientUserDao larClientUserDao;
	@Autowired
	private IntegralConsumptionDao integralConsumptionDao;
	@Autowired
	private LarClientUserAddressDao larClientUserAddressDao;
	@Autowired
	private CommodityDao commodityDao;
	@Autowired
	private ExchangeInfoDao exchangeInfoDao;
	
	
	@Override
	@Transactional
	public boolean insertSelective(ExchangeInfo exchangeInfo) throws Exception {
		if (exchangeInfo != null) {
			int count = 0;
			try {
				//先保存消费记录
				List<ShoppingCart> shoppingCarts = exchangeInfo.getShoppingCarts();
				if(shoppingCarts==null || shoppingCarts.size()<=0){
					return false;
				}
				// 匹配机构,需要根据用户坐标查询片区和对应的机构
				if(exchangeInfo.getCityId()==null || exchangeInfo.getCityId().length()<=0){
					return false;
				}
				// 根据服务城市ID查询机构ID
				City city = cityDao.selectByCityId(Long.valueOf(exchangeInfo.getCityId()));
				if (city == null || city.getId() == null) {
					return false;
				}
				Date date = new Date();
				
				exchangeInfo.setId(String.valueOf(UUIDUtil.getUUNum()));
				exchangeInfo.setOrderId(OrderManagerServiceImpl.generateNumber());
				//绑定机构
				exchangeInfo.setMechanismId(String.valueOf(city.getOrg()));
				exchangeInfo.setOrderStatus(1);
				exchangeInfo.setCreateDate(date);
				exchangeInfo.setEnable(0);
				// 先把用户添加进去
				// 从PC端进来的订单是没有绑定用户地址的
				if (exchangeInfo.getLarClientUserAddress().getId() == null) {
					LarClientUserAddress larClientUserAddress = null;
					larClientUserAddress = exchangeInfo.getLarClientUserAddress();
					larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
					larClientUserAddress.setEnable(0);
					larClientUserAddressDao.insertSelective(larClientUserAddress);
					exchangeInfo.setLarClientUserAddress(larClientUserAddress);
				}
				count = exchangeInfoDao.insertSelective(exchangeInfo);
				if (count > 0) {
					// 保存预约的商品ID，用于批量删除购物车
					List<String> ids = new ArrayList<String>();
					// 添加子单号
					List<ShoppingCart> shoppingCarts2 = exchangeInfo.getShoppingCarts();
					if (shoppingCarts2 != null && shoppingCarts2.size() > 0) {
						for (ShoppingCart shoppingCart : shoppingCarts2) {
							if (shoppingCart.getCommodity() != null && shoppingCart.getCommodity().getId() != null) {
								ExchangeOrders exchangeOrders = new ExchangeOrders();
								exchangeOrders.setId(String.valueOf(UUIDUtil.getUUNum()));
								exchangeOrders.setExchangeInfo(exchangeInfo);
								exchangeOrders.setEnable(0);
								exchangeOrders.setConfirmOrder(1);
								exchangeOrders.setCreateDate(date);
								exchangeOrders.setCommodity(shoppingCart.getCommodity());
								exchangeOrders.setNumber(shoppingCart.getNumber());
								exchangeInfoDao.insertSelectiveChild(exchangeOrders);
								ids.add(shoppingCart.getCommodity().getId());
							}
						}
					}
					
					//添加这个订单的消费记录，每个商品算一个消费记录
					for (ShoppingCart shoppingCart : shoppingCarts) {
						Map<String, Object> paramsMap = new HashMap<String, Object>();
						paramsMap.put("id", String.valueOf(UUIDUtil.getUUNum()));
						paramsMap.put("clientUserId", exchangeInfo.getAppUserId());
						//根据商品ID查询商品
						paramsMap.put("commodity",shoppingCart.getCommodity().getId());
						paramsMap.put("consumptionQuantity", shoppingCart.getNumber());
						double unitPrice = shoppingCart.getCommodity().getUnitPrice();
						paramsMap.put("totalIntegral",unitPrice*shoppingCart.getNumber());
						paramsMap.put("commodityUnit",shoppingCart.getCommodity().getMoneyUnit());
						paramsMap.put("mechanismId",city.getOrg());
						paramsMap.put("exchangeTime",date);
						paramsMap.put("remarks","APP用户兑换");
						paramsMap.put("enable",0);
						paramsMap.put("createDate",date);
						paramsMap.put("exchangeInfoId",exchangeInfo.getId());
						integralConsumptionDao.insertSelective(paramsMap);
					}
					
					// 兑换成功清空已兑换的购物车商品,根据商品ID和用户ID清空
					if (exchangeInfo.getAppUserId() != null && exchangeInfo.getAppUserId().trim().length() > 0) {
						if (ids != null && ids.size() > 0) {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("commodityIds", ids);
							params.put("userId", exchangeInfo.getAppUserId());
							commodityDao.deleteShoppingCartsByCommodity(params);
						}
					}
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("exchangeInfo is null");
		}
	}


	@Override
	@Transactional(readOnly = true)
	public List<ExchangeInfo> getExchangeInfoByUserId(String userId) throws Exception {
		try {
			// 根据业务员主键查询服务中的订单
			List<ExchangeInfo> exchangeInfos = exchangeInfoDao.getExchangeInfoByUserId(userId);
			return exchangeInfos;
		} catch (Exception e) {
			throw e;
		}
	}
	@Override
	@Transactional(readOnly = true)
	public LarPager<ExchangeInfo> selectByExample(LarPager<ExchangeInfo> larPager, List<Long> list) {
        List<ExchangeInfo> result = exchangeInfoDao.selectByExample(larPager, list);
        for(ExchangeInfo info : result){	//对maadate进行格式化
        	info.setMaaDate(info.getMaaDate().replace("年", "-").replace("月", "-").replace("日", ""));
        }
        larPager.setResult(result);
        larPager.setTotalCount(exchangeInfoDao.totalCount(larPager, list));
        return larPager;
	}
	@Override
	@Transactional
	public boolean updateBySelect(ExchangeInfo exchangeInfo) {
		int count = exchangeInfoDao.updateBySelect(exchangeInfo);
		if (count > 0) {
            return true;
        } else {
            return false;
        }
	}
	
	@Override
	@Transactional
	public Map<String,Object> cancelOrderById(Map<String, Object> params) {
		Map result=new HashMap();
		try {
			int count = exchangeInfoDao.cancelOrderById(params);
			if (count > 0) {
				//查询该兑换单据信息
				 List<ExchangeInfo> result1=exchangeInfoDao.selectByExampleByOrderId(params);
				 if(result1!=null&&result1.size()>0){
					 ExchangeInfo info= result1.get(0);
					 if(info!=null&&info.getIntegral()!=null&&info.getIntegral().length()>0){
						 Double intergral=new Double(info.getIntegral());
						 	String appUserId=info.getAppUserId();
						 	String phone=info.getPhone();
						 	//判断积分大于0进行积分返还
						 	if(intergral.doubleValue()>0&&appUserId!=null&&appUserId.length()>0){
						 		Map<String, Object> params4return = new HashMap<String, Object>();
						 		params4return.put("points", intergral.doubleValue());
						 		params4return.put("appUserId", appUserId);
						 		larClientUserDao.returnUserPoints(params4return);
						 		result.put("points", intergral.doubleValue());
						 		result.put("phone", phone);
						 	} 
					 }
				 }
				 result.put("resultFlag", true);
				return result;
			} else {
				result.put("resultFlag", false);
				return result;
			}
		} catch (Exception e) {
			throw e;
		}
	}

}
