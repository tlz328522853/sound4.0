package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.RpAccidentPerson;

/**
 * 
 * @author dc
 */
public interface RpAccidentPersonDao {
	
	void insert(List<RpAccidentPerson> rpAccidentPersons);
	
	long getTotalCount(Map<String, Object> param);
	
	List<RpAccidentPerson> findAllBy(Map<String, Object> param);

	void update(RpAccidentPerson rpAccidentPerson);

	void delete(@Param("ids") List<Long> ids);

}