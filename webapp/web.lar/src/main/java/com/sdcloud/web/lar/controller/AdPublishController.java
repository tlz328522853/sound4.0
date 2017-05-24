package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.AdCustomer;
import com.sdcloud.api.lar.entity.AdPosition;
import com.sdcloud.api.lar.entity.AdPublish;
import com.sdcloud.api.lar.service.AdCustomerService;
import com.sdcloud.api.lar.service.AdPositionService;
import com.sdcloud.api.lar.service.AdPublishService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.SendPhoneMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/adPublish")
public class AdPublishController {

	@Autowired
	private AdPublishService adPublishService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private AdCustomerService adCustomerService;
	@Autowired
	private AdPositionService adPositionService;
	@Autowired
	private DicService dicService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<AdPublish> pager) throws Exception{
		try {
			pager = adPublishService.findAll(pager);
			List<AdPublish> convert = this.convert(pager.getResult());
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) AdPublish adPublish,HttpServletRequest request) throws Exception{
		try {
			if(adPublish != null){
				AdCustomer customer = adCustomerService.getById(adPublish.getAbbrCustom(), null);
				String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
				if(userId != null){
					User user = userService.findByUesrId(Long.valueOf(userId));
					adPublish.setCreateUser(user.getUserId());
					adPublish.setCreateUserName(user.getName());
				}
				boolean b = adPublishService.save(adPublish);
				if(b){
					if(null != customer && null != customer.getPhone()){
						String message = SendPhoneMessage.sendPhoneMessage(customer.getPhone(), "上刊通知");
					}
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
	public ResultDTO update(@RequestBody(required=false) AdPublish adPublish){
		try {
			if(adPublish != null){
				boolean b = adPublishService.update(adPublish);
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
			boolean b = adPublishService.delete(id);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<AdPublish> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                Long id = Long.valueOf(map.get("orgId") + "");
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
                if (null != id) {
                    //是父节点再去查找
                    if (isParentNode) {
                        List<Org> list = orgService.findById(id, true);
                        for (Org org : list) {
                            ids.add(org.getOrgId());
                        }
                    }else{
                        larPager.getParams().put("org",id);
                        ids=null;
                    }
                }
            }
            return ResultDTO.getSuccess(200, adPublishService.findByOrgIds(larPager, ids));
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
				boolean b = adPublishService.batchDelete(ids);
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
	//转换类型
	private List<AdPublish> convert(List<AdPublish> list) throws Exception {
		List<Long> orgList = new ArrayList<>();
        List<Long> customerList = new ArrayList<>();
        List<Long> adPositionList = new ArrayList<>();
        for (AdPublish adPublish : list) {
            if(null != adPublish.getAbbrCustom()){
            	customerList.add(adPublish.getAbbrCustom());
            }
            if(null != adPublish.getAdName()){
            	adPositionList.add(adPublish.getAdName());
            }
            if (null != adPublish.getOrg()) {
                orgList.add(adPublish.getOrg());
            }
        }
        Map<Long, AdCustomer> customers = null;
        Map<Long, AdPosition> adPositions = null;
        Map<Long, Org> orgs = null;
        Map<Long, Dic> dics = new HashMap<>();
        //'广告载体' : '4663600092367423'
        List<Dic> dicList = dicService.findByPid(4663600092367423L, null);
        //'广告形式' : '4002971430598973'
        List<Dic> childList = dicService.findByPid(4002971430598973L, null);
        dicList.addAll(childList);
        for (Dic dic : dicList) {
        	dics.put(dic.getDicId(), dic);
		}
        if (customerList.size() > 0) {
        	customers = adCustomerService.findMapByIds(customerList);
        }
        if(adPositionList.size() > 0){
        	adPositions = adPositionService.findMapByIds(adPositionList);
        }
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        
        for (AdPublish adPublish : list) {
            if(null != customers && null != adPublish.getAbbrCustom()){
            	AdCustomer adCustomer = customers.get(adPublish.getAbbrCustom());
            	if(null != adCustomer){
            		adPublish.setAbbrName(adCustomer.getCustomAbbr());
            	}
            }
            if(null != adPositions && null != adPublish.getAdName()){
            	AdPosition adPosition = adPositions.get(adPublish.getAdName());
            	if(null != adPosition){
            		adPublish.setAdPositionName(adPosition.getName());
            	}
            }
            if(null != dics && null != adPublish.getVehicle()){
            	Dic dic = dics.get(adPublish.getVehicle());
            	if(null != dic){
            		adPublish.setVehicleName(dic.getName());
            	}
            }
            if(null != dics && null != adPublish.getAdShape()){
            	Dic dic = dics.get(adPublish.getAdShape());
            	if(null != dic){
            		adPublish.setAdShapeName(dic.getName());
            	}
            }
            if(null != orgs && null != adPublish.getOrg()){
            	Org org = orgs.get(adPublish.getOrg());
            	if(null != org){
            		adPublish.setOrgName(org.getName());
            	}
            }
        }
        return list;
    }
}
