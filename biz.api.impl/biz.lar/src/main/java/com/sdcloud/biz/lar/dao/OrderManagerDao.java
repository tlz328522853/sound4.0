package com.sdcloud.biz.lar.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.Evaluate;
import com.sdcloud.api.lar.entity.MyPoints;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecyclingImg;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.UserOrderCount;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface OrderManagerDao {
	// 查询总数
	@Select(value = "select count(o.id) from lar_ordermanager o left outer join lar_areasettings a on(o.areaSettingId=a.id) where o.enable = 0 and a.mechanismId=#{mechanismId} and orderStatusId=#{orderStatusId}")
	int countById(@Param("mechanismId") String mechanismId, @Param("orderStatusId") String orderStatusId);
	
	OrderManager getById(@Param("id") String id);
	// 查询子单号
	int countByChildId(@Param("params") Map<String, Object> params);

	// 根据id删除
	@Delete(value = "update lar_ordermanager set enable=1 where id = #{id}")
	int deleteById(@Param("id") String id);

	// 删除回收物品照片
	@Delete(value = "delete FROM lar_recyclingimg where orderManagerId = #{orderId}")
	int deleteRecyclingImgById(@Param("orderId") String orderId) throws Exception;

	// 删除联系人
	@Delete(value = "update lar_address set enable=1 where id = #{id}")
	int deleteClientUserAddressById(@Param("id") String id) throws Exception;

	// 删除子单号
	@Delete(value = "update lar_childorders set enable=1 where orderManagerId = #{orderId}")
	int deleteChildOrderByOrderId(@Param("orderId") String orderId) throws Exception;

	// 删除单个子单号
	@Delete(value = "update lar_childorders set enable=1 where id = #{id}")
	int deleteChildOrderById(@Param("id") String id) throws Exception;

	// 添加订单，指定字段
	int insertSelective(OrderManager orderManager);

	// 添加回收物，指定字段
	int insertSelectiveImg(RecyclingImg recyclingImg);

	// 添加子单号
	int insertSelectiveChild(ChildOrders childOrders);

	// 根据条件查询所有信息
	List<OrderManager> selectByExample(@Param("larPager") LarPager<OrderManager> larPager);

	// 根据条件查询所有信息,查询子单号
	List<ChildOrders> selectCildByExample(@Param("larPager") LarPager<ChildOrders> larPager);

	// 查询一个order记录
	OrderManager selectOrderByExample(@Param("params") Map<String, Object> params);

	// 更新用户，指定字段(有条件的)
	int updateByExampleSelective(@Param("params") Map<String, Object> params);

	// 更新图片，指定字段(有条件的)
	int updateByImgSelective(@Param("params") Map<String, Object> params);

	// 更新子单号
	int updateChildByExampleSelective(@Param("params") Map<String, Object> params);

	@Select(value = "select `id`,`typeName`,`enable` from lar_recyclingtype where enable=0")
	List<RecyclingType> getRecyclingTypes();

	@Select(value = "SELECT r.`id`,`recyclingTypeId`,`goodsId`,`goodsName`,`goodsDescribe`,`meteringCompany`,"
			+ "`imgUrl`,`startUsing`,`startUsingForApp`,r.`enable`,n.`id` AS \"recyclingTypeId.id\","
			+ "`typeName` AS \"recyclingTypeId.typeName\",n.`enable` AS \"recyclingTypeId.enable\" "
			+ "FROM `lar_recyclingmaterial` r LEFT OUTER JOIN `lar_recyclingtype` n ON(r.recyclingTypeId=n.id) WHERE recyclingTypeId=#{id} and r.enable=0")
	List<RecyclingMaterial> getRecyclingNames(@Param("id") String id);

	@Update("update lar_ordermanager set orderStatusId=#{params.orderStatusId},"
			+ "cancelOrderPersonId=#{params.cancelOrderPersonId},cancelOrderPersonName=#{params.cancelOrderPersonName},"
			+ "cancelOrderIllustrate=#{params.cancelOrderIllustrate},cancelDate=#{params.cancelDate},orderStatusName=#{params.orderStatusName} where id=#{params.id}")
	int cancelOrderById(@Param("params") Map<String, Object> params);

	@Update("update lar_ordermanager set orderStatusId=#{params.orderStatusId},takeDate=#{params.takeDate},userId=#{params.userId},userName=#{params.userName},orderStatusName=#{params.orderStatusName} where id=#{params.id}")
	int comeOrderById(@Param("params") Map<String, Object> updateParams);

	// 根据片区ID查询业务员
	@Select("SELECT `id`,`areaSettingId` as \"areaSetting.id\",`manId`,`manName`,`deviceId`,`ownedSupplierId` as \"ownedSupplier.id\",`manDescribe`,`personnelId`,`createDate`,`enable` FROM `lar_salesman`  WHERE enable=0  and areaSettingId=#{id}")
	List<Salesman> getSalesmansByAreaId(@Param("id") String id);

	@Update("update lar_ordermanager set orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},"
			+ "sendSingleId=#{param.sendSingleId},sendSingleName=#{param.sendSingleName},salesmanId=#{param.salesman},"
			+ "distributeDate=#{param.distributeDate},distributeIllustrate=#{param.distributeIllustrate},areaSettingId=#{param.areaSettingId}  "
			+ "where id=#{param.id}")
	int updateDispatchOrder(@Param("param") Map<String, Object> params);

	@Update("update lar_ordermanager set orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},cancelTakePersonId=#{param.cancelTakePersonId},"
			+ "cancelTakePersonName=#{param.cancelTakePersonName},cancelTakeIllustrate=#{param.cancelTakeIllustrate},cancelTakeDate=#{param.cancelTakeDate}  where id=#{param.id}")
	int cancelDispatchOrder(@Param("param") Map<String, Object> params);

	@Select("select `id`,`name`,`createdate`,`level` from lar_evaluate")
	List<Evaluate> getEvaluates();

	@Select("SELECT s.`id`,`manId`,`manName`,`deviceId`,`manDescribe`,s.`createDate`,s.`enable` FROM `lar_salesman` s LEFT OUTER JOIN `lar_areasettings` a ON(s.`areaSettingId`=a.id) WHERE a.`mechanismId`=#{id} AND s.enable=0")
	List<Salesman> getSalesmansBymechanismId(@Param("id") String id);

	@Update("update lar_ordermanager set finishDate=#{param.finishDate} ,orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},confirmPersionId=#{param.confirmPersionId}"
			+ ",confirmPersionName=#{param.confirmPersionName},completionType=#{param.completionType},completionName=#{param.completionName}"
			+ ",completionIllustrate=#{param.completionIllustrate},lengthService=#{param.lengthService},failureTypeId=#{param.failureTypeId},failureTypeName=#{param.failureTypeName}  where orderId=#{param.orderId}")
	int confirmOrder(@Param("param") Map<String, Object> param);

	@Update("update lar_ordermanager set finishDate=#{param.finishDate} ,orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},confirmPersionId=#{param.confirmPersionId}"
			+ ",confirmPersionName=#{param.confirmPersionName},completionType=#{param.completionType},completionName=#{param.completionName}"
			+ ",completionIllustrate=#{param.completionIllustrate},lengthService=#{param.lengthService},failureTypeId=#{param.failureTypeId},failureTypeName=#{param.failureTypeName}  where id=#{param.id}")
	int confirmOrders(@Param("param") Map<String, Object> param);

	long countByIds(@Param("params") Map<String, Object> params);

	@Update("update lar_ordermanager set cancelSendPersonId=#{param.cancelSendPersonId},cancelSendPersonName=#{param.cancelSendPersonName}"
			+ ",orderStatusId=#{param.orderStatusId},orderStatusName=#{param.orderStatusName},cancelSendIllustrate=#{param.cancelSendIllustrate},cancelSendDate=#{param.cancelSendDate}  where id=#{param.id}")
	int cancelSendOrder(@Param("param") Map<String, Object> params);

	@Update("update lar_childorders set confirmOrder=#{params.confirmOrder} where id=#{params.id}")
	int childOrderConfirm(@Param("params") Map<String, Object> params);

	@Select("SELECT appUserId, COUNT(appUserId) AS \"orderCount\" " + "FROM lar_ordermanager AS a "
			+ "LEFT JOIN  `lar_clientuser` b ON  a.appUserId = b.customerId  "
			+ "WHERE a.orderStatusId = 4 and b.enable=0 " + "GROUP BY appUserId")
	List<UserOrderCount> getOrderByAppUserId();

	@Select("SELECT confirmPersionId,confirmPersionName FROM `lar_ordermanager` o LEFT OUTER JOIN `lar_areasettings` a "
			+ "ON(o.areaSettingId=a.id) WHERE o.enable=0  AND mechanismId=#{id} AND confirmPersionId IS NOT NULL GROUP BY confirmPersionId")
	List<OrderManager> getConfirmationPersonById(@Param("id") String id);

	long countTransaction(@Param("params") Map<String, Object> params);

	List<OrderManager> selectTransaction(@Param("larPager") LarPager<OrderManager> pager);

	@Select(value = "SELECT id FROM `lar_salesman` WHERE `enable`=0 AND personnelId=#{customerId}")
	String getSalesmansByCustomerId(@Param("customerId") String customerId);
	
	@Select(value = "SELECT id FROM `lar_salesman` WHERE `enable`=0 AND personnelId=#{next_sales_man}")
	String getByCustomerId(String next_sales_man);

	@Select(value = "SELECT id FROM `lar_salesman` WHERE `enable`=0 AND personnelId=#{customerId}")
	List<String> getSalesmansByCustomerIds(@Param("customerId") String customerId);

	// @Select(value = "SELECT COUNT(id) FROM `lar_ordermanager` WHERE
	// `orderStatusId` = 3 AND salesmanId=#{id} AND ENABLE=0 ")
	int selectDisCount(@Param("id") List<String> id);

	List<OrderManager> getOrderByCustomerId(@Param("params") Map<String, Object> params);

	List<OrderManager> getOrderByCustomerIds(@Param("params") Map<String, Object> params);

	List<OrderManager> getOrderByCustomerIdByPage(@Param("params") Map<String, Object> params);

	Long totalCount(@Param("params") Map<String, Object> params);

	@Update("update lar_ordermanager set evaluateId=#{evaluate} where id=#{orderId}")
	int updateEvaluation(@Param("orderId") Long aLong, @Param("evaluate") Integer integer);

	int appConfirmOrder(@Param("params") Map<String, Object> updateParams);

	List<MyPoints> getOrderByUserId(@Param("params") Map<String, Object> params);

	// 更改订单状态
	@Update("update lar_ordermanager o set o.grab_order=33 where unix_timestamp(o.placeOrder)+${time} < unix_timestamp(now()) and o.orderStatusId=1  and o.grab_order=31 ")
	int updateGrabState(@Param("time") Long time);

	// 根据订单ID更新客户的积分
	long upateIntegralByOrderId(@Param("orderId") String orderId, @Param("integral") int integral);

	int updateGrab(@Param("params") Map<String, Object> params, @Param("condition") Map<String, Object> condition);

	@Update("update lar_ordermanager set check_status=#{checkStatus},money=#{money} where orderId=#{orderId} AND check_status = 0")
	int updateCheckStatus(OrderManager orderManager);

	/**
	 * 查询抢单模式下失效的订单
	 * 
	 * @param time
	 *            抢单时效时间 单位:m 秒
	 * @param date
	 * @return
	 */
	List<OrderManager> findInvalidOrder(@Param("time") Long time, @Param("date") Date date);

	/**
	 * 批量更新
	 * 
	 * @param list
	 * @return
	 */
	int batchUpdate(List<OrderManager> list);

	List<OrderManager> getConfirmationPersonById2(@Param("orgIds") List<Long> orgIds);

	List<Long> getCheckMens(@Param("larPager") LarPager<OrderManager> larPager);

	List<Salesman> getSalesmansBymechanismId2(@Param("orgIds") List<Long> orgIds);

	boolean updateSalesmanId(Map<String, Object> result);
	@Select("select o.id,orderId,areaSettingId,placeOrder,"
			+ "orderStatusId,orderStatusName,mechanismId AS 'areaSetting.mechanismId',"
			+ "a.id AS 'areaSetting.id' "
			+ "from  `lar_ordermanager` o "
			+ "LEFT OUTER JOIN `lar_areasettings` a ON o.areaSettingId=a.id "
			+ "where orderId=#{orderId}")
	List<OrderManager> getByOrderId(String orderId);
	@Select("select placeOrder from `lar_ordermanager` where orderId=#{orderId}")
	Date selectPlaceOrder(String orderId);
}
