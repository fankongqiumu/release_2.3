package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.view.SDAppView;
import com.librarygames.R;

/**
 * 下注区
 * Created by Administrator on 2016/11/22.
 */

public class PokerBetAreaView extends SDAppView {

    protected ImageView iv_star;//下注区图片
    protected TextView tv_mul;//下注区倍数
    protected TextView tv_total_bet;//下注区总的金币数
    protected TextView tv_bet;//下注区自己的下注金币数

    private int betAll;
    private int betUser;
    private int index;
    private int position;

    public PokerBetAreaView(Context context) {
        super(context);
        init();
    }

    public PokerBetAreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerBetAreaView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_bet_area);
        initView();
    }

    private void initView()
    {
        iv_star = find(R.id.iv_star);
        tv_mul = find(R.id.tv_mul);
        tv_total_bet = find(R.id.tv_total_bet);
        tv_bet = find(R.id.tv_bet);
    }

    public void resetBets() {
        this.betAll = 0;
        this.betUser = 0;
        setBetGold(0,0);
    }

    public void setBetGold(int totalBetGold)
    {
        setBetGold(totalBetGold,0);
    }

    public void setBetGold(int totalBetGold,int betGold)
    {
        if(totalBetGold >= betAll) {
            this.betAll = totalBetGold;
            SDViewBinder.setTextView(tv_total_bet,String.valueOf(totalBetGold));
        }
        if(betGold >= betUser) {
            this.betUser = betGold;
            SDViewBinder.setTextView(tv_bet,String.valueOf(betGold));
        }
    }

    public View getImgView()
    {
        return iv_star;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        setIndex(position - 1);
    }

    public void hideUserBet(boolean isCreater) {
        if (isCreater) {
            SDViewUtil.hide(tv_bet);
        } else
            SDViewUtil.show(tv_bet);
    }

    public int getIndex() {
        return index;
    }

    private void setIndex(int index) {
        this.index = index;
    }
}
