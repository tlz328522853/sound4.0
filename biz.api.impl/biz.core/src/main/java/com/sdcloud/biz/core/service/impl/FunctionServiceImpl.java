package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.sdcloud.api.core.entity.Function;
import com.sdcloud.api.core.service.FunctionService;
import com.sdcloud.biz.core.dao.FunctionDao;
import com.sdcloud.biz.core.dao.FunctionRightDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("functionService")
public class FunctionServiceImpl implements FunctionService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FunctionDao functionDao;
	
	@Autowired
	private FunctionRightDao functionRightDao;
	
	@Transactional
	public Function insert(Function function) throws Exception {
		logger.info("start method: long insert(Function function), arg function: " + function);
		if(function == null){
			logger.warn("arg function is null");
			throw new RuntimeException("arg function is null");
		}
		// 新建功能时必须指定所属模块
		if(function.getModuleId() == null){
			logger.warn("arg function 's moduleId is null, illegal");
			throw new RuntimeException("arg function 's moduleId is null, illegal");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("generate id: " + id);
				function.setFuncId(id);
				try {
					functionDao.insert(function);
					break;
				} catch (Exception se) {
					if (se instanceof DuplicateKeyException) {
						if (tryTime != 0) {
							continue;
						}
						String msg = se.getMessage();
						int i = msg.indexOf("for key '");
						msg = msg.substring(i + 9);
						int j = msg.indexOf("'");
						msg = msg.substring(0, j);

						logger.warn("Exception DuplicateKeyException: " + se);
						throw new RuntimeException("向数据库插入记录失败，请检查，错误信息：" + msg);
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return function;
	}

	@Transactional
	public void delete(List<Long> funcIds) throws Exception {
		logger.info("start method: void delete(List<Long> funcIds), arg funcIds: " + funcIds);
		if(funcIds == null || funcIds.size() == 0){
			logger.warn("arg funcIds is null or size=0");
			throw new RuntimeException("arg funcIds is null or size=0");
		}
		try {
			functionDao.delete(funcIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("删除失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Function function) throws Exception {
		logger.info("start method: void update(Function function), arg function: " + function);
		if(function == null || function.getFuncId() == null){
			logger.warn("arg function is null or function 's funcId is null");
			throw new IllegalArgumentException("arg function is null or function 's funcId is null");
		}
		try {
			functionDao.update(function);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Map<Long, List<Function>> findByModuleId(List<Long> moduleIds, Map<String,Object> params) throws Exception {
		logger.info("start method: Map<Long, List<Function>> findByModuleId(List<Long> moduleIds, Object ... params), arg moduleIds: " + 
					moduleIds + ", arg params: " + params);
		if(moduleIds == null || moduleIds.size() == 0){
			logger.warn("arg moduleIds is null or size=0");
			throw new RuntimeException("arg moduleIds is null or size=0");
		}
		Map<Long, List<Function>> result = new HashMap<Long, List<Function>>();
		List<Function> qryList = new ArrayList<Function>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("moduleIds", moduleIds);
			if(params != null && params.size() > 0){
				for (Entry<String, Object> param : params.entrySet()) {
					map.put(param.getKey().toString(),param.getValue());
				};
			}
			qryList = functionDao.findByModuleId(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		
		for (Function f : qryList) {
			List<Function> list = result.get(f.getModuleId());
			if(list == null){
				list = new ArrayList<Function>();
				list.add(f);
				result.put(f.getModuleId(), list);
			} else{
				list.add(f);
			}
		}
		logger.info("complete method, return result: " + result);
		return result;
	}

	public Pager<Function> findByPid(Pager<Function> pager, Map<String, Object> param) throws Exception{
		return null;
	}
	
	@Transactional
	public List<Function> findByOwnerId(Long ownerId) throws Exception {
		logger.info("start method: List<Function> findByOwnerId(Long ownerId), arg ownerId: " + ownerId);
		if(ownerId == null){
			logger.warn("arg ownerId is null");
			throw new RuntimeException("arg ownerId is null");
		}
		List<Function> functions = new ArrayList<Function>();
		try {
			List<Long> funcIds = functionRightDao.findFuncIds(ownerId);
			
			if(funcIds == null || funcIds.size() == 0){
				throw new IllegalArgumentException("");
			}
			
			functions = functionDao.findById(funcIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return functions: " + functions);
		return functions;
	}
	
	@Transactional
	public List<Long> findByOwnerId2(Long ownerId) throws Exception {
		logger.info("start method: List<Function> findByOwnerId2(Long ownerId), arg ownerId: " + ownerId);
		if(ownerId == null){
			logger.warn("arg ownerId is null");
			throw new RuntimeException("arg ownerId is null");
		}
		
		List<Long> funcIds = new ArrayList<Long>();
		try {
			funcIds = functionRightDao.findFuncIds(ownerId);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return functions: " + funcIds);
		return funcIds;
	}

	@Transactional
	public Pager<Function> findAll(Pager<Function> pager, Map<String, Object> params) throws Exception{
		logger.info("start method: Pager<Function> findPager(Pager<Function> pager, Map<String, Object> params), "
					+ "arg pager: " + pager + ", arg params: " + params);
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (pager!=null) {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("funcId"); //默认ID排序
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc"); //默认升序
				}
				if (pager.isAutoCount()) {

					long totalCount = functionDao.getTotalCount(params);
					pager.setTotalCount(totalCount);
					logger.info("querying total count result : " + totalCount);
				}
				map.put("pager", pager);
			}else {
				pager=new Pager<Function>();
			}

			if (params != null && params.size() > 0) {
				for (Entry<String, Object> entry : params.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			List<Function> functions = functionDao.findAll(map);
			pager.setResult(functions);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public List<Function> findAll() throws Exception {
		logger.info("start method: List<Function> findAll()");
		List<Function> functions = new ArrayList<Function>();
		try {
			functions = functionDao.findAll(null);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return functions: " + functions);
		return functions;
	}

	@Transactional
	public Function findById(Long funcId) throws Exception {
		logger.info("start method: Function findById(Long funcId), arg funcId: " + funcId);
		if(funcId == null){
			logger.warn("arg funcId is null");
			throw new RuntimeException("arg funcId is null");
		}
		List<Function> functions = null;
		try {
			functions = functionDao.findById(Arrays.asList(funcId));
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return function: " + functions);
		return functions.get(0);
	}

	@Override
	public List<String> findUrlByUserId(String userId) throws Exception {
		logger.info("start method: findUrlByUserId(String userId), arg userId: " + userId);
		if(userId == null){
			logger.warn("arg userId is null");
			throw new RuntimeException("arg userId is null");
		}
		List<String> Urls = null;
		try {
			Urls = functionDao.findUrlByUserId(userId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return function: " + Urls);
		return Urls;
	}

	@Override
	public List<String> findUrl(Map<String, Object> param) throws Exception {
		logger.info("start method: findUrl(Map<String, Object> param), arg param: " + param);
		if(param==null){
			logger.warn("arg param is null");
			throw new RuntimeException("arg param is null");
		}
		List<String> Urls = null;
		try {
			Urls = functionDao.findUrls(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return function: " + Urls);
		return Urls;
	}

	@Override
	public List<Function> findAuthenFunction(Long moduleId,Long pId) throws Exception {
		try {
			logger.info("Enter the :{} method  moduleId:{} pId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),moduleId, pId);

			return functionDao.findAuthenFunction(moduleId, pId);
		} catch (Exception e) {
			logger.error("method {} execute error, moduleId:{}  pId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),moduleId, pId, e);
			throw e;
		}
		
	}	
}
