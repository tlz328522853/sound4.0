package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.Profit;
import com.sdcloud.api.lar.service.ProfitService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profit")
public class ProfitController {

	@Autowired
	private ProfitService profitService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<Profit> pager){
		try {
			pager = profitService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) Profit profit,HttpServletRequest request) throws Exception{
		try {
			if(profit != null){
				String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
				if(userId != null){
					User user = userService.findByUesrId(Long.valueOf(userId));
					profit.setCreateUser(user.getUserId());
					profit.setCreateUserName(user.getName());
				}
				boolean b = profitService.save(profit);
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
	public ResultDTO update(@RequestBody(required=false) Profit profit){
		try {
			if(profit != null){
				boolean b = profitService.update(profit);
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
			boolean b = profitService.delete(id);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Profit> larPager) {
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
            return ResultDTO.getSuccess(200, profitService.findByOrgIds(larPager, ids));
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
				boolean b = profitService.batchDelete(ids);
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
