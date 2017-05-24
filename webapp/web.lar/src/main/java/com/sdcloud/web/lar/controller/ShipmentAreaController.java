package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.service.ShipmentAreaService;
import com.sdcloud.biz.lar.util.Constant;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.KeyToNameUtil;

@RestController
@RequestMapping("/api/shipmentArea")
public class ShipmentAreaController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	OrgService orgService;
	@Autowired
	private ShipmentAreaService shipmentAreaService;

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentArea> larPager) {
		try {
			List<Org> orgList = orgService.findById(1L, true);
			shipmentAreaService.findAll(larPager);
			KeyToNameUtil.convertOrg(orgList, larPager.getResult(), "getOrg", "setOrgName");
			return ResultDTO.getSuccess(200, larPager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/findByOrgIds")
	public ResultDTO findByOrgId(@RequestBody(required = false) LarPager<ShipmentArea> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			Map<Long, String> orgMap=new HashMap<Long, String>();//机构ID->name
			if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != id) {
					List<Org> list = orgService.findById(id, isParentNode);
					for (Org org : list) {
						ids.add(org.getOrgId());
						orgMap.put(org.getOrgId(), org.getName());
					}
				}

			}
			shipmentAreaService.findByOrgIds(larPager, ids);
			for(ShipmentArea area:	larPager.getResult()){
				area.setOrgName(orgMap.get(area.getOrg()));//机构名值转换
			}
			return ResultDTO.getSuccess(200, larPager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
    /**
     * 更改：新增物流区域
     * @author jzc 2016年6月15日
     * @param shipmentArea
     * @return
     */
	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) ShipmentArea shipmentArea) {
		try {
			if (shipmentArea != null && shipmentArea.getId()==null
					&&StringUtils.isNotEmpty(shipmentArea.getPosition())
					&&shipmentArea.getPosition().contains("[{")) {
				boolean flag=shipmentAreaService.save(shipmentArea);
				if(flag){
					//此处修改的前提，认为机构ID（mechanismId）存在
					//异步：此处添加新区域成功，需要根据机构ID>cityId>联系人列表>进行区域联系人验证
					shipmentAreaService.diffAddrByShipmentArea(shipmentArea,null,Constant.FROM_METHOD_INSERT);
					logger.info("异步执行>>物流区域添加成功>>重新修改联系人地址的areaType类型......id:"+shipmentArea.getId());
				}
				return ResultDTO.getSuccess(200, "保存成功!",flag);
			}
			else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	/**
	 * 更改：更新物流区域
	 * @author jzc 2016年6月15日
	 * @param shipmentArea
	 * @return
	 */
	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) ShipmentArea shipmentArea) {
		try {
			
			if (shipmentArea != null && shipmentArea.getId() != null) {
				//更新之前的区域
				ShipmentArea shipmentAreaBefore=shipmentAreaService.getById(shipmentArea.getId(),null);
				boolean flag = shipmentAreaService.update(shipmentArea);
				if (flag) {
					//异步：此处更新区域成功，需要根据 区域ID>cityId>联系人列表>进行区域联系人验证
					shipmentAreaService.diffAddrByShipmentArea(shipmentArea,shipmentAreaBefore,Constant.FROM_METHOD_UPDATE);
					logger.info("异步执行>>物流区域修改成功>>重新修改联系人地址的areaType类型......id:"+shipmentArea.getId());
				}
				return ResultDTO.getSuccess(200, "修改成功!",shipmentAreaService.update(shipmentArea));
			}
			else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
    /**
     * 更改：删除物流区域
     * @author jzc 2016年6月15日
     * @param id
     * @return
     */
	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			//获取删除前的区域对象
			ShipmentArea area=shipmentAreaService.getById(id,null);
			boolean deleteById = shipmentAreaService.deleteById(id);
			if (deleteById) {
				//异步：此处删除区域成功，需要根据 区域ID>cityId>联系人列表>进行区域联系人验证
				shipmentAreaService.diffAddrByShipmentArea(area,null,Constant.FROM_METHOD_DELETE);
				logger.info("异步执行>>物流区域删除成功>>重新修改联系人地址的areaType类型......id:"+id);
				return ResultDTO.getSuccess(200, "删除成功!",deleteById);
			}
			else{
				return ResultDTO.getFailure(500, "删除失败,请检查该片区下是否还有业务员");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}
}
