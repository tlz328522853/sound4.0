package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.AdvertDirective;
/**
 * 
 * @author shilin 
 *
 */
public interface AdvertDirectiveDao {
	void insert(@Param("advertDirectives")List<AdvertDirective> advertDirectives);
	
	void delete(@Param("advertDirectiveIds") List<Long> advertDirectiveIds);
	
	void deleteByMacs(@Param("deviceMacs") List<String> deviceMacs);
	
	void update(AdvertDirective advertDirective);
	
	List<AdvertDirective> findAllBy(Map<String, Object> param);
	
	long getTotalCount(Map<String, Object> param);
	
	AdvertDirective getAdvertDirective(@Param("advertDirectiveId") Long advertDirectiveId);
}
