package com.sdcloud.web.lar.util.oms;

import com.google.gson.Gson;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by luorongjie on 2017/4/18.
 *

 */
public class OmsParamsUtils {

    /**
     * * 用于参数的转换 将LarPager<>的参数类型转为
     v=1.0&sign=&message={
         "starttime": "2017-1-1 00:00:00",
         "endtime": "2017-5-1 00:00:00",
         "pagesize": 0,
         "pagenum": 0,
     }
     *这种类型
     *
     * @param larPager
     * @return
     */
    public static String getOmsParams(LarPager<Object> larPager){
        String params = "v=1.0&sign=&message=";
        String json = "{}";

        if(null !=larPager.getParams()){
            Gson gson = new Gson();
            larPager.getParams().put("pagesize",larPager.getPageSize());
            larPager.getParams().put("pagenum",larPager.getPageNo());
            json = gson.toJson(larPager.getParams());
        }
        return params+json;
    }


    /**
     * 用于请求对象的转换成请求字符串
     * @param larPager
     *
     * v=1.0&sign=234&message=
        [
            {
                "startnum":"1",
                "endnum":"1000"
            }
        ]
     * @return
     */
    public static String getMemberParams(LarPager<Object> larPager){
        String params = "v=1.0&sign=&message=";
        String json = "{}";
        return params+json;
    }

}
