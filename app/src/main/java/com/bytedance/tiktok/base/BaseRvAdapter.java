package com.bytedance.tiktok.base;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * create on 2018/11/15
 * description RecyclerAdapter基类
 * tip多套布局T传Object类型，其他直接传具体类型
 */
public abstract class BaseRvAdapter<T,VH extends BaseRvViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context context;
    protected List<T> mDataList;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    public BaseRvAdapter(Context context, List<T> datas) {
        this.context = context;
        mDataList = datas;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        //item点击事件获取
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(v, position);
            });
        }
        onBindData(holder, mDataList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected abstract void onBindData(VH holder, T data, int position);

    public void addData(T data) {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    public void addDataToPosition(T data,int position) {
        mDataList.add(position,data);
        notifyItemInserted(position);
    }

    public void addDataList(List<T> datas) {
        int oldCount  = mDataList.size();
        mDataList.addAll(datas);
        notifyItemRangeInserted(oldCount, datas.size());
    }

    public void removeDataFromPosition(int position) {
        mDataList.remove(position);
        notifyDataSetChanged();
    }

    public void onlyRemoveItem(int position) {
        mDataList.remove(position);
    }

    public List<T> getDatas() {
        return mDataList;
    }

    public void clearDataList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
