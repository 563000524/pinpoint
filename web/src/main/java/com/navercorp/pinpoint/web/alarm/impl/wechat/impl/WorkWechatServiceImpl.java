package com.navercorp.pinpoint.web.alarm.impl.wechat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.navercorp.pinpoint.web.alarm.impl.wechat.WorkWechatService;
import com.navercorp.pinpoint.web.alarm.impl.wechat.bean.WorkWechatAppInfo;
import com.navercorp.pinpoint.web.alarm.impl.wechat.bean.WorkWechatTokenInfo;
import com.navercorp.pinpoint.web.alarm.impl.wechat.req.WorkWechatAppMessageReq;
import com.navercorp.pinpoint.web.alarm.impl.wechat.req.WorkWechatBotMessageReq;
import com.navercorp.pinpoint.web.alarm.impl.wechat.resp.WorkWechatAppMessageResp;
import com.navercorp.pinpoint.web.alarm.impl.wechat.resp.WorkWechatBotMessageResp;
import com.navercorp.pinpoint.web.alarm.impl.wechat.resp.WorkWechatTokenResp;
import com.navercorp.pinpoint.web.alarm.impl.wechat.util.HttpUtil;

/**
 * 企业微信消息发送API适配器
 *
 */
@Component
public class WorkWechatServiceImpl implements WorkWechatService
{
    private static final Logger              logger                = LoggerFactory.getLogger(WorkWechatServiceImpl.class);
    @Value("${alarm.workwechat.appMessageUrl}")
    private String                           appMessageUrl ;
    @Value("${alarm.workwechat.tokenurl:https}")
    private String                           tokenUrl;
    // 要通知的机器人url列表
    @Value("#{'${alarm.workwechat.botUrlList:}'.split(',')}")
    private List<String>                     botUrlList            = new ArrayList<>();
    @Resource
    private List<WorkWechatAppInfo>          workWechatAppInfoList = new ArrayList<>();
    // 应用id、token映射
    private Map<String, WorkWechatTokenInfo> corpidTokenMap        = new ConcurrentHashMap<>();
    
    @Override
    public void sendBOTMessage(String message)
    {
        for (int i = 0; i < botUrlList.size(); i++)
        {
            if(botUrlList.get(i).equals("")==false){
                sendBotMessage$(message, botUrlList.get(i));                
            }
        }
    }
    
    private void sendBotMessage$(String message, String url)
    {
        try
        {
            logger.info("发送机器人消息{},{}", url, message);
            WorkWechatBotMessageReq req = WorkWechatBotMessageReq.buildSimpleWorkWechatBotMessageReq(message);
            String reqJson = JSON.toJSONString(req);
            String resp = HttpUtil.sendBody(url, reqJson);
            logger.info("机器人消息响应{}", resp);
            WorkWechatBotMessageResp wechatBotMessageResp = JSON.parseObject(resp, WorkWechatBotMessageResp.class);
            if (wechatBotMessageResp.getErrcode().equals("0") == false)
            {
                throw new Exception(wechatBotMessageResp.getErrcode() + wechatBotMessageResp.getErrmsg());
            }
            else
            {
            }
        }
        catch (Exception e)
        {
            logger.error("机器人消息异常", e);
        }
    }
    
    @Override
    public void sendAppMessage(String message)
    {
        for (int i = 0; i < workWechatAppInfoList.size(); i++)
        {
            WorkWechatAppInfo workWechatAppInfo = workWechatAppInfoList.get(i);
            sendAppMessage$(message, workWechatAppInfo);
        }
    }
    
    private void sendAppMessage$(String message, WorkWechatAppInfo workWechatAppInfo)
    {
        try
        {
            logger.info("发送APP消息{},{}", workWechatAppInfo, message);
            String accessToken = getAccessToken(workWechatAppInfo.getCorpid(), workWechatAppInfo.getCorpsecret());
            WorkWechatAppMessageReq req = WorkWechatAppMessageReq.buildSimpleWorkWechatAppMessageReq(workWechatAppInfo.getAgentid(), message);
            String reqJson = JSON.toJSONString(req);
            String resp = HttpUtil.sendBody(appMessageUrl + accessToken, reqJson);
            logger.info("APP消息响应{}", resp);
            WorkWechatAppMessageResp wechatAppMessageResp = JSON.parseObject(resp, WorkWechatAppMessageResp.class);
            if (wechatAppMessageResp.getErrcode().equals("0") == false)
            {
                throw new Exception(wechatAppMessageResp.getErrcode() + wechatAppMessageResp.getErrmsg());
            }
            else
            {
            }
        }
        catch (Exception e)
        {
            logger.error("APP消息异常", e);
        }
    }
    
    /**
     * 获取token
     * 
     * @param corpid
     * @param corpsecret
     * @return
     * @throws Exception
     */
    public String getAccessToken(String corpid, String corpsecret) throws Exception
    {
        WorkWechatTokenInfo tokenBean = corpidTokenMap.get(corpid);
        if (tokenBean == null || System.currentTimeMillis() > tokenBean.getTimeoutStamp())
        {
            synchronized (this)
            {
                if (tokenBean == null || System.currentTimeMillis() > tokenBean.getTimeoutStamp())
                {
                    refreshToken(corpid, corpsecret);
                }
            }
        }
        WorkWechatTokenInfo bean = corpidTokenMap.get(corpid);
        if (bean == null)
        {
            logger.error("TOKEN异常");
            throw new Exception("TOKEN异常");
        }
        return bean.getAccessToken();
    }
    
    /**
     * 刷新token
     * 
     * @param corpid
     * @param secret
     */
    private void refreshToken(String corpid, String secret) throws Exception
    {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("corpid", corpid);
        tokenMap.put("corpsecret", secret);
        String resp = HttpUtil.get(tokenUrl, tokenMap);
        logger.info("TOKEN请求{},响应{}", tokenMap, resp);
        WorkWechatTokenResp workWechatTokenResp = JSON.parseObject(resp, WorkWechatTokenResp.class);
        if (workWechatTokenResp.getErrcode().equals("0") == false)
        {
            logger.error("TOKEN异常", workWechatTokenResp);
            throw new Exception(workWechatTokenResp.getErrcode() + workWechatTokenResp.getErrmsg());
        }
        else
        {
            long timeout = workWechatTokenResp.getExpires_in() * 1000 + System.currentTimeMillis() - 1000 * 60 * 5;
            corpidTokenMap.put(corpid, new WorkWechatTokenInfo(timeout, //
                    workWechatTokenResp.getAccess_token()));
        }
    }
    
}
