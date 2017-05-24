package com.sdcloud.web.lar.controller.app;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.AdRoll;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.service.AdRollService;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ServerPropUtil;

@RestController
@RequestMapping("/app/adRoll")
public class AppAdRollController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AdRollService adRollService;
	@Autowired
	private CityService cityService;

	/**
	 * 获取轮播广告,提供给客户和业务员APP
	 * 轮播的个数上限5个
	 * @param larPager
	 * @return
	 */
	@RequestMapping(value="/findAll")
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) Map<String, Object> map) {
		try {
			LarPager<AdRoll> pager = new LarPager<>(5);
			if(map == null){
				pager = adRollService.findAll(pager);
			}else{
				if(!map.isEmpty()){
					City city = cityService.selectByCityId(Long.valueOf(map.get("city") + ""));
					List<Long> ids = new ArrayList<>();
					if(null != city){
						ids.add(city.getOrg());
						pager = adRollService.findByOrgIds(pager, ids);
					}
				}
			}
			//构造详情url
			String serverAddr=ServerPropUtil.getInstance().getProp("httpserver")+"/app/adRoll/detail/";
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
}
