package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipExportRecord;
import com.sdcloud.api.lar.entity.ShipmentExpress;
import com.sdcloud.api.lar.service.ShipExportRecordService;
import com.sdcloud.api.lar.service.ShipmentExpressService;
import com.sdcloud.api.lar.util.ExportReadInfo;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.Pager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.FileOperationsUtils;

/**
 * 
 * @author jiazc
 * @date 2016-10-24
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/api/shipExportRecord")
public class ShipExportRecordController {

	private static final Logger logger = LoggerFactory.getLogger(ShipExportRecordController.class);

	@Autowired
	private ShipExportRecordService shipExportRecordService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ShipmentExpressService expressService;
	@Autowired
	private UserService userService;
	
	/**
	 * 查询所有数据
	 * @param pager 查询条件
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<ShipExportRecord> pager) throws Exception{
		try {
			Map<String, Object> params = pager.getParams();
			
			Object obj = params.get("includeSub");
			boolean includeSub = false;
			if(null != obj){
				includeSub = (Boolean) params.get("includeSub");
			}
			Long orgId = (Long)params.get("org");
			
			if(includeSub){
				List<Org> orgs = orgService.findById(orgId, includeSub);
				List<Long> orgIds = new ArrayList<>();
				for (int i = 0; i < orgs.size(); i++) {
					Org org = orgs.get(i);
					orgIds.add(org.getOrgId());
				}
				params.put("org", orgIds);
			}
			pager = shipExportRecordService.findAll(pager);
			pager.getParams().put("org", orgId);
			//类型装换
			converParam(pager.getResult());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(500, "查询错误!");
		}
		return ResultDTO.getSuccess(pager);
	}
	
	private void converParam(List<ShipExportRecord> result) throws Exception{

		if(result.size() > 0){
			Set<Long> ids = new HashSet<>();
			for (ShipExportRecord record : result) {
				ids.add(record.getCreateUser());
			}
			Map<Long, User> userMap = userService.findUserMapByIds(new ArrayList<Long>(ids));
			for (ShipExportRecord record : result) {
				Long userId = record.getCreateUser();
				if(userId != null && userMap.get(userId) != null){
					record.setCreateUserName(userMap.get(userId).getName());
				}
			}
		}
	}
	
	/**
	 * 查询导入数据实时信息
	 */
	@RequestMapping(value="/exportInfo")
	@ResponseBody
	public ResultDTO exportInfo(HttpServletRequest request){
		ExportReadInfo readInfo =null;
		try {
			Object userId=request.getAttribute("token_userId");
			readInfo = shipExportRecordService.getReadInfo(Long.parseLong(userId.toString()));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return ResultDTO.getFailure(500, "获取数据异常!");
		}
		return ResultDTO.getSuccess(readInfo);
	}
	
	/**
	 * 
	 * @param files 导入的文件
	 * @param orgId 结构Id
	 * @param type 0:快件揽收,1:派件接单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/upload")
	@ResponseBody
	public ResultDTO upload(@RequestParam("file") MultipartFile[] files, Long orgId, Integer type, HttpServletRequest request) throws Exception{
		
		//当前登录人Id
		String userId=(String)request.getAttribute("token_userId");
		//参数
		Pager<Employee> pager = new Pager<>(1000);
		Map<String, Object> params = new HashMap<>();
		params.put("orgId", orgId);
		params.put("includeSub", true);
		Org org = null;//当前机构
		List<Employee> emps = null;//机构下所有员工
		List<ShipmentExpress> expresses = null;//快递公司
		
		//用于redis缓存信息
		ExportReadInfo info = new ExportReadInfo();
		
		try {
			
			Pager<Employee> emp = employeeService.findByOrg(pager, params);
			emps = emp.getResult();
			
			List<Org> orgList = orgService.findById(orgId, false);//查询当前机构
			org = orgList.get(0);
			logger.info("success:获取到选择的机构数据："+org.getName());
			//查询当前的机构下的快递公司
			List<Long> orgIds = new ArrayList<>();
			orgIds.add(org.getOrgId());
			LarPager<ShipmentExpress> larPager = expressService.findByOrgIds(new LarPager<ShipmentExpress>(100), orgIds);
			expresses = larPager.getResult();
			logger.info("success:获取到选择的机构下的快递公司数量："+expresses.size());
		} catch (Exception e1) {
			info.setReadStatus(ExportReadInfo.SYSTEM_STATUS_error);
			shipExportRecordService.cacheReadInfo(info, Long.valueOf(userId));
			logger.error("faild:获取机构或者机构下的快递公司异常！",e1);
		}
		
		try {
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				info.setFileName(fileName);
				Map<String, Object> map = shipExportRecordService.readExcel(file.getInputStream(),info, org, emps, expresses, type, Long.valueOf(userId));
				
				ShipExportRecord record = (ShipExportRecord)map.get("record");
				//上传文件
				Object object = map.get("is");
				if(object != null){
					InputStream is = (InputStream)map.get("is");
					String fileUrl = FileOperationsUtils.fileIsUpload(is, fileName);
					logger.info("success:错误文件excel下载url："+fileUrl);
					//设置url
					record.setFaildFileUrl(fileUrl);
				}
				shipExportRecordService.save(record);
				logger.info("success:该次导入记录保存至数据库：id:"+record.getId());
			}
			
			return ResultDTO.getSuccess("导入数据成功");
			
		} catch (NumberFormatException e) {
			info.setReadStatus(ExportReadInfo.SYSTEM_STATUS_error);
			shipExportRecordService.cacheReadInfo(info, Long.valueOf(userId));
			logger.error("faild:userId数字转换异常！",e);
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "获取不到用户Id!");
		} catch (IOException e) {
			info.setReadStatus(ExportReadInfo.UPLOAD_STATUS_faild);
			shipExportRecordService.cacheReadInfo(info, Long.valueOf(userId));
			logger.error("faild:excle文件上传异常！",e);
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "文件上传失败!");
		} catch (Exception e) {
			info.setReadStatus(ExportReadInfo.READ_STATUS_error);
			shipExportRecordService.cacheReadInfo(info, Long.valueOf(userId));
			logger.error("faild:excel文件读取异常！",e);
			return ResultDTO.getFailure(AppCode.BIZ_ERROR, "系统错误!");
		}
		
	}
	
}
