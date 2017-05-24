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

import com.sdcloud.api.envsanitation.entity.AssetDustbin;
import com.sdcloud.api.envsanitation.service.AssetDustbinService;
import com.sdcloud.framework.entity.Pager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:test-bean-context.xml")
public class AssetDustbinServciceImplTest {
	
	@Autowired
	private AssetDustbinService assetDustbinService;
	
	@Test
	public void insert(){
		AssetDustbin assetDustbin = new AssetDustbin();
		assetDustbin.setAssetTypeId(1073L);
		assetDustbin.setAssetName("垃圾箱");
		assetDustbin.setNewOrgId(103L);
		
		assetDustbin.setDustbinNumber("垃圾箱009");
		
		try {
//			assetDustbinService.insert(assetDustbin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void delete(){

		List<Long> assetIds = new ArrayList<Long>();
		assetIds.add(3326963758905914L);
		assetIds.add(318730404081745L);
		
		try {
			assetDustbinService.delete(assetIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		AssetDustbin assetDustbin = new AssetDustbin();
		assetDustbin.setAssetId(843605141468216L);
		assetDustbin.setAssetName("垃圾箱");
		assetDustbin.setAssetTypeId(1073L);
		assetDustbin.setDustbinNumber("垃圾箱002");
		
		try {
			assetDustbinService.update(assetDustbin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findBy(){
		Pager<AssetDustbin> pager = new Pager<AssetDustbin>();
		pager.setPageNo(1);
		pager.setPageSize(2);
		
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("position", "街");
//		map.put("dustbinNumber", "垃圾箱009");
		map.put("newOrgId", "7");
		
		try {
			pager = assetDustbinService.findBy(pager, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("pager: " + pager);
	}
	
	@Test
	public void findById() throws Exception {
		AssetDustbin assetDustbin = assetDustbinService.findById(694962907885912L);
		System.out.println(assetDustbin);
	}
	
}
