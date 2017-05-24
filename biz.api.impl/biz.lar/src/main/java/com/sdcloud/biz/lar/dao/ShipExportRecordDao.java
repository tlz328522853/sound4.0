package com.sdcloud.biz.lar.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ShipExportRecord;
import com.sdcloud.framework.entity.LarPager;

/**
 * 
 * @author jiazc
 * @date 2016-10-24
 * @version 1.0
 */
@Repository
public interface ShipExportRecordDao extends BaseDao<ShipExportRecord> {
	
    /**
     * 批量删除数据
     * @param primary 主键值
     * @return 影响条数
     */
    public long deleteByIds(Collection<Long> ids);
    
    /**
	 * 根据删主键软删除的数据
	 * @param model
	 */
    public int disableByPrimary(long id);

	/**
	 * 根据多个ID软删除数据
	 * @param model
	 */
	public int disableByIds(Collection<Long> ids);
	
    /**
	 * 批量更新数据
	 * @param model
	 */
	public int updateBatch(List<ShipExportRecord> data);
	
    /**
     * 根据主键查询数据
     * @param primary
     * @return
     */
    public ShipExportRecord selectByPrimary(long id);
    
    /**
     * 根据条件分页查询数据
     * @param conditions 查询条件
     * @param pager
     * @return
     */
    public List<ShipExportRecord> findAll(@Param("params") Map<String, Object> conditions,@Param("pager") LarPager<ShipExportRecord> pager);
    
    /**
     * 根据条件查询总数
     * @param conditions 查询条件
     * @return
     */
    public long totalCount(@Param("params") Map<String, Object> conditions);
	
	
}
