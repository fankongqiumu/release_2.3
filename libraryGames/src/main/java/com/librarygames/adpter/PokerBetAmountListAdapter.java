package com.librarygames.adpter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import com.librarygames.model.PokerBetAmountModel;
import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 * 游戏面板底部下注区域 下注金币列表 适配器
 */

public class PokerBetAmountListAdapter extends SDSimpleAdapter<PokerBetAmountModel> {
    private long money;
    public PokerBetAmountListAdapter(List<PokerBetAmountModel> listModel, Activity activity, long account) {
        super(listModel, activity);
        this.money = account;
        getSelectManager().setMode(SDSelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
    }

    public void setMoney(long money) {
        this.money = money;
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return  R.layout.item_poker_bet_amount;
    }

    @Override
    public void bindData(final int position, final View convertView, ViewGroup parent, final PokerBetAmountModel model) {

        FrameLayout fl_gold = get(R.id.fl_gold,convertView);
//        final ImageView iv_gold_ring = get(R.id.iv_gold_ring,convertView);
//        ImageView iv_gold = get(R.id.iv_gold,convertView);
        TextView tv_gold = get(R.id.tv_gold,convertView);

        FrameLayout fl_gold2 = get(R.id.fl_gold2,convertView);
        ImageView iv_gold2 = get(R.id.iv_gold2,convertView);
        TextView tv_gold2 = get(R.id.tv_gold2,convertView);

        if (model.getMoney() <= money) {
            fl_gold.setEnabled(true);
            fl_gold2.setEnabled(true);
            iv_gold2.setImageResource(R.drawable.ic_btn_jb);
            SDViewUtil.setTextViewColorResId(tv_gold2,R.color.game_poker_panel_gold_color);

            if (model.isSelected()) {
                SDViewUtil.show(fl_gold);
            }else {
                SDViewUtil.hide(fl_gold);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(position,model,convertView);
                }
            });
        }else {
            fl_gold.setEnabled(false);
            fl_gold2.setEnabled(false);
            iv_gold2.setImageResource(R.drawable.bg_grey_circle);
            SDViewUtil.setTextViewColorResId(tv_gold2,R.color.text_content);
        }

        SDViewBinder.setTextView(tv_gold,model.getMoney() + "");
        SDViewBinder.setTextView(tv_gold2,model.getMoney() + "");
    }
}
