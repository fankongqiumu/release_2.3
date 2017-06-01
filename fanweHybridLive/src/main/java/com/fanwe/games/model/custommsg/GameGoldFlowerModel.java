package com.fanwe.games.model.custommsg;

import com.fanwe.games.model.GamePokerModelModel;
import com.librarygames.model.PokerGoldFlowerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shibx on 2016/12/7.
 * 扑克游戏信息内容
 */

public class GameGoldFlowerModel {

    private int win;
    private List<Integer> bet;
    private List<Integer> user_bet;
    private List<GamePokerModelModel> cards_data;
    private List<PokerGoldFlowerModel> listPokers;

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public List<Integer> getBet() {
        return bet;
    }

    public void setBet(List<Integer> bet) {
        this.bet = bet;
    }

    public List<Integer> getUser_bet() {
        return user_bet;
    }

    public void setUser_bet(List<Integer> user_bet) {
        this.user_bet = user_bet;
    }

    public List<GamePokerModelModel> getCards_data() {
        return cards_data;
    }

    public void setCards_data(List<GamePokerModelModel> cards_data) {
        this.cards_data = cards_data;
        parseData(cards_data);
    }

    private void parseData(List<GamePokerModelModel> cards_data) {
        PokerGoldFlowerModel pokerGoldFlowerModel;
        listPokers = new ArrayList<>();
        for (GamePokerModelModel gamePokerModelModel : cards_data) {
            pokerGoldFlowerModel = new PokerGoldFlowerModel();
            pokerGoldFlowerModel.setType(gamePokerModelModel.getType());
            pokerGoldFlowerModel.setListCards(gamePokerModelModel.getListCards());
            listPokers.add(pokerGoldFlowerModel);
        }
    }

    public List<PokerGoldFlowerModel> getListPokers() {
        return listPokers;
    }
}
