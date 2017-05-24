package com.sdcloud.web.lar.controller.app;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.ExchangeInfo;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.ShoppingCart;
import com.sdcloud.api.lar.service.CommodityService;
import com.sdcloud.api.lar.service.ExchangeInfoService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;

@RestController
@RequestMapping("/app/exchangeInfo")
public class AppExchangeInfoController {
	
	@Autowired
	private ExchangeInfoService exchangeInfoService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private LarClientUserService larClientUserService;
	@Autowired
	private CommodityService commodityService;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 增加
	@RequestMapping(value = "/saveExchangeInfo", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) ExchangeInfo exchangeInfo) throws Exception {
		try {
			logger.info("Enter the :{} method ", Thread.currentThread() .getStackTrace()[1].getMethodName());
			if (exchangeInfo != null && (exchangeInfo.getId() == null || exchangeInfo.getId().length() <= 0)) {
				//先判断商品是否已下架，如果已下架，直接返回错误信息。
				boolean isDownShelf = commodityService.isDownShelf(exchangeInfo);
				if(!isDownShelf){
					return ResultDTO.getFailure(AppCode.BIZ_DATA, "部分商品已下架，请重新选择兑换商品！");
				}
				double points = 0;
				double money = 0;
				List<ShoppingCart> shoppingCarts = exchangeInfo.getShoppingCarts();
				if(shoppingCarts==null || shoppingCarts.size()<=0){
					logger.error("err method, no shoppingCarts ");
					return ResultDTO.getSuccess(AppCode.SUCCESS, "没有兑换商品！");
				}
				for (ShoppingCart shoppingCart : shoppingCarts) {
					//如果是积分支付就计算所有商品的总积分
					if(shoppingCart.getCommodity().getMoneyUnit()==1){
						points += shoppingCart.getNumber()*shoppingCart.getCommodity().getUnitPrice();
					}else{//计算钱
						money += shoppingCart.getNumber()*shoppingCart.getCommodity().getUnitPrice();
					}
				}
				//消费积分与消费金额关联
				exchangeInfo.setMoney(new BigDecimal(money));
				exchangeInfo.setIntegral(points+"");
				boolean insertUserGetId=false;
				if((points>0 && money<=0) || (points>0 && money>0)){
					LarClientUser myPoints = larClientUserService.getMyPoints(exchangeInfo.getAppUserId());
					//先判断用户积分是否足够
					if(myPoints.getNowPoints()>=points){
						insertUserGetId = exchangeInfoService.insertSelective(exchangeInfo);
						//减掉用户积分
						if(insertUserGetId){
							larClientUserService.updateUserPoints(points,exchangeInfo.getAppUserId());
						}
						String detail = "恭喜您成功兑换以下商品！共消费"+points+"积分。";
						return ResultDTO.getSuccess(AppCode.SUCCESS, detail);
					}else{
						logger.error("err method, : 积分不足" );
						return ResultDTO.getFailure(AppCode.BIZ_SCORE, "积分不足！");
					}
				}
				if(points<=0 && money>0){
					insertUserGetId = exchangeInfoService.insertSelective(exchangeInfo);
					String detail = "恭喜您成功兑换以下商品！共消费"+points+"元。";
					return ResultDTO.getSuccess(AppCode.SUCCESS, detail);
				}
				if (insertUserGetId=false) {
					logger.error("err method, insertUserGetId: false" );
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "预约兑换失败！");
				}
			} else {
				logger.error("err method, exchangeInfo.getId is empty");
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} executeerror, exchangeInfo:{} ", Thread.currentThread().getStackTrace()[1].getMethodName(), exchangeInfo, e);
			//e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
		return ResultDTO.getFailure(AppCode.BIZ_ERROR, "预约兑换失败！");
	}
	
	@RequestMapping("/getExchangeInfoByUserId/{userId}")
	@ResponseBody
	public ResultDTO getExchangeInfoByUserId(@PathVariable(value="userId") String userId) throws Exception {
		try {
			if(userId!=null && userId.trim().length()>0){
				List<ExchangeInfo> exchangeInfos = exchangeInfoService.getExchangeInfoByUserId(userId);
				if (exchangeInfos!=null && exchangeInfos.size()>0) {
					return ResultDTO.getSuccess(exchangeInfos);
				} else {
					return ResultDTO.getFailure(200, "该用户没有购买记录！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/selectByExample")
	@ResponseBody
    public ResultDTO selectByExample(@RequestBody(required = false) LarPager<ExchangeInfo> larPager) {
        try {
            if(larPager != null){
            	if(larPager.getOrder() == null){
            		larPager.setOrder("desc");
            	}
            	larPager = exchangeInfoService.selectByExample(larPager, null);
                return ResultDTO.getSuccess(AppCode.SUCCESS,larPager);
            }else{
            	return ResultDTO.getFailure(AppCode.BAD_REQUEST, "参数错误!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
        }
    }
}
