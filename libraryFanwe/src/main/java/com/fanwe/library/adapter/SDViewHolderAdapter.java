package com.fanwe.library.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.adapter.viewholder.SDViewHolder;

import java.util.List;

public abstract class SDViewHolderAdapter<T> extends SDAdapter<T>
{

    public SDViewHolderAdapter(List<T> listModel, Activity activity)
    {
        super(listModel, activity);
    }

    @Override
    protected final void onUpdateView(int position, View convertView, ViewGroup parent, T model)
    {
        SDViewHolder<T> holder = (SDViewHolder<T>) convertView.getTag();
        holder.onUpdateView(position, convertView, parent, model);

        onUpdateView(position, convertView, parent, model, holder);
    }

    /**
     * 当调用item刷新的时候会触发此方法
     *
     * @param position
     * @param convertView
     * @param parent
     * @param model
     * @param holder
     */
    protected void onUpdateView(int position, View convertView, ViewGroup parent, T model, SDViewHolder<T> holder)
    {
        onBindData(position, convertView, parent, model, holder);
    }

    @Override
    public final View onGetView(int position, View convertView, ViewGroup parent)
    {
        SDViewHolder<T> holder = null;
        if (convertView == null)
        {
            holder = onCreateViewHolder(position, convertView, parent);
            holder.setAdapter(this);

            int layoutId = holder.getLayoutId(position, convertView, parent);
            convertView = getLayoutInflater().inflate(layoutId, null);
            holder.setItemView(convertView);

            holder.findViews(position, convertView, parent);
            convertView.setTag(holder);
        } else
        {
            holder = (SDViewHolder<T>) convertView.getTag();
        }
        holder.onBindData(position, convertView, parent, getItem(position));

        onBindData(position, convertView, parent, getItem(position), holder);
        return convertView;
    }

    public abstract SDViewHolder<T> onCreateViewHolder(int position, View convertView, ViewGroup parent);

    public abstract void onBindData(int position, View convertView, ViewGroup parent, T model, SDViewHolder<T> holder);

}
