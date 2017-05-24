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

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.biz.core.dao.DicDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("dicService")
public class DicServiceImpl implements DicService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DicDao dicDao;
	
	@Transactional
	public Dic insert(Dic dic) throws Exception {
		logger.info("start method: long insert(Dic dic), arg dic: " + dic);
		if(dic == null){
			logger.warn("arg dic is null");
			throw new RuntimeException("arg dic is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				
				id = UUIDUtil.getUUNum();
				logger.info("create new dicId:" + id);
				dic.setDicId(id);
				
				try {
					dicDao.insert(dic);
					break;
				} catch (Exception se) {
					if(tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key dicId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return dic;
	}

	@Transactional
	public void delete(List<Long> dicIds, Map<String, Object> param) throws Exception {
		
		logger.info("start method: delete(List<Long> dicIds, Map<String, Object> param), arg dicIds: " + dicIds + ", arg param: " + param);
		if(dicIds == null || dicIds.size() == 0){
			logger.warn("arg dicIds is null or size=0");
			throw new RuntimeException("arg dicIds is null or size=0");
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("dicIds", dicIds);
			int rtnNum = dicDao.delete(map);
			// 表示删除失败
			if (rtnNum == 0) {
				logger.error("delete failure");
				throw new RuntimeException("当前登录用户不能操作平台字典");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Dic update(Dic dic, Map<String, Object> param) throws Exception {
		
		logger.info("start method: void update(Dic dic), arg dic: " + dic);
		if(dic == null || dic.getDicId() == null){
			logger.warn("arg dic is null or dic 's funcId is null");
			throw new RuntimeException("arg dic is null or dic 's funcId is null");
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String,Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("dic", dic);
			int rtnNum = dicDao.update(map);
			// 表示更新失败
			if (rtnNum == 0) {
				logger.error("update failure");
				throw new RuntimeException("当前登录用户不能操作平台字典");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		//返回修改后的记录
		try {
			dic = findById(dic.getDicId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改成功但获取修改后的数据时发生错误，事务回滚，请检查");
		}
		logger.info("complete method, return dic: " + dic);
		return dic;
	}

	@Transactional
	public Pager<Dic> findAll(Pager<Dic> pager, Map<String, Object> param) throws Exception{
		logger.info("start method: Pager<Dic> findAll(Pager<Module> pager), arg pager: " + pager + ", arg param:" + param);
		//无分页信息执行
		if (pager == null) {
			
			pager = new Pager<Dic>();
			List<Dic> dics;
			try {
				dics = dicDao.findAll(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("查询时发生错误，请检查");
			}
			pager.setResult(dics);
			
		} else {
			
			try {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("dicId");
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc");
				}
	
				if (pager.isAutoCount()) {
					long totalCount = dicDao.getTotalCount(param);
					pager.setTotalCount(totalCount);
					logger.info("querying total count result : " + totalCount);
				}
				
				// 拷贝
				Map<String,Object> map=new HashMap<String,Object>();
				if (param != null && param.size() > 0) {
					for (Entry<String, Object> entry : param.entrySet()) {
						map.put(entry.getKey(), entry.getValue());
					}
				}
				map.put("pager", pager);
				List<Dic> dics = dicDao.findAll(map);
				pager.setResult(dics);
	
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("查询时发生错误，请检查");
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public Dic findById(Long dicId) throws Exception {
		logger.info("start method: Dic findById(Long dicId), arg dicId: " + dicId);
		if(dicId == null){
			logger.warn("arg dicId is null");
			throw new RuntimeException("arg dicId is null");
		}
		Dic dic = null;
		try {
			dic = dicDao.findById(dicId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("查询时发生错误，请检查");
		}
		logger.info("complete method, return dic: " + dic);
		return dic;
	}

	@Transactional
	public List<Dic> findByPid(Long pid, Map<String, Object> param) throws Exception{
		logger.info("start method: List<Dic> findByPid(Long pid), arg pid: " + pid);
		if(pid == null){
			logger.warn("arg pid is null");
			throw new RuntimeException("arg pid is null");
		}
		List<Dic> dics = new ArrayList<Dic>();
		
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("pid", pid);
			dics = dicDao.findByPid(map);
			if(map.get("includeChild")!=null){
				int includeChild=(int) map.get("includeChild");
				List<Long> dicIds=new ArrayList<Long>();
				if(includeChild==1){
					List<Long> parentIds=new ArrayList<Long>();
					for (Dic dic : dics) {
						parentIds.add(dic.getDicId());
					}
					getChild(dicIds, parentIds, null);
				}
				dics.addAll(findByDicIds(dicIds));
			}
			
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("查询时发生错误，请检查");
		}
		logger.info("complete method, return dics: " + dics);
		return dics;
	}

	@Override
	public List<Dic> findPlatformTenantDicByPid(Long pId, List<Long> tenantIds,
			boolean hasChild) throws Exception {
		List<Dic> dics=new ArrayList<Dic>();
		try {
			logger.info("Enter the :{} method  pId:{} tenantIds:{} hasChild:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),pId,tenantIds,hasChild);
			List<Long> pIds = new ArrayList<Long>();
			pIds.add(pId);
			List<Long> childs = dicDao.findTenantDicByPid(pIds, tenantIds);
			List<Long> dicIds=new ArrayList<Long>();
			dicIds.addAll(childs);
			if(hasChild){
				getChild(dicIds,childs,tenantIds);
			}
			childs.addAll(dicIds);
			dics=dicDao.findByIds(childs);
		} catch (Exception e) {
			logger.error("method {} execute error, pId:{} tenantIds:{} hasChild:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),pId,tenantIds,hasChild, e);
			throw e;
		}
		return dics;
	}
	private void getChild(List<Long> dicIds,List<Long> parentIds,List<Long> tenantIds){
		parentIds=dicDao.findTenantDicByPid(parentIds, tenantIds);
		if(parentIds!=null&&parentIds.size()>0){
			dicIds.addAll(parentIds);
			getChild(dicIds,parentIds,tenantIds);
		}
	}

	@Transactional
	public List<Dic> findByDicIds(List<Long> ids) throws Exception {
		List<Dic> dics = new ArrayList<Dic>();
		try {
			logger.info("Enter the method findByDicIds, ids:{}", ids);
			dics = dicDao.findByIds(ids);
		} catch (Exception e) {
			logger.error("method findByDicIds execute error, ids:{}, Exception:{}", ids, e);
			throw e;
		}
		return dics;
	}
	
}