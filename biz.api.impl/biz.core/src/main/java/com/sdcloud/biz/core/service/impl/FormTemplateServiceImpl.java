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

import com.sdcloud.api.core.entity.FormTemplate;
import com.sdcloud.api.core.service.FormTemplateService;
import com.sdcloud.biz.core.dao.FormTemplateDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("formTplService")
public class FormTemplateServiceImpl implements FormTemplateService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FormTemplateDao formTplDao;
	
	private FormTemplate insertDB(FormTemplate formTpl){
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new formTplId:" + id);
				formTpl.setFormTplId(id);
				
				try {
					formTplDao.insert(formTpl);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key formTplId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		return formTpl;
	}
	
	@Transactional
	public FormTemplate insert(FormTemplate formTpl) throws Exception {
		logger.info("start method: long insert(FormTemplate formTpl), arg formTpl: " + formTpl);
		
		if(formTpl == null){
			logger.warn("arg formTpl is null");
			throw new IllegalArgumentException("arg formTpl is null");
		}
		/*long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new formTplId:" + id);
				formTpl.setFormTplId(id);
				
				try {
					formTplDao.insert(formTpl);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key formTplId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}*/
		
		insertDB(formTpl);
		
		logger.info("complete method, return formTpl: " + formTpl);
		return formTpl;
	}

	@Transactional
	public void delete(List<Long> formTplIds) throws Exception {
		
		logger.info("start method: void delete(List<Long> formTplIds), arg formTplIds: " + formTplIds);
		
		if(formTplIds == null || formTplIds.size() == 0){
			logger.warn("arg formTplIds is null or size=0");
			throw new IllegalArgumentException("arg formTplIds is null or size=0");
		}
		try {
			formTplDao.delete(formTplIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return void");
	}

	@Transactional
	public FormTemplate update(FormTemplate formTpl) throws Exception {
		logger.info("start method: void update(FormTemplate formTpl), arg formTpl: " + formTpl);
		if(formTpl == null || formTpl.getFormTplId() == null){
			logger.warn("arg formTpl is null or formTpl 's funcId is null");
			throw new IllegalArgumentException("arg formTpl is null or formTpl 's funcId is null");
		}
		try {
			formTplDao.update(formTpl);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			formTpl = findById(formTpl.getFormTplId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return formTpl: " + formTpl);
		return formTpl;
	}

	@Transactional
	public Pager<FormTemplate> findAll(Pager<FormTemplate> pager, Map<String, Object> param) throws Exception{
		logger.info("start method: Pager<FormTemplate> findAll(Pager<Module> pager), arg pager: "
				+ pager + ", arg param:" + param);
		
		//无分页信息执行
		if (pager == null) {
			pager = new Pager<FormTemplate>();
			List<FormTemplate> formTpls;
			try {
				formTpls = formTplDao.findAll(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
			pager.setResult(formTpls);
			
		} else {
			
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("formTplId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
	
				if (pager.isAutoCount()) {
	
					long totalCount = formTplDao.getTotalCount(param);
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
				List<FormTemplate> formTpls = formTplDao.findAll(map);
				pager.setResult(formTpls);
	
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw e;
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public FormTemplate findById(Long formTplId) throws Exception {
		logger.info("start method: FormTemplate findById(Long formTplId), arg formTplId: " + formTplId);
		if(formTplId == null){
			logger.warn("arg formTplId is null");
			throw new IllegalArgumentException("arg formTplId is null");
		}
		FormTemplate formTpl = null;
		try {
			formTpl = formTplDao.findById(formTplId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return formTpl: " + formTpl);
		return formTpl;
	}

	@Override
	public List<FormTemplate> findByPid(Long pid) throws Exception{
		logger.info("start method: List<FormTemplate> findByPid(Long pid), arg pid: " + pid);
		if(pid == null){
			logger.warn("arg pid is null");
			throw new IllegalArgumentException("arg pid is null");
		}
		List<FormTemplate> formTpls = new ArrayList<FormTemplate>();
		
		try {
			formTpls = formTplDao.findByPid(pid);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return formTpls: " + formTpls);
		return formTpls;
	}	
}
