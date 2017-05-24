package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.ShipmentTurnOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 韩亚辉 on 2016/3/31.
 */
@Repository
public interface ShipmentTurnOrderDao extends BaseDao<ShipmentTurnOrder> {
    List<ShipmentTurnOrder> getByOrderId(@Param("orderId") Long orderId);
}
