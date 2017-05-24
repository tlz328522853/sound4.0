package com.sdcloud.biz.lar.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sdcloud.api.lar.entity.XingeEntity;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;

/**
 * Android: type=1 >客户端  , type=2 > 业务员端
 * IOS  : type=3 >客户端 , type=4 > 业务员端
 *
 * @author wanghs
 *         2016年5月21日
 */
@Component
public class XingeAppUtils {

    private static Properties prop;
    private static int isOpen;
    static {
        try {
            prop = new Properties();
            InputStream is = XingeAppUtils.class.getResourceAsStream("/conf/xinge.properties");
            prop.load(is);
            isOpen = Integer.valueOf(prop.getProperty(Constant.ISOPEN));
        } catch (Exception e) {
            System.err.println("不能读取属性文件. " + "请确保properties在CLASSPATH指定的路径中");
        }
    }

    /**
     * 发送给Android单个账号
     *
     * @param xingeEntity 信鸽实体(accounts可为null)
     * @return
     * @throws InterruptedException 
     */
    @Async
    public void pushSingleAccount(XingeEntity xingeEntity,int type) {
    	JSONObject ret = null;
        if(isOpen == 1){
	        XingeApp xingeApp = getXingeApp(type);
	        //消息
	        Message message = getMessage(xingeEntity,type);
        	ret = xingeApp.pushSingleAccount(0, xingeEntity.getAccount(), message);
        }
    }

    /**
     * 发送给Ios单个账号
     *
     * @param xingeEntity 信鸽实体(accounts,titile可为null)
     * @return
     */
    @Async
    public void pushSingleAccountIOS(XingeEntity xingeEntity,int type) {
    	JSONObject ret = null;
        if(isOpen == 1){
	        XingeApp xingeApp = getXingeApp(type);
	        //消息
	        MessageIOS message = getMessageIOS(xingeEntity,type);
        	ret = xingeApp.pushSingleAccount(0, xingeEntity.getAccount(), message, Integer.valueOf(prop.getProperty(Constant.ENVIRONMENT)));
        }
    }

    /**
     * 发送给Android所有设备
     *
     * @param xingeEntity 信鸽实体(accounts,account,可为null)
     * @return
     */
    @Async
    public void pushAllDevice(XingeEntity xingeEntity,int type) {
    	JSONObject ret = null;
    	if(isOpen == 1){
	        XingeApp xingeApp = getXingeApp(type);
	        //消息
	        Message message = getMessage(xingeEntity,type);
	        //默认运行app首页
	        ClickAction action = new ClickAction();
	        message.setAction(action);
        	ret = xingeApp.pushAllDevice(0, message);
        }
    }

    /**
     * 发送给Ios所有设备
     *
     * @param xingeEntity 信鸽实体(accounts,titile,account,可为null)
     * @return
     */
    @Async
    public void pushAllDeviceIOS(XingeEntity xingeEntity,int type) {
    	JSONObject ret = null;
        if(isOpen == 1){
	        XingeApp xingeApp = getXingeApp(type);
	        //消息
	        MessageIOS message = getMessageIOS(xingeEntity,type);
        	ret = xingeApp.pushAllDevice(0, message, Integer.valueOf(prop.getProperty(Constant.ENVIRONMENT)));
        }
    }
    /**
     * 批量推送多个账号IOS
     * @param xingeEntity 信鸽实体(titile,account,可为null)
     * @return
     */
    @Async
    public void pushAccountListIOS(XingeEntity xingeEntity,int type){
    	JSONObject ret = null;
    	List<String> accounts = xingeEntity.getAccounts();
    	if(isOpen == 1){
			XingeApp xingeApp = getXingeApp(type);
	        //消息
	        MessageIOS message = getMessageIOS(xingeEntity,type);
	        ret = xingeApp.createMultipush(message, Integer.valueOf(prop.getProperty(Constant.ENVIRONMENT)));
	        int code = (Integer)ret.get("ret_code");
	        if(code == 0){
	        	String pushId = (String)ret.getJSONObject("result").get("push_id");
	        	
	        	int batchCount = 1000;//每1000条发送一次
	    		int index = 0 ;//初始坐标
	    		int count = accounts.size()/batchCount;//发送次数
	    		
	    		for (int i = 0; i < count; i++) {
	    			List<String> list = accounts.subList(index, index+batchCount);
	    			ret = xingeApp.pushAccountListMultiple(Long.valueOf(pushId), list);
	    			index += batchCount;
	    		}
	    		
	    		int remainder = accounts.size()%batchCount;//是否有剩余的没有发送
	    		if(remainder > 0){
	    			ret = xingeApp.pushAccountListMultiple(Long.valueOf(pushId), accounts.subList(index,accounts.size()));
	    		}
	        }
    	}
    }
    /**
     * 批量推送多个账号Android
     * @param xingeEntity 信鸽实体(account,可为null)
     * @return
     */
    @Async
    public void pushAccountList(XingeEntity xingeEntity,int type) {
    	JSONObject ret = null;
    	List<String> accounts = xingeEntity.getAccounts();
    	if(isOpen == 1){
    		 XingeApp xingeApp = getXingeApp(type);
    	        //消息
    	        Message message = getMessage(xingeEntity,type);
    	        ret = xingeApp.createMultipush(message);
    	        int code = (Integer)ret.get("ret_code");
    	        if(code == 0){
    	        	String pushId = (String)ret.getJSONObject("result").get("push_id");
    	        	
    	        	int batchCount = 1000;//每1000条发送一次
    	    		int index = 0 ;//初始坐标
    	    		int count = accounts.size()/batchCount;//发送次数
    	    		
    	    		for (int i = 0; i < count; i++) {
    	    			List<String> list = accounts.subList(index, index+batchCount);
    	    			ret = xingeApp.pushAccountListMultiple(Long.valueOf(pushId), list);
    	    			index += batchCount;
    	    		}
    	    		
    	    		int remainder = accounts.size()%batchCount;//是否有剩余的没有发送
    	    		if(remainder > 0){
    	    			ret = xingeApp.pushAccountListMultiple(Long.valueOf(pushId), accounts.subList(index,accounts.size()));
    	    		}
    	        }
    	}
    }
    
