package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ShipSalesCustom;

/**
 * 业务员的客户地址
 * @author jzc
 * 2016年6月18日
 */
@Repository
public interface ShipSalesCustomDao extends BaseDao<ShipSalesCustom>{
    
	/**
	 * 添加
	 */
	@Override
	int save(ShipSalesCustom salesCustom);
    
	/**
	 * 更新
	 */
	@Override
	int update(ShipSalesCustom salesCustom);
	
	/**
	 * 查询所有
	 * @author jzc 2016年6月18日
	 * @param salesCustom
	 * @return
	 */
	List<ShipSalesCustom> findAll(ShipSalesCustom salesCustom);
	
	/**
	 * 根据条件 count
	 * @author jzc 2016年6月18日
	 * @param salesCustom
	 * @return
	 */
	Long totalCount(ShipSalesCustom salesCustom);
	

}
