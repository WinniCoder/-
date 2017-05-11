package com.whu_phone.presenter;

import android.support.v4.app.Fragment;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.whu_phone.model.AnnouncementInfo;
import com.whu_phone.model.ResponseClass;
import com.whu_phone.view.AnnouncementFragment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhui on 2017/2/25.
 */

public class AnnouncementPresenter extends BaseInternetPresenter {
    private AnnouncementFragment fragment;
    private int page;

    public AnnouncementPresenter(AnnouncementFragment fragment) {
        this.fragment = fragment;
    }

    public void searchAnnouncement(int page) {
        this.page = page;
        Map<String, String> params = new HashMap<>();
        postAPI(Request.Method.GET, fragment.getContext(), "http://123.206.44.238:8888/noticeinfo?page=" + page, params, AnnouncementInfo.class, true);
    }

    @Override
    protected void successResponse(ResponseClass response) {
        if (page == 1) {
            deleteInfos();
            saveInfo((List<AnnouncementInfo>) response.getData());
            fragment.infos.clear();
        }
        fragment.infos.addAll((List<AnnouncementInfo>) response.getData());
        fragment.searchSuccess();
    }

    @Override
    protected void errorResponse(String msg) {
        if (page == 1) {
            fragment.refreshFail();
        } else {
            fragment.loadFail();
        }
    }

    public void saveInfo(List<AnnouncementInfo> announcementInfos) {
        ActiveAndroid.beginTransaction();
        try {
            for (AnnouncementInfo announcementInfo : announcementInfos) {
                announcementInfo.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public List<AnnouncementInfo> getInfos() {
        return new Select().from(AnnouncementInfo.class).execute();
    }

    public void deleteInfos() {
        new Delete().from(AnnouncementInfo.class).execute();
    }
}
