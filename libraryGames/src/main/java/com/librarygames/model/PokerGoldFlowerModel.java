package com.librarygames.model;

import java.util.List;

/**
 * Created by shibx on 2016/11/30.
 */

public class PokerGoldFlowerModel {
    
    private List<PokerCardModel> listCards;
    private int type;

    public List<PokerCardModel> getListCards() {
        return listCards;
    }

    public void setListCards(List<PokerCardModel> listCards) {
        this.listCards = listCards;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
