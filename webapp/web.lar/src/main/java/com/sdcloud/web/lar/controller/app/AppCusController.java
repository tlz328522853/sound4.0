package com.sdcloud.web.lar.controller.app;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.APIConnectionException;
import com.pingplusplus.exception.APIException;
import com.pingplusplus.exception.AuthenticationException;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;
import com.sdcloud.api.lar.entity.OrderCharge;
import com.sdcloud.api.lar.entity.ShipmentCitySend;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.service.ShipmentCitySendService;
import com.sdcloud.api.lar.service.ShipmentHelpMeBuyService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.OrderUtils;
/**
 * 客户端接口
 * @author wrs
 *
 */
@RestController
@RequestMapping("/app/cus")
public class AppCusController {
    @Autowired
    private VoucherService voucherService;

	/**
	 * 同城送保存订单
	 * 
	 * @param orderCharge
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findVoucher", method = RequestMethod.POST)
	public ResultDTO findVoucher(@RequestBody(required = false) Voucher voucher) {
		try {
			return ResultDTO.getSuccess(AppCode.SUCCESS, "获取优惠券列表成功", voucherService.findByCus(voucher.getCustomerId()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
		}
	}
}
