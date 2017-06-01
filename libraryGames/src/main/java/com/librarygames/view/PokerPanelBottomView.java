package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.view.SDAppView;
import com.librarygames.R;
import com.librarygames.adpter.PokerBetAmountListAdapter;
import com.librarygames.model.PokerBetAmountListModel;
import com.librarygames.model.PokerBetAmountModel;
import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 * 游戏面板底部下注区域 view
 */

public class PokerPanelBottomView extends SDAppView {
    private LinearLayout ll_recharge;
    private TextView tv_account;
    private TextView tv_recharge;
    protected ImageView iv_coin;
    private LinearLayout ll_layout_gold;
    private ImageView iv_history;
    private ImageView iv_autodeal;
    private PokerBetAmountListAdapter mAdapter;
    private List<PokerBetAmountModel> mListBet;
    private int selectBetGold;
    private long mMoney;
    private int minMoney;
    private PokerBottomViewListener pokerBottomViewListener;

    public PokerPanelBottomView(Context context) {
        super(context);
        init();
    }

    public PokerPanelBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerPanelBottomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.view_poker_bottom_view);
        initView();
    }

    private void initView() {
        ll_recharge = find(R.id.ll_recharge);
        tv_account = find(R.id.tv_account);
        tv_recharge = find(R.id.tv_recharge);
        ll_layout_gold = find(R.id.ll_layout_gold);
        iv_history = find(R.id.iv_history);
        iv_coin = find(R.id.iv_coin);
        iv_autodeal = find(R.id.iv_autodeal);
//        tv_recharge.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        ll_recharge.setOnClickListener(this);
        iv_history.setOnClickListener(this);
        iv_autodeal.setOnClickListener(this);
    }

    /**
     * 设置是否是主播
     * @param isCreater 是否主播
     * @deprecated
     * @see #enableDoBet(boolean)
     */
    public void setIsCreater(boolean isCreater) {
        if (isCreater)
            SDViewUtil.hide(ll_layout_gold);
        else
            SDViewUtil.show(ll_layout_gold);
    }

    public void enableDoBet(boolean enable) {
        if (enable) {
            SDViewUtil.hide(ll_layout_gold);
        } else
        {
            SDViewUtil.show(ll_layout_gold);
        }
    }

    public void isCreater(boolean isCreater) {
        if (isCreater) {
            SDViewUtil.show(iv_autodeal);
        } else
        {
            SDViewUtil.hide(iv_autodeal);
        }
    }

    /**
     * 是否开启自动发牌功能
     * @param open
     */
    public void openAutoFunction(boolean open) {
        if(! open)
        {
            SDViewUtil.hide(iv_autodeal);
            iv_autodeal.setOnClickListener(null);
        }
    }

    public void setAutoUi(boolean isAutoDeal) {
        if(isAutoDeal) {
            iv_autodeal.setImageResource(R.drawable.ic_auto_deal);
        } else {
            iv_autodeal.setImageResource(R.drawable.ic_manual_deal);
        }
    }

    /**
     * 初始化
     * @param list 下注金额列表
     * @param account 账户余额
     */
    public void setData(List<Integer> list, long account) {
        PokerBetAmountListModel model = new PokerBetAmountListModel(list);
        this.minMoney = list.get(0);
        this.mListBet = model.getListBetAmount();
        setAccount(account);

    }

    /**
     * 设置账户余额
     * @param account 账户余额
     */
    public void setAccount(long account) {
        this.mMoney = account;
        if (mAdapter == null)
            initAdapter();
        mAdapter.setMoney(account);
        getAllBetAmountView();
        if (mMoney < mAdapter.getSelectManager().getSelectedItem().getMoney())
            mAdapter.getSelectManager().performClick(0);
        SDViewBinder.setTextView(tv_account, String.valueOf(account));
    }

    private void initAdapter() {
        mAdapter = new PokerBetAmountListAdapter(mListBet, getActivity(), mMoney);
        mAdapter.getSelectManager().setSelected(0,true);//默认选中第一项
        mAdapter.setItemClickListener(new SDAdapter.ItemClickListener<PokerBetAmountModel>() {
            @Override
            public void onClick(int position, PokerBetAmountModel item, View view) {
                int itemMoney = item.getMoney();
                if (itemMoney <= mMoney)
                {
                    mAdapter.getSelectManager().performClick(position);
                }
            }
        });
    }

    private void getAllBetAmountView() {
        ll_layout_gold.removeAllViews();//清除所有子view
        for (int i = 0; i < mListBet.size(); i++) {
            View view = mAdapter.getView(i, null, ll_layout_gold);
            if (view != null) {
                ll_layout_gold.addView(view);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == iv_history) {
            pokerBottomViewListener.onClickHistory();
        }else if (v == ll_recharge) {
            pokerBottomViewListener.onClickRecharge();
        } else if(v == iv_autodeal) {
            pokerBottomViewListener.onClickAutoDeal();
        }
    }

    public int getSelectBetGold() {
        if (mMoney < minMoney) {
            selectBetGold = 0;
        }
        else
            selectBetGold =mAdapter.getSelectManager().getSelectedItem().getMoney();
        return selectBetGold;
    }

    public void setPokerBottomViewListener(PokerBottomViewListener pokerBottomViewListener) {
        this.pokerBottomViewListener = pokerBottomViewListener;
    }

    public interface PokerBottomViewListener {
        void onClickRecharge();
        void onClickHistory();
        void onClickAutoDeal();
    }
}
