package com.sdcloud.web.lar.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.Personnel;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.api.lar.service.OwnedSupplierService;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/app/ownedSupplier")
public class AppOwnedSupplierController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private OwnedSupplierService ownedSupplierService;
	
	// 查询列表
	@RequestMapping("/getOwnedSuppliers")
	@ResponseBody
	public ResultDTO getSalesmans(@RequestBody(required = false) LarPager<OwnedSupplier> larPager) throws Exception {
		try {
			larPager = ownedSupplierService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	//根据机构ID查询供应商列表
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
	}
	
	//根据机构ID查询供应商列表
	@RequestMapping("/getPersonnelsById/{id}")
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
	}
	
	@RequestMapping(value = "/saveOwnedSupplier", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) OwnedSupplier ownedSupplier)
			throws Exception {
		LarPager<OwnedSupplier> result = null;
		try {
			if (ownedSupplier != null && (ownedSupplier.getId()==null || ownedSupplier.getId().length()<=0)) {
				boolean insertUserGetId = ownedSupplierService.insertSelective(ownedSupplier);
				if (insertUserGetId) {
					result = new LarPager<OwnedSupplier>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", ownedSupplier.getAreaSetting().getMechanismId());
					result.setParams(params);
					result = ownedSupplierService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "供货商添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/deleteOwnedSupplier/{id}/{mechanismId}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") String id,@PathVariable(value="mechanismId") String mechanismId) throws Exception {
		LarPager<OwnedSupplier> result = null;
		try {
			if (id != null && id.trim().length()>0 && mechanismId!=null && mechanismId.trim().length()>0) {
				boolean deleteById = ownedSupplierService.deleteById(id);
				if (deleteById) {
					result = new LarPager<OwnedSupplier>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", mechanismId);
					result.setParams(params);
					result = ownedSupplierService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "供货商删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value = "/updateOwnedSupplier", method = RequestMethod.POST)
	public ResultDTO update(@RequestBody(required = false) OwnedSupplier ownedSupplier) throws Exception {
		LarPager<OwnedSupplier> result = null;
		try {
			if (ownedSupplier != null && ownedSupplier.getId() != null && ownedSupplier.getId().trim().length() > 0) {
				boolean updateByExampleSelective = ownedSupplierService.updateByExampleSelective(ownedSupplier);
				if (updateByExampleSelective) {
					result = new LarPager<OwnedSupplier>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("mechanismId", ownedSupplier.getAreaSetting().getMechanismId());
					result.setParams(params);
					result = ownedSupplierService.selectByExample(result);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "供货商修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}