package com.whu_phone.presenter;

import android.content.SharedPreferences;
import android.preference.Preference;

import com.android.volley.Request;
import com.whu_phone.model.MyCourse;
import com.whu_phone.model.ResponseClass;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;
import com.whu_phone.view.Login;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuhui on 2017/2/21.
 */

public class LoginPresenter extends BaseInternetPresenter {
    private Login loginActivity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String number;
    private String password;

    public LoginPresenter(Login loginActivity,SharedPreferences preferences) {
        this.loginActivity = loginActivity;
        this.preferences=preferences;
        editor=preferences.edit();
    }

    public void login(String number, String password) {
        Map<String, String> params = new HashMap<>();
        this.number=number;
        this.password=password;
        params.put("username", number);
        params.put("password", password);
        postAPI(Request.Method.POST,loginActivity, "http://123.206.44.238:8888/login", params, StudentInfo.class, false);
    }

    @Override
    protected void errorResponse(String msg) {
        loginActivity.loginFail(msg);
    }

    @Override
    protected void successResponse(ResponseClass response) {
        loginActivity.loginSuccess();
        try {
            new SaveToPreferences<StudentInfo>(loginActivity).saveObject((StudentInfo) response.getData(),"studentinfo");
            editor.putBoolean("ifLogin", true);
            editor.putString("username",number);
            editor.putString("password",password);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
