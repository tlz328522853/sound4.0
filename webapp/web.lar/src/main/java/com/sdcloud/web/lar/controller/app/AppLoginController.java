package com.sdcloud.web.lar.controller.app;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.Producer;
import com.sdcloud.api.authority.service.CaptchaService;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.UserLogin;
import com.sdcloud.api.lar.entity.VerifyCode;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.api.lar.service.LoginService;
import com.sdcloud.api.lar.service.VerifyCodeService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.framework.util.GetRequestIPUtil;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.SendPhoneMessage;

@RestController
@RequestMapping("/app/authority")
public class AppLoginController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	//注册 和 找回密码使用
	private static final String CAPTCHA_KEY = "APP_CAPTCHA:";
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private LarClientUserService larClientUserService;
	@Autowired
	private VerifyCodeService verifyCodeService;
	@Autowired
	private Producer captchaProducer;
	@Autowired
	private CaptchaService captchaService;

	private String getCaptchaKey(HttpServletRequest request, String key) {
		String ip=GetRequestIPUtil.getIpAddr(request);
		logger.info("success 验证码：>>"+ip);   
		return CAPTCHA_KEY + ip + "_" + key;
	}

	@RequestMapping("/captcha")
	public String generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a jpeg
		response.setContentType("image/jpeg");
		// create the text for the image
		String capText = captchaProducer.createText();
		// 转为小写
		capText = capText.toLowerCase();
		// store the text in the session
		// request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY,
		// capText);
		// 登录IP与验证码缓存
		String key = getCaptchaKey(request, capText);
		String value = "1";
		captchaService.add(key, value);

		// create the image with the text
		BufferedImage bi = captchaProducer.createImage(capText);
		ServletOutputStream out = response.getOutputStream();
		// write the data out
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
		return null;
	}


	
	/**
	 * // 登陆
	 * 用未注册的手机号登录时，应该提示用户该手机号未注册
	 * @author jzc 2016年6月22日
	 * @param userLogin
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO userLogin(@RequestBody(required = false) UserLogin userLogin) {
		try {
			Map<String, Object> authorityInfo = loginService.authorityUser(userLogin);
			int code = Integer.valueOf(String.valueOf(authorityInfo.get("code")));
			String message = authorityInfo.get("message").toString();
			if (AppCode.SUCCESS == code) {
				return ResultDTO.getSuccess(code, message, authorityInfo.get("user"));
			} else {
				return ResultDTO.getFailure(code, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO register(@RequestBody(required = false) LarClientUser larClientUser) {
		try {
			
			if(larClientUser.getCode()==null){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "请输入短信验证码");
			}
			VerifyCode verifyCode =verifyCodeService.findCode(larClientUser.getPhone());
			if(verifyCode==null){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码不正确");
			}
			if(!verifyCode.getCode().equals(larClientUser.getCode())){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码不正确");
			}
			if(verifyCode.getEndTime().before(new Date())){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码过期");
			}
			LarClientUser c = larClientUserService.findByPhone(larClientUser.getPhone());
			if (c != null) {
				if(c.getEnable() == 1){
					return ResultDTO.getFailure(AppCode.RESISTER_REPEAT, "该手机号已被禁用，请联系客服");
				}else{
					return ResultDTO.getFailure(AppCode.RESISTER_REPEAT, "该手机号已被注册，您可以直接登录");
				}
			}
			
			if(null != larClientUser.getRegCity()){//用户所有城市初始化为 注册城市
				larClientUser.setCityId(Integer.valueOf(larClientUser.getRegCity().toString()));
				larClientUser.setCityName(larClientUser.getRegCityName());
			}
			boolean flag = larClientUserService.insertSelective(larClientUser);
			if (flag) {
				UserLogin userLogin = new UserLogin();
				userLogin.setUserAccount(larClientUser.getPhone());
				userLogin.setPassWord(larClientUser.getPassWord());
				userLogin.setAppUUID("200");
				Map<String, Object> authorityInfo = loginService.authorityUser(userLogin);
				int code = Integer.valueOf(String.valueOf(authorityInfo.get("code")));
				String message = authorityInfo.get("message").toString();
				if (AppCode.SUCCESS == code) {
					return ResultDTO.getSuccess(code, "注册成功", authorityInfo.get("user"));
				} else {
					return ResultDTO.getFailure(code, message);
				}
			} else {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "注册用户失败，请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "注册用户失败，请重试");
		}
	}

	/**
	 * 重置密碼
	 * 
	 * @param larClientUser
	 * @return
	 */
	@RequestMapping(value = "/resetPass", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO resetPass(@RequestBody(required = false) LarClientUser larClientUser) {
		try {
			if(larClientUser.getCode()==null){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "请输入短信验证码");
			}
			VerifyCode verifyCode =verifyCodeService.findCode(larClientUser.getPhone());
			if(verifyCode==null){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码不正确");
			}
			if(!verifyCode.getCode().equals(larClientUser.getCode())){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码不正确");
			}
			if(verifyCode.getEndTime().before(new Date())){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "短信验证码过期");
			}
			LarClientUser c = larClientUserService.findByPhone(larClientUser.getPhone());
			if (c == null) {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "用户不存在");
			}
			if(c.getEnable() == 1){
				return ResultDTO.getFailure(AppCode.RESISTER_REPEAT, "该手机号已被禁用，请联系客服");
			}
			larClientUser.setId(c.getId());
			boolean flag = larClientUserService.updateByExampleSelective(larClientUser);
			if (flag) {
				UserLogin userLogin = new UserLogin();
				userLogin.setUserAccount(larClientUser.getPhone());
				userLogin.setPassWord(larClientUser.getPassWord());
				userLogin.setAppUUID("200");
				Map<String, Object> authorityInfo = loginService.authorityUser(userLogin);
				int code = Integer.valueOf(String.valueOf(authorityInfo.get("code")));
				String message = authorityInfo.get("message").toString();
				if (AppCode.SUCCESS == code) {
					return ResultDTO.getSuccess(code, "重置密码成功", authorityInfo.get("user"));
				} else {
					return ResultDTO.getFailure(code, "重置密码失败,请重试");
				}
			} else {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "重置密码失败,请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "重置密码失败,请重试");
		}
	}

	/**
	 * 修改：已注册的手机号进行注册时，点击“获取验证码”，应该提示用户该手机号已注册。
	 * 生成验证码并发送给客户
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/code/{phone}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO code(@PathVariable String phone,String captcha
			  ,HttpServletRequest request){
		try {
			if(!validateCaptcha(captcha, request)){
				logger.info("faild>>"+phone+"：输入图片验证码："+captcha+"，验证错误！");
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "图片验证码不正确，请重新输入");
			}
			logger.info("success>>"+phone+"：输入图片验证码："+captcha+"，验证通过！");
			if(!Constant.isMobileNO(phone)){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "手机号不正确！");
			}
			VerifyCode verifyCode = verifyCodeService.createCode(phone);
			if (verifyCode == null) {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "生成短信验证码未成功！");
			} else {
				logger.info("success>>"+phone+"：发送短信验证码......");
				StringBuilder sb = new StringBuilder();
				sb.append("欢迎使用好嘞，验证码：");
				sb.append(verifyCode.getCode());
				sb.append(",好嘞将给您提供优质的服务。");
				SendPhoneMessage.sendPhoneMessage(phone, sb.toString());
				return ResultDTO.getSuccess(AppCode.SUCCESS, "获取短信验证码成功",0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "服务器维护中...！");
		}
		

	}
	
	
	/**
	 * 修改：已注册的手机号进行注册时，点击“获取验证码”，应该提示用户该手机号已注册。
	 * 生成验证码并发送给客户
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/codeRegist/{phone}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO codeRegist(@PathVariable String phone,String captcha
			  ,HttpServletRequest request){
		try {
			if(!validateCaptcha(captcha, request)){
				logger.info("faild>>"+phone+"：输入图片验证码："+captcha+"，验证错误！");
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "图片验证码不正确，请重新输入");
			}
			logger.info("success>>"+phone+"：输入图片验证码："+captcha+"，验证通过！");
			if(!Constant.isMobileNO(phone)){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "手机号不正确！");
			}
			int num=larClientUserService.countByPhone(phone);
			if(num>0){
				return ResultDTO.getFailure(105041, "该手机号已经注册！");
			}
			VerifyCode verifyCode = verifyCodeService.createCode(phone);
			if (verifyCode == null) {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "生成短信验证码未成功！");
			} else {
				logger.info("success>>"+phone+"：发送短信验证码......");
				StringBuilder sb = new StringBuilder();
				sb.append("欢迎使用好嘞，验证码：");
				sb.append(verifyCode.getCode());
				sb.append(",好嘞将给您提供优质的服务。");
				SendPhoneMessage.sendPhoneMessage(phone, sb.toString());
				return ResultDTO.getSuccess(AppCode.SUCCESS, "获取短信验证码成功",0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "服务器维护中...！");
		}
		

	}
	
	/**
	 * 修改：找回密码的手机号进行找回时，点击“获取验证码”，应该提示用户该手机号未注册。
	 * 生成验证码并发送给客户
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/codeReset/{phone}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO codeReset(@PathVariable String phone,String captcha
			  ,HttpServletRequest request){
		try {
			if(!validateCaptcha(captcha, request)){
				logger.info("faild>>"+phone+"：输入图片验证码："+captcha+"，验证错误！");
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "图片验证码不正确，请重新输入");
			}
			logger.info("success>>"+phone+"：输入图片验证码："+captcha+"，验证通过！");
			if(!Constant.isMobileNO(phone)){
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "手机号不正确！");
			}
			int num=larClientUserService.countByPhone(phone);
			if(num==0){
				return ResultDTO.getFailure(105041, "该手机号尚未注册！");
			}
			VerifyCode verifyCode = verifyCodeService.createCode(phone);
			if (verifyCode == null) {
				return ResultDTO.getFailure(AppCode.BIZ_DATA, "生成短信验证码未成功！");
			} else {
				logger.info("success>>"+phone+"：发送短信验证码......");
				StringBuilder sb = new StringBuilder();
				sb.append("欢迎使用好嘞，验证码：");
				sb.append(verifyCode.getCode());
				sb.append(",好嘞将给您提供优质的服务。");
				SendPhoneMessage.sendPhoneMessage(phone, sb.toString());
				return ResultDTO.getSuccess(AppCode.SUCCESS, "获取短信验证码成功",0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "服务器维护中...！");
		}
		

	}
	
	/**
	 * 判断 验证 验证码 是否正确
	 * @author jzc 2016年8月30日
	 * @param captcha
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean validateCaptcha(String captcha,HttpServletRequest request) throws Exception{
			
		if (captcha == null || "".equals(captcha.trim())) {
			return false;
		}
		// 登录IP与验证码缓存中获取
		// 转为小写
		captcha = captcha.toLowerCase();
		String key = getCaptchaKey(request, captcha);
		String val = captchaService.get(key);
		if (StringUtils.isEmpty(val)) {
			return false;
		}
		// 删除缓存中的验证码信息
		captchaService.remove(key);
		return true;
	}
	
	/**
	 * 验证手机号 频率次数
	 * SET类型保存 redis key(APP:MOBILE:13345674567) value(当前时间 纳秒数)
	 * 失效时间：24小时
	 * @author jzc 2016年8月31日
	 * @return
	 */
	public void validateMobileRate(String phone,HttpServletRequest request){
		String ip= GetRequestIPUtil.getIpAddr(request);
		//一分钟 内允许的频率，只能一次
			//1.如果存在，获取最新的 手机号记录
			//2.比较是否在一分钟范围内
		
		//24小时内 允许的 次数
			//1.根据手机号 获取 还没有 失效的 记录数
			//2.比较是否 在 允许的次数之内，如果允许，保存通过；不允许，提示用户
	}
	
	/**
	 * 验证IP 频率次数
	 * SET类型保存 redis key(APP:IP:192.168.1.1) value(当前时间 纳秒数)
	 * 失效时间：24小时
	 * @author jzc 2016年8月31日
	 * @return
	 */
	public void validateIpRate(HttpServletRequest request){
		//IP 也需要验证频率 和 次数，规则 同上 需要 配置
	}
}
