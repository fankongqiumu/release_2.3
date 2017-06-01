package com.librarygames.model;

import com.fanwe.library.common.SDSelectManager;

/**
 * Created by shibx on 2016/12/20.
 * 下注选中金币实体类
 */

public class PokerBetAmountModel implements SDSelectManager.SDSelectable{

    private int money;
    private boolean selected;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
