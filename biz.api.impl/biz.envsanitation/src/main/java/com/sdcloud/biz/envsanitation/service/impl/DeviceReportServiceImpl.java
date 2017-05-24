package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcloud.api.envsanitation.entity.AdvertDirective;
import com.sdcloud.api.envsanitation.entity.AdvertDirectiveReslut;
import com.sdcloud.api.envsanitation.entity.Advertisement;
import com.sdcloud.api.envsanitation.entity.DeviceAdvertisement;
import com.sdcloud.api.envsanitation.entity.InventoryAccounting;
import com.sdcloud.api.envsanitation.entity.OperationAdResult;
import com.sdcloud.api.envsanitation.entity.OperationResult;
import com.sdcloud.api.envsanitation.service.DeviceReportService;
import com.sdcloud.biz.envsanitation.dao.AdvertDirectiveDao;
import com.sdcloud.biz.envsanitation.dao.AdvertisementDao;
import com.sdcloud.biz.envsanitation.dao.DeviceAdvertisementDao;
import com.sdcloud.biz.envsanitation.dao.DeviceDao;
import com.sdcloud.framework.common.AdvertDirectiveState;
import com.sdcloud.framework.common.AdvertDirectiveType;
import com.sdcloud.framework.common.UUIDUtil;
import com.sound.cloud.rpc.api.MqttInterface;
import com.sound.cloud.rpc.bean.Ad;
@Service("deviceReportService")
public class DeviceReportServiceImpl implements DeviceReportService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AdvertDirectiveDao advertDirectiveDao;
	@Autowired
	private DeviceAdvertisementDao deviceAdvertisementDao;
	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private MqttInterface mqttInterface ;
	@Override
	public String dataReport(String deviceMac,String sn, Map<String, Object> datas) {
		try {
			logger.info("Enter the :{} method deviceMac:{}  sn:{} datas:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac,sn,datas);
			List<Long> adidLs=new ArrayList<Long>();
			if (datas.get("ads") != null&&!datas.get("ads").toString().trim().equals("")) {
				String[] adids=datas.get("ads").toString().split(",");
				for (String id : adids) {
					adidLs.add(Long.valueOf(id));
				}
				
			}
			advertDataSynchronization(deviceMac,
					adidLs);
			if(datas.get("adStatus")!= null){
				deviceDao.updateRun(deviceMac, Integer.valueOf(datas.get("adStatus").toString()));
			}
		} catch (Exception e) {
			logger.error("method {} execute error,deviceMac:{} sn:{} datas:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceMac,sn,datas, e);
			return "1";
		}
		return "0";
	}
	@Override
	public String errorReport(String deviceMac,String sn, Map<String, Object> datas) {
		try {
			logger.info("Enter the :{} method  deviceMac:{}  sn:{}  datas:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac,sn,datas);

			if (datas.get("ads") != null) {
				//advertDataSynchronization(deviceMac,
				//		(List<Long>) datas.get("adverts"));
			}
		} catch (Exception e) {
			logger.error("method {} execute error, sn:{} deviceMac:{}  sn:{}   datas:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceMac,sn,datas, e);
			return "1";
		}
		return "0";
	}
    private void advertDataSynchronization(String deviceMac,List<Long> deviceAdvertIds){
    	List<DeviceAdvertisement> advertisements=deviceAdvertisementDao.findByDeviceMac(deviceMac);
    	List<Long> advertIds=new ArrayList<Long>();
    	List<Long> deleteIds=new ArrayList<Long>();
    	if(deviceAdvertIds!=null&&deviceAdvertIds.size()>0){
    		deleteIds.addAll(deviceAdvertIds);
    	}
    	
    	List<Long> addIds=new ArrayList<Long>();
    	Map<Long,DeviceAdvertisement> dads=new HashMap<Long,DeviceAdvertisement>();
    	for (DeviceAdvertisement advertisement : advertisements) {
    		advertIds.add(advertisement.getAdvertisementId());
    		dads.put(advertisement.getAdvertisementId(), advertisement);
		}
    	addIds.addAll(advertIds);
    	if(deviceAdvertIds!=null&&deviceAdvertIds.size()>0){
    		addIds.removeAll(deviceAdvertIds);
    	}
    	deleteIds.removeAll(advertIds);
    	
    	//添加广告到设备
    	if(addIds.size()>0){
    		logger.info("Enter the :{} method  deviceMac:{} addIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac,addIds);
    		//操作记录
    		/*AdvertDirective advertDirective=new AdvertDirective();
    		advertDirective.setDeviceMac(deviceMac);
    		advertDirective.setName("广告添加");
    		advertDirective.setState(AdvertDirectiveState.fill);
    		advertDirective.setType(AdvertDirectiveType.add);
    		advertDirective.setCreateTime(new Date());
    		Long id = UUIDUtil.getUUNum();
    		advertDirective.setAdvertDirectiveId(id);
    		List<AdvertDirective> addis=new ArrayList<AdvertDirective>();
    		addis.add(advertDirective);
    		advertDirectiveDao.insert(addis);*/
    		Long id = UUIDUtil.getUUNum();
    		Map<String, Object> param=new HashMap<String,Object>();
    		param.put("advertisementIds", addIds);
    		List<Advertisement> advers=new ArrayList<Advertisement>();
    		advers=advertisementDao.findAllBy(param);
    		List<Ad> ads=new ArrayList<Ad>();
    		for (Advertisement advertisement : advers) {
    			Ad ad=new Ad();
    			ad.setId(advertisement.getAdvertisementId().toString());
    			ad.setUrl(advertisement.getFileUrl().split(",")[0]);
    			ad.setSecond(dads.get(advertisement.getAdvertisementId()).getResidenceTime());
    			ad.setCount(dads.get(advertisement.getAdvertisementId()).getPlayCount());
    			ads.add(ad);
    		}
    		List<String> macs=new ArrayList<String>();
    		macs.add(deviceMac);
    		if(ads.size()>0){
    			logger.info("Enter the addAd method  adId:{} macs:{} ads:{}",
    					id.toString(), macs, ads);
    			mqttInterface.addAd(id.toString(), macs, ads);
    		}
    		
    	}
    	//从设备删除广告
    	if(deleteIds.size()>0){
    		logger.info("Enter the :{} method  deviceMac:{} deleteIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac,deleteIds);
    	/*	AdvertDirective advertDirective=new AdvertDirective();
			advertDirective.setDeviceMac(deviceMac);
			advertDirective.setName("广告删除");
			advertDirective.setState(AdvertDirectiveState.fill);
			advertDirective.setCreateTime(new Date());
			Long id = UUIDUtil.getUUNum();
    		advertDirective.setAdvertDirectiveId(id);
    		advertDirective.setType(AdvertDirectiveType.delete);
    		List<AdvertDirective> addis=new ArrayList<AdvertDirective>();
    		addis.add(advertDirective);
    		advertDirectiveDao.insert(addis);*/
    		Long id = UUIDUtil.getUUNum();
			List<String> deviceMacs=new ArrayList<String>();
			deviceMacs.add(deviceMac);
			List<String> deviceAds=new ArrayList<String>();
			for (Long deviceAd : deleteIds) {
				deviceAds.add(deviceAd.toString());
			}
			logger.info("Enter the writeDev method  adId:{} macs:{} ads:{} arg:2",
					id.toString(), deviceMacs, deviceAds);
			mqttInterface.writeDev(id.toString(), deviceMacs, deviceAds, 2);
    	}
    	
		
    }
	@Override
	public String optionResult(OperationResult op) {
		try {
			logger.info("Enter the :{} method  op:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),op);

			AdvertDirective advertDirective = advertDirectiveDao
					.getAdvertDirective(Long.valueOf(op.getSn()));
			if(advertDirective==null){
				return "1";
			}
			if (op.getErroCode().equals("0")) {
				advertDirective.setState(AdvertDirectiveState.successful);
			} else {
				advertDirective.setState(AdvertDirectiveState.failure);
			}
			List<AdvertDirectiveReslut> list=new ArrayList<AdvertDirectiveReslut>();
			if (!advertDirective.getContent().equals("")) {
				ObjectMapper mapper = new ObjectMapper();
				JavaType type = mapper.getTypeFactory().constructParametricType(ArrayList.class,
						AdvertDirectiveReslut.class);
				list = (List<AdvertDirectiveReslut>) mapper.readValue(advertDirective.getContent(), type);
				for (AdvertDirectiveReslut adr : list) {
					String result=op.getErroCode();
					if(StringUtils.isEmpty(result)){
						continue;
					}
					adr.setState(AdvertDirectiveState.failure);
					if(result.equals("0")){
						adr.setState(AdvertDirectiveState.successful);
						adr.setDescribe("操作成功");
					}else if(result.equals("1")){
						adr.setDescribe("未知错误");
					}else if(result.equals("101")){
						adr.setDescribe("广告不存在");
					}else if(result.equals("102")){
						adr.setDescribe("广告已存在");
					}else if(result.equals("3")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("设备离线");
					}else if(result.equals("2")){
						adr.setDescribe("设备已处在操作状态");
					}else{
						adr.setDescribe("未知错误");
					}
				}
				advertDirective.setContent(new ObjectMapper().writeValueAsString(list));
			}
			advertDirective.setUpdateTime(new Date());
			advertDirectiveDao.update(advertDirective);
			return "0";
		} catch (Exception e) {
			logger.error("method {} execute error, :{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			return "1";
			
		}
     
	}
	@Override
	public String optionAdResult(OperationAdResult op) {
		try {
			logger.info("Enter the :{} method  op:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),op);

			AdvertDirective advertDirective = advertDirectiveDao
					.getAdvertDirective(Long.valueOf(op.getSn()));
			if(advertDirective==null){
				return "1";
			}
			advertDirective.setState(AdvertDirectiveState.successful);
			List<AdvertDirectiveReslut> list=new ArrayList<AdvertDirectiveReslut>();
			if (!advertDirective.getContent().equals("")) {
				ObjectMapper mapper = new ObjectMapper();
				JavaType type = mapper.getTypeFactory().constructParametricType(ArrayList.class,
						AdvertDirectiveReslut.class);
				list = (List<AdvertDirectiveReslut>) mapper.readValue(advertDirective.getContent(), type);
				for (AdvertDirectiveReslut adr : list) {
					if(adr.getAdId()==null){
						continue;
					}
					String result=op.getAds().get(adr.getAdId().toString());
					if(StringUtils.isEmpty(result)){
						continue;
					}
					adr.setState(AdvertDirectiveState.failure);
					if(result.equals("0")){
						adr.setState(AdvertDirectiveState.successful);
						adr.setDescribe("操作成功");
					}else if(result.equals("1")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("未知错误");
					}else if(result.equals("101")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("广告不存在");
					}else if(result.equals("102")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("广告已存在");
					}else if(result.equals("3")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("设备离线");
					}else if(result.equals("2")){
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("设备已处在操作状态");
					}else{
						advertDirective.setState(AdvertDirectiveState.failure);
						adr.setDescribe("未知错误");
					}
				}
				advertDirective.setContent(new ObjectMapper().writeValueAsString(list));
			}
			
			advertDirective.setUpdateTime(new Date());
			advertDirectiveDao.update(advertDirective);
			return "0";
		} catch (Exception e) {
			logger.error("method {} execute error, :{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			return "1";
			
		}
	}

}
