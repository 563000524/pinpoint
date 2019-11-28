package com.navercorp.pinpoint.web.alarm.impl;

public interface AlarmMessageProxy
{
    void sendMessage(String number, String message);
}
