package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.DistributeExpress;

@Repository
public interface DistributeExpressDao extends BaseDao<DistributeExpress>{

	List<DistributeExpress> findByOrderNos(@Param("orderNos") List<String> orderNos);
	
	DistributeExpress findByOrderNo(@Param("orderNo") String orderNo);
	
	int updateByOrderNo(DistributeExpress distribute);
	
	/**
	 * 批量验证运单号是否存在，返回已经存在的运单号
	 * @author jzc 2016年10月27日
	 * @param orderNos
	 * @return
	 * @throws Exception
	 */
	List<String> queryByOrderNos(@Param("orderNos") List<String> orderNos);
	
	/**
	 * 数据导入批量添加
	 * @author jzc 2016年10月27日
	 * @param result
	 * @return
	 */
	int batchExportSave(@Param("list") List<DistributeExpress> result);

	/**
	 * 根据ids查询数据
	 * @param list
	 * @return
	 */
	List<DistributeExpress> getByIds(List<Long> list);
	
}
