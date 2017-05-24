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

import com.sdcloud.api.core.entity.Module;
import com.sdcloud.api.core.service.ModuleService;
import com.sdcloud.biz.core.dao.FunctionDao;
import com.sdcloud.biz.core.dao.ModuleDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("moduleService")
public class ModuleServiceImpl implements ModuleService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ModuleDao moduleDao;

	@Autowired
	private FunctionDao functionDao;

	@Transactional
	public Module insert(Module module) throws Exception {
		logger.info("start method: long insert(Module module), arg module: " + module);
		if (module == null) {
			logger.warn("arg module is null");
			throw new RuntimeException("arg module is null");
		}

		long id = -1;
		try {

			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("create new moduleId:" + id);
				module.setModuleId(id);
				try {
					// module.setUrl("home.sideMenu({moduleName:'custom',moduleId:"
					// + id + "})");
					module.setUrl("home.sideMenu({moduleName:'" + module.getUrl() + "',moduleId:" + id + "})");
					module.setIcon("img/top_tb_11.png");
					moduleDao.insert(module);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key moudle:" + id);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return id: " + id);
		return module;
	}

	@Transactional
	public void delete(List<Long> moduleIds) throws Exception {
		logger.info("start method: void delete(List<Long> moduleIds), arg moduleIds: " + moduleIds);
		if (moduleIds == null || moduleIds.size() == 0) {
			logger.warn("arg moduleIds is null or size=0");
			throw new RuntimeException("arg moduleIds is null or size=0");
		}

		try {
			// 检查是否有关联的function，若有则不删除
			logger.info("checking if it has function");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("moduleIds", moduleIds);
			long functionCount = functionDao.getTotalCount(param);
			if (functionCount > 0) {
				logger.warn("cannot delete due to it has function");
				throw new RuntimeException("包含功能，无法删除");
			}
			try {
				moduleDao.delete(moduleIds);
			} catch (Exception e) {
				throw new RuntimeException("删除失败");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Module module) throws Exception {
		logger.info("start method: void update(Module module), arg module: " + module);
		if (module == null || module.getModuleId() == null) {
			logger.warn("arg module is null or module 's moduleId is null");
			throw new RuntimeException("arg module is null or module 's moduleId is null");
		}
		try {
			moduleDao.update(module);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<Module> findAll(Pager<Module> pager, Map<String, Object> map) throws Exception {
		logger.info("start method: Pager<User> findAll(Pager<Module> pager), arg pager: " + pager + ", arg map:" + map);
		// 无分页信息执行
		if (pager == null) {
			pager = new Pager<Module>();
			List<Module> modules;
			try {
				modules = moduleDao.findAll(map);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("获取失败");
			}
			pager.setResult(modules);
			logger.info("complete method, return modules: " + modules);
			return pager;
		}

		else {
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("moduleId"); // 默认ID排序
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc"); // 默认升序
				}

				if (pager.isAutoCount()) {

					long totalCount = moduleDao.getTotalCount(map);
					pager.setTotalCount(totalCount);

					logger.info("querying total count result : " + totalCount);
				}

				Map<String, Object> param = new HashMap<String, Object>();
				if (map != null && map.size() > 0) {
					for (Entry<String, Object> entry : map.entrySet()) {
						param.put(entry.getKey(), entry.getValue());
					}
				}
				param.put("pager", pager);
				List<Module> modules = moduleDao.findAll(param);
				pager.setResult(modules);

			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("获取失败");
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public Module findById(Long moduleId) throws Exception {
		logger.info("start method: Module findById(Long moduleId), arg moduleId: " + moduleId);
		if (moduleId == null) {
			logger.warn("arg moduleId is null");
			throw new RuntimeException("arg moduleId is null");
		}
		Module module = null;
		try {
			module = moduleDao.findById(moduleId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return salesPackage: " + module);
		return module;
	}

	@Override
	public Pager<Module> findModuleByAuthority(Pager<Module> pager, Map<String, Object> param) throws Exception {
		logger.info(
				"start method: Pager<User> findAll(Pager<Module> pager), arg pager: " + pager + ", arg map:" + param);

		// 无分页信息执行
		if (pager == null) {
			pager = new Pager<Module>();
			List<Module> modules;
			try {
				modules = moduleDao.findModuleByAuthority(param);
			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("获取失败");
			}
			pager.setResult(modules);
			logger.info("complete method, return modules: " + modules);
			return pager;
		} else {
			try {
				logger.info("init default pager");
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("moduleId"); // 默认ID排序
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc"); // 默认升序
				}

				if (pager.isAutoCount()) {

					long totalCount = moduleDao.getTotalCount(param);
					pager.setTotalCount(totalCount);

					logger.info("querying total count result : " + totalCount);
				}
				param.put("pager", pager);
				List<Module> modules = moduleDao.findAll(param);
				pager.setResult(modules);

			} catch (Exception e) {
				logger.error("err method, Exception: " + e);
				throw new RuntimeException("获取失败");
			}
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public List<Module> findAllModule(Map<String, Object> map) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),map);
		List<Module> moduels=new ArrayList<Module>();
		try {
			moduels=moduleDao.findAllModule(map);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),map,e);
			throw e;
		}
		return moduels;
	}

}
