package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;

import com.librarygames.GameConstant;
import com.librarygames.R;

/**
 * Created by shibx on 2016/11/17.
 * 游戏面板--扎金花
 */

public class PokerGoldFlowerPanelView extends PokerBasePanelView {

    public PokerGoldFlowerPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PokerGoldFlowerPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerGoldFlowerPanelView(Context context, boolean isCreater, PokerPanelListener listener) {
        super(context);
        this.isCreater = isCreater;
        this.mListener = listener;
        mGameType = GameConstant.POKER_GOLDFLOWER;
        init();
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_goldflower_panel);
        initViews(this);
        register();
        setImageRes(R.drawable.ic_goldflower_starblue, R.drawable.ic_goldflower_starred, R.drawable.ic_goldflower_staryello);
    }
}
