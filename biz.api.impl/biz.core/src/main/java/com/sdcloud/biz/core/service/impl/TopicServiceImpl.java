package com.sdcloud.biz.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.core.entity.Topic;
import com.sdcloud.api.core.service.TopicService;
import com.sdcloud.biz.core.dao.TopicDao;
import com.sdcloud.biz.core.dao.TopicRightDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author lms
 */
@Service("topicService")
public class TopicServiceImpl implements TopicService{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TopicDao topicDao;
	
	@Autowired
	private TopicRightDao topicRightDao;
	
	@Transactional
	public Topic insert(Topic topic) throws Exception {
		logger.info("start method: long insert(Topic topic), arg topic: " + topic);
		if(topic == null){
			logger.warn("arg topic is null");
			throw new RuntimeException("arg topic is null");
		}
		// 新建功能时必须指定所属模块
		if(topic.getModuleId() == null){
			logger.warn("arg topic 's moduleId is null, illegal");
			throw new RuntimeException("arg topic 's moduleId is null, illegal");
		}
		long id = -1;
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				id = UUIDUtil.getUUNum();
				logger.info("generate id: " + id);
				topic.setTopicId(id);
				try {
					topicDao.insert(topic);
					break;
				} catch (Exception se) {
					if (se instanceof DuplicateKeyException) {
						if (tryTime != 0) {
							continue;
						}
						String msg = se.getMessage();
						int i = msg.indexOf("for key '");
						msg = msg.substring(i + 9);
						int j = msg.indexOf("'");
						msg = msg.substring(0, j);

						logger.warn("Exception DuplicateKeyException: " + se);
						throw new RuntimeException("向数据库插入记录失败，请检查，错误信息：" + msg);
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		
		logger.info("complete method, return id: " + id);
		return topic;
	}

	@Transactional
	public void delete(List<Long> topicIds) throws Exception {
		logger.info("start method: void delete(List<Long> funcIds), arg funcIds: " + topicIds);
		if(topicIds == null || topicIds.size() == 0){
			logger.warn("arg funcIds is null or size=0");
			throw new RuntimeException("arg funcIds is null or size=0");
		}
		try {
			topicDao.delete(topicIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("删除失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void update(Topic topic) throws Exception {
		logger.info("start method: void update(Topic topic), arg topic: " + topic);
		if(topic == null || topic.getTopicId() == null){
			logger.warn("arg topic is null or topic 's funcId is null");
			throw new IllegalArgumentException("arg topic is null or topic 's funcId is null");
		}
		try {
			topicDao.update(topic);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("修改失败");
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Map<Long, List<Topic>> findByModuleId(List<Long> moduleIds, Map<String,Object> params) throws Exception {
		logger.info("start method: Map<Long, List<Topic>> findByModuleId(List<Long> moduleIds, Object ... params), arg moduleIds: " + 
					moduleIds + ", arg params: " + params);
		if(moduleIds == null || moduleIds.size() == 0){
			logger.warn("arg moduleIds is null or size=0");
			throw new RuntimeException("arg moduleIds is null or size=0");
		}
		Map<Long, List<Topic>> result = new HashMap<Long, List<Topic>>();
		List<Topic> qryList = new ArrayList<Topic>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("moduleIds", moduleIds);
			if(params != null && params.size() > 0){
				for (Entry<String, Object> param : params.entrySet()) {
					map.put(param.getKey().toString(),param.getValue());
				};
			}
			qryList = topicDao.findByModuleId(map);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		
		for (Topic f : qryList) {
			List<Topic> list = result.get(f.getModuleId());
			if(list == null){
				list = new ArrayList<Topic>();
				list.add(f);
				result.put(f.getModuleId(), list);
			} else{
				list.add(f);
			}
		}
		logger.info("complete method, return result: " + result);
		return result;
	}

	public Pager<Topic> findByPid(Pager<Topic> pager, Map<String, Object> param) throws Exception{
		return null;
	}
	
	@Transactional
	public List<Topic> findByOwnerId(Long ownerId) throws Exception {
		logger.info("start method: List<Topic> findByOwnerId(Long ownerId), arg ownerId: " + ownerId);
		if(ownerId == null){
			logger.warn("arg ownerId is null");
			throw new RuntimeException("arg ownerId is null");
		}
		List<Topic> topics = new ArrayList<Topic>();
		try {
			List<Long> funcIds = topicRightDao.findTopicIds(ownerId);
			
			if(funcIds == null || funcIds.size() == 0){
				throw new IllegalArgumentException("");
			}
			
			topics = topicDao.findById(funcIds);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return topics: " + topics);
		return topics;
	}
	
	@Transactional
	public List<Long> findByOwnerId2(Long ownerId) throws Exception {
		logger.info("start method: List<Topic> findByOwnerId2(Long ownerId), arg ownerId: " + ownerId);
		if(ownerId == null){
			logger.warn("arg ownerId is null");
			throw new RuntimeException("arg ownerId is null");
		}
		
		List<Long> funcIds = new ArrayList<Long>();
		try {
			funcIds = topicRightDao.findTopicIds(ownerId);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return topics: " + funcIds);
		return funcIds;
	}

	@Transactional
	public Pager<Topic> findAll(Pager<Topic> pager, Map<String, Object> params) throws Exception{
		logger.info("start method: Pager<Topic> findPager(Pager<Topic> pager, Map<String, Object> params), "
					+ "arg pager: " + pager + ", arg params: " + params);
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (pager!=null) {
				if (StringUtils.isEmpty(pager.getOrderBy())) {
					pager.setOrderBy("funcId"); //默认ID排序
				}
				if (StringUtils.isEmpty(pager.getOrder())) {
					pager.setOrder("asc"); //默认升序
				}
				if (pager.isAutoCount()) {

					long totalCount = topicDao.getTotalCount(params);
					pager.setTotalCount(totalCount);
					logger.info("querying total count result : " + totalCount);
				}
				map.put("pager", pager);
			}else {
				pager=new Pager<Topic>();
			}

			if (params != null && params.size() > 0) {
				for (Entry<String, Object> entry : params.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			List<Topic> topics = topicDao.findAll(map);
			pager.setResult(topics);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public List<Topic> findAll() throws Exception {
		logger.info("start method: List<Topic> findAll()");
		List<Topic> topics = new ArrayList<Topic>();
		try {
			topics = topicDao.findAll(null);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return topics: " + topics);
		return topics;
	}

	@Transactional
	public Topic findById(Long funcId) throws Exception {
		logger.info("start method: Topic findById(Long funcId), arg funcId: " + funcId);
		if(funcId == null){
			logger.warn("arg funcId is null");
			throw new RuntimeException("arg funcId is null");
		}
		List<Topic> topics = null;
		try {
			topics = topicDao.findById(Arrays.asList(funcId));
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return topic: " + topics);
		return topics.get(0);
	}

	

	

	@Override
	public List<Topic> findAuthenTopic(Long moduleId) throws Exception {
		try {
			logger.info("Enter the :{} method  moduleId:{} pId:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),moduleId);

			return topicDao.findAuthenTopic(moduleId);
		} catch (Exception e) {
			logger.error("method {} execute error, moduleId:{}  pId:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(),moduleId,e);
			throw e;
		}
		
	}

		
}
