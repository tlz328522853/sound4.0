package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.envsanitation.entity.AdvertDirective;
import com.sdcloud.api.envsanitation.service.AdvertDirectiveService;
import com.sdcloud.biz.envsanitation.dao.AdvertDirectiveDao;
import com.sdcloud.framework.common.Constants;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.Pager;

@Service("advertDirectiveService")
public class AdvertDirectiveServiceImpl implements AdvertDirectiveService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AdvertDirectiveDao advertDirectiveDao;
	
	@Transactional
	public void insert(List<AdvertDirective> advertDirectives) throws Exception {
		logger.info("start method: long insert(AdvertDirective advertDirectives), arg advertDirectives: "
				+ advertDirectives);

		if (advertDirectives==null||advertDirectives.size()<=0) {
			logger.warn("arg AdvertDirective is null");
			throw new IllegalArgumentException("arg AdvertDirective is null");
		}
		for (AdvertDirective advertDirective : advertDirectives) {
			long id = -1;
			id = UUIDUtil.getUUNum();
			advertDirective.setAdvertDirectiveId(id);
		}
		
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					advertDirectiveDao.insert(advertDirectives);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}

		
	}

	@Transactional
	public void delete(List<Long> advertDirectives) throws Exception {
		logger.info("start method: void delete(List<Long> advertDirectives), arg advertDirectives: "
				+ advertDirectives);
		if (advertDirectives == null || advertDirectives.size() == 0) {
			logger.warn("arg advertDirectives is null or size=0");
			throw new IllegalArgumentException("arg advertDirectives is null or size=0");
		}
		try {
			
			advertDirectiveDao.delete(advertDirectives);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");

	}

	@Transactional
	public void update(AdvertDirective advertDirective) throws Exception {
		logger.info("start method: void update(advertDirective advertDirective), arg advertDirective: "
				+ advertDirective);
		if (advertDirective == null   ) {
			logger.warn("arg advertDirective is null or advertDirectiveId is null");
			throw new IllegalArgumentException("arg advertDirective is null or advertDirectiveId is null");
		}
		try {
			
			advertDirectiveDao.update(advertDirective);
			
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.info("complete method, return void");
	}

	@Transactional
	public Pager<AdvertDirective> findBy(Pager<AdvertDirective> pager, Map<String, Object> param) throws Exception {
		logger.info("start method: Pager<AdvertDirective> findBy(Pager<AdvertDirective> pager, Map<String, Object> param), arg pager: "
				+ pager + ", arg param: " + param);
		
		if (pager == null) {
			pager = new Pager<AdvertDirective>();
		}
		if (param == null) {
			param = new HashMap<String, Object>();
		}
		try {
			logger.info("init default pager");
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				pager.setOrderBy("advertDirectiveId");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				pager.setOrder("ASC");
			}

			if (pager.isAutoCount()) {
				long totalCount = advertDirectiveDao.getTotalCount(param);
				pager.setTotalCount(totalCount);

				logger.info("querying total count result : " + totalCount);
			}
			
			param.put("pager", pager); //将pager装入到map中
			
			List<AdvertDirective> advertDirectives = advertDirectiveDao.findAllBy(param);

			pager.setResult(advertDirectives);

		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		logger.debug("complete method, return pager: " + pager);
		return pager;
	}

	@Transactional
	public Long insert(AdvertDirective advertDirective) throws Exception {
		long id = -1;
		id = UUIDUtil.getUUNum();
		advertDirective.setAdvertDirectiveId(id);
		List<AdvertDirective> advertDirectives=new ArrayList<AdvertDirective>();
		advertDirectives.add(advertDirective);
		try {
			int tryTime = Constants.DUPLICATE_PK_MAX;
			while (tryTime-- > 0) {
				try {
					advertDirectiveDao.insert(advertDirectives);
					break;
				} catch (Exception se) {
					if (tryTime == 1)
						throw se;
					if (se instanceof DuplicateKeyException) {
						throw se;
					}
				}
			}
		} catch (Exception e) {
			logger.error("err method, Exception: " + e);
			throw e;
		}
		return id;
	}

}
