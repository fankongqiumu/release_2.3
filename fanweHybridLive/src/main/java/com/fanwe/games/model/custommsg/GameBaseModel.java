package com.fanwe.games.model.custommsg;

import com.fanwe.live.model.custommsg.CustomMsg;

import java.util.List;

/**
 * Created by shibx on 2016/11/30.
 * 游戏相关数据对象，推送数据与接口数据共用
 */

public class GameBaseModel extends CustomMsg {

    protected int room_id;//房间号
    protected String desc;//描述
    protected long time;//剩余时长
    protected int game_id;//游戏id
    protected int game_log_id;//游戏轮数
    protected int game_status;//游戏状态，1：游戏开始，2：游戏结束
    protected int game_action;//游戏操作，不同游戏略有不同：开始：1；下注：2；停止：3；结算：4；翻牌：5；
    protected List<Integer> bet_option;//投注金额选项
    protected List<String> option;

    //add
    protected String user_id; //接收消息的指定用户，为0时所有用户均可处理

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getGame_status() {
        return game_status;
    }

    public void setGame_status(int game_status) {
        this.game_status = game_status;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_log_id() {
        return game_log_id;
    }

    public void setGame_log_id(int game_log_id) {
        this.game_log_id = game_log_id;
    }

    public int getGame_action() {
        return game_action;
    }

    public void setGame_action(int game_action) {
        this.game_action = game_action;
    }

    public List<Integer> getBet_option() {
        return bet_option;
    }

    public void setBet_option(List<Integer> bet_option) {
        this.bet_option = bet_option;
    }

    public List<String> getOption() {
        return option;
    }

    public void setOption(List<String> option) {
        this.option = option;
    }
}
