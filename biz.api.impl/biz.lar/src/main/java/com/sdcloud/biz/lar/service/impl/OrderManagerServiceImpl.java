package com.sdcloud.biz.lar.service.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.json.JSON;
import com.sdcloud.api.lar.entity.AreaSetting;
import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.Evaluate;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.api.lar.entity.MessageCenter;
import com.sdcloud.api.lar.entity.MyPoints;
import com.sdcloud.api.lar.entity.OrderManager;
import com.sdcloud.api.lar.entity.RecyclingImg;
import com.sdcloud.api.lar.entity.RecyclingMaterial;
import com.sdcloud.api.lar.entity.RecyclingType;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.UserOrderCount;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.AreaSettingService;
import com.sdcloud.api.lar.service.LarClientUserAddressService;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.api.lar.service.RecyclingItemsService;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.biz.lar.dao.AreaSettingDao;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.LarClientUserAddressDao;
import com.sdcloud.biz.lar.dao.LarEventDao;
import com.sdcloud.biz.lar.dao.MessageCenterDao;
import com.sdcloud.biz.lar.dao.OrderManagerDao;
import com.sdcloud.biz.lar.dao.RecycleOrderDao;
import com.sdcloud.biz.lar.dao.RecyclingItemsDao;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.biz.lar.dao.TableLockDao;
import com.sdcloud.biz.lar.util.MapUtil;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class OrderManagerServiceImpl implements OrderManagerService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderManagerDao orderManagerDao;
	@Autowired
	private TableLockDao tableLockDao;
	@Autowired
	private XingeAppUtils xingeAppUtils;
	@Autowired
	private LarClientUserAddressDao larClientUserAddressDao;

	@Autowired
	private LarClientUserAddressService larClientUserAddressService;

	@Autowired
	private SalesmanDao salesmanDao;

	@Autowired
	private AreaSettingDao areaSettingDao;

	@Autowired
	private AreaSettingService areaSettingService;
	@Autowired
	private CityDao cityDao;

	@Autowired
	private RecyclingItemsService recyclingItemsService;

	@Autowired
	private RecyclingItemsDao recyclingItemsDao;

	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private MessageCenterDao messageCenterDao;
	
	@Autowired
	private RecycleOrderDao recycleOrderDao;
	
	@Autowired
	private LarEventDao larEventDao;

	@Transactional(readOnly = true)
	public int countById(String mechanismId) throws Exception {
		int count = 0;
		try {
			count = orderManagerDao.countById(mechanismId, "0");
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	// 查询子单号
	@Transactional(readOnly = true)
	public int countByChildId(String orderId) throws Exception {
		int count = 0;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", orderId);
			count = orderManagerDao.countByChildId(params);
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	@Transactional
	public boolean deleteById(OrderManager orderManager) throws Exception {
		if (orderManager != null && orderManager.getId() != null) {
			int count = 0;
			try {
				// 删除订单
				count = orderManagerDao.deleteById(orderManager.getId());
				// 如果联系人不是用户的就也删除
				LarClientUserAddress larClientUserAddress = orderManager.getLarClientUserAddress();
				String id = larClientUserAddress.getId();
				// 查询这个联系人是否绑定了账户
				int userCount = larClientUserAddressDao.selectUserByAddressId(id);
				if (userCount <= 0) {
					count += orderManagerDao.deleteClientUserAddressById(id);
				}
				// 回收物照片删除
				count += orderManagerDao.deleteRecyclingImgById(id);
				// 子单号删除
				count += orderManagerDao.deleteChildOrderByOrderId(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("orderManager is error");
		}
	}

	// 删除子单号
	@Transactional
	public boolean deleteByChildId(String id) throws Exception {
		if (id != null && id.trim().length() > 0) {
			int count = 0;
			try {
				// 子单号删除
				count = orderManagerDao.deleteChildOrderById(id);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Transactional
	public boolean insertSelective(OrderManager orderManager) throws Exception {
		if (orderManager != null) {
			int count = 0;
			// 保存该城市下所有区域
			List<AreaSetting> areaSettings = null;
			City city = null;
			try {
				// app端提交上来的订单是没有片区的
				if (orderManager.getAreaSetting() == null || orderManager.getAreaSetting().getId() == null) {
					// 匹配机构,需要根据用户坐标查询片区和对应的机构
					// 根据服务城市ID查询机构ID
					city = cityDao.selectByCityId(orderManager.getCity());
					if (city == null || city.getId() == null) {
						return false;
					}
					// 根据机构ID查询片区对象
					areaSettings = areaSettingDao.selectAreaById(String.valueOf(city.getOrg()));
					if (areaSettings == null || areaSettings.size() <= 0) {
						return false;
					}
					// 绑定片区ID和订单
					orderManager.setAreaSetting(areaSettings.get(0));
				}
				orderManager.setId(String.valueOf(UUIDUtil.getUUNum()));
				orderManager.setOrderId(generateNumber());
				orderManager.setBusinessTypeId(1);
				orderManager.setBusinessTypeName("资源回收");
				Date date = new Date();
				orderManager.setPlaceOrder(date);
				orderManager.setOrderStatusId(1);
				orderManager.setOrderStatusName("等待接单");
				orderManager.setCreateDate(date);
				orderManager.setEnable(0);
				orderManager.setCreateDate(date);
				orderManager.setGrabOrder(10);

				// 先把用户添加进去
				// 从PC端进来的订单是没有绑定用户地址的
				if (orderManager.getLarClientUserAddress().getId() == null) {
					LarClientUserAddress larClientUserAddress = null;
					larClientUserAddress = orderManager.getLarClientUserAddress();
					larClientUserAddress.setId(String.valueOf(UUIDUtil.getUUNum()));
					larClientUserAddress.setEnable(0);
					larClientUserAddressDao.insertSelective(larClientUserAddress);
					orderManager.setLarClientUserAddress(larClientUserAddress);
					orderManager.setOrderSourceId(4);
					orderManager.setOrderSourceName("pc");
				}

				// 看看是后台设置的 是什么模式 抢单 或自动 或手动
				Map<String, String> map = sysConfigService.findMap();
				String orderModel = map.get("orderModel");
				// 默认是手动
				if ("自动派单".equals(orderModel)) {
					List<AreaSetting> list = this.getAreaList(city.getOrg(), // 根据地址与机构查询片区
							orderManager.getLarClientUserAddress().getId());
					if (null != list && list.size() > 0) {
						List<Salesman> salesmans = new ArrayList<>();
						Salesman salesman = null;
						for (AreaSetting areaSetting : list) {// 遍历片区,查询出所有片区的所有业务员
							salesmans.addAll(salesmanDao.getSalesmansByAreaId(areaSetting.getId()));
						}
						if (null != salesmans && salesmans.size() > 0) {
							Random random = new Random();
							int j = random.nextInt(salesmans.size());
							salesman = salesmans.get(j);
							orderManager.setAreaSetting(salesman.getAreaSetting());
							orderManager.setGrabOrder(21);
							orderManager.setOrderStatusId(3);
							orderManager.setOrderStatusName("服务中");
							orderManager.setSalesman(salesman);
							orderManager.setPlaceOrder(new Date());
						}
						// 转变保存日期时的格式
						orderManager.setMaaDate(orderManager.getMaaDate().replace('年', '-').replace('月', '-')
								.replaceAll("日", "").replaceAll("  ", " "));
						count = orderManagerDao.insertSelective(orderManager);

						if (count > 0 && null != salesman && null != salesman.getPersonnelId()) {
							// 推送
							String sysUser = salesman.getPersonnelId() + "";

							XingeEntity xingeEntity = new XingeEntity();
							xingeEntity.setTitle("有新的订单");
							xingeEntity.setContent("有新的订单");
							xingeEntity.setAccount(sysUser);
							xingeEntity.setGrabOrder(21);

							xingeAppUtils.pushSingleAccount(xingeEntity,2);
							xingeAppUtils.pushSingleAccountIOS(xingeEntity,4);
						}

					}

				} else if ("抢单".equals(orderModel)) {
					orderManager.setGrabOrder(31);

					List<AreaSetting> list = this.getAreaList(city.getOrg(),
							orderManager.getLarClientUserAddress().getId());
					StringBuffer areaList = new StringBuffer();// 存放用于抢单的地区
					for (int i = 0; i < list.size(); i++) {
						if (i < list.size() - 1) {
							areaList.append(list.get(i).getId() + ",");
						} else {
							areaList.append(list.get(i).getId() + "");
						}
					}
					orderManager.setAreaList(areaList.toString());
					List<Salesman> salesmans = salesmanDao.getSalesmansByAreaIds(list);
					// PersonnelId的集合，用来给指定业务员发推 送
					List<String> personnelIds = new ArrayList<>();
					for (Salesman salesman : salesmans) {
						if (!personnelIds.contains(salesman.getPersonnelId())) {
							personnelIds.add(salesman.getPersonnelId());
						}
					}
					// 转变保存日期时的格式
					orderManager.setMaaDate(orderManager.getMaaDate().replace('年', '-').replace('月', '-')
							.replaceAll("日", "").replaceAll("  ", " "));
					count = orderManagerDao.insertSelective(orderManager);

					if (count > 0) {
						// 根据城限制只推送该城市的业务员
						logger.info("给所有android 和 ios 发送消息");
						XingeEntity xingeEntity = new XingeEntity();
						xingeEntity.setTitle("有新的订单");
						xingeEntity.setContent("有新的订单");
						xingeEntity.setAccounts(personnelIds);
						xingeEntity.setGrabOrder(31);
						xingeAppUtils.pushAccountList(xingeEntity,2);
						xingeAppUtils.pushAccountListIOS(xingeEntity,4);
					}
				} else {
					// 转变保存日期时的格式
					orderManager.setMaaDate(orderManager.getMaaDate().replace('年', '-').replace('月', '-')
							.replaceAll("日", "").replaceAll("  ", " "));
					count = orderManagerDao.insertSelective(orderManager);
				}

				if (count > 0) {
					// 添加回收物照片
					List<RecyclingImg> recyclingImg = orderManager.getRecyclingImg();
					if (recyclingImg != null && recyclingImg.size() > 0) {
						for (RecyclingImg recyclingImg2 : recyclingImg) {
							if (recyclingImg2.getImgUrl() != null) {
								recyclingImg2.setId(String.valueOf(UUIDUtil.getUUNum()));
								recyclingImg2.setOrderManager(orderManager);
								orderManagerDao.insertSelectiveImg(recyclingImg2);
							}
						}
					}
					List<String> recyList = new ArrayList<String>();// 保存预约的商品ID，用于批量删除回收蓝
					// 添加子单号
					List<ChildOrders> childOrders = orderManager.getChildOrders();
					if (childOrders != null && childOrders.size() > 0) {
						for (ChildOrders childOrders2 : childOrders) {
							if (childOrders2.getRecyclingMaterial() != null
									&& childOrders2.getRecyclingMaterial().getId() != null) {
								childOrders2.setId(String.valueOf(UUIDUtil.getUUNum()));
								childOrders2.setOrderManager(orderManager);
								childOrders2.setEnable(0);
								childOrders2.setConfirmOrder(1);
								childOrders2.setCreateDate(date);
								orderManagerDao.insertSelectiveChild(childOrders2);
								recyList.add(childOrders2.getRecyclingMaterial().getId());
							}
						}
					}
					// 预约成功清空已预约的回收蓝商品,根据商品id和用户id删除
					if (orderManager.getAppUserId() != null && orderManager.getAppUserId().trim().length() > 0) {
						if (recyList != null && recyList.size() > 0) {
							String recyIds=StringUtils.join(recyList,',');
							String userId= orderManager.getAppUserId();
							List<String>ids=recyclingItemsDao.findByUserAndRecy(userId,recyIds);
							recyclingItemsDao.deleteRecoveryBluesByIds(ids);
						}
					}
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("OrderManager is null");
		}
	}

	/**
	 * 
	 * @param org
	 *            机构id
	 * @param addressId
	 * @return 片区集合
	 */
	public List<AreaSetting> getAreaList(Long org, String addressId) {
		List<AreaSetting> areas = new ArrayList<>();

		List<Long> ids = new ArrayList<>();
		ids.add(org);
		List<AreaSetting> areaSettings = areaSettingDao.selectAreaById(org + "");
		LarClientUserAddress larClientUserAddress;
		try {
			larClientUserAddress = larClientUserAddressService.selectByPrimaryKey(addressId);
			if (null != larClientUserAddress) {
				String latitude = larClientUserAddress.getLatitude();
				String longitude = larClientUserAddress.getLongitude();
				Point2D.Double aDouble = new Point2D.Double(Double.parseDouble(latitude),
						Double.parseDouble(longitude));
				if (null != areaSettings && areaSettings.size() > 0) {
					for (AreaSetting item : areaSettings) {
						String position = item.getAreaPosition();
						if (StringUtils.isNotBlank(position)) {
							List<Map<String, Double>> parse = JSON.parse(position, List.class);
							List<Point2D.Double> doubleList = new ArrayList<>();
							if (null != parse && parse.size() > 0) {
								for (Map<String, Double> map1 : parse) {
									doubleList.add(new Point2D.Double(map1.get("lat"), map1.get("lng")));
								}
							}
							boolean flag = MapUtil.checkWithJdkGeneralPath(aDouble, doubleList);
							if (flag) {
								areas.add(item);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return areas;
	}

	// 添加子单号
	@Transactional
	public boolean insertSelective(ChildOrders childOrders) throws Exception {
		if (childOrders != null) {
			int count = 0;
			try {
				childOrders.setId(String.valueOf(UUIDUtil.getUUNum()));
				childOrders.setCreateDate(new Date());
				childOrders.setEnable(0);
				childOrders.setConfirmOrder(1);
				count = orderManagerDao.insertSelectiveChild(childOrders);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("childOrders is null");
		}
	}

	@Transactional(readOnly = true)
	public LarPager<OrderManager> selectByExample(LarPager<OrderManager> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<OrderManager>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if (params == null || params.size() <= 0) {
				throw new IllegalArgumentException("params is error");
			}
			if (!params.containsKey("mechanismId") &&!params.containsKey("orgIds")) {
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (!params.containsKey("orderStatusId") && !params.containsKey("orderStatusIds")) {
				throw new IllegalArgumentException("params orderStatusId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = 0;
//				if (params.containsKey("orderStatusIds")) {
//					totalCount = orderManagerDao.countByIds(params);
//				} else {
//					totalCount = orderManagerDao.countById(String.valueOf(params.get("mechanismId")),
//							String.valueOf(params.get("orderStatusId")));
//				}
				totalCount = orderManagerDao.countByIds(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<OrderManager> result = orderManagerDao.selectByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Transactional(readOnly = true)
	public LarPager<OrderManager> getTransactions(LarPager<OrderManager> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<OrderManager>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if (params == null || params.size() <= 0) {
				throw new IllegalArgumentException("params is error");
			}
			if (!params.containsKey("mechanismId") && !params.containsKey("orgIds")) {
				throw new IllegalArgumentException("params mechanismId is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = 0;
				totalCount = orderManagerDao.countTransaction(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<OrderManager> result = orderManagerDao.selectTransaction(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	// 查询子单号
	@Transactional(readOnly = true)
	public LarPager<ChildOrders> selectCildByExample(LarPager<ChildOrders> pager) throws Exception {
		if (pager == null) {
			pager = new LarPager<ChildOrders>();
		}
		try {
			if (StringUtils.isEmpty(pager.getOrderBy())) {
				// 多个用逗号
				pager.setOrderBy("createDate");
			}
			if (StringUtils.isEmpty(pager.getOrder())) {
				// 多个用逗号
				pager.setOrder("desc");
			}
			Map<String, Object> params = pager.getParams();
			if (params != null && params.size() > 0) {
				if (!params.containsKey("orderId")) {
					throw new IllegalArgumentException("orderId is error");
				}
				;
			} else {
				throw new IllegalArgumentException("params is error");
			}
			if (pager.isAutoCount()) {
				long totalCount = orderManagerDao.countByChildId(params);
				pager.setTotalCount(totalCount);
				if (totalCount <= 0) {
					return pager;
				}
			}
			List<ChildOrders> result = orderManagerDao.selectCildByExample(pager);
			pager.setResult(result);
		} catch (Exception e) {
			throw e;
		}
		return pager;
	}

	@Transactional(readOnly = true)
	public OrderManager selectByPrimaryKey(Map<String, Object> params) throws Exception {
		if (params != null && params.size() > 0) {
			try {
				OrderManager orderManager = orderManagerDao.selectOrderByExample(params);
				return orderManager;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IllegalArgumentException("id is error");
		}
	}

	@Transactional
	public boolean updateByExampleSelective(OrderManager orderManager) throws Exception {
		if (orderManager != null && orderManager.getId() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderManager", orderManager);
			// 修改订单信息
			int count = orderManagerDao.updateByExampleSelective(params);
			// 修改个人信息
			params.clear();
			params.put("larClientUserAddress", orderManager.getLarClientUserAddress());
			count = larClientUserAddressDao.updateByExampleSelective(params);
			// 修改图片,删除原来的
			count = orderManagerDao.deleteRecyclingImgById(orderManager.getId());
			// 从新给订单添加
			List<RecyclingImg> recyclingImg = orderManager.getRecyclingImg();
			if (recyclingImg != null && recyclingImg.size() > 0) {
				for (RecyclingImg recyclingImg2 : recyclingImg) {
					if (recyclingImg2.getImgUrl() != null) {
						recyclingImg2.setId(String.valueOf(UUIDUtil.getUUNum()));
						recyclingImg2.setOrderManager(orderManager);
						orderManagerDao.insertSelectiveImg(recyclingImg2);
					}
				}
			}
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new IllegalArgumentException("orderManager or id is error");
		}
	}

	// 修改子单号
	@Transactional
	public boolean updateByExampleSelective(ChildOrders childOrders) throws Exception {
		try {
			if (childOrders != null && childOrders.getId() != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("childOrders", childOrders);
				// 修改子单号信息
				int count = orderManagerDao.updateChildByExampleSelective(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new IllegalArgumentException("childOrders or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<RecyclingType> getRecyclingTypes() {
		return orderManagerDao.getRecyclingTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecyclingMaterial> getRecyclingNames(String id) {
		return orderManagerDao.getRecyclingNames(id);
	}

	@Override
	@Transactional
	public boolean cancelOrderById(Map<String, Object> params) {
		try {
			int count = orderManagerDao.cancelOrderById(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean comeOrderById(Map<String, Object> updateParams) {
		try {
			updateParams.put("takeDate", new Date());
			int count = orderManagerDao.comeOrderById(updateParams);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public List<Salesman> getSalesmansByAreaId(String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<Salesman> salesmans = orderManagerDao.getSalesmansByAreaId(id);
				return salesmans;
			} else {
				throw new IllegalArgumentException("area or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Evaluate> getEvaluates() throws Exception {
		try {
			List<Evaluate> evaluates = orderManagerDao.getEvaluates();
			return evaluates;
		} catch (Exception e) {
			throw e;
		}
	}

	// 派单
	@Override
	@Transactional
	public boolean dispatchOrder(Map<String, Object> params) throws Exception {
		try {
			int count = orderManagerDao.updateDispatchOrder(params);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean cancelDispatchOrder(Map<String, Object> params) throws Exception {
		try {
			if (params != null && params.size() > 0) {
				int count = orderManagerDao.cancelDispatchOrder(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Override
	@Transactional
	public boolean cancelSendOrder(Map<String, Object> updateParams) throws Exception {
		try {
			if (updateParams != null && updateParams.size() > 0) {
				int count = orderManagerDao.cancelSendOrder(updateParams);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Salesman> getSalesmansBymechanismId(String id) throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				List<Salesman> salesmans = orderManagerDao.getSalesmansBymechanismId(id);
				return salesmans;
			} else {
				throw new IllegalArgumentException("area or id is error");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public boolean confirmOrder(Map<String, Object> updateParams) throws Exception {
		try {
			if (updateParams != null && updateParams.size() > 0) {
				int count = orderManagerDao.confirmOrder(updateParams);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Override
	@Transactional
	public boolean confirmOrders(Map<String, Object> updateParams) throws Exception {
		try {
			if (updateParams != null && updateParams.size() > 0) {
				int count = orderManagerDao.confirmOrders(updateParams);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Override
	@Transactional
	public boolean appConfirmOrder(Map<String, Object> updateParams, OrderManager orderManager) throws Exception {
		try {
			if (updateParams != null && updateParams.size() > 0) {
				int count = orderManagerDao.appConfirmOrder(updateParams);
				if (count > 0) {
					// 操作子单
					List<ChildOrders> childOrders = orderManager.getChildOrders();
					for (ChildOrders childOrders2 : childOrders) {
						childOrders2.setId(String.valueOf(UUIDUtil.getUUNum()));
						childOrders2.setCreateDate(new Date());
						childOrders2.setEnable(0);
						childOrders2.setConfirmOrder(0);
						count = orderManagerDao.insertSelectiveChild(childOrders2);
						/*
						 * if(childOrders2.getId()==null ||
						 * childOrders2.getId().length()<=0){
						 * //如果是修改了原来子单的信息，让app提交的时候把子单ID去掉，按新子单添加
						 * childOrders2.setId(String.valueOf(UUIDUtil.getUUNum()
						 * )); childOrders2.setCreateDate(new Date());
						 * childOrders2.setEnable(0);
						 * childOrders2.setConfirmOrder(0); count =
						 * orderManagerDao.insertSelectiveChild(childOrders2);
						 * }else{ //已有子单，改变状态 Map<String, Object> params = new
						 * HashMap<String, Object>();
						 * params.put("id",childOrders2.getId());
						 * params.put("confirmOrder",
						 * childOrders2.getConfirmOrder()); count =
						 * orderManagerDao.childOrderConfirm(params); }
						 */
					}
					// 业务员消费的积分
					Object integral = updateParams.get("integral");
					if (integral != null && !"0".equals(integral)) {
						// TODO orderManagerDao
						long count1 = orderManagerDao.upateIntegralByOrderId(orderManager.getOrderId(),
								Integer.valueOf(integral + ""));
						count = salesmanDao.updateSalesmanPoints(String.valueOf(integral),
								orderManager.getSalesman().getId());
						Map<String, Object> params = new HashMap<>();
						params.put("id", orderManager.getId());
						OrderManager manager = orderManagerDao.selectOrderByExample(params);
						String content = "亲,您的回收已成功,恭喜您获得" + orderManager.getIntegral() + "积分";
						MessageCenter mc = new MessageCenter();
						mc.setId(UUIDUtil.getUUNum());
						mc.setTitle("回收消息");
						mc.setDetail(content);
						mc.setCustomerId(Long.valueOf(manager.getAppUserId()));
						mc.setCreateDate(new Date());
						messageCenterDao.save(mc);

						XingeEntity xingeEntity = new XingeEntity();
						xingeEntity.setTitle("积分");
						xingeEntity.setContent(content);
						xingeEntity.setAccount(manager.getAppUserId());
						xingeAppUtils.pushSingleAccount(xingeEntity,1);
						xingeAppUtils.pushSingleAccountIOS(xingeEntity,3);
					}
					// 减掉业务员消费的积分
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Override
	@Transactional
	public boolean childOrderConfirm(ChildOrders childOrders) throws Exception {
		try {
			if (childOrders != null && childOrders.getId() != null) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", childOrders.getId());
				params.put("confirmOrder", childOrders.getConfirmOrder());
				int count = orderManagerDao.childOrderConfirm(params);
				if (count > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	// 根据用户ID查询已完成订单
	@Override
	@Transactional(readOnly = true)
	public List<UserOrderCount> getOrderByAppUserId() throws Exception {
		try {
			List<UserOrderCount> orderByAppUserId = orderManagerDao.getOrderByAppUserId();
			return orderByAppUserId;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderManager> getConfirmationPersonById(String id) throws Exception {
		try {
			return orderManagerDao.getConfirmationPersonById(id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public int queryOrderCount(String employeeId) throws Exception {
		try {
			// 根据登陆人员的工号查询业务员
			List<String> ids = orderManagerDao.getSalesmansByCustomerIds(employeeId);
			if (ids == null || ids.size() < 1) {
				return 0;
			} else {
				// 根据业务员主键查询派单状态的订单数量
				return orderManagerDao.selectDisCount(ids);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderManager> getOrderByCustomerId(String customerId) throws Exception {
		try {
			// 根据登陆人员的工号查询业务员
			List<String> ids = orderManagerDao.getSalesmansByCustomerIds(customerId);
			// String id = customerId;
			if (ids == null || ids.size() < 1) {
				return null;
			} else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("salesman", ids);
				params.put("orderStatusId", 3);
				params.put("orderBy", "o.maaDate ASC ,o.placeOrder ASC");
				// 根据业务员主键查询服务中的订单
				List<OrderManager> orderByCustomerId = orderManagerDao.getOrderByCustomerIds(params);
				return orderByCustomerId;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderManager> getOrderDetails(String customerId) throws Exception {
		try {
			// 根据登陆人员的工号查询业务员
			List<String> ids = orderManagerDao.getSalesmansByCustomerIds(customerId);
			if (ids == null || ids.size() < 1) {
				return null;
			} else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("salesman", ids);
				List<Object> orderStatusIds = new ArrayList<Object>();
				orderStatusIds.add("0");
				orderStatusIds.add("4");
				params.put("orderStatusIds", orderStatusIds);
				params.put("orderBy", "o.finishDate desc");
				// 根据业务员主键查询服务中的订单
				List<OrderManager> orderByCustomerId = orderManagerDao.getOrderByCustomerIds(params);
				return orderByCustomerId;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MyPoints> getOrderByUserId(String userId) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			List<MyPoints> myPoints = orderManagerDao.getOrderByUserId(params);
			List<MyPoints> myPoints2 = recycleOrderDao.getOrderByUserId(params);
			List<MyPoints> myPoints3 = larEventDao.getOrderByUserId(params);
			
			Iterator<MyPoints> iterator = myPoints.iterator();
			while(iterator.hasNext()){
				 MyPoints next = iterator.next();
				 //TODO whs设置为1,表示预约回收,  以后会改动
				 next.setOrderSourceId(1);
				 if(next.getPaymentTypeId() == 2){
					 iterator.remove();
				 }
			}

			Iterator<MyPoints> iterator2 = myPoints2.iterator();
			while(iterator2.hasNext()){
				 MyPoints next = iterator2.next();
				 next.setOrderSourceId(5);//  回收类型   5 卖废品
				 if(next.getPaymentTypeId() == 2){
					 iterator2.remove();
				 }
			}
			myPoints.addAll(myPoints2);
			myPoints.addAll(myPoints3);
			MyPoints mPoints = null;
			
			if(null !=myPoints && myPoints.size()>1){
			//排序
				for (int i = 0; i < myPoints.size(); i++) {
					for(int j = i+1; j < myPoints.size(); j++){
						if(myPoints.get(i).getFinishDate() .before(myPoints.get(j).getFinishDate())  ){
							mPoints=myPoints.get(i);
							myPoints.set(i,myPoints.get(j));
							myPoints.set(j,mPoints);
						}
					}
				}
			}
			return myPoints;
		} catch (Exception e) {
			throw e;
		}
	}

	// 设定几位数
	private static final int LENGTH = 8;

	/**
	 * * 这是典型的随机洗牌算法。 * 流程是从备选数组中选择一个放入目标数组中，将选取的数组从备选数组移除（放至最后，并缩小选择区域） *
	 * 算法时间复杂度O（n） * @return 随机8为不重复数组
	 */
	public static String generateNumber() {
		String no = "";
		// 初始化备选数组
		int[] defaultNums = new int[10];
		for (int i = 0; i < defaultNums.length; i++) {
			defaultNums[i] = i;
		}
		Random random = new Random();
		int[] nums = new int[LENGTH];
		// 默认数组中可以选择的部分长度
		int canBeUsed = 10;
		// 填充目标数组
		for (int i = 0; i < nums.length; i++) {
			// 将随机选取的数字存入目标数组
			int index = random.nextInt(canBeUsed);
			nums[i] = defaultNums[index];
			// 将已用过的数字扔到备选数组最后，并减小可选区域
			swap(index, canBeUsed - 1, defaultNums);
			canBeUsed--;
		}
		if (nums.length > 0) {
			for (int i = 0; i < nums.length; i++) {
				no += nums[i];
			}
		}
		Calendar cal = Calendar.getInstance();// 使用日历类
		String year = String.valueOf(cal.get(Calendar.YEAR));// 得到年
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);// 得到月，因为从0开始的，所以要加1
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));// 得到天
		if (month.length() < 2) {
			month = "0" + month;
		}
		if (day.length() < 2) {
			day = "0" + day;
		}
		return String.valueOf(year + month + day + no);
	}

	/**
	 * 交换方法
	 *
	 * @param i
	 *            交换位置
	 * @param j
	 *            互换的位置
	 * @param nums
	 *            数组
	 */
	private static void swap(int i, int j, int[] nums) {
		int temp = nums[i];
		nums[i] = nums[j];
		nums[j] = temp;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderManager> detail(Map<String, Object> map) {
		return orderManagerDao.getOrderByCustomerId(map);
	}

	@Override
	@Transactional
	public boolean updateEvaluation(Long aLong, Integer integer) {
		int count = orderManagerDao.updateEvaluation(aLong, integer);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Long totalCount(Map<String, Object> params) {
		return orderManagerDao.totalCount(params);
	}

	@Override
	public LarPager<OrderManager> selectOrderByCustomerId(LarPager<OrderManager> larPager) throws Exception {
		Map<String, Object> params = larPager.getParams();
		params.put("first", larPager.getFirst());
		params.put("pageSize", larPager.getPageSize());
		params.put("orderBy", "o.placeOrder DESC, o.maaDate DESC");
		List<OrderManager> list = orderManagerDao.getOrderByCustomerId(params);

		Long count = orderManagerDao.totalCount(params);
		larPager.setTotalCount(count);
		larPager.setResult(list);
		return larPager;
	}

	@Autowired
	private SalesmanService salesmanService;

	/**
	 * 
	 * @param grabParams
	 *            id:订单id userId:用户id distributeIllustrate：派单说明 flag:过期/抢单
	 *            grab_order:抢单状态
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean grabOrder(Map<String, Object> grabParams, Map<String, Object> condition) throws Exception {
		try {

			Map<String, Object> params = new HashMap<>();
			OrderManager orderManager = new OrderManager();
			params.put("orderManager", orderManager);

			orderManager.setId(grabParams.get("ids") + "");
			// params.put("id", grabParams.get("id"));
			if (StringUtils.isNotBlank(grabParams.get("grab_order") + "")) {
				orderManager.setGrabOrder(Integer.parseInt(grabParams.get("grab_order") + ""));
			}
			// params.put("grab_order", grabParams.get("grab_order"));

			if ("过期".equals(grabParams.get("flag"))) {

				return orderManagerDao.updateByExampleSelective(params) > 0;

			} else if ("抢单".equals(grabParams.get("flag"))) {

				//Date grab_order_time = new Date();
				Date grab_order_time = (Date)grabParams.get("grab_order_time");
				orderManager.setGrabOrderTime(grab_order_time);
				// params.put("grab_order_time", grab_order_time);

				int orderStatusId = 3;
				orderManager.setOrderStatusId(orderStatusId);
				// params.put("orderStatusId", orderStatusId);

				String orderStatusName = "服务中";
				orderManager.setOrderStatusName(orderStatusName);
				// params.put(orderStatusName, orderStatusName);
				//抢单时效
				if(grabParams.get("grabOrderMin") != null){
					Long timeShiXiao = Long.valueOf(grabParams.get("grabOrderMin")+"");
					orderManager.setGrabOrderMin(timeShiXiao);
				}
				// 用户ID转变为业务员ID，业务员Name
				String userId = grabParams.get("userId") + "";
				List<Salesman> salesmans = salesmanService.getByPersonnelId(userId);

				if (null != salesmans && salesmans.size() > 0) {
					Salesman salesman = salesmans.get(0);

					// 接单人
					orderManager.setSalesman(salesman);
					// orderManager.setUserId(salesman.getId());
					// orderManager.setUserName(salesman.getManName());
					orderManager.setAreaSetting(salesman.getAreaSetting());

					// 派单人

					// params.put("userId", salesman.getId());
					// params.put("userName", salesman.getManName());

					String distributeIllustrate = null;
					// 转换备注
					if (null != grabParams.get("task_explain") && !"".equals(grabParams.get("task_explain"))) {
						distributeIllustrate = grabParams.get("task_explain") + "";
						// params.put("distributeIllustrate",distributeIllustrate);
						orderManager.setDistributeIllustrate(distributeIllustrate);
					}

				} else {
					return false;
				}

				int num = orderManagerDao.updateGrab(params, condition);

				return num > 0;

			} else {
				throw new RuntimeException("flag 参数有误");
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateGrabState(Long time) {
		int bizType = 4;
		logger.info("method:{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		int lockflag = tableLockDao.lock(bizType);// 获取锁
		if (lockflag == 0) {// 获取不到锁，直接返回
			logger.info("method:{},卖废品未获取到锁", Thread.currentThread().getStackTrace()[1].getMethodName());
			return false;
		}else{
			logger.info("method:{},卖废品取得锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		try {
			int count = orderManagerDao.updateGrabState(time);
			logger.info("method:{},更新:{}条数据状态", Thread.currentThread().getStackTrace()[1].getMethodName(), count);
			return count > 0 ? true : false;
		} finally {
			tableLockDao.unLock(bizType);// 解锁
			logger.info("method:{},卖废品解锁", Thread.currentThread().getStackTrace()[1].getMethodName());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public OrderManager getOrderManagerByOrderId(String orderId) {
		Map<String, Object> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("grabOrder", false);

		return orderManagerDao.getOrderByCustomerId(params).get(0);
	}

	@Override
	public int updateCheckStatus(OrderManager orderManager) {
		// TODO Auto-generated method stub
		return orderManagerDao.updateCheckStatus(orderManager);
	}
	@Override
	public List<OrderManager> findInvalidOrder(Long time) {
		return orderManagerDao.findInvalidOrder(time, new Date());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean batchUpdate(List<OrderManager> list) {
		List<String> accounts = new ArrayList<>();
		for (OrderManager order : list) {
			if(null != order.getSendSingleId()){
				order.setDistributeDate(new Date());
				order.setGrabOrder(33);
				order.setOrderStatusId(3);
				order.setOrderStatusName("服务中");
				accounts.add(order.getSendSingleId()+"");
			}else{
				order.setGrabOrder(33);
			}
		}
		int update = orderManagerDao.batchUpdate(list);
		if(update > 0 && accounts.size()>0){
			XingeEntity xingeEntity = new XingeEntity();
			xingeEntity.setTitle("有新的订单");
			xingeEntity.setContent("有新的订单");
			xingeEntity.setGrabOrder(21);
			xingeEntity.setAccounts(accounts);
			xingeAppUtils.pushAccountList(xingeEntity,2);
			xingeAppUtils.pushAccountListIOS(xingeEntity,4);
		}
		return update > 0;
	}

	@Override
	@Transactional(readOnly=true)
	public List<OrderManager> getConfirmationPersonById2(List<Long> orgIds) {
		try {
			return orderManagerDao.getConfirmationPersonById2(orgIds);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Long> getCheckMens(LarPager<OrderManager> larPager) {
		return orderManagerDao.getCheckMens(larPager);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Salesman> getSalesmansBymechanismId2(List<Long> orgIds) {
		try {
			return orderManagerDao.getSalesmansBymechanismId2(orgIds);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public String getByCustomerId(String next_sales_man) {
		try {
			return orderManagerDao.getByCustomerId(next_sales_man);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(readOnly=false)
	public boolean updateSalesmanId(Map<String, Object> result) {
		try {
			
			return orderManagerDao.updateSalesmanId(result);
		} catch (Exception e) {
			throw e;
			
		}
	}

	@Override
	public Date selectPlaceOrder(String orderId) {
		try {
			return orderManagerDao.selectPlaceOrder(orderId);
		} catch (Exception e) {
			throw e;
			
		}
	}
}
