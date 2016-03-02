package com.wiky.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 4399_wuhui on 2016/3/2.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private String[] mDataset;

    //自定义的ViewHolder,持有Item中的所有View
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 当前item布局中仅有的一个TextView
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.my_text_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // 创建一个新的 views (被layout manager回调)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // 关联item布局，item.xml见步骤5
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        // 创建并放回一个ViewHolder对象
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // 在这方法中对item中的数据（View）进行修改(被layout manager回调)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 对holder（即对应item）的TextView进行修改
        holder.mTextView.setText(mDataset[position]);

    }

    // 返回数据（dataset）的大小 (被layout manager回调) @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
