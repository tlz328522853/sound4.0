package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.RecyclingSpec;
import com.sdcloud.api.lar.service.RecyclingSpecService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * 回收物规格定义
 * @author luorongjie
 *
 */
@RestController
@RequestMapping("/api/recyclingPrice")
public class RecyclingPriceController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OrgService orgService;
	
	@Autowired
	private RecyclingSpecService recyclingSpecService;
	
	@Autowired
	private UserService userService;
	
	/**
	 *  增加
	 * @param recyclingSpec
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) RecyclingSpec recyclingSpec,HttpServletRequest request) {		
		
		logger.info("Enter the :{} method ", Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		try {
			// 获得当前登录用户
			Object userId = request.getAttribute("token_userId");
			// 查询用户
			User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
			Boolean save=false,update=false;
			Long priceId = recyclingSpec.getPriceId();
			
			if(recyclingSpec.getEnable() == 1){
				//更新价格或者保存时，先检查其对应的规格是否已打开。
				RecyclingSpec spec = recyclingSpecService.getSpecById(recyclingSpec.getId());
				if(spec.getStatus() != 2){
					return ResultDTO.getFailure("操作失败！该规格已停用。");
				}
			}
			
			if(null != priceId && priceId>0){
				logger.info("Enter the update  ");
				recyclingSpec.setUpdateUser(user.getUserId());
				recyclingSpec.setUpdateUserName(user.getName());
				update = recyclingSpecService.updatePrice(recyclingSpec);
			}else{
				logger.info("Enter the save  ");
				recyclingSpec.setUpdateUser(user.getUserId());
				recyclingSpec.setUpdateUserName(user.getName());
				recyclingSpec.setCreateUser(user.getUserId());
				recyclingSpec.setCreateUserName(user.getName());
				save = recyclingSpecService.savePrice(recyclingSpec);
			}
			
			if(update || save){
				return ResultDTO.getSuccess( null,"操作成功！");
			}
			return ResultDTO.getFailure("操作失败！");
		} catch (Exception e) {
			logger.error("method {} executeerror, recyclingSpec:{} ", Thread.currentThread().getStackTrace()[1].getMethodName(), recyclingSpec, e);
			return ResultDTO.getFailure("服务 器异常，操作失败！");
		}
	}
	
	
	/**
	 * 查询所有
	 * @param larPager
	 * @return
	 */
	@RequestMapping(value = "/findByOrgs", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByOrgs(@RequestBody(required = false)LarPager<RecyclingSpec> larPager) {		
		
		logger.info("Enter the :{} method ", Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		try {
			Map<String, Object> map = larPager.getExtendMap();
            Object orgObj = map.get("org");
            Object includeSubObj = map.get("includeSub");
            if(null ==orgObj )
            	return ResultDTO.getFailure("参数有误！");
            List<Long> ids = this.getOrgList(orgObj,includeSubObj);
            larPager.setOrderBy("p.update_date,s.update_date");
            larPager.setOrder("DESC,DESC");
			recyclingSpecService.findPriceByOrgId(larPager, ids);
			return ResultDTO.getSuccess(larPager);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("method {} executeerror, findByOrgs:{} ", Thread.currentThread().getStackTrace()[1].getMethodName(), e);
			return ResultDTO.getFailure("获取失败！");
		}
	}
	
	
	
	/**
	 * 根据物品ID和机构 ID查询价格(业务员端APP使用)
	 * @param orgMap
	 * @return
	 */
	@RequestMapping(value = "/findByGoods", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByGoods(@RequestBody(required = false)Map<String,Object> orgMap) {		
		
		logger.info("Enter the :{} method ", Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		try {
			Object orgObj = orgMap.get("org");
			Object goodsObj = orgMap.get("goods");
			if(null == orgObj && null == goodsObj){
				logger.error("method {} executeerror, findByGoods:{} ", Thread.currentThread().getStackTrace()[1].getMethodName());
				return ResultDTO.getFailure("参数有误！");
			}
			Object includeSubObj = orgMap.get("includeSub");
			List<Long> orgIds = this.getOrgList(orgObj,includeSubObj);
			LarPager<RecyclingSpec> larPager = new LarPager<>();
			larPager.setPageSize(1000000);
			larPager.getParams().put("goods_id", Long.parseLong(goodsObj.toString()));
			larPager.getExtendMap().put("enable", "1");
			
            larPager.setOrderBy("s.update_date");
            larPager.setOrder("DESC");
			recyclingSpecService.findPriceByOrgId(larPager, orgIds);
			
			return ResultDTO.getSuccess(larPager.getResult());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("method {} executeerror, findByGoods:{} ", Thread.currentThread().getStackTrace()[1].getMethodName(), e);
			return ResultDTO.getFailure("保存失败！");
		}
	}
	
	/**
	 * 
	 * @param orgObj 机构 id 多个以“AAA”拼接
	 * @param includeSubObj 是否包含子机构 
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj,Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if(null != includeSubObj){
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}
		
		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for(String orgString:orgArr){
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds ;
	}
}
