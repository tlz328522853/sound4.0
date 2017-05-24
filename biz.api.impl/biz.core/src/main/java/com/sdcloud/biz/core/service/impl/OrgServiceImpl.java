package com.sdcloud.biz.core.service.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sdcloud.biz.core.util.ExceptionUtil;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.biz.core.dao.EmployeeDao;
import com.sdcloud.biz.core.dao.OrgDao;
import com.sdcloud.biz.core.dao.ProjectDao;
import com.sdcloud.biz.core.dao.UserDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author czz
 * 
 */
@Service("orgService")
public class OrgServiceImpl implements OrgService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrgDao orgDao;
	@Autowired
	private ProjectDao projectDao;
//	@Autowired
//	private UserDao userDao;

	@Autowired
	private EmployeeDao employeeDao;
	@Value(value="#{sysConfigProperties.dbIndexName}")
	private String dbIndexName;

	@Transactional
	public Org insert(Org org) throws Exception {
		logger.info("start insert a new Organization : " + org);
		long orgId = -1;
		if (org == null) {
			logger.warn("org is null");
			throw new RuntimeException("机构信息为空");
		}
		if(0 == org.getIsSubCompany() && org.getCompanyId() == null){
			logger.warn("insert department org,  but companyId is null");
			throw new RuntimeException("部门必须要设置companyId");
		}
		if(StringUtils.isEmpty(org.getOwnerCode())){
			String code=getLastCode();
			org.setOwnerCode(code);
		}
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				orgId = UUIDUtil.getUUNum();
				logger.info("create new orgId:" + orgId);
				if(StringUtils.isEmpty(org.getOrgId())){
					org.setOrgId(orgId);
				}
				if(1 == org.getIsSubCompany()){//项目公司，直接设置公司Id
					org.setCompanyId(orgId);
				}
				try {
					orgDao.insert(org);
					break;
				} catch (Exception se) {
					ExceptionUtil.analysisException(se, dbIndexName, "同一租户下机构全称不能重复");
					logger.error("err method, Exception: " + se);
					if(tryTime == 1)
						throw se;
//						throw new RuntimeException("向数据库插入记录失败，请检查");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key orgId:" + orgId);
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err when insert a Organization : " + org, e);
			throw e;
		}
		logger.info("complete insert a new Organization : " + org);
		return org;
	}
	
	private String getLastCode(){
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("parentId",1);
		List<String> codes=orgDao.findOwnerCodeByParam(param);
		List<Integer> codeIns=new ArrayList<Integer>();
		for (String code : codes) {
			String[] spcode=code.split("ORG");
			if(spcode!=null&&spcode.length>1){
				Integer codeIn=Integer.valueOf(spcode[1]);
				if(codeIn>0&&codeIn<999){
					codeIns.add(codeIn);
				}
			}
		}
		String code = "";
		
		Integer codeR = 1;
		if(codeIns.size()>0){
			Collections.sort(codeIns);
			codeR=codeIns.get(codeIns.size()-1)+1;
		}
		
		
		
		if(codeR>999&&codeIns.size()<999){
			for (int i = 0; i < codeIns.size(); i++) {
				if(codeIns.get(i)+1<codeIns.get(i+1)){
					codeR=codeIns.get(i)+1;
					break;
				}
			}
		}
		code=String.valueOf(codeR);
		for (int i = code.length(); i < 3; i++) {
			code="0"+code;
		}
		return "ORG" + code;
		/*Set<String> newCodes = new HashSet<String>();
		for (String code : codes) {
			if (code.length() < 6) {
				continue;
			}
			newCodes.add(code.substring(3, 6));
		}
		DecimalFormat format = new DecimalFormat("000");
		
		for (int i = 1; i < 1000; i++) {
			code = format.format(i);
			if (!newCodes.contains(code)) {
				break;
			}
		}*/
		
	}

	@Transactional
	public void deleteById(long delOrgId, boolean isSubCompany) throws Exception {
		logger.info("start deleteById a Organization : " + delOrgId);
		// 用于查找部门下的用户和员工
		List<Long> orgIds = new ArrayList<Long>();

		try {
			if (isSubCompany) {
				// 检查是否是根节点
				logger.info("checking if it is root node");
				Long pOrgId = orgDao.findParent(delOrgId);
				if (pOrgId == null) {
					logger.warn("delOrgId " + delOrgId + " does not exist");
					throw new RuntimeException("该机构不存在");
				}
				if (pOrgId == 0) {
					logger.warn("delOrgId " + delOrgId
							+ " cannot be deleted due to it's root");
					throw new RuntimeException("该机构为根节点无法删除");
				}

				// 检查是否有子部门
				logger.info("checking if it has children org");
				orgIds.add(delOrgId);
				List<Long> children = new ArrayList<Long>();
				getChild(children, orgIds, 2);//查询子公司和部门
				if (children != null && children.size() > 1) {
					logger.warn("cannot delete due to it has children org");
					throw new RuntimeException("该机构包含子部门无法删除");
				}

				// 检查是否有用户
				/*logger.info("checking if it has user");
				List<User> users = userDao.findByOrg(orgIds, null);
				if (users.size() > 0) {
					logger.warn("cannot delete due to it has user");
					throw new RuntimeException("该机构包含用户无法删除");
				}*/

			}
			// 检查是否有员工
			logger.info("checking if it has employee");

			long employeeCount = 0;
			if(orgIds.size()>0){
				Map<String, Object> param = new HashMap<>();
				param.put("orgIds", orgIds);
				employeeCount = employeeDao.countByOrg(param);
			}
			
			// 删除绑定的项目公司信息
			Map<String, Object> param = new HashMap<>();
			param.put("orgIds", Arrays.asList(delOrgId));
			projectDao.delete(param);
			
			
		if (employeeCount > 0) {
				logger.warn("cannot delete due to it has employee");
				throw new RuntimeException("该机构包含员工无法删除");
			}
			orgDao.deleteById(delOrgId);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete deleteById a Organization : " + delOrgId);
	}

	@Transactional
	public Org update(Org org) throws Exception {
		logger.info("start update org : " + org);
		if (org == null) {
			logger.warn("updateOrg is null");
			throw new RuntimeException("updateOrg is null");
		}
		try {
			Map<String,Object> param1 = new HashMap<String, Object>();
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(org.getOrgId());
			param1.put("orgIds", orgIds);
			List<Org> oldOrgs = orgDao.findById(param1);
			if(null != oldOrgs && oldOrgs.size() > 0){
				Org oldOrg = oldOrgs.get(0);
				if(oldOrg.getIsSubCompany() != org.getIsSubCompany()){//机构的类别发生变化
					logger.info("orgId:{} type changed from {} to {}", org.getOrgId(), oldOrg.getIsSubCompany(), org.getIsSubCompany());
					//查询父机构
					orgIds.remove(0);
					orgIds.add(oldOrg.getParentId());
					List<Org> parentOrgs = orgDao.findById(param1);
					Org parentOrg = null;
					if(null != parentOrgs && parentOrgs.size() > 0){
						parentOrg = parentOrgs.get(0);
					}
					
					if(0 == org.getIsSubCompany()){//由项目公司修改为部门					
						
						if(null == parentOrg){
							throw new Exception("部门不可以做根节点");
						}
						//设置其子机构下的部门为其上级机构的companyId
						orgDao.setOrgAsDepart(oldOrg.getOrgId(), parentOrg.getCompanyId());
					}else if (1 == org.getIsSubCompany()){//由部门修改为项目公司
						
						if(parentOrg.getIsSubCompany() == 0)//部门下不可以创建公司
						{
							throw new Exception("不可以将部门下的部门设置成公司");
						}						
						orgDao.setOrgAsCompany(oldOrg.getOrgId(), oldOrg.getOwnerCode());
					}
				}
			}			
			orgDao.update(org);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orgIds", Arrays.asList(org.getOrgId()));
			org = orgDao.findById(param).get(0);
		} catch (Exception e) {
			
			ExceptionUtil.analysisException(e, dbIndexName, "同一租户下机构全称不能重复");
			logger.error("err method, Exception: " + e);
			throw e;
			
//			logger.error("err method, Exception: " + e);
//			throw new RuntimeException("修改失败");
		}
		logger.info("complete update org : " + org);
		return org;
	}

	@Transactional
	public List<Org> findByOwnerCode(long ownerCode) throws Exception{
		return null;
	}

	@Transactional
	public List<Org> findById(long orgId, boolean includeChild) throws Exception {
		logger.info("start findById orgId : " + orgId);
		List<Org> orgs = null;
		try {
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.add(orgId);
			if (includeChild) {
				logger.info("quering children org : ");
				// 查询子部门id集合
				getChild(orgIds, orgIds, 2);//包括公司和部门		
				logger.info("query children org result : " + orgIds);				
			}
			logger.info("quering org by ids : " + orgIds);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orgIds", orgIds);
			orgs = orgDao.findById(param);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("");
		}
		logger.info("complete findById orgId : " + orgId);
		return orgs;
	}
	
	@Transactional
	public List<Org> findAll(Map<String, Object> param) throws Exception {
		logger.info("start method: List<Org> findAll()");
		List<Org> orgs = new ArrayList<Org>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String,Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			orgs = orgDao.findAll(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("");
		}
		logger.info("complete method, return orgs: " + orgs);
		return orgs;
	}

	@Transactional
	public List<Org> findOrgByParam(Map<String, Object> param) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread()
				.getStackTrace()[1].getMethodName(), param);
		List<Org> orgs = new ArrayList<Org>();
		try {
			List<Long> orgIds = new ArrayList<Long>();
			
//			orgs = orgDao.findByParam(param);
			if (param.get("includeChild")!=null&&(boolean) param.get("includeChild")) {
				logger.info("quering children org : ");
				for (Org org : orgs) {

					// 查询子部门id集合
					String ownerCode=org.getOwnerCode();
					List<Long> idStringList = orgDao.findNewChildren(ownerCode);
					logger.info("query children org result :{} ", idStringList);
					
					param.remove("orgId");
					param.remove("parentId");
					param.remove("tenanId");
					orgIds.addAll(idStringList);
				}
				param.put("orgIds", orgIds);
			}
			orgs = orgDao.findByParam(param);
		} catch (Exception e) {
			logger.error("method {} execute error ,param:{} Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					param, e);
			throw new RuntimeException("获取机构失败");
		}
		return orgs;
	}
