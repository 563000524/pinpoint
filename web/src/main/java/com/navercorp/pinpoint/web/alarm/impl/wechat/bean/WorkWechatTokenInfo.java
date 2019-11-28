package com.navercorp.pinpoint.web.alarm.impl.wechat.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkWechatTokenInfo
{
    private long   timeoutStamp;
    private String accessToken;
}
