package com.sdcloud.biz.lar.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/28.
 */
@Repository
public interface ShipmentHelpMeBuyDao extends BaseDao<ShipmentHelpMeBuy> {
    int updateState(@Param("params") Map<String, Object> params, @Param("ids") List<Long> list);

    List<ShipmentHelpMeBuy> getBalance();

    List<ShipmentHelpMeBuy> getCount();

    OrderDetailDTO orderDetail(@Param("id") Long id, @Param("map") Map<String, Object> map);

    /**
     * 获取此业务员下的 带评价的订单
     *
     * @param userId
     * @return
     */
    List<OrderDetailDTO> getEvaluation(@Param("id")Long userId);
    
    List<ShipmentHelpMeBuy> getByIds(List<Long> ids);

    ShipmentHelpMeBuy getByNo(String orderNo);

    List<ShipmentHelpMeBuy> findByOrgIdsOne(@Param("larPager")LarPager<ShipmentHelpMeBuy> larPager, @Param("ids")List<Long> list);

    long countByOrgIdsOne(@Param("larPager")LarPager<ShipmentHelpMeBuy> larPager,@Param("ids") List<Long> list);
    @Update("update lar_help_me_buy a  set  a.grab_order=33 where unix_timestamp(a.order_time)+${time}< unix_timestamp(now())  and  a.order_State='等待接单'  and a.grab_order=31 ")
    int updateGrabState(@Param("time") long time);

	int updateGrab(@Param("result")Map<String, Object> result, @Param("list")List<Long> list, @Param("condition")Map<String, Object> condition);
	/**
	 * 返回优惠券
	 * @param ids
	 * @return
	 */
	int backCoupon(@Param("ids") List<Long> ids);
	/**
	 * 查询抢单模式下失效的订单
	 * @param time 抢单时效时间 单位:m 秒
	 * @param date
	 * @return
	 */
	List<ShipmentHelpMeBuy> findInvalidOrder(@Param("time") Long time,@Param("date") Date date);
	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	int batchUpdate(List<ShipmentHelpMeBuy> list);
}
