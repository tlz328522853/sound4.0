package com.sdcloud.biz.lar.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.RecycleBag;
import com.sdcloud.api.lar.service.RecycleBagService;
import com.sdcloud.biz.lar.dao.CityDao;
import com.sdcloud.biz.lar.dao.RecycleBagDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

@Service
public class RecycleBagServiceImpl implements RecycleBagService {

	@Autowired
	private RecycleBagDao recycleBagDao;
	
	@Autowired
	private CityDao cityDao;
	
	@Transactional(readOnly = true)
    public LarPager<RecycleBag> selectByExample(LarPager<RecycleBag> pager) throws Exception {
        if (pager == null) {
            pager = new LarPager<RecycleBag>();
        }
        try {
            if (StringUtils.isEmpty(pager.getOrderBy())) {
                // 多个用逗号
                pager.setOrderBy("createDate");
            }
            if (StringUtils.isEmpty(pager.getOrder())) {
                // 多个用逗号
                pager.setOrder("desc");
            }
            Map<String, Object> params = pager.getParams();
            if (params == null || params.size() <= 0) {
                throw new IllegalArgumentException("params is error");
            }
            if (pager.isAutoCount()) {
                long totalCount = 0;
                totalCount = recycleBagDao.countByIds(params);
                pager.setTotalCount(totalCount);
                if (totalCount <= 0) {
                    return pager;
                }
            }
            List<RecycleBag> result = recycleBagDao.selectByExample(pager);
            pager.setResult(result);
        } catch (Exception e) {
            throw e;
        }
        return pager;
    }

	@Override
	@Transactional
	public boolean completeOrders(Map<String, Object> updateParams) {
		try {
            if (updateParams != null && updateParams.size() > 0) {
                int count = recycleBagDao.completeOrders(updateParams);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return false;
	}

	@Override
	@Transactional
    public boolean insertSelective(RecycleBag recycleBag) throws Exception {
        if (recycleBag != null) {
            int count = 0;
            try {
            	//app端提交上来的订单是没有片区的
            	if(recycleBag.getCityId()!=null){
            		City city = cityDao.selectByCityId(Long.valueOf(recycleBag.getCityId()));
                	if(city==null || city.getId()==null){
                		return false;
                	}
                	recycleBag.setOrg(String.valueOf(city.getOrg()));
            	}else{
            		return false;
            	}
            	if(recycleBag.getLarClientUserAddressId()==null || recycleBag.getLarClientUserAddressId().getId()==null){
            		return false;
            	}
            	Date date = new Date();
                Calendar calendar = new GregorianCalendar(); 
                calendar.setTime(date); 
                //把日期往后增加一天.整数往后推,负数往前移动 
                calendar.add(calendar.DATE,1);
                
            	recycleBag.setId(String.valueOf(UUIDUtil.getUUNum()));
            	recycleBag.setCustomerId(generateNumber());
            	recycleBag.setScheduleDate(calendar.getTime());
            	recycleBag.setOrderTime(date);
                recycleBag.setOrderState("2");
                recycleBag.setOrderStateName("未完成");
                if(recycleBag.getOrderRemark()==null){
                	recycleBag.setOrderRemark("无");
                }
                recycleBag.setCreateDate(date);
                count = recycleBagDao.insertSelective(recycleBag);
                if(count>0){
                	return true;
                }else{
                	return false;
                }
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new IllegalArgumentException("OrderManager is null");
        }
    }
	
	//设定几位数
    private static final int LENGTH = 8;
	
	public static String generateNumber() {
        String no = "";
        // 初始化备选数组
        int[] defaultNums = new int[10];
        for (int i = 0; i < defaultNums.length; i++) {
            defaultNums[i] = i;
        }
        Random random = new Random();
        int[] nums = new int[LENGTH];
        // 默认数组中可以选择的部分长度
        int canBeUsed = 10;
        // 填充目标数组
        for (int i = 0; i < nums.length; i++) {
            // 将随机选取的数字存入目标数组
            int index = random.nextInt(canBeUsed);
            nums[i] = defaultNums[index];
            // 将已用过的数字扔到备选数组最后，并减小可选区域
            swap(index, canBeUsed - 1, defaultNums);
            canBeUsed--;
        }
        if (nums.length > 0) {
            for (int i = 0; i < nums.length; i++) {
                no += nums[i];
            }
        }
        Calendar cal = Calendar.getInstance();//使用日历类
        String year = String.valueOf(cal.get(Calendar.YEAR));//得到年
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);//得到月，因为从0开始的，所以要加1
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));//得到天
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        return String.valueOf(year + month + day + no);
    }

    private static void swap(int i, int j, int[] nums) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
