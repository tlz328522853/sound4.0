package com.sdcloud.web.lar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * 常量
 * Created by 韩亚辉 on 2016/4/1.
 */
public class Constant {
    public static Long SHOP_TYPE_PID=8930310136510366l;
    public static Long COMPANY=3754912513964447l;
    public static Long BUY_TYPE=1l;
    
    public static final String TOKEN_USERID = "token_userId";
    
    /**
     * 验证是否是手机号码
     * @author jzc 2016年6月22日
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){  
    	  
//	    Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
//	    Matcher m = p.matcher(mobiles);
    	try {
    		if(StringUtils.isNotEmpty(mobiles)&&mobiles.length()==11){
    			Long phone=Long.parseLong(mobiles);
    			if(phone>=20000000000l){
    				return false;
    			}
    		}
		} catch (Exception e) {
			return false;
		}
	    return true;  
    }  
    
    public static void main(String[] args) {
    	System.out.println(isMobileNO("15016155153"));
	}
}
