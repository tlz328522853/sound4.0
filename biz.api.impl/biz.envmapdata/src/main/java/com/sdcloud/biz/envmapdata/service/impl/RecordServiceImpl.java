package com.sdcloud.biz.envmapdata.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sdcloud.api.envmapdata.service.RecordService;
import com.sdcloud.biz.envmapdata.dao.RecordDao;
import com.sdcloud.biz.envmapdata.util.PropertiesUtils;
import com.sdcloud.framework.common.TimeCalculateUtil;

/**
 * 
 * @author czz
 *	112798405100 01001G01 1458200220000
 *	      通讯号			类型id	时间戳
 * @param <T>
 */
@Service("recordService")
public class RecordServiceImpl implements RecordService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Map<String,Set<String>> PropWhiteList;
	
	private static String CAR_TYPE_STR = "01001G01";
	
	private static String EMP_TYPE_STR = "P0000001";

	@Autowired
	private PropertiesUtils systemConfigProperties;
	
	@Autowired
	private RecordDao recordDao;
	@Override
	public List<Map<String, String>> findHistoryRecords(String carMobileNumber, String type, Long from, Long to) throws Exception {
		logger.info("start query hbase, carMobileNumber:{}  type:{}  from:{}  to: {}", 
				carMobileNumber, type, from, to);
		
		List<Map<String, String>> records = new ArrayList<Map<String,String>>();
		try{
			if(StringUtils.isEmpty(carMobileNumber)){
				logger.warn("carMobileNumber is empty");
				throw new IllegalArgumentException("carMobileNumber is empty");
			}
			/**
			 * 初始化车辆和人员的属性白名单
			 */
			loadConfig();
			Set<String> props = PropWhiteList.get(type);
			
			if(to < from){
				logger.warn("Date from is after Date to");
				throw new IllegalArgumentException("Date from is after Date to");
			}
			String tableName = systemConfigProperties.getProperty(type);//type2TableMap.get(type);
			if(StringUtils.isEmpty(tableName)){
				throw new  Exception("device type is wrong or no such type");
			}
			ResultScanner rs = recordDao.queryHbaseByRange(tableName, carMobileNumber, type, from, to, props, -1L);
			
			//bd_lat,bd_lon,speed 一样的话压缩成一条数据
			//Double preSpeed = null;
			
			//往前第一个点
			Map<String, String> preItem = null;
			Double preLat = null;
			Double preLon = null;
			Double preLength = null;//当前点到前第一个点的距离
			
			/*//往前第二个点
			Map<String, String> preItem2 = null;
			Double preLat2 = null;
			Double preLon2 = null;
			Double preLength2 = null;//前第一个点到第二个点的距离
*/			
			//Double tempSpeed = null;
			//当前点
			Map<String, String> item = null;
			Double tempLat = null;
			Double tempLon = null;
			Double tempLength = null;//前第一个点到第二个点的距离
			
			
			
			Long tempStart = null;
			Long tempEnd = null;
			for (Result r : rs) {
				List<Cell> cells = r.listCells();
				item = new HashMap<String, String>();
				String keyProp = null;
				for(Cell c : cells){
					keyProp = Bytes.toString(c.getQualifier());
					if(props.contains(keyProp))
						item.put(Bytes.toString(c.getQualifier()),
								Bytes.toString(c.getValue()));
				}
				logger.debug("item:{}",item);
				if(item.size() == 0) continue;//无有效数据，直接丢掉
				
				//logger.info("item:{}",item);
				if(item.get("bd_lat") ==null|| item.get("bd_lat").equals("") || item.get("bd_lon") == null || item.get("bd_lon").equals("") ){
					continue;					
				}
				if(preItem == null){//第一个点
					try{
						preLat = Double.parseDouble(item.get("bd_lat"));
						preLon = Double.parseDouble(item.get("bd_lon"));
						//preSpeed = Double.parseDouble(item.get("speed"));
					}catch(NumberFormatException e){
						logger.warn("parse bd_lat, bd_lat, speed Error:{};\nItem:{}",e,item);
						continue;
					}
					preItem = item;
					records.add(item);
				}
				else{//非第一个点
					try{
						if(item.get("bd_lat")!=null && item.get("bd_lon") != null/* &&item.get("speed") != null*/){
							tempLat = Double.parseDouble(item.get("bd_lat"));
							tempLon = Double.parseDouble(item.get("bd_lon"));
							//tempSpeed = Double.parseDouble(item.get("speed"));
						}
						
					}catch(NumberFormatException e){
						logger.warn("parse bd_lat, bd_lat, speed Error:{};\nItem:{}",e,item);
						continue;
					}		
					
					//是否停留
					if(Math.abs(preLat - tempLat) < 0.0001 && Math.abs(preLon - tempLon) < 0.0001/* && preSpeed == tempSpeed*/){
						//覆盖前一条数据，保留停留是的最后一条
						if(records.size() > 1)
							records.remove(records.size() - 1);
						//停留时间计算
						if(preItem.get("dataTime") != null && item.get("dataTime") != null){
							try{							
								tempStart = Long.parseLong(preItem.get("dataTime"));
								tempEnd = Long.parseLong(item.get("dataTime"));
								item.put("stayTime", Long.toString(tempEnd - tempStart));
							}
							catch(Exception e){
								item.put("stayTime", "0");
							}
						}
						records.add(item);
						continue;						
					}
					
					//停留时间计算
					if(preItem.get("dataTime") != null && item.get("dataTime") != null){
						try{							
							tempStart = Long.parseLong(preItem.get("dataTime"));
							tempEnd = Long.parseLong(item.get("dataTime"));
							item.put("stayTime", Long.toString(tempEnd - tempStart));
						}
						catch(Exception e){
							item.put("stayTime", "0");
						}
					}
					records.add(item);

					preItem = item;
					preLat = tempLat;
					preLon = tempLon;
					/*tempLength = Math.abs(preLat - tempLat) + Math.abs(preLon - tempLon);
					
					//异常点检查
					if(preLength == null){//第二个点
						preLength = tempLength;
						preLat2 = preLat;
						preLon2 = preLon;
						records.add(item);
					}
					else{//非第二个点，检查是否前一个点是异常点（异常点是前三个点构成一个超级锐角三角形），若果前一个点是异常点，则抛弃
						preLength2 = preLength;
						preLength = Math.abs(preLat2 - tempLat) + Math.abs(preLon2 - tempLon);
						if((preLength2/preLength)>50&&(tempLength/preLength)>50){//异常点
							records.remove(records.size() - 1);
 
							//当前点成为前第一个点
							preItem = item;
							preLat = tempLat;
							preLon = tempLon;
							
							records.add(item);
						}else{//非异常点

							//前第一个点成为前第二个点
							preItem2 = preItem;
							preLat2 = preLat;
							preLon2 = preLon;
							//当前点成为前第一个点
							preItem = item;
							preLat = tempLat;
							preLon = tempLon;
									
							preLength = tempLength;
							records.add(item);
						}
					}*/
					
				}
			}

		}
		catch(Exception e){
			logger.info("err when CarRecordServiceImpl.getCarRecords, carMobileNumber:{}  type:{}  from:{}  to: {}", 
					carMobileNumber, type, from, to);
			
			throw e;
		}
		logger.debug("complete CarRecordServiceImpl.getCarRecord, srecords:{}", records);
		return records;
	}
	

	@Override
	public List<List<Object>> findOilRecords(String carMobileNumber) throws Exception {


		return null;
	}

	@Override
	public List<List<Object>> findOilRecords(String carMobileNumber, Long from, Long to) throws Exception {

		logger.info("start query hbase, findOilRecords:{}, from:{}, to: {}", 
				carMobileNumber, from, to);
		
		List<List<Object>> records = new ArrayList<List<Object>>(2);
		List<Object> oilList = new ArrayList<Object>();
		List<Object> timeList = new ArrayList<Object>();
		List<Object> speedList = new ArrayList<Object>();
		List<Object> zMileageList = new ArrayList<Object>();
		String oilSpeedPro = systemConfigProperties.getProperty("oilSpeed");
		String oilVolumePro = systemConfigProperties.getProperty("oilVolume");
		String zMileagePro = systemConfigProperties.getProperty("zMileage");
		records.add(timeList);
		records.add(oilList);
		records.add(speedList);
		records.add(zMileageList);
		
		try{
			if(StringUtils.isEmpty(carMobileNumber)){
				logger.warn("carMobileNumber is empty");
				throw new IllegalArgumentException("carMobileNumber is empty");
			}
			if(to < from){
				logger.warn("Date from is after Date to");
				throw new IllegalArgumentException("Date from is after Date to");
			}
			String tableName = systemConfigProperties.getProperty(CAR_TYPE_STR);//type2TableMap.get(type);
			if(StringUtils.isEmpty(tableName)){
				throw new  Exception("device type is wrong or no such type");
			}
			//查询
			Set<String> props = new HashSet<String>(3){{add("dataTime");add("speed");add("direction");}};
			ResultScanner rs = recordDao.queryHbaseByRange(tableName, carMobileNumber, CAR_TYPE_STR, from, to, props,-1L);
			//组装
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String keyProp = null;			
			Date timestamp = null;
			String speed = null;
			String oilVol = null;
			String zMileageVal = null;
			String temp = null;
			for (Result r : rs) {
				List<Cell> cells = r.listCells();
				if(cells.size() < 2) continue;
				timestamp = null;
				speed = null;
				oilVol = null;
				//zMileageVal = null;
				for(Cell c : cells){
					keyProp = Bytes.toString(c.getQualifier());
					if("dataTime".equals(keyProp)){
						Date date = new Date(Long.parseLong(Bytes.toString(c.getValue())));
						timestamp = date;
					}
					else if(oilSpeedPro.equals(keyProp)){
						speed = Bytes.toString(c.getValue());
					}
					else if(oilVolumePro.equals(keyProp)){
						oilVol = Bytes.toString(c.getValue());
					}
					else if(zMileagePro.equals(keyProp)){
						temp = Bytes.toString(c.getValue());
						zMileageVal = temp.equals("null") || StringUtils.isEmpty(temp) ? zMileageVal: temp;
					}
				}
				
				if(oilVol != null && timestamp != null){
					timeList.add(formatter.format(timestamp));
					oilList.add(oilVol);					
					speedList.add(speed);
					zMileageList.add(zMileageVal);
				}
			}
			
		}catch(Exception e){
			logger.info("err, findOilRecords:{}, from:{}, to: {}, exception: {}", 
					carMobileNumber, from, to, e);
		}
		return records;
	}

	/**
	 * 初始化车辆和人员的属性白名单
	 */
	private void loadConfig() {
		
		if(PropWhiteList == null){
			PropWhiteList = new HashMap<>();
		}
		
		if(PropWhiteList.get(CAR_TYPE_STR) == null || PropWhiteList.get(CAR_TYPE_STR).size() == 0){
			String carPropStr = systemConfigProperties.getProperty("CarProperties");
			if(StringUtils.isEmpty(carPropStr)){
				throw new RuntimeException("config CarProperties is not set");
			}
			PropWhiteList.put(CAR_TYPE_STR, new HashSet<String>(Lists.newArrayList(carPropStr.split(","))));
		}
		
		if(PropWhiteList.get(EMP_TYPE_STR) == null || PropWhiteList.get(EMP_TYPE_STR).size() == 0){
			String empPropStr = systemConfigProperties.getProperty("EmpProperties");
			if(StringUtils.isEmpty(empPropStr)){
				throw new RuntimeException("config EmpProperties is not set");
			}
			PropWhiteList.put(EMP_TYPE_STR, new HashSet<String>(Lists.newArrayList(empPropStr.split(","))));
		}
	}

	@Override
	public List<Map<String, String>> findHistoryRecordsOneDay(String carMobileNumber, String type, Long wholeDay) throws Exception {
		
		logger.info("start the method CarRecordServiceImpl.getCarRecordsOneDay"+
				", carMobileNumber:{}, wholeDay:{}", carMobileNumber, wholeDay);
		
		try {
			if(StringUtils.isEmpty(carMobileNumber)){
				logger.warn("carMobileNumber is empty");
				throw new IllegalArgumentException("carMobileNumber is empty");
			}
			
			Long from = TimeCalculateUtil.getDayStartTime(wholeDay);
			Long to = TimeCalculateUtil.getDayEndTime(wholeDay);
			
			logger.info("complete CarRecordServiceImpl.getCarRecordsOneDay");
			return findHistoryRecords(carMobileNumber, type, from, to);
			
		} catch (Exception e) {
			logger.error("err when CarRecordServiceImpl.getCarRecords"+
					", carMobileNumber:{} , wholeDay:{}", carMobileNumber, wholeDay);
			throw e;
		}
	}

	@Override
	public List<Map<String, String>> findRealTimeRecords(List<String> carMobileNumbers, String deviceTypeCode) throws Exception {
		//call backend redis by Dao 
		logger.info("start query redis");
		/**
		 * 初始化车辆和人员的属性白名单
		 */
		loadConfig();
		Set<String> props = PropWhiteList.get(deviceTypeCode);
		List<Map<String, String>> entries = recordDao.queryRedis(carMobileNumbers, deviceTypeCode);
		for(Map<String, String> entry : entries){
			entry.keySet().retainAll(props);
		}
		logger.info("complete query redis");
		return entries;
	}

	@Override
	public Map<String,String> dustbinFromRedis(List<String> redisKey) throws Exception {
		logger.info("start RecordServiceImpl.dustbinFromRedis, param: redisKey {}",redisKey);
		List<Map<String, String>> buckets = recordDao.getDustbins(redisKey);
		
		Map<String, String> dustbins = new HashMap<String, String>();
		for(Map<String, String> bucket : buckets){
			if(bucket.size() > 0)
				dustbins.putAll(bucket);			
		}
		
		logger.debug("dustbins:{}",dustbins);
		logger.info("complete RecordServiceImpl.dustbinFromRedis");
		return dustbins;
	}

	@Override
	public void putKeyValue(String key, String value, Long timeout) throws Exception {
		logger.info("start RecordServceImpl.putKeyValue,  key:{}, value:{} timeout:{}", key, value, timeout);
		try{
			if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
				logger.info("key and value cannot be null");
				throw new Exception("key and value cannot be null");
			}
			if(timeout > 0){
				recordDao.putKeyValueT(key, value, timeout);
			}
			else{
				recordDao.putKeyValue(key, value);
			}
			logger.info("complete RecordServiceImpl.putKeyValue");
		}
		catch(Exception e){
			logger.info("err when RecordServiceImpl.putKeyValue,  key:{}, value:{} timeout:{}", key, value, timeout);
			logger.info("exception: {}", e);
			throw e;
		}
	}

	@Override
	public String getKeyValue(String key) throws Exception {
		logger.info("start RecordServiceImpl.getKeyValue, key:{}", key);
		try{
			String res = recordDao.getKeyValue(key);
			logger.info("complete RecordServiceImple.getKeyValue, result: {}", res);
			return res;
		}
		catch(Exception e){
			throw e;
		}
	}

}
