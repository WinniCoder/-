package com.whu_phone.model;

import java.util.List;

/**
 * Created by wuhui on 2017/2/15.
 */

public class ResponseClass<T> {
    private int code;
    private String msg;
    private T data;

    public ResponseClass(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
