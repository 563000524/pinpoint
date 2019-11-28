package com.navercorp.pinpoint.web.alarm.impl;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.bosssoft.itfinance.citizen.sms.dubboservice.ISmsDubboService;
import com.bosssoft.itfinance.citizen.sms.dubboservice.req.SmsReq;

/**
 * 短信预警
 *
 */
@Component
public class SmsAlarmMessageProxy implements ApplicationContextAware, AlarmMessageProxy
{
    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    private ISmsDubboService smsDubboService;
    @Value("${dubbo.registry.address:1.1.1.1}")
    private String           address;
    @Value("${dubbo.registry.protocol:zookeeper}")
    private String           protocol;
    @Value("${dubbo.sms.version:1.0.0}")
    private String           version;
    @Value("${dubbo.sms.group:}")
    private String           group;
    @Value("${alarm.sms.switch}")
    private boolean          SWITCH;
    
    @Override
    public void sendMessage(String number, String message)
    {
        if (SWITCH==false)
        {
            return;
        }
        SmsReq sms = new SmsReq();
        sms.setContent((String) message);
        sms.setMobile(number);
        sms.setMsgType("pinpoint");
        sms.setSender("pinpoint");
        sms.setSendTime(DateTime.now().toString("yyyyMMddHHmmss"));
        try
        {
            smsDubboService.sendSms(sms);
            logger.info("{},发送成功", sms);
        }
        catch (Exception e)
        {
            logger.error("{},发送异常", sms, e);
        }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        if (SWITCH==false)
        {
            return;
        }
        logger.info("初始化短信");
        ApplicationConfig application = new ApplicationConfig();
        application.setLogger("slf4j");
        application.setName("yyy");
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol(protocol);
        registry.setAddress(address);
        ReferenceConfig<ISmsDubboService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(ISmsDubboService.class);
        reference.setVersion("1.0.0");
        reference.setGroup("");
        reference.setCheck(false);
        reference.setTimeout(1000 * 30);
        reference.setRetries(0);
        smsDubboService = reference.get();
        logger.info("初始化短信service成功");
    }
    
}
