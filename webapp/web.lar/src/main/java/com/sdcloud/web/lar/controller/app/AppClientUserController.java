package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarRegion;
import com.sdcloud.api.lar.entity.Version;
import com.sdcloud.api.lar.entity.Voucher;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.LarClientUserService;
import com.sdcloud.api.lar.service.VersionService;
import com.sdcloud.api.lar.service.VoucherService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import com.sdcloud.web.lar.util.SendPhoneMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/app/larClientUser")
public class AppClientUserController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LarClientUserService larClientUserService;
	@Autowired
	private CityService cityService;
	@Autowired
    private VersionService versionService;
	@Autowired
    private VoucherService voucherService;
	
    // 增加
    @RequestMapping(value = "/saveClientUser", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO save(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
        LarPager<LarClientUser> result = null;
        try {
            if (larClientUser != null && (larClientUser.getId() == null || larClientUser.getId().length() <= 0)) {
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
    
    /**
     * 导入客户数据,发送短信
     * @param larClientUser
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO importData(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
    	logger.info("method:{},phone:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),larClientUser.getPhone());
        try {
            if (larClientUser != null && (larClientUser.getId() == null || larClientUser.getId().length() <= 0)) {
            	if(null == larClientUser.getPassWord()){
            		larClientUser.setPassWord("123456");
            	}
                boolean insertUserGetId = larClientUserService.insertSelective(larClientUser);
                if (insertUserGetId) {
                	String content = "尊敬的"+larClientUser.getName()+"先生/女士，"
                			+ "您的好嘞社区账户："+larClientUser.getPhone()+" 密码：123456，可以自行更改密码，"
        					+ "可关注好嘞微信公众号:soundhaolei【卖废品，寄快递，用好嘞】";
                	logger.info("method:{},content:{}", Thread.currentThread() .getStackTrace()[1].getMethodName(),content);
                	String message = SendPhoneMessage.sendPhoneMessage(larClientUser.getPhone(), content);
                    return ResultDTO.getSuccess(AppCode.SUCCESS,"导入客户数据成功",true);
                } else {
                    return ResultDTO.getFailure(AppCode.BIZ_DATA, "导入客户数据失败！");
                }
            } else {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
        	logger.error("method:{},Exception:{}",Thread.currentThread() .getStackTrace()[1].getMethodName(),e);
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误!");
        }
    }

    // 删除
    // 需要同时更改用户对应的地址表
    @RequestMapping(value = "/deleteClientUser/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO delete(@PathVariable(value = "id") String id) throws Exception {
        LarPager<LarClientUser> result = null;
        try {
            if (id != null && id.trim().length() > 0) {
                boolean deleteById = larClientUserService.deleteById(id.trim());
                if (deleteById) {
                    result = larClientUserService.selectByExample(new LarPager<LarClientUser>());
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
    public ResultDTO update(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
        LarPager<LarClientUser> result = null;
        try {
            if (larClientUser != null && larClientUser.getId() != null && larClientUser.getId().trim().length() > 0) {
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
    // 修改
    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    public ResultDTO updateInfo(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
        try {
            if (larClientUser != null && larClientUser.getId() != null && larClientUser.getId().trim().length() > 0) {
            	if(larClientUser.getCityName()!=null){
					List<City> cityList=cityService.find(larClientUser.getCityName());
					if(cityList!=null&&cityList.size()>0){
						LarRegion region=new LarRegion();
						region.setRegionId(( Integer.valueOf(cityList.get(0).getRegionId().toString())));
						larClientUser.setCityId(Integer.valueOf(cityList.get(0).getRegionId().toString()));
					}
				}
            	boolean flag = larClientUserService.updateInfo(larClientUser);
                if (flag) {
                    return ResultDTO.getSuccess(null);
                } else {
                    return ResultDTO.getFailure(AppCode.BIZ_DATA, "用户信息修改失败！");
                }
            } else {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResultDTO.getFailure(AppCode.BIZ_DATA, "用户信息修改失败！");
        }
    }
    // 查看
    @RequestMapping(value = "/findInfo", method = RequestMethod.POST)
    public ResultDTO findInfo(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
        try {
            if (larClientUser != null && larClientUser.getId() != null && larClientUser.getId().trim().length() > 0) {
            	if(larClientUser.getCityName()!=null){
					List<City> cityList=cityService.find(larClientUser.getCityName());
					if(cityList!=null&&cityList.size()>0){
						LarRegion region=new LarRegion();
						region.setRegionId(( Integer.valueOf(cityList.get(0).getRegionId().toString())));
						larClientUser.setCityId(Integer.valueOf(cityList.get(0).getRegionId().toString()));
					}
				}
            	boolean flag = larClientUserService.updateInfo(larClientUser);
                if (flag) {
                    return ResultDTO.getSuccess(null);
                } else {
                    return ResultDTO.getFailure(AppCode.BIZ_DATA, "用户信息修改失败！");
                }
            } else {
                return ResultDTO.getFailure(AppCode.BAD_REQUEST, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResultDTO.getFailure(AppCode.BIZ_DATA, "用户信息修改失败！");
        }
    }

    @ExceptionHandler(value = {Exception.class})
    public void handlerException(Exception ex) {
        System.out.println(ex);
    }

    // 查询列表
    @RequestMapping("/getClientUsers")
    @ResponseBody
    public ResultDTO getClientUsers(@RequestBody(required = false) LarPager<LarClientUser> larPager) throws Exception {
        try {
            larPager = larClientUserService.selectByExample(larPager);
        } catch (Exception e) {
            throw e;
        }
        return ResultDTO.getSuccess(larPager);
    }
    /**
     * 根据ID查询用户信息
     * @param larClientUser
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getClientUser", method = RequestMethod.POST)
    public ResultDTO getClientUserById(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
        try {
            if (larClientUser.getId() != null && larClientUser.getId().trim().length() > 0) {
                 larClientUser = larClientUserService.selectByPrimaryKey(larClientUser.getId());
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

    // 上传头像
    @RequestMapping(value = "/fileUploader")
    @ResponseBody
    public ResultDTO UserIconUpload(@RequestParam("file") MultipartFile[] multipartFiles, HttpServletRequest request)
            throws Exception {
        String fileName = null;
        String webUrl = null;
        // 遍历所有上传上来的文件
        for (MultipartFile multipartFile : multipartFiles) {
            /** 构建图片保存的目录 **/
            String logoPathDir = "/images";
            /** 得到图片保存目录的真实路径 **/
            String logoRealPathDir = request.getSession().getServletContext().getRealPath(logoPathDir);
            /** 根据真实路径创建目录 **/
            File logoSaveFile = new File(logoRealPathDir);
            if (!logoSaveFile.exists())
                logoSaveFile.mkdirs();
            /** 获取文件的后缀 **/
            String suffix = multipartFile.getOriginalFilename()
                    .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            // /**使用UUID生成文件名称**/
            String logImageName = UUID.randomUUID().toString() + suffix;// 构建文件名称
            // String logImageName = multipartFile.getOriginalFilename();
            /** 拼成完整的文件保存路径加文件 **/
            fileName = logoRealPathDir + File.separator + logImageName;
            webUrl = request.getContextPath() + logoPathDir + File.separator + logImageName;
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
    }


	@RequestMapping(value = "/updateMsgFlag", method = RequestMethod.POST)
	public ResultDTO updateMsgFlag(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
		Integer count = 0;
		count = larClientUserService.updateMsgFlag(larClientUser.getId(), larClientUser.getMsgFlag());
		if (count > 0) {
			return ResultDTO.getSuccess(AppCode.SUCCESS, "更新消息推送状态成功", null);
		} else {
			return ResultDTO.getFailure(AppCode.BIZ_DATA, "更新消息推送状态失败");
		}
	}

	@RequestMapping(value = "/findMsgFlag", method = RequestMethod.POST)
	public ResultDTO findMsgFlag(@RequestBody(required = false) LarClientUser larClientUser) throws Exception {
		Integer msgFlag = larClientUserService.findMsgFlag(larClientUser.getId());
		return ResultDTO.getSuccess(AppCode.SUCCESS, "查询消息状态成功", msgFlag);
	}

    //用户中心
    @RequestMapping(value = "/userCenter")
    @ResponseBody
    public ResultDTO userCenter(@RequestBody(required = false) Map<String, Object> map) {
        try {
            String id = map.get("userId") + "";
            int source = Integer.parseInt(map.get("source") + "");
            if (id != null && id.trim().length() > 0) {
                LarClientUser larClientUser = larClientUserService.selectByPrimaryKey(id);
                List<Voucher> voucherList = voucherService.findByCus(Long.parseLong(id));
                Version version = versionService.getNewVersion(source);
                Map<String, Object> result = new HashMap<>();
                if (larClientUser != null) {
                    result.put("img", larClientUser.getIconUrl());
                    result.put("name", larClientUser.getName());
                    result.put("phone", larClientUser.getPhone());
                    result.put("point", larClientUser.getNowPoints());
                    result.put("push", larClientUser.getIconUrl());
                }
                if (voucherList != null) {
                    result.put("voucher", voucherList.size());
                } else {
                    result.put("voucher", 0);
                }
                //TODO 需要调用message接口
                result.put("message", "");
                if (version != null) {
                    result.put("version", version.getVersion());
                } else {
                    result.put("version", "");
                }
                return ResultDTO.getSuccess(AppCode.SUCCESS, "返回成功", result);
            } else {
                return ResultDTO.getFailure(AppCode.BIZ_ERROR, "参数错误");
            }
        } catch (Exception e) {
            return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "服务器错误");
        }
    }
}