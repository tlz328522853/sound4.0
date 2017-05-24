package com.sdcloud.biz.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.api.cache.redis.service.HashOperationsService;
import com.sdcloud.api.cache.redis.service.ModuleDefineService;
import com.sdcloud.api.cache.redis.service.ValueOperationsService;

/**
 * junit测试类
 * @author jzc
 * 2016年9月13日
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合 
@ContextConfiguration(locations={"classpath:conf/test-bean-context.xml"}) // 加载配置
public class TestDubbo {
	
	@Autowired // 注入
	private ValueOperationsService valueOperationsService;
	@Autowired // 注入
	private HashOperationsService hashOperationsService;
	@Autowired
	private ModuleDefineService moduleDefineService;

	
	@Test
	public void testSayHello(){
		Map<String, String> maps=new HashMap<>();
		maps.put("fileName", "测试文件");
		maps.put("userTotalSize", "100");
		maps.put("userReadSize", "60");
		maps.put("userSuccessSize","50");
		maps.put("userErrorSize", "10");
		maps.put("beginTime", "500000");
		maps.put("currentTime", "6500000");
		maps.put("readStatus", "1");
		
		hashOperationsService.putAll("O2O_SHIPMENT:EXPORT:222", maps);
		Boolean flag=hashOperationsService.getOperations_expire(
				"O2O_SHIPMENT:EXPORT:222",
				30000, TimeUnit.MILLISECONDS);
		System.out.println(flag);
//		maps=hashOperationsService.entries("O2O_SHIPMENT:hashTestKey");
//		System.out.println(maps.get("id"));
//		System.out.println(maps.get("name"));
//		//项目注册 redis key_head
//		moduleDefineService.addModuleCode("O2O_SHIPMENT:");
//		//添加 数据
//		valueOperationsService.set("O2O_SHIPMENT:testkey", "test");
//        //查询数据
//		System.out.println(valueOperationsService.get("O2O_SHIPMENT:testkey"));
	}

}
