package com.sdcloud.web.lar.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.entity.EventPics;
import com.sdcloud.api.envsanitation.service.EventService;
import com.sdcloud.api.lar.entity.IntegralSetting;
import com.sdcloud.api.lar.entity.LarEvent;
import com.sdcloud.api.lar.service.IntegralSettingService;
import com.sdcloud.api.lar.service.LarEventService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.FileOperationsUtils;
import com.sdcloud.web.lar.util.SendPhoneMessage;

@RestController
@RequestMapping("api/event")
public class LarEventController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EventService eventService;

	@Autowired
	private LarEventService larEventService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private IntegralSettingService integralSettingService;
	
	// 保存图片
	@RequestMapping(value = "/saveImages")
	@ResponseBody
	public ResultDTO saveImages(@RequestParam("files") List<MultipartFile> files) {
		logger.info(
				"start method: saveImages(@RequestParam(files) List<MultipartFile> files ");
		List<String> webUrls = new ArrayList<>();
		try {
			for (MultipartFile file : files) {
				webUrls.add(FileOperationsUtils.fileUpload(file));
			}
			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取图片RUL", webUrls);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
		}
	}

	/**
	 * 处理事件
	 * @param event
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/deal",consumes="application/json")
	@ResponseBody
	public ResultDTO deal(@RequestBody(required = false)Event event, HttpServletRequest request)
			throws Exception {

		logger.info(
				"start method: ResultDTO deal( RequestBody(required = false)Event event, HttpServletRequest request), arg event: "
						+ event);
		try {

			if (event.getEventId() == null || StringUtils.isBlank(event.getDealState())) {
				return ResultDTO.getFailure(501, "dealState or eventId is null");
			}

			// 数据库获取待处理的事件
			Event eventDB = eventService.findEventById(event.getEventId());
			if (eventDB == null) {
				return ResultDTO.getFailure(501, "event not found");
			}
			eventDB.setLevel(event.getLevel());
			
			if (eventDB.getDealStatus() == 1) {
				return ResultDTO.getFailure(501, "event has dealed");
			}
			Long userId = Long.parseLong(request.getAttribute("token_userId").toString());
			User user = userService.findByUesrId(userId);
			Employee employee = user.getEmployee();

			if (employee == null || employee.getEmployeeId() == null) {
				logger.error("err method, employee is null");
				return ResultDTO.getFailure(501, "this user is not bind employee");
			}

			eventDB.setDealPerson(employee.getEmployeeId());

			// 设置eventDB其他属性值
			if (event.getDealTime() != null) {
				eventDB.setDealTime(event.getDealTime());
			} else {
				eventDB.setDealTime(new Date());
			}
			eventDB.setDealStatus(1);
			eventDB.setDealState(event.getDealState());
			
			//保存积分
			IntegralSetting setting = integralSettingService.getByLevel(eventDB.getLevel());
			eventDB.setIntegral(new Long(setting.getIntegralNum()));
			
			boolean deal = larEventService.deal(new LarEvent(eventDB.getEventId(), null,eventDB.getReportPerson(),
					eventDB.getIntegral(),eventDB.getOrgId()),eventDB.getLevel());
			if(deal){//发放积分成功.
				eventService.update(eventDB);
				this.savePics(event);
				
				//发送短信
				String content = "亲，您上报的事件已经处理完毕，恭喜您获得了"+eventDB.getIntegral()+"积分奖励，期待您再次参与，为环境、无止境。";
				String sendPhoneMessage = SendPhoneMessage.sendPhoneMessage(eventDB.getReportPhone(), content);
				//System.out.println(sendPhoneMessage);
				logger.info("sendPhoneMessage phone:"+eventDB.getReportPhone()+",content:"+content+",result=====>>"
						+ sendPhoneMessage);
				return ResultDTO.getSuccess(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("err method, Exception: " + e);
			return ResultDTO.getFailure(501, e.getMessage());
		}
		logger.info("complete method, return event: " + event);
		return ResultDTO.getFailure(500, "处理失败!");
	}
	
	/**
	 * 保存图片
	 * 
	 */
	private void savePics(Event event) {
		try {
			List<EventPics> eventPics = event.getEventPics();
			if (null != eventPics && eventPics.size() > 0) {
				int i = 1;
				for (EventPics pic : eventPics) {
					pic.setPicName(event.getEventId() + "" + i);
					pic.setPicPath(pic.getPicPath().substring(pic.getPicPath().indexOf("/", 20)));//去掉IP及商品号
					pic.setPicSequence(i++);
					pic.setEventId(event.getEventId());
					pic.setPicType(1);
					eventService.insertEventPics(pic);// 保存图片对象
				}
			}
		} catch (Exception e) {
			logger.error("error upload file", e.getMessage());

		}
	}
}
