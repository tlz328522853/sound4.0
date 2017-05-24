package com.sdcloud.web.lar.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页的工具类
 * Created by 韩亚辉 on 2016/3/22.
 */
public class LarPagerUtils {
    /**
     * 转换参数，把值为空的去掉
     * @param map
     * @return
     */
    public static Map<String,Object> paramsConvert(Map<String,Object> map){
        Map<String, Object> result = new HashMap<>();
        for (String item : map.keySet()) {
            if (null != map.get(item) && StringUtils.isNotBlank(map.get(item) + "")) {
                result.put(item, map.get(item));
            }
        }
        return result;
    }
}
