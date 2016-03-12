package com.wiky.myapplication.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiky.myapplication.R;
import com.wiky.myapplication.entity.MyWifiScanResult;

import java.util.ArrayList;

/**
 * Created by 4399_wuhui on 2016/3/2.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<MyWifiScanResult> wifiDatas;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    //自定义的ViewHolder,持有Item中的所有View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 当前item布局中仅有的一个TextView
        public View view;
        public TextView mBssid;   //wifi名称
        public ImageView mWifiType;
        public TextView mSignalStrenth; //信号强度

        public ViewHolder(View v) {
            super(v);
            view = v;
            mBssid = (TextView) v.findViewById(R.id.txt_bssid);
            mWifiType = (ImageView) v.findViewById(R.id.img_wifi_type_lock);
            mSignalStrenth = (TextView) v.findViewById(R.id.tv_signal_strenth);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<MyWifiScanResult> wifiDatas) {
        this.wifiDatas = wifiDatas;
    }

    // 创建一个新的 views (被layout manager回调)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifiinfo, parent, false);
        v.setOnClickListener(this);
        // 创建并放回一个ViewHolder对象
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // 在这方法中对item中的数据（View）进行修改(被layout manager回调)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.view.setTag(position + "");
        MyWifiScanResult result = wifiDatas.get(position);
        holder.mBssid.setText(result.getScanResult().SSID);
        String level = (result.getScanResult().level + 200) / 2 + "%";
        holder.mSignalStrenth.setText(level);
        if (!result.isNeedPassword()) {
            //不需要密码的信号
            holder.mWifiType.setVisibility(View.GONE);
        }
        if (result.isShare()) {
            //分享的信号（可以连接上）
            holder.mWifiType.setImageResource(R.drawable.img_wifi_type_unlock);
        }

    }

    // 返回数据（dataset）的大小 (被layout manager回调) @Override
    public int getItemCount() {
        return wifiDatas.size();
    }

    @Override
    public void onClick(View v) {
        if(onRecyclerViewItemClickListener!=null){
            //注意这里使用getTag方法获取数据
            onRecyclerViewItemClickListener.onRecycleViewItemClick(v,Integer.parseInt(v.getTag().toString()));
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onRecycleViewItemClick(View view , int position);
    }

    public OnRecyclerViewItemClickListener getOnRecyclerViewItemClickListener() {
        return onRecyclerViewItemClickListener;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }
}
