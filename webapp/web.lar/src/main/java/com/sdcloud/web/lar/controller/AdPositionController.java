package com.sdcloud.web.lar.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.AdPosition;
import com.sdcloud.api.lar.entity.AdPublish;
import com.sdcloud.api.lar.service.AdPositionService;
import com.sdcloud.api.lar.service.AdPublishService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

@RestController
@RequestMapping("/api/adPosition")
public class AdPositionController {

	@Autowired
	private AdPositionService adPositionService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private AdPublishService adPublishService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<AdPosition> pager){
		try {
			
			pager = adPositionService.findAll(pager);
			LarPager<AdPublish> publishPager = new LarPager<>(100000);
			List<AdPublish> adPublishsList = adPublishService.findAll(publishPager).getResult();
			Map <Long,Long>map=new HashMap();
			for(AdPublish p:adPublishsList){
				map.put(p.getAdName(), p.getAdName());
			}
			for(AdPosition p:pager.getResult()){
				if(map.get(p.getId())!=null){
					p.setIssue(true);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/findPosAndPub",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findPosAndPub(@RequestBody(required=false) Map<String, Object> map){
		try {
			LarPager<AdPosition> positionPager = new LarPager<>(100000);
			LarPager<AdPublish> publishPager = new LarPager<>(100000);
			if(null != map && !map.isEmpty()){
				positionPager.setParams(map);
				publishPager.setParams(map);
			}
			List<AdPosition> adPositionsList = adPositionService.findAll(positionPager).getResult();
			List<AdPublish> adPublishsList = adPublishService.findAll(publishPager).getResult();
			map.clear();
			map = this.convert(adPositionsList, adPublishsList);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(map);
	}
	
	private Map<String, Object> convert(List<AdPosition> adPositionsList,List<AdPublish> adPublishsList){
		Map<String, Object> map = new HashMap<>();
		int positionCount = 0;
		int publishCount = 0;
		float money = 0.0f;
		//当前时间
		Date date = new Date();
		for (AdPosition adPosition : adPositionsList) {
			if(null != adPosition.getCount()){
				positionCount += adPosition.getCount();
			}
		}
		for (AdPublish adPublish : adPublishsList) {
			Date startTime = adPublish.getStartTime();
			Date endTime = adPublish.getEndTime();
			if(null != adPublish.getCount()){
				if(null != startTime && null != endTime){
					if(date.getTime()-startTime.getTime()>0 && date.getTime()-endTime.getTime()<0){
						publishCount += adPublish.getCount();
					}
				}
			}
			if(null != adPublish.getAmount() && isLastMonth(date,adPublish.getCreateDate())){
				money += adPublish.getAmount();
			}
		}
		map.put("isShow", positionCount>0?"是":"否");
		map.put("positionCount", positionCount);
		map.put("publishCount", publishCount);
		map.put("money", money);
		return map;
	}
	//判断是否为上个月
	private boolean isLastMonth(Date date,Date creatDate){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		//上个月
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
		String lastDate = format.format(calendar.getTime());
		//创建的月份
		String createDate = format.format(creatDate);
		if(lastDate.equals(createDate)){
			return true;
		}
		return false;
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) AdPosition adPosition) throws Exception{
		try {
			if(adPosition != null){
				boolean b = adPositionService.save(adPosition);
				if(b){
					return ResultDTO.getSuccess("添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) AdPosition adPosition){
		try {
			if(adPosition != null){
				boolean b = adPositionService.update(adPosition);
				if(b){
					return ResultDTO.getSuccess("修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") Long id){
		try {
			boolean b = adPositionService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<AdPosition> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                Long id = Long.valueOf(map.get("orgId") + "");
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
                if (null != id) {
                    //是父节点再去查找
                    if (isParentNode) {
                        List<Org> list = orgService.findById(id, true);
                        for (Org org : list) {
                            ids.add(org.getOrgId());
                        }
                    }else{
                        larPager.getParams().put("org",id);
                        ids=null;
                    }
                }
            }
            return ResultDTO.getSuccess(200, adPositionService.findByOrgIds(larPager, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
	
	@RequestMapping(value="/batchDelete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO batchDelete(@RequestParam("ids") String ids){
		try {
			if(ids != null){
				boolean b = adPositionService.batchDelete(ids);
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
