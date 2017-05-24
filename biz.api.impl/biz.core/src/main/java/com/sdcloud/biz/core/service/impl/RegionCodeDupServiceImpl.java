package com.sdcloud.biz.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Project;
import com.sdcloud.api.core.service.RegionCodeDupService;
import com.sdcloud.biz.core.dao.ProjectDao;
import com.sdcloud.biz.core.dao.RegionCodeDupDao;

@Service("regionCodeDupService")
public class RegionCodeDupServiceImpl implements RegionCodeDupService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RegionCodeDupDao regionCodeDupDao;
	
	@Autowired
	private ProjectDao projectDao;

	@Override
	@Transactional
	public Project updateOrInsertForProject(Project project) throws Exception {
		logger.info("Project updateOrInsertForProject(Project project), arg project: " + project);

		try {
			String districtCode = project.getDistrictCode();
			Long orgId = project.getOrgId();
			if(orgId == null) {
				logger.error("orgId is null");
				throw new Exception("error arg orgId");
			}
			String initRegionIndex = "700000";
			if(districtCode == null || districtCode.length() == 0) {
				return project;
			}
			Long findForDup = projectDao.findForDup(districtCode, orgId);
			if(findForDup > 0) {
				String regionIndex = regionCodeDupDao.findOne();
				if(regionIndex == null) {
					regionCodeDupDao.insert(initRegionIndex);
					districtCode = initRegionIndex;
				}else{
					districtCode = String.valueOf(Long.parseLong(regionIndex) + 1);
					regionCodeDupDao.update(districtCode);
				}
				project.setDistrictCode(districtCode);
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new Exception(e);
		}
		logger.info("complete method, return project: " + project);
		return project;
	}
	

	

}
