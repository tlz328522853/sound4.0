package com.sdcloud.biz.authority.service.impl;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sdcloud.api.authority.service.UserLoginInfoService;
import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.biz.authority.dao.UserAuthorityDao;
import com.sdcloud.biz.authority.dao.UserLoginInfoDao;


/**
 * @author lihuiquan
 * 
 */

@Service("userLoginInfoService")
public class UserLoginInfoServiceImpl implements UserLoginInfoService {

	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	UserAuthorityDao userRoleAuthorityDao;
	@Autowired//@Resource(name="fakeUserLoginInfoDao")内存
	UserLoginInfoDao userLoginInfoDao;
	
	@Override
	public void addUserLoginInfo(String userId, Map<String, String> loginInfo)throws Exception {
		logger.info("Enter the :{} method userId :{} loginInfo:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,loginInfo);

		try {
			String ip=loginInfo.get(CacheKeyGenerator.INFO_TYPE_IP);
			String token=loginInfo.get(CacheKeyGenerator.INFO_TYPE_TOKEN);
			if(!StringUtils.isEmpty(ip)&&!StringUtils.isEmpty(token)){
				userLoginInfoDao.removeTokenByIP(ip);
				userLoginInfoDao.addTokenByIP(ip, token);
			}
			userLoginInfoDao.addUserLoginInfo(userId, loginInfo);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{} ,loginInfo:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,loginInfo,e);
			throw e;
		}
	}
	@Override
	public Map<String, Map<String, String>> getUserLoginInfo(String userId)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return userLoginInfoDao.getUserLoginInfo(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public Map<String,String> getUserLoginIP(String userId)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return userLoginInfoDao.getUserLoginIP(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public String getUserLoginTime(String userId) throws Exception{
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return userLoginInfoDao.getUserLoginTime(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public List<String> getUserLoginToken(String userId)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return userLoginInfoDao.getUserLoginToken(userId);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public void removeUserLoginInfo(String userId,String loginType)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			userLoginInfoDao.removeUserLoginInfo(userId,loginType);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public void removeUserLoginInfoByUserIds(List<Long> userIds)throws Exception {
		logger.info("Enter the :{} method userId :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds);
		try {
			userLoginInfoDao.removeUserLoginInfoByUserIds(userIds);
		} catch (Exception e) {
			logger.error("redis cache {} err method,userId:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userIds,e);
			throw e;
		}
	}
	
	@Override
	public void addTokenByIP(String ip, String token)throws Exception {
		logger.info("Enter the :{} method ip :{} token:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),ip,token);
		try {
			userLoginInfoDao.addTokenByIP(ip, token);
		} catch (Exception e) {
			logger.error("redis cache {} err method,ip:{} ,token:{}  Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),ip,token,e);
			throw e;
		}
	}
	@Override
	public String getTokenByIP(String ip)throws Exception {
		logger.info("Enter the :{} method ip :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),ip);
		try {
			return userLoginInfoDao.getTokenByIP(ip);
		} catch (Exception e) {
			logger.error("redis cache {} err method,ip:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),ip,e);
			throw e;
		}
	}
	@Override
	public void removeTokenByIP(String ip) throws Exception{
		logger.info("Enter the :{} method ip :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),ip);
		try {
			userLoginInfoDao.removeTokenByIP(ip);
		} catch (Exception e) {
			logger.error("redis cache {} err method,ip:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),ip,e);
			throw e;
		}
	}
	@Override
	public long getTenantIdByUser(String userId) throws Exception {
		try {
			return userLoginInfoDao.getTenantIdByUser(userId);
		} catch (Exception e) {
			logger.error("redis cache method {} err ,userId:{} , Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public Map<String, Map<String, String>> getUserLoginInfo(String userId,String... keys) throws Exception {
		logger.info("Enter the :{} method ip :{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),keys);
		try {
			return userLoginInfoDao.getUserLoginInfo(userId, keys);
		} catch (Exception e) {
			logger.error("redis cache {} err method,ip:{}   Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),keys,e);
			throw e;
		}
		
	}
	@Override
	public long getUserGroupIdByUser(String userId) throws Exception {
		logger.info("Enter the :{} method userId :{} loginInfo:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),userId);
		try {
			return userLoginInfoDao.getUserGroupIdByUser(userId);
		} catch (Exception e) {
			logger.error("redis cache method {} err ,userId:{} , Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),userId,e);
			throw e;
		}
	}
	@Override
	public Long getUserLoginEmployee(String userId) throws Exception {
		Long employeeId=null;
		try {
			logger.info("Enter the :{} method  userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId);
			employeeId=userLoginInfoDao.getUserLoginEmployee(userId);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId, e);
			throw e;
		}
		return employeeId;
	}
	@Override
	public Map<String, String> getUserLoginTokenMap(String userId)
			throws Exception {
		try {
			logger.info("Enter the :{} method  userId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),userId);

			return userLoginInfoDao.getUserLoginTokenMap(userId);
		} catch (Exception e) {
			logger.error("method {} execute error, userId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),userId, e);
			throw e;
		}
		
	}

}
