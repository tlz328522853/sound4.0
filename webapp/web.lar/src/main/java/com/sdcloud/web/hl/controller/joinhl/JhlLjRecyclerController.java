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

import com.sdcloud.api.hl.entity.LjRecycler;
import com.sdcloud.api.hl.entity.LjRecyclerChild;
import com.sdcloud.api.hl.service.LjRecyclerChildService;
import com.sdcloud.api.hl.service.LjRecyclerService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * hl_lj_recycler 浪尖回收机
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/jhl/ljRecycler")
public class JhlLjRecyclerController{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LjRecyclerService ljRecyclerService;
	@Autowired
	private LjRecyclerChildService ljRecyclerChildService;
		
	/**
	 * 添加数据
	 * @param ljRecycler 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody LjRecycler ljRecycler){
		try {
			if(ljRecycler != null){
				if(StringUtils.isEmpty(ljRecycler.getMchid())){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				if(ljRecyclerService.countByMchid(ljRecycler.getMchid())>0){
					return ResultDTO.getSuccess(201, "重复添加，数据已存在",null);
				}
					Byte a=0;
					if(ljRecycler.getStatus()==null){
						ljRecycler.setStatus(a);
					}
					if(ljRecycler.getStatusBottle()==null){
						ljRecycler.setStatusBottle(a);
					}
					if(ljRecycler.getStatusDoor()==null){
						ljRecycler.setStatusDoor("00000");
					}
					if(ljRecycler.getStatusPaper()==null){
						ljRecycler.setStatusPaper(a);
					}
					if(ljRecycler.getStatusClothes()==null){
						ljRecycler.setStatusClothes(a);
					}
					if(ljRecycler.getUseNumber()==null){
						ljRecycler.setUseNumber(0);
					}
				
				boolean b = ljRecyclerService.save(ljRecycler);
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
	 * @param ljRecycler 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  LjRecycler ljRecycler){
		try {
			if(ljRecycler != null){
				if(StringUtils.isEmpty(ljRecycler.getMchid())
						||ljRecycler.getClothesWeight()==null||ljRecycler.getBottleCount()==null){
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
				LjRecycler oldObj=ljRecyclerService.selectByMchid(ljRecycler.getMchid());
				if(oldObj==null){
					return ResultDTO.getFailure(402, "未查询到该记录，请检查数据");
				}
				//做计算，并且把计算完的结果 添加到数据库，比较旧值和新值大小
				if(oldObj!=null){
					//计算
					//计算
					if(oldObj.getBottleCount()==null){
						oldObj.setBottleCount(0);
						
					}
					if(oldObj.getClothesWeight()==null){
						oldObj.setClothesWeight(0);
						
					}
					
					
					Integer a= ljRecycler.getBottleCount()-oldObj.getBottleCount();
					Integer b= ljRecycler.getClothesWeight()-oldObj.getClothesWeight();
					
					//设置子数据
					LjRecyclerChild ljRecyclerChild = new LjRecyclerChild();
					ljRecyclerChild.setMchid(ljRecycler.getMchid());
					ljRecyclerChild.setNickname(ljRecycler.getNickname());
					ljRecyclerChild.setPotx(ljRecycler.getPotx());
					ljRecyclerChild.setPoty(ljRecycler.getPoty());
					ljRecyclerChild.setBottleCount(a);
					ljRecyclerChild.setClothesWeight(b);
					ljRecyclerChild.setStatus(ljRecycler.getStatus());
					ljRecyclerChild.setStatusBottle(ljRecycler.getStatusBottle());
					ljRecyclerChild.setStatusClothes(ljRecycler.getStatusClothes());
					ljRecyclerChild.setStatusDoor(ljRecycler.getStatusDoor());
					ljRecyclerChild.setStatusPaper(ljRecycler.getStatusPaper());
					ljRecyclerChild.setUseNumber(ljRecycler.getUseNumber());
					ljRecyclerChild.setFlushTime(ljRecycler.getFlushTime());
					
					ljRecyclerChildService.save(ljRecyclerChild);
				}
				
				boolean b = ljRecyclerService.update(ljRecycler);
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
