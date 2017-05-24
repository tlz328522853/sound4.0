package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.PointConfig;
import com.sdcloud.api.lar.service.PointConfigService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
/**
 * 
 * @author dingx
 *
 */
@RestController
@RequestMapping("/api/pointConfig")
public class PointConfigController {
	
	private final Logger logger = LoggerFactory.getLogger(super.getClass());
	
	@Autowired
	private PointConfigService pointConfigService;
		
	@RequestMapping("/findByOrg")
	public ResultDTO findByOrg(@RequestBody(required = false) LarPager<PointConfig> larPager) {
		
		try {
			PointConfig pointConfig;
			String org = larPager.getExtendMap().get("orgId").toString();
			pointConfig = pointConfigService.findByOrg(org);
			if(pointConfig == null){
				pointConfig = new PointConfig();
				pointConfig.setOrg(Long.valueOf(org));
				pointConfig.setPointRate(0+"");
				pointConfig.setPointLine(0+"");
				pointConfig.setCreateUser(0L);
				pointConfig.setCreateDate(new Date());
				pointConfigService.savePointConfig(pointConfig);
				logger.info("save pointConfig success !");
			}
			return ResultDTO.getSuccess(200, pointConfig);
		} catch (Exception e) {
			logger.info("findByOrg methods : param org is not exist !");
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
	/**
	 * APP业务员端获取机构积分支付比率接口
	 */
	@RequestMapping("/findPointConfigByOrgId/{orgId}")
	@ResponseBody
	public ResultDTO findPointConfigByOrgId(@PathVariable(value = "orgId") String orgId) throws Exception {
		logger.info("APP findpointConfigByOrgId method start !");
		List<PointConfig> list = new ArrayList<PointConfig>();
		PointConfig pointConfig;
		try {
			pointConfig = pointConfigService.findByOrg(orgId);
			if (pointConfig != null) {
				list.add(pointConfig);
				return ResultDTO.getSuccess(list);
			} else {
				pointConfig = new PointConfig();
				pointConfig.setOrg(Long.valueOf(orgId));
				pointConfig.setPointRate(0+"");
				pointConfig.setPointLine(0+"");
				pointConfig.setCreateUser(0L);
				pointConfig.setCreateDate(new Date());
				pointConfigService.savePointConfig(pointConfig);
				logger.info("save pointConfig success !");
				list.add(pointConfig);
				return ResultDTO.getSuccess(list);
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) PointConfig pointConfig, HttpServletRequest request) {
		try {
			// 获得当前登录用户
			
			Object userId = request.getAttribute("token_userId");
			if(userId == null){
				logger.info("Login user ID does not exist !");
			}
			pointConfig.setCreateUser(Long.valueOf(String.valueOf(userId)));
			pointConfig.setCreateDate(new Date());
			pointConfigService.savePointConfig(pointConfig);
			return ResultDTO.getSuccess(200, "保存成功！",null);
		} catch (Exception e) {
			logger.info("pointConfig save error !");
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) PointConfig pointConfig, HttpServletRequest request) {
		try {			
			// 获得当前登录用户
			String userId = request.getAttribute("token_userId").toString();
			pointConfig.setCreateUser(Long.valueOf(userId));
			pointConfig.setCreateDate(new Date());
			pointConfigService.updatePointConfig(pointConfig);
			return ResultDTO.getSuccess(200, "修改成功!",null);
		} catch (Exception e) { 
			logger.info("pointConfig update error !");
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
}
