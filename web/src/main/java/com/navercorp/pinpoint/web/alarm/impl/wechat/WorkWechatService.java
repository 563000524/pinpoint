package com.navercorp.pinpoint.web.alarm.impl.wechat;

public interface WorkWechatService
{
    /**
     * 向应用发送消息
     * 
     * @param message
     */
    void sendAppMessage(String message);
    
    /**
     * 使用微信机器人发送消息
     * 
     * @param message
     */
    void sendBOTMessage(String message);
}
