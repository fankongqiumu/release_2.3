package com.librarygames.model;

import com.librarygames.utils.GamesResUtil;

/**
 * Created by shibx on 2016/11/22.
 */

public class PokerCardModel {
    private int number;
    private int type;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeResId() {
        return GamesResUtil.getPokerTypeResId(getType());
    }

    public int getNumberResId() {
        return GamesResUtil.getPokerNumResId(getNumber(),getType());
    }

    public int getCenterTypeResId() {
        return GamesResUtil.getPokerCenterResId(getNumber(), getType());
    }
}
