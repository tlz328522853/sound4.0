package com.sdcloud.web.lar.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.service.MaaDateSettingService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/app/maaDateSetting")
public class AppMaaDateSettingController {
	
	@Autowired
	private MaaDateSettingService maaDateSettingService;
	
	// 查询列表
	@RequestMapping("/getMaaDateSettings")
	@ResponseBody
	public ResultDTO getMaaDateSettings(@RequestBody(required = false) LarPager<MaaDateSetting> larPager) throws Exception {
		try {
			larPager = maaDateSettingService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	@RequestMapping(value = "/saveMaaDateSetting", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) MaaDateSetting maaDateSetting)
			throws Exception {
		LarPager<MaaDateSetting> result = null;
		try {
			if (maaDateSetting != null && (maaDateSetting.getId()==null || maaDateSetting.getId().length()<=0)) {
				boolean insertUserGetId = maaDateSettingService.insertSelective(maaDateSetting);
				if (insertUserGetId) {
					result = maaDateSettingService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "预约时间添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/deleteMaaDateSetting/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") String id) throws Exception {
		LarPager<MaaDateSetting> result = null;
		try {
			if (id != null && id.trim().length()>0) {
				boolean deleteById = maaDateSettingService.deleteById(id);
				if (deleteById) {
					result = maaDateSettingService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "预约时间删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/updateMaaDateSetting", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) MaaDateSetting maaDateSetting) throws Exception {
		LarPager<MaaDateSetting> result = null;
		try {
			if (maaDateSetting != null && maaDateSetting.getId() != null && maaDateSetting.getId().trim().length() > 0) {
				boolean updateByExampleSelective = maaDateSettingService.updateByExampleSelective(maaDateSetting);
				if (updateByExampleSelective) {
					result = maaDateSettingService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "预约时间修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*//根据机构ID查询供应商列表
	@RequestMapping("/getOwnedSuppliersById/{id}")
	@ResponseBody
	public ResultDTO getOwnedSuppliersById(@PathVariable(value="id") String id) throws Exception {
		try {
			if(id!=null && id.trim().length()>0){
				List<OwnedSupplier> ownedSuppliers = ownedSupplierService.getOwnedSuppliersById(id);
				if(ownedSuppliers!=null && ownedSuppliers.size()>0){
					return ResultDTO.getSuccess(ownedSuppliers);
				}else{
					return ResultDTO.getFailure(500, "该机构没有供货商！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/
	
	//根据机构ID查询供应商列表
	/*@RequestMapping("/getPersonnelsById/{id}")
	@ResponseBody
	public ResultDTO getPersonnelsById(@PathVariable(value="id") String id) throws Exception {
		try {
			if(id!=null && id.trim().length()>0){
				List<Personnel> ownedSuppliers = ownedSupplierService.getPersonnelsById(id);
				if(ownedSuppliers!=null && ownedSuppliers.size()>0){
					return ResultDTO.getSuccess(ownedSuppliers);
				}else{
					return ResultDTO.getFailure(500, "该机构没有工作人员！");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}*/
}