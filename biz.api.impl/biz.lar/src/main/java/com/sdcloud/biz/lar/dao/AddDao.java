package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by luorongjie on 2017/5/9.
 */
@Repository
public interface AddDao {

    int addStock(@Param("outstock") Map<String ,Object> outstock);

    int addBrand(@Param("brand") Map<String ,Object> brand);

    int addSku(@Param("sku") Map<String ,Object> sku);
}
