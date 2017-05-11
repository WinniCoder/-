package com.whu_phone.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.camnter.easyslidingtabs.widget.EasySlidingTabs;
import com.whu_phone.R;
import com.whu_phone.presenter.ExaminationPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuhui on 2017/3/1.
 */

public class ExaminationFragment extends Fragment {

    @BindView(R.id.tabs)
    EasySlidingTabs tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.course)
    TextView course;

    private List<Fragment> fragments;
    private List<String> terms;
    private ExaminationPresenter examinationPresenter;
    private long currentTime;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TabsFragmentAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.examination, container, false);
        ButterKnife.bind(this, view);

        initScores();

        initFragmentPager();
        tabs.setViewPager(pager);

        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_anim,R.anim.exit_anim);
                transaction.replace(R.id.framelayout, MainActivity.fragments.get(0));
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examinationPresenter = new ExaminationPresenter(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
    }

    private void initScores() {
        currentTime = System.currentTimeMillis();
        if (currentTime - preferences.getLong("score_refresh_time", 0) > 24 * 60 * 60 * 1000) {
            showProgressDialog();
            examinationPresenter.searchExamination();
        }
    }

    private void initFragmentPager() {
        fragments = new ArrayList<>();
        terms = examinationPresenter.loadTerms();
        for (int i = 0; i < terms.size(); i++) {
            fragments.add(ExaminationOneFragment.create(terms.get(i), examinationPresenter));
        }
        adapter = new TabsFragmentAdapter(getChildFragmentManager(), terms, fragments);
        pager.setAdapter(adapter);
    }

    public void refreshSuccess() {
        closeProgressDialog();
        initFragmentPager();
        tabs.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "查询成功", Toast.LENGTH_SHORT).show();
        editor.putLong("score_refresh_time", currentTime).commit();
    }

    public void refreshFail(String msg) {
        closeProgressDialog();
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在查询成绩。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
//        refreshLayout.setRefreshing(false);
    }


    public class TabsFragmentAdapter extends FragmentPagerAdapter implements EasySlidingTabs.TabsTitleInterface {
        private List<String> titles;
        private List<Fragment> fragments;

        public TabsFragmentAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragments.get(position);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public SpannableString getTabTitle(int position) {
            CharSequence title = getPageTitle(position);
            if (TextUtils.isEmpty(title)) return new SpannableString("");
            return new SpannableString(title);
        }

        @Override
        public int getTabDrawableBottom(int position) {
            return 0;
        }

        @Override
        public int getTabDrawableLeft(int position) {
            return 0;
        }

        @Override
        public int getTabDrawableRight(int position) {
            return 0;
        }

        @Override
        public int getTabDrawableTop(int position) {
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < titles.size()) {
                return titles.get(position);
            } else {
                return "";
            }
        }
    }
}
