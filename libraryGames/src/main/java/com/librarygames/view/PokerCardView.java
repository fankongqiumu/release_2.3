package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import com.librarygames.model.PokerCardModel;

/**
 * Created by shibx on 2016/11/17.
 *  单张扑克牌view
 */

public class PokerCardView extends BaseGamesView {

    private RelativeLayout rl_poker_info;

    protected ImageView iv_poker_back;

    private ImageView iv_poker_num;

    private ImageView iv_poker_type;

    private ImageView iv_poker_icon;

    public PokerCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PokerCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerCardView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_card);
        initView();
    }

    private void initView() {
        rl_poker_info = find(R.id.rl_poker_info);
        iv_poker_back = find(R.id.iv_poker_back);
        iv_poker_num = find(R.id.iv_poker_num);
        iv_poker_type = find(R.id.iv_poker_type);
        iv_poker_icon = find(R.id.iv_poker_icon);
    }

    /**
     * 牌背是否显示
     * @return
     */
    public boolean isPokerBackShown() {
        return iv_poker_back.getVisibility() == View.VISIBLE;
    }

    public void showPokerBack() {
        //隐藏牌面信息，展示牌背
        SDViewUtil.show(iv_poker_back);
        SDViewUtil.invisible(rl_poker_info);
    }

    public void setPoker(PokerCardModel model) {
        iv_poker_num.setImageResource(model.getNumberResId());
        iv_poker_icon.setImageResource(model.getCenterTypeResId());
        iv_poker_type.setImageResource(model.getTypeResId());
    }

    public void showPokerInfo() {
        SDViewUtil.invisible(iv_poker_back);
        SDViewUtil.show(rl_poker_info);
    }

    public void hide() {
        SDViewUtil.invisible(iv_poker_back);
        SDViewUtil.invisible(rl_poker_info);
    }
}
