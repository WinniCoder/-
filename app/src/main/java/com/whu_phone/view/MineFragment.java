package com.whu_phone.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.whu_phone.R;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;
import com.whu_phone.presenter.MinePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by wuhui on 2017/2/20.
 */

public class MineFragment extends Fragment {
    @BindView(R.id.mine_toolbar)
    Toolbar toolbar;
    @BindView(R.id.student_ID)
    TextView studentID;
    @BindView(R.id.student_type)
    TextView studenType;
    @BindView(R.id.college)
    TextView college;
    @BindView(R.id.major)
    TextView major;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.birthday)
    TextView birthday;
    @BindView(R.id.dormitory)
    TextView dormitory;
    @BindView(R.id.join_day)
    TextView joinDay;


    MorphyToolbar morphyToolbar;

    private StudentInfo info;
    private MinePresenter minePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine, container, false);
        ButterKnife.bind(this, view);

        minePresenter=new MinePresenter(getContext());

        info = minePresenter.getInfo();

        initToolbar();
        initInfos();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void initToolbar() {
        morphyToolbar = MorphyToolbar.builder((AppCompatActivity) getActivity(), toolbar)
                .withToolbarAsSupportActionBar()
                .withTitle(info.getName())
                .withSubtitle(info.getCollege())
                .withPicture(R.drawable.header)
                .withHidePictureWhenCollapsed(false)
                .build();

        morphyToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morphyToolbar.isCollapsed()) {
                    morphyToolbar.expand(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), new MorphyToolbar.OnMorphyToolbarExpandedListener() {
                        @Override
                        public void onMorphyToolbarExpanded() {
                        }
                    });
                } else {
                    morphyToolbar.collapse();
                }
            }
        });

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initInfos() {
        studentID.setText(info.getStudentID());
        studenType.setText(info.getStudentType());
        college.setText(info.getCollege());
        major.setText(info.getMajor());
        grade.setText(info.getGrade());
        sex.setText(info.getSex());
        birthday.setText(info.getBirthday());
        dormitory.setText(info.getDormitory());
        joinDay.setText(info.getJoinDay());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage("确定注销登录？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    minePresenter.exit();
                    Intent intent = new Intent(getActivity(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
