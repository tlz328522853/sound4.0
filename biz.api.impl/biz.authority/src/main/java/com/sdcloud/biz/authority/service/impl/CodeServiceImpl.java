package com.sdcloud.biz.authority.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.authority.service.CodeService;
import com.sdcloud.biz.authority.dao.CodeDao;

/**
 * 
 * @author lms
 */
@Service("codeService")
public class CodeServiceImpl implements CodeService {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired//@Resource(name="fakeCodeDao")内存
	private CodeDao codeDao;
	
	
	public void addType(String urlType, String ... codes) {
		
		codeDao.addType(urlType, codes);
	}
	
	public Set<String> getType(String urlType) {
		
		return codeDao.getType(urlType);
	}
	
	public void removeType(String urlType, String code) {
		
		codeDao.removeType(urlType, code);
	}

}
