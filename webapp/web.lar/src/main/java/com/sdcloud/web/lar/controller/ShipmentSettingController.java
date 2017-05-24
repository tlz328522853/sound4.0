package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipmentOrderTime;
import com.sdcloud.api.lar.service.ShipmentOrderTimeService;
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
 * 物流管理--->设置
 * Created by 韩亚辉 on 2016/3/15.
 */
@RestController
@RequestMapping("/api/shipmentSetting")
public class ShipmentSettingController {

    @Autowired
    private ShipmentOrderTimeService shipmentOrderTimeService;

    /**
     * 列表
     *
     * @param pager
     * @return
     */
    @RequestMapping("/findAll")
    public ResultDTO timeSettingList(@RequestBody(required = false) LarPager<ShipmentOrderTime> pager) {
        try {
            return ResultDTO.getSuccess(200, shipmentOrderTimeService.findAll(pager));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    /**
     * 添加
     *
     * @param shipmentOrderTime
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultDTO saveTimeSetting(@RequestBody ShipmentOrderTime shipmentOrderTime, HttpServletRequest request) {
        try {
            if(null==shipmentOrderTime.getStartTime()){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "开始时间不能为空！");
            }
            if(null==shipmentOrderTime.getEndTime()){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "结束时间不能为空！");
            }
            if(shipmentOrderTime.getEndTime().compareTo(shipmentOrderTime.getStartTime())<=0){
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "结束时间必须大于开始时间！");
            }
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentOrderTime.setCreateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "保存成功!",shipmentOrderTimeService.save(shipmentOrderTime));
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
    public ResultDTO updateTimeSetting(@RequestBody ShipmentOrderTime shipmentOrderTime, HttpServletRequest request) {
        try {
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                shipmentOrderTime.setUpdateUser(user.getUserId());
            }
            return ResultDTO.getSuccess(200, "修改成功!",shipmentOrderTimeService.update(shipmentOrderTime));
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
            return ResultDTO.getSuccess(200, "删除成功!",shipmentOrderTimeService.delete(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @Autowired
    private UserService userService;

    private List<ShipmentOrderTime> convert(List<ShipmentOrderTime> list) throws Exception {
        Set<Long> userIds = new HashSet<>();
        List<Long> updateUserIds = new ArrayList<>();
        for (ShipmentOrderTime shipmentOrderTime : list) {
            if (null != shipmentOrderTime.getCreateUser()) {
                userIds.add(shipmentOrderTime.getCreateUser());
            }
            if (null != shipmentOrderTime.getUpdateUser()) {
                userIds.add(shipmentOrderTime.getUpdateUser());
            }
        }
        Map<Long, User> users = new HashMap<>();
        if (userIds.size() > 0) {
            updateUserIds.addAll(userIds);
            users = userService.findUserMapByIds(updateUserIds);
        }
        for (ShipmentOrderTime shipmentOrderTime : list) {
            if (null != users) {
                if (null != shipmentOrderTime.getCreateUser()) {
                    User user = users.get(shipmentOrderTime.getCreateUser());
                    if (null != user) {
                        shipmentOrderTime.setCreateUserName(user.getName());
                    }
                }
                if (null != shipmentOrderTime.getUpdateUser()) {
                    User user = users.get(shipmentOrderTime.getUpdateUser());
                    if (null != user) {
                        shipmentOrderTime.setUpdateUserName(user.getName());
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<ShipmentOrderTime> pager = new LarPager<>();
        pager.setPageSize(10000000);
        LarPager<ShipmentOrderTime> orderTimeLarPager = shipmentOrderTimeService.findAll(pager);
        if (null != orderTimeLarPager && orderTimeLarPager.getResult() != null && orderTimeLarPager.getResult().size() > 0) {
            ExportExcelUtils<ShipmentOrderTime> exportExcelUtils = new ExportExcelUtils<>("预约时间段");
            Workbook workbook = null;
            try {
                List<ShipmentOrderTime> list = this.convert(this.convert(orderTimeLarPager.getResult()));
                workbook = exportExcelUtils.writeContents("预约时间段", list);
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
