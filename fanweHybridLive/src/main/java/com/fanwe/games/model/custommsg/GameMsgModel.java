package com.fanwe.games.model.custommsg;

import com.fanwe.games.model.GameBankerModel;

/**
 * Created by shibx on 2016/11/25.
 * 游戏开始推送消息
 */

public class GameMsgModel extends GameBaseModel {

    private GameGoldFlowerModel game_data;

    private int banker_status;

    private GameBankerModel banker;

    private long principal;

    public GameGoldFlowerModel getGame_data() {
        return game_data;
    }

    public void setGame_data(GameGoldFlowerModel game_data) {
        this.game_data = game_data;
    }

    public int getBanker_status() {
        return banker_status;
    }

    public void setBanker_status(int banker_status) {
        this.banker_status = banker_status;
    }

    public GameBankerModel getBanker() {
        return banker;
    }

    public void setBanker(GameBankerModel banker) {
        this.banker = banker;
    }

    public long getPrincipal() {
        return principal;
    }

    public void setPrincipal(long principal) {
        this.principal = principal;
    }
}
