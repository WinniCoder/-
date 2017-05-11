package com.whu_phone.presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whu_phone.model.OnlinePC;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;
import com.whu_phone.view.ScoolFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by wuhui on 2017/4/21.
 */

public class ScoolPresenter {
    private ScoolFragment scoolFragment;
    private RequestQueue requestQueue;
    private String token;

    public ScoolPresenter(ScoolFragment scoolFragment) {
        this.scoolFragment = scoolFragment;
        requestQueue = Volley.newRequestQueue(scoolFragment.getContext());
        token = ((StudentInfo) (new SaveToPreferences<StudentInfo>(scoolFragment.getContext()).getObject("studentinfo"))).getToken();
    }

    public void onlineSearchRequest() {
        StringRequest onlineRequest = new StringRequest(Request.Method.POST, "http://123.206.44.238:8888/netinfo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getInt("code")==200){
                                JSONObject data=jsonObject.getJSONObject("data");
                                Type pcListType=new TypeToken<ArrayList<OnlinePC>>(){}.getType();
                                ArrayList<OnlinePC> pcs=new Gson().fromJson(data.getString("online_pc"),pcListType);
                                double balance=data.getDouble("balance");
                                String status=data.getString("status");
                                scoolFragment.searchSuccess(pcs,balance,status);
                            }else {
                                scoolFragment.searchFail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("wuhuiErrot","wuhuiError",error);
                        scoolFragment.searchFail();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        onlineRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 10, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(onlineRequest);
    }

    public void downLineRequest(final String ip, final int index) {
        StringRequest downRequest = new StringRequest(Request.Method.POST, "http://123.206.44.238:8888/netoffline",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getInt("code")==200){
                                scoolFragment.downSuccess(ip,index);
                            } else {
                                scoolFragment.downFail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("wuhuierror","wuhuierror",error);
                        scoolFragment.downFail();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("ip", ip);
                return params;
            }
        };
        downRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 10, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(downRequest);
    }
}
