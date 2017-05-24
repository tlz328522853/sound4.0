package com.sdcloud.biz.hl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.User;
import com.sdcloud.api.hl.service.UserService;
import com.sdcloud.biz.hl.dao.UserDao;
import com.sdcloud.biz.lar.service.impl.BaseServiceImpl;

/**
 * hl_user 用户基本数据
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Override
	public long countByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return userDao.countByUserId(userId);
	}


}
