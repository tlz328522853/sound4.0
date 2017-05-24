package com.sdcloud.biz.envsanitation.service.impl;

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

import com.sdcloud.api.envsanitation.entity.RpAccidentPerson;
import com.sdcloud.api.envsanitation.service.RpAccidentPersonService;
import com.sdcloud.biz.envsanitation.dao.RpAccidentPersonDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

/**
 * 
 * @author dc
 */
@Service("rpAccidentPersonService")
public class RpAccidentPersonServiceImpl implements RpAccidentPersonService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RpAccidentPersonDao rpAccidentPersonDao;

	@Transactional
	public List<RpAccidentPerson> insert(List<RpAccidentPerson> accidentPersons) throws Exception {
		logger.info("start method: insert, arg accidentPersons: " + accidentPersons);
		if (accidentPersons == null || accidentPersons.size() == 0) {
			logger.warn("accidentPersons is empty");
			throw new RuntimeException("ts is empty");
		}
		for (RpAccidentPerson rpAccidentPerson : accidentPersons) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			rpAccidentPerson.setId(id);
		}
		int tryTime = Constants.DUPLICATE_PK_MAX;
		try {
			while (tryTime-- > 0) {
				try {
					rpAccidentPersonDao.insert(accidentPersons);
					break;
				} catch (Exception se) {
					logger.error("err method, Exception: " + se);
					if (tryTime == 0)
						throw new RuntimeException("向数据库插入记录失败，主键重复，请重试");
					if (se instanceof DuplicateKeyException) {
						logger.warn("duplicate primary key assetId: ");
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		logger.info("complete method, return ts: " + accidentPersons);
		return accidentPersons;
	}

	@Transactional
	public Pager<RpAccidentPerson> findBy(Pager<RpAccidentPerson> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<RpAccidentPerson> findBy(Pager<RpAccidentPerson> pager, Map<String, Object> param), "
				+ "arg pager: " + pager + ", arg param: " + param);
		if (pager == null) {
			pager = new Pager<RpAccidentPerson>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("updateTime");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("desc");
			}

			if (pager.isAutoCount()) {
				long totalCount = rpAccidentPersonDao.getTotalCount(param);
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
			map.put("pager", pager); // 将pager装入到map中
			List<RpAccidentPerson> accidentPersons = rpAccidentPersonDao.findAllBy(map);
			pager.setResult(accidentPersons);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw new RuntimeException("获取失败");
		}
		logger.info("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public void update(RpAccidentPerson accidentPerson) throws Exception {
		logger.info("start method: void update(RpAccidentPerson accidentPerson), arg accidentPerson: "
				+ accidentPerson);
		if (accidentPerson == null || accidentPerson.getId() == null) {
			logger.warn("arg accidentPerson is null or accidentPerson 's id is null");
			throw new IllegalArgumentException("arg accidentPerson is null or accidentPerson 's id is null");
		}
		try {
			rpAccidentPersonDao.update(accidentPerson);
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public void delete(List<Long> ids) throws Exception {
		logger.info("start method: void delete(List<Long> ids), arg ids:" + ids);
		if (ids == null || ids.size() == 0) {
			logger.warn("arg id is null or size = 0");
			throw new IllegalArgumentException("arg id is null");
		}
		try {
			rpAccidentPersonDao.delete(ids);
		} catch (Exception e) {
			logger.error("err method,Exception:" + e);
			throw e;
		}
		logger.info("complete method,return void");
	}

}