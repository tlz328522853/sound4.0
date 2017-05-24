package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.lar.service.VersionService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by 韩亚辉 on 2016/4/21.
 */
@RestController
@RequestMapping("/app/version")
public class AppVersionController {
    @Autowired
    private VersionService versionService;

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
