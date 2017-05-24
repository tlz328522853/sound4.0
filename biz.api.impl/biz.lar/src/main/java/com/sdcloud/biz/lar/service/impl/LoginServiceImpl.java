package com.sdcloud.biz.lar.service.impl;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.cache.redis.service.ValueOperationsService;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.UserLogin;
import com.sdcloud.api.lar.service.LoginService;
import com.sdcloud.api.lar.util.CacheKeyUtil;
import com.sdcloud.biz.lar.dao.LarClientUserDao;
import com.sdcloud.biz.lar.dao.LoginDao;
import com.sdcloud.biz.lar.util.Constant;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.exception.AppCode;

@Service
public class LoginServiceImpl implements LoginService {
	// 加盐
	private static final String YAN = "sound1$68869liuwenzhao//*-+'[]kaifa;384785*^*&%^%$%";

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LoginDao loginDao;
	@Autowired
	private LarClientUserDao larClientUserDao;
	@Autowired
	private ValueOperationsService valueOperationsService;

	public Map<String, Object> authorityUserByToken(String token) {
		// 创建验证信息的map
		Map<String, Object> authorityInfo = new HashMap<String, Object>();
		boolean timeout = false;
		if (token == null || token.length() <= 0) {
			authorityInfo.put("code", AppCode.BAD_REQUEST);
			authorityInfo.put("message", "非法请求，请重新尝试");
			return authorityInfo;
		}
		// 查询内存表
		UserLogin userLogin = loginDao.getTokenHeap(token.trim());
		if (userLogin == null) {
			// 内存表没有就查询事务表
			userLogin = loginDao.getTokenDb(token.trim());
		}
		if (userLogin == null) {
			authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
			authorityInfo.put("message", "账号已过期,请重新登录");
			return authorityInfo;
		} else {
			// 如果存在就验证token是否过期
			Date dt1 = userLogin.getCreateDate();
			Date dt2 = userLogin.getEndDate();
			Date dt3 = new Date();
			// 过期时段
			long oldinterval = Long.valueOf(userLogin.getExpireSecond());
			// 实际时段
			long nowinterval = dt3.getTime() - dt1.getTime();
			// 过期时间是否在当前时间之前
			boolean flag = dt3.before(dt2);
			if (flag) {
				// 再次验证时段是否符合
				if (nowinterval >= oldinterval) {
					// 过期
					timeout = true;
				}
				// 没过期
			} else {
				// 过期
				timeout = true;
			}
			// 如果过期就重新登陆并删除token
			if (timeout) {
				loginDao.deleteTokenDb(userLogin.getToken());
				loginDao.deleteTokenHeap(userLogin.getToken());
				authorityInfo.clear();
				authorityInfo.put("code", AppCode.BAD_REQUEST);
				authorityInfo.put("message", "账号过期，请重新登录");
				return authorityInfo;
			} else {
				// token没过期，根据用户id查询用户
				String clientUserId = userLogin.getClientUserId();
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", clientUserId);
				LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
				if(larClientUser.getEnable() == 1){
					authorityInfo.clear();
					authorityInfo.put("code", AppCode.BAD_REQUEST);
					authorityInfo.put("message", "账号禁用,请联系管理员");
					return authorityInfo;
				}
				larClientUser.setToken(userLogin.getToken());
				authorityInfo.clear();
				authorityInfo.put("code", AppCode.SUCCESS);
				authorityInfo.put("message", "登录成功");
				authorityInfo.put("user", larClientUser);
				return authorityInfo;
			}
		}

	}

