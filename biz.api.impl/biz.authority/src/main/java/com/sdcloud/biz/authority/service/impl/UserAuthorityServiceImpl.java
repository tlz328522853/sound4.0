package com.sdcloud.biz.authority.service.impl;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.authority.entity.AuthorityURl;
import com.sdcloud.api.authority.service.UserAuthorityService;
import com.sdcloud.biz.authority.dao.UserAuthorityDao;
import com.sdcloud.biz.authority.dao.UserLoginInfoDao;


/**
 * @author lihuiquan
 * 
 */

@Service("userAuthorityService")
public class UserAuthorityServiceImpl implements UserAuthorityService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired//@Resource(name="userRoleAuthorityDao")
	UserAuthorityDao userAuthorityDao;
	@Autowired//@Resource(name="userLoginInfoDao")
	UserLoginInfoDao userLoginInfoDao;
	
	@Override
	public List<AuthorityURl> findAuthorityURLByUser(long userId)
			throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		return null;
	}

	@Override
	public boolean hasAuthorityURL(String userId,String url) throws Exception {
		logger.info("Enter the :{} method userId :{} url:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,url);
		try {
			return userAuthorityDao.hasAuthorityByURL(userId, url);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} url:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,url,e);
			throw e;
		}
	}

	@Override
	public void cacheAuthorityByUser(String userId, String[] roleIds,
			String[] functionIds)throws Exception {
		logger.info("Enter the :{} method userId :{} roleIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roleIds);
		try {
			userAuthorityDao.addAuthorityFunction(userId, functionIds);
			userAuthorityDao.addAuthorityRole(userId, roleIds);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{},roleIds:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roleIds,e);
			throw e;
		}
		
	}

	@Override
	public void deleteAuthorityByUser(String userId)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			userAuthorityDao.removeAuthorityFunction(userId, null);
			userAuthorityDao.removeAuthorityRole(userId, null);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
		
	}
	@Override
	public void deleteAuthorityByUsers(List<Long> userIds)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds);
		try {
			//
			userAuthorityDao.removeAuthorityFunctionByUserIds(userIds);
			userAuthorityDao.removeAuthorityRoleByUserIds(userIds);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds,e);
			throw e;
		}
		
	}

	@Override
	public void updataAuthorityFunction(String userId, String... functionIds)throws Exception {
		logger.info("Enter the :{} method userId :{} functionIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),functionIds);
		try {
			this.userAuthorityDao.updataAuthorityFunction(userId, functionIds);
		}catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} ,functionIds:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,functionIds,e);
			throw e;
		}
	}

	@Override
	public void updataRoleFuncation(final String roleId, final String... functionIds)throws Exception {
		logger.info("Enter the :{} method roleId :{} functionIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,functionIds);
		try {
				userAuthorityDao.updataRoleFuncation(roleId,functionIds);
		} catch (Exception e) {
			logger.error("redis cache {} err method,roleId:{} ,functionIds:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,functionIds,e);
			throw e;
		}
	}

	@Override
	public void updataFuncation(String functionId, String url)throws Exception {
		logger.info("Enter the :{} method functionId :{} url:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),functionId,url);
		try {
			userAuthorityDao.updataFuncation(functionId, url);
		} catch (Exception e) {
			logger.error("redis cache {} err method,functionId:{} ,url:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),functionId,url,e);
			throw e;
		}
	}

	@Override
	public void cacheFuncation(final Map<String, String> functionId_url)
			throws Exception {
		logger.info("Enter the :{} method functionId_url :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),functionId_url);
		try {
			if (functionId_url != null || functionId_url.size() > 0) {
				for (Entry function : functionId_url.entrySet()) {
					userAuthorityDao.addFunction(function.getKey().toString(),
							function.getValue().toString());
					userAuthorityDao.updataUrlFuncation(function.getValue()
							.toString(), function.getKey().toString());
				}
			}
		} catch (Exception e) {
			logger.error(
					"redis cache {} err method,roleId:{},functionId_url:{}  Exception:{}",
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					functionId_url, e);
			throw e;
		}

	}

	@Override
	public void updataUserRole(String userId, String... roleIds)throws Exception {
		logger.info("Enter the :{} method userId :{} roleIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roleIds);
		try {
			userAuthorityDao.updataUserRole(userId, roleIds);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} ,roleIds:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,roleIds,e);
			throw e;
		}
		
	}

	@Override
	public void removeFunction(String functionId)  throws Exception{
		logger.info("Enter the :{} method functionId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),functionId);
		try {
			userAuthorityDao.removeFunction(functionId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,functionId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),functionId,e);
			throw e;
		}
	}

	@Override
	public void removeRole(String roleId)throws Exception {
		logger.info("Enter the :{} method roleId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId);
		try {
			userAuthorityDao.removeRole(roleId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,roleId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),roleId,e);
			throw e;
		}
	}

	@Override
	public void addRoleType(Map<String, String> role_type) throws Exception {
		logger.info("Enter the :{} method role_type :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),role_type);
		try {
			userAuthorityDao.addRoleType(role_type);
		} catch (Exception e) {
			logger.error("redis cache {} err method,role_type:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),role_type,e);
			throw e;
		}
	}

	@Override
	public String getAdminiRoleType(String userId) throws Exception {
		// TODO Auto-generated method stub
		
		logger.info("Enter the :{} method userId :{} ", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return this.userAuthorityDao.getAdminiRoleType(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}

	@Override
	public List<String> getUserRole(String userId) throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,userId);
		try {
			return userAuthorityDao.getUserRole(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}

	@Override
	public void updataGroupFuncation(String groupId, String... functionIds)
			throws Exception {
		logger.info("Enter the :{} method roleId :{} functionIds:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,functionIds);
		try {
				userAuthorityDao.updataGroupFuncation(groupId,functionIds);
			} catch (Exception e) {
			logger.error("redis cache {} err method,roleId:{} ,functionIds:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),groupId,functionIds,e);
			throw e;
		}
		
	}

	@Override
	public void addRoleFuncation(String roleId, String... functionIds)
			throws Exception {
		userAuthorityDao.addRoleFuncation(roleId, functionIds);
		
	}

	@Override
	public String findFunctionName(String url) throws Exception {
		try {
			logger.info("Enter the :{} method  url:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),url);
			return userAuthorityDao.findFunctionName(url);
		} catch (Exception e) {
			logger.error("method {} execute error, url:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),url, e);
			throw e;
		}
	}

	@Override
	public void updataFuncationName(Map<String,String> url_names) throws Exception {
		try {
			logger.info("Enter the :{} method  url_names:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),url_names);
			for (Entry<String, String> urlNames : url_names.entrySet()) {
				userAuthorityDao.updataFuncationName(urlNames.getKey(), urlNames.getValue());
			}
		} catch (Exception e) {
			logger.error("method {} execute error, url_names:{}  Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),url_names, e);
			throw e;
		}
	}

	@Override
	public void removeFunctionName(String url) throws Exception {
		try {
			logger.info("Enter the :{} method  url:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),url);

			// TODO Auto-generated method stub
			userAuthorityDao.removeFunctionName(url);
		} catch (Exception e) {
			logger.error("method {} execute error, url:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),url, e);
			throw e;
		}
	}

}
