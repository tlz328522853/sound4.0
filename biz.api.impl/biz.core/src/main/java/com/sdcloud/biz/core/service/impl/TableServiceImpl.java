package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Table;
import com.sdcloud.api.core.service.TableService;
import com.sdcloud.biz.core.dao.TableDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;

/**
 * 
 * @author lms
 */
@Service("tblService")
public class TableServiceImpl implements TableService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TableDao tblDao;
	
	@Transactional
	public Table insert(Table tbl) throws Exception {
		logger.info("start method: long insert(Table tbl), arg tbl: " + tbl);
		
		if(tbl == null){
			logger.warn("arg tbl is null");
			throw new IllegalArgumentException("arg tbl is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new tblId:" + id);
				tbl.setTblId(id);
				
				try {
					tblDao.insert(tbl);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key tblId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return tbl;
	}

	@Transactional
	public void delete(List<Long> tblIds) throws Exception {
		
		logger.info("start method: void delete(List<Long> tblIds), arg tblIds: " + tblIds);
		
		if(tblIds == null || tblIds.size() == 0){
			logger.warn("arg tblIds is null or size=0");
			throw new IllegalArgumentException("arg tblIds is null or size=0");
		}
		try {
			tblDao.delete(tblIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return void");
	}

	@Transactional
	public Table update(Table tbl) throws Exception {
		logger.info("start method: void update(Table tbl), arg tbl: " + tbl);
		if(tbl == null || tbl.getTblId() == null){
			logger.warn("arg tbl is null or tbl 's funcId is null");
			throw new IllegalArgumentException("arg tbl is null or tbl 's funcId is null");
		}
		try {
			tblDao.update(tbl);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			tbl = findById(tbl.getTblId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return tbl: " + tbl);
		return tbl;
	}

	@Transactional
	public Table findById(Long tblId) throws Exception {
		logger.info("start method: Table findById(Long tblId), arg tblId: " + tblId);
		if(tblId == null){
			logger.warn("arg tblId is null");
			throw new IllegalArgumentException("arg tblId is null");
		}
		Table tbl = null;
		try {
			tbl = tblDao.findById(tblId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return tbl: " + tbl);
		return tbl;
	}

	@Override
	public List<Table> findByPid(Long pid) {
		logger.info("start method: List<Table> findByPid(Long pid), arg pid: " + pid);
		if(pid == null){
			logger.warn("arg pid is null");
			throw new IllegalArgumentException("arg pid is null");
		}
		List<Table> tbls = new ArrayList<Table>();
		
		try {
			tbls = tblDao.findByPid(pid);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return tbls: " + tbls);
		return tbls;
	}

	@Override
	public List<Table> findAll(Map<String, Object> param) {
		logger.info("start method: List<Dic> findAll(Map<String, Object> param), arg param: " + param);
		List<Table> tbls = new ArrayList<Table>();
		try {
			tbls = tblDao.findAll(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return tbls: " + tbls);
		return tbls;
	}	
}
