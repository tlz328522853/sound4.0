package com.sdcloud.web.sa.controller;

import java.util.HashMap;

import com.sdcloud.api.lar.service.shipAnalysis.AddService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.framework.entity.ResultDTO;

/**
 * Created by luorongjie on 2017/4/21.
 *  新增接口
 */
@RestController
@RequestMapping("/shipAnalysis/add")
public class AddController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AddService addService;

    @RequestMapping(value="/addStock")
    @ResponseBody
    public ResultDTO outStock(@RequestBody HashMap<String, Object> stock) {
    	try{
            addService.addStock(stock);
    		return ResultDTO.getSuccess(null, "保存成功！");
        }catch (Exception e){
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }

    }

    @RequestMapping(value="/addBrand")
    @ResponseBody
    public ResultDTO addBrand(@RequestBody HashMap<String, Object> brand) {
        try{
            addService.addBrand(brand);
            return ResultDTO.getSuccess(null, "保存成功！");
        }catch (Exception e){
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }

    @RequestMapping(value="/addSku")
    @ResponseBody
    public ResultDTO addSku(@RequestBody HashMap<String, Object> sku) {
        try{
            addService.addSku(sku);
            return ResultDTO.getSuccess(null, "保存成功！");
        }catch (Exception e){
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
}
