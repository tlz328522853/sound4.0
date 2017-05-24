package com.sdcloud.web.lar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/larClientUserAddress")
public class LarClientUserAddressController {
	
	@Autowired
	private LarClientUserAddressService larClientUserAddressService;
	
	@Autowired
	private LarClientUserService larClientUserService;
	
	//增加
	@RequestMapping(value="/saveUserAddress",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) LarClientUserAddress larClientUserAddress) throws Exception{
		LarPager<LarClientUser> result = null;
		try {
			if(larClientUserAddress!=null){
				boolean insertAddressGetId = larClientUserAddressService.insertSelective(larClientUserAddress);
				//查询一次用户列表
				if(insertAddressGetId){
					result = larClientUserService.selectByExample(new LarPager<LarClientUser>());
					return ResultDTO.getSuccess(result);
				}else{
					return ResultDTO.getFailure(500, "添加用户地址失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	//删
	@RequestMapping(value="/deleteUserAddress/{id}",method=RequestMethod.GET)
	public ResultDTO delete(@PathVariable(value="id") String id) throws Exception{
		try {
			if(id!=null && id.trim().length()>0){
				boolean deleteById = larClientUserAddressService.deleteById(id.trim());
				if(deleteById){
					return ResultDTO.getSuccess("地址删除成功！");
				}else{
					return ResultDTO.getFailure(500, "地址删除失败！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
		//修改
		@RequestMapping(value="/updateUserAddress",method=RequestMethod.POST)
		public ResultDTO update(@RequestBody(required=false) LarClientUserAddress larClientUserAddress) throws Exception{
			LarPager<LarClientUser> result = null;
			try {
				if(larClientUserAddress!=null && larClientUserAddress.getId()!=null && larClientUserAddress.getId().trim().length()>0){
					boolean updateByExampleSelective = larClientUserAddressService.updateByExampleSelective(larClientUserAddress);
					if(updateByExampleSelective){
						result = larClientUserService.selectByExample(new LarPager<LarClientUser>());
						return ResultDTO.getSuccess(result);
					}else{
						return ResultDTO.getFailure(500, "地址修改失败");
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
			} catch (Exception e) {
				throw e;
			}
		}
		
		@RequestMapping(value="/updateDefaultEnableById/{id}/{userId}/{defaultEnable}",method=RequestMethod.GET)
		public ResultDTO update(@PathVariable(value="id") String id,@PathVariable(value="userId") String userId,@PathVariable(value="defaultEnable") String defaultEnable) throws Exception{
			try {
				if(id!=null && id.trim().length()>0 && userId!=null && userId.trim().length()>0){
					boolean updateByExampleSelective = larClientUserAddressService.updateDefaultEnableById(id,userId,defaultEnable);
					if(updateByExampleSelective){
						return ResultDTO.getSuccess("状态更改成功!");
					}else{
						return ResultDTO.getFailure(500, "地址修改失败");
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
			} catch (Exception e) {
				throw e;
			}
		}
		
		//查询列表
		@RequestMapping("/getAllAddress")
		@ResponseBody
		public ResultDTO getAllAddress(@RequestBody(required=false) LarPager<LarClientUserAddress> larPager) throws Exception{
			LarPager<LarClientUserAddress> result = null;
			try {
				result = larClientUserAddressService.selectByExample(larPager);
			} catch (Exception e) {
				throw e;
			}
			return ResultDTO.getSuccess(result);
		}
		
		//查询指定用户的
		@RequestMapping(value="/getUserAddress/{id}",method=RequestMethod.POST)
		@ResponseBody
		public ResultDTO getUserAddress(@PathVariable(value="id") String id,@RequestBody(required=false) LarPager<LarClientUserAddress> larPager) throws Exception{
			LarPager<LarClientUserAddress> result = null;
			try {
				if(id!=null && id.trim().length()>0){
					result = larClientUserAddressService.selectByUserId(larPager,id.trim());
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
			} catch (Exception e) {
				throw e;
			}
			return ResultDTO.getSuccess(result);
		}
		
		//查询指定ID的
		@RequestMapping(value="/getUserAddressById/{id}",method=RequestMethod.GET)
		@ResponseBody
		public ResultDTO getUserAddressById(@PathVariable(value="id") String id) throws Exception{
			try {
				if(id!=null && id.trim().length()>0){
					LarClientUserAddress larClientUserAddress = larClientUserAddressService.selectByPrimaryKey(id.trim());
					if(larClientUserAddress!=null){
						return ResultDTO.getSuccess(larClientUserAddress);
					}else{
						return ResultDTO.getFailure(500, "没有这个地址数据");
					}
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
				}
			} catch (Exception e) {
				throw e;
		}
	}
}
