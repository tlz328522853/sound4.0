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
import com.sdcloud.api.hl.service.ExpressFindService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.hl.util.DateUtils;

/**
 * hl_express_find 快递查询
 * @author jiazc
 * @date 2017-05-08
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/apihl/expressFind")
public class ExpressFindController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ExpressFindService expressFindService;
	@Autowired
	private CountService countService;
	
		
	/**
	 * 添加数据
	 * @param expressFind 添加数据
	 * @return
	 */
//	@RequestMapping(value="/getdata",method=RequestMethod.POST)
//	@ResponseBody
//	public ResultDTO getdata(@RequestBody Map<String, Object> condition){
//		Map<String,Object> resultData=null;
//		try {
//			if(condition != null&&condition.size()>0){//非默认查询
//				resultData=new HashMap<>();
//				String[] xdata={"1月","2月","3月","四月","五月","六月"};
//				int[] ydata_daishou={100,200,300,400,500,600};//代收快递量
//				int[] ydata_cha={10,20,30,40,50,60};//查快递量
//				int[] ydata_shou={1,2,3,4,5,6};//收快递量
//				resultData.put("xdata", xdata);
//				resultData.put("ydata_daishou", ydata_daishou);
//				resultData.put("ydata_cha", ydata_cha);
//				resultData.put("ydata_shou", ydata_shou);
//			}
//			else{
//				resultData=new HashMap<>();
//				String[] xdata={"一月","二月","三月","四月","五月","六月"};
//				int[] ydata_daishou={100,200,300,400,500,600};//代收快递量
//				int[] ydata_cha={10,20,30,40,50,60};//查快递量
//				int[] ydata_shou={1,2,3,4,5,6};//收快递量
//				resultData.put("xdata", xdata);
//				resultData.put("ydata_daishou", ydata_daishou);
//				resultData.put("ydata_cha", ydata_cha);
//				resultData.put("ydata_shou", ydata_shou);
//			}
//			return ResultDTO.getSuccess(resultData);
//		} catch (Exception e) {
//			logger.error("系统处理异常:method {}, Exception:{}",
//        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
//			return ResultDTO.getFailure(500, "系统错误!");
//		}
//	}
	
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
				dateMap=DateUtils.defaultMonth(6);
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
			co =countService.countExpressChartDate(startDate,endDate);
		}
		else{
			co = countService.countExpressChartMonth(startDate,endDate);
		}
		//对比数据并补全
		int[] ydata_daishou=new int[xdata.length];//代收快递量
		int[] ydata_cha=new int[xdata.length];//查快递量
		int[] ydata_fa=new int[xdata.length];//发快递量
		Map<String,CountChartEntry> maps=new HashMap<>();
		for(CountChartEntry entry:co){
			maps.put(entry.getDateStr(), entry);
		}
		for(int i=0;i<xdata.length;i++){
			if(maps.get(xdata[i])!=null){
				ydata_daishou[i]=maps.get(xdata[i]).getNumInt1();
				ydata_cha[i]=maps.get(xdata[i]).getNumInt3();
				ydata_fa[i]=maps.get(xdata[i]).getNumInt2();
				continue;
			}
			ydata_daishou[i]=0;
			ydata_cha[i]=0;
			ydata_fa[i]=0;
		}
		//返回的 数据json格式	
		resultData=new HashMap<>();
		if(recDay==0){
			xdata=DateUtils.slitDateStr(xdata);
		}
		resultData.put("xdata", xdata);
		resultData.put("ydata_daishou", ydata_daishou);
		resultData.put("ydata_cha", ydata_cha);
		resultData.put("ydata_fa", ydata_fa);
		return resultData;
	}
	
	
}
