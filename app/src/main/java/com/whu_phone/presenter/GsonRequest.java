package com.whu_phone.presenter;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whu_phone.model.ResponseClass;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import ikidou.reflect.TypeBuilder;

import java.io.UnsupportedEncodingException;

/**
 * Created by wuhui on 2017/2/15.
 */

public class GsonRequest extends Request<ResponseClass> {
    private final Response.Listener<ResponseClass> listener;
    private Class clazz;
    private boolean isList;
    private static Gson gson;

    public GsonRequest(int method, String url, Response.Listener<ResponseClass> listener, Response.ErrorListener errorListener,Class clazz,boolean isList) {
        super(method, url, errorListener );
        this.listener=listener;
        this.clazz=clazz;
        this.isList=isList;
    }

    @Override
    protected Response<ResponseClass> parseNetworkResponse(NetworkResponse response) {
        getGson();
        try {
            String jsonString= new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return isList?Response.success(fromJsonArray(jsonString,clazz),HttpHeaderParser.parseCacheHeaders(response)):Response.success(fromJsonObject(jsonString,clazz),HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ResponseClass response) {
        listener.onResponse(response);
    }

    public static <T> ResponseClass<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(ResponseClass.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();

        ResponseClass<List<T>> test = gson.fromJson(json, type);
        return test;
    }

    public static <T> ResponseClass<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(ResponseClass.class)
                .addTypeParam(clazz)
                .build();
        return gson.fromJson(json, type);
    }

    private void getGson() {
        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return clazz == Field.class || clazz == Method.class;
            }
        };
        gson = new GsonBuilder()
                .addSerializationExclusionStrategy(exclusionStrategy)
                .addDeserializationExclusionStrategy(exclusionStrategy)
                .create();
    }

}
