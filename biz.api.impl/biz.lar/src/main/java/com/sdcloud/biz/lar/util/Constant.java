package com.sdcloud.biz.lar.util;
/**
 * 
 * @author wanghs
 * 2016年5月21日
 */
public class Constant {

	public static final String XGANDROIDCUSOMER_ID ="xgAndroidCusomer.accessId";
    public static final String XGANDROIDCUSOMER_KEY ="xgAndroidCusomer.secretKey";
    public static final String XGANDROIDBIZ_ID ="xgAndroidBiz.accessId";
    public static final String XGANDROIDBIZ_KEY ="xgAndroidBiz.secretKey";
    
    public static final String XGIOSCUSOMER_ID ="xgIosCusomer.accessId";
    public static final String XGIOSCUSOMER_KEY ="xgIosCusomer.secretKey";
    public static final String XGIOSBIZ_ID ="xgIosBiz.accessId";
    public static final String XGIOSBIZ_KEY ="xgIosBiz.secretKey";
    
    public static final String ENVIRONMENT = "environment";
    public static final String ISOPEN = "isOpen";
    public static final long APP_TOKEN_EXPIRES_TIME =43200;//单位分钟：一个月
    
    
    /**********区域变更的常量定义************/
    /**
     * 物流更改
     */
    public static final Integer FROM_SHIPMENT_AREA = 1;
    /**
     * 回收更改
     */
    public static final Integer FROM_AREA_SETTING = 2;
    /**
     * 来自添加更改区域
     */
    public static final Integer FROM_METHOD_INSERT = 11;
    /**
     * 来自删除更改区域
     */
    public static final Integer FROM_METHOD_DELETE = 12;
    /**
     * 来自更新更改区域
     */
    public static final Integer FROM_METHOD_UPDATE = 13;
}
