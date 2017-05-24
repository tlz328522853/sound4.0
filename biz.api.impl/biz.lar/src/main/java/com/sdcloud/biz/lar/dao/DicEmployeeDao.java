package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.lar.entity.DicEmployee;
import com.sdcloud.framework.entity.Pager;

public interface DicEmployeeDao {
	/**
	 * 查找部门集合下所有的员工
	 * @param orgIds 部门id集合
	 * @param pager 分页
	 * @return 员工集合
	 */
	List<DicEmployee> findByOrg(@Param("pager")Pager<DicEmployee> pager);
	/*
	 * 部门下员工总数
	 */
	long countByOrg();
}