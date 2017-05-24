package com.sdcloud.web.lar.controller.app;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.biz.lar.util.XingeAppUtils;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.tencent.xinge.XingeApp;

/**
 * Created by 王洪生 on 2016/7/8
 * 业务员端
 */
@RequestMapping("app/appXinge")
@RestController
public class AppXingeController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value="/deleteToken",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO deleteToken(@RequestBody(required=false) Map<String, Object> map){
		try {
			logger.info("Enter the :{} method  map:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),map);
			
			if(!map.isEmpty()){
				String account = map.get("account")+"";
				int type = (Integer)map.get("type");
				XingeApp xingeApp = XingeAppUtils.getXingeApp(type);
		    	JSONObject result = xingeApp.queryTokensOfAccount(account);
		    	JSONObject tokens = (JSONObject)result.get("result");
		    	boolean b = tokens.isNull("tokens");
		    	if(!b){
		    		JSONArray array = (JSONArray)tokens.get("tokens");
		    		for (int i = 0; i < array.length(); i++) {
		    			String token = (String)array.get(i);
		    			xingeApp.deleteTokenOfAccount(account, token);
					}
		    	}
		    	return ResultDTO.getSuccess(AppCode.SUCCESS, "删除成功",true);
			}
	    	return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数不能为空!");
		} catch (Exception e) {
			logger.error("method {} execute error, map:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), map, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}

}
