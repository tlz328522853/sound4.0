package com.sdcloud.web.hl.controller.joinhl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.ServiceCust;
import com.sdcloud.api.hl.entity.ServiceCustChild;
import com.sdcloud.api.hl.service.ServiceCustChildService;
import com.sdcloud.api.hl.service.ServiceCustService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_service_cust 便民服务表
 * @author jiazc
 * @date 2017-05-16
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/serviceCust")
public class JhlServiceCustController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ServiceCustService serviceCustService;
	@Autowired
	private ServiceCustChildService serviceCustChildService;
		
	/**
	 * 添加数据
	 * @param serviceCust 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody ServiceCust serviceCust){
		try {
			if(serviceCust != null){
				if(serviceCust.getId()!=null){
					serviceCust.setScId(serviceCust.getId().intValue());
					serviceCust.setId(null);
					if(serviceCustService.countByScId(serviceCust.getScId())>0){
						return ResultDTO.getSuccess(201,"重复添加，数据已存在",null);
					}
				}
				boolean b = serviceCustService.save(serviceCust);
				if(b){
					return ResultDTO.getSuccess(200,"成功");
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
	 * @param serviceCust 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  ServiceCust serviceCust){
		try {
			if(serviceCust != null || serviceCust.getWeight1()==null
					||serviceCust.getWeight2()==null||serviceCust.getWeight3()==null
					||serviceCust.getWeight4()==null||serviceCust.getWeight5()==null
					||serviceCust.getWeight6()==null||serviceCust.getWeight7()==null
					||serviceCust.getWeight8()==null||serviceCust.getWeight9()==null){
				if(serviceCust.getId()!=null){
					serviceCust.setScId(serviceCust.getId().intValue());
					serviceCust.setId(null);
				}else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				ServiceCust oldObj=serviceCustService.selectByScId(serviceCust.getScId());
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
					Integer a= serviceCust.getWeight1()-oldObj.getWeight1();
					Integer b= serviceCust.getWeight2()-oldObj.getWeight2();
					Integer c= serviceCust.getWeight3()-oldObj.getWeight3();
					Integer d= serviceCust.getWeight4()-oldObj.getWeight4();
					Integer e= serviceCust.getWeight5()-oldObj.getWeight5();
					Integer f= serviceCust.getWeight6()-oldObj.getWeight6();
					Integer g= serviceCust.getWeight7()-oldObj.getWeight7();
					Integer h= serviceCust.getWeight8()-oldObj.getWeight8();
					Integer i= serviceCust.getWeight9()-oldObj.getWeight9();
					
					ServiceCustChild serviceCustChild = new ServiceCustChild();
					serviceCustChild.setScId(serviceCust.getScId());
					serviceCustChild.setNickname(serviceCust.getNickname());
					serviceCustChild.setPotx(serviceCust.getPotx());
					serviceCustChild.setPoty(serviceCust.getPoty());
					serviceCustChild.setWeight1(a);
					serviceCustChild.setWeight2(b);
					serviceCustChild.setWeight3(c);
					serviceCustChild.setWeight4(d);
					serviceCustChild.setWeight5(e);
					serviceCustChild.setWeight6(f);
					serviceCustChild.setWeight7(g);
					serviceCustChild.setWeight8(h);
					serviceCustChild.setWeight9(i);
					serviceCustChild.setFlushTime(serviceCust.getFlushTime());
					
					serviceCustChildService.save(serviceCustChild);
				}
				boolean b = serviceCustService.update(serviceCust);
				if(b){
					return ResultDTO.getSuccess(200,"成功");
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
