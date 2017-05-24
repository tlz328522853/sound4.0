package com.sdcloud.biz.core.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.TableField;
import com.sdcloud.api.core.service.TableFieldService;
import com.sdcloud.biz.core.dao.TableFieldDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("fieldService")
public class TableFieldServiceImpl implements TableFieldService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TableFieldDao fieldDao;
	
	@Transactional
	public TableField insert(TableField field) throws Exception {
		logger.info("start method: long insert(TableField field), arg field: " + field);
		
		if(field == null){
			logger.warn("arg field is null");
			throw new IllegalArgumentException("arg field is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new fieldId:" + id);
				field.setFieldId(id);
				
				try {
					fieldDao.insert(field);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key fieldId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return field;
	}

	@Transactional
	public void delete(List<Long> fieldIds) throws Exception {
		
		logger.info("start method: void delete(List<Long> fieldIds), arg fieldIds: " + fieldIds);
		
		if(fieldIds == null || fieldIds.size() == 0){
			logger.warn("arg fieldIds is null or size=0");
			throw new IllegalArgumentException("arg fieldIds is null or size=0");
		}
		try {
			fieldDao.delete(fieldIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return void");
	}

	@Transactional
	public TableField update(TableField field) throws Exception {
		logger.info("start method: void update(TableField field), arg field: " + field);
		if(field == null || field.getFieldId() == null){
			logger.warn("arg field is null or field 's fieldId is null");
			throw new IllegalArgumentException("arg field is null or field 's fieldId is null");
		}
		try {
			fieldDao.update(field);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			field = findById(field.getFieldId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return field: " + field);
		return field;
	}

	@Transactional
	public Pager<TableField> findAll(Pager<TableField> pager, Map<String, Object> param) {
		logger.info("start method: Pager<TableField> findAll(Pager<TableField> pager), arg pager: "
				+ pager + ", arg param:" + param);
		
		//无分页信息执行
		if (pager == null) {
			pager = new Pager<TableField>();
			List<TableField> fields;
			try {
				fields = fieldDao.findAll(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			pager.setResult(fields);
			
		} else {
			
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("fieldId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
	
				if (pager.isAutoCount()) {
	
					long totalCount = fieldDao.getTotalCount(param);
					pager.setTotalCount(totalCount);
					logger.info("querying total count result : " + totalCount);
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				if (param != null && param.size() > 0) {
					for (Entry<String, Object> entry : param.entrySet()) {
						map.put(entry.getKey(), entry.getValue());
					}
				}
				map.put("pager", pager);
				
				List<TableField> fields = fieldDao.findAll(map);
				pager.setResult(fields);
	
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public TableField findById(Long fieldId) throws Exception {
		logger.info("start method: TableField findById(Long fieldId), arg fieldId: " + fieldId);
		if(fieldId == null){
			logger.warn("arg fieldId is null");
			throw new IllegalArgumentException("arg fieldId is null");
		}
		TableField field = null;
		try {
			field = fieldDao.findById(fieldId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return field: " + field);
		return field;
	}

	@Override
	public List<TableField> findByPid(Long pid) {
		logger.info("start method: List<TableField> findByPid(Long pid), arg pid: " + pid);
		if(pid == null){
			logger.warn("arg pid is null");
			throw new IllegalArgumentException("arg pid is null");
		}
		List<TableField> fields = new ArrayList<TableField>();
		
		try {
			fields = fieldDao.findByPid(pid);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return fields: " + fields);
		return fields;
	}

	@Override
	public List<String> find(Map<String, Object> param) {
		logger.info("start method: List<String> find(Map<String, Object> param), arg param: " + param);
		if(param == null){
			logger.warn("arg param is null");
			throw new IllegalArgumentException("arg param is null");
		}
		
		List<String> strList = null;
		try {
			strList = fieldDao.find(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return strList: " + strList);
		return strList;
	}	
}
