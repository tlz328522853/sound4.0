package com.sdcloud.web.lar.controller.app;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.AdHome;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.service.AdHomeService;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ServerPropUtil;
@RestController
@RequestMapping("/app/adHome")
public class AppAdHomeController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private AdHomeService adHomeService;
	@Autowired
	private CityService cityService;

	/**
	 * 获取首页广告
	 * 
	 * @param larPager
	 * @return
	 */
	@RequestMapping("/findByCity")
	@ResponseBody
	public ResultDTO findByCity(@RequestBody(required = false) LarPager<AdHome> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			if(!map.isEmpty()){
				larPager.setOrderBy("createDate");
				larPager.setOrder("desc");
				
				City city = cityService.selectByCityId(Long.valueOf(map.get("city") + ""));
				List<Long> ids = new ArrayList<>();
				if(null != city){
					ids.add(city.getOrg());
					larPager = adHomeService.findByOrgIds(larPager, ids);
				}
			}
			
			//构造详情url
			String serverAddr=ServerPropUtil.getInstance().getProp("httpserver")+"/app/adHome/detail/";
			for(AdHome adHome:larPager.getResult()){
				adHome.setDetailUrl(serverAddr+adHome.getId());
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取首页广告",larPager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	/**
	 * 获取首页广告
	 * 
	 * @param larPager
	 * @return
	 */
	@RequestMapping("/findByOrg")
	@ResponseBody
	public ResultDTO findByOrg(@RequestBody(required = false) LarPager<AdHome> larPager) {
		logger.info("Enter the :{} method larPager :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),larPager);
		if(larPager==null){
			logger.error("method {} execute error larPager is null", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(AppCode.BAD_REQUEST, "请求参数异常！");
		}
		try {
			Map<String, Object> map = larPager.getExtendMap();
			if(!map.isEmpty()){
				larPager.setOrderBy("createDate");
				larPager.setOrder("desc");
				
				Long orgId = Long.valueOf(map.get("orgId")+"");
				List<Long> ids = new ArrayList<>();
				if(null != orgId){
					ids.add(orgId);
					larPager = adHomeService.findByOrgIds(larPager, ids);
				}
			}
			
			//构造详情url
			String serverAddr=ServerPropUtil.getInstance().getProp("httpserver")+"/app/adHome/detail/";
			for(AdHome adHome:larPager.getResult()){
				adHome.setDetailUrl(serverAddr+adHome.getId());
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取首页广告",larPager);
		} catch (Exception e) {
			logger.error("method {} execute error, ", Thread.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	/**
	 * 获取首页广告的详情
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
			AdHome adHome = adHomeService.getById(id,null);
			sb.append(" <html>");
			sb.append(" <head>");
			sb.append(" </head>");
			sb.append(" <body>");
			sb.append(adHome==null?"":adHome.getDetail());
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
		}}
}
