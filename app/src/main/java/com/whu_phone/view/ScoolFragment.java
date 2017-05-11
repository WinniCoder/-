package com.whu_phone.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whu_phone.R;
import com.whu_phone.model.OnlinePC;
import com.whu_phone.presenter.ScoolPresenter;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuhui on 2017/2/20.
 */

public class ScoolFragment extends Fragment {
    @BindView(R.id.left_money_num)
    TextView leftMoney;
    @BindView(R.id.left_day)
    TextView leftDay;
    @BindView(R.id.net_status)
    TextView netStatus;
    @BindView(R.id.online_list)
    PullLoadMoreRecyclerView onlineList;
    @BindView(R.id.no_computer)
    LinearLayout noComputer;

    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<OnlinePC> pcs;
    private ScoolPresenter scoolPresenter;
    private NetAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.school, container, false);
        ButterKnife.bind(this, view);

        scoolPresenter = new ScoolPresenter(this);
        pcs = new ArrayList<>();
        initList();

        return view;
    }

    private void initList() {
        onlineList.setLinearLayout();
        onlineList.setPushRefreshEnable(false);
        adapter = new NetAdapter();
        onlineList.setAdapter(adapter);
        onlineList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                scoolPresenter.onlineSearchRequest();
            }

            @Override
            public void onLoadMore() {

            }
        });
        onlineList.setRefreshing(true);
        scoolPresenter.onlineSearchRequest();
    }

    public void searchSuccess(ArrayList<OnlinePC> pcs, double balance, String status) {
        onlineList.setPullLoadMoreCompleted();
        this.pcs = pcs;
        leftMoney.setText(String.valueOf(balance));
        if (isAdded()) {
            if (status.equals("正常")) {
                netStatus.setTextColor(getResources().getColor(R.color.normalColor));
            } else {
                netStatus.setTextColor(getResources().getColor(R.color.errorColor));
            }
        }
        netStatus.setText(status);
        leftDay.setText(String.valueOf((int) (balance * 100 / 66)));
        adapter.notifyDataSetChanged();

        noComputer.setVisibility(pcs.size() == 0 ? View.VISIBLE : View.GONE);
    }

    public void searchFail() {
        onlineList.setPullLoadMoreCompleted();
        Toast.makeText(getContext(), "网络状态查询失败", Toast.LENGTH_SHORT).show();
    }

    public void downSuccess(String ip, int index) {
        onlineList.setPullLoadMoreCompleted();
        Toast.makeText(getContext(), "设备 " + ip + " 下线成功", Toast.LENGTH_SHORT).show();
        pcs.remove(index);
        adapter.notifyDataSetChanged();
    }

    public void downFail() {
        onlineList.setPullLoadMoreCompleted();
        Toast.makeText(getContext(), "设备下线失败，请稍后重试", Toast.LENGTH_SHORT).show();
    }

    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class NetAdapter extends RecyclerView.Adapter<NetAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final OnlinePC pc = pcs.get(position);
            holder.onlineTime.setText("上线时间：" + pc.getUptime());
            holder.isSelf.setVisibility(pc.getIp().equals(getLocalIp()) ? View.VISIBLE : View.INVISIBLE);
            holder.downLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("确认下线？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onlineList.setRefreshing(true);
                                    scoolPresenter.downLineRequest(pc.getIp(), position);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return pcs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView onlineTime;
            TextView isSelf;
            Button downLine;

            public ViewHolder(View itemView) {
                super(itemView);
                onlineTime = (TextView) itemView.findViewById(R.id.online_time);
                isSelf = (TextView) itemView.findViewById(R.id.isSelf);
                downLine = (Button) itemView.findViewById(R.id.down_line);
            }
        }
    }

}
