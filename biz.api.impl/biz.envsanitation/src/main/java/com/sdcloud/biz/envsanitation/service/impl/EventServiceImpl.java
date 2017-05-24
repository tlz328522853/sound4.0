package com.sdcloud.biz.envsanitation.service.impl;

import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.entity.EventPics;
import com.sdcloud.api.envsanitation.service.EventService;
import com.sdcloud.biz.envsanitation.dao.EventDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

public class EventServiceImpl implements EventService {

	private final Logger logger = LoggerFactory.getLogger(super.getClass());

	@Autowired
	private EventDao eventDao;

	@Transactional
	public Event insert(Event event) throws Exception {
		logger.info("start method long insert(Event event) args event: " + event);
		if (event == null) {
			logger.warn("arg is null");
			throw new IllegalArgumentException("arg event is null");
		}
		long id = -1L;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			id = UUIDUtil.getUUNum();
			logger.info("create new Event id:" + id);
			event.setEventId(Long.valueOf(id));
			try {
				eventDao.insert(event);
			} catch (Exception ex) {
				do {
					if (tryTime == 1)
						throw ex;
					if (ex instanceof DuplicateKeyException)
						logger.warn("duplicate primary key Eventid" + id);
				} while (tryTime-- > 0);
			}

		} catch (Exception e) {
			logger.error("err method,Exception" + e);
			throw e;
		}
		logger.info("complete method,return id :" + id);
		return event;
	}

	@Transactional
	public void delete(List<Long> eventId) throws Exception {
		logger.info("start method: void delete(List<Long> eventIds),arg eventIds:" + eventId);
		if ((eventId == null) || (eventId.size() == 0)) {
			logger.warn("arg eventId is null or size=0");
			throw new IllegalArgumentException("arg eventId is null");
		}
		try {
			eventDao.delete(eventId);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

	@Transactional
	public Event update(Event event) throws Exception {
		logger.info("start method: Event update(Event event), arg Event:" + event);
		if ((event == null) || (event.getEventId() == null)) {
			logger.warn("arg event is null or event's eventid is null");
			throw new IllegalArgumentException("arg event is null or event's eventid is null");
		}
		try {
			eventDao.update(event);
			event = findEventById(event.getEventId());
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method, return event: " + event);
		return event;
	}
	/*
	 * @Transactional public void update(Event event) throws Exception {
	 * logger.info("start method : void update(Event event), arg Event:" +
	 * event); if ((event == null) || (event.getEventId() == null)) {
	 * logger.warn("arg event is null or event's eventid is null"); throw new
	 * IllegalArgumentException( "arg event is null or event's eventid is null"
	 * ); } try { eventDao.update(event); } catch (Exception e) { logger.error(
	 * "err method,Exception:" + e); throw e; } logger.info(
	 * "complete method,return void"); }
	 */

	@Transactional
	public Pager<Event> findAll(Pager<Event> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<Event> findAll(Pager<Event> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<Event>();
		}

		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");

			// 拷贝查询条件
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}

			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("eventId");
			}

			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = eventDao.getTotalCount(map);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result :" + totalCount);
			}
			map.put("pager", pager);

			List<Event> eventids = eventDao.findAllBy(map);
			pager.setResult(eventids);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method,return pager:" + pager);
		return pager;
	}

	@Transactional
	public Event findEventById(Long eventId) {
		logger.info("start method: Event findById(long eventId),arg  eventId:" + eventId);
		if ((eventId == null) || (eventId == 0)) {
			logger.warn("arg eventId is null or eventId=0");
			throw new IllegalArgumentException("arg eventId is null");
		}
		Event result = null;
		try {
			result = eventDao.findById(eventId);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return " + result);
		return result;
	}

	@Override
	public long insertEventPics(EventPics eventPics) throws Exception {
		logger.info("start method long insert(EventPics eventPics) args event: " + eventPics);
		if (eventPics == null) {
			logger.warn("arg is null");
			throw new IllegalArgumentException("arg event is null");
		}
		long id = -1L;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			id = UUIDUtil.getUUNum();
			logger.info("create new Event id:" + id);
			eventPics.setEventPicId(Long.valueOf(id));
			try {
				eventDao.insertEventPics(eventPics);
			} catch (Exception ex) {
				do {
					if (tryTime == 1)
						throw ex;
					if (ex instanceof DuplicateKeyException)
						logger.warn("duplicate primary key Eventid" + id);
				} while (tryTime-- > 0);
			}

		} catch (Exception e) {
			logger.error("err method,Exception" + e);
			throw e;
		}
		logger.info("complete method,return id :" + id);
		return id;
	}

	@Override
	public void deleteEventPics(List<Long> eventPicsId) throws Exception {
		logger.info("start method: void delete(List<Long> eventPicsId),arg eventPicsId:" + eventPicsId);
		if ((eventPicsId == null) || (eventPicsId.size() == 0)) {
			logger.warn("arg eventPicsId is null or size=0");
			throw new IllegalArgumentException("arg eventPicsId is null");
		}
		try {
			eventDao.deleteEventPics(eventPicsId);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");

	}

	@Override
	public void updateEventPic(EventPics eventPics) throws Exception {
		logger.info("start method : void update(EventPics eventPics), arg Event:" + eventPics);
		if ((eventPics == null) || (eventPics.getEventId() == null)) {
			logger.warn("arg event is null or event's eventid is null");
			throw new IllegalArgumentException("arg eventPics is null or event's eventPicsId is null");
		}
		try {
			eventDao.updateEventPic(eventPics);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");

	}

	@Override
	public List<EventPics> findByEventId(Long evnetId) throws Exception {

		return null;
	}

	@Override
	@Transactional
	public void batchInsertEventPics(String[] eventPics, Long eventId, Boolean isDeal) throws Exception {
		
		logger.info("start method void batchInsertEventPics(String[] eventPics, long eventId, Boolean deal) args event: " + eventPics);
		
		if (eventPics == null || eventPics.length == 0) {
			return;
		}
		
		try {
			long id = -1L;
			int picSequence = 1;
			List<EventPics> EventPicsList = new ArrayList<>();
			int tryTime = Constants.DUPLICATE_PK_MAX;
			for(String pic : eventPics) {
				
				String[] split = pic.split("\\|");
				if(split.length < 2) {
					continue;
				}
				
				id = UUIDUtil.getUUNum();
				logger.info("create new Event id:" + id);
				
				EventPics eventPic = new EventPics();
				eventPic.setEventPicId(Long.valueOf(id));
				eventPic.setEventId(eventId);
				eventPic.setPicSequence(picSequence++);
				eventPic.setPicName(split[0]);
				eventPic.setPicPath(split[1]);
				
				if (isDeal) {
					eventPic.setPicType(1);
				} else {
					eventPic.setPicType(0);
				}
				
				EventPicsList.add(eventPic);
			}
			
			try {
				eventDao.batchInsertEventPics(EventPicsList);
			} catch (Exception ex) {
				do {
					if (tryTime == 1)
						throw ex;
					if (ex instanceof DuplicateKeyException)
						logger.warn("duplicate primary key Eventid" + id);
				} while (tryTime-- > 0);
			}

		} catch (Exception e) {
			logger.error("err method,Exception" + e);
			throw e;
		}
	}

}