package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.core.entity.User;
import com.sdcloud.api.core.service.DicService;
import com.sdcloud.api.core.service.OrgService;
import com.sdcloud.api.core.service.UserService;
import com.sdcloud.api.lar.entity.ShipIncome;
import com.sdcloud.api.lar.service.ShipIncomeService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.Constant;
import com.sdcloud.web.lar.util.ExportExcelUtils;

@RestController
@RequestMapping("/api/shipIncome")
public class ShipIncomeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ShipIncomeService ShipIncomeService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private UserService userService;
	@Autowired
	private DicService dicService;
	
	//添加数据
	@RequestMapping(value="/save")
	@ResponseBody
	public ResultDTO save(@RequestBody(required=false) ShipIncome shipIncome,HttpServletRequest request){
		try {
			logger.info("Enter the :{} method  shipIncome:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),shipIncome);
			
			String userId = (String)request.getAttribute(Constant.TOKEN_USERID);
			if(userId != null){
				User user = userService.findByUesr(Long.valueOf(userId));
				shipIncome.setCreateUser(Long.valueOf(user.getUserId()));
				shipIncome.setCreateUserName(user.getName());
			}
			//设置登记时间
			shipIncome.setRecordDate(new Date());
			boolean b = ShipIncomeService.save(shipIncome);
			return ResultDTO.getSuccess(AppCode.SUCCESS, "保存成功", b);
		} catch (Exception e) {
			logger.error("method {} execute error, shipIncome:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), shipIncome, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO update(@RequestBody(required=false) ShipIncome shipIncome){
		try {
			logger.info("Enter the :{} method  shipIncome:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),shipIncome);
			
			boolean b = ShipIncomeService.update(shipIncome);
			return ResultDTO.getSuccess(AppCode.SUCCESS,"修改成功",b);
			
		} catch (Exception e) {
			logger.error("method {} execute error, shipIncome:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), shipIncome, e);
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统异常！");
		}
	}
	
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public ResultDTO delete(@PathVariable(value="id") Long id){
		try {
			logger.info("Enter the :{} method  id:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),id);
			boolean b = ShipIncomeService.delete(id);
			if(id != null){
				if(b){
					return ResultDTO.getSuccess(AppCode.SUCCESS,"删除成功",b);
				}else{
					return ResultDTO.getFailure(AppCode.BIZ_ERROR, "删除失败!");
				}
			}else{
				return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			logger.error("method {} execute error, id:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), id, e);
			throw e;
		}
	}
	
	@RequestMapping("/findByOrgIds")
	@ResponseBody
    public ResultDTO findByOrgIds(@RequestBody(required = false) LarPager<ShipIncome> larPager) {
        try {
        	logger.info("Enter the :{} method  larPager:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),larPager);
        	
            Map<String, Object> map = larPager.getExtendMap();
            List<Long> ids = new ArrayList<>();
            if (!map.isEmpty()) {
                Long id = Long.valueOf(map.get("orgId") + "");
                boolean includeSub = Boolean.valueOf(map.get("includeSub") + "");
                if (null != id) {
                    List<Org> list = orgService.findById(id, includeSub);
                    for (Org org : list) {
                        ids.add(org.getOrgId());
                    }
                }
            }
            LarPager<ShipIncome> pager = ShipIncomeService.findByOrgIds(larPager, ids);
            this.convert(pager.getResult());
            return ResultDTO.getSuccess(AppCode.SUCCESS, pager);
        } catch (Exception e) {
        	logger.error("method {} execute error, larPager:{} Exception:{}", Thread
					.currentThread().getStackTrace()[1].getMethodName(), larPager, e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误！");
        }
    }
	
	@RequestMapping(value="/batchDelete",method=RequestMethod.POST)
	@ResponseBody
	public ResultDTO batchDelete(@RequestParam("ids") String ids){
		try {
			logger.info("Enter the :{} method  ids:{}", Thread.currentThread()
					.getStackTrace()[1].getMethodName(),ids);
			if(ids != null){
				boolean b = ShipIncomeService.batchDelete(ids);
				if(b){
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
	
	//导出
	@RequestMapping("/export")
    public void export(@RequestBody(required = false) LarPager<ShipIncome> larPager,HttpServletResponse response) throws Exception {
		
		 Map<String, Object> map = larPager.getExtendMap();
         List<Long> ids = new ArrayList<>();
         if (!map.isEmpty()) {
             Long id = Long.valueOf(map.get("orgId") + "");
             boolean includeSub = Boolean.valueOf(map.get("includeSub") + "");
             if (null != id) {
                 List<Org> list = orgService.findById(id, includeSub);
                 for (Org org : list) {
                     ids.add(org.getOrgId());
                 }
             }
         }
        larPager.setPageSize(10000);
        LarPager<ShipIncome> pager = ShipIncomeService.findByOrgIds(larPager, ids);
		
        if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
            ExportExcelUtils<ShipIncome> exportExcelUtils = new ExportExcelUtils<>("派件收入登记");
            Workbook workbook = null;
            try {
            	List<ShipIncome> list = this.convert(pager.getResult());
            	 workbook = exportExcelUtils.writeContents("派件收入登记", list);
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
	private List<ShipIncome> convert(List<ShipIncome> list) throws Exception {
		List<Dic> dics = dicService.findByPid(3754912513964447L, null);
		Map<Long, Dic> dicMap = new HashMap<>();
		for(Dic dic : dics){
			dicMap.put(dic.getDicId(), dic);
		}
		
		Set<Long> ids = new HashSet<>();
		for (ShipIncome de : list) {
			if(null != de.getOrgId()){
				ids.add(de.getOrgId());
			}
		}
		List<Long> orgIds = new ArrayList<>();
		orgIds.addAll(ids);
		Map<Long, Org> map = orgService.findOrgMapByIds(orgIds, false);
		for (ShipIncome de : list) {
			if(null != de.getOrgId()){
				Org org = map.get(de.getOrgId());
				de.setOrgName(org.getName());
			}
			if(de.getExpressId() != null){
				Dic dic = dicMap.get(de.getExpressId());
				if(dic != null){
					de.setExpressName(dic.getName());
				}
			}
		}
        return list;
    }
}
