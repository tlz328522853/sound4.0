package com.sdcloud.web.lar.controller.app;


import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.event.DocumentEvent.EventType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.entity.EventPics;
import com.sdcloud.api.envsanitation.service.EventService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarEvent;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.LarEventService;
import com.sdcloud.framework.entity.Pager;
import com.sdcloud.framework.entity.ResultDTO;
@RestController
@RequestMapping("app/event")
public class AppLarEventController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EventService eventService;
	
	@Autowired 
	private CityService cityService;
	
	@Autowired
	private LarEventService larEventService;

	@Autowired
	private DicService dicService;
	
	@Value(value = "#{sysConfig.eventPid}")
	private Long eventPid;
	
	@Value(value = "#{sysConfig.fileserver}")
	private String fileserver;
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/report", 
		    consumes="application/json")
	public ResultDTO report(@RequestBody(required = false)Event event)
			throws Exception {
		logger.info(
				"start method: ResultDTO report(Event event), arg event: "
						+ event);
		try {
			Long cityId = event.getOrgId();
			event.setOrgId(null);
			if(null !=cityId){
				City city = cityService.selectByCityId(cityId);
				event.setOrgId(city.getOrg());
			}
			//event.setReportPerson(employee.getEmployeeId());保存上报人
			if (event.getReportTime() == null) {
				event.setReportTime(new Date());
			}
			event.setEventSource("CLIENT");
			event = eventService.insert(event);
			List<EventPics> eventPicsList = new ArrayList<>();
			savePics(event);
			event.setEventPics(eventPicsList);
			logger.info("complete insert");
			//保存larEvent
			String eventTypeName = null;
			List<Dic> dics = dicService.findByPid(this.eventPid, null);//先获取事件类型名
			for(Dic dic:dics){
				if(dic.getDicId().equals(event.getEventType())){
					eventTypeName = dic.getName();
					break;
				}
			}
			LarEvent larEvent = new LarEvent(event.getEventId(),eventTypeName, event.getReportPerson(), null,event.getOrgId());
			Boolean save = larEventService.save(larEvent);
			if(save)
				return ResultDTO.getSuccess(null);
			return ResultDTO.getFailure(500, "保存失败!");
	
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			return ResultDTO.getFailure(501, e.getMessage());
		}
	}
	
	/**
	 * 保存图片
	 * 
	 */
	private void savePics(Event event) {
		try {
			List<EventPics> eventPics = event.getEventPics();
			if(null !=eventPics && eventPics.size()>0){
				int i = 1;
				for(EventPics pic :eventPics){
					pic.setPicName(event.getEventId()+""+i);
					pic.setPicPath(pic.getPicPath().substring(pic.getPicPath().indexOf("/", 20)));//去掉IP及商品号
					pic.setPicSequence(i++);
					pic.setEventId(event.getEventId());
					pic.setPicType(0);
					eventService.insertEventPics(pic);//保存图片对象
				}
			}
			logger.info("complete insert");
		} catch (Exception e) {
			logger.error("error upload file,file name:", e.getMessage());
		}
	}
	
	/**
	 * 查询事件类型
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getEventTypeList")
	public ResultDTO getEventTypeList() throws Exception {

		logger.info("start method: ResultDTO getEventTypeList()");
		List<Dic> result = null;
		try {
			result = dicService.findByPid(this.eventPid, null);
		} catch (Exception e) {
			logger.error("err when getEventTypeList", e);
			return ResultDTO.getFailure(e.getMessage());
		}
		logger.info("complete method, return result: " + result);
		return ResultDTO.getSuccess(result);
	}
	
	/**
	 * 获取上报人是该用户的事件
	 * 
	 * @param pager
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getEventList")
	public ResultDTO getEventList(@RequestBody(required = false)Pager<Event> pager, HttpServletRequest request) throws Exception {

		logger.info("start method: ResultDTO getEventList(Pager<Event> pager, HttpServletRequest request), arg pager: "
				+ pager);

		List<Event> result =  new ArrayList<>();
		List<Event> processingEvent = new ArrayList<>();

		pager.setOrderBy("reportTime");
		pager.setOrder("desc");

		try {
			// 查询条件
			Map<String, Object> param = new HashMap<>();
			param=pager.getParams();

			// 合成APP端的数据结构类型
			processingEvent = eventService.findAll(pager, param).getResult();

			result = this.convertEventToApp(processingEvent);

		} catch (Exception e) {
			logger.error("err when getEventRecordList", e);
			return ResultDTO.getFailure(501, e.getMessage());
		}
		logger.info("complete getEventList,result:" + result);
		return ResultDTO.getSuccess(result);
	}

	//合成APP端的数据结构类型
	private List<Event> convertEventToApp(List<Event> events) throws Exception {
		List<Dic> dics = dicService.findByPid(this.eventPid, null);//先获取事件类型名
		if(null !=events && events.size()>0){
			for(Event event:events){
				for(Dic dic:dics){
					if(dic.getDicId().equals(event.getEventType())){
						event.setExtStr1(dic.getName());
					}
				}
				List<EventPics> eventPics = event.getEventPics();
				convertEventPics(eventPics);
			}
		}
		return events;
	}

	//转换图片的path加上ip
	private void convertEventPics(List<EventPics> eventPics) {
		if(eventPics !=null &&eventPics.size()>0){
			for(EventPics pic:eventPics){
				if(pic.getPicPath() !=null)
					pic.setPicPath(fileserver+"/"+pic.getPicPath());
			}
		}
	}
}
