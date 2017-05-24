package com.sdcloud.web.lar.controller.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.ShipmentShop;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.ShipmentShopService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * Created by 韩亚辉 on 2016/3/23.
 */
@RestController
@RequestMapping("/app/shop")
public class AppShipmentShopController {
    @Autowired
    private ShipmentShopService shipmentShopService;
    @Autowired
    private CityService cityService;
    
    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public ResultDTO findAll(@RequestBody(required = false) LarPager<ShipmentShop> larPager) {
        try {
        	Map params=larPager.getParams();//行政区划ID转化为服务城市ID
        	Object c=params.get("city");
        	if(c!=null){
        		City city=cityService.selectByCityId((Integer)c+0L);
        		params.put("city", city.getId());
        		
        	}
            return ResultDTO.getSuccess(AppCode.SUCCESS,"获取商店成功",shipmentShopService.findAll(larPager).getResult());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }
}
