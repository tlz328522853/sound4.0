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

import com.sdcloud.api.envsanitation.entity.AssetStation;
import com.sdcloud.api.envsanitation.service.AssetStationService;
import com.sdcloud.framework.entity.Pager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class AssetStationServciceImplTest {
	
	@Autowired
	private AssetStationService assetStationService;
	
	@Test
	public void insert(){
		AssetStation assetStation = new AssetStation();
		assetStation.setAssetTypeId(5287L);
		assetStation.setAssetName("中转站");
		assetStation.setNewOrgId(7L);
		
		assetStation.setPosition("惠新西街北口");
		
		try {
//			assetStationService.insert(assetStation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delete(){

		List<Long> assetIds = new ArrayList<Long>();
		assetIds.add(6427409049012830L);
		
		try {
			assetStationService.delete(assetIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		AssetStation assetStation = new AssetStation();
		assetStation.setAssetId(8415773559637243L);
		assetStation.setAssetName("中转站22");
		assetStation.setPosition("惠新西街北口22");
		assetStation.setResponsibility("北京朝阳区惠新西街北口");
		
		try {
			assetStationService.update(assetStation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findBy(){
		Pager<AssetStation> pager = new Pager<AssetStation>();
		pager.setPageNo(1);
		pager.setPageSize(2);
		
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("position", "街");
//		map.put("dustbinNumber", "垃圾箱009");
		map.put("newOrgId", "7");
		
		try {
			pager = assetStationService.findBy(pager, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("pager: " + pager);
	}
	
	@Test
	public void findById() throws Exception {
		AssetStation assetStation = assetStationService.findById(8415773559637243L);
		System.out.println(assetStation);
	}
	
}
