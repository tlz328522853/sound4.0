package com.sdcloud.web.lar.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.sdcloud.api.lar.entity.Detail;
import com.sdcloud.api.lar.service.DetailService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.web.lar.util.WebViewUtils;
@RestController
@RequestMapping("/app/common")
public class AppDetailController {

	@Autowired
	private DetailService detailService;
	/**
	 * 获取详情
	 * type:1-->客服电话  2-->快递条款  3-->调度电话  4-->关于好嘞 5-->资费说明
	 * @param larPager
	 * @return
	 */
	@RequestMapping(value="/detail/{id}",method=RequestMethod.GET)
	@ResponseBody
	public void detail( @PathVariable Long id, HttpServletResponse response) {
		
		try {
			LarPager<Detail> larPager = new LarPager<>();
			Map<String , Object> map = new HashMap<>();
			map.put("type", id);
			larPager.setParams(map);
			List<Detail> list = detailService.findAll(larPager).getResult();
			if(list != null && list.size()>0){
				Detail detail = list.get(0);
				WebViewUtils.printView(response, detail.getDetail());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
