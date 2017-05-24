package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TestDao {
	public final String fixColsFind = "pass,des";
	List<Map<String, Object>> find(@Param("fixCols")Object fixCols,@Param("extCols")Object extCols);
}
