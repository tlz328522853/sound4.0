package com.sdcloud.biz.lar.dao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import com.sdcloud.api.lar.entity.TableLock;
/**
 * 锁定业务表标识：具体业务表锁定后不做更新操作，避免并发死锁
 */
@Repository
public interface TableLockDao extends BaseDao<TableLock>{
	/**
	 * @param bizType 业务表类型标识
	 * @return 是否获取到锁：0未获取到，1获取到锁
	 */
	@Update("update lar_table_lock  set lockFlag=1 where bizType=${bizType} and lockFlag=0")
	int lock(@Param("bizType")int  bizType);
	/**
	 * @param bizType 业务表类型标识
	 * @return 是否获取到锁：0未获取到，1获取到锁
	 */
	@Update("update lar_table_lock  set lockFlag=0 where bizType=${bizType}")
	int unLock(@Param("bizType")int  bizType);
	
}
