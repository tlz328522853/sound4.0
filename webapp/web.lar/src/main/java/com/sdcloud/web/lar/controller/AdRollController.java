package com.sdcloud.web.lar.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.AdRoll;
import com.sdcloud.api.lar.service.AdRollService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ServerPropUtil;

@RestController
@RequestMapping("/api/adRoll")
public class AdRollController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AdRollService adRollService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	
	/**
	 * 获取轮播广告,提供给客户和业务员APP
	 * 轮播的个数上限5个
	 * @param larPager
	 * @return
	 */
	@RequestMapping(value="/findAll")
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) Map<String, Object> map) throws Exception{
		try {
			LarPager<AdRoll> pager = new LarPager<>(5);
			if(map == null){
				pager = adRollService.findAll(pager);
			}else{
				if(!map.isEmpty()){
		            List<Long> ids = new ArrayList<>();
	            	ids.add(Long.valueOf(map.get("orgId") + ""));
	                pager = adRollService.findByOrgIds(pager, ids);
				}
			}
			//构造详情url
			String serverAddr=ServerPropUtil.getInstance().getProp("httpserver")+"/api/adRoll/detail/";
			for(AdRoll adRoll:pager.getResult()){
				adRoll.setDetailUrl(serverAddr+adRoll.getId());
			}
			
			return ResultDTO.getSuccess(AppCode.SUCCESS,"成功获取轮播广告",pager.getResult());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}
	
	/**
	 * 获取轮播广告的详情
	 * 
	 * @param larPager
	 * @return
	 */
	@RequestMapping(value="/detail/{id}",method=RequestMethod.GET)
	@ResponseBody
	public void detail( @PathVariable Long id, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			response.reset();
			response.setContentType("text/html;charset=utf-8");
			StringBuilder sb = new StringBuilder();
			AdRoll adRoll = adRollService.getById(id,null);
			sb.append(" <html>");
			sb.append(" <head>");
			sb.append(" </head>");
			sb.append(" <body>");
			sb.append(adRoll==null?"":adRoll.getDetail());
			sb.append(" </body>");
			sb.append(" </html>");
			out = response.getWriter();
			out.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out != null){
				out.close();
				out = null;
			}
		}
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) AdRoll adRoll,HttpServletRequest request) throws Exception{
		try {
			if(adRoll != null){
				String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
				if(userId != null){
					User user = userService.findByUesr(Long.valueOf(userId));
					adRoll.setCreateUser(Long.valueOf(user.getUserId()));
					adRoll.setCreateUserName(user.getName());
				}
				boolean b = adRollService.save(adRoll);
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
	public ResultDTO update(@RequestBody(required=false) AdRoll adRoll){
		try {
			if(adRoll != null){
				boolean b = adRollService.update(adRoll);
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
			boolean b = adRollService.delete(id);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<AdRoll> larPager) {
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
                    	for(String orgIdStr:split){
                    		List<Org> list = orgService.findById(Long.parseLong(orgIdStr), true);
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
            return ResultDTO.getSuccess(200, adRollService.findByOrgIds(larPager, ids));
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
				boolean b = adRollService.batchDelete(ids);
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
