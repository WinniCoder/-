package com.whu_phone.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.whu_phone.R;
import com.whu_phone.model.ExaminationScore;
import com.whu_phone.presenter.ExaminationPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuhui on 2017/3/2.
 */

public class ExaminationOneFragment extends Fragment {
    private View view;
    @BindView(R.id.score_list)
    ListView listView;
    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout refreshLayout;
    private ExaminationPresenter examinationPresenter;

    private int refreshIconColor[] = new int[]{
            Color.parseColor("#F794F1"), Color.parseColor("#CD73EB"), Color.parseColor("#EDCAD5"), Color.parseColor("#F7E74F"), Color.parseColor("#FFA896"),
    };

    public ExaminationOneFragment(ExaminationPresenter examinationPresenter) {
        this.examinationPresenter=examinationPresenter;
    }

    public static ExaminationOneFragment create(String term,ExaminationPresenter examinationPresenter) {
        ExaminationOneFragment sf=new ExaminationOneFragment(examinationPresenter);
        Bundle bundle=new Bundle();
        bundle.putString("term",term);
        sf.setArguments(bundle);
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.examination_one, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle=getArguments();
        List<ExaminationScore> scores = examinationPresenter.loadScores(bundle.getString("term",""));
        listView.setAdapter(new ScoreItemAdapter(getContext(), R.layout.score_item, scores));
        initRefreshLayout();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initRefreshLayout() {
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
        refreshLayout.setColorSchemeColors(refreshIconColor);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                examinationPresenter.searchExamination();
            }
        });
    }

    public class ScoreItemAdapter extends ArrayAdapter<ExaminationScore> {
        private int resourceId;


        public ScoreItemAdapter(Context context, int resource, List<ExaminationScore> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ExaminationScore score = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.category = (TextView) view.findViewById(R.id.category);
                viewHolder.credit = (TextView) view.findViewById(R.id.credit);
                viewHolder.grade = (TextView) view.findViewById(R.id.grade);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.name.setText(score.getName());
            viewHolder.category.setText(score.getCategory());
            viewHolder.credit.setText(score.getCredit()+"学分");
            viewHolder.grade.setText(score.getGrade());
            return view;
        }

        class ViewHolder {
            TextView name;
            TextView category;
            TextView credit;
            TextView grade;
        }
    }
}
