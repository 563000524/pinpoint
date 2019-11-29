package com.navercorp.pinpoint.web.alarm.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.navercorp.pinpoint.web.alarm.impl.wechat.WorkWechatService;

@Component
public class WechatAlarmMessageProxy implements ApplicationContextAware, AlarmMessageProxy
{
    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WorkWechatService workWechatService;
    @Value("${alarm.workwechat.switch}")
    private boolean           SWITCH;
    @Value("${alarm.title:[福州电子缴费平台]\n}")
    private String                  title;
    
    @Override
    public void sendMessage(String number, String message)
    {
        if (SWITCH==false)
        {
            return;
        }
        workWechatService.sendAppMessage(message);
        workWechatService.sendBOTMessage(message);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        if (SWITCH==false)
        {
            return;
        }
        logger.info("初始化微信service成功");
        workWechatService.sendAppMessage(title+"pinpoint启动成功");
        workWechatService.sendBOTMessage(title+"pinpoint启动成功");
    }
    
}
