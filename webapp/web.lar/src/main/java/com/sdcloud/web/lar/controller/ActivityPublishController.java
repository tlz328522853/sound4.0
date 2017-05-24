package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ActivityPublish;
import com.sdcloud.api.lar.service.ActivityPublishService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;

@RestController
@RequestMapping("/api/activityPublish")
public class ActivityPublishController {

	@Autowired
	private ActivityPublishService activityPublishService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<ActivityPublish> pager){
		try {
			pager = activityPublishService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) ActivityPublish activityPublish,HttpServletRequest request) throws Exception{
		try {
			if(activityPublish != null){
				String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
				if(userId != null){
					User user = userService.findByUesr(Long.valueOf(userId));
					activityPublish.setCreateUser(Long.valueOf(user.getUserId()));
					activityPublish.setCreateUserName(user.getName());
				}
				boolean b = activityPublishService.save(activityPublish);
				if(b){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"添加成功！",b);
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
	public ResultDTO update(@RequestBody(required=false) ActivityPublish activityPublish){
		try {
			if(activityPublish != null){
				boolean b = activityPublishService.update(activityPublish);
				if(b){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"修改成功！",b);
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
			boolean b = activityPublishService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"删除成功！",b);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ActivityPublish> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                String idStr = map.get("orgId").toString();
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
                if (null != idStr) {
                    //是父节点再去查找
                    if (isParentNode) {
                    	String[] split = idStr.split("AAA");
                    	for(String orgStr:split){
                    		List<Org> list = orgService.findById(Long.parseLong(orgStr), true);
                            for (Org org : list) {
                                ids.add(org.getOrgId());
                            }
                    	}
                    }else{
                        larPager.getParams().put("issueFirm",Long.parseLong(idStr));
                        ids=null;
                    }
                }
            }
            LarPager<ActivityPublish> pager = activityPublishService.findByOrgIds(larPager, ids);
            List<Long> userIds = new ArrayList<>();
            for (ActivityPublish ap : pager.getResult()) {
            	if(ap.getCreateUser() != null){
            		userIds.add(ap.getCreateUser());
            	}
			}
            if(userIds.size()>0){
            	Map<Long, User> users = userService.findUserMapByIds(userIds);
            	for (ActivityPublish ap : pager.getResult()) {
            		if(users != null && ap.getCreateUser() != null){
            			User user = users.get(ap.getCreateUser());
            			//后台页面要求展示为Employee的Name,如果该用户没有绑定employee则显示登录 用户的Name
            			if(null != user && null != user.getEmployee()){
            				//发布人取得是员工名字
            				ap.setCreateUserName(user.getEmployee().getName());
            			}else if(null != user){
            				ap.setCreateUserName(user.getName());
            			}
            		}
            	}
            }
            return ResultDTO.getSuccess(200, pager);
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
				boolean b = activityPublishService.batchDelete(ids);
				if(b){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"删除成功！",b);
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
