package com.navercorp.pinpoint.web.alarm.impl.wechat.req;

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
public class WorkWechatAppMessageReq
{
    private String                        touser;                   // 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向该企业应用的全部成员发送
    private String                        toparty;                  // 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
    private String                        totag;                    // 标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
    private String                        msgtype;                  // 消息类型，此时固定为：text
    private String                        agentid;                  // 企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口
                                                                    // 获取企业授权信息
                                                                    // 获取该参数值
    private WorkWechatAppMessageReqDetail text;                     // 消息内容，最长不超过2048个字节，超过将截断（支持id转译）
    private String                        safe;                     // 表示是否是保密消息，0表示否，1表示是，默认0
    private String                        enable_id_trans;          // 表示是否开启id转译，0表示否，1表示是，默认0
    private String                        enable_duplicate_check;   // 表示是否开启重复消息检查，0表示否，1表示是，默认0
    private String                        duplicate_check_interval; // 表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
    
    public static WorkWechatAppMessageReq buildSimpleWorkWechatAppMessageReq(String agentid, String message)
    {
        WorkWechatAppMessageReq req = new WorkWechatAppMessageReq();
        req.setTouser("@all");
        req.setAgentid(agentid);
        req.setMsgtype("text");
        req.setText(new WorkWechatAppMessageReqDetail(message));
        return req;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkWechatAppMessageReqDetail
    {
        private String content;
    }
    
}
