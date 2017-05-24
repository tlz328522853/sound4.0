package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.ShipmentExpressService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * 
 * @author wrs
 */
@RestController
@RequestMapping(value = "/app/dic")
public class AppDicController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DicService dicService;
	@Autowired
	private CityService cityService;
	@Autowired
	private ShipmentExpressService expressService;

	@RequestMapping(value = "/getDicList/{pid}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getDicList(@PathVariable Long pid) {
		List<Dic> list = new ArrayList<Dic>();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orderBy", "sequence");
			list = dicService.findByPid(pid, param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "获取字典失败");
		}
		return ResultDTO.getSuccess(AppCode.SUCCESS, "获取字典成功", list);
	}

	@RequestMapping(value = "/getExpress", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getExpress(@RequestBody(required = false) Map<String, Object> map) {
		logger.info("app/dic getExpress start");
		City city = null;
		try {
			Long cityId = Long.valueOf(map.get("city") + "");// 获取城市
			city = cityService.selectByCityId(cityId);
		} catch (Exception e) {
			logger.error("app/dic getExpress fail",e);
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "城市数据有误！");
		}
		if (city == null || city.getId() == null) {
			return ResultDTO.getSuccess(AppCode.SUCCESS, "城市与机构不匹配");
		}
		LarPager larPager = new LarPager();
		larPager.setOrderBy("sequence");
		larPager.setOrder("asc");
		larPager.setPageNo(1);
		larPager.setPageSize(1000);
		List ids = new ArrayList();
		ids.add(city.getOrg());
		larPager = expressService.findByOrgIds(larPager, ids);

		// Long city = Long.valueOf(map.get("city");

		// List<Dic> list = new ArrayList<Dic>();
		// try {
		// Map<String, Object> param = new HashMap<String, Object>();
		// param.put("orderBy", "sequence");
		// list = dicService.findByPid(pid, param);
		// } catch (Exception e) {
		// logger.error("err method, Exception: " + e);
		// return ResultDTO.getFailure(AppCode.BIZ_ERROR, "获取字典失败");
		// }
		return ResultDTO.getSuccess(AppCode.SUCCESS, "获取快递字典成功", larPager.getResult());
	}

}
