package com.sdcloud.web.lar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.web.lar.util.ExportExcelUtils;
import com.sdcloud.web.lar.util.FileOperationsUtils;

@RestController
@RequestMapping("/api/larClientUser")
public class LarClientUserController {

	@Autowired
	private LarClientUserService larClientUserService;

	Logger logger = LoggerFactory.getLogger(LarClientUserController.class);
	public static final String BASE_URL = "fileserver";
	public static final String FILE_PATH = "uploadpath";
	public static final String RESOURCE_USER = "webDavUser";
	public static final String RESOURCE_PASSWORD = "webDavPassword";

	// 增加
	@RequestMapping(value = "/saveClientUser", method = RequestMethod.POST)
	@ResponseBody
	public ResultDTO save(@RequestBody(required = false) LarClientUser larClientUser)
			throws Exception {
		LarPager<LarClientUser> result = null;
		try {
			if (larClientUser != null && (larClientUser.getId()==null || larClientUser.getId().length()<=0)) {
				if(larClientUser.getPhone() != null){
					int count = larClientUserService.countByPhone(larClientUser.getPhone());
					if(count > 0){
						return ResultDTO.getFailure(400,"手机号已注册,请重新注册!");
					}
				}
				boolean insertUserGetId = larClientUserService.insertSelective(larClientUser);
				if (insertUserGetId) {
					result = larClientUserService.selectByExample(new LarPager<LarClientUser>());
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "用户添加失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 删除
	// 需要同时更改用户对应的地址表
	@RequestMapping(value = "/deleteClientUser/{id}/{pageSize}/{pageNo}", method = RequestMethod.GET)
	@ResponseBody
	public ResultDTO delete(@PathVariable(value = "id") String id,
			@PathVariable(value = "pageSize") int pageSize,
			@PathVariable(value = "pageNo") int pageNo
			) throws Exception {
		LarPager<LarClientUser> result = null;
		try {
			if (id != null && id.trim().length() > 0) {
				boolean deleteById = larClientUserService.deleteById(id.trim());
				if (deleteById) {
					LarPager<LarClientUser> pager  = new LarPager<>();
					pager.setPageNo(pageNo);
					pager.setPageSize(pageSize);
					result = larClientUserService.selectByExample(pager);
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "用户删除失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// 修改
	@RequestMapping(value = "/updateClientUser", method = RequestMethod.POST)
	public ResultDTO update(
			@RequestBody(required = false) LarClientUser larClientUser)
			throws Exception {
		LarPager<LarClientUser> result = null;
		try {
			if (larClientUser != null && larClientUser.getId() != null
					&& larClientUser.getId().trim().length() > 0) {
				if(larClientUser.getPhone() != null){
					int count = larClientUserService.countByPhone(larClientUser.getPhone());
					if(count >= 1){
						return ResultDTO.getFailure(400,"手机号已注册,请重新注册!");
					}
				}
				boolean updateByExampleSelective = larClientUserService.updateByExampleSelective(larClientUser);
				if (updateByExampleSelective) {
					result = larClientUserService.selectByExample(new LarPager<LarClientUser>());
					return ResultDTO.getSuccess(result);
				} else {
					return ResultDTO.getFailure(500, "用户修改失败！");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	@ExceptionHandler(value={Exception.class})
	public void handlerException(Exception ex){
		System.out.println(ex);
	}

	// 查询列表
	@RequestMapping("/getClientUsers")
	@ResponseBody
	public ResultDTO getClientUsers(@RequestBody(required = false) LarPager<LarClientUser> larPager)
			throws Exception {
		try {
			larPager = larClientUserService.selectByExample(larPager);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}
	
	// 查询列表
	@RequestMapping("/getClientUsers1")
	@ResponseBody
	public ResultDTO getClientUsers1(@RequestBody(required = false) LarPager<LarClientUser> larPager) throws Exception {
		try {
			larPager = larClientUserService.getClientUsers(larPager);
		} catch (Exception e) {
			throw e;
		}
		return ResultDTO.getSuccess(larPager);
	}

	// 查询一个
	@RequestMapping(value = "/getClientUser/{id}")
	@ResponseBody
	public ResultDTO getClientUserById(@PathVariable(value = "id") String id)
			throws Exception {
		try {
			if (id != null && id.trim().length() > 0) {
				LarClientUser larClientUser = larClientUserService.selectByPrimaryKey(id);
				if (larClientUser != null) {
					return ResultDTO.getSuccess(larClientUser);
				} else {
					return ResultDTO.getFailure(500, "没有这个用户的数据");
				}
			} else {
				return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/*// 上传头像
	@RequestMapping(value = "/fileUploader")
	@ResponseBody
	public ResultDTO UserIconUpload(@RequestParam("file") MultipartFile[] multipartFiles,HttpServletRequest request)throws Exception {
		String fileName = null;
		String webUrl = null;
		// 遍历所有上传上来的文件
		for (MultipartFile multipartFile : multipartFiles) {
			// 构建图片保存的目录 
			String logoPathDir = "/images";
			//得到图片保存目录的真实路径
			String logoRealPathDir = request.getSession().getServletContext().getRealPath(logoPathDir);
			//根据真实路径创建目录
			File logoSaveFile = new File(logoRealPathDir);
			if (!logoSaveFile.exists())
				logoSaveFile.mkdirs();
			//获取文件的后缀
			String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
			//使用UUID生成文件名称
			String logImageName = UUID.randomUUID().toString()+ suffix;//构建文件名称
			//String logImageName = multipartFile.getOriginalFilename();
		   // 拼成完整的文件保存路径加文件
			fileName = logoRealPathDir + File.separator + logImageName;
			webUrl=request.getContextPath()+logoPathDir+File.separator+logImageName;
			File file = new File(fileName);
			try {
				multipartFile.transferTo(file);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResultDTO.getSuccess(webUrl);
	}*/
	
	// 上传头像
	@RequestMapping(value = "/fileUploader")
	@ResponseBody
	public ResultDTO UserIconUpload(@RequestParam("file") MultipartFile[] multipartFiles,HttpServletRequest request)throws Exception {
		String webUrl = null;
		for (MultipartFile file : multipartFiles) {
				try {
					webUrl=FileOperationsUtils.fileUpload(file);
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}
		return ResultDTO.getSuccess(webUrl);
	}
	
	//文件下载
	@RequestMapping(value = "/fileDown",method=RequestMethod.GET)
	@ResponseBody
	public void fileDown(HttpServletRequest request,HttpServletResponse response){
		try {
			FileOperationsUtils.fileDown(null,request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private String getDatePath(Date date){
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//		String strDate = simpleDateFormat.format(date);
//		return strDate;
//	}
	
	//导出  
	@RequestMapping("/export")
	public void export(HttpServletResponse response,@RequestBody(required = false) LarPager<LarClientUser> pager) {
		try {
			pager.setPageSize(1000000);
			pager = larClientUserService.getClientUsers(pager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    if (null != pager && null != pager.getResult() && pager.getResult().size() > 0) {
	        ExportExcelUtils<LarClientUser> exportExcelUtils = new ExportExcelUtils<>("客户积分台账");
	        Workbook workbook = null;
	        try {
	            workbook = exportExcelUtils.writeContents("客户积分台账", this.convert(pager.getResult()));
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
	 
	
	private List<LarClientUser> convert(List<LarClientUser> result) throws Exception {
		for(LarClientUser user : result){
			user.setConsumedPoints(user.getHistoryPoints()-user.getNowPoints());
		}
		return result;
	}
	
}