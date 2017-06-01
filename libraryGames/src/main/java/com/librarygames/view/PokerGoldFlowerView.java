package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.librarygames.R;
import com.librarygames.model.PokerGoldFlowerModel;
import com.librarygames.utils.GamesResUtil;

/**
 * Created by shibx on 2016/11/17.
 *  扎金花 3张牌 view
 */

public class PokerGoldFlowerView extends PokerBaseView {

    private PokerCardView pcv_poker_1;
    private PokerCardView pcv_poker_2;
    private PokerCardView pcv_poker_3;

    public PokerGoldFlowerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PokerGoldFlowerView(Context context, AttributeSet attrs) { 
        super(context, attrs);
        init();
    }

    public PokerGoldFlowerView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_goldflower);
        initView();
    }

    protected void initView() {
        pcv_poker_1 = find(R.id.pcv_poker_1);
        pcv_poker_2 = find(R.id.pcv_poker_2);
        pcv_poker_3 = find(R.id.pcv_poker_3);
    }

    @Override
    protected void setPokerBack(int gameType) {
        pcv_poker_1.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_2.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
        pcv_poker_3.iv_poker_back.setImageResource(GamesResUtil.getPokerBackResId(gameType));
    }

    public void setPokerCards(PokerGoldFlowerModel pokerGoldFlowerModel) {
        pcv_poker_1.setPoker(pokerGoldFlowerModel.getListCards().get(0));
        pcv_poker_2.setPoker(pokerGoldFlowerModel.getListCards().get(1));
        pcv_poker_3.setPoker(pokerGoldFlowerModel.getListCards().get(2));

    }

    public View[] getPokerViews() {
        View[] views = new View[3];
        views[0] = pcv_poker_1;
        views[1] = pcv_poker_2;
        views[2] = pcv_poker_3;
        return views;
    }

    /**
     * 是否增加所有扑克--背面
     * @return
     */
    public boolean isAllAdded() {
        return pcv_poker_1.isPokerBackShown() && pcv_poker_2.isPokerBackShown() && pcv_poker_3.isPokerBackShown();
    }

    /**
     * 是否展示所有扑克--正面
     * @return
     */
    public boolean isAllShown() {
        return !pcv_poker_1.isPokerBackShown() && !pcv_poker_2.isPokerBackShown() && !pcv_poker_3.isPokerBackShown();
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
    }

    public void showPokersInfo() {
        pcv_poker_1.showPokerInfo();
        pcv_poker_2.showPokerInfo();
        pcv_poker_3.showPokerInfo();
    }

    public void resetPoker() {
        pcv_poker_1.hide();
        pcv_poker_2.hide();
        pcv_poker_3.hide();
    }
}
