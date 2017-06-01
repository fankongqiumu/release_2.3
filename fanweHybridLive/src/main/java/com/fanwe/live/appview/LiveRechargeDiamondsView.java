package com.fanwe.live.appview;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveRechargePayDialogAdapter;
import com.fanwe.live.adapter.LiveRechargeRuleAdapter;
import com.fanwe.live.model.App_rechargeActModel;
import com.fanwe.live.model.PayItemModel;
import com.fanwe.live.model.RuleItemModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shibx on 2017/3/6.
 * 直播间充值窗口{@link com.fanwe.live.dialog.LiveRechargeDialog} 充值视图
 */

public class LiveRechargeDiamondsView extends BaseAppView implements TextWatcher, LiveRechargeRuleAdapter.OnRuleViewClickListener {

//    private LinearLayout ll_payment;
    private ScrollView scroll_pay_rule;

    private SDGridLinearLayout lv_payRule;

    private SDGridLinearLayout lv_payment;

    private TextView tv_account;

    private LinearLayout ll_rate;

    private EditText et_money;

    private TextView tv_num;

    private TextView tv_recharge;

    private LiveRechargeRuleAdapter mAdapterRule;
    private LiveRechargePayDialogAdapter mAdapterPayment;

    private App_rechargeActModel mActModel;

    private OnChosePayRuleListener mListener;

    public LiveRechargeDiamondsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LiveRechargeDiamondsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveRechargeDiamondsView(Context context) {
        super(context);
        init();
    }

    public LiveRechargeDiamondsView(Context context, App_rechargeActModel actmodel) {
        super(context);
        this.mActModel = actmodel;
        init();
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.view_recharge_diamonds;
    }

    @Override
    protected void init() {
        super.init();
//        ll_payment = find(R.id.ll_payment);//商品列表模块
        scroll_pay_rule = find(R.id.scroll_pay_rule);
        ll_rate  =find(R.id.ll_rate);//其他金额输入模块
        lv_payRule = find(R.id.lv_pay_rule);
        lv_payment = find(R.id.lv_payment);

        et_money = find(R.id.et_money);
        tv_num = find(R.id.tv_num);
        tv_account = find(R.id.tv_account);
        tv_recharge = find(R.id.tv_recharge);
        et_money.addTextChangedListener(this);
        tv_recharge.setOnClickListener(this);
        initGridView();
        initData();
    }

    private void initGridView() {
        lv_payRule.setColNumber(2);
        lv_payment.setColNumber(3);
        mAdapterRule = new LiveRechargeRuleAdapter(null, getActivity());
        mAdapterRule.setOnRuleViewClickListener(this);
        mAdapterPayment = new LiveRechargePayDialogAdapter(null, getActivity());
        mAdapterPayment.getSelectManager().setMode(SDSelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
        mAdapterPayment.setSelectStateListener(new SDSelectManager.SelecteStateListener<PayItemModel>() {

            @Override
            public void onNormal(int position, PayItemModel item) {
                mAdapterRule.updateData(new ArrayList<RuleItemModel>());
            }

            @Override
            public void onSelected(int position, PayItemModel item) {
                mAdapterRule.updateData(getRuleList(item));
            }
        });
        mAdapterPayment.setOnItemClickListener(new SDAdapter.ItemClickListener<PayItemModel>() {
            @Override
            public void onClick(int position, PayItemModel item, View view) {
                mAdapterPayment.getSelectManager().performClick(item);
            }
        });

        lv_payRule.setAdapter(mAdapterRule);
        lv_payment.setAdapter(mAdapterPayment);
    }

    private void initData() {
        SDViewBinder.setTextView(tv_account, "钻石余额：" + mActModel.getDiamonds());
        List<PayItemModel> listPayment = mActModel.getPay_list();
        if (listPayment != null && listPayment.size() > 0) {
            mAdapterPayment.updateData(listPayment);
        }
        mAdapterPayment.getSelectManager().performClick(0);
    }

    private List<RuleItemModel> getRuleList(PayItemModel model) {
        List<RuleItemModel> list = model.getRule_list();
        if(list == null || list.isEmpty())
            list = mActModel.getRule_list();
        if(mActModel.getShow_other() == 1) {
            RuleItemModel lastRuleModel = list.get(list.size()-1);
            if(!lastRuleModel.is_other_money()) {
                RuleItemModel ruleModel = new RuleItemModel();
                ruleModel.setIs_other_money(true);
                list.add(ruleModel);
            }
        }
        return list;
    }

    public void updateData(App_rechargeActModel actmodel) {
        this.mActModel = actmodel;
        initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_recharge:
                int money = Integer.parseInt(et_money.getText().toString());
                //点击直接发起支付
                PayItemModel paymentModel = mAdapterPayment.getSelectManager().getSelectedItem();
                if(paymentModel == null) {
                    SDToast.showToast("请选择支付方式");
                    return;
                }
                int payId = paymentModel.getId();
                mListener.onSubmitOther(payId, money);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if(!TextUtils.isEmpty(str)) {
            if (Long.parseLong(str) == 0) {
                if (str.length() > 1) {
                    et_money.setText(String.valueOf(0));
                    et_money.setSelection(et_money.getText().toString().length());
                }
                SDViewUtil.hide(tv_recharge);
            } else
                SDViewUtil.show(tv_recharge);
            int diamonds = Integer.parseInt(et_money.getText().toString()) * mActModel.getRate();
            tv_num.setText(String.valueOf(diamonds));
        } else {
            tv_num.setText("0");
            SDViewUtil.hide(tv_recharge);
        }

    }

    @Override
    public void onClickOtherView() {
        //切换至其他金额兑换
        SDViewUtil.show(ll_rate);
        SDViewUtil.hide(scroll_pay_rule);
    }

    public boolean isShownOtherView() {
        return ll_rate.isShown();
    }

    public void showRules() {
        //切换至商品列表
        SDViewUtil.show(scroll_pay_rule);
        SDViewUtil.hide(ll_rate);
    }

    @Override
    public void onClickRuleView(RuleItemModel model) {
        //点击直接发起支付
        PayItemModel paymentModel = mAdapterPayment.getSelectManager().getSelectedItem();
        if(paymentModel == null) {
            SDToast.showToast("请选择支付方式");
            return;
        }
        int payId = paymentModel.getId();
        int ruleId = model.getId();
        mListener.onChosePayRule(payId, ruleId);
    }

    public void setOnChosePayRuleListener(OnChosePayRuleListener listener) {
        this.mListener = listener;
    }

    public interface OnChosePayRuleListener {
        /**
         * 选择下发的商品列表
         * @param payId 支付方式
         * @param ruleId 商品id
         */
        void onChosePayRule(int payId, int ruleId);

        /**
         * 其他金额兑换
         * @param money 输入金额
         */
        void onSubmitOther(int payId, int money);
    }
}
