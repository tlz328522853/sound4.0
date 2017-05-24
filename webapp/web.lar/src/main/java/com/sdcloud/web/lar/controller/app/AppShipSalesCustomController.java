package com.sdcloud.web.lar.controller.app;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.SendPhoneMessage;

/**
 * app端  物流（快件揽收）业务员的客户接口
 * @author jzc
 * 2016年6月18日
 */
@RestController
@RequestMapping("/app/salescustom")
public class AppShipSalesCustomController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	private ShipSalesCustomService shipSalesCustomService;
	@Autowired
	private LarClientUserService larClientUserService;
	
	/**
	 * 检索客户的信息（通过手机号）
	 * 过滤非该城市的信息
	 * @author jzc 2016年6月18日
	 * @param pager
	 * @return
	 */
	@RequestMapping(value="/getCustomInfo",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO getCustomInfo(@RequestBody(required=false) LarClientUser larClientUser){
		try {
			if(larClientUser!=null&&larClientUser.getOrg()!=null
					&&StringUtils.isNotEmpty(larClientUser.getCinfo())){
				
				List<LarClientUser> matchUserList=larClientUserService.matchLarClientUser(larClientUser);
				if(matchUserList.size()>0){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"请求成功！",matchUserList);
				}else{
					return ResultDTO.getFailure(AppCode.BAD_REQUEST, "您输入的手机号码不存在！");
				}
				
			}else {
				logger.warn("非法请求，请重新尝试！");
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	
	/**
	 * 业务员 添加 客户信息
	 * @author jzc 2016年6月18日
	 * @param salesCustom
	 * @return
	 */
	
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) LarClientUser larClientUser){
		try {
			if (larClientUser != null && larClientUser.getCreateUser()!=null
					&& larClientUser.getOrg()!=null
					&& StringUtils.isNotEmpty(larClientUser.getName())
					&& StringUtils.isNotEmpty(larClientUser.getPhone())
					&& StringUtils.isNotEmpty(larClientUser.getAddress())
					&& StringUtils.isNotEmpty(larClientUser.getAddressDetail())
					&&Constant.isMobileNO(larClientUser.getPhone())
					&& null !=larClientUser.getOrg()) {
				
				if(larClientUser.getPhone() != null){
					int count = larClientUserService.countByPhone(larClientUser.getPhone());
					if(count > 0){
						return ResultDTO.getFailure(AppCode.BIZ_ERROR,"手机号已被使用过,请添加新号码!"); 
					}
				}
				//添加用户密码，密码为手机后6位
				String passWord = larClientUser.getPhone().substring(5);
				larClientUser.setPassWord(passWord);
				
				boolean insertFlag = larClientUserService.insertFromSalesCustom(larClientUser);
				if (insertFlag) {
					//larClientUser.setPassWord(null);
					logger.info("添加成功！");
					StringBuffer returnPointsMess=new StringBuffer();
					returnPointsMess.append( "尊敬的用户，您的好嘞社区账户是:");
					returnPointsMess.append(larClientUser.getPhone());
					returnPointsMess.append("，密码是 "+passWord);
					returnPointsMess.append("，敬请妥善保存。卖废品，寄快递，用好嘞，关注微信公众号：soundhaolei。 ");
					SendPhoneMessage.sendPhoneMessage(larClientUser.getPhone(),returnPointsMess.toString());
						
					return ResultDTO.getSuccess(AppCode.SUCCESS,"添加成功！",larClientUser);
				} else {
					logger.warn("添加失败！");
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "添加失败");
				}
			} else {
				logger.warn("非法请求，请重新尝试！");
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
		}
	}
	
	
	/**
	 * 业务员 添加 客户信息
	 * @author jzc 2016年6月18日
	 * @param salesCustom
	 * @return
	 */
//	@RequestMapping(value="/save",method=RequestMethod.POST)
//	@ResponseBody
//	public ResultDTO save(@RequestBody(required=false) ShipSalesCustom salesCustom){
//		try {
//			if(salesCustom!= null&&StringUtils.isNotEmpty(salesCustom.getUserName())
//					&&StringUtils.isNotEmpty(salesCustom.getContact())
//					&&salesCustom.getOrg()!=null
//					&&salesCustom.getCreateUser()!=null
//					&&StringUtils.isNotEmpty(salesCustom.getAddress())
//					&&StringUtils.isNotEmpty(salesCustom.getDetail())){
//				//是否能够通过token   得到业务员的ID
//				salesCustom.setEnable(0);
//				boolean flag = shipSalesCustomService.save(salesCustom);
//				if(flag){
//					return ResultDTO.getSuccess(AppCode.SUCCESS,"添加成功！",salesCustom);
//				}else{
//					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "添加失败");
//				}
//			}else{
//				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常");
//		}
//	}
	
}
