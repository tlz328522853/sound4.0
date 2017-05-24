package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.entity.MallTimeSetting;
import com.sdcloud.api.lar.service.MallTimeSettingService;
import com.sdcloud.biz.lar.dao.MaaDateSettingDao;
import com.sdcloud.biz.lar.dao.MallTimeSettingDao;
import com.sdcloud.framework.entity.LarPager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商城时间段设置
 * Created by dingx on 2016/7/11.
 */
@Service
public class MallTimeSettingServiceImpl extends BaseServiceImpl<MallTimeSetting> implements MallTimeSettingService {
	
	@Autowired
	private MallTimeSettingDao mallTimeSettingDao;
	
	@Transactional(readOnly=true)
	public LarPager<MallTimeSetting> selectByExample(LarPager<MallTimeSetting> pager)throws Exception {
		if (pager == null) {
			pager = new LarPager<MallTimeSetting>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("startTime");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("asc");
			}
			if (pager.isAutoCount()) {
				long totalCount = mallTimeSettingDao.countById();
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<MallTimeSetting> result = mallTimeSettingDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Transactional(readOnly=true)
	public int countById() throws Exception {
		int count = 0;
		try {
			count = mallTimeSettingDao.countById();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}
	
}
