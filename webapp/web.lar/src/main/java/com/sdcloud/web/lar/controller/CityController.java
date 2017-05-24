package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/city")
public class CityController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CityService cityService;
	@Autowired
	private OrgService orgService;

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<City> larPager) {
		try {
			larPager = cityService.findAll(larPager);
			return ResultDTO.getSuccess(200, larPager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) City city, HttpServletRequest request) {
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			city.setCreateUser(Long.valueOf(String.valueOf(userId)));
			city.setCityStatus(0);
			
			List<Long> list = new ArrayList<>();
			list.add(city.getOrg());
			LarPager<City> larPager = new LarPager<City>();
			
			Long count = cityService.countByOrgIds(larPager, list);
			larPager.getParams().put("regionId", city.getRegionId());
			Long count2 = cityService.countByOrgIds(larPager, null);//查询当前城市是否已经被添加过
			if(count > 0){
				return ResultDTO.getFailure("服务城市已存在!");
			}
			if(count2 > 0){
				return ResultDTO.getFailure("该城市已经被添加过!");
			}
			return ResultDTO.getSuccess(200, "保存成功！",cityService.save(city));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) City city, HttpServletRequest request) {
		try {
			City selectByCityId = cityService.selectById(city.getId());
			if(!city.getRegionId().equals(selectByCityId.getRegionId())){
				LarPager<City> larPager = new LarPager<City>();
				larPager.getParams().put("regionId", city.getRegionId());
				Long count = cityService.countByOrgIds(larPager, null);//查询当前城市是否已经被添加过
				if(count > 0){
					return ResultDTO.getFailure("修改失败,该城市已存在其他机构");
				}
			}
			
			// 获得当前登录用户
			String userId = request.getAttribute("token_userId").toString();
			city.setUpdateUser(Long.valueOf(userId));
			cityService.update(city);
			return ResultDTO.getSuccess(200, "修改成功!",null);
		} catch (Exception e) { ;
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
	
	//服务城市的  停用  和  启用
	@RequestMapping("/updateCityStatus")
	public ResultDTO updateCityStatus(@RequestBody(required = false) City city, HttpServletRequest request) {
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			city.setUpdateCityStatusUser(Long.valueOf(String.valueOf(userId)));
			if(city.getCityStatus()==0){
				city.setCityStatus((int)1);
			}else{
				city.setCityStatus((int)0);
			}
			return ResultDTO.getSuccess(200, cityService.update(city));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			return ResultDTO.getSuccess(200, cityService.delete(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<City> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                String idStr = map.get("orgId").toString();
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
                if (null != idStr) {
                    //是父节点再去查找
                    if (isParentNode) {
                    	String[] split = idStr.split("AAA");//idStr 如果有多个机构 ，使用"AAA"来隔开。		
                    	for(String orgId :split){
                    		 List<Org> list = orgService.findById(Long.parseLong(orgId), true);
                             for (Org org : list) {
                                 ids.add(org.getOrgId());
                             }
                    	}
                    }else{
                        larPager.getParams().put("org",Long.parseLong(idStr));
                        ids=null;
                    }
                }
            }
            return ResultDTO.getSuccess(200, cityService.findByOrgIds(larPager, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
}
