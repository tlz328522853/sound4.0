package com.sdcloud.web.lar.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.Version;
import com.sdcloud.api.lar.entity.XingeEntity;
import com.sdcloud.api.lar.service.VersionService;
import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

/**
 * Created by 韩亚辉 on 2016/4/21.
 */
@Controller
@RequestMapping("/api/version")
public class VersionController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private VersionService versionService;
    @Autowired
    private UserService userService;
    @Autowired
	private XingeAppUtils xingeAppUtils;

    @RequestMapping("save")
    @ResponseBody
    public ResultDTO save(@RequestBody(required = false) Version version, HttpServletRequest request) {
        try {
        	logger.info("Enter the :{} method  version:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),version);
        	
            Object userId=request.getAttribute("token_userId");
            if(null!=userId&&userId!=""){
                User user=userService.findByUesrId(Long.valueOf(userId+""));
                version.setCreateUser(user.getUserId());
            }
            version.setId(UUIDUtil.getUUNum());
            version.setCreateDate(new Date());
            Boolean save = versionService.save(version);
            if(save){
            	String detail = "最新版本号为:"+version.getVersion();
            	XingeEntity xingeEntity = new XingeEntity();
            	//101进入首页(不进入消息中心)
            	xingeEntity.setGrabOrder(101);
				xingeEntity.setTitle("好嘞社区");
				xingeEntity.setContent(detail);
				//TODO whs 需要测试一下,需要异步
				switch (version.getSource()) {
				case "1":
					xingeAppUtils.pushAllDevice(xingeEntity,2);
					break;
				case "2":
					xingeAppUtils.pushAllDeviceIOS(xingeEntity,4);
					break;
				case "3":
					xingeAppUtils.pushAllDevice(xingeEntity,1);
					break;
				case "4":
					xingeAppUtils.pushAllDeviceIOS(xingeEntity,3);
					break;
				}
            }
            return ResultDTO.getSuccess(200, "获取成功", save);
        } catch (Exception e) {
        	logger.error("method {} execute error, version:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), version, e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }

    @RequestMapping("findAll")
    @ResponseBody
    public ResultDTO findAll(@RequestBody(required = false) LarPager<Version> larPager) {
        try {
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
        	
            return ResultDTO.getSuccess(200, "获取成功", versionService.findAll(larPager));
        } catch (Exception e) {
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }
    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }
    
    @ResponseBody
   	@RequestMapping(value = "/newVersion", method = RequestMethod.POST)
    public ResultDTO getNewVersion(@RequestBody(required = false) Map<String,Object> map) {
        try {
            int source=Integer.parseInt(map.get("source")+"");
            return ResultDTO.getSuccess(AppCode.SUCCESS, "获取成功", versionService.getNewVersion(source));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
        }
    }
}