	@Override
	@Transactional
	public Map<String, Object> authorityUser(UserLogin userLogin) throws Exception {
		// 创建验证信息的map
		Map<String, Object> authorityInfo = new HashMap<String, Object>();
		// 创建数据库条件的map
		Map<String, Object> params = new HashMap<String, Object>();
		// 是否有token
		boolean isToken = false;
		// 客户端请求的对象
		UserLogin token = null;
		// token是否超时
		boolean timeout = false;
		try {
			if (userLogin == null) {
				authorityInfo.put("code", AppCode.BAD_REQUEST);
				authorityInfo.put("message", "非法请求，请重新尝试");
				return authorityInfo;
			}
			if (userLogin.getToken() == null
					&& (userLogin.getAppUUID() == null || userLogin.getAppUUID().length() <= 0)) {
				authorityInfo.put("code", AppCode.BAD_REQUEST);
				authorityInfo.put("message", "非客户端请求，无法通过");
				return authorityInfo;
			}
			if ((userLogin.getToken() == null && userLogin.getUserAccount() == null)) {
				authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
				authorityInfo.put("message", "用户名或密码错误");
				return authorityInfo;
			}
			if ((userLogin.getToken() == null && userLogin.getPassWord() == null)) {
				authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
				authorityInfo.put("message", "用户名或密码错误");
				return authorityInfo;
			}
			if (userLogin.getToken() != null && userLogin.getPassWord() != null) {
				if (userLogin.getToken().length() <= 0 && userLogin.getPassWord().length() <= 0) {
					authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
					authorityInfo.put("message", "用户名或密码错误");
					return authorityInfo;
				}
			}
			if (userLogin.getToken() != null && userLogin.getUserAccount() != null) {
				if (userLogin.getToken().length() <= 0 && userLogin.getUserAccount().length() <= 0) {
					authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
					authorityInfo.put("message", "用户名或密码错误");
					return authorityInfo;
				}
			}
			if(larClientUserDao.countByPhone(userLogin.getUserAccount())<=0){
				authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
				authorityInfo.put("message", "该手机号未注册");
				return authorityInfo;
			}
			// 判断客户端请求的对象是否携带token
			if (userLogin.getToken() != null && userLogin.getToken().trim().length() > 0) {
				// 如果有就匹配数据库token
				// 查询内存表
				token = loginDao.getTokenHeap(userLogin.getToken().trim());
				if (token == null) {
					// 内存表没有就查询事务表
					token = loginDao.getTokenDb(userLogin.getToken().trim());
					if (token == null) {
						// 没有token
						isToken = false;
					} else {
						// 有token
						isToken = true;
					}
				} else {
					isToken = true;
				}
			} else {
				isToken = false;
			}
			// 没有token,验证用户
			if (isToken == false) {
				if (userLogin.getUserAccount() != null && userLogin.getPassWord() != null) {
					params.put("phone", userLogin.getUserAccount().trim());
					params.put("passWord", userLogin.getPassWord().trim());
					// 更具用户名和密码查询,只能获得一个地址，这需要修改
					LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
					if (larClientUser != null && larClientUser.getId() != null && larClientUser.getId().length() > 0) {
						// 有用户，生成token
						String generateToken = this.generateToken(userLogin.getUserAccount(), userLogin.getPassWord(),
								userLogin.getAppUUID());
						// 存储token
						long currentTime = System.currentTimeMillis();
						long endTime = System.currentTimeMillis() + 2592000000L;
						UserLogin saveToken = new UserLogin(String.valueOf(UUIDUtil.getUUNum()),
								userLogin.getUserAccount(), userLogin.getPassWord(), userLogin.getAppUUID(),
								generateToken, new Date(currentTime), new Date(endTime), "2592000000",
								larClientUser.getId());
						// 尝试删除这个用户ID的token
						loginDao.deleteTokenDbById(larClientUser.getId());
						loginDao.deleteTokenHeapById(larClientUser.getId());
						// 插入数据库
						int insertTokenDb = loginDao.insertTokenDb(saveToken);
						if (insertTokenDb <= 0) {
							throw new IllegalArgumentException("数据库创建token失败");
						}
						int insertTokenHeap = loginDao.insertTokenHeap(saveToken);
						if (insertTokenHeap <= 0) {
							throw new IllegalArgumentException("数据库创建token失败");
						}
						authorityInfo.clear();
						larClientUser.setToken(generateToken);
						// 登录成功并生成token，返回用户信息
						authorityInfo.put("code", 200);
						authorityInfo.put("message", "登录成功");
						authorityInfo.put("user", larClientUser);
						return authorityInfo;
					} else {
						authorityInfo.clear();
						authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
						authorityInfo.put("message", "用户名或密码错误");
						return authorityInfo;
					}
				} else {
					authorityInfo.clear();
					authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
					authorityInfo.put("message", "用户名或密码错误");
					return authorityInfo;
				}
			}
			// 有token
			if (isToken) {
				// 如果存在就验证token是否过期
				Date dt1 = token.getCreateDate();
				Date dt2 = token.getEndDate();
				Date dt3 = new Date();
				// 过期时段
				long oldinterval = Long.valueOf(token.getExpireSecond());
				// 实际时段
				long nowinterval = dt3.getTime() - dt1.getTime();
				// 过期时间是否在当前时间之前
				boolean flag = dt3.before(dt2);
				if (flag) {
					// 再次验证时段是否符合
					if (nowinterval >= oldinterval) {
						// 过期
						timeout = true;
					}
					// 没过期
				} else {
					// 过期
					timeout = true;
				}
				// 如果过期就重新登陆并删除token
				if (timeout) {
					loginDao.deleteTokenDb(token.getToken());
					loginDao.deleteTokenHeap(token.getToken());
					authorityInfo.clear();
					authorityInfo.put("code", AppCode.BAD_REQUEST);
					authorityInfo.put("message", "账号过期，请重新登陆");
					return authorityInfo;
				} else {
					// token没过期，根据用户id查询用户
					String clientUserId = token.getClientUserId();
					params.clear();
					params.put("id", clientUserId);
					LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
					larClientUser.setToken(token.getToken());
					authorityInfo.clear();
					authorityInfo.put("code", AppCode.SUCCESS);
					authorityInfo.put("message", "登录成功");
					authorityInfo.put("user", larClientUser);
					return authorityInfo;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return authorityInfo;
	}

	
	
	
	
	
/**----------------------------暂时没使用，下面是把app登录 的userId和token保存到缓存服务器-----------------------------------**/	
	
	/**
	 * 1.验证登录请求基本信息
	 * 2.验证token是否失效,根据token获取clientUserId
	 * 3.验证账号密码
	 * @author jzc 2016年9月26日
	 * @param userLogin
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> authorityUser2(UserLogin userLogin) throws Exception {
		// 创建验证信息的map
		Map<String, Object> authorityInfo = new HashMap<String, Object>();
		// 创建数据库条件的map
		Map<String, Object> params = new HashMap<String, Object>();
		// 客户端用户ID
		String clientUserId = null;
		try {
			//1.验证登录请求基本信息
            if(!this.baseInfoAuthority(userLogin, authorityInfo)){
            	logger.warn("登录验证失败(基本信息)，用户："+userLogin.getUserAccount());
            	return authorityInfo;
            }
            //2.验证token是否失效,根据token获取clientUserId
            clientUserId=this.tokenAuthority(userLogin.getToken());
			if (StringUtils.isEmpty(clientUserId)) {//没有token,验证用户并返回信息
				//3.验证账号密码
				userInfoAuthority(userLogin, authorityInfo, params);
				return authorityInfo;
			}
			else{// 如果存在,使用token令牌登录
				params.clear();
				params.put("id", clientUserId);
				LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
				larClientUser.setToken(userLogin.getToken());
				authorityInfo.clear();
				authorityInfo.put("code", AppCode.SUCCESS);
				authorityInfo.put("message", "登录成功");
				authorityInfo.put("user", larClientUser);
				logger.info("令牌登录成功！用户名："+larClientUser.getPhone()+",令牌："+userLogin.getToken());
				return authorityInfo;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 验证 令牌 token
	 * @author jzc 2016年9月26日
	 * @param token
	 * @return
	 */
	public Map<String, Object> authorityUserByToken2(String token) {
		// 创建验证信息的map
		Map<String, Object> authorityInfo = new HashMap<String, Object>();
		if (StringUtils.isEmpty(token)) {
			authorityInfo.put("code", AppCode.BAD_REQUEST);
			authorityInfo.put("message", "非法请求，请重新尝试");
			logger.warn("请求的令牌为 空！token:"+token);
			return authorityInfo;
		}
		token=token.trim();
		String clientUserId=this.tokenAuthority(token);
		if(StringUtils.isEmpty(clientUserId)){
			authorityInfo.clear();
			authorityInfo.put("code", AppCode.BAD_REQUEST);
			authorityInfo.put("message", "账号过期，请重新登录");
			logger.warn("请求的令牌已失效或不存在！token:"+token);
			return authorityInfo;
		}
		else {
			// token没过期，根据用户id查询用户
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", clientUserId);
			LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
			if(larClientUser.getEnable() == 1){
				authorityInfo.clear();
				authorityInfo.put("code", AppCode.BAD_REQUEST);
				authorityInfo.put("message", "账号禁用,请联系管理员");
				logger.warn("该账号被禁用！user_account:"+larClientUser.getPhone());
				return authorityInfo;
			}
			larClientUser.setToken(token);
			authorityInfo.clear();
			authorityInfo.put("code", AppCode.SUCCESS);
			authorityInfo.put("message", "登录成功");
			authorityInfo.put("user", larClientUser);
			logger.info("令牌验证成功！用户名："+larClientUser.getPhone()+",令牌："+token);
			return authorityInfo;
		}
	}
	
	/**
	 * 验证登录用户的 基本信息
	 * @author jzc 2016年9月23日
	 * @param userLogin
	 * @param authorityInfo
	 */
	private boolean baseInfoAuthority(UserLogin userLogin,Map<String, Object> authorityInfo){
		if (userLogin == null) {
			authorityInfo.put("code", AppCode.BAD_REQUEST);
			authorityInfo.put("message", "非法请求，请重新尝试");
			return false;
		}
		if (StringUtils.isEmpty(userLogin.getAppUUID())) {
			authorityInfo.put("code", AppCode.BAD_REQUEST);
			authorityInfo.put("message", "非客户端请求，无法通过");
			return false;
		}
		if (StringUtils.isEmpty(userLogin.getUserAccount())) {
			authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
			authorityInfo.put("message", "用户名或密码错误");
			return false;
		}
		if (StringUtils.isEmpty(userLogin.getPassWord())) {
			authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
			authorityInfo.put("message", "用户名或密码错误");
			return false;
		}
		if(larClientUserDao.countByPhone(userLogin.getUserAccount())<=0){
			authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
			authorityInfo.put("message", "该手机号未注册");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证令牌（token）是否失效
	 * @author jzc 2016年9月26日
	 * @return
	 */
	private String tokenAuthority(String token){
		// 判断客户端请求的对象是否携带token
		if (StringUtils.isNotEmpty(token)) {
			//验证redis中 token情况
			return valueOperationsService.get(
					CacheKeyUtil.getO2oCommonKey(APP_LOGIN_TOKEN+token));
		}
		return null;
	}
	
	/**
	 * 验证用户的 账号 密码信息
	 * 生成token 令牌并存储
	 * key:APP_LOGIN:CUSID:{userId}=token >>>key(userId),value(token)
	 * key:APP_LOGIN:TOKEN:{token}=userId >>>key(token),value(userId)
	 * @author jzc 2016年9月26日
	 * @return
	 */
	public static String APP_LOGIN_CUSID="APP_LOGIN:CUSID:";
	public static String APP_LOGIN_TOKEN="APP_LOGIN:TOKEN:";
	private void userInfoAuthority(UserLogin userLogin,Map<String, Object> authorityInfo,
			Map<String, Object> params){
	    params.clear();
		params.put("phone", userLogin.getUserAccount().trim());
		params.put("passWord", userLogin.getPassWord().trim());
		// 根据用户名（手机号唯一）和密码查询
		LarClientUser larClientUser = larClientUserDao.selectUserByPass(params);
		if (larClientUser != null &&StringUtils.isNotEmpty(larClientUser.getId())) {
			// 有用户，生成token
			String generateToken = this.generateToken(userLogin.getUserAccount(), userLogin.getPassWord(),
					userLogin.getAppUUID());
			
			//存储token和userId到 redis
			String oldToken=valueOperationsService.get(
					CacheKeyUtil.getO2oCommonKey(APP_LOGIN_CUSID+larClientUser.getId()));
			valueOperationsService.set(CacheKeyUtil.getO2oCommonKey(APP_LOGIN_CUSID+larClientUser.getId()),
					generateToken, Constant.APP_TOKEN_EXPIRES_TIME, TimeUnit.MINUTES);
			valueOperationsService.set(CacheKeyUtil.getO2oCommonKey(APP_LOGIN_TOKEN+generateToken), 
					larClientUser.getId(), Constant.APP_TOKEN_EXPIRES_TIME, TimeUnit.MINUTES);
			
			//删除无效的token信息
			if(StringUtils.isNotEmpty(oldToken)){
				logger.debug("删除无效的-oldToken:"+oldToken);
				valueOperationsService.getOperations_delete(CacheKeyUtil.getO2oCommonKey(APP_LOGIN_TOKEN+oldToken));
			}
			
			//登录成功并生成token，返回用户信息
			authorityInfo.clear();
			larClientUser.setToken(generateToken);
			authorityInfo.put("code", 200);
			authorityInfo.put("message", "登录成功");
			authorityInfo.put("user", larClientUser);
			logger.info("账号密码登录成功！用户名："+larClientUser.getPhone()+",分配令牌："+generateToken);
		} else {
			authorityInfo.clear();
			authorityInfo.put("code", AppCode.AUTHORIZED_FAIL);
			authorityInfo.put("message", "用户名或密码错误");
			logger.warn("登录验证失败(账号密码信息)，用户："+userLogin.getUserAccount());
		}
	}
	
	// 创建token
	public String generateToken(String account, String passWord, String appUUid) {
		String token = MD5(System.currentTimeMillis() + YAN + account + passWord + appUUid);
		return token;
	}

	// MD5
	public final static String MD5(String s) {
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			return byte2hex(mdInst.digest());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 将字节数组转换成16进制字符串
	private static String byte2hex(byte[] b) {
		StringBuilder sbDes = new StringBuilder();
		String tmp = null;
		for (int i = 0; i < b.length; i++) {
			tmp = (Integer.toHexString(b[i] & 0xFF));
			if (tmp.length() == 1) {
				sbDes.append("0");
			}
			sbDes.append(tmp);
		}
		return sbDes.toString();
	}
}
