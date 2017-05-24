package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Project;
import com.sdcloud.api.core.service.ProjectService;
import com.sdcloud.biz.core.dao.ProjectDao;

/**
 * 
 * @author lms
 */
@Service("projectService")
public class ProjectServiceImpl implements ProjectService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProjectDao projectDao;

	@Transactional
	public Project insert(Project project) throws Exception {

		logger.info("start method: Project insert(Project project), arg project: " + project);
		if (project == null || project.getOrgId() == null) {
			logger.warn("project is null or project's orgId is null");
			throw new RuntimeException("project is null or project's orgId is null");
		}
		try {
			int num = projectDao.insert(project);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		// 返回修改后的记录
		try {
			project = this.findById(project.getOrgId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("新增成功但获取新增的数据时发生错误，事务回滚，请检查");
		}

		logger.info("complete method, return project: " + project);
		return project;
	}

	@Transactional
	public void delete(List<Long> orgIds, Map<String, Object> param) throws Exception {

		logger.info("start method: delete(List<Long> orgIds, Map<String, Object> param), arg orgIds: " + orgIds
				+ ", arg param: " + param);
		if (orgIds == null || orgIds.size() == 0) {
			logger.warn("orgIds is null or orgIds'size = 0");
			throw new RuntimeException("orgIds is null or orgIds'size = 0");
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("orgIds", orgIds);
			int rtnNum = projectDao.delete(map);
			// 表示删除失败
			if (rtnNum == 0) {
				logger.error("delete failure, check again");
				throw new RuntimeException("delete failure, check again");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Project update(Project project, Map<String, Object> param) throws Exception {

		logger.info("start method: void update(Project project), arg project: " + project);

		if (project == null || project.getOrgId() == null) {
			logger.warn("project is null or project's orgId is null");
			throw new RuntimeException("project is null or project's orgId is null");
		}

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("project", project);
			int rtnNum = projectDao.update(map);
			// 表示更新失败
			if (rtnNum == 0) {
				logger.error("update failure, check again");
				throw new RuntimeException("update failure, check again");
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		// 返回修改后的记录
		try {
			project = this.findById(project.getOrgId());
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改成功但获取修改后的数据时发生错误，事务回滚，请检查");
		}
		logger.info("complete method, return project: " + project);
		return project;
	}

	@Transactional
	public Project findById(Long orgId) throws Exception {
		logger.info("start method: Project findById(Long orgId), arg orgId: " + orgId);
		if (orgId == null) {
			logger.warn("orgId is null");
			throw new RuntimeException("orgId is null");
		}
		Project project = null;
		try {
			project = projectDao.findById(orgId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("查询时发生错误，请检查");
		}
		logger.info("complete method, return project: " + project);
		return project;
	}

	@Override
	public List<Project> findAll(Map<String, Object> param) throws Exception {

		logger.info("start method: List<Project> findAll(Map<String, Object> param), arg param: " + param);

		List<Project> projects = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			projects = projectDao.findAll(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return projects: " + projects);
		return projects;
	}

	@Override
	public List<Project> findMonitorTree(Map<String, Object> param) throws Exception {
		logger.info("start method: List<Project> findMonitorTree(Map<String, Object> param), arg param: " + param);

		List<Project> projects = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			projects = projectDao.findMonitorTree(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return projects: " + projects);
		return projects;
	}

}
