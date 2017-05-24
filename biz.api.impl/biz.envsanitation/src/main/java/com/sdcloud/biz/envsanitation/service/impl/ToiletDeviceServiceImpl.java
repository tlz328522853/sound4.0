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

import com.sdcloud.api.envsanitation.entity.Device;
import com.sdcloud.api.envsanitation.entity.ToiletDevice;
import com.sdcloud.api.envsanitation.service.ToiletDeviceService;
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
@Service("toiletDeviceService")
public class ToiletDeviceServiceImpl implements ToiletDeviceService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ToiletDeviceDao toiletDeviceDao;
	
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public void insert(List<ToiletDevice> toiletDevices) throws Exception {
		logger.info("start method: long insert(ToiletDevice ToiletDevice), arg ToiletDevice: "
				+ toiletDevices);

		if (toiletDevices==null||toiletDevices.size()<=0) {
			logger.warn("arg ToiletDevice is null");
			throw new IllegalArgumentException("arg ToiletDevice is null");
		}
		for (ToiletDevice toiletDevice : toiletDevices) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			toiletDevice.setToiletDeviceId(id);;
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					toiletDeviceDao.insert(toiletDevices);
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
	public void delete(List<Long> toiletDevices) throws Exception {
		logger.info("start method: void delete(List<Long> ToiletDevices), arg ToiletDevices: "
				+ toiletDevices);
		if (toiletDevices == null || toiletDevices.size() == 0) {
			logger.warn("arg ToiletDevices is null or size=0");
			throw new IllegalArgumentException("arg ToiletDevices is null or size=0");
		}
		try {
			
			toiletDeviceDao.delete(toiletDevices);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(ToiletDevice toiletDevice) throws Exception {
		logger.info("start method: void update(ToiletDevice ToiletDevice), arg ToiletDevice: "
				+ toiletDevice);
		if (toiletDevice == null || toiletDevice.getToiletDeviceId() == null) {
			logger.warn("arg ToiletDevice is null or ToiletDevice 's inventoryId is null");
			throw new IllegalArgumentException("arg ToiletDevice is null or ToiletDevice 's addOidId is null");
		}
		try {
			
			toiletDeviceDao.update(toiletDevice);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Device> findBy(Pager<Device> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<ToiletDevice> findBy(Pager<ToiletDevice> pager, Map<String, Object> param), arg pager: "
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
				long totalCount = toiletDeviceDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Device> toiletDevices = toiletDeviceDao.findAllBy(param);

			pager.setResult(toiletDevices);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.debug("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public void deletes(Long dicId, List<Long> inventoryIds) throws Exception {
		try {
			logger.info("Enter the :{} method  dicId:{}  inventoryIds:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),dicId,inventoryIds);

			toiletDeviceDao.deletes(dicId, inventoryIds);
		} catch (Exception e) {
			logger.error("method {} execute error, dicId:{}  inventoryIds:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),dicId,inventoryIds, e);
			throw e;
		}
	}

	@Override
	public List<Device> findDevice(Long aseetId) throws Exception {
		List<Device> devices=new ArrayList<Device>();
		try {
			logger.info("Enter the :{} method  aseetId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName());
			
			devices=toiletDeviceDao.findDevice(aseetId);
		} catch (Exception e) {
			logger.error("method {} execute error, aseetId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),aseetId, e);
			throw e;
		}
		return devices;
	}

	@Override
	public void deleteAll(Long aseetId) throws Exception {
		try {
			logger.info("Enter the :{} method  aseetId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),aseetId);
			toiletDeviceDao.deleteAll(aseetId);
		} catch (Exception e) {
			logger.error("method {} execute error, aseetId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), aseetId,e);
			throw e;
		}
		
	}


}
