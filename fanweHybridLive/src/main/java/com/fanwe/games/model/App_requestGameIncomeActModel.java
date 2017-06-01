package com.fanwe.games.model;

import com.fanwe.hybrid.model.BaseActModel;

/**
 * Created by shibx on 2016/12/5.
 */

public class App_requestGameIncomeActModel extends BaseActModel {

    private long user_diamonds;//玩家钻石
    private long coin;//玩家游戏币
    private int gain;//玩家本轮游戏获取的货币

    public long getUser_diamonds() {
        return user_diamonds;
    }

    public void setUser_diamonds(long user_diamonds) {
        this.user_diamonds = user_diamonds;
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }
}
