package com.sdcloud.biz.envsanitation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.envsanitation.entity.AssetCar;
import com.sdcloud.api.envsanitation.service.AssetCarService;
import com.sdcloud.framework.entity.Pager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class AssetCarServciceImplTest {
	
	@Autowired
	private AssetCarService assetCarService;
	
	@Test
	public void insert(){
		AssetCar assetCar = new AssetCar();
		assetCar.setAssetTypeId(22L);
		assetCar.setAssetName("车辆");
		assetCar.setNewOrgId(103L);
		
		assetCar.setCarName("洒水车");
		
		try {
//			assetCarService.insert(assetCar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delete(){

		List<Long> assetIds = new ArrayList<Long>();
		assetIds.add(1L);
		assetIds.add(2450126308917998L);
		
		try {
			assetCarService.delete(assetIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		AssetCar assetCar = new AssetCar();
		assetCar.setAssetId(2036860284154523L);
		assetCar.setAssetName("车辆2");
		assetCar.setCarName("清扫车");
		
		try {
			assetCarService.update(assetCar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findBy(){
		Pager<AssetCar> pager = new Pager<AssetCar>();
		pager.setPageNo(1);
		pager.setPageSize(2);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newOrgId", 103L);
		map.put("carType", 2L);
		map.put("carNumber", "京AH");
		map.put("stopSign", 0);
		
		try {
			pager = assetCarService.findBy(pager, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("pager: " + pager);
	}
	
}
