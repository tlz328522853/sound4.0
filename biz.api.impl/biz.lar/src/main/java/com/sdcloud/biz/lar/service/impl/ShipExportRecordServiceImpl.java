package com.sdcloud.biz.lar.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sdcloud.api.cache.redis.service.HashOperationsService;
import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Org;
import com.sdcloud.api.lar.entity.DistributeExpress;
import com.sdcloud.api.lar.entity.ShipExportNotice;
import com.sdcloud.api.lar.entity.ShipExportRecord;
import com.sdcloud.api.lar.entity.ShipmentExpress;
import com.sdcloud.api.lar.entity.TakingExpress;
import com.sdcloud.api.lar.service.IRowReader;
import com.sdcloud.api.lar.service.ShipExportRecordService;
import com.sdcloud.api.lar.util.CacheKeyUtil;
import com.sdcloud.api.lar.util.DataValidUtl;
import com.sdcloud.api.lar.util.ExportReadInfo;
import com.sdcloud.biz.lar.dao.DistributeExpressDao;
import com.sdcloud.biz.lar.dao.ShipExportNoticeDao;
import com.sdcloud.biz.lar.dao.ShipExportRecordDao;
import com.sdcloud.biz.lar.dao.TakingExpressDao;
import com.sdcloud.biz.lar.util.ErrorExcelUtils;
import com.sdcloud.biz.lar.util.ExcelReader;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * 
 * @author jiazc
 * @date 2016-10-24
 * @version 1.0
 */
@Service
public class ShipExportRecordServiceImpl extends BaseServiceImpl<ShipExportRecord> implements ShipExportRecordService,IRowReader{
   
	@Autowired
	private ShipExportRecordDao shipExportRecordDao;
	@Autowired
	private ShipExportNoticeDao shipExportNoticeDao;
	@Autowired
	private HashOperationsService hashOperationsService;
	@Autowired
	private DistributeExpressDao distributeExpressDao;
	@Autowired
	private TakingExpressDao takingExpressDao;
	
	public static String KEY_EXPORT="EXPORT:";//导入数据模块的部分key定义

    @Override
    public LarPager<ShipExportRecord> findAll(LarPager<ShipExportRecord> larPager) {
        List<ShipExportRecord> list = shipExportRecordDao.findAll(larPager.getParams(),larPager);
        larPager.setResult(list);
        larPager.setTotalCount(shipExportRecordDao.totalCount(larPager.getParams()));
        return larPager;
    }
    
