package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.UserLog;
import com.sdcloud.api.lar.service.UserLogService;
import com.sdcloud.biz.lar.dao.UserLogDao;

/**
 * lar_user_log 用户操作日志记录
 * @author jiazc
 * @date 2017-01-11
 * @version 1.0
 */
@Service
public class UserLogServiceImpl extends BaseServiceImpl<UserLog> implements UserLogService{
	
	public static int BATCH_SIZE=10;//批量操作尺寸
	public static List<UserLog> USER_LOG_POOLS=new ArrayList<>(1000);//用户日志池
	public static List<UserLog> USER_LOG_CACHES=new ArrayList<>(1000);//添加缓存块
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserLogDao userLogDao;

	@Transactional(readOnly = false)
	public Boolean save(UserLog userLog) {
		batchSaveLogs(userLog);
		return true;
	}
	/**
	 * 1.异步添加用户操作至内存集合中
	 * 2.达到临界点批量添加到数据库
	 * @author jzc 2017年1月11日
	 * @param userLog
	 */
	@Async
	private void batchSaveLogs(UserLog userLog){
		User user=null;
		try {
			user=userService.findByUesr(userLog.getUserId());
			userLog.setUserName(user.getName());
			userLog.setUserGroup(user.getGroupName());
		} catch (Exception e) {
			userLog.setUserName("未知");
			userLog.setUserGroup("未知");
			userLog.setRemark("用户异常");
		}
		USER_LOG_POOLS.add(userLog);
		if(USER_LOG_POOLS.size()>=BATCH_SIZE){//大于或等于批量尺寸
			synchronized(this) {
				if(USER_LOG_POOLS.size()>=BATCH_SIZE){//大于或等于批量尺寸
					USER_LOG_CACHES.addAll(USER_LOG_POOLS);
					try {
						userLogDao.batchSave(USER_LOG_CACHES);//批量添加
					} catch (Exception e) {
						for(UserLog log:USER_LOG_CACHES){
							try {
								userLogDao.save(log);
							} catch (Exception e2) {
								logger.warn("EXCEPTION: add userlog >>"+log.toString());
								continue;
							}
						}
					}
					logger.info("batch add user operation log("+BATCH_SIZE+")--success!");
					USER_LOG_POOLS.removeAll(USER_LOG_CACHES);
				}
			}
		}
	}
	


}
