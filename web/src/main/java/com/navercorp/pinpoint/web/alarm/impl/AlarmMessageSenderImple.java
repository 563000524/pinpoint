package com.navercorp.pinpoint.web.alarm.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.bosssoft.itfinance.citizen.sms.dubboservice.ISmsDubboService;
import com.bosssoft.itfinance.citizen.sms.dubboservice.req.SmsReq;
import com.navercorp.pinpoint.web.alarm.AlarmMessageSender;
import com.navercorp.pinpoint.web.alarm.checker.AlarmChecker;
import com.navercorp.pinpoint.web.service.UserGroupService;

@Component
public class AlarmMessageSenderImple implements AlarmMessageSender, ApplicationContextAware
{
    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserGroupService userGroupService;
    private ISmsDubboService smsDubboService;
    @Value("${dubbo.registry.address:172.18.169.18:12181}")
    private String           address;
    @Value("${dubbo.registry.protocol:zookeeper}")
    private String           protocol;
    @Value("${dubbo.sms.version:1.0.0}")
    private String           version;
    @Value("${dubbo.sms.group:}")
    private String           group;
    
    @SuppressWarnings("rawtypes")
    @Override
    public void sendSms(AlarmChecker checker, int sequenceCount)
    {
        List<String> receivers = userGroupService.selectPhoneNumberOfMember(checker.getuserGroupId());
        if (receivers.size() == 0)
        {
            return;
        }
        for (Object message : checker.getSmsMessage())
        {
            logger.info("send SMS : {}", message);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            for (String phoneNumber : receivers)
            {
                SmsReq sms = new SmsReq();
                sms.setContent((String) message);
                sms.setMobile(phoneNumber);
                sms.setMsgType("pinpoint");
                sms.setSender("pinpoint");
                sms.setSendTime(df.format(new Date()));
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
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void sendEmail(AlarmChecker checker, int sequenceCount)
    {
        logger.debug("邮件发送,尚未实现");
    }
    
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
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
        logger.debug("初始化短信service成功");
    }
}
