package com.navercorp.pinpoint.web.alarm.impl.wechat.req;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息格式
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkWechatBotMessageReq
{
    private String                        msgtype;
    private WorkWechatBotMessageReqDetail text;
    
    public static WorkWechatBotMessageReq buildSimpleWorkWechatBotMessageReq(String message)
    {
        List<String> list = new ArrayList<>();
        list.add("@all");
        WorkWechatBotMessageReq req = new WorkWechatBotMessageReq("text", //
                new WorkWechatBotMessageReqDetail(list, message));
        return req;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkWechatBotMessageReqDetail
    {
        private List<String> mentioned_list;
        private String       content;
    }
    
}
