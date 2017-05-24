package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpContractInfo;

/**
 * 
 * @author dc
 */
public interface RpContractInfoDao {
	
	void insert(List<RpContractInfo> contractInfos);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpContractInfo> findAllBy(Map<String, Object> param);

	void update(RpContractInfo contractInfo);

	void delete(@Param("ids") List<Long> ids);

	void file(RpContractInfo contractInfo);

}