package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;



import com.sdcloud.api.envsanitation.entity.Device;
/**
 * 
 * @author lihuiquan
 *
 */
public interface DeviceDao {
	void insert(@Param("devices")List<Device> devices);
	
	void delete(@Param("deviceMacs") List<String> deviceMacs);
    void updateRun(@Param("deviceMac")String deviceMac, @Param("run")Integer run);
	void update(Device device);
	
	List<Device> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
}
