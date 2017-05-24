package com.sdcloud.web.lar.controller.app;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.sdcloud.api.lar.entity.RecycleBag;
import com.sdcloud.api.lar.service.RecycleBagService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/recycleBag")
public class AppRecycleBagController {
	
	@Autowired
	private RecycleBagService recycleBagService;
	
	// APP增加订单
	@RequestMapping(value = "/saveRecycleBag", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) RecycleBag recycleBag) throws Exception {
		try {
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "提交失败！该功能暂未开放！请下载新版本。");
			
			/*if (recycleBag != null && (recycleBag.getId()==null || recycleBag.getId().length()<=0)) {
				boolean insertUserGetId = recycleBagService.insertSelective(recycleBag);
				if (insertUserGetId) {
					return ResultDTO.getSuccess(AppCode.SUCCESS, "预约成功！");
				} else {
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "预约失败！");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	// 查询列表
	@RequestMapping("/getRecycleBags")
	@ResponseBody
	public ResultDTO getRecycleBags(@RequestBody(required = false) LarPager<RecycleBag> larPager) throws Exception {
		try {
			if(larPager.getParams()==null && larPager.getParams().containsKey("appUserId")==false && larPager.getParams().get("appUserId")==null){
				return ResultDTO.getFailure(AppCode.BIZ_ERROR, "非法用户！");
			}
			larPager = recycleBagService.selectByExample(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
}