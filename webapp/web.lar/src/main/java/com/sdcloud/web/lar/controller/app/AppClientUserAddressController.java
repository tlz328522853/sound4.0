package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/larClientUserAddress")
public class AppClientUserAddressController {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private LarClientUserAddressService larClientUserAddressService;
	@Autowired
	private CityService cityService;
	
	/**
	 * 修改：需要验证经纬度是否在片区 
	 * 增加：客户端传值areaType：0 所有，1物流，2回收
	 * @author jzc 2016年6月16日
	 * @param larClientUserAddress
	 * @return
	 */
	@RequestMapping(value = "/saveUserAddress", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) LarClientUserAddress larClientUserAddress) {
		try {
			if (larClientUserAddress != null
					&&StringUtils.isNotBlank(larClientUserAddress.getRegion())) {
				List<City> cityList=cityService.find(larClientUserAddress.getRegion());
				if(CollectionUtils.isNotEmpty(cityList)){
					LarRegion region=new LarRegion();
					region.setRegionId((Integer.valueOf(cityList.get(0).getRegionId().toString())));
					larClientUserAddress.setCityId(region);
					boolean valFalg=larClientUserAddressService.validateAddrArea(larClientUserAddress
							, cityList.get(0).getOrg());
					if(!valFalg){
						//用户添加的地址，没有在城市所属的区域内，返回错误信息
						logger.warn("----------用户添加的地址，没有在城市所属的区域内!");
						//return ResultDTO.getFailure(AppCode.BIZ_ERROR, "非常抱歉，该地址暂未开通服务，敬请谅解");
					}
				}
				else{
					//城市不存在，返回错误信息
					logger.warn("城市不存在，返回错误信息!");
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "添加用户地址失败");
				}
				larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
				boolean flag = larClientUserAddressService.insertSelective(larClientUserAddress);
				// 查询一次用户列表
				if (flag) {
					Map<String, String> resultMap=new HashMap<>();
					resultMap.put("id", larClientUserAddress.getId());
					return ResultDTO.getSuccess(AppCode.SUCCESS, "添加用户地址成功", resultMap);
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "添加用户地址失败");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}

	// 删
	@RequestMapping(value = "/deleteUserAddress/{id}", method = RequestMethod.GET)
	public ResultDTO delete(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = larClientUserAddressService.deleteById(id.trim());
				if (deleteById) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "删除联系人成功！", null);
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "删除联系人失败！");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}

	/**
	 * 修改:修改联系人，需要验证经纬度是否在片区 
	 * @author jzc 2016年6月16日
	 * @param larClientUserAddress
	 * @return
	 */
	@RequestMapping(value = "/updateUserAddress", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) LarClientUserAddress larClientUserAddress) {
		try {
			if (larClientUserAddress != null 
					&& StringUtils.isNotEmpty(larClientUserAddress.getId())
					&&StringUtils.isNotEmpty(larClientUserAddress.getRegion())) {
				List<City> cityList=cityService.find(larClientUserAddress.getRegion());
				if(CollectionUtils.isNotEmpty(cityList)){
					LarRegion region=new LarRegion();
					region.setRegionId(( Integer.valueOf(cityList.get(0).getRegionId().toString())));
					larClientUserAddress.setCityId(region);
					boolean valFalg=larClientUserAddressService.validateAddrArea(larClientUserAddress
							, cityList.get(0).getOrg());
					if(!valFalg){
						//用户添加的地址，没有在城市所属的区域内，返回错误信息
						logger.warn("----------用户添加的地址，没有在城市所属的区域内!");
						//return ResultDTO.getFailure(AppCode.BIZ_ERROR, "非常抱歉，该地址暂未开通服务，敬请谅解");
					}
				}
				else{
					//城市不存在，返回错误信息
					logger.warn("城市不存在，返回错误信息!");
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "添加用户地址失败");
				}
				boolean updateByExampleSelective = larClientUserAddressService
						.updateByExampleSelective(larClientUserAddress);
				if (updateByExampleSelective) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "修改联系人成功", larClientUserAddress);
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "修改联系人失败");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}

	@RequestMapping(value = "/updateDefaultEnableById/{id}/{userId}/{defaultEnable}", method = RequestMethod.GET)
	public ResultDTO update(@PathVariable(value = "id") String id, @PathVariable(value = "userId") String userId,
			@PathVariable(value = "defaultEnable") String defaultEnable) throws Exception {
		try {
			if (id != null && id.trim().length() > 0 && userId != null && userId.trim().length() > 0) {
				boolean updateByExampleSelective = larClientUserAddressService.updateDefaultEnableById(id, userId,
						defaultEnable);
				
				return updateByExampleSelective ? ResultDTO.getSuccess("状态更改成功!") :  ResultDTO.getFailure(500, "地址修改失败");
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	

	// 查询列表
	@RequestMapping("/getAllAddress")
	@ResponseBody
	public ResultDTO getAllAddress(@RequestBody(required = false) LarPager<LarClientUserAddress> larPager)
			throws Exception {
		LarPager<LarClientUserAddress> result = null;
		try {
			result = larClientUserAddressService.selectByExample(larPager);
			return ResultDTO.getSuccess(result);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 获取用户默认的地址
	 * @author jzc 2016年6月14日
	 * @param id
	 * @param larPager
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getDefaultUserAddress/{id}", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getDefaultUserAddress(@PathVariable(value = "id") String id,
			@RequestBody(required = false) LarPager<LarClientUserAddress> larPager) throws Exception {
		LarPager<LarClientUserAddress> result = null;
		
		try {
			Integer cusAreaType=Integer.parseInt(larPager.getParams().get("areaType").toString()); 
			if (id != null && id.trim().length() > 0&&cusAreaType!=null) {
				larPager.setPageNo(1);
				larPager.setPageSize(20);
				larPager.getParams().put("defaultEnable", 1);//默认地址
				result = larClientUserAddressService.selectByUserId(larPager, id.trim());
				LarClientUserAddress address=null;
				if(result.getResult()!=null&&result.getResult().size()>0){
					address=result.getResult().get(0);
					Integer resultAreaType=address.getAreaType();
					if(cusAreaType==0//0>>>0,1,2,3
							||(cusAreaType==1&&(resultAreaType==0||resultAreaType==1))//1>>>0,1
							||(cusAreaType==2&&(resultAreaType==0||resultAreaType==2))){//2>>>0,2
					}
	                else{
	                	
	                	address=null;
	                }
				}
				
				return ResultDTO.getSuccess(AppCode.SUCCESS, "查询默认联系人成功", address);
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");

		}

	}

	/**
	 * 查询指定用户的地址列表
	 * @author jzc 2016年6月14日
	 * @param id
	 * @param larPager
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserAddress/{id}", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO getUserAddress(@PathVariable(value = "id") String id,
			@RequestBody(required = false) LarPager<LarClientUserAddress> larPager) throws Exception {
		LarPager<LarClientUserAddress> result = null;
		try {
			Integer cusAreaType=Integer.parseInt(larPager.getParams().get("areaType").toString()); 
			if (id != null && id.trim().length() > 0) {
				larPager.setPageNo(1);
				larPager.setPageSize(100);
				result = larClientUserAddressService.selectByUserId(larPager, id.trim());
				Map<String, List<LarClientUserAddress>> map=this.differentAddress(result,cusAreaType);
				return ResultDTO.getSuccess(AppCode.SUCCESS, "查询联系人成功", map);
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception  e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");

		}

	}
	/**
	 * 区分地址 可用 不可用
	 * @author jzc 2016年6月14日
	 * @param map 返回客户端的可用 不可用键值对
	 * @param result 数据库返回地址列表
	 * @param cusAreaType 客户端请求地址区域类型
	 * @return
	 */
	private Map<String, List<LarClientUserAddress>> differentAddress( 
			LarPager<LarClientUserAddress> result, Integer cusAreaType){
		Map<String, List<LarClientUserAddress>> map=new HashMap<>();
		List<LarClientUserAddress> usableResult=new ArrayList<>();//可用的地址
		List<LarClientUserAddress> disableResult=new ArrayList<>();//不可用的地址
		//cusAreaType:访问参数：0所有区域地址,1物流区域地址,2回收区域地址
		//0
		if(cusAreaType==0){
			usableResult.addAll(result.getResult());
		}
		else if(cusAreaType==1){
			//1
			for(LarClientUserAddress address:result.getResult()){
				if(address.getAreaType()!=null){
					switch (address.getAreaType()) {
					case 0:
					case 1:
						usableResult.add(address);//可用
						break;
					default:
						disableResult.add(address);//不可用
						break;
					}	
				}
				else{
					disableResult.add(address);//不可用
				}
				
			}
		}
		else if(cusAreaType==2){
			//2
			for(LarClientUserAddress address:result.getResult()){
				if(address.getAreaType()!=null){
					switch (address.getAreaType()) {
					case 0:
					case 2:
						usableResult.add(address);//可用
						break;
					default:
						disableResult.add(address);//不可用
						break;
					}
				}
				else{
					disableResult.add(address);//不可用
				}
				
			}
		}
		map.put("usableResult", usableResult);
		map.put("disableResult", disableResult);
		return map;
	}

	/**
	 * 修改：查询指定ID的
	 * @author jzc 2016年6月17日
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserAddressById/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO getUserAddressById(@PathVariable(value = "id") String id) throws Exception {
		try {
			if (StringUtils.isNotEmpty(id)) {
				LarClientUserAddress larClientUserAddress = larClientUserAddressService.selectByPrimaryKey(id.trim());
				if (larClientUserAddress != null) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "查看联系人成功", larClientUserAddress);
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_DATA, "没有这个联系人");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
}

