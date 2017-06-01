package com.fanwe.games.model;

import com.fanwe.games.model.GameBetModel;
import com.fanwe.hybrid.model.BaseActModel;

/**
 * Created by shibx on 2016/11/25.
 */

public class App_betGamesActModel extends BaseActModel {

    private GameBetModel data;

    public GameBetModel getData() {
        return data;
    }

    public void setData(GameBetModel data) {
        this.data = data;
    }
}
