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

import com.sdcloud.api.core.entity.PropertyTemplate;
import com.sdcloud.api.core.service.PropertyTemplateService;
import com.sdcloud.biz.core.dao.PropertyTemplateDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("propTplService")
public class PropertyTemplateServiceImpl implements PropertyTemplateService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PropertyTemplateDao propTplDao;
	
	@Transactional
	public PropertyTemplate insert(PropertyTemplate propTpl) throws Exception {
		logger.info("start method: long insert(PropertyTemplate propTpl), arg propTpl: " + propTpl);
		
		if(propTpl == null){
			logger.warn("arg propTpl is null");
			throw new IllegalArgumentException("arg propTpl is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new propTplId:" + id);
				propTpl.setPropTplId(id);
				
				try {
					propTplDao.insert(propTpl);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key propTplId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return propTpl;
	}

	@Transactional
	public void delete(List<Long> propTplIds) throws Exception {
		
		logger.info("start method: void delete(List<Long> propTplIds), arg propTplIds: " + propTplIds);
		
		if(propTplIds == null || propTplIds.size() == 0){
			logger.warn("arg propTplIds is null or size=0");
			throw new IllegalArgumentException("arg propTplIds is null or size=0");
		}
		try {
			propTplDao.delete(propTplIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return void");
	}

	@Transactional
	public PropertyTemplate update(PropertyTemplate propTpl) throws Exception {
		logger.info("start method: void update(PropertyTemplate propTpl), arg propTpl: " + propTpl);
		if(propTpl == null || propTpl.getPropTplId() == null){
			logger.warn("arg propTpl is null or propTpl 's funcId is null");
			throw new IllegalArgumentException("arg propTpl is null or propTpl 's funcId is null");
		}
		try {
			propTplDao.update(propTpl);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			propTpl = findById(propTpl.getPropTplId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return propTpl: " + propTpl);
		return propTpl;
	}

	@Transactional
	public Pager<PropertyTemplate> findAll(Pager<PropertyTemplate> pager, Map<String, Object> param) throws Exception{
		logger.info("start method: Pager<PropertyTemplate> findAll(Pager<Module> pager), arg pager: "
				+ pager + ", arg param:" + param);
		
		//无分页信息执行
		if (pager == null) {
			pager = new Pager<PropertyTemplate>();
			List<PropertyTemplate> propTpls;
			try {
				propTpls = propTplDao.findAll(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			pager.setResult(propTpls);
			
		} else {
			
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("propTplId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
	
				if (pager.isAutoCount()) {
	
					long totalCount = propTplDao.getTotalCount(param);
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
				List<PropertyTemplate> propTpls = propTplDao.findAll(map);
				pager.setResult(propTpls);
	
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public PropertyTemplate findById(Long propTplId) throws Exception {
		logger.info("start method: PropertyTemplate findById(Long propTplId), arg propTplId: " + propTplId);
		if(propTplId == null){
			logger.warn("arg propTplId is null");
			throw new IllegalArgumentException("arg propTplId is null");
		}
		PropertyTemplate propTpl = null;
		try {
			propTpl = propTplDao.findById(propTplId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return propTpl: " + propTpl);
		return propTpl;
	}

	@Override
	public List<PropertyTemplate> findByPid(Long pid) throws Exception{
		logger.info("start method: List<PropertyTemplate> findByPid(Long pid), arg pid: " + pid);
		if(pid == null){
			logger.warn("arg pid is null");
			throw new IllegalArgumentException("arg pid is null");
		}
		List<PropertyTemplate> propTpls = new ArrayList<PropertyTemplate>();
		
		try {
			propTpls = propTplDao.findByPid(pid);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return propTpls: " + propTpls);
		return propTpls;
	}	
}
