package com.navercorp.pinpoint.web.alarm.impl.wechat.resp;

import lombok.Data;

/**
 * 申请token响应值
 *
 */
@Data
public class WorkWechatTokenResp
{
    private String errcode;
    private String errmsg;
    private String access_token;
    private int    expires_in;
}
