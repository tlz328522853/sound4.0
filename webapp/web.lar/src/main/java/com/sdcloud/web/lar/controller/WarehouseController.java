package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.RecoveryHouse;
import com.sdcloud.api.lar.entity.Warehouse;
import com.sdcloud.api.lar.service.RecoveryHouseService;
import com.sdcloud.api.lar.service.WarehouseService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;


/**
 * 
 * @author luorongjie
 *
 */
@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private OrgService orgService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<Warehouse> pager){
		try {
			pager = warehouseService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) Warehouse warehouse) throws Exception{
		try {
			if(warehouse != null){
				
				Long uuid = UUIDUtil.getUUNum();
				warehouse.setId(uuid);
				warehouse.setWarehouseNo(uuid);
				
				boolean b = warehouseService.save(warehouse);
				if(b){
					return ResultDTO.getSuccess("添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) Warehouse warehouse){
		try {
			if(warehouse != null){
				boolean b = warehouseService.update(warehouse);
				if(b){
					return ResultDTO.getSuccess("修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") Long id){
		try {
			boolean b = warehouseService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Warehouse> larPager) {
		
		
		
		 try {
	            Map<String, Object> map = larPager.getExtendMap();
	            List<Long> ids = new ArrayList<>();
	            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
	                Long id = Long.valueOf(map.get("orgId") + "");
	                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
	                if (null != id) {
	                    //是父节点再去查找
	                  
		                List<Org> list = orgService.findById(id, isParentNode);
		                for (Org org : list) {
		                    ids.add(org.getOrgId());
		                }
	                   
	                }
	            }
		
		
        /*try {
        	
        	Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			Map<Long, String> orgMap=new HashMap<Long, String>();//机构ID->name
			if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
				Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
				if (null != id) {
					List<Org> list = orgService.findById(id, isParentNode);
					for (Org org : list) {
						ids.add(org.getOrgId());
						orgMap.put(org.getOrgId(), org.getName());
					}
				}
			}*/
        	
           /* Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                Long id = Long.valueOf(map.get("orgId") + "");
                larPager.getParams().put("org",id);
            }*/
            return ResultDTO.getSuccess(200, warehouseService.findByOrgIds(larPager, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
	
	@RequestMapping(value="/batchDelete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO batchDelete(@RequestParam("ids") String ids){
		try {
			if(ids != null){
				boolean b = warehouseService.batchDelete(ids);
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
}
