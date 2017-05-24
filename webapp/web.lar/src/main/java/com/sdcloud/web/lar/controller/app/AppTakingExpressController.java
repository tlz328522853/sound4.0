package com.sdcloud.web.lar.controller.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.TakingExpressService;
import com.sdcloud.framework.entity.ResultDTO;

/**
 * 揽件
 * @author luorongjie
 *
 */
@RestController
@RequestMapping(value = "/app/takingExpress")
public class AppTakingExpressController {
	
	@Autowired
	private TakingExpressService takingExpressService;
	
	@RequestMapping(value="/save", method = RequestMethod.POST)
    public ResultDTO save(@RequestBody(required = false) TakingExpress takingExpress) {
        try {
        	List<String> orderNos = new ArrayList<>();
        	orderNos.add(takingExpress.getOrderNo());
        	List<TakingExpress> expresses = takingExpressService.getByOrderNo(orderNos);
        	if(expresses==null || expresses.size() == 0){
        		takingExpress.setTakingDate(new Date());
            	takingExpress.setCreateUser(takingExpress.getTakingMan());
            		if(takingExpressService.save(takingExpress)){
            			return ResultDTO.getSuccess(200,"保存成功!",null);
            		}
            	return ResultDTO.getFailure(500, "保存失败！");
        	}
        	return ResultDTO.getFailure(500, "保存失败,请检查订单号是否已存在.");
        	
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
}
