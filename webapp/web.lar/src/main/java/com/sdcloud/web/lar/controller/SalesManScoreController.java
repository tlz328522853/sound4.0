package com.sdcloud.web.lar.controller;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.Score;
import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.SalesManScoreService;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.LarPagerUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by 韩亚辉 on 2016/4/11.
 */
@RestController
@RequestMapping("api/score")
public class SalesManScoreController {
	@Autowired
	private SalesManScoreService scoreService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private DicService dicService;
	@Autowired
	private EmployeeService employeeService;

	@RequestMapping("/findAll")
	public ResultDTO findAll(@RequestBody(required = false) LarPager<Score> larPager) {
		try {
			return ResultDTO.getSuccess(200, scoreService.findAll(larPager));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/save")
	public ResultDTO save(@RequestBody(required = false) Score score,HttpServletRequest request) {
		try {
			score.setRechargeDate(new Date());
			
			String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
			
			User user = userService.findByUesrId(Long.parseLong(userId));
			
			if(user != null &&null !=user.getEmployee()){
				score.setRechargeUser(user.getEmployee().getEmployeeId());
				return ResultDTO.getSuccess(200, scoreService.saveScore(score));
			}
			return ResultDTO.getFailure(500, "添加失败!该用户没有绑定员工!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/update")
	public ResultDTO update(@RequestBody(required = false) Score shipmentSendExpress) {
		try {
			
			return ResultDTO.getSuccess(200, scoreService.updateScore(shipmentSendExpress));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@RequestMapping("/delete/{id}")
	public ResultDTO delete(@PathVariable("id") Long id) {
		try {
			return ResultDTO.getSuccess(200, scoreService.deleteScore(id));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	//转换
	private List<Score> convert(List<Score> list) throws Exception {
		List<Long> empList = new ArrayList<>();
		Set<Long> empSet = new HashSet<>();
		List<Long> orgList = new ArrayList<>();
		List<Dic> dics = dicService.findByPid(Constant.COMPANY, null);
		Map<Long, Dic> dicMap = new HashMap<>();
		for (Dic item : dics) {
			dicMap.put(item.getDicId(), item);
		}
		for (Score score : list) {
			if (null != score.getOrg()) {
				orgList.add(Long.parseLong(score.getOrg()));
			}
		}
		Map<Long, User> users = new HashMap<>();
		Map<Long, Org> orgs = new HashMap<>();
		if (empSet.size() > 0) {
			empList.addAll(empSet);
			users = userService.findUserMapByIds(empList);
		}
		if (orgList.size() > 0) {
			orgs = orgService.findOrgMapByIds(orgList, false);
		}
		for (Score item : list) {
			if (null != users && users.size() > 0) {
				if (null != item.getRechargeUser()) {
					User user = users.get(item.getRechargeUser());
					if (null != user) {
						item.setRechargeUserName(user.getName());
					}
				}
			}
			if (null != orgs && orgs.size() > 0) {
				if (null != item.getOrg()) {
					Org org = orgs.get(Long.parseLong(item.getOrg()));
					if (null != org) {
						item.setOrgName(org.getName());
					}
				}
			}
			//封公司积分出账量
			if(null==item.getRechargeScore() )
				item.setRechargeScore(0);
			if(null==item.getGiveScore())
				item.setGiveScore(0);
			item.setSumScore(item.getRechargeScore() + item.getGiveScore());
		
		}
		return list;
	}

	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<Score> pager) {
		List<Long> ids = new ArrayList<>();
		LarPager<Score> orderTimeLarPager =null;
		try {
			Map<String, Object> map = pager.getExtendMap();
			
			if (map != null && null != map.get("orgId")) {
				//Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("includeSub") + "");
				//if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						/*List<Org> list = orgService.findById(id, true);
						for (Org org : list) {
							ids.add(org.getOrgId());
						}*/
						String orgStr = map.get("orgId").toString();
						String[] orgArr = orgStr.split("AAA");
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
						
					} else {
						String id = map.get("orgId").toString();
						Map<String, Object> result = LarPagerUtils.paramsConvert(pager.getParams());
						result.put("mechanismId", id);
						pager.setParams(result);
						ids = null;
					}
				}
			//}
			//LarPager<Score> pager = new LarPager<>();
			pager.setPageSize(1000000);
			orderTimeLarPager= scoreService.findByOrgIds(pager, ids);
			
			// 封装RechargeUserName
			List<Score> scores = pager.getResult();
			this.potRechargeUserName(scores);
		}  catch (Exception e1) {
			e1.printStackTrace();
		}
		if (null != orderTimeLarPager && null != orderTimeLarPager.getResult()
				&& orderTimeLarPager.getResult().size() > 0) {
			ExportExcelUtils<Score> exportExcelUtils = new ExportExcelUtils<>("积分充值");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("积分充值", this.convert(orderTimeLarPager.getResult()));
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

	//用于封装RechargeUserName的方法
	private void potRechargeUserName(List<Score> scores) throws Exception {
		if (scores != null && scores.size() > 0) {
			List<Long> rechargeUsers = new ArrayList<>();
			for (Score score : scores) {
				rechargeUsers.add(score.getRechargeUser());
			}
			List<Employee> employees = employeeService.findById(rechargeUsers);
			for (Score score : scores) {
				for (Employee employee : employees) {
					if (score.getRechargeUser().equals(employee.getEmployeeId())) {
						score.setRechargeUserName(employee.getName());
						break;
					}
				}
			}
		}
	}

	/**
	 * 根据机构id获取本机构及子机构的数据
	 *
	 * @param larPager
	 * @return
	 */
	@RequestMapping("/findByOrgIds")
	public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Score> larPager) {
		try {
			Map<String, Object> map = larPager.getExtendMap();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("orgId")) {
				//Long id = Long.valueOf(map.get("orgId") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("includeSub") + "");
				//if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						/*List<Org> list = orgService.findById(id, true);
						for (Org org : list) {
							ids.add(org.getOrgId());
						}*/
						//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
						String orgStr = map.get("orgId").toString();
						String[] orgArr = orgStr.split("AAA");
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
						
					} else {
						String id = map.get("orgId").toString();
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("mechanismId", id);
						larPager.setParams(result);
						ids = null;
					}
				}
			//}
			LarPager<Score> pager = scoreService.findByOrgIds(larPager, ids);
			// 封装RechargeUserName
			List<Score> scores = pager.getResult();
			this.potRechargeUserName(scores);
			
			return ResultDTO.getSuccess(200, pager);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}

	@ExceptionHandler(value = { Exception.class })
	public void handlerException(Exception ex) {
		System.out.println(ex);
	}
	
	//查询充值操作人。
	@RequestMapping("/getEchargeUser")
	public ResultDTO getEchargeUser(@RequestBody(required = false) LarPager<Score> larPager) {
		try {
			Map<String, Object> map = larPager.getParams();
			List<Long> ids = new ArrayList<>();
			if (map != null && null != map.get("org")) {
				//Long id = Long.valueOf(map.get("org") + "");
				Boolean isParentNode = Boolean.valueOf(map.get("includeSub") + "");
				//if (null != id) {
					// 是父节点再去查找
					if (isParentNode) {
						/*List<Org> list = orgService.findById(id, true);
						for (Org org : list) {
							ids.add(org.getOrgId());
						}*/
						
						//mechanismId 如果有多个机构 ，使用"AAA"来隔开。
						String orgStr = map.get("org").toString();
						String[] orgArr = orgStr.split("AAA");
						for(String orgString:orgArr){
							Long mechanismId = Long.parseLong(orgString);
							List<Org> list = orgService.findById(mechanismId, true);
							for (Org org : list) {
								ids.add(org.getOrgId());
							}
						}
						
					} else {
						String id = map.get("org").toString();
						Map<String, Object> result = LarPagerUtils.paramsConvert(larPager.getParams());
						result.put("org", id);
						larPager.setParams(result);
						ids = null;
					}
				}
			//}
			
			List<Long> empIds = scoreService.getEchargeUser(larPager, ids);
			List<Employee> emps = new ArrayList<>();
			if(empIds !=null && empIds.size()>0)
				emps = employeeService.findById(empIds);
			return ResultDTO.getSuccess(200, emps);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(500, "服务器错误！");
		}
	}
}
