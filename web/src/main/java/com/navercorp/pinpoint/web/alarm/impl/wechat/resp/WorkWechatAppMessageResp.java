package com.navercorp.pinpoint.web.alarm.impl.wechat.resp;

import lombok.Data;

/**
 * 发送APP消息响应值
 *
 */
@Data
public class WorkWechatAppMessageResp
{
    private String errcode;
    private String errmsg;
    private String access_token;
    private int    expires_in;
}
