package com.sdcloud.web.lar.controller.app;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.entity.ShipmentExpression;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.LarRegionService;
import com.sdcloud.api.lar.service.ShipmentExpressionService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/city")
public class AppCityController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LarRegionService larRegionService;
	@Autowired
	private CityService cityService;
	@Autowired
	private ShipmentExpressionService shipmentExpressionService;

	/**
	 * 获取服务城市
	 */
	@RequestMapping(value = "/serviceCity", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO serviceCity(@RequestHeader HttpHeaders header) {
		try {
			logger.info("Enter the :{} method ", Thread.currentThread()
					.getStackTrace()[1].getMethodName());
			
			List<City> cityList = cityService.serviceCity();
			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取服务城市", cityList);
		} catch (Exception e) {
			logger.error("method {} execute error, Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}

	@RequestMapping(value = "/getRegions/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getRegions(@PathVariable(value = "id") String id) throws Exception {
		try {
			logger.info("Enter the :{} method  id:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),id);
			
			if (id != null && id.trim().length() > 0) {
				List<LarRegion> larRegions = larRegionService.getLarRegions(Integer.valueOf(id));
				if (larRegions != null && larRegions.size() > 0) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "获取地区成功", larRegions);
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_DATA, "没有数据");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} execute error, id:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), id, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}

	/**
	 * 获取公式 cityId-->城市ID type--> 1: 北京同城送  2:北京帮我买:
	 */
	@RequestMapping(value = "/expression")
	@ResponseBody
	public ResultDTO getExpression(@RequestBody(required = false) Map<String, Object> map) {
		try {
			logger.info("Enter the :{} method  map:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),map);
			
			if (map != null && !map.isEmpty()) {
				if(Integer.valueOf(2) == map.get("type")){
					map.put("type", "同城送");
				}
				if(Integer.valueOf(3) == map.get("type")){
					map.put("type", "帮我买");
				}
				Long org = -1L;
				City city = cityService.selectByCityId((Integer) map.get("cityId")+0L);
				org = city.getOrg();
				map.remove("cityId");
				map.put("org", org);
				LarPager<ShipmentExpression> larPager = new LarPager<>();
				larPager.setParams(map);
				List<ShipmentExpression> result = shipmentExpressionService.findAll(larPager).getResult();
				if (result.size() > 0) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "获取公式成功", result.get(0));
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_DATA, "未开通该服务");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} execute error, map:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), map, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}
	
	/**
	 * 城市是否停用接口
	 * @param cityId
	 * @return
	 */
	@RequestMapping(value="/isDisable",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO isDisable(@RequestBody(required=false) Map<String, Number> map){
		try {
			if(!CollectionUtils.isEmpty(map)){
				Number cityId = map.get("cityId");
				boolean flag = cityService.isDisable(cityId.longValue());
				return ResultDTO.getSuccess(flag);
			}else{
				return ResultDTO.getFailure(AppCode.BAD_REQUEST,"参数不能为空");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.BIZ_ERROR,"服务器故障!");
		}
	}
}
