package com.fanwe.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.model.LiveGiftModel;
import com.fanwe.live.utils.GlideUtil;

import java.util.List;

public class LiveGiftAdapter extends SDSimpleAdapter<LiveGiftModel>
{
    private LiveGiftAdapterListener listener;

    public void setListener(LiveGiftAdapterListener listener)
    {
        this.listener = listener;
    }

    public LiveGiftAdapter(List<LiveGiftModel> listModel, Activity activity)
    {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent)
    {
        return R.layout.item_live_gift;
    }

    @Override
    public void bindData(final int position, View convertView, ViewGroup parent, final LiveGiftModel model)
    {
        ImageView iv_gift = get(R.id.iv_gift, convertView);
        ImageView iv_selected = get(R.id.iv_selected, convertView);
        ImageView iv_much = get(R.id.iv_much, convertView);
        TextView tv_price = get(R.id.tv_price, convertView);
        TextView tv_score = get(R.id.tv_score, convertView);

        if (model.isSelected())
        {
            SDViewUtil.show(iv_selected);
            SDViewUtil.hide(iv_much);
        } else
        {
            SDViewUtil.hide(iv_selected);
            if (model.getIs_much() == 1)
            {
                SDViewUtil.show(iv_much);
            } else
            {
                SDViewUtil.hide(iv_much);
            }
        }

        SDViewBinder.setTextView(tv_price, String.valueOf(model.getDiamonds()));
        SDViewBinder.setTextView(tv_score, model.getScore_fromat());

        GlideUtil.load(model.getIcon()).into(iv_gift);

        convertView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onClickItem(position, model, LiveGiftAdapter.this);
                }
            }
        });
    }

    public interface LiveGiftAdapterListener
    {
        void onClickItem(int position, LiveGiftModel model, LiveGiftAdapter adapter);
    }

}