    public static XingeApp getXingeApp(int type) {
        Long accessId = null;
        String secretKey = null;
        XingeApp xingeApp = null;
        if (type == 1) {
            accessId = Long.valueOf(prop.getProperty(Constant.XGANDROIDCUSOMER_ID));
            secretKey = prop.getProperty(Constant.XGANDROIDCUSOMER_KEY);
            xingeApp = new XingeApp(accessId, secretKey);
        }
        if (type == 2) {
            accessId = Long.valueOf(prop.getProperty(Constant.XGANDROIDBIZ_ID));
            secretKey = prop.getProperty(Constant.XGANDROIDBIZ_KEY);
            xingeApp = new XingeApp(accessId, secretKey);
        }
        if (type == 3) {
            accessId = Long.valueOf(prop.getProperty(Constant.XGIOSCUSOMER_ID));
            secretKey = prop.getProperty(Constant.XGIOSCUSOMER_KEY);
            xingeApp = new XingeApp(accessId, secretKey);
        }
        if (type == 4) {
            accessId = Long.valueOf(prop.getProperty(Constant.XGIOSBIZ_ID));
            secretKey = prop.getProperty(Constant.XGIOSBIZ_KEY);
            xingeApp = new XingeApp(accessId, secretKey);
        }
        return xingeApp;
    }
    public static Message getMessage(XingeEntity xingeEntity,int type){
    	Message message = new Message();
    	message.setStyle(new Style(0, 1, 1, 1, 0));
        message.setType(Message.TYPE_NOTIFICATION);
        message.setTitle(xingeEntity.getTitle());
        message.setContent(xingeEntity.getContent());
        if (type == 1) {
            ClickAction action = new ClickAction();
            action.setActivity("com.soundgroup.okay.activity.MessageActivity");
            message.setAction(action);
        }
        if(type == 2){
        	int grabOrder = xingeEntity.getGrabOrder();
        	ClickAction action = new ClickAction();
        	//物流
        	if(grabOrder == 11){
        		action.setActivity("com.soundgroup.environmentalsanitation.activity.deliver.DeliverWaitListActivity");
        	}
        	//回收
			if(grabOrder == 21){
			    action.setActivity("com.soundgroup.environmentalsanitation.activity.recycle.RecycleWaitListActivity");    		
        	}
			//抢单
			if(grabOrder == 31){
        		action.setActivity("com.soundgroup.environmentalsanitation.activity.GrabActivity");
        	}
			message.setAction(action);
        }
        return message;
    }
    public static MessageIOS getMessageIOS(XingeEntity xingeEntity,int type){
    	//消息
        MessageIOS message = new MessageIOS();
        message.setAlert(xingeEntity.getContent());
        //角标
        message.setBadge(0);
        //声音
        message.setSound("beep.wav");
        // 自定义消息
        Map<String, Object> map = new HashMap<>();
        map.put("grabOrder", xingeEntity.getGrabOrder());
        message.setCustom(map);
        
        return message;
    }
    
    //测试方法
    public static void main(String[] args) {
    	String htt = "880032950759480";//韩业务员
    	String wmy = "7110815495373793";//王业务员
    	String cxl  = "758457812667040";//查小玲
    	int ywlx = 21;
    	
    	XingeEntity xingeEntity = new XingeEntity();
    	xingeEntity.setGrabOrder(ywlx);
    	xingeEntity.setTitle("好嘞社区");
    	xingeEntity.setAccount(wmy);
    	List<String> list = new ArrayList<>();
    	
    }
}
