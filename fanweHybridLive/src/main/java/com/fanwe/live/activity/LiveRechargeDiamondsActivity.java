package com.fanwe.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.constant.Constant;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.hybrid.model.PaySdkModel;
import com.fanwe.hybrid.model.YJWAPPayModel;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.library.dialog.SDDialogProgress;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveRechargePaymentAdapter;
import com.fanwe.live.adapter.LiveRechrgeDiamondsPaymentRuleAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_payActModel;
import com.fanwe.live.model.App_rechargeActModel;
import com.fanwe.live.model.PayItemModel;
import com.fanwe.live.model.PayModel;
import com.fanwe.live.model.RuleItemModel;
import com.fanwe.live.view.SDProgressPullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值界面
 * Created by Administrator on 2016/7/6.
 */
public class LiveRechargeDiamondsActivity extends BaseTitleActivity
{

    @ViewInject(R.id.ptrsv_content)
    private SDProgressPullToRefreshScrollView ptrsv_content;

    @ViewInject(R.id.tv_user_money)
    private TextView tv_user_money;

    @ViewInject(R.id.lv_payment)
    private SDGridLinearLayout lv_payment;

    @ViewInject(R.id.lv_payment_rule)
    private SDGridLinearLayout lv_payment_rule;

    @ViewInject(R.id.et_money)
    private EditText et_money;

    @ViewInject(R.id.tv_money_to_diamonds)
    private TextView tv_money_to_diamonds;

    @ViewInject(R.id.tv_exchange)
    private TextView tv_exchange;

    @ViewInject(R.id.ll_payment)
    private View ll_payment;
    @ViewInject(R.id.ll_payment_rule)
    private View ll_payment_rule;
    @ViewInject(R.id.ll_other_ticket_exchange)
    private View ll_other_ticket_exchange;

    private List<RuleItemModel> mListCommonPaymentRule;

    private LiveRechargePaymentAdapter adapterPayment;
    private List<PayItemModel> listPayment = new ArrayList<>();

    private LiveRechrgeDiamondsPaymentRuleAdapter adapterPaymentRule;
    private List<RuleItemModel> listPaymentRule = new ArrayList<>();


    private int paymentId;
    private int paymentRuleId;
    private int exchangeMoney;

