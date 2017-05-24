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

import com.sdcloud.api.envsanitation.entity.AssetWc;
import com.sdcloud.api.envsanitation.service.AssetWcService;
import com.sdcloud.framework.entity.Pager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class AssetWcServciceImplTest {
	
	@Autowired
	private AssetWcService assetWcService;
	
	@Test
	public void insert(){
		AssetWc assetWc = new AssetWc();
		assetWc.setAssetTypeId(2099L);
		assetWc.setAssetName("公厕");
		assetWc.setNewOrgId(2L);
		
		assetWc.setPosition("大望路");
		
		try {
//			assetWcService.insert(assetWc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delete(){

		List<Long> assetIds = new ArrayList<Long>();
		assetIds.add(6158574211318441L);
		assetIds.add(3910399876940484L);
		
		try {
			assetWcService.delete(assetIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		AssetWc assetWc = new AssetWc();
		assetWc.setAssetId(6158574211318441L);
		assetWc.setAssetName("公厕22");
		assetWc.setPosition("大望路22");
		
		try {
			assetWcService.update(assetWc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findBy(){
		Pager<AssetWc> pager = new Pager<AssetWc>();
		pager.setPageNo(1);
		pager.setPageSize(2);
		
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("position", "街");
//		map.put("dustbinNumber", "垃圾箱009");
		map.put("newOrgId", "2");
		
		try {
			pager = assetWcService.findBy(pager, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("pager: " + pager);
	}
	
	@Test
	public void findById() throws Exception {
		AssetWc assetWc = assetWcService.findById(8439100662300807L);
		System.out.println(assetWc);
	}
	
}
