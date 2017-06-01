package com.librarygames.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 * 下注金币实体列表类
 */

public class PokerBetAmountListModel {

    private List<PokerBetAmountModel> listBetAmount;

    public PokerBetAmountListModel(List<Integer> listBet) {
        listBetAmount = new ArrayList<>();
        if(listBet != null && !listBet.isEmpty()) {
            PokerBetAmountModel model;
            for (Integer integer : listBet) {
                model = new PokerBetAmountModel();
                model.setMoney(integer);
                listBetAmount.add(model);
            }
        }
    }

    public List<PokerBetAmountModel> getListBetAmount() {
        return listBetAmount;
    }
}
