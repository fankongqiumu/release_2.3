package com.fanwe.games.model;

import com.librarygames.model.PokerCardModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shibx on 2016/11/29.
 */

public class GamePokerModelModel {

    private int type;
    private List<int []> cards;
    private List<PokerCardModel> listCards;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<int[]> getCards() {
        return cards;
    }

    public void setCards(List<int[]> cards) {
        this.cards = cards;
        parseData(cards);
    }

    private void parseData(List<int[]> cards) {
        PokerCardModel pokerCardModel;
        listCards = new ArrayList<>();
        for (int[] card : cards) {
            pokerCardModel = new PokerCardModel();
            pokerCardModel.setType(card[0]);
            pokerCardModel.setNumber(card[1]);
            listCards.add(pokerCardModel);
        }
    }

    public List<PokerCardModel> getListCards() {
        return listCards;
    }
}
