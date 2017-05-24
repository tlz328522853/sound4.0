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

import com.sdcloud.api.hl.entity.Elescale;
import com.sdcloud.api.hl.entity.ElescaleChild;
import com.sdcloud.api.hl.service.ElescaleChildService;
import com.sdcloud.api.hl.service.ElescaleService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_elescale 电子秤信息
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/elescale")
public class JhlElescaleController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElescaleService elescaleService;
	@Autowired
	private ElescaleChildService elescaleChildService;
		
	/**
	 * 添加数据
	 * @param elescale 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody Elescale elescale){
		try {
			if(elescale != null){
				if(StringUtils.isEmpty(elescale.getMchid())){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				if(elescaleService.countByElescaleId(elescale.getMchid())>0){
					return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
				}
				if(elescale.getUseNumber()==null){
					elescale.setUseNumber(0);
				}
				boolean b = elescaleService.save(elescale);
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
	 * @param elescale 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  Elescale elescale){
		try {
			if(elescale != null){
				if(StringUtils.isEmpty(elescale.getMchid())||elescale.getWeight1()==null
						||elescale.getWeight2()==null||elescale.getWeight3()==null
						||elescale.getWeight4()==null||elescale.getWeight5()==null
						||elescale.getWeight6()==null||elescale.getWeight7()==null
						||elescale.getWeight8()==null||elescale.getWeight9()==null){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				Elescale oldObj=elescaleService.selectByElescaleId(elescale.getMchid());
				if(oldObj==null){
					return ResultDTO.getFailure(402, "未查询到该记录，请检查数据");
				}
				//做计算，并且把计算完的结果 添加到数据库，比较旧值和新值大小
				if(oldObj!=null){
					//计算
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
					Integer a= elescale.getWeight1()-oldObj.getWeight1();
					Integer b= elescale.getWeight2()-oldObj.getWeight2();
					Integer c= elescale.getWeight3()-oldObj.getWeight3();
					Integer d= elescale.getWeight4()-oldObj.getWeight4();
					Integer e= elescale.getWeight5()-oldObj.getWeight5();
					Integer f= elescale.getWeight6()-oldObj.getWeight6();
					Integer g= elescale.getWeight7()-oldObj.getWeight7();
					Integer h= elescale.getWeight8()-oldObj.getWeight8();
					Integer i= elescale.getWeight9()-oldObj.getWeight9();
					
					//设置子数据
					ElescaleChild elescaleChild = new ElescaleChild();
					elescaleChild.setMchid(elescale.getMchid());
					elescaleChild.setNickname(elescale.getNickname());
					elescaleChild.setPotx(elescale.getPotx());
					elescaleChild.setPoty(elescale.getPoty());
					elescaleChild.setWeight1(a);
					elescaleChild.setWeight2(b);
					elescaleChild.setWeight3(c);
					elescaleChild.setWeight4(d);
					elescaleChild.setWeight5(e);
					elescaleChild.setWeight6(f);
					elescaleChild.setWeight7(g);
					elescaleChild.setWeight8(h);
					elescaleChild.setWeight9(i);
					elescaleChild.setUseNumber(elescale.getUseNumber());
					elescaleChild.setFlushTime(elescale.getFlushTime());
					
					
					elescaleChildService.save(elescaleChild);
				}
				
				boolean b = elescaleService.update(elescale);
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
