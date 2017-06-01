package com.fanwe.library.adapter.viewholder;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.adapter.SDAdapter;

public abstract class SDViewHolder<T>
{

    private SDAdapter adapter;
    private View itemView;
    private SparseArray<View> arrayView;

    public void setItemView(View itemView)
    {
        if (this.itemView == null)
        {
            this.itemView = itemView;
        }
    }

    public View getItemView()
    {
        return itemView;
    }

    public Activity getActivity()
    {
        if (adapter != null)
        {
            return adapter.getActivity();
        } else
        {
            return null;
        }
    }

    public void setAdapter(SDAdapter adapter)
    {
        if (this.adapter != adapter)
        {
            this.adapter = adapter;
        }
    }

    public SDAdapter getAdapter()
    {
        return adapter;
    }

    public <V extends View> V find(int id)
    {
        return (V) itemView.findViewById(id);
    }

    /**
     * 在itemView中通过id查找view，如果需要频繁的通过id查找view，调用此方法查找效率较高
     *
     * @param id
     * @param <V>
     * @return
     */
    public <V extends View> V get(int id)
    {
        if (arrayView == null)
        {
            arrayView = new SparseArray<>();
        }
        View view = arrayView.get(id);
        if (view == null)
        {
            view = itemView.findViewById(id);
            if (view != null)
            {
                arrayView.put(id, view);
            }
        }
        return (V) view;
    }

    public abstract int getLayoutId(int position, View convertView, ViewGroup parent);

    public abstract void findViews(int position, View convertView, ViewGroup parent);

    public void onUpdateView(int position, View convertView, ViewGroup parent, T model)
    {
        onBindData(position, convertView, parent, model);
    }

    public abstract void onBindData(int position, View convertView, ViewGroup parent, T model);

}
