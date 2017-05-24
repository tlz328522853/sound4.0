package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.lar.entity.TakingExpress;

public interface TakingExpressDao extends BaseDao<TakingExpress>{

	List<TakingExpress> getByOrderNo(@Param("orderNos")List<String> orderNos);
	
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
	int batchExportSave(@Param("list") List<TakingExpress> result);

}
