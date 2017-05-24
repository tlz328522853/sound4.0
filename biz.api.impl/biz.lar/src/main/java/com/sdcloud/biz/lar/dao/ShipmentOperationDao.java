package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.ShipmentArea;
import com.sdcloud.api.lar.entity.ShipmentOperation;
import com.sdcloud.framework.entity.LarPager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by 韩亚辉 on 2016/3/18.
 */
@Repository
public interface ShipmentOperationDao extends BaseDao<ShipmentOperation>{

    ShipmentOperation findBySysId(@Param("id") Long id);
    
    List<ShipmentOperation> findListBySysId(Long userId);
    
    List<ShipmentOperation> findByMap(@Param("map") Map<String, Object> map);

	List<ShipmentOperation> selectByOrg(@Param("org")String org);

	List<ShipmentOperation> selectByShipmentAreas(@Param("shipmentAreas")List<ShipmentArea> shipmentAreas);
	
	List<ShipmentOperation> findEmploy(@Param("larPager") LarPager<ShipmentOperation> larPager, @Param("ids") List<Long> ids);

	Long countByEmploy(@Param("larPager") LarPager<ShipmentOperation> larPager, @Param("ids") List<Long> list);

}
