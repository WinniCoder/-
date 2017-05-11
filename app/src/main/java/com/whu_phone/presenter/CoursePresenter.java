package com.whu_phone.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
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
import com.whu_phone.model.MyCourse;
import com.whu_phone.model.ResponseClass;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;
import com.whu_phone.view.CourseFragment;
import com.whu_phone.view.CourseView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhui on 2017/2/20.
 */

public class CoursePresenter {
    private CourseFragment courseFragment;
    private SharedPreferences.Editor editor;

    public CoursePresenter(CourseFragment courseFragment,SharedPreferences preferences) {
        this.courseFragment=courseFragment;
        this.editor=preferences.edit();
    }

    public static List<MyCourse> getAllCourses(int week) {
        return new Select()
                .from(MyCourse.class)
                .where("end_week>=? ",week)
                .where("start_week<=?",week)
                .execute();
    }

    public static List<MyCourse> getCourses() {
        return new Select().from(MyCourse.class).execute();
    }

    public void addCourse(String name, String place, int startWeek, int endWeek, int day, String teacher, int startNum, int endNum) {
        MyCourse course=new MyCourse(name,place,startWeek,endWeek,day,teacher,startNum,endNum);
        course.save();
    }

    public void addAllCourses(List<MyCourse> courses) {
        ActiveAndroid.beginTransaction();
        try {
            for (MyCourse course:courses) {
                course.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void deleteCourse() {
        new Delete().from(MyCourse.class).execute();
    }

    public void refreshCourse(final Context context) {
        RequestQueue queue= Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://123.206.44.238:8888/course", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jResponse=new JSONObject(response);
                    if (jResponse.getInt("code") == 200) {
                        JSONObject data = jResponse.getJSONObject("data");
                        String yearWeekTime = data.getString("week");
                        editor.putString("week", yearWeekTime);
                        editor.commit();
                        courseFragment.setCurrentWeek();
                        List<MyCourse> courses=new ArrayList<>();
                        JSONArray jsonArray=data.getJSONArray("courses");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject course = jsonArray.getJSONObject(i);
                            MyCourse myCourse = new MyCourse(course.getString("name"), course.getString("place"),
                                    course.getInt("startWeek"), course.getInt("endWeek"), course.getInt("day") + 1,
                                    course.getString("teacher"), course.getInt("startTime"), course.getInt("endTime"));
                            courses.add(myCourse);
                        }
                        successResponse(courses);
                    } else {
                        errorResponse(jResponse.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("token",((StudentInfo)(new SaveToPreferences<StudentInfo>(context).getObject("studentinfo"))).getToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 10, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


    protected void successResponse(List<MyCourse> courses) {
        deleteCourse();
        addAllCourses(courses);
        courseFragment.refreshSuccess();
    }

    protected void errorResponse(String msg) {
        courseFragment.refreshFail(msg);
    }
}
