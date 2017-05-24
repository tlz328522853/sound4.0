package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.service.RecycleInstockService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.ExportExcelUtils;

import io.jsonwebtoken.lang.Collections;

/**
 * lar_recycle_instock 入库管理
 * @author luorongjie
 * @date 2016-12-05
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/recycleInstock")
public class RecycleInstockController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RecycleInstockService recycleInstockService;
	@Autowired 
	private OrgService orgService;
	@Autowired 
	private UserService userService;
		
	/**
	 * 添加数据
	 * @param recycleInstock 添加数据
	 * @return
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO insert(@RequestBody RecycleInstock recycleInstock, HttpServletRequest request){
		try {
			if(recycleInstock != null){
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				
				recycleInstock.setRegisterId(user.getUserId());
				recycleInstock.setRegisterName(user.getName());
				recycleInstock.setRegisteDate(new Date());
				recycleInstock.setCreateUser(user.getUserId());
				recycleInstock.setCreateUserName(user.getName());
				recycleInstock.setCreateDate(new Date());
				recycleInstock.setUpdateUserName(user.getName());
				recycleInstock.setUpdateDate(new Date());
				
				recycleInstock.setInstockStatus((byte) 1);
				recycleInstock.setAuditStatus((byte) 1);
				
				recycleInstock.setEnable(0);
				recycleInstock.setVersion(0L);

				//3/29/2017 入库单编码自动生成，生成规则 20位 年月日+12位随机码
				recycleInstock.setInstockNo(OrderUtils.generateNumber20());
				
				recycleInstock.setInstockNo(recycleInstock.getInstockNo().trim());
				//入库单编码不能重复
				Boolean exist  = recycleInstockService.existByInstockNo(null,recycleInstock.getInstockNo());
				if(exist){
					return ResultDTO.getFailure(500, "添加失败,入库单编码重复!");
				}
				
				boolean b = recycleInstockService.save(recycleInstock);
				if(b){
					return ResultDTO.getSuccess(null,"添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}
	
	/**
	 * 更新数据
	 * @param recycleInstock 更新数据
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody  RecycleInstock recycleInstock, HttpServletRequest request){
		try {
			if(recycleInstock != null){
				
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate =new Date();
				recycleInstock.setUpdateDate(newDate);
				recycleInstock.setUpdateUser(user.getUserId());
				recycleInstock.setUpdateUserName(user.getName());
				//入库单编码不能重复
				Boolean exist  = recycleInstockService.existByInstockNo(recycleInstock.getId(),recycleInstock.getInstockNo());
				if(exist && recycleInstock.getEnable() !=1){
					return ResultDTO.getFailure(500, "添加失败,入库单编码重复!");
				}
				
				boolean b = recycleInstockService.update(recycleInstock);
				if(b){
					return ResultDTO.getSuccess(200,"操作成功！",null);
				}else{
					return ResultDTO.getFailure(500, "操作失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}
	
	/**
	 * 根据机构查询所有
	 * @param pager
	 * @return
	 */
	@RequestMapping(value="/findByOrgIds",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findByOrgIds(@RequestBody  LarPager<RecycleInstock> pager){
		try {
			if(pager != null){
				Map<String, Object> map = pager.getExtendMap();
	            Object orgObj = map.get("org");
	            Object includeSubObj = map.get("includeSub");
	            if(null ==orgObj )
	            	return ResultDTO.getFailure("参数有误！");
	            List<Long> orgList = getOrgList(orgObj, includeSubObj);
	            recycleInstockService.findByOrgIds(pager, orgList);          
	            return ResultDTO.getSuccess(pager);
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
		
	}

	/**
	 * 
	 * @param orgObj 机构 id 多个以“AAA”拼接
	 * @param includeSubObj 是否包含子机构 
	 * @return
	 * @throws Exception
	 */
	private List<Long> getOrgList(Object orgObj,Object includeSubObj) throws Exception {
		Boolean includeSub = false;
		if(null != includeSubObj){
			includeSub = Boolean.parseBoolean(includeSubObj.toString());
		}
		
		String[] orgArr = orgObj.toString().split("AAA");
		List<Long> orgIds = new ArrayList<>();
		for(String orgString:orgArr){
			Long orgId = Long.parseLong(orgString);
			List<Org> list = orgService.findById(orgId, includeSub);
			for (Org org : list) {
				orgIds.add(org.getOrgId());
			}
		}
		return orgIds ;
	}
	
	
	/**
	 * 审核入库
	 * @param recycleInstock 更新数据
	 * @return
	 */
	@RequestMapping(value="/audit",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO audit(@RequestBody  RecycleInstock recycleInstock, HttpServletRequest request){
		try {
			if(recycleInstock != null){
				
				Object userId = request.getAttribute("token_userId");
				// 查询用户
				User user = userService.findByUesrId(Long.valueOf(String.valueOf(userId)));
				Date newDate =new Date();
				recycleInstock.setAuditDate(newDate);
				recycleInstock.setAuditUser(user.getUserId());
				recycleInstock.setAuditUserName(user.getName());
				recycleInstock.setUpdateDate(newDate);
				recycleInstock.setUpdateUser(user.getUserId());
				recycleInstock.setUpdateUserName(user.getName());
				recycleInstock.setInstockDate(newDate);
				recycleInstock.setAuditStatus((byte)2);
				recycleInstock.setInstockStatus((byte)2);
				
				boolean b = recycleInstockService.audit(recycleInstock);
				if(b){
					return ResultDTO.getSuccess(200,"审核成功！",null);
				}else{
					return ResultDTO.getFailure(500, "审核失败");
				}
			}
			logger.warn("请求参数有误:method {}",
        			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
        			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}	
	}
	
	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<RecycleInstock> pager) {
		List<RecycleInstock> instocks = null;
		
		try {
			pager.setPageSize(1000000);
			Map<String, Object> map = pager.getExtendMap();
            Object orgObj = map.get("org");
            Object includeSubObj = map.get("includeSub");
            List<Long> orgList = getOrgList(orgObj, includeSubObj);
            recycleInstockService.findByOrgIds(pager, orgList);          
            instocks = pager.getResult();
            
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		if (!Collections.isEmpty(instocks)) {
			ExportExcelUtils<RecycleInstock> exportExcelUtils = new ExportExcelUtils<>("库存台账");
			Workbook workbook = null;
			try {
				workbook = exportExcelUtils.writeContents("入库管理", this.convert(instocks));
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

	//导出时封装orgName
	private List<RecycleInstock> convert(List<RecycleInstock> instocks)throws Exception {
		List<Long> orgList = new ArrayList<>();
		
        for (RecycleInstock instock : instocks) {
            if (null != instock.getOrgId()) {
                orgList.add(Long.valueOf(instock.getOrgId()));
            }
            if(instock.getAuditStatus().equals((byte)2)){
            	instock.setAuditStatusName("已审核");
            	instock.setInstockStatusName("已入库");
            }else{
            	instock.setAuditStatusName("未审核");
            	instock.setInstockStatusName("未入库");
            }
            instock.getSpecNo();
        }
        Map<Long, Org> orgs = null;
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        for (RecycleInstock instock : instocks) {
        	if(null !=orgs){
        		if(null != instock.getOrgId()){
        			Org org = orgs.get(Long.valueOf(instock.getOrgId()));
                	if(null != org){
                		instock.setOrgName(org.getName());
                	}
        		}
        	}
        }
        return instocks;
	}
	
	/**
	 * 查询是否存在此入库单。
	 * @param
	 * @return
	 */
	@RequestMapping(value="/existByInstockNo",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO existByInstockNo(@RequestBody Map<String,Object> params){
		try {
			if(params != null){
				String instockNo = null;
				Long instockId = null;
				if(null != params.get("instockNo")){
					instockNo = params.get("instockNo").toString().trim();
				}
				if(null != params.get("instockId")){
					instockId = Long.parseLong(params.get("instockId").toString());
				}
				
				Boolean exist  = recycleInstockService.existByInstockNo(instockId,instockNo);
				return  ResultDTO.getSuccess(200,"审核成功！",exist);
			}
			logger.warn("请求参数有误:method {}",
	    			Thread.currentThread().getStackTrace()[1].getMethodName());
			return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
		} catch (Exception e) {
			logger.error("系统处理异常:method {}, Exception:{}",
	    			Thread.currentThread().getStackTrace()[1].getMethodName(),e,e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
		}
	}
	
}
