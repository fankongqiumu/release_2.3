package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.librarygames.model.PokerGoldFlowerModel;

/**
 * Created by shibx on 2016/12/12.
 *  扎金花、斗牛 牌型 view 基类
 */

public abstract class PokerBaseView extends BaseGamesView {

    public PokerBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PokerBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerBaseView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
    }

    protected abstract void initView();

    protected abstract void setPokerBack(int gameType);

    public abstract void setPokerCards(PokerGoldFlowerModel pokerGoldFlowerModel);

    public abstract View[] getPokerViews();

    public abstract boolean isAllAdded();

    public abstract boolean isAllShown();

    public abstract void addPoker();

    public abstract void addPokerBack();

    public abstract void showPokersInfo();

    public abstract void resetPoker();
}
