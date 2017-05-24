package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.api.lar.service.ShipmentOperationService;
import com.sdcloud.biz.lar.dao.ShipmentOperationDao;
import com.sdcloud.framework.entity.LarPager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 韩亚辉 on 2016/3/18.
 */
@Service
public class ShipmentOperationServiceImpl extends BaseServiceImpl<ShipmentOperation> implements ShipmentOperationService{

    @Autowired
    private ShipmentOperationDao shipmentOperationDao;
    @Override
    public ShipmentOperation findBySysId(Long aLong) {
        return shipmentOperationDao.findBySysId(aLong);
    }

    @Override
    public List<ShipmentOperation> findByMap(Map<String, Object> map) {
        return shipmentOperationDao.findByMap(map);
    }

	@Override
	public List<ShipmentOperation> findListBySysId(Long userId) {
		return shipmentOperationDao.findListBySysId(userId);
	}

	@Override
	public LarPager<ShipmentOperation> findEmploy(LarPager<ShipmentOperation> larPager, List<Long> ids) {
		List<ShipmentOperation> list = shipmentOperationDao.findEmploy(larPager, ids);
		Long count = shipmentOperationDao.countByEmploy(larPager, ids);
		larPager.setResult(list);
		larPager.setTotalCount(count);
		return larPager;
		
	}
}
