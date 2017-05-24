package com.sdcloud.web.lar.util.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by 韩亚辉 on 2016/4/15.
 */
public class CommonObjectMapper extends ObjectMapper {
    public CommonObjectMapper() {
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//未知属性不处理

    }

}