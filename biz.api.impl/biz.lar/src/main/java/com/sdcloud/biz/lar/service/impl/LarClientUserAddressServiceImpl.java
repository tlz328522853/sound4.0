package com.sdcloud.biz.lar.service.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.biz.lar.dao.AreaSettingDao;
import com.sdcloud.biz.lar.dao.LarClientUserAddressDao;
import com.sdcloud.biz.lar.dao.ShipmentAreaDao;
import com.sdcloud.biz.lar.util.Constant;
import com.sdcloud.biz.lar.util.MapUtil;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class LarClientUserAddressServiceImpl implements LarClientUserAddressService{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LarClientUserAddressDao larClientUserAddressDao;
	@Autowired
	private AreaSettingDao areaSettingDao;//回收区域
	@Autowired
	private AreaSettingService areaSettingService;//回收区域
	@Autowired
	private ShipmentAreaDao shipmentAreaDao;//物流区域
	@Autowired
	private CityService cityService;//城市
	@Autowired
    private TaskExecutor taskExecutor;//线城池 引入
	
	
	/**
	 * 区域变更内部线程类，异步执行
	 * @author jzc
	 * 2016年6月16日
	 */
    class AreaChangeThread extends Thread{
		
		 String areaNowPostiton;//当前的区域位置
		 String areaBeforePotion;//变更前的区域位置  可为null
		 Integer from;//来自：物流|回收
		 Integer fromMethod;//方法：添加、删除、 修改
		 String orgId;//机构ID
    	
		public AreaChangeThread(String areaNowPostiton, String areaBeforePotion, Integer from, Integer fromMethod,
				String orgId) {
			super();
			this.areaNowPostiton = areaNowPostiton;
			this.areaBeforePotion = areaBeforePotion;
			this.from = from;
			this.fromMethod = fromMethod;
			this.orgId = orgId;
		}

		@Override
		public void run() {
			if(this.from!=null&&this.fromMethod!=null
							&&StringUtils.isNotEmpty(this.orgId)){
				
				if(this.fromMethod==Constant.FROM_METHOD_INSERT){//添加方法
					if(this.areaNowPostiton!=null){
						logger.info("执行添加方法！");
						insertAreaChange(this.areaNowPostiton,this.from,this.orgId);
					}else{
						logger.warn("------添加区域更改，区域的经纬度区域为空！");
					}
				}
				else if(this.fromMethod==Constant.FROM_METHOD_DELETE){//删除方法
					if(this.areaNowPostiton!=null){
						logger.info("执行删除方法！");
						deleteAreaChange(this.areaNowPostiton,this.from,this.orgId);
					}else{
						logger.warn("------删除区域更改，区域的经纬度区域为空！");
					}
				}
				else if(this.fromMethod==Constant.FROM_METHOD_UPDATE){//更新方法
					//添加一样
					if(this.areaNowPostiton!=null&&this.areaBeforePotion==null){
						logger.info("更新>>执行添加方法！更改前区域positon is null");
						insertAreaChange(this.areaNowPostiton,this.from,this.orgId);
					}
					//删除一样
					else if(this.areaNowPostiton==null&&this.areaBeforePotion!=null){
						logger.info("更新>>执行删除方法！当前区域positon is null");
						deleteAreaChange(this.areaBeforePotion,this.from,this.orgId);
					}
					//更新
					else if(this.areaNowPostiton!=null&&this.areaBeforePotion!=null){
						logger.info("更新>>执行更新方法！更改前后区域都不为空");
						updateAreaChange(this.areaNowPostiton,this.areaBeforePotion,this.from,this.orgId);
					}
					else{
						logger.warn("更新>>------区域更改，更改前后的区域经纬度区域都 为空！");
					}
				}
				else{
					
				}
				logger.info("end:本次区域更改，异步执行结束！");
			}
			else{
				logger.error("end:区域变更参数有误！areaNowPostiton="+this.areaNowPostiton+
						",from="+this.from+",fromMethod="+this.fromMethod+",orgId="+this.orgId);
			}
		}
		
	}
    
	/**
	 * 
	 * @author jzc 2016年6月15日
	 * @param areaNowPostiton 当前的区域位置
	 * @param areaBeforePotion 变更前的区域位置  可为null
	 * @param from 来自：物流|回收
	 * @param fromMethod 方法：添加、删除、 修改
	 * @param orgId 机构ID
	 * @return
	 */
	@Override
	public void validateAddrByAreaChange(String areaNowPostiton, String areaBeforePotion, Integer from,
			Integer fromMethod, String orgId) {
		try {
			AreaChangeThread areaChangeThread=new AreaChangeThread(areaNowPostiton, areaBeforePotion, from, fromMethod, orgId);
			taskExecutor.execute(areaChangeThread);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	/**
	 * 新增区域变更，更新用户地址areaType
	 * @author jzc 2016年6月16日
	 * @param areaNowPostiton
	 * @param from
	 * @param orgId
	 */
	private void insertAreaChange(String areaNowPostiton,Integer from,String orgId){
        //1.根据机构orgId，从数据库获取城市的ID
		//2.根据城市ID获取该城市所有未删除的联系人集合
		//3.用areaNowPostiton与每个用户地址positon做对比,成功的放入成功集合
		List<LarClientUserAddress> sucUserAddresses=this.getSucUserAddList(areaNowPostiton,orgId);
		//4.对比成功的(userAreaType)与from(1物流，2回收)做比较并设置areaType属性,需要更新的放入更新集合
		//判断规则 例如：物流1  用户areaType:0>0, 1>1, 2>0, 3>1, 其它>1
		List<LarClientUserAddress> updateUserAddresses=this.addChangeUserAreaTypes(sucUserAddresses, from);
		//5.批量更新areaType修改的信息
		this.batchUpateUserAddss(updateUserAddresses);
	}
	
	/**
	 * 删除区域变更，更新用户地址areaType
	 * @author jzc 2016年6月16日
	 * @param areaNowPostiton
	 * @param from
	 * @param orgId
	 */
	private void deleteAreaChange(String areaNowPostiton,Integer from,String orgId){
		//1.根据机构orgId，从数据库获取城市的ID
		//2.根据城市ID获取该城市所有未删除的联系人集合
		//3.用areaNowPostiton与每个用户地址positon做对比,成功的放入成功集合
		List<LarClientUserAddress> sucUserAddresses=this.getSucUserAddList(areaNowPostiton,orgId);
		//4.根据机构orgId，从数据库获取该机构的所有未删除区域集合
		List<String> positions=this.delGetPositionsByOrgId(from, orgId);
		//5.用对比成功的用户地址集合 与 区域集合 做对比
		//6.对比成功的修改areaType状态，不成功的修改areaType为3
		  //判断规则 例如：物流1  用户areaType:0>0, 1>1, 2>0, 3>1, 其它>1
		  //需要更新的用户地址集合
		List<LarClientUserAddress> updateUserAddresses=this.delAreaChangeUserTypes(sucUserAddresses, positions, from);
		//7.批量更新areaType修改的信息
		this.batchUpateUserAddss(updateUserAddresses);
	}
	
	/**
	 * 修改区域变更，更新用户地址areaType
	 * @author jzc 2016年6月16日
	 * @param areaNowPostiton
	 * @param areaBeforePotion
	 * @param from
	 * @param orgId
	 */
	private void updateAreaChange(String areaNowPostiton, String areaBeforePotion
			, Integer from, String orgId){
		//1.根据机构orgId，从数据库获取城市的ID
		Long cityId=this.getCityOrgionIdByOrgId(orgId);
		if(cityId==null)return;
		//2.根据城市ID获取该所有为删除的联系人集合
		List<LarClientUserAddress> clientUserAddresses=this.getAllUserAddsByOrgId(cityId);
		//3.用areaNowPostiton 与联系人集合对比,成功的放入集合中，获取nowAreaAddress
		List<LarClientUserAddress> nowAreaAddress=
				this.getMatchSucAdds(areaNowPostiton, clientUserAddresses);
		//4.用areaBeforePotion 与联系人集合对比,成功的放入集合中，获取beforeAreaAddress
		List<LarClientUserAddress> beforeAreaAddress=
				this.getMatchSucAdds(areaBeforePotion, clientUserAddresses);
		//5.根据两个区域地址集合做对比，找重复数据，去除重复数据,并且修改地址的areaType
		List<LarClientUserAddress> updateUserAddresses=new ArrayList<>();
		if(CollectionUtils.isEmpty(nowAreaAddress)
				&&CollectionUtils.isNotEmpty(beforeAreaAddress)){
			//6当前区域无联系人地址，并且以前区域存在地址，与删除区域步骤一样beforeAreaAddress
			//6.4.根据机构orgId，从数据库获取该机构的所有未删除区域集合
			List<String> positions=this.delGetPositionsByOrgId(from, orgId);
			//6.5.用对比成功的用户地址集合 与 区域集合 做对比
			//6.6.对比成功的修改areaType状态，不成功的修改areaType为3
			updateUserAddresses=this.delAreaChangeUserTypes(beforeAreaAddress, positions, from);
		}
		else if(CollectionUtils.isNotEmpty(nowAreaAddress)
				&&CollectionUtils.isEmpty(beforeAreaAddress)){
			//7 当前区域有联系人地址，并且以前区域不存在地址，与新增区域步骤一样nowAreaAddress
			//7.4.对比成功的(userAreaType)与from(1物流，2回收)做比较并设置areaType属性,需要更新的放入更新集合
			updateUserAddresses=this.addChangeUserAreaTypes(nowAreaAddress, from);
		}
		else if(CollectionUtils.isNotEmpty(nowAreaAddress)
				&&CollectionUtils.isNotEmpty(beforeAreaAddress)){
			//8 当前区域有联系人地址，以前区域也存在，则找出重复的部分
			List<LarClientUserAddress> repeatAreaAddress=new ArrayList<>();
			for(LarClientUserAddress nowAddress:nowAreaAddress){
				
				for(LarClientUserAddress beforeAddress:beforeAreaAddress){
					if(repeatAreaAddress.contains(beforeAddress))continue;
					if(nowAddress.getId().equals(beforeAddress.getId())){
						//8.1重复部分直接判断areaType
						//需要更改的areaType对象加入updateUserAddresses中
						this.changeAddAreaType(updateUserAddresses, beforeAddress, from);
						//放入重复的集合
						repeatAreaAddress.add(beforeAddress);
					}
				}
			}
			//8.2当前集合-重复部分>>与新增判断areaType步骤一样
			nowAreaAddress.removeAll(repeatAreaAddress);
			updateUserAddresses.addAll(this.addChangeUserAreaTypes(nowAreaAddress, from));
			//8.3以前集合-重复部分>>删除步骤一样
			beforeAreaAddress.removeAll(repeatAreaAddress);
			List<String> positions=this.delGetPositionsByOrgId(from, orgId);
			updateUserAddresses.addAll(this.delAreaChangeUserTypes(beforeAreaAddress, positions, from));
			//8.2重复的区域>>重新验证areaType(与新增判断areaType一样)
			updateUserAddresses.addAll(this.addChangeUserAreaTypes(repeatAreaAddress, from));
		}
		else{
			//9
			logger.warn("新旧区域内不存在联系人地址，此次区域变更不涉及到联系人地址变更！");
		}
		
		//10.批量更新areaType修改的信息
		this.batchUpateUserAddss(updateUserAddresses);
	}
	
	
	/**1,2,3
	 * 根据机构orgId，获取区域匹配成功的联系人
	 * @author jzc 2016年6月16日
	 * @param orgId 
	 * @param areaNowPostiton 
	 * @return
	 */
	private List<LarClientUserAddress> getSucUserAddList(String areaNowPostiton, String orgId){
		 //1.根据机构orgId，从数据库获取城市的行政ID
		Long cityId=this.getCityOrgionIdByOrgId(orgId);
		if(cityId==null)return null;
		//2.根据城市ID获取该城市所有未删除的联系人集合
		List<LarClientUserAddress> clientUserAddresses=this.getAllUserAddsByOrgId(cityId);
		//3.用areaNowPostiton与每个用户地址positon做对比,成功的放入成功集合
		List<LarClientUserAddress> sucUserAddresses=
				this.getMatchSucAdds(areaNowPostiton, clientUserAddresses);
		return sucUserAddresses;
	}
	
	//1.根据机构orgId，从数据库获取城市的行政ID
	private Long  getCityOrgionIdByOrgId(String orgId){
		List<City> citys=cityService.findByOrg(orgId);
		if(CollectionUtils.isEmpty(citys)){
			logger.info("faild:根据机构ID="+orgId+">>>>未获取到可用 城市！");
			return null;
		}
		return citys.get(0).getRegionId();
	}
	
	//2.根据城市ID获取该城市所有未删除的联系人集合
	private List<LarClientUserAddress> getAllUserAddsByOrgId(Long cityId){
		List<LarClientUserAddress> clientUserAddresses=larClientUserAddressDao.selectByCityId(cityId);
		if(CollectionUtils.isEmpty(clientUserAddresses)){
			logger.info("faild:根据城市ID="+cityId+">>>>未获取到可用联系人！");
			clientUserAddresses=new ArrayList<>(10);
		}
		return clientUserAddresses;
	}	
	
	//3.用areaNowPostiton与每个用户地址positon做对比,成功的放入成功集合
	private List<LarClientUserAddress> getMatchSucAdds(String areaNowPostiton,List<LarClientUserAddress> clientUserAddresses){
		List<LarClientUserAddress> sucUserAddresses=new ArrayList<>(clientUserAddresses.size());
		Point2D.Double aDouble = new Point2D.Double();
		String latitude=null;
		String longitude=null;
		for(LarClientUserAddress userAddress:clientUserAddresses){
			latitude=userAddress.getLatitude();
			longitude=userAddress.getLongitude();
			try {
				aDouble.setLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				continue;
			}
			boolean flag=MapUtil.validatePosition(areaNowPostiton, aDouble);
			if(flag)sucUserAddresses.add(userAddress);
		}
		return sucUserAddresses;
	}	
	
	//(添加)4.对比成功的(userAreaType)与from(1物流，2回收)做比较并设置areaType属性,需要更新的放入更新集合
	private List<LarClientUserAddress> addChangeUserAreaTypes(List<LarClientUserAddress> sucUserAddresses,Integer from){
	        //判断规则 例如：物流1  用户areaType:0>0, 1>1, 2>0, 3>1, 其它>1
			List<LarClientUserAddress> updateUserAddresses=new ArrayList<>(sucUserAddresses.size());
			for(LarClientUserAddress sucUserAddress:sucUserAddresses){
				this.changeAddAreaType(updateUserAddresses, sucUserAddress, from);
			}
		return updateUserAddresses;
	}
	
	//4.根据机构orgId，从数据库获取该机构的所有未删除区域集合
	private List<String> delGetPositionsByOrgId(Integer from,String orgId){
		List<String> positions=new ArrayList<>(100000);
		if(from==Constant.FROM_SHIPMENT_AREA){//物流
			List<ShipmentArea> shipmentAreas= shipmentAreaDao.selectShipmentAreasByOrg(orgId);
			if(CollectionUtils.isNotEmpty(shipmentAreas)){
				for(ShipmentArea area:shipmentAreas){
					if(StringUtils.isNotEmpty(area.getPosition())
							&&area.getPosition().contains("[{")){
						positions.add(area.getPosition());
					}
				}
			}
		}
		else if(from==Constant.FROM_AREA_SETTING){//回收
			List<AreaSetting> areaSettings=areaSettingDao.selectAreaById(orgId);
			if(CollectionUtils.isNotEmpty(areaSettings)){
				for(AreaSetting area:areaSettings){
					if(StringUtils.isNotEmpty(area.getAreaPosition())
							&&area.getAreaPosition().contains("[{")){
						positions.add(area.getAreaPosition());
					}
				}
			}
		}
		else{
			logger.error("参数有误！from="+from);
		}
		return positions;
	}
	
	//5.用对比成功的用户地址集合 与 区域集合 做对比
	//6.对比成功的修改areaType状态，不成功也的修改areaType
	private List<LarClientUserAddress> delAreaChangeUserTypes(List<LarClientUserAddress> sucUserAddresses,
			List<String> positions,Integer from){
		//需要更新的用户地址集合
		List<LarClientUserAddress> updateUserAddresses=new ArrayList<>();
		if(CollectionUtils.isNotEmpty(positions)
				&&CollectionUtils.isNotEmpty(sucUserAddresses)){
			Point2D.Double aDouble = new Point2D.Double();
			String latitude=null;
			String longitude=null;
			
			//用户地址集合
			for(LarClientUserAddress userAddress:sucUserAddresses){
				//片区区域经纬度集合
				boolean isArea=false;//是否存在其它区域内
				for(String positon:positions){
					if(updateUserAddresses.contains(userAddress))continue;
					latitude=userAddress.getLatitude();
					longitude=userAddress.getLongitude();
					try {
						aDouble.setLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						continue;
					}
					//验证用户地址 与 区域的包含关系
					isArea=MapUtil.validatePosition(positon, aDouble);
					if(isArea){//该用户在区域内，判断areaType并修改
						this.changeAddAreaType(updateUserAddresses, userAddress, from);
						break;
					}
				}
				if(!isArea){//用户不在区域内，查看该用户的areaType,并设置新的值
					this.changeDelAreaType(updateUserAddresses, userAddress, from);
				}
			}
		}
		return updateUserAddresses;
	}
	
	/**
	 * 添加、更新：修改区域内的用户地址areaType
	 * @author jzc 2016年6月16日
	 * @param updateUserAddresses 需要更新的用户地址集合
	 * @param userAddress 用户地址
	 * @param from 1 物流 2回收
	 */
	private void changeAddAreaType(List<LarClientUserAddress> updateUserAddresses,
			LarClientUserAddress userAddress,Integer from){
		//判断规则 例如：物流1  用户areaType:0>0, 1>1, 2>0, 3>1, 其它>1
		Integer userAreaType=userAddress.getAreaType();
		if(userAreaType==null||userAreaType>3||userAreaType<0){
			userAddress.setAreaType(from);
			updateUserAddresses.add(userAddress);
		}
		else{
			if(userAreaType!=from&&userAreaType!=0){
				switch (userAreaType) {
                case 3:
                	userAddress.setAreaType(from);
                	updateUserAddresses.add(userAddress);
					break;
				default:
					userAddress.setAreaType(0);
					updateUserAddresses.add(userAddress);
					break;
				}
			}
		}
	}
	
	
	/**
	 * 删除：修改区域内的用户地址areaType
	 * 例如：要删除的物流区域中的用户 并不存在其它物流区域内，但仍有可能是回收区域内的用户
	 * @author jzc 2016年6月16日
	 * @param updateUserAddresses 需要更新的用户地址集合
	 * @param userAddress 用户地址
	 * @param from 1 物流 2回收
	 */
	private void changeDelAreaType(List<LarClientUserAddress> updateUserAddresses,
			LarClientUserAddress userAddress,Integer from){
		//踢出人判断规则 例如：物流1  用户areaType:0>2, 1>3, 2>2, 3>3, 其它>3
		Integer userAreaType=userAddress.getAreaType();
		if(userAreaType==null||userAreaType>3||userAreaType<0){
			userAddress.setAreaType(3);
			updateUserAddresses.add(userAddress);
		}
		else{
			if(userAreaType==from||userAreaType==0){
				switch (userAreaType) {//0,1
                case 0:
                	userAddress.setAreaType(from==1?2:1);
					updateUserAddresses.add(userAddress);
					break;
				default:
					userAddress.setAreaType(3);
                	updateUserAddresses.add(userAddress);
					break;
				}
			}
		}
	}
	//5.批量更新areaType修改的信息
	@Transactional
	private void batchUpateUserAddss(List<LarClientUserAddress> updateUserAddresses){
		
		if(CollectionUtils.isNotEmpty(updateUserAddresses)){
			//批量更新
			int count=larClientUserAddressDao.updateBatch(updateUserAddresses);
			if(count>0){
				logger.info("批量更新数据成功！数据量>>："+updateUserAddresses.size());
			}else{
				logger.info("批量更新数据失败！应该更新的数据量："+updateUserAddresses.size());
			}
		}
	}

	
	
	
	/**
	 * 验证用户新添加地址 是否在城市机构区域内：false否  true是    orgId城市机构ID
	 * @author jzc 2016年6月14日
	 * @param larClientUserAddress
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean validateAddrArea(LarClientUserAddress larClientUserAddress, long orgId) throws Exception {
		String longitude=larClientUserAddress.getLongitude();
		String latitude=larClientUserAddress.getLatitude();
		if(orgId==0
				||StringUtils.isEmpty(longitude)
				||StringUtils.isEmpty(latitude)) {
			logger.info("data is null... orgId="+orgId
					+",longitude="+longitude
					+",latitude="+latitude);
			return false;
		}
		Point2D.Double aDouble = new Point2D.Double(Double.parseDouble(latitude), Double.parseDouble(longitude));
		boolean flagShipment=this.validateShipmentArea(orgId,aDouble);//物流
		boolean flagArea=this.validateAreaSetting(orgId,aDouble);//回收
		if(flagArea&&flagShipment){//都包括
			larClientUserAddress.setAreaType(0);
			return true;
		}
		else if(flagShipment){//物流
			larClientUserAddress.setAreaType(1);
			return true;
		}
		else if(flagArea){//回收
			larClientUserAddress.setAreaType(2);
			return true;
		}
		larClientUserAddress.setAreaType(3);//都不是
		return false;
	}
		
	/**
	 * 验证是否在回收片区
	 * @author jzc 2016年6月14日
	 * @param orgId
	 * @param aDouble
	 * @return
	 * @throws Exception
	 */
	private boolean validateAreaSetting(long orgId, Point2D.Double aDouble) throws Exception{
		boolean flag=false;
		LarPager<AreaSetting> pager = new LarPager<AreaSetting>();
		pager.setPageSize(100000);
		pager.getParams().put("mechanismId", orgId);
		pager=areaSettingService.selectByExample(pager);
		List<AreaSetting> listArea=pager.getResult();
		for(AreaSetting areaSetting:listArea){
			
			flag=MapUtil.validatePosition(areaSetting.getAreaPosition(), aDouble);
			if(flag)break;
		}
		
		return flag;
	}
	
    /**
     * 验证是否在物流片区
     * @author jzc 2016年6月14日
     * @param orgId
     * @param aDouble
     * @return
     */
    private boolean validateShipmentArea(long orgId,Point2D.Double aDouble){
    	boolean flag=false;
		LarPager<ShipmentArea> pager = new LarPager<ShipmentArea>();
		pager.setPageSize(100000);
		pager.getParams().put("org", orgId);
		List<ShipmentArea> shipmentAreas =shipmentAreaDao.findAll(pager);
		for(ShipmentArea shipmentArea:shipmentAreas){
			
			flag=MapUtil.validatePosition(shipmentArea.getPosition(), aDouble);
			if(flag)break;
		}
		
		return flag;
	}
    
    
	@Override
	@Transactional(readOnly = true)
	public int countById() throws Exception {
		int count = 0;
		try {
			count = larClientUserAddressDao.countById();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Override
	@Transactional
	public boolean deleteById(String id) throws Exception {
		int count = 0;
		if(id!=null && id.trim().length()>0){
			try {
				count = larClientUserAddressDao.deleteById(id);
			} catch (Exception e) {
				throw e;
			}
			if(count>0){
				return true;
			}else{
				return false;
			}
		}else{
			throw new IllegalArgumentException("id is error");
		}
	}

	@Override
	@Transactional
	public boolean insert(LarClientUserAddress larClientUserAddress) throws Exception {
		if(larClientUserAddress!=null){
			int count = 0;
			try {
				count = larClientUserAddressDao.insert(larClientUserAddress);
				if(count>0){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("larClientUserAddress is null");
		}
	}

	@Override
	@Transactional
	public boolean insertSelective(LarClientUserAddress larClientUserAddress) throws Exception {
		if(larClientUserAddress!=null){
			try {
				if(StringUtils.isEmpty(larClientUserAddress.getId())){
					larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
				}
				int count = larClientUserAddressDao.insertSelective(larClientUserAddress);
				if(count>0){
					if(null !=larClientUserAddress.getDefaultEnable() &&larClientUserAddress.getDefaultEnable()==1){
						//如果是默认地址，就把这个用户其他默认的改掉
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("id", larClientUserAddress.getId());
						params.put("larClientUser", larClientUserAddress.getLarClientUser().getId());
						larClientUserAddressDao.updateByDefaultEnable(params);
					}
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("larClientUserAddress is null");
		}
	}

	@Override
	@Transactional
	public boolean insertAddressGetId(LarClientUserAddress larClientUserAddress) throws Exception {
		if(larClientUserAddress!=null){
			try {
				larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
				int count = larClientUserAddressDao.insertAddressGetId(larClientUserAddress);
				if(count>0){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("larClientUserAddress is null");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<LarClientUserAddress> selectByExample(LarPager<LarClientUserAddress> larPager) throws Exception {
		if (larPager == null) {
			larPager = new LarPager<LarClientUserAddress>();
		}
		try {
			if (StringUtils.isEmpty(larPager.getOrderBy())) {
				// 多个用逗号
				larPager.setOrderBy("id");
			}
			if (StringUtils.isEmpty(larPager.getOrder())) {
				// 多个用逗号
				larPager.setOrder("desc");
			}

			if (larPager.isAutoCount()) {
				long totalCount = larClientUserAddressDao.countById();
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<LarClientUserAddress> result = larClientUserAddressDao.selectByExample(larPager);
			larPager.setResult(result);
			return larPager;
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	@Transactional
	public boolean updateByExampleSelective(LarClientUserAddress larClientUserAddress) throws Exception {
		if(larClientUserAddress!=null){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("larClientUserAddress", larClientUserAddress);
			try {
				int count = larClientUserAddressDao.updateByExampleSelective(params);
				if(count>0){
					//如果是默认地址，就替换
					if(larClientUserAddress.getDefaultEnable()==1){
						//如果是默认地址，就把这个用户其他默认的改掉
						params.clear();
						params.put("id", larClientUserAddress.getId());
						params.put("larClientUser", larClientUserAddress.getLarClientUser().getId());
						larClientUserAddressDao.updateByDefaultEnable(params);
					}
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("larClientUserAddress is null");
		}
	}

	@Override
	@Transactional
	public boolean updateById(LarClientUserAddress larClientUserAddress) throws Exception {
		if(larClientUserAddress!=null && larClientUserAddress.getId()!=null){
			try {
				return  larClientUserAddressDao.updateById(larClientUserAddress) > 0;
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("larClientUserAddress is null");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarClientUserAddress selectByPrimaryKey(String id) throws Exception {
		if(id!=null){
			try {
				LarClientUserAddress larClientUserAddress = larClientUserAddressDao.selectByPrimaryKey1(id);
				return larClientUserAddress;
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("id is null");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LarPager<LarClientUserAddress> selectByUserId(LarPager<LarClientUserAddress> larPager,String id) throws Exception {
		if (larPager == null) {
			larPager = new LarPager<LarClientUserAddress>();
		}
		try {
			Map<String, Object> params=null;
			if(larPager.getParams()==null){
				params = new HashMap<String, Object>();
			}else{
				params = larPager.getParams();
			}
			params.put("larClientUserId", id);
			larPager.setParams(params);
			if (StringUtils.isEmpty(larPager.getOrderBy())) {
				// 多个用逗号
				larPager.setOrderBy("id");
			}
			if (StringUtils.isEmpty(larPager.getOrder())) {
				// 多个用逗号
				larPager.setOrder("desc");
			}

			if (larPager.isAutoCount()) {
				long totalCount=0;
				if(params.get("city")!=null){
					String city= params.get("city").toString();
					totalCount = larClientUserAddressDao.countByUserAndCity(id,city);
				}else{
					totalCount = larClientUserAddressDao.countByUserId(id);
				}
				larPager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return larPager;
				}
			}
			List<LarClientUserAddress> result = larClientUserAddressDao.selectByUserId(larPager);
			larPager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return larPager;
	}

	@Override
	@Transactional
	public boolean updateDefaultEnableById(String id,String userId,String defaultEnable) {
		if(id!=null){
			try {
				int count=0;
				Map<String, Object> params = new HashMap<String, Object>();
				if(defaultEnable.equals("1")){
					params.put("defaultEnable", 1);
					params.put("id", id);
					count = larClientUserAddressDao.updateDefaultEnableById(params);
					params.clear();
					params.put("id", id);
					params.put("larClientUser", userId);
					count = larClientUserAddressDao.updateByDefaultEnable(params);
				}else{
					params.clear();
					params.put("defaultEnable", 0);
					params.put("id", id);
					count = larClientUserAddressDao.updateDefaultEnableById(params);
				}
				if(count>0){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		}else{
			throw new IllegalArgumentException("id is null");
		}
	}

	@Override
	public Map<String, LarClientUserAddress> selectByIds(List<String> ids) {
		Map<String, LarClientUserAddress> map = new HashMap<>();
		List<LarClientUserAddress> list = larClientUserAddressDao.selectByIds(ids);
		for (LarClientUserAddress ca : list) {
			map.put(ca.getId(), ca);
		}
		return map;
	}

}