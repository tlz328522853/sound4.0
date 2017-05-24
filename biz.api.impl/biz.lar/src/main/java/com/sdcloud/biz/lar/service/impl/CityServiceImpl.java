package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.biz.lar.dao.CityDao;

/**
 * 
 * @author wrs<br/>
 * 服务城市service
 */
@Service
public class CityServiceImpl extends BaseServiceImpl<City> implements CityService {
	@Autowired
	private CityDao cityDao;
    
	/**
	 * 服务城市
	 */
	public List<City> serviceCity(){
		return cityDao.serviceCity();
	}

	public City selectByCityId(Long id) {
		return cityDao.selectByCityId(id);
	}
	/**
	 * 根据城市名匹配服务城市
	 * @param regionName
	 * @return
	 */
	public List<City> find(String regionName) {
		return cityDao.findByRegionName(regionName);
	}

	/**
	 * 根据机构ID获取城市
	 * @author jzc 2016年6月16日
	 * @param org
	 * @return
	 */
	@Override
	public List<City> findByOrg(String org) {
		return cityDao.findByOrg(org);
	}

	@Override
	public City selectById(Long id) {
		
		return cityDao.selectById(id);
	}

	@Override
	public boolean isDisable(Long cityId) {
		City city = cityDao.selectByCityId(cityId);
		if(null != city && Integer.valueOf(0).equals(city.getCityStatus())){
			return false;
		}
		return true;
	}
}
