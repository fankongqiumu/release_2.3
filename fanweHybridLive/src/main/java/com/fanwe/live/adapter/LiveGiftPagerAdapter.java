package com.fanwe.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.adapter.SDPagerAdapter;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveGiftAdapter.LiveGiftAdapterListener;
import com.fanwe.live.model.LiveGiftModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveGiftPagerAdapter extends SDPagerAdapter<List<LiveGiftModel>>
{
    private Map<Integer, LiveGiftAdapter> mMapPositionAdapter = new HashMap<Integer, LiveGiftAdapter>();
    private LiveGiftAdapterListener listener;

    public void setListener(LiveGiftAdapterListener listener)
    {
        this.listener = listener;
    }

    public LiveGiftPagerAdapter(List<List<LiveGiftModel>> listModel, Activity activity)
    {
        super(listModel, activity);
    }

    public void clickAdapter(LiveGiftAdapter adapter)
    {
        for (LiveGiftAdapter value : mMapPositionAdapter.values())
        {
            if (!value.equals(adapter))
            {
                value.getSelectManager().clearSelected();
            }
        }
    }

    @Override
    public View getView(ViewGroup container, int position)
    {
        View view = getLayoutInflater().inflate(R.layout.item_pager_grid_linear, container, false);

        SDGridLinearLayout ll = (SDGridLinearLayout) view.findViewById(R.id.ll_content);
        ll.setColNumber(4);

        List<LiveGiftModel> listModel = getItemModel(position);
        LiveGiftAdapter adapter = new LiveGiftAdapter(listModel, mActivity);
        adapter.setListener(listener);
        ll.setAdapter(adapter);
        mMapPositionAdapter.put(position, adapter);

        return view;
    }

}
