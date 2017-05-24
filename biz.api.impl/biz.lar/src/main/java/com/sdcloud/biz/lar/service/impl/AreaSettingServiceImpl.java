package com.sdcloud.biz.lar.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.biz.lar.dao.AreaSettingDao;
import com.sdcloud.biz.lar.util.Constant;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class AreaSettingServiceImpl implements AreaSettingService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AreaSettingDao areaSettingDao;
	@Autowired
	private LarClientUserAddressService larClientUserAddressService;
	
	
	/**
	 * 需要根据机构ID>cityId>联系人列表>进行区域联系人验证和区分,
	 * areaSetting 回收区域对象  || areaSetting 回收区域对象 
	 * @author jzc 2016年6月15日
	 * @param areaSetting 回收区域对象 
	 * @param areaSettingBefore 修改前的回收区域
	 * @param fromMethod 来自方法类型：例 FROM_METHOD_INSERT
	 */
	@Override
	public void diffAddrByAreaSetting(AreaSetting areaSetting
			             ,AreaSetting areaSettingBefore,Integer fromMethod) {
		String areaNowPostiton = null;//当前区域positon
		String areaBeforePotion = null;//更新前区域pisition
		String orgId = null;//机构ID
		try {
			if(fromMethod==Constant.FROM_METHOD_INSERT||fromMethod==Constant.FROM_METHOD_DELETE){
				//添加区域方法  //删除区域方法
				if(areaSetting!=null){
					areaNowPostiton=areaSetting.getAreaPosition();
					orgId=areaSetting.getMechanismId();
				}
			}
			else if(fromMethod==Constant.FROM_METHOD_UPDATE){
				//更新区域方法
				if(areaSetting!=null){//当前区域对象
					areaNowPostiton=areaSetting.getAreaPosition();
					orgId=areaSetting.getMechanismId();
				}
				if(areaSettingBefore!=null){//以前区域对象
					areaBeforePotion=areaSettingBefore.getAreaPosition();
					orgId=areaSettingBefore.getMechanismId();
				}
				if((areaNowPostiton==null&&areaBeforePotion==null)
						||(areaNowPostiton!=null&&areaBeforePotion!=null
						  &&areaNowPostiton.equals(areaBeforePotion))){
					logger.info("回收区域更新：区域大小未变，无需修改该城市的用户地址areaType！");
					return;
				}
			}
			else{
				//其它
			}
			logger.info("机构："+orgId+"-------------回收区域变动，异步执行代码开始!");
			larClientUserAddressService.validateAddrByAreaChange(areaNowPostiton, areaBeforePotion
					, Constant.FROM_AREA_SETTING, fromMethod, orgId);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public int countById(String mechanismId) throws Exception {
		int count = 0;
		try {
			count = areaSettingDao.countById(mechanismId);
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		if (id!=null && id.trim().length()>0) {
			int count = 0;
			try {
				count = areaSettingDao.deleteById(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional
	public boolean insertSelective(AreaSetting areaSetting) throws Exception {
		if (areaSetting != null) {
			int count = 0;
			try {
				areaSetting.setId(String.valueOf(UUIDUtil.getUUNum()));
				areaSetting.setEnable(0);
				areaSetting.setCreateDate(new Date());
				count = areaSettingDao.insertSelective(areaSetting);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}

	@Override
	@Transactional
	public boolean insertAreaGetId(AreaSetting areaSetting) throws Exception {
		if (areaSetting != null) {
			int count = 0;
			try {
				areaSetting.setId(String.valueOf(UUIDUtil.getUUNum()));
				areaSetting.setEnable(0);
				areaSetting.setCreateDate(new Date());
				areaSettingDao.insertSelective(areaSetting);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("larClientUser is null");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<AreaSetting> selectByExample(LarPager<AreaSetting> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<AreaSetting>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
//			Map<String, Object> params = pager.getParams();
//			String mechanismId = "0";
//			if(params==null){
//				params = new HashMap<String, Object>();
//				pager.setParams(params);
//			}else{
//				if(params.containsKey("mechanismId")){
//					mechanismId=String.valueOf(params.get("mechanismId"));
//				}
//			}
//			if (pager.isAutoCount()) {
//				long totalCount = areaSettingDao.countById(mechanismId);
//				pager.setTotalCount(totalCount);
//				if (totalCount <= 0) {
//					return pager;
//				}
//			}
		  Map<String, Object> params = pager.getParams();
            if (params == null || params.size() <= 0) {
                throw new IllegalArgumentException("params is error");
            }
            if (pager.isAutoCount()) {
                long totalCount = 0;
                totalCount = areaSettingDao.countByIds(params);
                pager.setTotalCount(totalCount);
                if (totalCount <= 0) {
                    return pager;
                }
            }
			List<AreaSetting> result = areaSettingDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Override
	@Transactional
	public boolean deleteByExample(Map<String, Object> params) throws Exception {
		int count = 0;
		try {
			count = areaSettingDao.deleteByExample(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public AreaSetting selectByPrimaryKey(String id) throws Exception {
		if (id != null && id.length() > 0) {
			try {
				AreaSetting areaSetting = areaSettingDao.selectByPrimaryKey(id);
				return areaSetting;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional
	public boolean updateByExampleSelective(AreaSetting areaSetting)
			throws Exception {
		if (areaSetting != null && areaSetting.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("areaSetting", areaSetting);
			//修改用户信息
			int count = areaSettingDao.updateByExampleSelective(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("larClientUser or id is error");
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<AreaSetting> selectAreaById(String id) throws Exception {
		if (id != null && id.length() > 0) {
			try {
				List<AreaSetting> areaSettings = areaSettingDao.selectAreaById(id);
				return areaSettings;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

}
