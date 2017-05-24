package com.sdcloud.web.lar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.ChildOrders;
import com.sdcloud.api.lar.service.ArriveGoodsServive;
import com.sdcloud.api.lar.service.OrderManagerService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * 
 * @author luorongjie
 *
 */

@RestController
@RequestMapping("/api/arriveGoods")
public class ArriveGoodsController {

	/*@Autowired
	private ArriveGoodsServive arriveGoodsServive;
	*/
	@Autowired
	private OrderManagerService orderManagerService;
	
	
	// 查询列表
	@RequestMapping("/getChildOrders")
	@ResponseBody
	public ResultDTO getChildOrders(@RequestBody(required = false) LarPager<ChildOrders> larPager)throws Exception {
		try {
			larPager = orderManagerService.selectCildByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
}
