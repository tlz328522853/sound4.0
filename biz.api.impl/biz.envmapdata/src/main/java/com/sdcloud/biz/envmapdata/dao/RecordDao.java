package com.sdcloud.biz.envmapdata.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.ResultScanner;

/**
 * 查询redis和hbase，获取人车物的历史状态和实时状态
 * @author czz
 *
 */
public interface RecordDao {
	/**
	 * 在hbase中获取人车物的历史状态
	 * @param tableName HBase表名
	 * @param mobileNumber 信号传输号码
	 * @param type 设备信号类型
	 * @param from 查询开始时间
	 * @param to 查询结束时间
	 * @return 
	 * @throws IOException 
	 */
	ResultScanner queryHbaseByRange(String tableName, String mobileNumber, String type, Long from, Long to) throws IOException;
	
	/**
	 * 在hbase中获取人车物的历史状态
	 * @param tableName HBase表名
	 * @param mobileNumber 信号传输号码
	 * @param type 设备信号类型
	 * @param from 查询开始时间
	 * @param to 查询结束时间
	 * @param to 查询结束时间
	 * @return cols 返回指定列
	 * @throws IOException 
	 */
	ResultScanner queryHbaseByRange(String tableName, String mobileNumber, String type, Long from, Long to, Set<String> cols, Long maxSize) throws IOException;
	
	/**
	 * 在redis中批量获取人车物的实时状态
	 * @param mobileNumbers 信号传输号码
	 * @param type 设备信号类型
	 * @return
	 */
	List<Map<String, String>> queryRedis(List<String> mobileNumbers, String type);

	/**
	 * 获取垃圾桶集合
	 * @param redisKey trash:{companyCode}:{bucketId}
	 * @return
	 */
	List<Map<String, String>> getDustbins(List<String> redisKeys);

	/**
	 * 
	 * @param deviceIds 设备id列表
	 * @param type 设备类型
	 * @return
	 */
	List<Map<String, String>> queryStatusRedis(List<String> deviceIds, String type);
	
	
	/**
	 * 设置键值对，可以设置有效期
	 * @param key
	 * @param value
	 * @param timeout 有效期，单位毫秒
	 * @throws Exception
	 */
	void putKeyValueT(String key, String value, Long timeout) throws Exception;
	
	/**
	 * 设置键值对，不可以设置有效期
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	void putKeyValue(String key, String value) throws Exception;
	
	/**
	 * 获取缓存
	 * @param key
	 * @return
	 * @throws Exception
	 */
	String getKeyValue(String key) throws Exception;
}
