package com.sdcloud.web.lar.controller.app;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.MaaDateSetting;
import com.sdcloud.api.lar.entity.ShipmentOrderTime;
import com.sdcloud.api.lar.service.MaaDateSettingService;
import com.sdcloud.api.lar.service.ShipmentOrderTimeService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * 物流管理--->设置
 * Created by 韩亚辉 on 2016/3/15.
 */
@RestController
@RequestMapping("/app/shipmentOrderTime")
public class AppShipmentSettingController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ShipmentOrderTimeService shipmentOrderTimeService;
    @Autowired
	private MaaDateSettingService maaDateSettingService;
    @Autowired
 	private SysConfigService sysConfigService;
    

    /**
     * 时间列表
     * module值: 1--物流预约时间  2--物流预约时间(一周) 3--回收预约时间  4--回收预约时间(一周)
     * @param pager
     * @return
     * @throws Throwable 
     */
    @SuppressWarnings("deprecation")
	@RequestMapping("/findAll")
    public ResultDTO timeSettingList(@RequestBody(required = false) LarPager<ShipmentOrderTime> pager) throws Throwable {
        try {
        	Map<String, Object> extendMap = pager.getExtendMap();
        	if(pager.getOrder() == null){
        		pager.setOrder("asc");
        	}
        	if(extendMap.get("module") != null){
        		int	module =(Integer) extendMap.get("module");
        		//查询物流全部预约时间
        		if(module == 1){
        			List<ShipmentOrderTime> result = shipmentOrderTimeService.findAll(pager).getResult();
        			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取物流预约时间",result);
        		}
        		//七天预约时间
        		if(module == 2){
        			List<ShipmentOrderTime> result = shipmentOrderTimeService.findAll(pager).getResult();
        			List<Map<String, Object>> list = getAppointmentTime(result,module);
        			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取物流预约时间",list);
        		}
        		
        		LarPager<MaaDateSetting> mdsPager = new LarPager<>();
            	mdsPager.setToken(pager.getToken());
            	mdsPager.setPageNo(pager.getPageNo());
            	mdsPager.setPageSize(pager.getPageSize());
            	mdsPager.setOrderBy("startDate");
            	mdsPager.setOrder(pager.getOrder());
            	maaDateSettingService.selectByExample(mdsPager);
            	
        		if(module == 3){
        			List<MaaDateSetting> result = maaDateSettingService.selectByExample(mdsPager).getResult();
        			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取回收预约时间",result);
        		}
        		if(module == 4){
        			List<MaaDateSetting> result = maaDateSettingService.selectByExample(mdsPager).getResult();
        			List<Map<String, Object>> list = getAppointmentTime(result,module);
        			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取回收预约时间",list);
        		}
        		return ResultDTO.getFailure(AppCode.BAD_REQUEST, "module传参错误！");
        	}else{
        		return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，extendMap传参为空！");
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    /**
     * 添加
     *
     * @param shipmentOrderTime
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO saveTimeSetting(@RequestBody ShipmentOrderTime shipmentOrderTime) {
        try {
            return ResultDTO.getSuccess(200, shipmentOrderTimeService.save(shipmentOrderTime));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 修改
     *
     * @param shipmentOrderTime
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO updateTimeSetting(@RequestBody ShipmentOrderTime shipmentOrderTime) {
        try {
            return ResultDTO.getSuccess(200, shipmentOrderTimeService.update(shipmentOrderTime));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResultDTO deleteTimeSetting(@PathVariable Long id) {
        try {
            return ResultDTO.getSuccess(200, shipmentOrderTimeService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    //获取一周的预约时间
    public <T> List<Map<String, Object>> getAppointmentTime(List<T> result,int module) throws Throwable{
    	logger.info("create order scheduleDate and scheduleTime"); 
    	int time=30;//默认可选预约时间延迟30分钟
    	Map configMap=sysConfigService.findMap();
    	if(configMap!=null){
    		try{
    			time=Integer.valueOf(configMap.get("scheduleTime").toString());
    		}catch(Exception e){
    		}
    	}
    	List<Map<String, Object>> list = new ArrayList<>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    	//获取当前时间
    	Date date = new Date();
    	int currentMinutes = date.getHours()*60 + date.getMinutes()+time;
    	//获取result接口集的索引
    	int index = -1;
    	String simpleName = result.get(0).getClass().getSimpleName();
    	for (int i = 0; i < result.size(); i++) {
    		Date starTime = null;
    		if("ShipmentOrderTime".equals(simpleName)){
    			Field field = result.get(i).getClass().getDeclaredField("startTime");
    			field.setAccessible(true);
    			starTime = (Date) field.get(result.get(i));
    		}
    		if("MaaDateSetting".equals(simpleName)){
    			Field field = result.get(i).getClass().getDeclaredField("startDate");
    			field.setAccessible(true);
    			starTime = (Date) field.get(result.get(i));
    		}
    		int setMinutes = starTime.getHours()*60+starTime.getMinutes();
			if(currentMinutes < setMinutes){
				index = i;
				break;
			}
		}
    	Map<String, Object> map = null;
    	if(module==4){
    		int currentTime = date.getHours()*60 + date.getMinutes();//当前回收预约下单时间
    		int timeLine = getTimeLine(configMap.get("timeLine").toString());//废品回收预约时间临界处理
        	if(currentTime < timeLine){
    	    	//当天的预约时间
    	    	if(index >= 0 ){
    	    		map = new HashMap<>();
    	    		map.put("date", sdf.format(date));
    	    		map.put("value", result.subList(index, result.size()));
    	    		list.add(map);
    	    	}
        	}
    	}else{
    		//当天的预约时间
	    	if(index >= 0 ){
	    		map = new HashMap<>();
	    		map.put("date", sdf.format(date));
	    		map.put("value", result.subList(index, result.size()));
	    		list.add(map);
	    	}
    	}
    	//获取六天的的预约时间
    	for (int i = 1; i <= 2; i++) {
    		map = new HashMap<>();
    		map.put("date", sdf.format(getDateAfter(date, i)));
    		map.put("value", result);
    		list.add(map);
		}
		return list;
    }
    public Date getDateAfter(Date date,int day){
    	Calendar calendar =Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+day);
    	return calendar.getTime();
    }
    
    //卖废品预约时间界限的转化
    private int getTimeLine(String timeLine){
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//（时间戳转化为date类型）
        long lt = new Long(timeLine);
        Date date = new Date(lt);
		@SuppressWarnings("deprecation")
		int line = date.getHours()*60 + date.getMinutes();
		return line;
    }
}
