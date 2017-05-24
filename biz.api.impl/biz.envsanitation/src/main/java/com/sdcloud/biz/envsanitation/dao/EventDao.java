package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.entity.EventPics;

/**
 * 
 * @author scholar
 *
 */
public interface EventDao {
	
	/**
	 * insert one Event entity
	 * @param event
	 */
	long insert(Event event);
	
	/**
	 * delete a list of Event entity
	 * @param eventIds
	 */
	void delete(@Param("eventIds") List<Long> eventIds);
	
	/**
	 * update one Event entity
	 * @param event
	 */
	void update(Event event);
	
	
	/**
	 * find All Entity meet certain requirements 
	 * @param params
	 * @return
	 */
	List<Event>  findAllBy(Map<String,Object> params);
	
	long getTotalCount(Map<String,Object> params);
	
	Event findById(@Param("id")long id);
	
	long insertEventPics(EventPics eventPics);
	
	void deleteEventPics(List<Long> eventPicsId);
	
	void updateEventPic(EventPics eventPics);
	
	List<EventPics> findPicsByEventId(@Param("eventId")Long eventId);

	void batchInsertEventPics(@Param("eventPicsList") List<EventPics> eventPicsList);

}
