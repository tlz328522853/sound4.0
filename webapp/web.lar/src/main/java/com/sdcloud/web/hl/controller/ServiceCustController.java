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
import com.sdcloud.api.hl.service.CountService;
import com.sdcloud.api.hl.service.ServiceCustService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.hl.util.DateUtils;

/**
 * hl_service_cust 便民服务表
 * @author jiazc
 * @date 2017-05-17
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/apihl/serviceCust")
public class ServiceCustController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ServiceCustService serviceCustService;
	@Autowired
	private CountService countService;
	
	/**
	 * 便民服务亭--回收
	 * @author jzc 2017年5月19日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value="/getdataRecyle")
	@ResponseBody
	public ResultDTO getdataRecyle(@RequestBody Map<String, Object> condition){
		try {
			Map<String,Object> dateMap=null;
			Integer recDay=1;
			Integer planningType=1;//0按回收亭、1日期 选择
			if(condition != null&&condition.size()>0){//非默认查询
				//验证参数planningType
				if(condition.get("rec_day")!=null&&
						condition.get("planning_type")!=null&&
						condition.get("start_date")!=null&&
						condition.get("end_date")!=null){//子页-自定义查询日月
					recDay=(Integer) condition.get("rec_day");
					planningType=(Integer) condition.get("planning_type");
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
						condition.get("planning_type")!=null&&
						condition.get("start_date")==null&&
						condition.get("end_date")==null){//子页-默认查询日月
					recDay=(Integer) condition.get("rec_day");
					planningType=(Integer) condition.get("planning_type");
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
			Map<String,Object> resultData=resultDataRecyle(dateMap,recDay,planningType);
			return ResultDTO.getSuccess(resultData);
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "系统错误!");
		}
	}
	
	
	private Map<String,Object> resultDataRecyle(Map<String,Object> dateMap,int recDay
			,int planningType){
		Map<String,Object> resultData=new HashMap<>();
		String[] xdata=(String[])dateMap.get("dateStrs");//对比的格式日期字符串数组
		double[] ydata=new double[xdata.length];
		Date startDate=(Date)dateMap.get("startDate");//查询的开始时间
		Date endDate=(Date)dateMap.get("endDate");//查询的结束时间
		List<CountChartEntry> co = null;
		if(planningType==1){//根据日期维度
			if(recDay==0){
				co =countService.countConveRecyleDate(startDate,endDate);
			}
			else{
				co = countService.countConveRecyleMonth(startDate,endDate);
			}
			//对比数据并补全
			Map<String,Integer> maps=new HashMap<>();
			for(CountChartEntry entry:co){
				maps.put(entry.getDateStr(), entry.getNumInt1());
			}
			for(int i=0;i<xdata.length;i++){
				if(maps.get(xdata[i])!=null){
					ydata[i]=maps.get(xdata[i])/1000.00;
					continue;
				}
				ydata[i]=0;
			}
			//返回的 数据json格式	
			resultData=new HashMap<>();
			if(recDay==0){
				xdata=DateUtils.slitDateStr(xdata);
			}
		}
		else{//根据服务亭维度
			co = countService.countRecyle(startDate,endDate);
			xdata=new String[co.size()];
			ydata=new double[co.size()];
			for(int i=0;i<co.size();i++){
				xdata[i]=co.get(i).getNickname();
				ydata[i]=co.get(i).getNumInt1()/1000.00;
			}
		}
		
		resultData.put("xdata", xdata);
		resultData.put("ydata", ydata);
		return resultData;
	}
	
	
	/**
	 * 便民服务亭--快递
	 * @author jzc 2017年5月19日
	 * @param condition
	 * @return
	 */
	@RequestMapping(value="/getdataExpress")
	@ResponseBody
	public ResultDTO getdataExpress(@RequestBody Map<String, Object> condition){
		try {
			Map<String,Object> dateMap=null;
			Integer recDay=1;
			Integer planningType=1;//0按回收亭、1日期 选择
			if(condition != null&&condition.size()>0){//非默认查询
				//验证参数planningType
				if(condition.get("rec_day")!=null&&
						condition.get("planning_type")!=null&&
						condition.get("start_date")!=null&&
						condition.get("end_date")!=null){//子页-自定义查询日月
					recDay=(Integer) condition.get("rec_day");
					planningType=(Integer) condition.get("planning_type");
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
						condition.get("planning_type")!=null&&
						condition.get("start_date")==null&&
						condition.get("end_date")==null){//子页-默认查询日月
					recDay=(Integer) condition.get("rec_day");
					planningType=(Integer) condition.get("planning_type");
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
			Map<String,Object> resultData=resultDataExpress(dateMap,recDay,planningType);
			return ResultDTO.getSuccess(resultData);
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(500, "系统错误!");
		}
	}
	
	
	private Map<String,Object> resultDataExpress(Map<String,Object> dateMap,int recDay
			,int planningType){
		Map<String,Object> resultData=new HashMap<>();
		String[] xdata=(String[])dateMap.get("dateStrs");//对比的格式日期字符串数组
		int[] ydata=new int[xdata.length];
		Date startDate=(Date)dateMap.get("startDate");//查询的开始时间
		Date endDate=(Date)dateMap.get("endDate");//查询的结束时间
		List<CountChartEntry> co = null;
		if(planningType==1){//根据日期维度
			if(recDay==0){
				co =countService.countExpressDate(startDate,endDate);
			}
			else{
				co = countService.countExpressMonth(startDate,endDate);
			}
			//对比数据并补全
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
		}
		else{//根据服务亭维度
			co = countService.countExpress(startDate,endDate);
			xdata=new String[co.size()];
			ydata=new int[co.size()];
			for(int i=0;i<co.size();i++){
				xdata[i]=co.get(i).getNickname();
				ydata[i]=co.get(i).getNumInt1();
			}
		}
		
		resultData.put("xdata", xdata);
		resultData.put("ydata", ydata);
		return resultData;
	}
	
	
}
