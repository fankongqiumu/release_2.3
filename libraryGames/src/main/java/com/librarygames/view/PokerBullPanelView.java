package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;

import com.librarygames.GameConstant;
import com.librarygames.R;

/**
 * Created by shibx on 2016/12/12.
 * 游戏面板--斗牛
 */

public class PokerBullPanelView extends PokerBasePanelView {

    public PokerBullPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PokerBullPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerBullPanelView(Context context, boolean isCreater, PokerPanelListener listener) {
        super(context);
        this.isCreater = isCreater;
        this.mListener = listener;
        mGameType = GameConstant.POKER_BULL;
        init();
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_bull_panel);
        initViews(this);
        register();
        setImageRes(R.drawable.ic_bull_starblue, R.drawable.ic_bull_starred, R.drawable.ic_bull_staryello);
    }
}
