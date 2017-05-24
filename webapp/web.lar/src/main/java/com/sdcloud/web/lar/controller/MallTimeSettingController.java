package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.MallTimeSetting;
import com.sdcloud.api.lar.service.MallTimeSettingService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 商城时间段设置
 * Created by dingx on 2016/7/11.
 */
@RestController
@RequestMapping("/api/mallTimeSetting")
public class MallTimeSettingController {

    @Autowired
    private MallTimeSettingService mallTimeSettingService;

    /**
     * 列表
     *
     * @param pager
     * @return
     */
    @RequestMapping("/findAll")
    public ResultDTO timeSettingList(@RequestBody(required = false) LarPager<MallTimeSetting> pager) {
        try {
            return ResultDTO.getSuccess(200, mallTimeSettingService.findAll(pager));
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
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
