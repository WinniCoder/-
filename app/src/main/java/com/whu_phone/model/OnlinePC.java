package com.whu_phone.model;

/**
 * Created by wuhui on 2017/4/21.
 */

public class OnlinePC {
    private String ip;
    private String uptime;

    public OnlinePC(String ip, String uptime) {
        this.ip = ip;
        this.uptime = uptime;
    }

    public String getIp() {
        return ip;
    }

    public String getUptime() {
        return uptime;
    }
}
