package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.Event;
import com.sdcloud.api.envsanitation.service.EventService;
import com.sdcloud.framework.entity.Pager;

import junit.framework.Assert;

/**
 * 
 * @author czz
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class EventTest {
	
	Logger logger = LoggerFactory.getLogger(super.getClass());
	@Autowired
	private EventService eventService;
	
	
	@Test
	public void findAll(){
		Pager<Event> pager = new Pager<>(10);
		Map<String, Object> param = new HashMap<>();
		List<Long> orgIds = new LinkedList<>();
		orgIds.add(3070580183024905l);
		param.put("newOrgId", orgIds);
		try {
			pager = eventService.findAll(pager, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("#########"+pager.getResult().toString());
		Assert.assertNotNull(pager.getResult());
	}
	
//	@Test
	public void findById(){
		Event event = null;
		try {
			event = eventService.findEventById(8649154251437730l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(event);
	}

}
