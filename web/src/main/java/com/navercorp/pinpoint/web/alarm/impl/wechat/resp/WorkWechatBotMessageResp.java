package com.navercorp.pinpoint.web.alarm.impl.wechat.resp;

import lombok.Data;

/**
 * 发送机器人消息响应值
 *
 */
@Data
public class WorkWechatBotMessageResp
{
    private String errcode;
    private String errmsg;
}
