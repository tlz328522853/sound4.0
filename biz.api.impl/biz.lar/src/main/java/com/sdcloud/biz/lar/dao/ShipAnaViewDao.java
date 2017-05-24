package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.ShipAnaView;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luorongjie on 2017/5/13.
 */
@Repository
public interface ShipAnaViewDao {

    List<ShipAnaView> getShipAndReco(@Param("month") String month);

    List<ShipAnaView> getBrand(@Param("month") String month);

    List<ShipAnaView> getSku(@Param("month") String month);

    List<ShipAnaView> getStockfee(@Param("month") String month);

    List<ShipAnaView> getStockPrice(@Param("month") String month);

    List<ShipAnaView> getDelivery(@Param("month") String month);

    List<ShipAnaView> getCollect(@Param("month") String month);

    List<ShipAnaView> getStockPriceCollect(@Param("month") String month);

}
