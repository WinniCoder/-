package com.whu_phone.presenter;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.whu_phone.model.ExaminationScore;
import com.whu_phone.model.ResponseClass;
import com.whu_phone.model.SaveToPreferences;
import com.whu_phone.model.StudentInfo;
import com.whu_phone.view.ExaminationFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhui on 2017/3/2.
 */

public class ExaminationPresenter extends BaseInternetPresenter {
    private ExaminationFragment fragment;

    public ExaminationPresenter(ExaminationFragment fragment) {
        this.fragment=fragment;
    }
    public void searchExamination() {
        String token=((StudentInfo)(new SaveToPreferences<StudentInfo>(fragment.getContext()).getObject("studentinfo"))).getToken();
        postAPI(Request.Method.GET,fragment.getContext(),"http://123.206.44.238:8888/grade?token="+token,new HashMap<String, String>(),ExaminationScore.class,true);
    }

    @Override
    protected void successResponse(ResponseClass response) {
        deleteScores();
        addScores((List<ExaminationScore>) response.getData());
        fragment.refreshSuccess();
    }

    @Override
    protected void errorResponse(String msg) {
        fragment.refreshFail(msg);
    }

    public void addScores(List<ExaminationScore> scores) {
        ActiveAndroid.beginTransaction();
        try {
            for(ExaminationScore score:scores) {
                score.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void deleteScores() {
        new Delete().from(ExaminationScore.class).execute();
    }

    public List<String> loadTerms() {
        List<ExaminationScore> termscores= new Select().distinct().from(ExaminationScore.class).groupBy("term").orderBy("term DESC").execute();
        List<String> terms=new ArrayList<>();
        for (ExaminationScore termscore:termscores) {
            terms.add(termscore.getTerm());
        }
        return terms;
    }

    public List<ExaminationScore> loadScores(String term) {
        return new Select().from(ExaminationScore.class).where("term=?",term).execute();
    }
}
