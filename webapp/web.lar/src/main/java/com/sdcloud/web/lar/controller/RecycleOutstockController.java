package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sdcloud.web.lar.util.OrderUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.RecycleChenkExport;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.service.RecycleOutstockService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;

import io.jsonwebtoken.lang.Collections;

/**
 * lar_recycle_outstock 出库管理
 * 
 * @author luorongjie
 * @date 2016-12-07
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleOutstock")
public class RecycleOutstockController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecycleOutstockService recycleOutstockService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;

	/**
	 * 添加数据
	 * 
	 * @param recycleOutstock
	 *            添加数据
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			if (recycleOutstock != null) {
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));

				recycleOutstock.setRegisterId(user.getUserId());
				recycleOutstock.setRegisterName(user.getName());
				recycleOutstock.setRegisteDate(new Date());
				recycleOutstock.setCreateUser(user.getUserId());
				recycleOutstock.setCreateUserName(user.getName());
				recycleOutstock.setCreateDate(new Date());
				recycleOutstock.setUpdateUserName(user.getName());
				recycleOutstock.setUpdateDate(new Date());

				recycleOutstock.setOutstockStatus((byte) 1);
				recycleOutstock.setAuditStatus((byte) 1);
				recycleOutstock.setCheckStatus((byte) 1);
				recycleOutstock.setEnable((byte) 0);
				recycleOutstock.setVersion(1L);

				//3/29/2017 入库单编码自动生成，生成规则 20位 年月日+12位随机码
				recycleOutstock.setOutstockNo(OrderUtils.generateNumber20());

				recycleOutstock.setOutstockNo(recycleOutstock.getOutstockNo().trim());

				// 修改改出库单 编码不能重复
				Boolean exist = recycleOutstockService.existByOutstockNo(null, recycleOutstock.getOutstockNo());
				if (exist) {
					return ResultDTO.getFailure(500, "添加失败！出库单编码重复。");
				}

				boolean b = recycleOutstockService.save(recycleOutstock);

				if (b) {
					return ResultDTO.getSuccess(null, "添加成功！");
				} else {
					return ResultDTO.getFailure(500, "添加失败。");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (IndexOutOfBoundsException e) {
			return ResultDTO.getFailure(500, "添加失败。该物品规格暂未入过库，无法出库 ！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param recycleOutstock
	 *            更新数据
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			if (recycleOutstock != null) {
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate = new Date();

				boolean b = false;
				if (recycleOutstock.getSoldMoney() != null) {// 对账

					if (recycleOutstock.getCheckStatus() == 3) {// 归档
						recycleOutstock.setCheckStatus((byte) 4);
					} else if (recycleOutstock.getCheckStatus() == 2) {// 结算

						recycleOutstock.setCheckStatus((byte) 3);
						recycleOutstock.setBalanceName(user.getName());
						recycleOutstock.setBalanceDate(newDate);
					} else {

						recycleOutstock.setCheckMenName(user.getName());
						recycleOutstock.setCheckDate(newDate);
						recycleOutstock.setCheckStatus((byte) 2);
					}
					recycleOutstock.setCheckUpdateName(user.getName());
					recycleOutstock.setCheckUpdateDate(newDate);

					b = recycleOutstockService.update(recycleOutstock);

				} else {// 出库

					// 修改改出库单 编码不能重复
					Boolean exist = recycleOutstockService.existByOutstockNo(recycleOutstock.getId(),
							recycleOutstock.getOutstockNo());
					if (exist && recycleOutstock.getEnable() != 1) {
						return ResultDTO.getFailure(500, "操作失败！出库单编码重复。");
					}

					recycleOutstock.setUpdateDate(newDate);
					recycleOutstock.setUpdateUser(user.getUserId());
					recycleOutstock.setUpdateUserName(user.getName());
					b = recycleOutstockService.updateNum(recycleOutstock);// 没有入过库的规格不能出库
					// b = recycleOutstockService.update(recycleOutstock);
				}

				if (b) {
					return ResultDTO.getSuccess(200, "操作成功！", null);
				} else {
					return ResultDTO.getFailure(500, "操作失败！");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (RuntimeException e) {

			return ResultDTO.getFailure(500, "没有入过库的规格不能出库！");

		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 更新数据
	 * 
	 * @param recycleOutstock
	 *            更新duizhang
	 * @return
	 */
	@RequestMapping(value = "/updateCheck", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO updateCheck(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			if (recycleOutstock != null) {
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate = new Date();

				boolean b = false;
				if (recycleOutstock.getSoldMoney() != null) {// 对账

					if (recycleOutstock.getCheckStatus() == 3) {// 归档
						recycleOutstock.setCheckStatus((byte) 4);
					} else if (recycleOutstock.getCheckStatus() == 2) {// 结算

						recycleOutstock.setCheckStatus((byte) 3);
						recycleOutstock.setBalanceName(user.getName());
						recycleOutstock.setBalanceDate(newDate);
					} else {

						recycleOutstock.setCheckMenName(user.getName());
						recycleOutstock.setCheckDate(newDate);
						recycleOutstock.setCheckStatus((byte) 2);
					}
					recycleOutstock.setCheckUpdateName(user.getName());
					recycleOutstock.setCheckUpdateDate(newDate);

					BigDecimal d = new BigDecimal(9999999.99);
					BigDecimal c = new BigDecimal(0.0099);
					if (recycleOutstock.getSaleTotalMoney() != null
							&& (recycleOutstock.getSaleTotalMoney().compareTo(d) == 1
									|| recycleOutstock.getSaleTotalMoney().compareTo(c) == -1)) {// 比较大于
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					if (recycleOutstock.getSoldNum() != null && (recycleOutstock.getSoldNum().compareTo(d) == 1
							|| recycleOutstock.getSoldNum().compareTo(c) == -1)) {// 判断计费数量
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					if (recycleOutstock.getSoldMoney() != null && (recycleOutstock.getSoldMoney().compareTo(d) == 1
							|| recycleOutstock.getSoldMoney().compareTo(c) == -1)) {// 判断应收款
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					b = recycleOutstockService.update(recycleOutstock);

				} else {
					return ResultDTO.getFailure(500, "操作失败！");
				}

				if (b) {
					return ResultDTO.getSuccess(200, "操作成功！", null);
				} else {
					return ResultDTO.getFailure(500, "操作失败！");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (RuntimeException e) {

			return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");

		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 更新对账数据
	 * 
	 * @param recycleOutstock
	 *            更新数据
	 * @return
	 */
	@RequestMapping(value = "/checkUpdate", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO checkUpdate(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			if (recycleOutstock != null) {
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate = new Date();
				// recycleOutstock.setUpdateDate(newDate);

				boolean b = false;
				if (recycleOutstock.getSoldMoney() != null) {// 对账
					if (recycleOutstock.getCheckStatus() == 3) {// 修改结算
						recycleOutstock.setBalanceName(user.getName());
						recycleOutstock.setBalanceDate(newDate);
					} else if (recycleOutstock.getCheckStatus() == 2) {// 修改对账
						recycleOutstock.setCheckMenName(user.getName());
						recycleOutstock.setCheckDate(newDate);
					}
					recycleOutstock.setCheckUpdateName(user.getName());
					recycleOutstock.setCheckUpdateDate(newDate);
					BigDecimal d = new BigDecimal(9999999.99);
					BigDecimal c = new BigDecimal(0.0099);
					if (recycleOutstock.getSaleTotalMoney() != null
							&& (recycleOutstock.getSaleTotalMoney().compareTo(d) == 1
									|| recycleOutstock.getSaleTotalMoney().compareTo(c) == -1)) {// 比较大于
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					if (recycleOutstock.getSoldNum() != null && (recycleOutstock.getSoldNum().compareTo(d) == 1
							|| recycleOutstock.getSoldNum().compareTo(c) == -1)) {// 判断计费数量
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					if (recycleOutstock.getSoldMoney() != null && (recycleOutstock.getSoldMoney().compareTo(d) == 1
							|| recycleOutstock.getSoldMoney().compareTo(c) == -1)) {// 判断应收款
						return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");
					}
					b = recycleOutstockService.update(recycleOutstock);
				}

				if (b) {
					return ResultDTO.getSuccess(200, "操作成功！", null);
				} else {
					return ResultDTO.getFailure(500, "操作失败！");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (RuntimeException e) {

			return ResultDTO.getFailure(500, "请正确输入数字（0.01-999999.99）！");

		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 用于删除图片
	 */
	@RequestMapping(value = "/updateImg", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO updateImg(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			String imgUrl = null;
			if (recycleOutstock != null) {
				Long id = recycleOutstock.getId();
				imgUrl = recycleOutstock.getImgUrl();
				if (imgUrl == null) {
					return ResultDTO.getFailure(500, "操作失败！");
				}
				Boolean b = recycleOutstockService.updateImg(id);

				if (b) {
					return ResultDTO.getSuccess(200, "操作成功！", "操作成功！");
				} else {
					return ResultDTO.getFailure(500, "操作失败！");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (RuntimeException e) {

			return ResultDTO.getFailure(500, "没有入过库的规格不能出库！");

		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 根据机构查询所有
	 * 
	 * @param pager
	 * @return
	 */
	@RequestMapping(value = "/findByOrgIds", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByOrgIds(@RequestBody LarPager<RecycleOutstock> pager) {
		try {
			if (pager != null) {
				Map<String, Object> map = pager.getExtendMap();
				Object orgObj = map.get("org");
				Object includeSubObj = map.get("includeSub");
				if (null == orgObj)
					return ResultDTO.getFailure("参数有误！");
				List<Long> orgList = getOrgList(orgObj, includeSubObj);
				recycleOutstockService.findByOrgIds(pager, orgList);
				return ResultDTO.getSuccess(pager);
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 
	 * @param orgObj
	 *            机构 id 多个以“AAA”拼接
	 * @param includeSubObj
	 *            是否包含子机构
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj, Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if (null != includeSubObj) {
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}

		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for (String orgString : orgArr) {
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds;
	}

	/**
	 * 审核入库
	 * 
	 * @param recycleOutstock
	 *            更新数据
	 * @return
	 */
	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO audit(@RequestBody RecycleOutstock recycleOutstock, HttpServletRequest request) {
		try {
			if (recycleOutstock != null) {

				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate = new Date();
				recycleOutstock.setAuditDate(newDate);
				recycleOutstock.setAuditUser(user.getUserId());
				recycleOutstock.setAuditUserName(user.getName());
				recycleOutstock.setOutstockDate(newDate);
				recycleOutstock.setAuditStatus((byte) 2);
				recycleOutstock.setOutstockStatus((byte) 2);
				recycleOutstock.setUpdateDate(newDate);
				recycleOutstock.setUpdateUserName(user.getName());

				boolean b = recycleOutstockService.audit(recycleOutstock);
				if (b) {
					return ResultDTO.getSuccess(200, "审核成功！", null);
				} else {
					return ResultDTO.getFailure(500, "审核失败！库存不足。");
				}
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}

	}

	/**
	 * 对账导出
	 * 
	 * @param response
	 * @param pager
	 */

	@RequestMapping("/recycleChenkExport")
	public void recycleChenkExport(HttpServletResponse response,
			@RequestBody(required = false) LarPager<RecycleOutstock> pager) {
		List<RecycleOutstock> outstocks = null;
		try {
			pager.setPageSize(1000000);
			Map<String, Object> map = pager.getExtendMap();
			Object orgObj = map.get("org");
			Object includeSubObj = map.get("includeSub");
			List<Long> orgList = getOrgList(orgObj, includeSubObj);
			recycleOutstockService.findByOrgIds(pager, orgList);
			outstocks = pager.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!Collections.isEmpty(outstocks)) {
			ExportExcelUtils<RecycleChenkExport> exportExcelUtils = new ExportExcelUtils<>("出库销售对账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("出库销售对账", this.convert2(outstocks));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
				OutputStream outputStream = response.getOutputStream();
				workbook.write(outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (workbook != null) {
					try {
						workbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 导出时封装orgName
	private List<RecycleChenkExport> convert2(List<RecycleOutstock> outstocks) throws Exception {
		List<Long> orgList = new ArrayList<>();
		List<RecycleChenkExport> exports = new ArrayList<>();

		for (RecycleOutstock outstock : outstocks) {
			if (null != outstock.getOrgId()) {
				orgList.add(Long.valueOf(outstock.getOrgId()));
			}
			if (outstock.getAuditStatus().equals((byte) 2)) {
				outstock.setAuditStatusName("已审核");
				outstock.setOutstockStatusName("已出库");
			} else {
				outstock.setAuditStatusName("未审核");
				outstock.setOutstockStatusName("未出库");
			}
			if (outstock.getNum() != null && outstock.getSoldNum() != null) {
				outstock.setInvalid(outstock.getNum().subtract(outstock.getSoldNum()));
			}
			outstock.getSpecNo();
			exports.add(new RecycleChenkExport(outstock));
		}
		Map<Long, Org> orgs = null;
		if (orgList.size() > 0) {
			orgs = orgService.findOrgMapByIds(orgList, false);
		}
		for (RecycleChenkExport outstock : exports) {

			if (null != orgs) {
				if (null != outstock.getOrgId()) {
					Org org = orgs.get(Long.valueOf(outstock.getOrgId()));
					if (null != org) {
						outstock.setOrgName(org.getName());
					}
				}
			}
		}
		return exports;
	}

	/**
	 * 出库导出
	 * 
	 * @param response
	 * @param pager
	 */
	@RequestMapping("/export")
	public void export(HttpServletResponse response, @RequestBody(required = false) LarPager<RecycleOutstock> pager) {
		List<RecycleOutstock> outstocks = null;
		try {
			pager.setPageSize(1000000);
			Map<String, Object> map = pager.getExtendMap();
			Object orgObj = map.get("org");
			Object includeSubObj = map.get("includeSub");
			List<Long> orgList = getOrgList(orgObj, includeSubObj);
			recycleOutstockService.findByOrgIds(pager, orgList);
			outstocks = pager.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!Collections.isEmpty(outstocks)) {
			ExportExcelUtils<RecycleOutstock> exportExcelUtils = new ExportExcelUtils<>("出库管理");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("出库管理", this.convert(outstocks));
				String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
				OutputStream outputStream = response.getOutputStream();
				workbook.write(outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (workbook != null) {
					try {
						workbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 导出时封装orgName
	private List<RecycleOutstock> convert(List<RecycleOutstock> outstocks) throws Exception {
		List<Long> orgList = new ArrayList<>();
		for (RecycleOutstock outstock : outstocks) {
			if (null != outstock.getOrgId()) {
				orgList.add(Long.valueOf(outstock.getOrgId()));
			}
			if (outstock.getAuditStatus().equals((byte) 2)) {
				outstock.setAuditStatusName("已审核");
				outstock.setOutstockStatusName("已出库");
			} else {
				outstock.setAuditStatusName("未审核");
				outstock.setOutstockStatusName("未出库");
			}
			outstock.getSpecNo();
		}
		Map<Long, Org> orgs = null;
		if (orgList.size() > 0) {
			orgs = orgService.findOrgMapByIds(orgList, false);
		}
		for (RecycleOutstock outstock : outstocks) {
			if (null != orgs) {
				if (null != outstock.getOrgId()) {
					Org org = orgs.get(Long.valueOf(outstock.getOrgId()));
					if (null != org) {
						outstock.setOrgName(org.getName());
					}
				}
			}
		}
		return outstocks;
	}

	/**
	 * 查询是否存在此出库单。
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/existByOutstockNo", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO existByOutstockNo(@RequestBody Map<String, Object> params) {
		try {
			if (params != null) {
				String outstockNo = null;
				Long outstockId = null;
				if (null != params.get("outstockNo")) {
					outstockNo = params.get("outstockNo").toString().trim();
				}
				if (null != params.get("outstockId")) {
					outstockId = Long.parseLong(params.get("outstockId").toString());
				}

				Boolean exist = recycleOutstockService.existByOutstockNo(outstockId, outstockNo);
				return ResultDTO.getSuccess(200, "审核成功！", exist);
			}
			logger.warn("请求参数有误:method {}", Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}", Thread.currentThread().getStackTrace()[1].getMethodName(), e,
					e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}

}
