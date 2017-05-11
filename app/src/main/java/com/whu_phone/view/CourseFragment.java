package com.whu_phone.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.whu_phone.R;
import com.whu_phone.presenter.CoursePresenter;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by wuhui on 2017/2/20.
 */

public class CourseFragment extends Fragment {

    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout refreshLayout;
    @BindView(R.id.examination)
    TextView examination;
    @BindView(R.id.weeks_num)
    Spinner weeksNum;
    @BindView(R.id.course_view)
    CourseView courseView;

    private View view;
    private TextView daysNum;
    public static int currentWeek;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CoursePresenter coursePresenter;
    private ProgressDialog progressDialog;

    private int refreshIconColor[] = new int[]{
            Color.parseColor("#F794F1"), Color.parseColor("#CD73EB"), Color.parseColor("#EDCAD5"), Color.parseColor("#F7E74F"), Color.parseColor("#FFA896"),
    };
    private String[] weeksArray = new String[]{
            "第一周 ", "第二周 ", "第三周 ", "第四周 ", "第五周 ", "第六周 ", "第七周 ", "第八周 ", "第九周 ", "第十周 ",
            "第十一周 ", "第十二周 ", "第十三周 ", "第十四周 ", "第十五周 ", "第十六周 ", "第十七周 ", "第十八周 ", "第十九周 ", "第二十周 "
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.course, container, false);
        ButterKnife.bind(this, view);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        coursePresenter = new CoursePresenter(this, preferences);

        if (!preferences.getBoolean("course_refresh_time", false)) {
            showProgressDialog();
            coursePresenter.refreshCourse(getContext());
        }

        setweeksNum();

        setDayNumBk();

        initRefreshLayout();

        examination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);
                transaction.replace(R.id.framelayout, MainActivity.fragments.get(4));
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //设置周数
    private void setweeksNum() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.weeks_item, weeksArray);
        spinnerAdapter.setDropDownViewResource(R.layout.weeks_dropdown_item);
        weeksNum.setAdapter(spinnerAdapter);
        //设置下拉项高度
        Field field;
        try {
            field = Spinner.class.getDeclaredField("mPopup");
            field.setAccessible(true);
            ListPopupWindow popUp = (ListPopupWindow) field.get(weeksNum);
            popUp.setHeight(500);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        setCurrentWeek();
        weeksNum.setSelection(currentWeek > 20 ? 20 : currentWeek - 1);

        weeksNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentWeek = position + 1;
                courseView.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //设置周几
    private void setDayNumBk() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String dayNum = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        switch (dayNum) {
            case "1":
                daysNum = (TextView) view.findViewById(R.id.Sunday);
                break;
            case "2":
                daysNum = (TextView) view.findViewById(R.id.Monday);
                break;
            case "3":
                daysNum = (TextView) view.findViewById(R.id.Tuesday);
                break;
            case "4":
                daysNum = (TextView) view.findViewById(R.id.Wednesday);
                break;
            case "5":
                daysNum = (TextView) view.findViewById(R.id.Thursday);
                break;
            case "6":
                daysNum = (TextView) view.findViewById(R.id.Friday);
                break;
            case "7":
                daysNum = (TextView) view.findViewById(R.id.Saturday);
                break;
        }
        daysNum.setBackgroundColor(Color.parseColor("#72ddf9"));
    }

    private void initRefreshLayout() {
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setColorSchemeColors(refreshIconColor);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                coursePresenter.refreshCourse(getContext());
            }
        });
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("课表加载中。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        refreshLayout.setRefreshing(false);
    }

    public void setCurrentWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(new Date(System.currentTimeMillis()));
        String beginTime = preferences.getString("week", currentTime);
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        try {
            cal1.setTime(formatter.parse(currentTime));
            cal2.setTime(formatter.parse(beginTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        double dayCount = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);
        currentWeek = (int) dayCount / 7 + 1;
        editor.putInt("currentWeek",currentWeek).commit();
    }

    public void refreshSuccess() {
        closeProgressDialog();
        weeksNum.setSelection(currentWeek - 1);
        courseView.invalidate();
        Toast.makeText(getContext(), "课表get √", Toast.LENGTH_SHORT).show();
        if (!preferences.getBoolean("course_refresh_time", false)) {
            editor.putBoolean("course_refresh_time", true);
            editor.commit();
        }
    }

    public void refreshFail(String msg) {
        closeProgressDialog();
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
