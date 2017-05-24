package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
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
import com.sdcloud.api.lar.service.RecoveryHouseService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/recoveryHouse")
public class RecoveryHouseController {

	@Autowired
	private RecoveryHouseService recoveryHouseService;
	@Autowired
	private OrgService orgService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<RecoveryHouse> pager){
		try {
			pager = recoveryHouseService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) RecoveryHouse recoveryHouse) throws Exception{
		try {
			if(recoveryHouse != null){
				boolean b = recoveryHouseService.save(recoveryHouse);
				if(b){
					return ResultDTO.getSuccess(200,"添加成功！");
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
	public ResultDTO update(@RequestBody(required=false) RecoveryHouse recoveryHouse){
		try {
			if(recoveryHouse != null){
				boolean b = recoveryHouseService.update(recoveryHouse);
				if(b){
					return ResultDTO.getSuccess(200,"修改成功！");
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
			boolean b = recoveryHouseService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess(200,"删除成功！",null);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<RecoveryHouse> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                String idStr = map.get("orgId").toString();//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
                String[] split = idStr.split("AAA");
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode")+"");
                for(String id:split){
                     List<Org> list = orgService.findById(Long.parseLong(id), isParentNode);
                     for (Org org : list) {
     					ids.add(org.getOrgId());
     				}
                }
            }else{
            	ResultDTO.getFailure("参数错误!");
            }
            return ResultDTO.getSuccess(200, recoveryHouseService.findByOrgIds(larPager, ids));
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
				boolean b = recoveryHouseService.batchDelete(ids);
				if(b){
					return ResultDTO.getSuccess(200,"删除成功！");
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
