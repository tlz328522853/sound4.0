package com.sdcloud.web.lar.util;

import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.ResultDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by 韩亚辉 on 2016/3/30.
 */
public class OrderUtils {
    //设定几位数
    private static final int LENGTH = 8;

    /**
     * 　　* 这是典型的随机洗牌算法。
     * 　　* 流程是从备选数组中选择一个放入目标数组中，将选取的数组从备选数组移除（放至最后，并缩小选择区域）
     * 　　* 算法时间复杂度O（n）
     * 　　* @return 随机8为不重复数组
     */
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

    /**
     * 交换方法
     *
     * @param i    交换位置
     * @param j    互换的位置
     * @param nums 数组
     */
    private static void swap(int i, int j, int[] nums) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    /**
     * 生成20位随机数
     * @return
     */
    public static String generateNumber20() {
       String str = generateNumber();
        Random random = new Random();
        str = str+random.nextInt(10);
        str = str+random.nextInt(10);
        str = str+random.nextInt(10);
        str = str+random.nextInt(10);
        return str;
    }

    //把前台参数转成后台参数
    public static Map<String, Object> appParamConvert(Map<String, Object> map, Long userId, List<Long> list) {
        String ids = map.get("ids") + "";
        String orderStateNo = map.get("orderStateNo") + "";
        if ("1".equals(orderStateNo)) {
            //转单
            map.put("turn_order_time", new Date());
            map.put("turn_order_user", userId);
        } else if ("2".equals(orderStateNo)) {
            //确认完成
            map.put("finish_time", new Date());
            map.put("finish_user", userId);
        } else if("8".equals(orderStateNo)){
            //抢单 //  设置 接单时间，接单人 订单状态  派单时间，派单人，地区
            /*map.put("take_time", new Date());
            map.put("take_user", userId);*/
            /*map.put("distribute_time", new Date());
            map.put("distribute_user", userId);*/
            map.put("order_state", "服务中");
        }
        if (StringUtils.isNotBlank(ids)) {
            if (ids.indexOf(",") > 0) {
                String[] idArr = ids.split(",");
                for (String item : idArr) {
                    list.add(Long.parseLong(item));
                }
            } else {
                list.add(Long.parseLong(ids));
            }
            map.remove("ids");
            map.remove("orderStateNo");
            return LarPagerUtils.paramsConvert(map);
        } else {
            return map;
        }

    }

    //把前台参数转成后台参数
    public static Map<String, Object> paramConvert(Map<String, Object> map, List<Long> list,Object id) {
        Long userId=UUIDUtil.getUUNum();
        if(id!=null&&id!=""){
            try{
                userId=Long.parseLong(id+"");
            }catch (Exception e){
            }
        }
        String ids = map.get("ids") + "";
        String orderStateNo = map.get("orderStateNo") + "";
        if ("0".equals(orderStateNo)) {
            //取消订单
            map.put("cancel_time", new Date());
            map.put("cancel_user", userId);
        } else if ("1".equals(orderStateNo)) {
            //接单
            map.put("take_time", new Date());
            map.put("take_user", userId);
        } else if ("2".equals(orderStateNo)) {
            //取消接单
            map.put("cancel_take_time", new Date());
            map.put("cancel_take_user", userId);
        } else if ("3".equals(orderStateNo)) {
            //派单
            map.put("distribute_time", new Date());
            map.put("distribute_user", userId);
        } else if ("4".equals(orderStateNo)) {
            //取消派单
            map.put("cancel_distribute_time", new Date());
            map.put("cancel_distribute_user", userId);
        } else if ("5".equals(orderStateNo)) {
            //转单
            map.put("turn_order_time", new Date());
            map.put("turn_order_user", userId);
        } else if ("6".equals(orderStateNo)) {
            //确认完成
            map.put("finish_time", new Date());
            map.put("finish_user", userId);
        } else if ("7".equals(orderStateNo)) {
            //对账
            map.put("account_time", new Date());
            map.put("account_user", userId);
        }
        if (StringUtils.isNotBlank(ids)) {
            if (ids.indexOf(",") > 0) {
                String[] idArr = ids.split(",");
                for (String item : idArr) {
                    list.add(Long.parseLong(item));
                }
            } else {
                list.add(Long.parseLong(ids));
            }
            map.remove("ids");
            map.remove("orderStateNo");
            return LarPagerUtils.paramsConvert(map);
        } else {
            return null;
        }
    }
}
