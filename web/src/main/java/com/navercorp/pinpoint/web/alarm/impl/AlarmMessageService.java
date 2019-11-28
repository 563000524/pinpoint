package com.navercorp.pinpoint.web.alarm.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.navercorp.pinpoint.web.alarm.AlarmMessageSender;
import com.navercorp.pinpoint.web.alarm.checker.AlarmChecker;
import com.navercorp.pinpoint.web.service.UserGroupService;

/**
 * 预警通知总入口
 *
 */
@Component
public class AlarmMessageService implements AlarmMessageSender
{
    private final Logger            logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserGroupService        userGroupService;
    //企业微信代理
    @Autowired
    private WechatAlarmMessageProxy wechatAlarmMessageProxy;
    //短信机代理
    @Autowired
    private SmsAlarmMessageProxy    smsAlarmMessageProxy;
    
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
            for (String phoneNumber : receivers)
            {
                smsAlarmMessageProxy.sendMessage(phoneNumber, (String) message);
            }
            wechatAlarmMessageProxy.sendMessage("", (String) message);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void sendEmail(AlarmChecker checker, int sequenceCount)
    {
        logger.debug("邮件发送,尚未实现");
    }
    
}
