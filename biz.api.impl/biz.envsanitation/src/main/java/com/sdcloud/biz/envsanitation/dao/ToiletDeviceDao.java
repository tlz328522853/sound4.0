package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;






import com.sdcloud.api.envsanitation.entity.Device;
import com.sdcloud.api.envsanitation.entity.ToiletDevice;
/**
 * 
 * @author lihuiquan
 *
 */
public interface ToiletDeviceDao {
	void insert(@Param("toiletDevices")List<ToiletDevice> toiletDevices);
	
	void delete(@Param("toiletDeviceIds") List<Long> toiletDeviceIds);
	
	void deleteByMacs(@Param("deviceMacs") List<String> deviceMacs);
	
	void update(ToiletDevice toiletDevice);
	
	List<Device> findAllBy(Map<String, Object> param);
	
    List<Device> findDevice(@Param("aseetId")Long aseetId);
    
	void deletes(@Param("aseetId")Long aseetId,@Param("deviceIds")List<Long> deviceIds) ;
	
	void deleteAll(@Param("aseetId")Long aseetId);
	 
	long getTotalCount(Map<String, Object> param);
}
