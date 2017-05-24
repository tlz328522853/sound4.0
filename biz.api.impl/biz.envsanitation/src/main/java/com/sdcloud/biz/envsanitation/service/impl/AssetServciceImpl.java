package com.sdcloud.biz.envsanitation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.AssetObject;
import com.sdcloud.api.envsanitation.service.AssetService;
import com.sdcloud.biz.envsanitation.dao.AssetDao;
import com.sdcloud.biz.envsanitation.dao.AssetObjectDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 * @param <T>
 */
public abstract class AssetServciceImpl<T extends AssetObject> implements AssetService<T> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AssetDao<T> assetDao;
	@Autowired
	private AssetObjectDao assetObjectDao;

	@Transactional
	public List<T> insert(List<T> ts) throws Exception {
		logger.info("start method: List<T> insert(List<T> ts), arg ts: " + ts);
		if (ts == null || ts.size() == 0) {
			logger.warn("ts is empty");
			throw new RuntimeException("ts is empty");
		}
		for (T t : ts) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			t.setAssetId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					// 两张表插入
					assetObjectDao.insert((List<AssetObject>) ts);
					assetDao.insert(ts);
					break;
				} catch (Exception se) {
					logger.error("err method, Exception: " + se);
					if (tryTime == 0)
						throw new RuntimeException("向数据库插入记录失败，主键重复，请重试");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key assetId: ");
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return ts: " + ts);
		return ts;
	}
	
/*	@Transactional
	public T insert(T t) throws Exception {
		logger.info("start method: long insert(T t), arg t: " + t);
		
		if (t == null) {
			logger.warn("arg t is null");
			throw new RuntimeException("arg t is null");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new assetId:" + id);
				t.setAssetId(id);
				try {
					// 两张表插入
					assetObjectDao.insert(t);
					assetDao.insert(t);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，主键重复，请重试");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key assetId:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return t;
	}*/

	@Transactional
	public void delete(List<Long> assetIds) throws Exception {
		logger.info("start method: void delete(List<Long> assetIds), arg assetIds: " + assetIds);
		if (assetIds == null || assetIds.size() == 0) {
			logger.warn("arg assetIds is null or size=0");
			throw new RuntimeException("arg assetIds is null or size=0");
		}
		try {
			// 两张表删除
			assetObjectDao.delete(assetIds);
			assetDao.delete(assetIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("删除失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(T t) throws Exception {
		logger.info("start method: void update(T t), arg t: " + t);
		if (t == null || t.getAssetId() == null) {
			logger.warn("arg t is null or t 's assetId is null");
			throw new RuntimeException("arg t is null or t 's assetId is null");
		}
		try {
			// 两张表修改
			assetObjectDao.update(t);
			assetDao.update(t);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException(e);
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<T> findBy(Pager<T> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<T> findBy(Pager<T> pager, Map<String, Object> param), arg pager: " + pager
				+ ", arg param: " + param);

		if (pager == null) {
			pager = new Pager<T>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("assetId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = assetDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			//若为导出
			Object for_exportData = param.get("for_exportData");
			if (for_exportData != null && "true".equals(for_exportData.toString())) {
				Long totalCount = pager.getTotalCount();
				if (totalCount != null) {
					pager.setPageSize(Integer.parseInt(totalCount.toString()));
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("pager", pager); // 将pager装入到map中
			List<T> ts = assetDao.findAllBy(map);
			pager.setResult(ts);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public T findById(Long assetId) throws Exception {
		logger.info("start method: T findById(Long assetId), arg assetId: " + assetId);
		if (assetId == null) {
			logger.warn("arg assetId is null");
			throw new RuntimeException("arg assetId is null");
		}
		T t = null;
		try {
			t = assetDao.findById(assetId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return t: " + t);
		return t;
	}

}
