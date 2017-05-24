package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.lar.entity.AdCustomer;
import com.sdcloud.api.lar.service.AdCustomerService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/adCustomer")
public class AdCustomeController {

	@Autowired
	private AdCustomerService adCustomerService;
	@Autowired
	private OrgService orgService;
	
	@RequestMapping(value="/findAll",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO findAll(@RequestBody(required=false) LarPager<AdCustomer> pager){
		try {
			pager = adCustomerService.findAll(pager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(pager);
	}
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) AdCustomer adCustomer) throws Exception{
		try {
			if(adCustomer != null){
				boolean b = adCustomerService.save(adCustomer);
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
	public ResultDTO update(@RequestBody(required=false) AdCustomer adCustomer){
		try {
			if(adCustomer != null){
				boolean b = adCustomerService.update(adCustomer);
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
			boolean b = adCustomerService.delete(id);
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
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<AdCustomer> larPager) {
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
            return ResultDTO.getSuccess(200, adCustomerService.findByOrgIds(larPager, ids));
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
				boolean b = adCustomerService.batchDelete(ids);
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
        LarPager<AdCustomer> pager = new LarPager<>();
        pager.setPageSize(10000);
        LarPager<AdCustomer> contractLarPager = adCustomerService.findByOrgIds(pager, null);
        
        if (null != contractLarPager && null != contractLarPager.getResult() && contractLarPager.getResult().size() > 0) {
            ExportExcelUtils<AdCustomer> exportExcelUtils = new ExportExcelUtils<>("客户档案");
            Workbook workbook = null;
            try {
            	List<AdCustomer> list = this.convert(this.convert(contractLarPager.getResult()));
            	 workbook = exportExcelUtils.writeContents("客户档案", list);
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
	
	private List<AdCustomer> convert(List<AdCustomer> list) throws Exception {
		List<Long> orgList = new ArrayList<>();
        for (AdCustomer adCustomer : list) {
            if (null != adCustomer.getOrg()) {
                orgList.add(adCustomer.getOrg());
            }
        }
        
        Map<Long, Org> orgs = null;
        if (orgList.size() > 0) {
        	orgs = orgService.findOrgMapByIds(orgList, false);
        }
        
        for (AdCustomer adCustomer : list) {
            if(null != orgs && null != adCustomer.getOrg()){
            	Org org = orgs.get(adCustomer.getOrg());
            	if(null != org){
            		adCustomer.setOrgName(org.getName());
            	}
            }
            if(null != adCustomer.getType()){
            	String typeName = adCustomer.getType()==0 ? "客户" : "意向客户";
            	adCustomer.setTypeName(typeName);
            }
        	adCustomer.setCode(adCustomer.getId());
        }
        return list;
    }
}
