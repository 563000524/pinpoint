package com.navercorp.pinpoint.web.alarm.impl.wechat.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.fastjson.JSON;
import com.navercorp.pinpoint.web.alarm.impl.wechat.bean.WorkWechatAppInfo;

/**
 * 初始化企业微信api调用所需的参数
 *
 */
@Configuration
public class WorkWechatConfig
{
    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    @Value("${alarm.workwechat.appList:[]}")
    private String appListJSON;
    
    @Bean
    public List<WorkWechatAppInfo> initWorkWechatAppInfo()
    {
        logger.info("初始化appListJSON{}",appListJSON);
        List<WorkWechatAppInfo> list = JSON.parseArray(appListJSON, WorkWechatAppInfo.class);
        return list;
    }
}