/*
	@Transactional
	public List<Org> findChildOrgs(List<Long> parentIds, Map<String, Object> map, boolean onlyDep) throws Exception {
		logger.info("Enter the :{} method param:{}", Thread.currentThread().getStackTrace()[1].getMethodName(),
				parentIds);
		List<Org> orgs = null;
		try {
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.addAll(parentIds);
			getChild(orgIds, parentIds, onlyDep);//包括子公司和部门
			
			Map<String, Object> param = new HashMap<String, Object>();
			if (map != null && map.size() > 0) {
				for (Entry<String, Object> entry : map.entrySet()) {
					param.put(entry.getKey(), entry.getValue());
				}
			}
			param.put("orgIds", orgIds);
			orgs = orgDao.findById(param);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), parentIds, e);
			throw new RuntimeException("获取机构失败");
		}
		return orgs;
	}*/
	
	@Transactional
	public List<Org> findChildOrgs(List<Long> parentIds, Map<String, Object> map) throws Exception {
		logger.info("Enter the :{} method param:{}, map:{}", Thread.currentThread().getStackTrace()[1].getMethodName(),
				parentIds,map);
		List<Org> orgs = null;
		try {
			List<Long> orgIds = new ArrayList<Long>();
			orgIds.addAll(parentIds);
			logger.info("parentIds:{}",parentIds);
			if(null != map.get("onlyDep") && map.get("onlyDep").equals("true"))
				getChild(orgIds, parentIds, 0);//只查询子部门
			else if(null != map.get("onlyOrg") && map.get("onlyOrg").equals("true"))
				getChild(orgIds, parentIds, 1);//只查询子公司
		    else
				getChild(orgIds, parentIds, 2);//包括子公司和部门
		
			logger.info("orgIds:{}",orgIds);
			Map<String, Object> param = new HashMap<String, Object>();
			if (map != null && map.size() > 0) {
				for (Entry<String, Object> entry : map.entrySet()) {
					param.put(entry.getKey(), entry.getValue());
				}
			}
			param.put("orgIds", orgIds);
			orgs = orgDao.findById(param);
		} catch (Exception e) {
			logger.error("method {} execute error,param:{} Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), parentIds, e);
			throw new RuntimeException("获取机构失败");
		}
		return orgs;
	}

	//onlyDep 为true时，只查询部门和子部门; 为false时，查询所有子部门和子公司
	private void getChild(List<Long> orgIds, List<Long> parentIds, Integer onlyDep) {
		if(parentIds == null || parentIds.size() == 0){
			return;
		}
		List<Map<String,Object>> results = orgDao.findChildById(parentIds);
		if (results != null && results.size() > 0) {
			List<Long> pIds = new ArrayList<Long>();
			for (Map<String,Object> item : results) {
				if(onlyDep==0 &&item.get("isSubCompany")!= null && (Integer)item.get("isSubCompany") == 1){
					continue;//不查询子公司
				}else if(onlyDep==1 &&item.get("isSubCompany")!= null && (Integer)item.get("isSubCompany") != 1){
					continue;//不查询子公司
				}
				pIds.add((Long)item.get("orgId"));
			}
			
			if(pIds.size()>0){
				orgIds.addAll(pIds);
				getChild(orgIds, pIds, onlyDep);
			}
				
		}
	}

	@Transactional
	public Map<Long,Org> findOrgMapByIds(List<Long> orgIds, boolean includeChild) throws Exception {
		logger.info("start findByIds orgId : " + orgIds);
		Map<Long,Org> result = new HashMap<Long, Org>();
		List<Org> orgs = null;
		try {
			if (includeChild) {
				logger.info("quering children org : ");
				getChild(orgIds, orgIds,2);//部门和公司都查询
				logger.info("quering org by ids : " + orgIds);
			}		
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orgIds", orgIds);
			orgs = orgDao.findById(param);
			for(Org org : orgs){
				result.put(org.getOrgId(), org);
			}
		} catch (Exception e) {
			logger.error("err when findByIds : " + orgIds, e);
			throw new RuntimeException("获取机构失败");
		}
		logger.info("complete findById orgId : " + orgIds);
		return result;
	}

	@Transactional
	public List<Org> findMonitorOrg(List<Long> orgIds) throws Exception {
		
		logger.info("start method OrgService.findMonitorOrg, orgIds:" + orgIds);
		
		List<Org> orgs = new ArrayList<Org>();		
		try{
			if(orgIds!=null&&orgIds.size()>0){
				orgs = orgDao.findMonitorOrg(orgIds);
			}
			
		}catch(Exception e){			
			logger.error("err when OrgService.findMonitorOrg: ",e);
			throw new RuntimeException("获取检测公司失败");		
		}
		
		logger.info("complete OrgService.findMonitorOrg, result: " + orgs);
		return orgs;
	}

	@Override
	public List<Long> findAuthenOrgIds(Long userId) throws Exception {
		
		List<Long> orgIds = null;
		
		logger.info("start method OrgServiceImpl.findAuthenOrgIds, userId:{}", userId);
		try{
			orgIds = orgDao.findAuthenOrgIds(userId);
		    getChild(orgIds, orgIds, 2);
			
			
		}
		catch(Exception e){
			logger.info("err when OrgServiceImpl.findAuthenOrgIds",e);
			throw new RuntimeException("获取授权公司失败");
		}
		logger.info("complete method OrgServiceImpl.findAuthenOrgIds, orgIds:", orgIds);
		return orgIds;
	}

	@Override
	public Pager<Org> findBy(Pager<Org> pager, Map<String, Object> param)
			throws Exception {
		logger.info("start method: Pager<Org> findBy(Pager<Org> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<Org>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("createTime");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = orgDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<Org> Orgs = orgDao.findAllBy(param);

			pager.setResult(Orgs);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Override
	public Integer findTotalProjectByOrgId(Long orgId) throws Exception {
		logger.info("start method: Integer findTotalProjectByOrgId(Long orgId), arg orgId: {}", orgId);
		Integer totalProject = null;
		try {
			totalProject = orgDao.findTotalProjectByOrgId(orgId);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return totalProject: {}", totalProject);
		return totalProject;
	}


}
