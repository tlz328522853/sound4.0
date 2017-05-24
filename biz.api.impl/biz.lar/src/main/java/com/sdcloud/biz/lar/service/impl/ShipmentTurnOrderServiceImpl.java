package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import com.sdcloud.api.lar.service.ShipmentTurnOrderService;
import com.sdcloud.biz.lar.dao.ShipmentTurnOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 韩亚辉 on 2016/3/31.
 */
@Service
public class ShipmentTurnOrderServiceImpl extends BaseServiceImpl<ShipmentTurnOrder> implements ShipmentTurnOrderService {
    @Autowired
    private ShipmentTurnOrderDao shipmentTurnOrderDao;

    @Override
    public List<ShipmentTurnOrder> getByOrderId(Long orderId) {
        return shipmentTurnOrderDao.getByOrderId(orderId);
    }
}
