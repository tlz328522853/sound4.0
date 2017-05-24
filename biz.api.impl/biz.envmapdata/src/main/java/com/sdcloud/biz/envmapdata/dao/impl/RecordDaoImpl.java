package com.sdcloud.biz.envmapdata.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.sdcloud.biz.envmapdata.dao.RecordDao;
import com.sdcloud.biz.envmapdata.util.HBaseUtil;

@Service("recordDao")
public class RecordDaoImpl implements RecordDao {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String KEYFORMAT = "%s:%s";
	
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> realtime_record;
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, String> valueOper;
	
	@Override
	public ResultScanner queryHbaseByRange(String tableName,
			String mobileNumber, String type, Long from, Long to)
			throws IOException {

		logger.info("start query hbase, tableName:{} mobileNumber:{}  type:{}  from:{}  to: {}", 
				tableName, mobileNumber, type, from, to);	
		ResultScanner rs = this.queryHbaseByRange(tableName, mobileNumber, type, from, to, null, -1L);
		logger.info("complete query");
		return rs;
	}

	@Override
	public ResultScanner queryHbaseByRange(String tableName, String mobileNumber, String type, Long from, Long to,
			Set<String> cols, Long maxSize) throws IOException {
		logger.info("start query hbase, tableName:{}, mobileNumber:{},  type:{},  from:{},  to: {}, cols:{}, maxSize", 
				tableName, mobileNumber, type, from, to, cols, maxSize);
		String reversCarMobileNumber = StringUtils.reverse(mobileNumber);
		String rowKeyFrom = reversCarMobileNumber + type + from;
		String rowKeyTo = reversCarMobileNumber + type + to;
		logger.info("query hbase,tableName:"+tableName+" from:" + rowKeyFrom + " ,to:" + rowKeyTo);
		//查询
		Table table = HBaseUtil.getHTable(tableName);
		Scan s = new Scan(Bytes.toBytes(rowKeyFrom), Bytes.toBytes(rowKeyTo));
		s.setCacheBlocks(true);
		s.setCaching(100);
		
		s.setMaxResultSize(6000);//设置最多返回个数
		
		//过滤列
		//if(null != cols && cols.size() > 0){
		//	for(String col : cols){
		//		//s.addFamily();
		//		s.addColumn(Bytes.toBytes("f"), Bytes.toBytes(col));
		//	}			
		//}		
		
		ResultScanner rs = table.getScanner(s);
		logger.info("complete query");
		return rs;
	}
	@Override
	public List<Map<String, String>> queryRedis(List<String> mobileNumbers,
			String type) {

		logger.info("start query redis, mobileNumbers:{}, type:{}", mobileNumbers,type);
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		
		//key: DEV01001G01:136832681940
		String key = null;
		Map<String,String> info = null;
		Set<String> mobileNumbersSet = new HashSet<String>(mobileNumbers);//去重复
		for(String mobileNumber: mobileNumbersSet){
			key = String.format(KEYFORMAT, type, mobileNumber);
			info = realtime_record.entries(key);
			if(info.keySet().size() > 0&&info.containsKey("bd_lon") && info.containsKey("bd_lat"))
				result.add(info);
		}
		logger.info("complete query redis.");
		return result;
	}
	
	@Override
	public List<Map<String, String>> queryStatusRedis(List<String> deviceIds,
			String type) {

		logger.info("start query redis, deviceIds:{}, type:{}" , deviceIds, type);
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		
		//key: DEV01001G01:136832681940
		String key = null;
		Map<String,String> info = null;
		Set<String> mobileNumbersSet = new HashSet<String>(deviceIds);//去重复
		for(String mobileNumber: mobileNumbersSet){
			key = String.format(KEYFORMAT, type, mobileNumber);
			info = realtime_record.entries(key);
			result.add(info);
		}
		logger.info("complete query redis.");
		return result;
	}

	@Override
	public List<Map<String, String>> getDustbins(List<String> redisKeys) {
		logger.info("start RecordDaoImpl.getDustbins, param: redisKeys {}",redisKeys);
		List<Map<String,String>> dustbins = new ArrayList<Map<String,String>>();
		for(String redisKey : redisKeys){
			Map<String,String> bucket = realtime_record.entries(redisKey);
			if(bucket.size() > 0)
				dustbins.add(bucket);			
		}
		logger.info("complete RecordDaoImpl.getDustbins");
		return dustbins;
	}

	@Override
	public void putKeyValueT(String key, String value, Long timeout) throws Exception {
		
		valueOper.set(key, value, timeout, TimeUnit.MILLISECONDS);
		
	}

	@Override
	public void putKeyValue(String key, String value) throws Exception {
		
		valueOper.set(key, value);
		
	}

	@Override
	public String getKeyValue(String key) throws Exception {		
		return valueOper.get(key);
	}

	
	

}
