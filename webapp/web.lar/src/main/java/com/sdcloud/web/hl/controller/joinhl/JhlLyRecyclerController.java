package com.sdcloud.web.hl.controller.joinhl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.LyRecycler;
import com.sdcloud.api.hl.entity.LyRecyclerChild;
import com.sdcloud.api.hl.service.LyRecyclerChildService;
import com.sdcloud.api.hl.service.LyRecyclerService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_ly_recycler 联运回收机信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/lyRecycler")
public class JhlLyRecyclerController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LyRecyclerService lyRecyclerService;
	@Autowired
	private LyRecyclerChildService lyRecyclerChildService;
		
	/**
	 * 添加数据
	 * @param lyRecycler 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody LyRecycler lyRecycler){
		try {
			if(lyRecycler != null){
				if(StringUtils.isEmpty(lyRecycler.getMchid())){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				if(lyRecyclerService.countByMchid(lyRecycler.getMchid())>0){
					return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
				}
				if(lyRecycler.getUseNumber()==null){
					lyRecycler.setUseNumber(0);
				}
				boolean b = lyRecyclerService.save(lyRecycler);
				if(b){
					return ResultDTO.getSuccess(200,"成功",null);
				}else{
					return ResultDTO.getFailure(401, "失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "数据异常，系统繁忙");
		}
	}
	
	/**
	 * 更新数据
	 * @param lyRecycler 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  LyRecycler lyRecycler){
		try {
			if(lyRecycler != null){
				if(StringUtils.isEmpty(lyRecycler.getMchid())||lyRecycler.getWeight1()==null
						||lyRecycler.getWeight2()==null||lyRecycler.getWeight3()==null
						||lyRecycler.getWeight4()==null||lyRecycler.getWeight5()==null
						||lyRecycler.getWeight6()==null||lyRecycler.getWeight7()==null
						||lyRecycler.getWeight8()==null||lyRecycler.getWeight9()==null){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				LyRecycler oldObj=lyRecyclerService.selectByMchid(lyRecycler.getMchid());
				if(oldObj==null){
					return ResultDTO.getFailure(402, "未查询到该记录，请检查数据");
				}
				//做计算，并且把计算完的结果 添加到数据库，比较旧值和新值大小
				if(oldObj!=null){
					//计算
					if(oldObj.getWeight1()==null){
						oldObj.setWeight1(0);
						
					}
					if(oldObj.getWeight2()==null){
						oldObj.setWeight2(0);
						
					}
					if(oldObj.getWeight3()==null){
						oldObj.setWeight3(0);
						
					}
					if(oldObj.getWeight4()==null){
						oldObj.setWeight4(0);
						
					}
					if(oldObj.getWeight5()==null){
						oldObj.setWeight5(0);
						
					}
					if(oldObj.getWeight6()==null){
						oldObj.setWeight6(0);
						
					}
					if(oldObj.getWeight6()==null){
						oldObj.setWeight6(0);
						
					}
					if(oldObj.getWeight7()==null){
						oldObj.setWeight7(0);
						
					}
					if(oldObj.getWeight8()==null){
						oldObj.setWeight8(0);
						
					}

					if(oldObj.getWeight9()==null){
						oldObj.setWeight9(0);
						
					}
					Integer a= lyRecycler.getWeight1()-oldObj.getWeight1();
					Integer b= lyRecycler.getWeight2()-oldObj.getWeight2();
					Integer c= lyRecycler.getWeight3()-oldObj.getWeight3();
					Integer d= lyRecycler.getWeight4()-oldObj.getWeight4();
					Integer e= lyRecycler.getWeight5()-oldObj.getWeight5();
					Integer f= lyRecycler.getWeight6()-oldObj.getWeight6();
					Integer g= lyRecycler.getWeight7()-oldObj.getWeight7();
					Integer h= lyRecycler.getWeight8()-oldObj.getWeight8();
					Integer i= lyRecycler.getWeight9()-oldObj.getWeight9();
					
					//设置子数据
					LyRecyclerChild lyRecyclerChild = new LyRecyclerChild();
					lyRecyclerChild.setMchid(lyRecycler.getMchid());
					lyRecyclerChild.setNickname(lyRecycler.getNickname());
					lyRecyclerChild.setPotx(lyRecycler.getPotx());
					lyRecyclerChild.setPoty(lyRecycler.getPoty());
					lyRecyclerChild.setWeight1(a);
					lyRecyclerChild.setWeight2(b);
					lyRecyclerChild.setWeight3(c);
					lyRecyclerChild.setWeight4(d);
					lyRecyclerChild.setWeight5(e);
					lyRecyclerChild.setWeight6(f);
					lyRecyclerChild.setWeight7(g);
					lyRecyclerChild.setWeight8(h);
					lyRecyclerChild.setWeight9(i);
					lyRecyclerChild.setUseNumber(lyRecycler.getUseNumber());
					lyRecyclerChild.setFlushTime(lyRecycler.getFlushTime());
					
					lyRecyclerChildService.save(lyRecyclerChild);
				}
				
				boolean b = lyRecyclerService.update(lyRecycler);
				if(b){
					return ResultDTO.getSuccess(200,"成功",null);
				}else{
					return ResultDTO.getFailure(401, "失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "数据异常，系统繁忙");
		}	
		
	}

	
}
