package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.Advertisement;

public interface AdvertisementDao {
	void insert(Advertisement advert);
	
	void delete(@Param("advertIds") List<Long> advertIds);
	
	void update(Advertisement advert);
	Advertisement getAdvertisementById(@Param("advertisementId") Long advertisementId) throws Exception;
	List<Advertisement> findAllBy(Map<String, Object> param);
	long getTotalCount(Map<String, Object> param);
	Advertisement findById(@Param("advertisementId") Long advertisementId);
}
