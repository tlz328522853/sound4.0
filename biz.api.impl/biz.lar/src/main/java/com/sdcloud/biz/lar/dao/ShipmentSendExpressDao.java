package com.sdcloud.biz.lar.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.OrderDetailDTO;
import com.sdcloud.api.lar.entity.OrderServiceDTO;
import com.sdcloud.api.lar.entity.ShipmentSendExpress;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/25.
 */
@Repository
public interface ShipmentSendExpressDao extends BaseDao<ShipmentSendExpress> {
    int updateState(@Param("params") Map<String, Object> params, @Param("ids") List<Long> list);

    List<ShipmentSendExpress> getBalance();

    List<ShipmentSendExpress> getCount();

    List<OrderServiceDTO> serviceList(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    Long serviceListCount(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    OrderDetailDTO orderDetail(@Param("id") Long id, @Param("map") Map<String, Object> map);

    List<OrderServiceDTO> serviceHistory(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    long serviceHistoryCount(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    /**
     * 获取此业务员下的 带评价的订单
     *
     * @param userId
     * @return
     */
    List<OrderDetailDTO> getEvaluation(@Param("id") Long userId);

    ShipmentSendExpress getByNo(String orderNo);

    List<OrderServiceDTO> grabOrderList(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    long grabOrderListCount(@Param("larPager") LarPager<OrderServiceDTO> larPager, @Param("userId") Long userId);

    List<ShipmentSendExpress> findByOrgIdsOne(@Param("larPager")LarPager<ShipmentSendExpress> larPager,@Param("ids") List<Long> list);

    long countByOrgIdsOne(@Param("larPager")LarPager<ShipmentSendExpress> larPager,@Param("ids") List<Long> list);

    @Update("update lar_send_express a set a.grab_order=33    where unix_timestamp(a.order_time)+${time}< unix_timestamp(now())  and  a.order_State='等待接单' and a.grab_order=31 ")
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
	List<ShipmentSendExpress> findInvalidOrder(@Param("time") Long time,@Param("date") Date date);
	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	int batchUpdate(List<ShipmentSendExpress> list);
    /**
     * 根据工号获取业务员ids
     * @author jzc 2016年11月15日
     * @param customerId
     * @return
     */
	@Select(value = "SELECT id FROM `lar_operation` WHERE `enable`=0 AND sysUser=#{customerId}")
	List<String> getSalesmansByCustomerIds(@Param("customerId") String customerId);
    /**
     * 根据业务员ids 获取物流服务中的数量
     * @author jzc 2016年11月15日
     * @param id
     * @return
     */
	int selectDisCount(@Param("id") List<String> id);
}
