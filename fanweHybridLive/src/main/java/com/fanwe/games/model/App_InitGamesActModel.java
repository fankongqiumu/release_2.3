package com.fanwe.games.model;

import com.fanwe.hybrid.model.BaseActModel;

import java.util.List;

/**
 * Created by shibx on 2016/11/23.
 */

public class App_InitGamesActModel extends BaseActModel{

    private String rs_count;//开放游戏数量
    private List<PluginModel> list;//游戏列表

    public String getRs_count() {
        return rs_count;
    }

    public void setRs_count(String rs_count) {
        this.rs_count = rs_count;
    }

    public List<PluginModel> getList() {
        return list;
    }

    public void setList(List<PluginModel> list) {
        this.list = list;
    }
}
