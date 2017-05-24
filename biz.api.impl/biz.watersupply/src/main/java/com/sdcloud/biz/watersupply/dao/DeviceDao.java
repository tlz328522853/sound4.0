package com.sdcloud.biz.watersupply.dao;

import com.sdcloud.api.watersupply.entity.Device;

/**
 * 设备数据库访问接口
 * 
 * @author liy
 *
 */
public interface DeviceDao {
	
    /**
     * 根据主键删除设备数据
     * 
     * @param id - 设备主键
     * @return 删除的数据条数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入设备数据
     * 
     * @param record - 设备实例
     * @return - 插入的数据条数
     */
    int insert(Device record);


    /**
     * 根据设备Id查询设备数据
     * 
     * @param id - 设备Id
     * @return 设备实例
     */
    Device selectByPrimaryKey(Long id);

    /**
     * 根据设备Id更新设备非空属性
     * 
     * @param record - 设备实例
     * @return 更新的记录条数
     */
    int updateByPrimaryKeySelective(Device record);

    /**
     * 根据设备Id更新设备所有字段
     * 
     * @param record - 设备实例
     * @return 更新的记录条数
     */
    int updateByPrimaryKey(Device record);
}