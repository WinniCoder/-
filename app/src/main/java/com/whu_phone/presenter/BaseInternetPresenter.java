package com.whu_phone.presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.whu_phone.model.ResponseClass;

import java.util.Map;


/**
 * Created by wuhui on 2017/2/15.
 */

public class BaseInternetPresenter {
    public static final String TAG = "GsonRequest";
    private RequestQueue queue;

    public void postAPI(int method, final Context context, String url, final Map<String, String> params, Class clazz, boolean isList) {
        queue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest(method, url,
                new Response.Listener<ResponseClass>() {
                    @Override
                    public void onResponse(ResponseClass response) {
                        if (response.getCode() == 200) {
                            successResponse(response);
                        } else {
                            errorResponse(response.getMsg());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("RESPONSE_Error", "ERROR", error);
                errorResponse("网络连接超时");
            }
        }, clazz, isList) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        gsonRequest.setTag(TAG);
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(gsonRequest);
    }

    protected void successResponse(ResponseClass response) {
    }

    protected void errorResponse(String msg) {
    }

    public void cancelRequest() {
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


}
