package com.sdcloud.biz.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.core.entity.HistoryRecord;
import com.sdcloud.api.core.service.HistoryRecordService;
import com.sdcloud.biz.core.dao.HistoryRecordDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author gongkaihui
 *
 */
@Service("historyRecordService")
public class HistoryRecordServiceImpl implements HistoryRecordService{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HistoryRecordDao historyRecordDao;
	
	@Override
	public Pager<HistoryRecord> findByHistory(Pager<HistoryRecord> pager,Map<String, Object> param) throws Exception {
		logger.info("start method findByHistory: "+param);
		if(pager==null){
			logger.warn("pager is empty");
			throw new RuntimeException("arg pager is empty");
		}
		//List<HistoryRecord> historyRecord=null;
		try {
			// 拷贝
			Map<String, Object> map = new HashMap<String, Object>();
			
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			if(pager.isAutoCount()) {
				long totalCount = historyRecordDao.getTotalCount(map);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result :" + totalCount);
			}
			//limit #{pager.first}, #{pager.pageSize}
			map.put("pager", pager);
			List<HistoryRecord> historyRecord=historyRecordDao.findById(map);
			pager.setResult(historyRecord);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		//logger.info("end method: "+param);
		return pager;
	}

	@Override
	public void insert(HistoryRecord historyRecord) throws Exception {
		logger.info("start method: void insert(HistoryRecord historyRecord), arg historyRecord: " + historyRecord);
		if (historyRecord == null) {
			logger.warn("historyRecord is empty");
			throw new RuntimeException("historyRecord is empty");
		}
		long recordId = UUIDUtil.getUUNum();
		historyRecord.setRecordId(recordId);
		try {
			historyRecordDao.insert(historyRecord);
		} catch (Exception ex) {
			logger.error("err method, Exception: " + ex);
			throw new RuntimeException("向数据库插入记录失败，请检查");
		}
		logger.info("complete method:" + historyRecord);
		
	}

}
