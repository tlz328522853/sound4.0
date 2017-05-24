package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ShipmentArea;

/**
 * 
 * @author wrs
 */
@Repository
public interface ShipmentAreaDao extends BaseDao<ShipmentArea> {
	
	@Override
	@Select("select  id, org, name, workingArea, position,time,startDate, endDate, remark "
			+ "from lar_shipmentArea where id=#{id}")
	ShipmentArea getById(@Param("id") Long id, Map<String, Object> map);
	
	/**
	 * 修改：根据机构ID 查找该机构下的 物流片区
	 * @author jzc 2016年6月16日
	 * @param mechanismId
	 * @return
	 */
	@Select(value="select id, org, name, workingArea, position,time,startDate, endDate, remark from lar_shipmentArea  where org = #{org}  order by createDate ASC")
	List<ShipmentArea> selectShipmentAreasByOrg(@Param("org") String org);
	
}
