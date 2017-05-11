package com.whu_phone.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.whu_phone.R;
import com.whu_phone.model.AnnouncementInfo;
import com.whu_phone.presenter.AnnouncementPresenter;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuhui on 2017/2/20.
 */

public class AnnouncementFragment extends Fragment {
    @BindView(R.id.announcement_toolbar)
    Toolbar toolbar;
    @BindView(R.id.announcement_list)
    PullLoadMoreRecyclerView announcementList;

    public List<AnnouncementInfo> infos;
    private AnnouncementPresenter announcementPresenter;
    public static int page=1;
    private SharedPreferences preferences;
    private AnnouncementListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.announcement,container,false);
        ButterKnife.bind(this,view);

        initRecyclerView();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcementPresenter=new AnnouncementPresenter(this);
        infos=announcementPresenter.getInfos();
        preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public void initRecyclerView() {
        announcementList.setLinearLayout();
        announcementList.setColorSchemeResources(R.color.textColorPrimary);
        adapter=new AnnouncementListAdapter(infos);
        announcementList.setAdapter(adapter);
        announcementList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                announcementPresenter.searchAnnouncement(1);
                page=1;
            }

            @Override
            public void onLoadMore() {
                announcementPresenter.searchAnnouncement(++page);
            }
        });
        if (announcementPresenter.getInfos().size()==0||preferences.getLong("refresh_time",0)-System.currentTimeMillis()>24*60*60*1000) {
            announcementList.setRefreshing(true);
            announcementPresenter.searchAnnouncement(1);
        }
    }

    public void searchSuccess() {
        preferences.edit().putLong("refresh_time",System.currentTimeMillis()).commit();
        adapter.notifyDataSetChanged();
        announcementList.setPullLoadMoreCompleted();
    }

    public void refreshFail() {
        announcementList.setPullLoadMoreCompleted();
        Toast.makeText(getContext(),"刷新失败",Toast.LENGTH_SHORT).show();
    }

    public void loadFail() {
        announcementList.setPullLoadMoreCompleted();
        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.announcement_toolbar)
    void goToListTop(){
        announcementList.scrollToTop();
    }


    //adapter
    public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.AnnouncementViewHolder> {
        private List<AnnouncementInfo> infos;
        public AnnouncementListAdapter(List<AnnouncementInfo> infos) {
            this.infos=infos;
        }

        @Override
        public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_list_item,parent,false);
            return new AnnouncementViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
            final AnnouncementInfo info=infos.get(position);
            holder.title.setText(info.getTitle());
            holder.department.setText(info.getDepartment());
            holder.time.setText(info.getTime());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clickUrl = info.getUrl();
                    Intent intent = new Intent(getContext(), DetailAnnouncement.class);
                    intent.putExtra("url", clickUrl);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }

        class AnnouncementViewHolder extends RecyclerView.ViewHolder{
            TextView title;
            TextView department;
            TextView time;
            public AnnouncementViewHolder(View itemView) {
                super(itemView);
                title=(TextView) itemView.findViewById(R.id.announcement_title);
                department=(TextView) itemView.findViewById(R.id.publish_department);
                time=(TextView) itemView.findViewById(R.id.publish_time);
            }
        }
    }

}


