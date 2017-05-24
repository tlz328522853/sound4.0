package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.DistributeExpress;
import com.sdcloud.api.lar.entity.ShipExportNotice;
import com.sdcloud.api.lar.service.DistributeExpressService;
import com.sdcloud.api.lar.service.ShipExportNoticeService;
import com.sdcloud.api.lar.util.DataValidUtl;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/distribute")
public class DistributeExpressController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DistributeExpressService distributeExpressService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DicService dicService;
	@Autowired
	private ShipExportNoticeService shipExportNoticeService;
	
	//批量添加数据
	@RequestMapping(value="/batchSave1")
	@ResponseBody
	public ResultDTO batchSave1(@RequestBody(required=false) DistributeExpress distributes){
		try {
			logger.info("Enter the :{} method  distributes:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),distributes);
			Map<String, Object> map = new HashMap<>();
			if(distributes.getReceiver() != null && distributes.getReveiverName() == null){
				Employee emp = employeeService.findById(distributes.getReceiver());
				if(null != emp){
					distributes.setReveiverName(emp.getName());
				}
			}
			map= distributeExpressService.batchSave(distributes);
			
			boolean b = (boolean)map.get("flage");
			return b?ResultDTO.getSuccess(AppCode.SUCCESS,map.get("message")+"",map.get("data")):ResultDTO.getFailure((Integer)map.get("code"),map.get("message")+"");
		} catch (Exception e) {
			logger.error("method {} execute error, distributes:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), distributes, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) DistributeExpress distribute,HttpServletRequest request){
		try {
			logger.info("Enter the :{} method  distribute:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),distribute);
			String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
			if(userId != null && distribute.getUpdateReason() != null){
				User user = userService.findByUesr(Long.valueOf(userId));
				distribute.setUpdateUser(Long.valueOf(user.getUserId()));
				distribute.setUpdateUserName(user.getName());
			}
			if(distribute.getSignStatus() == 0){
				distribute.setSignStatus(1);
				distribute.setSignTime(new Date());
			}
			boolean b = distributeExpressService.update(distribute);
			int a = 0;
			if(b){
				List<DistributeExpress> data = new ArrayList<>();
				data.add(distribute);
				Map<String, ShipExportNotice> notices = mapAddNoticesDisExp(data);
				a = shipExportNoticeService.batchSave(new ArrayList<ShipExportNotice>(notices.values()));
			}
			return b?ResultDTO.getSuccess(AppCode.SUCCESS,"操作成功"):ResultDTO.getFailure(AppCode.BIZ_DATA,"签收失败!");
			
		} catch (Exception e) {
			logger.error("method {} execute error, distribute:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), distribute, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") Long id){
		try {
			logger.info("Enter the :{} method  id:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),id);
			DistributeExpress express = distributeExpressService.getById(id, null);
			boolean b = distributeExpressService.delete(id);
			if(id != null){
				if(b){
					List<DistributeExpress> data = new ArrayList<>();
					data.add(express);
					Map<String, ShipExportNotice> notices = mapAddNoticesDisExp(data);
					shipExportNoticeService.batchSave(new ArrayList<ShipExportNotice>(notices.values()));
					return ResultDTO.getSuccess(AppCode.SUCCESS,"删除成功！",b);
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} execute error, id:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), id, e);
			throw e;
		}
	}
	
	
	
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<DistributeExpress> larPager) {
        try {
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
                Long id = Long.valueOf(map.get("orgId") + "");
                Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
                if (null != id) {
                    //是父节点再去查找
                    if (isParentNode) {
                        List<Org> list = orgService.findById(id, true);
                        for (Org org : list) {
                            ids.add(org.getOrgId());
                        }
                    }else{
                        larPager.getParams().put("org",id);
                        ids=null;
                    }
                }
            }
            LarPager<DistributeExpress> pager = distributeExpressService.findByOrgIds(larPager, ids);
            
            return ResultDTO.getSuccess(200, pager);
        } catch (Exception e) {
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
	
	@RequestMapping(value="/batchDelete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO batchDelete(@RequestParam("ids") String ids){
		try {
			logger.info("Enter the :{} method  ids:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),ids);
			if(ids != null){
				boolean b = distributeExpressService.batchDelete(ids);
				if(b){
					List<DistributeExpress> data = distributeExpressService.getByIds(ids);
					Map<String, ShipExportNotice> notices = mapAddNoticesDisExp(data);
					shipExportNoticeService.batchSave(new ArrayList<ShipExportNotice>(notices.values()));
					return ResultDTO.getSuccess(AppCode.SUCCESS,"删除成功！",b);
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} execute error, ids:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), ids, e);
			throw e;
		}
	}
	
	/**
	 * app接口部分
	 * @param distributes
	 * @return
	 */
	
	//批量添加数据
	@RequestMapping(value="/appUpdate",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO appUpdate(@RequestBody(required=false) DistributeExpress distribute,HttpServletRequest request){
		try {
			logger.info("Enter the :{} method  distribute:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),distribute);
			String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
			if(userId != null && distribute.getUpdateReason() != null){
				User user = userService.findByUesr(Long.valueOf(userId));
				distribute.setUpdateUser(Long.valueOf(user.getUserId()));
				distribute.setUpdateUserName(user.getName());
			}
			if(distribute.getDistributer() != null && distribute.getDistributerName() == null){
				Employee emp = employeeService.findById(distribute.getDistributer());
				if(null != emp){
					distribute.setDistributerName(emp.getName());
				}
			}
			Map<String, Object> map = distributeExpressService.updateByOrderNo(distribute);
			boolean b = (boolean)map.get("flage");
			return b?ResultDTO.getSuccess(AppCode.SUCCESS,map.get("message")+"",true):ResultDTO.getFailure((Integer)map.get("code"),map.get("message")+"");
			
		} catch (Exception e) {
			logger.error("method {} execute error, distribute:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), distribute, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	//导出
	@RequestMapping("/export")
    public void export(@RequestBody(required = false) LarPager<DistributeExpress> larPager,HttpServletResponse response) throws Exception {
		
		Map<String, Object> map = larPager.getExtendMap();
        List<Long> ids = new ArrayList<>();
        if (map != null && null != map.get("orgId") && null != map.get("isParentNode")) {
            Long id = Long.valueOf(map.get("orgId") + "");
            Boolean isParentNode = Boolean.valueOf(map.get("isParentNode") + "");
            if (null != id) {
                //是父节点再去查找
                if (isParentNode) {
                    List<Org> list = orgService.findById(id, true);
                    for (Org org : list) {
                        ids.add(org.getOrgId());
                    }
                }else{
                    larPager.getParams().put("org",id);
                    ids=null;
                }
            }
        }
        larPager.setPageSize(10000);
        LarPager<DistributeExpress> pager = distributeExpressService.findByOrgIds(larPager, ids);
		
        if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
            ExportExcelUtils<DistributeExpress> exportExcelUtils = new ExportExcelUtils<>("派件接单");
            Workbook workbook = null;
            try {
            	List<DistributeExpress> list = this.convert(pager.getResult());
            	 workbook = exportExcelUtils.writeContents("派件接单", list);
                response.setContentType("APPLICATION/OCTET-STREAM");
                //response.setHeader("Content-Disposition", headStr);
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
	//转换
	private List<DistributeExpress> convert(List<DistributeExpress> list) throws Exception {
		List<Dic> dics = dicService.findByPid(3754912513964447L, null);
		Map<Long, Dic> map = new HashMap<>();
		for(Dic dic : dics){
			map.put(dic.getDicId(), dic);
		}
		for (DistributeExpress de : list) {
			de.setSignStatusName(de.getSignStatus()==1?"已签收":"未签收");
			if(de.getExpress() != null && de.getExpressName() == null){
				Dic dic = map.get(de.getExpress());
				de.setExpressName(dic.getName());
			}
		}
        return list;
    }
	/**
	 * 添加数据到通知
	 * @param data
	 * @return
	 */
	private Map<String, ShipExportNotice> mapAddNoticesDisExp(List<DistributeExpress> data) {
		Map<String, ShipExportNotice> notices = new HashMap<>();
		String curDate=DataValidUtl.parstDateToStr(new Date());
		for(DistributeExpress te: data){
			String dateStr=DataValidUtl.parstDateToStr(te.getSignTime());
			if(te.getSignTime() != null && !curDate.equals(dateStr)){
				ShipExportNotice exportNotice=new ShipExportNotice();
				exportNotice.setId(UUIDUtil.getUUNum());
				exportNotice.setOrg(te.getOrg());
				exportNotice.setDataDate(dateStr);
				exportNotice.setUniqueNo(System.nanoTime()+"");//批次号
				exportNotice.setType((byte)0);
				notices.put(te.getOrg()+":"+dateStr, exportNotice);
			}
		}
		return notices;
	}
}
