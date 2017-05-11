package com.whu_phone.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.query.Delete;
import com.whu_phone.model.AnnouncementInfo;
import com.whu_phone.model.ExaminationScore;
import com.whu_phone.model.MyCourse;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;

/**
 * Created by wuhui on 2017/3/8.
 */

public class MinePresenter {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MinePresenter(Context context) {
        this.context=context;
        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
    }

    public StudentInfo getInfo() {
        return new SaveToPreferences<StudentInfo>(context).getObject("studentinfo");
    }

    public void exit() {
        String number=preferences.getString("username","");
        String password=preferences.getString("password","");
        boolean isRemember = preferences.getBoolean("remember", false);
        editor.clear();
        editor.putBoolean("remember", isRemember);
        editor.putString("username",number);
        editor.putString("password",password);
        editor.commit();
        new Delete().from(MyCourse.class).execute();
        new Delete().from(AnnouncementInfo.class).execute();
        new Delete().from(ExaminationScore.class).execute();
    }
}
