package com.sdcloud.biz.hl.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.hl.entity.User;
import com.sdcloud.biz.lar.dao.BaseDao;

/**
 * hl_user 用户基本数据
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@Repository
public interface UserDao extends BaseDao<User> {

	/**
	 * 根据userId统计数量返回
	 * @author jzc 2017年5月11日
	 * @param userId
	 * @return
	 */
	long countByUserId(@Param("userId") Integer userId);
	
	
}
