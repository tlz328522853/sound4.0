package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.RecoveryBlue;
import com.sdcloud.api.lar.entity.RecyclingList;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.RecyclingItemsService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/recyclingItems")
public class AppRecyclingItemsController {

	@Autowired
	private RecyclingItemsService recyclingItemsService;
	
	@Autowired
	private CityService cityService;
	
	//获取每个类型下的回收物品列表
	@RequestMapping(value = "/getRecyclingList",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getRecyclingList(){
		try {
			List<RecyclingList> recyclingTypes = recyclingItemsService.getRecyclingList();
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"获取回收物品列表成功",recyclingTypes);
			}else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "没有回收物品数据",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//获取每个类型下的回收物品列表
	@RequestMapping(value = "/getCityRecyclingList",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO getCityRecyclingList(@RequestBody(required = false) Map<String, Object> cityId){
		try {
			Object object = cityId.get("cityId");
			if(null ==object)
				return ResultDTO.getSuccess(AppCode.BAD_REQUEST, "参数有误！",null);
			City city = cityService.selectByCityId(Long.parseLong(object.toString()));
			if(null == city)
				return ResultDTO.getSuccess(AppCode.BAD_REQUEST, "该城市没有开通服务！",null);
			List<RecyclingList> recyclingTypes = recyclingItemsService.getCityRecyclingList(city.getOrg());
			
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"获取回收物品列表成功",recyclingTypes);
			}else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "没有回收物品数据",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	@RequestMapping(value = "/getRecyclingTypesForList")
	@ResponseBody
	public ResultDTO getRecyclingTypes(){
		try {
			List<RecyclingType> recyclingTypes = recyclingItemsService.getRecyclingTypes();
			if(recyclingTypes!=null && recyclingTypes.size()>0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"获取回收物品类型成功",recyclingTypes);
			}else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "没有物品类型数据",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	@RequestMapping("/getRecyclingNames")
	@ResponseBody
	public ResultDTO getRecyclingNames(@RequestBody(required = false) RecyclingType recyclingType) {
		try {
			List<RecyclingMaterial> recyclingMaterials = recyclingItemsService.getRecyclingNames(recyclingType.getId());
			if(recyclingMaterials!=null && recyclingMaterials.size()>0){
				return ResultDTO.getSuccess(AppCode.SUCCESS,"获取回收物品成功",recyclingMaterials);
			}else {
				return ResultDTO.getSuccess(AppCode.SUCCESS, "没有物品数据",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}

	@ExceptionHandler(value={Exception.class})
	public void handlerException(Exception ex){
		System.out.println(ex);
	}
	
	@RequestMapping(value="/test")
	@ResponseBody
	public void test1(){
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		ids.add("2");
		ids.add("3");
		try {
			recyclingItemsService.test1(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//保存回收蓝物品的接口
	@RequestMapping(value="/saveRecoveryBlue")
	@ResponseBody
	public ResultDTO saveRecoveryBlue(@RequestBody(required=false) List<RecoveryBlue> recoveryBlues){
		try {
			if(recoveryBlues!=null && recoveryBlues.size()>0){
				boolean save = recyclingItemsService.saveRecoveryBlue(recoveryBlues);
				if(save){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"添加回收蓝成功");
				}else{
					return ResultDTO.getSuccess(AppCode.BIZ_DATA, "添加回收蓝失败");
				}
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "添加回收蓝失败",null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//根据用户id查询回收蓝列表
	@RequestMapping(value="/getRecoveryBlues/{userId}",method=RequestMethod.GET)
	@ResponseBody
	public ResultDTO getRecoveryBlues(@PathVariable(value="userId") String userId){
		try {
			if(userId!=null && userId.trim().length()>0){
				List<RecoveryBlue> recoveryBlues = recyclingItemsService.getRecoveryBlues(userId);
				return ResultDTO.getSuccess(AppCode.SUCCESS,"回收蓝有数据",recoveryBlues);
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	//清空回收蓝,批量清空
	@RequestMapping(value="deleteRecoveryBluesByUserId/{ids}",method=RequestMethod.GET)
	public ResultDTO deleteRecoveryBluesByUserId(@PathVariable(value="ids") List<String> ids){
		try {
			if(ids!=null && ids.size()>0){
				boolean delete = recyclingItemsService.deleteRecoveryBluesByIds(ids);
				if(delete){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"回收蓝清空成功");
				}else{
					return ResultDTO.getSuccess(AppCode.SUCCESS,"回收蓝清空失败");
				}
			}else{
				return ResultDTO.getSuccess(AppCode.BIZ_DATA, "用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
}