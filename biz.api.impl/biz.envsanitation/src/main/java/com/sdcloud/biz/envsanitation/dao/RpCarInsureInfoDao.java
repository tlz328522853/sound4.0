package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpCarInsureInfo;

/**
 * 
 * @author dc
 */
public interface RpCarInsureInfoDao {
	
	void insert(List<RpCarInsureInfo> carInsureInfos);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpCarInsureInfo> findAllBy(Map<String, Object> param);

	void update(RpCarInsureInfo carInsureInfo);

	void delete(@Param("ids") List<Long> ids);

}