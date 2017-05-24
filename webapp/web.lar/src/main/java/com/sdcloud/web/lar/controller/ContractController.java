package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Dic;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.AdCustomer;
import com.sdcloud.api.lar.entity.Contract;
import com.sdcloud.api.lar.entity.ShipmentHelpMeBuy;
import com.sdcloud.api.lar.entity.ShipmentOrderTime;
import com.sdcloud.api.lar.entity.ShipmentSelectAged;
import com.sdcloud.api.lar.service.AdCustomerService;
import com.sdcloud.api.lar.service.ContractService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

	@Autowired
	private ContractService contractService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private DicService dicService;
	@Autowired
	private AdCustomerService adCustomerService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<Contract> pager){
		try {
			pager = contractService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) Contract contract,HttpServletRequest request) throws Exception{
		try {
			if(contract != null){
				String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
				if(userId != null){
					User user = userService.findByUesr(Long.valueOf(userId));
					contract.setCreateUser(Long.valueOf(user.getUserId()));
					contract.setCreateUserName(user.getName());
				}
				boolean b = contractService.save(contract);
				if(b){
					return ResultDTO.getSuccess("添加成功！");
				}else{
					return ResultDTO.getFailure(500, "添加失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) Contract contract){
		try {
			if(contract != null){
				boolean b = contractService.update(contract);
				if(b){
					return ResultDTO.getSuccess("修改成功！");
				}else{
					return ResultDTO.getFailure(500, "修改失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") Long id){
		try {
			boolean b = contractService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<Contract> larPager) {
        try {
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
            return ResultDTO.getSuccess(200, contractService.findByOrgIds(larPager, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.getFailure(500, "服务器错误！");
        }
    }
	
	@RequestMapping(value="/batchDelete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO batchDelete(@RequestParam("ids") String ids){
		try {
			if(ids != null){
				boolean b = contractService.batchDelete(ids);
				if(b){
					return ResultDTO.getSuccess("删除成功！");
				}else{
					return ResultDTO.getFailure(500, "删除失败");
				}
			}else{
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	@RequestMapping("/export")
    public void export(HttpServletResponse response) {
        LarPager<Contract> pager = new LarPager<>();
        pager.setPageSize(10000);
        LarPager<Contract> contractLarPager = contractService.findByOrgIds(pager, null);
        
        if (null != contractLarPager && null != contractLarPager.getResult() && contractLarPager.getResult().size() > 0) {
            ExportExcelUtils<Contract> exportExcelUtils = new ExportExcelUtils<>("合同");
            Workbook workbook = null;
            try {
            	List<Contract> list = this.convert(this.convert(contractLarPager.getResult()));
            	 workbook = exportExcelUtils.writeContents("合同", list);
                String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
                //String headStr = "attachment; filename=\"" + fileName + "\"";
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
	private List<Contract> convert(List<Contract> list) throws Exception {
		
		List<Long> orgList = new ArrayList<>();
        List<Long> customerList = new ArrayList<>();
        List<Long> userId = new ArrayList<>();
        for (Contract contract : list) {
            if (null != contract.getCreateUser()) {
            	userId.add(contract.getCreateUser());
            }
            if (null != contract.getOrg()) {
                orgList.add(contract.getOrg());
            }
            if(null != contract.getCustomAbbr()){
            	customerList.add(contract.getCustomAbbr());
            }
        }
        
        Map<Long, User> users = null;
        Map<Long, Org> orgs = null;
        Map<Long, AdCustomer> customers = null;
        Map<Long, Dic> dics = new HashMap<>();
        //合同类型
        List<Dic> dicList = dicService.findByPid(6577781076375885L, null);
        for (Dic dic : dicList) {
        	dics.put(dic.getDicId(), dic);
		}
        if (userId.size() > 0) {
            users = userService.findUserMapByIds(userId);
        }
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        if (customerList.size() > 0) {
        	customers = adCustomerService.findMapByIds(customerList);
        }
        
        for (Contract contract : list) {
            if (null != users && null != contract.getCreateUser()) {
                User user = users.get(contract.getCreateUser());
                if (null != user) {
                    contract.setCreateUserName(user.getName());
                }
            }
            if(null != orgs && null != contract.getOrg()){
            	Org org = orgs.get(contract.getOrg());
            	if(null != org){
            		contract.setOrgName(org.getName());
            	}
            }
            if(null != customers && null != contract.getCustomAbbr()){
            	AdCustomer adCustomer = customers.get(contract.getCustomAbbr());
            	if(null != adCustomer){
            		contract.setCustomAbbrName(adCustomer.getCustomAbbr());
            	}
            }
            if(null != dics && null != contract.getType()){
            	Dic dic = dics.get(contract.getType());
            	if(null != dic){
            		contract.setTypeName(dic.getName());
            	}
            }
            if(null != contract.getIsReceive() && contract.getIsReceive() == 1){
            	contract.setReceive("是");
            }
            
        }
        return list;
    }

}
