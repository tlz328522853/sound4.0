package com.sdcloud.web.hl.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.hl.entity.CountChartEntry;
import com.sdcloud.api.hl.entity.CountNumEntry;
import com.sdcloud.api.hl.service.CountService;
import com.sdcloud.api.hl.service.UserService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.hl.util.DateUtils;

/**
 * hl_user 用户基本数据
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/apihl/user")
public class UserController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	@Autowired
	private CountService countService;
	
	@RequestMapping(value="/getdata")
	@ResponseBody
	public ResultDTO getdata(@RequestBody Map<String, Object> condition){
		try {
			Map<String,Object> dateMap=null;
			Integer recDay=1;
			if(condition != null&&condition.size()>0){//非默认查询
				//验证参数
				if(condition.get("rec_day")!=null&&
						condition.get("start_date")!=null&&
						condition.get("end_date")!=null){//子页-自定义查询日月
					recDay=(Integer) condition.get("rec_day");
					String startDate=(String) condition.get("start_date");
					String endDate=(String) condition.get("end_date");
					//判断是月还是日
					if(recDay==0){//天
						dateMap=DateUtils.selfDay(startDate, endDate);
					}
					else if(recDay==1){//月
						dateMap=DateUtils.selfMonth(startDate, endDate);
					}
				}
				else if(condition.get("rec_day")!=null&&
						condition.get("start_date")==null&&
						condition.get("end_date")==null){//子页-默认查询日月
					recDay=(Integer) condition.get("rec_day");
					//判断是月还是日
					if(recDay==0){//天
						dateMap=DateUtils.defaultDay(DateUtils.DEFAULT_DAY_CHILD);
					}
					else if(recDay==1){//月
						dateMap=DateUtils.defaultMonth(DateUtils.DEFAULT_MONTH_CHILD);
					}
				}
				else{
					return ResultDTO.getFailure(400, "非法请求，请重新尝试");
				}
			}
			else{//首页-默认查询往前6个月数据
				dateMap=DateUtils.defaultMonth(DateUtils.DEFAULT_MONTH);
			}
			Map<String,Object> resultData=resultData(dateMap,recDay);
			return ResultDTO.getSuccess(resultData);
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "系统错误!");
		}
	}
	
	
	private Map<String,Object> resultData(Map<String,Object> dateMap,int recDay){
		Map<String,Object> resultData=new HashMap<>();
		String[] xdata=(String[])dateMap.get("dateStrs");//对比的格式日期字符串数组
		Date startDate=(Date)dateMap.get("startDate");//查询的开始时间
		Date endDate=(Date)dateMap.get("endDate");//查询的结束时间
		List<CountChartEntry> co = null;
		if(recDay==0){
			co =countService.countUserChartDate(startDate,endDate);
		}
		else{
			co = countService.countUserChartMonth(startDate,endDate);
		}
		//对比数据并补全
		int[] ydata=new int[xdata.length];
		Map<String,Integer> maps=new HashMap<>();
		for(CountChartEntry entry:co){
			maps.put(entry.getDateStr(), entry.getNumInt1());
		}
		for(int i=0;i<xdata.length;i++){
			if(maps.get(xdata[i])!=null){
				ydata[i]=maps.get(xdata[i]);
				continue;
			}
			ydata[i]=0;
		}
		//返回的 数据json格式	
		resultData=new HashMap<>();
		if(recDay==0){
			xdata=DateUtils.slitDateStr(xdata);
		}
		resultData.put("xdata", xdata);
		resultData.put("ydata", ydata);
		return resultData;
	}
	
	
	/**
	 * 统计量总数 接口
	 * @author jzc 2017年5月16日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value="/getAll")
	@ResponseBody
	public ResultDTO getAll(){
		try {
			CountNumEntry numEntry=countService.countNum();
			numEntry.setRecyleNum(numEntry.getRecyleNum()/1000.00);
			numEntry.setLifeNum(HypayController.round(numEntry.getLifeNum(), 3));
			return ResultDTO.getSuccess(numEntry);
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "系统错误!");
		}
	}
	
	
}
