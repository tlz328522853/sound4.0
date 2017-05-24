package com.sdcloud.web.lar.controller;

import com.sdcloud.api.lar.entity.ShipAnaView;
import com.sdcloud.api.lar.service.ShipAnaViewService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by luorongjie on 2017/5/13.
 */

@RestController
@RequestMapping("/api/shipAnalysis")
public class shipAnalysisController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShipAnaViewService shipAnaViewService;

    // 查询物流仓、回收仓 柱形图
    @RequestMapping("/getShipAndReco")
    @ResponseBody
    public ResultDTO getShipAndReco(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getShipAndReco(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 查询品牌数量 柱形图
    @RequestMapping("/getBrand")
    @ResponseBody
    public ResultDTO getBrand(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getBrand(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 查询SKU数量 柱形图
    @RequestMapping("/getSku")
    @ResponseBody
    public ResultDTO getSku(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getSku(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }


    // 查询仓储费数量 柱形图
    @RequestMapping("/getStockfee")
    @ResponseBody
    public ResultDTO getStockfee(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getStockfee(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 查询库存货款 柱形图
    @RequestMapping("/getStockPrice")
    @ResponseBody
    public ResultDTO getStockPrice(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getStockPrice(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }


    // 查询配送费、订单数 柱形图
    @RequestMapping("/getDelivery")
    @ResponseBody
    public ResultDTO getDelivery(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getDelivery(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 查询汇总表
    @RequestMapping("/getCollect")
    @ResponseBody
    public ResultDTO getCollect(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getCollect(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 查询汇总表("单独查价格")
    @RequestMapping("/getStockPriceCollect")
    @ResponseBody
    public ResultDTO getStockPriceCollect(@RequestBody(required = false) Map<String,String> monthMap) throws Exception {
        try {
            List<ShipAnaView> views = null;
            String month = monthMap.get("month");
            if(null != month) {
                views = shipAnaViewService.getStockPriceCollect(month);
                return ResultDTO.getSuccess(views);
            } else {
                logger.warn("请求参数有误:method {}",
                        Thread.currentThread().getStackTrace()[1].getMethodName());
                return ResultDTO.getFailure(AppCode.BIZ_DATA,"参数有误！");
            }
        } catch (Exception e) {
            logger.error("系统处理异常:method {}, Exception:{}",
                    Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

}
