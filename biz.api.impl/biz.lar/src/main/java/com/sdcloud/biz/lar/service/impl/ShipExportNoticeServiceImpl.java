package com.sdcloud.biz.lar.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sdcloud.api.lar.entity.ShipExportNotice;
import com.sdcloud.api.lar.service.ShipExportNoticeService;
import com.sdcloud.biz.lar.dao.ShipExportNoticeDao;

/**
 * lar_ship_export_notice 物流导入通知表
 * @author jiazc
 * @date 2016-11-23
 * @version 1.0
 */
@Service
public class ShipExportNoticeServiceImpl extends BaseServiceImpl<ShipExportNotice> implements ShipExportNoticeService{

	@Autowired
	private ShipExportNoticeDao shipExportNoticeDao;
	@Override
	@Transactional(readOnly = false)
	public int batchSave(List<ShipExportNotice> result) {
		if(!CollectionUtils.isEmpty(result)){
			return shipExportNoticeDao.batchSave(result);
		}
		return 0;
	}


}
