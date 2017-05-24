package com.sdcloud.biz.envsanitation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.Advertisement;
import com.sdcloud.api.envsanitation.entity.DeviceAdvertisement;

public interface DeviceAdvertisementDao {
	
	void insert(@Param("deviceAdvertisements") List<DeviceAdvertisement> deviceAdvertisements);
	
	void delete(@Param("deviceAdvertisementIds") List<Long> deviceAdvertisementIds);
	
	void deleteByMacs(@Param("deviceMacs") List<String> deviceMacs);
	
	void deleteByAdverts(@Param("advertIds") List<Long> advertIds);
    
	void deleteAll(@Param("deviceMac")String deviceMac);
	
	List<Advertisement> findAdvertisementByDeviceMac(@Param("deviceMac")String deviceMac);
	
	List<DeviceAdvertisement> findByDeviceMac(@Param("deviceMac")String deviceMac);
	
	DeviceAdvertisement getDeviceAdvertisementById(@Param("deviceAdvertisementId") Long deviceAdvertisementId) throws Exception;
	public void update(DeviceAdvertisement deviceAdvertisement);
}
