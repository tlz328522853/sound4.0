package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.core.service.TestService;
import com.sdcloud.biz.core.dao.TestDao;

@Service("testService")
public class TestServiceImpl implements TestService {

	@Autowired
	TestDao testDao;
	
	public List<Map<String, Object>> find(long userId) {
				
		String fixCols=validateRight(userId, TestDao.fixColsFind);

		String extCols = "name";// get form database by user's extend table field
		
		return testDao.find(fixCols,extCols);
	}

	private String validateRight(long userId, String fixCols){
		return new String("pass");
	}
}
