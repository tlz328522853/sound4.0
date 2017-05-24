package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sdcloud.api.lar.entity.DistributeExpress;
import com.sdcloud.api.lar.service.DistributeExpressService;
import com.sdcloud.biz.lar.dao.DistributeExpressDao;
import com.sdcloud.framework.common.UUIDUtil;

@Service
public class DistributeExpressImpl extends BaseServiceImpl<DistributeExpress> implements DistributeExpressService{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DistributeExpressDao distributeExpressDao;
	
	@Transactional(readOnly=false)
	public Map<String, Object> batchSave(List<DistributeExpress> distributes){
		try {
			int count = 0;
			Map<String, Object> map = new HashMap<>();
			List<String> orderNos = new ArrayList<>();
			boolean flage = false;
			//检查运单号是否为空
			for (DistributeExpress de : distributes) {
				de.setId(UUIDUtil.getUUNum());
				de.setReveiveTime(new Date());
				if(de.getOrderNo() != null){
					orderNos.add(de.getOrderNo());
				}else{
					flage = true;
				}
			}
			if(flage){
				map.put("code", 1050404);
				map.put("flage", false);
				map.put("message", "单号不能为空!");
				return map;
			}
			//判断运单号是否存在
			Map<String, Object> exist = this.isExistOrderNo(orderNos);
			if((boolean)exist.get("flage")){
				map.put("code", 1050403);
				map.put("flage", false);
				map.put("message", exist.get("message")+"单号重复,不能保存");
				return map;
			}
			count = distributeExpressDao.batchSave(distributes);
			if(count > 0){
				map.put("flage", true);
				map.put("message", "批量添加成功");
			}else{
				map.put("code", 10504);
				map.put("flage", false);
				map.put("message", "批量添加失败!");
			}
			return map;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		
	}
	public Map<String, Object> isExistOrderNo(List<String> orderNos){
		Map<String, Object> map = new HashMap<>();
		List<String> orders = new ArrayList<>();
		boolean flage;
		List<DistributeExpress> list = distributeExpressDao.findByOrderNos(orderNos);
		if(CollectionUtils.isNotEmpty(list)){
			flage = true;
			for (DistributeExpress de : list) {
				orders.add(de.getOrderNo());
			}
		}else{
			flage = false;
		}
		map.put("message", orders);
		map.put("flage", flage);
		return map;
	}
	@Override
	@Transactional(readOnly=false)
	public Map<String, Object> updateByOrderNo(DistributeExpress distribute) {
		Map<String, Object> map = new HashMap<>();
		if(null == distribute.getOrderNo()){
			map.put("code", 1050404);
			map.put("flage", false);
			map.put("message","单号不能为空!");
			return map;
		}
		DistributeExpress express = distributeExpressDao.findByOrderNo(distribute.getOrderNo());
		int count = 0;
		if(null == express){
			map.put("code", 1050401);
			map.put("flage", false);
			map.put("message","请输入正确的订单号!");
		}else if(express.getSignStatus() == 1){
			map.put("code", 1050402);
			map.put("flage", false);
			map.put("message","签收失败,订单已签收过!");
		}else{
			distribute.setSignStatus(1);
			distribute.setSignTime(new Date());
			count = distributeExpressDao.updateByOrderNo(distribute);
			if(count > 0){
				map.put("flage", true);
				map.put("message","签收成功");
			}else{
				map.put("code", 10504);
				map.put("flage", false);
				map.put("message","签收失败!");
			}
		}
		return map;
	}
	@Override
	@Transactional(readOnly=false)
	public Map<String, Object> batchSave(DistributeExpress distributes) throws Exception {
		Map<String,Object> map = new HashMap<>();
		List<String> orderNos = distributes.getOrderNos();
		List<DistributeExpress> result = new ArrayList<>();
		int count = 0;
		List<String> list = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(orderNos)){
			map = this.isExistOrderNo(orderNos);
			list = (List)map.get("message");
			for (String orderNo : orderNos) {
				if(!list.toString().contains(orderNo)){
					DistributeExpress de = distributes.clone();
					de.setId(UUIDUtil.getUUNum());
					de.setOrderNo(orderNo);
					de.setReveiveTime(new Date());
					de.setCreateDate(new Date());
					result.add(de);
				}
			}
			//判断非空, 否自报错
			if(CollectionUtils.isNotEmpty(result)){
				count = distributeExpressDao.batchSave(result);
			}
		}else{
			map.put("code", 1050404);
			map.put("flage", false);
			map.put("message", "单号不能为空!");
			return map;
		}
		//定义重复单号的信息
		Map<String, Object> message = new HashMap<>();
		message.put("count",count);
		message.put("reOrderNo", list);
		if(count >= 0){
			map.put("flage", true);
			map.put("message", "批量添加成功");
			map.put("data", message);
			return map;
		}
		map.put("code", 10504);
		map.put("flage", false);
		map.put("message", "批量添加失败!");
		return map;
	}
	
	@Override
	public List<String> queryByOrderNos(List<String> orderNos) throws Exception {
		
		return distributeExpressDao.queryByOrderNos(orderNos);
	}
	@Override
	public List<DistributeExpress> getByIds(String ids) {
		List<Long> list = new ArrayList<>();
        if (ids != null && !"".equals(ids)) {
            String[] strs = ids.split(",");
            for (String str : strs) {
                list.add(Long.valueOf(str));
            }
        }
        return distributeExpressDao.getByIds(list);
	}
}
