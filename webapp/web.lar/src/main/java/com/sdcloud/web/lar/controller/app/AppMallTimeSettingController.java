package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.MallTimeSetting;
import com.sdcloud.api.lar.service.MallTimeSettingService;
import com.sdcloud.api.lar.service.ShipmentOrderTimeService;
import com.sdcloud.api.lar.service.SysConfigService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商城时间段设置
 * Created by dingx on 2016/7/11.
 */
@RestController
@RequestMapping("/app/mallTimeSetting")
public class AppMallTimeSettingController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
   
    @Autowired
 	private SysConfigService sysConfigService;
	
    @Autowired
    private MallTimeSettingService mallTimeSettingService;

    /**
     * 时间列表
     * module值:  2--物流预约时间(一周) 
     * @param pager
     * @return
     * @throws Throwable 
     */
    @RequestMapping("/findAll")
    public ResultDTO timeSettingList(@RequestBody(required = false) LarPager<MallTimeSetting> pager) throws Throwable {
    	try {
        	Map<String, Object> extendMap = pager.getExtendMap();
        	if(pager.getOrder() == null){
        		pager.setOrder("asc");
        	}
        	if(extendMap.get("module") != null){
        		int	module =(Integer) extendMap.get("module");
        		
        		if(module == 2){
        			List<MallTimeSetting> result = mallTimeSettingService.findAll(pager).getResult();
        			List<Map<String, Object>> list = getAppointmentTime(result);
        			return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取物流预约时间",list);
        		}
        		
        		LarPager<MallTimeSetting> mdsPager = new LarPager<>();
            	mdsPager.setToken(pager.getToken());
            	mdsPager.setPageNo(pager.getPageNo());
            	mdsPager.setPageSize(pager.getPageSize());
            	mdsPager.setOrderBy("startDate");
            	mdsPager.setOrder(pager.getOrder());
            	mallTimeSettingService.selectByExample(mdsPager);
            	
        		
        		return ResultDTO.getFailure(AppCode.BAD_REQUEST, "module传参错误！");
        	}else{
        		return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，extendMap传参为空！");
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
        }
    }
    
    
  //获取一周的预约时间
    public List<Map<String, Object>> getAppointmentTime(List<MallTimeSetting> result) throws Throwable{
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
    		//Date starTime = result.get(i).getStartTime();
    		
    		Date starTime = null;
    		if("MallTimeSetting".equals(simpleName)){
    			Field field = result.get(i).getClass().getDeclaredField("startTime");
    			field.setAccessible(true);
    			starTime = (Date) field.get(result.get(i));
    		}
//    		if("MaaDateSetting".equals(simpleName)){
//    			Field field = result.get(i).getClass().getDeclaredField("startDate");
//    			field.setAccessible(true);
//    			starTime = (Date) field.get(result.get(i));
//    		}
    		
    		int setMinutes = starTime.getHours()*60+starTime.getMinutes();
			if(currentMinutes < setMinutes){
				index = i;
				break;
			}
		}
    	Map<String, Object> map = null;
    	/*int timeLine = getTimeLine(configMap.get("timeLine").toString());//预约时间节点的处理
    	if(currentMinutes < timeLine){
	    	//当天的预约时间
	    	if(index >= 0 ){
	    		map = new HashMap<>();
	    		map.put("date", sdf.format(date));
	    		map.put("value", result.subList(index, result.size()));
	    		list.add(map);
	    	}
    	}*/
    	//获取六天的的预约时间
    	for (int i = 1; i <= 1; i++) {
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
    
    //处理预约时间节点
    @SuppressWarnings("deprecation")
	private int getTimeLine(String timeLine){
    	Long lt = new Long(timeLine);
    	Date date = new Date(lt);
    	int line =date.getHours()*60 + date.getMinutes();
    	return line;
    }

    /**
     * 添加
     *
     * @param mallTimeSetting
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO saveTimeSetting(@RequestBody MallTimeSetting mallTimeSetting, HttpServletRequest request) {
        try {
            if(null==mallTimeSetting.getStartTime()){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "开始时间不能为空！");
            }
            if(null==mallTimeSetting.getEndTime()){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "结束时间不能为空！");
            }
            if(mallTimeSetting.getEndTime().compareTo(mallTimeSetting.getStartTime())<=0){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "结束时间必须大于开始时间！");
            }
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                mallTimeSetting.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, mallTimeSettingService.save(mallTimeSetting));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 修改
     *
     * @param mallTimeSetting
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultDTO updateTimeSetting(@RequestBody MallTimeSetting mallTimeSetting, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                mallTimeSetting.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, mallTimeSettingService.update(mallTimeSetting));
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
            return ResultDTO.getSuccess(200, mallTimeSettingService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @Autowired
    private UserService userService;

    private List<MallTimeSetting> convert(List<MallTimeSetting> list) throws Exception {
        Set<Long> userIds = new HashSet<>();
        List<Long> updateUserIds = new ArrayList<>();
        for (MallTimeSetting mallTimeSetting : list) {
            if (null != mallTimeSetting.getCreateUser()) {
                userIds.add(mallTimeSetting.getCreateUser());
            }
            if (null != mallTimeSetting.getUpdateUser()) {
                userIds.add(mallTimeSetting.getUpdateUser());
            }
        }
        Map<Long, User> users = new HashMap<>();
        if (userIds.size() > 0) {
            updateUserIds.addAll(userIds);
            users = userService.findUserMapByIds(updateUserIds);
        }
        for (MallTimeSetting mallTimeSetting : list) {
            if (null != users) {
                if (null != mallTimeSetting.getCreateUser()) {
                    User user = users.get(mallTimeSetting.getCreateUser());
                    if (null != user) {
                    	mallTimeSetting.setCreateUserName(user.getName());
                    }
                }
                if (null != mallTimeSetting.getUpdateUser()) {
                    User user = users.get(mallTimeSetting.getUpdateUser());
                    if (null != user) {
                    	mallTimeSetting.setUpdateUserName(user.getName());
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<MallTimeSetting> pager = new LarPager<>();
        pager.setPageSize(10000000);
        LarPager<MallTimeSetting> orderTimeLarPager = mallTimeSettingService.findAll(pager);
        if (null != orderTimeLarPager && orderTimeLarPager.getResult() != null && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<MallTimeSetting> exportExcelUtils = new ExportExcelUtils<>("商城预约时间段");
            Workbook workbook = null;
            try {
                List<MallTimeSetting> list = this.convert(this.convert(orderTimeLarPager.getResult()));
                workbook = exportExcelUtils.writeContents("商城预约时间段", list);
                String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
                String headStr = "attachment; filename=\"" + fileName + "\"";
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                OutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
}
