package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.AppVersion;
import com.sdcloud.api.envsanitation.entity.Device;
import com.sdcloud.api.envsanitation.service.DeviceService;
import com.sdcloud.biz.envsanitation.dao.AdvertDirectiveDao;
import com.sdcloud.biz.envsanitation.dao.DeviceAdvertisementDao;
import com.sdcloud.biz.envsanitation.dao.DeviceDao;
import com.sdcloud.biz.envsanitation.dao.AppVersionDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.biz.envsanitation.dao.ToiletDeviceDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author lihuiquan
 *
 */
@Service("deviceService")
public class DeviceServiceImpl implements DeviceService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AdvertDirectiveDao advertDirectiveDao;
	@Autowired
	private DeviceDao deviceDao;
	@Autowired
	private AppVersionDao appVersionDao;
	@Autowired
	private DeviceAdvertisementDao deviceAdvertisementDao;
	@Autowired
	private AssetObjectDao assetObjectDao;
	@Autowired
	private ToiletDeviceDao toiletDeviceDao;
	@Transactional
	public void insert(List<Device> devices) throws Exception {
		logger.info("start method: long insert(Device Device), arg Device: "
				+ devices);

		if (devices==null||devices.size()<=0) {
			logger.warn("arg Device is null");
			throw new IllegalArgumentException("arg Device is null");
		}
		for (Device device : devices) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			device.setDeviceId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					deviceDao.insert(devices);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
	}
	
	@Transactional
	public List<AppVersion> upgrade(List<String> deviceMacs,List<String> versionNos) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		//通过当前版本号 找到 下一个版本号
		//param.put("oldVersionNo", versionNos.get(0).toString());
		List<AppVersion> list =  appVersionDao.findAllBy(param);
		List<AppVersion> appversions = new ArrayList<AppVersion>();
		String versionNo = "";
		if(versionNos.size() > 0 ){
			versionNo = versionNos.get(0).toString();
			List<AppVersion> a = findAllAppVersion(list,new ArrayList<AppVersion>(),versionNo);
			return a;
		}
		return null;
		
	}
	/**
	 * 
	 * @param allList  所有的版本 集合
	 * @param newList  根据当前版本号找出来的 可以升级的版本集合
	 * @param versionNo 当前版本号
	 * @return
	 */
	List<AppVersion> findAllAppVersion(List<AppVersion> allList,List<AppVersion> newList,String versionNo){
		for (AppVersion appVersion : allList) {
			if(appVersion.getOldVersionNo().equals(versionNo)){
				newList.add(appVersion);
				return findAllAppVersion(allList, newList, appVersion.getVersionNo());
			}
		}
		return newList;
	}
	
	@Transactional
	public void delete(List<String> deviceMacs) throws Exception {
		logger.info("start method: void delete(List<Long> Devices), arg Devices: "
				+ deviceMacs);
		if (deviceMacs == null || deviceMacs.size() == 0) {
			logger.warn("arg Devices is null or size=0");
			throw new IllegalArgumentException("arg Devices is null or size=0");
		}
		try {
			this.advertDirectiveDao.deleteByMacs(deviceMacs);
			this.deviceAdvertisementDao.deleteByMacs(deviceMacs);
			this.toiletDeviceDao.deleteByMacs(deviceMacs);
			deviceDao.delete(deviceMacs);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Device device) throws Exception {
		logger.info("start method: void update(Device Device), arg Device: "
				+ device);
		if (device == null || device.getDeviceMac() == null) {
			logger.warn("arg Device is null or Device 's deviceMac is null");
			throw new IllegalArgumentException("arg Device is null or Device 's addOidId is null");
		}
		try {
			
			deviceDao.update(device);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Device> findBy(Pager<Device> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<Device> findBy(Pager<Device> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<Device>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("createTime");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = deviceDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Device> Devices = deviceDao.findAllBy(param);

			pager.setResult(Devices);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.debug("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void updateRun(String deviceMac, Integer run) throws Exception {
		try {
			logger.info("Enter the :{} method  deviceMac:{} run:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),deviceMac,run);

			deviceDao.updateRun(deviceMac, run);
		} catch (Exception e) {
			logger.error("method {} execute error, deviceMac:{} run:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),deviceMac,run, e);
			throw e;
		}
	}



}
