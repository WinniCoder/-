package com.whu_phone.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.whu_phone.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.framelayout)
    FrameLayout frameLayout;
    @BindView(R.id.tab)
    PagerBottomTabLayout tab;

    public static List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initFragment();
        initBottomTab();
    }

    private void initFragment() {
        fragments=new ArrayList<>();
        fragments.add(new CourseFragment());
        fragments.add(new AnnouncementFragment());
        fragments.add(new ScoolFragment());
        fragments.add(new MineFragment());
        fragments.add(new ExaminationFragment());
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.framelayout,fragments.get(0));
        transaction.commit();
    }

    private void initBottomTab() {
        Controller controller=tab.builder()
                .addTabItem(R.drawable.course_table, "课表", getResources().getColor(R.color.bottonSelectedColor))
                .addTabItem(R.drawable.announcement, "通知", getResources().getColor(R.color.bottonSelectedColor))
                .addTabItem(R.drawable.school, "校园网", getResources().getColor(R.color.bottonSelectedColor))
                .addTabItem(R.drawable.mine, "我", getResources().getColor(R.color.bottonSelectedColor))
                .build();
        controller.addTabItemClickListener(new OnTabItemSelectListener() {
            @Override
            public void onSelected(int index, Object tag) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout,fragments.get(index));
                transaction.commit();
            }

            @Override
            public void onRepeatClick(int index, Object tag) {

            }
        });
    }

}
