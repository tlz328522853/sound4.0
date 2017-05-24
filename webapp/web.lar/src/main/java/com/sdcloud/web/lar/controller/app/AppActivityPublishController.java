package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.ActivityPublish;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.service.ActivityPublishService;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ServerPropUtil;
import com.sdcloud.web.lar.util.WebViewUtils;

@RestController
@RequestMapping("/app/activity")
public class AppActivityPublishController {

    @Autowired
    private ActivityPublishService activityPublishService;
    @Autowired
    private CityService cityService;

    /**
     * 获取活动发布
     *
     * @param larPager
     * @return
     */
    @RequestMapping("/findByCity")
    @ResponseBody
    public ResultDTO findByCity(@RequestBody(required = false) LarPager<ActivityPublish> larPager) {
        try {
            Map<String, Object> map = larPager.getExtendMap();
			if(!map.isEmpty()){
				larPager.setOrderBy("createDate");
	            larPager.setOrder("desc");
				
				City city = cityService.selectByCityId(Long.valueOf(map.get("city") + ""));
				List<Long> ids = new ArrayList<>();
				if(null != city){
					ids.add(city.getOrg());
					larPager = activityPublishService.findByOrgIds(larPager, ids);
				}
			}
            
            //构造详情url
            String serverAddr = ServerPropUtil.getInstance().getProp("httpserver") + "/app/activity/detail/";
            for (ActivityPublish ac : larPager.getResult()) {
                ac.setDetailUrl(serverAddr + ac.getId());
            }
            return ResultDTO.getSuccess(AppCode.SUCCESS, "成功获取周边活动", larPager);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    /**
     * 获取活动发布的详情
     *
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void getDetailById(@PathVariable Long id, HttpServletResponse response) {
        try {
            ActivityPublish activity = activityPublishService.getById(id, null);
            WebViewUtils.printView(response, activity.getDetail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
