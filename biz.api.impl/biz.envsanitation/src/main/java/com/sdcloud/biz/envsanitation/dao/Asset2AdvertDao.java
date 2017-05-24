package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import com.sdcloud.api.envsanitation.entity.Asset2Advert;

/**
 * 
 * @author lms
 */
public interface Asset2AdvertDao {

	long count(Map<String, Object> param);

	List<Asset2Advert> findAllCar(Map<String, Object> param);
	
	List<Asset2Advert> findAllStation(Map<String, Object> param);
	
	List<Asset2Advert> findAllWc(Map<String, Object> param);

}