    /**js轮询请求的对象方法
     * 根据key(head+export+userid)获取用户导入数据信息
     * @author jzc 2016年10月25日
     * @param userId
     * @return
     */
    public ExportReadInfo getReadInfo(Long userId) throws Exception{
    	String key=CacheKeyUtil.getO2oShipmentKey(KEY_EXPORT+userId);
    	ExportReadInfo readInfo=new ExportReadInfo();
    	try {
    		Map<String, String> cacheMap=hashOperationsService.entries(key);
    		if(CollectionUtils.isEmpty(cacheMap)){
    			return null;
    		}
        	readInfo.covertCacahMap(cacheMap);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
    	return readInfo;
    }
    
    /**
     * 缓存ExportReadInfo对象数据
     * @author jzc 2016年10月25日
     * @param readInfo
     * @param userId
     * @return
     */
    public boolean cacheReadInfo(ExportReadInfo readInfo,Long userId) throws Exception{
    	String key=CacheKeyUtil.getO2oShipmentKey(KEY_EXPORT+userId);
    	boolean flag=false;
    	try {
    		hashOperationsService.putAll(key, readInfo.getCacheMap());
    		flag=hashOperationsService.getOperations_expire(//存在1分钟
    				key,60000, TimeUnit.MILLISECONDS);
    		logger.info("success:缓存数据成功（数据总数："+readInfo.getUserTotalSize()+
    				",读取数："+readInfo.getUserReadSize()+
    				",成功数："+readInfo.getUserSuccessSize()+
    				",错误数："+readInfo.getUserErrorSize()+")");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
    	return flag;
    }
    
    /**
     * 验证 <快件揽收> 导入的数据，并返回验证通过的List<TakingExpress>
     * @author jzc 2016年10月26日
     * @param valExportBeans 需要验证的数据集合
     * @param errExportBeans 验证错误的数据集合
     * @param org 选择的机构数据
     * @param emps 机构底下的所有员工
     * @param expresses 机构下的所有快递公司
     * @return 验证通过的TakingExpress数据集合
     */
    public List<TakingExpress> validExpressData(List<TakingExpress.ExportBean> valExportBeans,
    		List<TakingExpress.ExportBean> errExportBeans,Org org,List<Employee> emps,
    		List<ShipmentExpress> expresses){
    	//验证通过把数据转化为TakingExpress对象加入集合
    	List<TakingExpress> expressList=new ArrayList<>(valExportBeans.size());
    	//批量验证派件运单号
    	this.batchValidExpressOrderNo(valExportBeans);
    	for(TakingExpress.ExportBean teb:valExportBeans){
    		if(validExpressData(teb,org,emps,expresses)&&teb.errMsgs.size()==0){//验证通过加入
    			expressList.add(teb.toTakingExpress());
    		}
    		else{//验证失败加入
    			errExportBeans.add(teb);
    		}
    		
    	}
    	logger.info("success:验证<快件揽收>数据成功（需要验证的数："+valExportBeans.size()+
				",成功数："+expressList.size()+
				",失败数："+(valExportBeans.size()-expressList.size())+
				")");
    	return expressList;
    }
    
    /**
     * 验证<派件接单> 导入的数据，并返回验证通过的List<DistributeExpress>
     * @author jzc 2016年10月26日
     * @param valExportBeans 需要验证的数据集合
     * @param errExportBeans 验证错误的数据集合
     * @param org 选择的机构数据
     * @param emps 机构底下的所有员工
     * @param expresses 机构下的所有快递公司
     * @return 验证通过的DistributeExpress数据集合
     */
    public List<DistributeExpress> validDistributeData(List<DistributeExpress.ExportBean> valExportBeans,
    		List<DistributeExpress.ExportBean> errExportBeans,Org org,List<Employee> emps,
    		List<ShipmentExpress> expresses){
    	//验证通过把数据转化为TakingExpress对象加入集合
    	List<DistributeExpress> distributeList=new ArrayList<>(valExportBeans.size());
    	//批量验证派件运单号
    	this.batchValidDistribOrderNo(valExportBeans);
    	for(DistributeExpress.ExportBean dee:valExportBeans){
    		if(validDistributeData(dee,org,emps,expresses)&&dee.errMsgs.size()==0){//验证通过加入
    			distributeList.add(dee.toDistributeExpress());
    		}
    		else{//验证失败加入
    			errExportBeans.add(dee);
    		}
    	}
    	logger.info("success:验证<派件接单>数据成功（需要验证的数："+valExportBeans.size()+
				",成功数："+distributeList.size()+
				",失败数："+(valExportBeans.size()-distributeList.size())+
				")");
    	return distributeList;
    }
    
    /**
     * 批量验证派件运单号
     * @author jzc 2016年10月26日
     * @param valExportBeans
     */
    private void batchValidExpressOrderNo(List<TakingExpress.ExportBean> valExportBeans) {
    	List<String> orderNos=new ArrayList<>(valExportBeans.size());
		for(TakingExpress.ExportBean dee:valExportBeans){
			if(validOrderNo(dee.orderNo, dee.errMsgs)){//基础验证通过，需要验证运单号是否唯一的对象
				orderNos.add(dee.orderNo);
			}
		}
		//此处调取批量验证，返回数据库已经存在的运单号
		orderNos=takingExpressDao.queryByOrderNos(orderNos);		
		//把验证非唯一的运单号对应的对象，添加错误提示信息
		Map<String, Integer> selfOrders=new HashMap<>(valExportBeans.size());
		for(TakingExpress.ExportBean dee:valExportBeans){
			if(!StringUtils.isEmpty(dee.orderNo)&&dee.orderNo.length()<=32){
				boolean flag=false;
				for(String orderNo:orderNos){//非唯一的运单号集合
					if(dee.orderNo.equals(orderNo)){
						flag=true;
						dee.errMsgs.add(TakingExpress.ExportBean.ORDER_NO_is_exist);
						break;
					}
				}
				if(!flag){
					if(selfOrders.get(dee.orderNo)!=null){
						dee.errMsgs.add(TakingExpress.ExportBean.ORDER_NO_is_exist);
					}
					else{
						selfOrders.put(dee.orderNo, 1);
					}
				}
			}
		}
		//验证 需要添加到数据库的运单号，是否自身重复
		
	}
    
    /**
     * 批量验证派件运单号
     * @author jzc 2016年10月26日
     * @param valExportBeans
     */
    private void batchValidDistribOrderNo(List<DistributeExpress.ExportBean> valExportBeans) {
    	List<String> orderNos=new ArrayList<>(valExportBeans.size());
		for(DistributeExpress.ExportBean dee:valExportBeans){
			if(validOrderNo(dee.orderNo, dee.errMsgs)){//基础验证通过，需要验证运单号是否唯一的对象
				orderNos.add(dee.orderNo);
			}
		}
		//此处调取批量验证，返回数据库已经存在的运单号
		orderNos=distributeExpressDao.queryByOrderNos(orderNos);
		//把验证非唯一的运单号对应的对象，添加错误提示信息
		Map<String, Integer> selfOrders=new HashMap<>(valExportBeans.size());
		for(DistributeExpress.ExportBean dee:valExportBeans){
			if(!StringUtils.isEmpty(dee.orderNo)&&dee.orderNo.length()<=32){
				boolean flag=false;
				for(String orderNo:orderNos){//非唯一的运单号集合
					if(dee.orderNo.equals(orderNo)){
						flag=true;
						dee.errMsgs.add(TakingExpress.ExportBean.ORDER_NO_is_exist);
						break;
					}
				}
				if(!flag){
					if(selfOrders.get(dee.orderNo)!=null){
						dee.errMsgs.add(TakingExpress.ExportBean.ORDER_NO_is_exist);
					}
					else{
						selfOrders.put(dee.orderNo, 1);
					}
				}
			}
		}
		
	}

	/**
     * 具体的验证<快件揽收>导入的每个字段
     * @author jzc 2016年10月26日
     * @param teb
     * @return
     */
    private boolean validExpressData(TakingExpress.ExportBean teb,Org org,List<Employee> emps,
    		List<ShipmentExpress> expresses){
    	boolean flag=true;
    	Long orgId=this.validOrgStr(teb.orgStr, org, teb.errMsgs);//验证机构名称
    	if(orgId!=null){
    		teb.org=orgId;
    	}else{
    		flag=false;
    	}
    	
    	Long expressId= this.validExpressStr(teb.expressStr, expresses, teb.errMsgs);//验证所属快递公司名称
    	if(expressId!=null){
    		teb.express=expressId;
    	}else{
    		flag=false;
    	}
    	
    	Long employeeId=this.validIdNo(teb.idNo, teb.takingManStr, emps, teb.errMsgs);//验证身份证号码
    	if(employeeId!=null){
    		teb.takingMan=employeeId;
    	}else{
    		flag=false;
    	}
    	
    	Double toPay=this.validToPayStr(teb.toPayStr, teb.errMsgs);//验证到付款
    	if(toPay!=null){
    		teb.toPay=toPay;
    		teb.toPayStr=toPay.toString();
    	}else{
    		flag=false;
    	}
    	
    	if(StringUtils.isEmpty(teb.takingManStr)){//验证揽件员名称
    		teb.errMsgs.add(TakingExpress.ExportBean.TAKING_MAN_not_null);
    		flag=false;
    	}
    	
    	if(StringUtils.isEmpty(teb.payWayStr)){//验证结算方式
    		teb.errMsgs.add(TakingExpress.ExportBean.PAY_WAY_not_null);	
    		flag=false;
    	}else{
    		if(!TakingExpress.ExportBean.PAY_WAY_ARR.contains(teb.payWayStr)){
    			teb.errMsgs.add(TakingExpress.ExportBean.PAY_WAY_not_exist);
    			flag=false;
    		}else{
    			teb.payWay=TakingExpress.ExportBean.PAY_WAY_ARR.indexOf(teb.payWayStr)+1;
    		}
    	}
    	
    	if(StringUtils.isEmpty(teb.moneyStr)){//验证运费金额
    		teb.errMsgs.add(TakingExpress.ExportBean.MONEY_not_null);	
    		flag=false;
    	}else{
    		if(!DataValidUtl.isDouble(teb.moneyStr)){
    			teb.errMsgs.add(TakingExpress.ExportBean.MONEY_invalid_num);
    			flag=false;
    		}else{
    			teb.money=DataValidUtl.formatDouble(teb.moneyStr);
    			teb.moneyStr=teb.money.toString();
    			if(teb.money<0){
    				teb.errMsgs.add(TakingExpress.ExportBean.MONEY_invalid_num);
        			flag=false;
    			}
    		}
    	}
    	
    	if(StringUtils.isEmpty(teb.takingDateStr)){//验证接收时间
    		teb.errMsgs.add(TakingExpress.ExportBean.TAKING_DATE_not_null);	
    		flag=false;
    	}else{
    		Date date=DataValidUtl.parstDateStr(teb.takingDateStr);
    		if(date==null){
    			teb.errMsgs.add(TakingExpress.ExportBean.TAKING_DATE_invalid);
    			flag=false;
    		}else{
    			teb.takingDate=date;
    		}
    	}
    	
    	return flag;
    }
    
    /**
     * 具体的验证<派件接单>导入的每个字段
     * @author jzc 2016年10月26日
     * @param dee
     * @return
     */
    private boolean validDistributeData(DistributeExpress.ExportBean dee,Org org,List<Employee> emps,
    		List<ShipmentExpress> expresses){
    	boolean flag=true;
    	Long orgId=this.validOrgStr(dee.orgStr, org, dee.errMsgs);//验证机构名称
    	if(orgId!=null){
    		dee.org=orgId;
    	}else{
    		flag=false;
    	}
    	
    	Long expressId= this.validExpressStr(dee.expressStr, expresses, dee.errMsgs);//验证所属快递公司名称
    	if(expressId!=null){
    		dee.express=expressId;
    	}else{
    		flag=false;
    	}
    	
    	Long employeeId=this.validIdNo(dee.idNo, dee.distributerStr, emps, dee.errMsgs);//验证身份证号码
    	if(employeeId!=null){
    		dee.distributer=employeeId;
    	}else{
    		flag=false;
    	}
    	
    	Double toPay=this.validToPayStr(dee.toPayStr, dee.errMsgs);//验证到付款
    	if(toPay!=null){
    		dee.toPay=toPay;
    		dee.toPayStr=toPay.toString();
    	}else{
    		flag=false;
    	}
    	
    	if(StringUtils.isEmpty(dee.distributerStr)){//验证派送员名称
    		dee.errMsgs.add(DistributeExpress.ExportBean.DISTRIBUTER_not_null);
    		flag=false;
    	}
    	
    	if(StringUtils.isEmpty(dee.signStatusStr)){//验证签收状态
    		dee.errMsgs.add(DistributeExpress.ExportBean.SIGN_STATUS_not_null);	
    		flag=false;
    	}else{
    		if(!DistributeExpress.ExportBean.SIGN_STATUS_ARR.contains(dee.signStatusStr)){
    			dee.errMsgs.add(DistributeExpress.ExportBean.SIGN_STATUS_not_exist);
    			flag=false;
    		}else{
    			dee.signStatus=1;//已签收
    		}
    	}
    	
    	if(StringUtils.isEmpty(dee.signTimeStr)){//验证接收时间
    		dee.errMsgs.add(DistributeExpress.ExportBean.SIGN_TIME_not_null);	
    		flag=false;
    	}else{
    		Date date=DataValidUtl.parstDateStr(dee.signTimeStr);
    		if(date==null){
    			dee.errMsgs.add(DistributeExpress.ExportBean.SIGN_TIME_invalid);
    			flag=false;
    		}else{
    			dee.signTime=date;
    		}
    	}
    	
    	return flag;
    }
    
    /**
     * 验证运单号
     * @author jzc 2016年10月26日
     * @param orderNo
     * @param errMsgs
     * @return
     */
    private boolean validOrderNo(String orderNo,List<String> errMsgs){
    	boolean flag=true;
    	if(StringUtils.isEmpty(orderNo)){//验证运单号
    		errMsgs.add(DistributeExpress.ExportBean.ORDER_NO_not_null);
    		flag=false;
    	}else{
    		if(!DataValidUtl.orderNoValid(orderNo)){
    			errMsgs.add(DistributeExpress.ExportBean.ORDER_NO_too_length);
    			flag=false;
    		}else{
    			//需要验证该运单号数据库是否存在
    			flag=true;
    		}
    	}
    	return flag;
    }
    
    /**
     * 验证机构的名称
     * @author jzc 2016年10月26日
     * @param orgStr
     * @param org
     * @param errMsgs
     * @return
     */
    private Long validOrgStr(String orgStr,Org org,List<String> errMsgs){
    	Long orgId=null;
    	if(StringUtils.isEmpty(orgStr)){//验证机构名称
    		errMsgs.add(DistributeExpress.ExportBean.ORG_not_null);	
    	}else{
    		//需要验证上传数据机构与选择的机构是否一致，需要设置机构ID（dee.org）
    		if(!orgStr.equals(org.getName())){
    			errMsgs.add(DistributeExpress.ExportBean.ORG_differ);
    		}else{
    			orgId=org.getOrgId();
    		}
    	}
    	return orgId;
    }
    
    /**
     * 验证expressStr，成功返回 expressId
     * @author jzc 2016年10月26日
     * @param expressStr
     * @param expresses
     * @param errMsgs
     * @return
     */
    private Long validExpressStr(String expressStr,List<ShipmentExpress> expresses
    		,List<String> errMsgs){
    	Long expressId=null;
    	if(StringUtils.isEmpty(expressStr)){//验证所属快递公司名称
    		errMsgs.add(DistributeExpress.ExportBean.EXPRESS_not_null);	
    	}else{
    		//需要验证系统该机构下是否存在该快递公司，需要设置快递公司ID（dee.express）
    		boolean expFlag=false;
    		for(ShipmentExpress express:expresses){
    			if(express.getExpressName().equals(expressStr)){
    				expFlag=true;
    				expressId=express.getExpress();
    				break;
    			}
    		}
    		if(!expFlag){
    			errMsgs.add(DistributeExpress.ExportBean.EXPRESS_not_exist);
    		}
    	}
    	return expressId;
    }
    
    /**
     * 验证身份证号idNo，成功返回 employeeId
     * @author jzc 2016年10月26日
     * @param idNo
     * @param takingManStr
     * @param emps
     * @param errMsgs
     * @return
     */
    private Long validIdNo(String idNo,String takingManStr,List<Employee> emps,List<String> errMsgs){
    	Long employeeId=null;
    	if(StringUtils.isEmpty(idNo)){//验证身份证号码
    		errMsgs.add(TakingExpress.ExportBean.ID_NO_not_null);
    	}else{
    		//揽件员与身份证号对应的员工名称是否一致,需要设置揽件员ID（teb.takingMan）
    		boolean empFlag=false;
    		for(Employee emp:emps){
    			if(emp.getIdentNo().equals(idNo)){
    				empFlag=true;
    				if(emp.getName().equals(takingManStr)){
    					employeeId=emp.getEmployeeId();
    				}
    				else{
    					errMsgs.add(TakingExpress.ExportBean.ID_NO_differ);
    				}
    				break;
    			}
    		}
    		if(!empFlag){
    			errMsgs.add(TakingExpress.ExportBean.ID_NO_not_exist);
    		}
    	}
    	return employeeId;
    }
    
    /**
     * 验证到付款toPayStr，并返回数值
     * @author jzc 2016年10月26日
     * @param toPayStr
     * @param errMsgs
     * @return
     */
    private Double validToPayStr(String toPayStr,List<String> errMsgs){
    	Double toPay=null;
    	if(StringUtils.isEmpty(toPayStr)){//验证到付款
    		errMsgs.add(TakingExpress.ExportBean.TOPAY_not_null);	
    	}else{
    		if(!DataValidUtl.isDouble(toPayStr)){
    			errMsgs.add(TakingExpress.ExportBean.TOPAY_invalid_num);
    		}else{
    			toPay=DataValidUtl.formatDouble(toPayStr);
    			if(toPay<0){//判断，如果到付款小于0，错误
    				errMsgs.add(TakingExpress.ExportBean.TOPAY_invalid_num);
    				toPay=null;
    			}
    		}
    	}
    	return toPay;
    }
    
    @Transactional(readOnly=false)
    @Override
	public Map<String, Object> readExcel(InputStream is, ExportReadInfo info, Org org, List<Employee> emps, List<ShipmentExpress> expresses, Integer type, Long userId) throws Exception {
    	
    	try {
			Map<String,ShipExportNotice> notices=new HashMap<>();//需要报表更新的数据的通知
			String uniqueNo=System.nanoTime()+"";//批次号
	    	Map<String, Object> map = new HashMap<>();
	    	
	    	int batchCount = 200;//每200条批量添加一次
			int index = 1 ;//初始坐标
			int row = 0;//有效行数
			int count = 0;//批量添加的次数row/batchCount
			int saveCount = 0;
			int remainder = 0;//是否有剩余的没有保存
			
	    	info.setBeginTime(System.currentTimeMillis());
	    	info.setCurrentTime(System.currentTimeMillis());
	    	//info.setFileName(fileName);
	    	this.cacheReadInfo(info, userId);
	    	logger.info("success:cache缓存<等待读取>状态...");
	    	//快件揽收业务处理
	    	if(type == 0){
	    		List<TakingExpress.ExportBean> result = new ArrayList<>();
	        	List<TakingExpress.ExportBean> error = new ArrayList<>();
	        	ExcelReader reader = new ExcelReader(this,type);
	          	reader.setTakingBean(result);
	          	reader.process(is);//解析excel文件数据
	          	
	          	row = result.size()-1;
	          	count = row/batchCount;
	          	info.setUserTotalSize(row);
	          	info.setReadStatus(ExportReadInfo.READ_STATUS_start);
	          	info.setUserReadSize(index-1);
				info.setUserSuccessSize(saveCount);
				info.setUserErrorSize(error.size());
				info.setCurrentTime(System.currentTimeMillis());
				this.cacheReadInfo(info, userId);
				logger.info("success:cache缓存<快件揽收- 开始读取>状态...");
				
	          	for (int i = 0; i < count; i++) {
	          		List<TakingExpress> data = this.validExpressData(result.subList(index, index+batchCount), error, org, emps, expresses);
	    			if(data.size()>0){
	    				saveCount += takingExpressDao.batchExportSave(setTakingValue(data,userId));
	    				this.mapAddNotices(notices,data,uniqueNo);
	    			}
	    			index += batchCount;
	    			info.setUserReadSize(index-1);
	    			info.setUserSuccessSize(saveCount);
	    			info.setUserErrorSize(error.size());
	    			info.setCurrentTime(System.currentTimeMillis());
	    			this.cacheReadInfo(info, userId);
	    		}
	          	
	    		remainder = row%batchCount;//是否有剩余的没有保存
	    		if(remainder > 0){
	    			List<TakingExpress> data = this.validExpressData(result.subList(index,result.size()), error, org, emps, expresses);
	    			if(data.size()>0){
	    				saveCount += takingExpressDao.batchExportSave(setTakingValue(data,userId));
	    				this.mapAddNotices(notices,data,uniqueNo);
	    			}
	    	    }
	    		
	    		info.setUserReadSize(row);
				info.setUserSuccessSize(saveCount);
				info.setUserErrorSize(error.size());
				info.setCurrentTime(System.currentTimeMillis());
				info.setReadStatus(ExportReadInfo.READ_STATUS_end);
				this.cacheReadInfo(info, userId);
				logger.info("success:cache缓存<快件揽收- 读取结束>状态...");
				//生成execle数据
				if(error.size() > 0){
					error.add(0, result.get(0));
					ErrorExcelUtils errorExcel = new ErrorExcelUtils("快件揽收");
	    			InputStream fis = errorExcel.writeExpressErrorData(error);
	    			logger.info("success:创建<快件揽收- 错误excle文件>成功！");
	    			map.put("is", fis);
				}
	    	}
	    	//派件接单业务处理
	    	if(type == 1){
	    		List<DistributeExpress.ExportBean> result = new ArrayList<>();
	        	List<DistributeExpress.ExportBean> error = new ArrayList<>();
	        	ExcelReader reader = new ExcelReader(this,type);
	          	reader.setDistributeBean(result);
	          	reader.process(is);
	          	
	          	row = result.size()-1;
	          	count = row/batchCount;
	          	info.setUserTotalSize(row);
	          	info.setReadStatus(ExportReadInfo.READ_STATUS_start);
	          	info.setUserReadSize(index-1);
				info.setUserSuccessSize(saveCount);
				info.setUserErrorSize(error.size());
				info.setCurrentTime(System.currentTimeMillis());
				this.cacheReadInfo(info, userId);
				logger.info("success:cache缓存<派件接单— 开始读取>状态...");
	          	
	          	for (int i = 0; i < count; i++) {
	          		List<DistributeExpress> data = this.validDistributeData(result.subList(index, index+batchCount), error, org, emps, expresses);
	          		if(data.size()>0){
	              		saveCount += distributeExpressDao.batchExportSave(setDistributeValue(data,userId));
	              		this.mapAddNoticesDisExp(notices,data,uniqueNo);
	          		}
	    			index += batchCount;
	    			info.setUserReadSize(index-1);
	    			info.setUserSuccessSize(saveCount);
	    			info.setUserErrorSize(error.size());
	    			info.setCurrentTime(System.currentTimeMillis());
	    			this.cacheReadInfo(info, userId);
	    		}
	          	
	    		remainder = row%batchCount;//是否有剩余的没有保存
	    		if(remainder > 0){
	    			List<DistributeExpress> data = this.validDistributeData(result.subList(index,result.size()), error, org, emps, expresses);
	    			if(data.size()>0){
	              		saveCount += distributeExpressDao.batchExportSave(setDistributeValue(data,userId));
	              		this.mapAddNoticesDisExp(notices,data,uniqueNo);
	          		}
	    	    }
	    		
	    		info.setUserReadSize(row);
				info.setUserSuccessSize(saveCount);
				info.setUserErrorSize(error.size());
				info.setCurrentTime(System.currentTimeMillis());
				info.setReadStatus(ExportReadInfo.READ_STATUS_end);
				this.cacheReadInfo(info, userId);
				logger.info("success:cache缓存<派件接单— 读取结束>状态...");
				//生成execle数据
				if(error.size() > 0){
					error.add(0, result.get(0));
					ErrorExcelUtils errorExcel = new ErrorExcelUtils("派件接单");
	    			InputStream fis = errorExcel.writeDistributeErrorData(error);
	    			logger.info("success:创建<派件接单— 错误excle文件>成功！");
	    			map.put("is", fis);
			    }
	    	}
	    	if(notices.size()>0){
	    		shipExportNoticeDao.batchSave(new ArrayList<ShipExportNotice>(notices.values()));
	    		logger.info("success:保存报表更新 <通知数据"+notices.size()+"> 成功！");
	    	}
	    	//保存记录,信息从缓存对象中获取
	    	ShipExportRecord t = new ShipExportRecord();
	    	t.setId(UUIDUtil.getUUNum());
	    	t.setOrg(org.getOrgId());
	    	t.setCreateUser(userId);
	    	t.setCreateDate(new Date());
	    	t.setType(type.byteValue());
	    	t.setFileName(info.getFileName());
	    	t.setAllNum(info.getUserTotalSize());
	    	t.setSuccessNum(info.getUserSuccessSize());
	    	t.setFaildNum(info.getUserErrorSize());
	    	t.setEnable((byte)0);
	    	map.put("record", t);
	    	
	    	return map;
    	} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
    
    private List<TakingExpress> setTakingValue(List<TakingExpress> data, Long userId) {
		Date date = new Date();
		for (TakingExpress taking : data) {
			taking.setCreateUser(userId);
			taking.setCreateDate(date);
		}
		return data;
	}

	private List<DistributeExpress> setDistributeValue(List<DistributeExpress> data, Long userId) {
    	Date date = new Date();
		for (DistributeExpress distribute : data) {
			distribute.setCreateUser(userId);
			distribute.setCreateDate(date);
		}
		return data;
	}

	/**
     * 添加<派件接单>导入的 通知到map集合
     * @author jzc 2016年11月24日
     * @param notices
     * @param data
     * @param uniqueNo
     */
    private void mapAddNoticesDisExp(Map<String, ShipExportNotice> notices, List<DistributeExpress> data, String uniqueNo) {
		for(DistributeExpress te:data){
			String dateStr=DataValidUtl.parstDateToStr(te.getSignTime());
			ShipExportNotice exportNotice=new ShipExportNotice();
			exportNotice.setId(UUIDUtil.getUUNum());
			exportNotice.setOrg(te.getOrg());
			exportNotice.setDataDate(dateStr);
			exportNotice.setUniqueNo(uniqueNo);
			exportNotice.setType((byte)0);
			notices.put(te.getOrg()+":"+dateStr, exportNotice);
		}
	}

	/**
     * 添加<快件揽收>导入的 通知到map集合
     * @author jzc 2016年11月24日
     * @param notices
     * @param data
     * @param uniqueNo 
     */
	private void mapAddNotices(Map<String, ShipExportNotice> notices, List<TakingExpress> data, String uniqueNo) {
		for(TakingExpress te:data){
			String dateStr=DataValidUtl.parstDateToStr(te.getTakingDate());
			ShipExportNotice exportNotice=new ShipExportNotice();
			exportNotice.setId(UUIDUtil.getUUNum());
			exportNotice.setOrg(te.getOrg());
			exportNotice.setDataDate(dateStr);
			exportNotice.setUniqueNo(uniqueNo);
			exportNotice.setType((byte)0);
			notices.put(te.getOrg()+":"+dateStr, exportNotice);
		}
		
	}

	@Override
	public void distributeResult(int curRow, Map<String, String> rowData, List<DistributeExpress.ExportBean> distributeBean) {
		
		int row = curRow + 1;
		DistributeExpress.ExportBean bean = new DistributeExpress.ExportBean();
		bean.no = rowData.get("A"+row);
		bean.orderNo = rowData.get("B"+row);
		bean.orgStr = rowData.get("C"+row);
		bean.expressStr = rowData.get("D"+row);
		bean.distributerStr = rowData.get("E"+row);
		bean.idNo = rowData.get("F"+row);
		bean.signStatusStr = rowData.get("G"+row);
		bean.toPayStr = rowData.get("H"+row);
		bean.signTimeStr = rowData.get("I"+row);
		distributeBean.add(bean);
	}

	@Override
	public void takingResult(int curRow, Map<String, String> rowData, List<TakingExpress.ExportBean> takingBean) {
		
		int row = curRow + 1;
		TakingExpress.ExportBean bean = new TakingExpress.ExportBean();
		bean.no = rowData.get("A"+row);
		bean.orderNo = rowData.get("B"+row);
		bean.orgStr = rowData.get("C"+row);
		bean.expressStr = rowData.get("D"+row);
		bean.takingManStr = rowData.get("E"+row);
		bean.idNo = rowData.get("F"+row);
		bean.payWayStr = rowData.get("G"+row);
		bean.moneyStr = rowData.get("H"+row);
		bean.toPayStr = rowData.get("I"+row);
		bean.takingDateStr = rowData.get("J"+row);
		takingBean.add(bean);
		
	}
    
}
