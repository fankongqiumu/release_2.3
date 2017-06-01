package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import com.librarygames.model.PokerGoldFlowerModel;
import com.librarygames.utils.GamesResUtil;

/**
 * Created by shibx on 2016/12/12.
 *  斗牛 5张牌 view
 */

public class PokerBullView extends PokerBaseView {

    private PokerCardView pcv_poker_1;
    private PokerCardView pcv_poker_2;
    private PokerCardView pcv_poker_3;
    private PokerCardView pcv_poker_4;
    private PokerCardView pcv_poker_5;

    public PokerBullView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PokerBullView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PokerBullView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_bull);
        initView();
    }

    protected void initView() {
        pcv_poker_1 = find(R.id.pcv_poker_1);
        pcv_poker_2 = find(R.id.pcv_poker_2);
        pcv_poker_3 = find(R.id.pcv_poker_3);
        pcv_poker_4 = find(R.id.pcv_poker_4);
        pcv_poker_5 = find(R.id.pcv_poker_5);
    }

    @Override
    protected void setPokerBack(int gameType) {
        pcv_poker_1.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_2.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_3.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_4.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_5.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            int w = r - l;
            if(w > 0) {
                int margin = (w - SDViewUtil.getViewWidth(pcv_poker_1) - getPaddingLeft() * 2) / 4;
                SDViewUtil.setViewMarginLeft(pcv_poker_2, margin);
                SDViewUtil.setViewMarginLeft(pcv_poker_4, 3 * margin);
            }
        }
    }

    public void setPokerCards(PokerGoldFlowerModel pokerGoldFlowerModel) {
        pcv_poker_1.setPoker(pokerGoldFlowerModel.getListCards().get(0));
        pcv_poker_2.setPoker(pokerGoldFlowerModel.getListCards().get(1));
        pcv_poker_3.setPoker(pokerGoldFlowerModel.getListCards().get(2));
        pcv_poker_4.setPoker(pokerGoldFlowerModel.getListCards().get(3));
        pcv_poker_5.setPoker(pokerGoldFlowerModel.getListCards().get(4));
    }

    public View[] getPokerViews() {
        View[] views = new View[5];
        views[0] = pcv_poker_1;
        views[1] = pcv_poker_2;
        views[2] = pcv_poker_3;
        views[3] = pcv_poker_4;
        views[4] = pcv_poker_5;
        return views;
    }

    /**
     * 是否增加所有扑克--背面
     * @return
     */
    public boolean isAllAdded() {
        return pcv_poker_1.isPokerBackShown() && pcv_poker_2.isPokerBackShown() &&
                pcv_poker_3.isPokerBackShown() && pcv_poker_4.isPokerBackShown() && pcv_poker_5.isPokerBackShown();
    }

    /**
     * 是否展示所有扑克--正面
     * @return
     */
    public boolean isAllShown() {
        return !pcv_poker_1.isPokerBackShown() && !pcv_poker_2.isPokerBackShown() && !pcv_poker_3.isPokerBackShown()
        && !pcv_poker_4.isPokerBackShown() && !pcv_poker_5.isPokerBackShown();
    }

    /**
     * 一张一张增加扑克--背面
     */
    public void addPoker() {
        if(! pcv_poker_1.isPokerBackShown()) {
            pcv_poker_1.showPokerBack();
        } else if(!pcv_poker_2.isPokerBackShown()) {
            pcv_poker_2.showPokerBack();
        } else if(! pcv_poker_3.isPokerBackShown()) {
            pcv_poker_3.showPokerBack();
        } else if(! pcv_poker_4.isPokerBackShown()) {
            pcv_poker_4.showPokerBack();
        } else if(! pcv_poker_5.isPokerBackShown()) {
            pcv_poker_5.showPokerBack();
        } else {
            return;
        }
    }

    /**
     * 一次性增加扑克--背面
     */
    public void addPokerBack() {
        if(! pcv_poker_1.isPokerBackShown()) {
            pcv_poker_1.showPokerBack();
        }
        if(! pcv_poker_2.isPokerBackShown()) {
            pcv_poker_2.showPokerBack();
        }
        if(! pcv_poker_3.isPokerBackShown()) {
            pcv_poker_3.showPokerBack();
        }
        if(! pcv_poker_4.isPokerBackShown()) {
            pcv_poker_4.showPokerBack();
        }
        if(! pcv_poker_5.isPokerBackShown()) {
            pcv_poker_5.showPokerBack();
        }
    }

    public void showPokersInfo() {
        pcv_poker_1.showPokerInfo();
        pcv_poker_2.showPokerInfo();
        pcv_poker_3.showPokerInfo();
        pcv_poker_4.showPokerInfo();
        pcv_poker_5.showPokerInfo();
    }

    public void resetPoker() {
        pcv_poker_1.hide();
        pcv_poker_2.hide();
        pcv_poker_3.hide();
        pcv_poker_4.hide();
        pcv_poker_5.hide();
    }
}
