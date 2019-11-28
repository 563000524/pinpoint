package com.navercorp.pinpoint.web.alarm.impl.wechat.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil
{
    private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS) // 设置连接超时
            .readTimeout(60, TimeUnit.SECONDS) // 设置读超时
            .writeTimeout(60, TimeUnit.SECONDS) // 设置写超时
            .retryOnConnectionFailure(true).connectionPool(new ConnectionPool(5, 6, TimeUnit.MINUTES))
            // 是否自动重连
            .build();
    
    @SuppressWarnings("deprecation")
    public static String sendBody(String url, String json) throws IOException
    {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }
    
    public static String get(String url, Map<String, String> param) throws IOException
    {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : param.keySet())
        {
            String val = param.get(key);
            urlBuilder.addQueryParameter(key, val);
        }
        Request request = new Request.Builder().url(urlBuilder.build()).get().build();
        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }
}
