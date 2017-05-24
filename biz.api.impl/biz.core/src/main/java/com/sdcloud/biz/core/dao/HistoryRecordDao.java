package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;

import com.sdcloud.api.core.entity.HistoryRecord;

/**
 * 
 * @author gongkaihui
 *
 */
public interface HistoryRecordDao {

	List<HistoryRecord> findById(Map<String, Object> param);
	
	void insert(HistoryRecord historyRecord);
	
	long getTotalCount(Map<String,Object> params);
	
	void update(HistoryRecord historyRecord);
}
