package com.sdcloud.biz.lar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.ShipmentAreaService;
import com.sdcloud.biz.lar.dao.ShipmentAreaDao;
import com.sdcloud.biz.lar.dao.ShipmentOperationDao;
import com.sdcloud.biz.lar.util.Constant;

/**
 * 
 * @author wrs
 */
@Service
public class ShipmentAreaServiceImpl extends BaseServiceImpl<ShipmentArea> implements ShipmentAreaService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LarClientUserAddressService larClientUserAddressService;
	@Autowired
	private ShipmentAreaDao shipmentAreaDao;
	@Autowired
	private ShipmentOperationDao shipmentOperationDao;
	
	/**
	 * 需要根据机构ID>cityId>联系人列表>进行区域联系人验证和区分,
	 * @author jzc 2016年6月15日
	 * @param shipmentArea 物流区域对象 
	 * @param shipmentAreaBefore 修改前的物流区域
	 * @param fromMethod 来自方法类型：例 FROM_METHOD_INSERT
	 */
	@Override
	public void diffAddrByShipmentArea(ShipmentArea shipmentArea, ShipmentArea shipmentAreaBefore,
			Integer fromMethod) {
		String areaNowPostiton = null;//当前区域positon
		String areaBeforePotion = null;//更新前区域pisition
		Long orgId = null;//机构ID
		try {
			if(fromMethod==Constant.FROM_METHOD_INSERT||fromMethod==Constant.FROM_METHOD_DELETE){
				//添加区域方法  //删除区域方法
				if(shipmentArea!=null){
					areaNowPostiton=shipmentArea.getPosition();
					orgId=shipmentArea.getOrg();
				}
			}
			else if(fromMethod==Constant.FROM_METHOD_UPDATE){
				//更新区域方法
				if(shipmentArea!=null){//当前区域对象
					areaNowPostiton=shipmentArea.getPosition();
					orgId=shipmentArea.getOrg();
				}
				if(shipmentAreaBefore!=null){//以前区域对象
					areaBeforePotion=shipmentAreaBefore.getPosition();
					orgId=shipmentAreaBefore.getOrg();
				}
				if((areaNowPostiton==null&&areaBeforePotion==null)
						||(areaNowPostiton!=null&&areaBeforePotion!=null
						  &&areaNowPostiton.equals(areaBeforePotion))){
					logger.info("物流区域更新：区域大小未变，无需修改该城市的用户地址areaType！");
					return;
				}
			}
			else{
				//其它
			}
			logger.info("机构："+orgId+"-------------物流区域变动，异步执行开始!");
			larClientUserAddressService.validateAddrByAreaChange(areaNowPostiton, areaBeforePotion
					, Constant.FROM_SHIPMENT_AREA, fromMethod, orgId.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	/**
	 * 如果该片区 下存在业务员,则不能删除该片区
	 */
	@Override
	@Transactional
	public Boolean deleteById(Long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("area", id);
		List<ShipmentOperation> shipmentOperations = shipmentOperationDao.findByMap(params);
		if(null != shipmentOperations &&shipmentOperations.size()>0){
			return false;
		}
		return shipmentAreaDao.delete(id)>0;
	}
}
