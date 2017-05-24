package com.sdcloud.web.lar.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.entity.EventPics;
import com.sdcloud.api.envsanitation.service.EventService;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.Pager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/sanitation")
public class SanitationController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EventService eventService;
	
	@Autowired
	private OrgService orgService;
	
	@Autowired
	private LarClientUserService larClientUserService;
	
	@Value(value = "#{sysConfig.fixUrl}")
	private String fixUrl;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value = "/getEvents")
	public ResultDTO getEvents(Pager<Event> pager, Event event) throws Exception {

		if (pager == null) {
			logger.warn("bad request, due to pager is null, please check again");
			throw new RuntimeException("bad request, due to pager is null, please check again");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> qryParams = pager.getParams();
		List<Long> clientUserList = new ArrayList<Long>(); 
		try {

			if (qryParams != null && qryParams.size() > 0) {
				for (Entry<String, Object> entry : qryParams.entrySet()) {
					param.put(entry.getKey(), entry.getValue());
				}
			}
			// 格式化日期数据
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (param.containsKey("reportTimeFrom") && param.get("reportTimeFrom") != null) {
				String time = format.format(new Date(param.get("reportTimeFrom").toString()));
				param.put("reportTimeFrom", time);
			}
			if (param.containsKey("reportTimeTo") && param.get("reportTimeTo") != null) {
				String time = format.format(new Date(param.get("reportTimeTo").toString()));
				param.put("reportTimeTo", time);
			}
			if (param.containsKey("dealTimeFrom") && param.get("dealTimeFrom") != null) {
				String time = format.format(new Date(param.get("dealTimeFrom").toString()));
				param.put("dealTimeFrom", time);
			}
			if (param.containsKey("dealTimeTo") && param.get("dealTimeTo") != null) {
				String time = format.format(new Date(param.get("dealTimeTo").toString()));
				param.put("dealTimeTo", time);
			}
			
			if(param.containsKey("reportPersonName") && param.get("reportPersonName") != null){
				String name = param.get("reportPersonName").toString();
				clientUserList = larClientUserService.queryClientUserLikeName(name);
				param.put("clientUserList", clientUserList);
			}
			Long newOrgId = null;
			if (null != qryParams.get("orgId")) {
				newOrgId = Long.parseLong(qryParams.get("orgId").toString());
			}

			// 用于查询
			List<Long> orgIds = new ArrayList<>();
			
			// ------------------事件监控，获取子部门所有事件，但是不包括子公司的事件
			String isMonitor = (String) pager.getParams().get("Monitor");
			if (!StringUtils.isEmpty(isMonitor) && isMonitor.equals("true")) {
				logger.info("query child departments:{}", orgIds);
				Map<String, Object> p = pager.getParams();
				p.put("onlyDep", "true");
				List<Org> orgs = orgService.findChildOrgs(orgIds, p);
				for (Org org : orgs) {
					orgIds.add(org.getOrgId());
				}
				logger.info("Child departments:{}", orgIds);
			}
			// ------------------事件监控，获取子部门所有事件，但是不包括子公司的事件

			// 包含子机构时
			if (param != null && param.get("orgIds") != null) {
				String[] split = param.get("orgIds").toString().split(",");
				for (String str : split) {
					Long orgId = Long.valueOf(str);
					orgIds.add(orgId);
				}
			} else if (newOrgId != null) {
				orgIds.add(newOrgId);
			}
			
			if (orgIds != null && orgIds.size() > 0) {
				param.put("newOrgId", orgIds);
			}
			pager.setOrderBy("dealstatus,reportTime");
			pager.setOrder("asc,desc");

			pager = eventService.findAll(pager, param);
			List<Event> events = pager.getResult();	
			
			if (events != null && !events.isEmpty()) {
				
				Set<Long> list = new HashSet<Long>(); 
				for (Event event1 : events) {
					list.add(event1.getReportPerson());
				}
				List<LarClientUser> userList = larClientUserService.queryClientUser(list);
				Map<Long,LarClientUser> map = new HashMap<>();
				for (LarClientUser larClientUser : userList) {
					map.put(Long.valueOf(larClientUser.getCustomerId()),larClientUser);
				}
				
				for (Event event1 : events) {
					if(event1.getReportPerson()!=null){
						LarClientUser user =  map.get(event1.getReportPerson());
						if(user!=null){
							event1.setExtStr1(user.getName()==null?"":user.getName());
							event1.setExtStr2(user.getPhone());
							event1.setExtStr3(user.getAddress()==null?null:user.getAddress());
						}
					}
				}
				
				List<Event> eventsAddPicPath = new ArrayList<>();
				for (Event e : events) {
					addResourceURLToEvent(e);
					eventsAddPicPath.add(e);
				}
				pager.setResult(eventsAddPicPath);
			}
			logger.info("complete findAll,result:" + pager);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}


	/**
	 * 补全图片的URL路径
	 * 
	 * @param event
	 */
	private void addResourceURLToEvent(Event event) {

		List<EventPics> pics = event.getEventPics();
		if (pics == null || pics.isEmpty()) {
			return;
		}

		for (EventPics pic : pics) {
			String path = this.fixUrl + pic.getPicPath();
			pic.setPicPath(path);
		}

	}
	
	/**
	 * 日期处理
	 * 
	 * @param binder
	 */
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		simpleDateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, true));
	}

	/**
	 * 校验event
	 * 
	 * @param event
	 * @return
	 */
	private boolean validateEventFileds(Event event) {

		if (event == null) {
			return false;
		}
		if (event.getEventType() == null) {
			return false;
		}
		if (StringUtils.isBlank(event.getEventDes())) {
			return false;
		}
		if (StringUtils.isBlank(event.getDiscoveryAddress())) {
			return false;
		}
		if (event.getReportTime() == null) {
			return false;
		}

		return true;
	}

	
}