    /**
     * 充值钻石页面
     * 兑换比例
     */
    private int exchangeRate = 1;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_recharge_diamonds;
    }

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        mTitle.setMiddleTextTop("充值");

        //支付方式
        adapterPayment = new LiveRechargePaymentAdapter(listPayment, this);
        adapterPayment.getSelectManager().setMode(SDSelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
        adapterPayment.setSelectStateListener(new SDSelectManager.SelecteStateListener<PayItemModel>() {
            @Override
            public void onNormal(int index, PayItemModel item) {
                adapterPaymentRule.updateData(new ArrayList<RuleItemModel>());
            }

            @Override
            public void onSelected(int index, PayItemModel item) {
                List<RuleItemModel> list = item.getRule_list();
                if(list != null && !list.isEmpty())
                    adapterPaymentRule.updateData(item.getRule_list());
                else
                    adapterPaymentRule.updateData(mListCommonPaymentRule);
            }
        });
        adapterPayment.setClickPaymentListener(new SDAdapter.ItemClickListener<PayItemModel>()
        {
            @Override
            public void onClick(int position, PayItemModel item, View view)
            {
                adapterPayment.getSelectManager().performClick(item);

            }
        });
        lv_payment.setAdapter(adapterPayment);

        //支付金额
        adapterPaymentRule = new LiveRechrgeDiamondsPaymentRuleAdapter(listPaymentRule, this);
        adapterPaymentRule.setClickPaymentRuleListener(new SDAdapter.ItemClickListener<RuleItemModel>()
        {
            @Override
            public void onClick(int position, RuleItemModel item, View view)
            {
                paymentRuleId = item.getId();
                clickPaymentRule(item);
            }
        });
        lv_payment_rule.setAdapter(adapterPaymentRule);


        //其他金额
        et_money.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                exchangeMoney = SDTypeParseUtil.getInt(s.toString());

                int diamonds = exchangeMoney * exchangeRate;
                String strDiamonds = new BigDecimal(diamonds).toPlainString();
                tv_money_to_diamonds.setText(strDiamonds);
            }
        });

        //兑换
        tv_exchange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickExchange();
            }
        });

        initPullToRefresh();
    }

    /**
     * 指定金额支付
     *
     * @param model
     */
    private void clickPaymentRule(RuleItemModel model)
    {
        if (!validatePayment())
        {
            return;
        }

        exchangeMoney = 0;
        requestPay();
    }

    /**
     * 输入金额支付
     */
    private void clickExchange()
    {
        paymentRuleId = 0;
        if (!validatePayment())
        {
            return;
        }

        if (exchangeMoney <= 0)
        {
            SDToast.showToast("请输入兑换金额");
            return;
        }

        requestPay();
    }

    private void requestPay()
    {
        CommonInterface.requestPay(paymentId, paymentRuleId, exchangeMoney, new AppRequestCallback<App_payActModel>()
        {
            private SDDialogProgress dialog = new SDDialogProgress();

            @Override
            protected void onStart()
            {
                super.onStart();
                dialog.setTextMsg("正在启动插件");
                dialog.show();
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dialog.dismiss();
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    PayModel payModel = actModel.getPay();
                    if (payModel != null)
                    {
                        PaySdkModel paySdkModel = payModel.getSdk_code();
                        if (paySdkModel != null)
                        {
                            String payCode = paySdkModel.getPay_sdk_type();
                            if (!TextUtils.isEmpty(payCode))
                            {
                                if (Constant.PaymentType.UPAPP.equalsIgnoreCase(payCode))
                                {
                                    CommonOpenSDK.payUpApp(paySdkModel, getActivity(), payResultListner);
                                } else if (Constant.PaymentType.BAOFOO.equalsIgnoreCase(payCode))
                                {
                                    CommonOpenSDK.payBaofoo(paySdkModel, getActivity(), 1, payResultListner);
                                } else if (Constant.PaymentType.ALIPAY.equalsIgnoreCase(payCode))
                                {
                                    CommonOpenSDK.payAlipay(paySdkModel, getActivity(), payResultListner);
                                } else if (Constant.PaymentType.WXPAY.equalsIgnoreCase(payCode))
                                {
                                    CommonOpenSDK.payWxPay(paySdkModel, getActivity(), payResultListner);
                                } else  if(Constant.PaymentType.WFTWXPAY.equalsIgnoreCase(payCode)) {
                                    CommonOpenSDK.WFTWxAppPay(paySdkModel.getWFTWxAppPayModel(), getActivity());
                                } else if(Constant.PaymentType.YJWAP.equalsIgnoreCase(payCode)) {
                                    openPayWap(paySdkModel.getYJWAPPayModel());
                                }
                            } else
                            {
                                SDToast.showToast("参数错误:payCode为空");
                            }
                        } else
                        {
                            SDToast.showToast("参数错误:sdk_code为空");
                        }
                    } else
                    {
                        SDToast.showToast("参数错误:pay为空");
                    }

                }
            }
        });
    }

    private void openPayWap(@NonNull YJWAPPayModel model) {
        String url = model.getUrl();
        if(TextUtils.isEmpty(url)) {
            SDToast.showToast("获取参数错误:url为空");
            return;
        }
        Intent intent = new Intent(this, LivePayWebViewActivity.class);
        intent.putExtra(LivePayWebViewActivity.EXTRA_URL, url);
        startActivity(intent);
    }

    private PayResultListner payResultListner = new PayResultListner()
    {
        @Override
        public void onSuccess()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    et_money.setText("");
                }
            });
        }

        @Override
        public void onDealing()
        {

        }

        @Override
        public void onFail()
        {

        }

        @Override
        public void onCancel()
        {

        }

        @Override
        public void onNetWork()
        {

        }

        @Override
        public void onOther()
        {

        }
    };

    private boolean validatePayment()
    {
        PayItemModel payment = adapterPayment.getSelectManager().getSelectedItem();
        if (payment == null)
        {
            SDToast.showToast("请选择支付方式");
            return false;
        }
        paymentId = payment.getId();

        return true;
    }

    private void initPullToRefresh()
    {
        ptrsv_content.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptrsv_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView)
            {

            }
        });
    }

    private void requestData()
    {
        CommonInterface.requestRecharge(new AppRequestCallback<App_rechargeActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    mListCommonPaymentRule = actModel.getRule_list();
                    exchangeRate = actModel.getRate();
                    if (actModel.getShow_other() == 1)
                    {
                        SDViewUtil.show(ll_other_ticket_exchange);
                    } else
                    {
                        SDViewUtil.hide(ll_other_ticket_exchange);
                    }

                    SDViewUtil.show(ll_payment);
                    SDViewUtil.show(ll_payment_rule);
                    SDViewBinder.setTextView(tv_user_money, String.valueOf(actModel.getDiamonds()));
                    adapterPayment.updateData(actModel.getPay_list());
//                    adapterPaymentRule.updateData(actModel.getRule_list());

                    int defaultPayIndex = -1;
                    List<PayItemModel> listPay = actModel.getPay_list();
                    if (listPay != null)
                    {
                        int i = 0;
                        for (PayItemModel pay : listPay)
                        {
                            if (paymentId == pay.getId())
                            {
                                defaultPayIndex = i;
                                break;
                            }
                            i++;
                        }
                        if (defaultPayIndex < 0)
                        {
                            defaultPayIndex = 0;
                            paymentId = 0;
                        }
                    }
                    adapterPayment.getSelectManager().setSelected(defaultPayIndex, true);
                } else
                    errorUi();

            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                ptrsv_content.onRefreshComplete();
                super.onFinish(resp);
            }
        });
    }

    private void errorUi() {
        SDViewBinder.setTextView(tv_user_money, "获取数据失败");
        SDViewUtil.hide(ll_payment);
        SDViewUtil.hide(ll_payment_rule);
        SDViewUtil.hide(ll_other_ticket_exchange);
    }

    @Override
    protected void onResume()
    {
        requestData();
        super.onResume();
    }
}
