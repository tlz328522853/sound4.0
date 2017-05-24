package com.sdcloud.web.lar.util.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.sdcloud.api.cache.redis.service.ModuleDefineService;
import com.sdcloud.api.lar.util.CacheKeyUtil;

/**
 * spring上下文 启动完毕的 监听 事件
 * @author jzc
 * 2016年8月24日
 */
@Component
public class ContextFinishEvent implements ApplicationListener<ContextRefreshedEvent> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AutoDispatchOrderThread autoDispatchOrderThread;
	@Autowired
	private ModuleDefineService moduleDefineService;//dubbo redis 模块注册

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		//判断事件类型，防止多次触发该事件，因为存在spring 与 spirngmvc两个上下文容器
        if(event.getApplicationContext().getDisplayName().
        		equals("Root WebApplicationContext")){
        	
        	logger.info("spring context start up success finish !");
        	//启动-订单超时自动派单
        	autoDispatchOrderThread.autoDispatchOrder();
        	//添加 redis公共头
        	addModuleCode(CacheKeyUtil.O2O_COMMON);
        	//添加 redis物流头
        	addModuleCode(CacheKeyUtil.O2O_SHIPMENT);
        	//添加 redis回收头
        	addModuleCode(CacheKeyUtil.O2O_RECOVERY);
       }
		
	}
	
	private void addModuleCode(String headCode){
		//注册redis key头
		int code=moduleDefineService.addModuleCode(headCode);
		if(code==1){
			logger.info("redis添加 （"+headCode+"）key head 成功！");
		}
		else if(code==0){
			logger.info("redis添加 （"+headCode+"）,已经 存在！成功！");
		}
		else{
			logger.warn("redis添加 （"+headCode+"）,失败！需要处理！");
			try {
				throw new Exception("紧急：redis添加 （"+headCode+"）,失败！需要管理员紧急处理！正在自动进行重新添加......");
			} catch (Exception e) {
				logger.warn(e.getMessage()+"\r\n\r\n\r\n");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				this.addModuleCode(headCode);
			}
		}
	}
	

}
