package com.whu_phone.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.whu_phone.R;
import com.whu_phone.presenter.LoginPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuhui on 2016/10/20.
 */

public class Login extends AppCompatActivity {
    private static boolean backKeyPressed = false;
    @BindView(R.id.login_btn) Button loginBtn;
    @BindView(R.id.study_number) EditText number;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.remember_password) CheckBox rememberPassword;
    @BindView(R.id.error_msg) TextView errorMsg;


    private String mynumber;
    private String mypassword;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean ifLogin = preferences.getBoolean("ifLogin", false);

        if (ifLogin) {
            startMainActivity();
        } else {
            //登录
            boolean isRemember = preferences.getBoolean("remember", false);
            if (isRemember) {
                number.setText(preferences.getString("username", ""));
                password.setText(preferences.getString("password", ""));
                rememberPassword.setChecked(true);
                loginBtnCanClick();
            }
            number.addTextChangedListener(watcher);
            password.addTextChangedListener(watcher);

        }

        loginPresenter=new LoginPresenter(this,preferences);

        loginBtn.setOnClickListener(listener);
    }

    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            loginBtnCanClick();
        }
    };

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor = preferences.edit();
            if (rememberPassword.isChecked()) {
                editor.putBoolean("remember", true);
                editor.putString("username", mynumber);
                editor.putString("password", mypassword);
            } else {
                editor.clear();
            }
            editor.commit();

            showProgressDialog();
            loginPresenter.login(mynumber,mypassword);
        }
    };

    @Override
    public void onBackPressed() {
        if (!backKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            backKeyPressed = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    backKeyPressed = false;
                }
            }, 2000);
        } else {
            this.finish();
            System.exit(0);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loginSuccess() {
        closeProgressDialog();
        errorMsg.setVisibility(View.GONE);
        Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
        startMainActivity();
    }

    public void loginFail(String msg) {
        closeProgressDialog();
        errorMsg.setVisibility(View.VISIBLE);
        errorMsg.setText(msg);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在登录。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void loginBtnCanClick() {
        mynumber = number.getText().toString().trim();
        mypassword = password.getText().toString().trim();
        if (!TextUtils.isEmpty(mynumber) && !TextUtils.isEmpty(mypassword)) {
            loginBtn.setClickable(true);
            loginBtn.setBackgroundColor(getResources().getColor(R.color.btnCanClickColor));
        } else {
            loginBtn.setClickable(false);
            loginBtn.setBackgroundColor(getResources().getColor(R.color.btnCanNotClickColor));
        }

    }
}
