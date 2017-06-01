package com.fanwe.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.live.R;

import java.util.List;

/**
 * Created by shibx on 2016/12/14.
 */

public class LivePluginToolsAdapter extends SDSimpleAdapter {

    public LivePluginToolsAdapter(List listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_tool_plugins;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent, Object model) {

    }
}
