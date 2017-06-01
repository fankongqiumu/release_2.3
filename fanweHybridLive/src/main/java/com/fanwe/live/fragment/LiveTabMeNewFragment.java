package com.fanwe.live.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanwe.auction.adapter.AuctionTabMeItemNewAdapter;
import com.fanwe.auction.model.AuctionTabMeItemModel;
import com.fanwe.hybrid.fragment.BaseFragment;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveChatC2CActivity;
import com.fanwe.live.activity.LiveDistributionActivity;
import com.fanwe.live.activity.LiveFamilyDetailsActivity;
import com.fanwe.live.activity.LiveFollowActivity;
import com.fanwe.live.activity.LiveMyFocusActivity;
import com.fanwe.live.activity.LiveMySelfContActivity;
import com.fanwe.live.activity.LiveRechargeDiamondsActivity;
import com.fanwe.live.activity.LiveRechargeVipActivity;
import com.fanwe.live.activity.LiveSearchUserActivity;
import com.fanwe.live.activity.LiveSociatyDetailsActivity;
import com.fanwe.live.activity.LiveUserEditActivity;
import com.fanwe.live.activity.LiveUserHomeReplayActivity;
import com.fanwe.live.activity.LiveUserPhotoActivity;
import com.fanwe.live.activity.LiveUserProfitActivity;
import com.fanwe.live.activity.LiveUserSettingActivity;
import com.fanwe.live.activity.LiveWebViewActivity;
import com.fanwe.live.activity.UserCenterAuthentActivity;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.LiveAddNewFamilyDialog;
import com.fanwe.live.dialog.LiveGameExchangeDialog;
import com.fanwe.live.dialog.LiveJoinCreateSociatyDialog;
import com.fanwe.live.event.ERefreshMsgUnReaded;
import com.fanwe.live.event.EUpdateUserInfo;
import com.fanwe.live.model.App_gameExchangeRateActModel;
import com.fanwe.live.model.App_userinfoActModel;
import com.fanwe.live.model.Deal_send_propActModel;
import com.fanwe.live.model.TotalConversationUnreadMessageModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.utils.GlideUtil;
import com.fanwe.live.utils.LiveUtils;
import com.fanwe.live.view.LiveUnReadNumTextView;
import com.fanwe.o2o.activity.O2OShoppingMystoreActivity;
import com.fanwe.pay.activity.PayBalanceActivity;
import com.fanwe.shop.activity.ShopMyStoreActivity;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */

public class LiveTabMeNewFragment extends BaseFragment
{
    public static final String TAG = "LiveTabMeFragment";

    @ViewInject(R.id.ll_search)
    private LinearLayout ll_search;// 搜索

    @ViewInject(R.id.tv_use_diamonds)
    private TextView tv_use_diamonds; // 送出

    @ViewInject(R.id.ll_chat)
    private RelativeLayout ll_chat;

    @ViewInject(R.id.tv_total_unreadnum)
    private LiveUnReadNumTextView tv_total_unreadnum;

    @ViewInject(R.id.fl_head)
    private FrameLayout fl_head;

    @ViewInject(R.id.tv_user_id)
    private TextView tv_user_id;

    @ViewInject(R.id.ll_user_id)
    private LinearLayout ll_user_id;

    @ViewInject(R.id.iv_head)
    private ImageView iv_head;// 头像

    @ViewInject(R.id.iv_level)
    private ImageView iv_level;// 等级

    @ViewInject(R.id.tv_nick_name)
    private TextView tv_nick_name; // 昵称

    @ViewInject(R.id.iv_vip)
    private ImageView iv_vip;//vip图片标识

    @ViewInject(R.id.iv_global_male)
    private ImageView iv_global_male;// 性别

    @ViewInject(R.id.iv_rank)
    private ImageView iv_rank;// 等级

    @ViewInject(R.id.iv_remark)
    private ImageView iv_remark;// 编辑

    @ViewInject(R.id.ll_v_explain)
    private LinearLayout ll_v_explain; // 认证
    @ViewInject(R.id.tv_v_explain)
    private TextView tv_v_explain;

    @ViewInject(R.id.ll_video)
    private LinearLayout ll_video;//直播
    @ViewInject(R.id.tv_video_num)
    private TextView tv_video_num;

    @ViewInject(R.id.ll_my_focus)
    private LinearLayout ll_my_focus;
    @ViewInject(R.id.tv_focus_count)
    private TextView tv_focus_count;// 关注

    @ViewInject(R.id.ll_my_fans)
    private LinearLayout ll_my_fans;
    @ViewInject(R.id.tv_fans_count)
    private TextView tv_fans_count; // 粉丝

    @ViewInject(R.id.tv_introduce)
    private TextView tv_introduce;// 签名

    @ViewInject(R.id.rl_level)
    private RelativeLayout rl_level;//等级
    @ViewInject(R.id.tv_level)
    private TextView tv_level;

    @ViewInject(R.id.rl_accout)
    private RelativeLayout rl_accout;//账户
    @ViewInject(R.id.tv_accout)
    private TextView tv_accout;

    @ViewInject(R.id.rl_income)
    private RelativeLayout rl_income;//收益
    @ViewInject(R.id.tv_income)
    private TextView tv_income;

    @ViewInject(R.id.ll_auction_gll_info)
    private LinearLayout ll_auction_gll_info;
    @ViewInject(R.id.auction_gll_info)
    private SDGridLinearLayout auction_gll_info;//竞拍Item

    @ViewInject(R.id.include_cont_linear)
    private View include_cont_linear;//印票贡献榜

    @ViewInject(R.id.rel_upgrade)
    private RelativeLayout rel_upgrade;//认证
    @ViewInject(R.id.tv_anchor)
    private TextView tv_anchor;
    @ViewInject(R.id.tv_v_type)
    private TextView tv_v_type;

    @ViewInject(R.id.rel_family)
    private RelativeLayout rel_family;//我的家族

    @ViewInject(R.id.rel_sociaty)
    private RelativeLayout rel_sociaty;//我的公会

    @ViewInject(R.id.rel_pay)
    private RelativeLayout rel_pay;//付费模式

    @ViewInject(R.id.rel_distribution)
    private RelativeLayout rel_distribution;//我的分销

    @ViewInject(R.id.ll_vip)
    private View ll_vip; //vip模块

    @ViewInject(R.id.ll_game_currency_exchange)
    private View ll_game_currency_exchange;//游戏币兑换模块

    @ViewInject(R.id.tv_game_currency)
    private TextView tv_game_currency;

    @ViewInject(R.id.tv_vip)
    private TextView tv_vip; //是否开通vip文字标识

    @ViewInject(R.id.rel_setting)
    private RelativeLayout rel_setting;//设置

    private App_userinfoActModel app_userinfoActModel;

    private AuctionTabMeItemNewAdapter adapter;
    private List<AuctionTabMeItemModel> auction_gll_info_array = new ArrayList<AuctionTabMeItemModel>();

    private LiveAddNewFamilyDialog dialogFam;
    private LiveJoinCreateSociatyDialog dialogSoc;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_new_tab_me;
    }

    @Override
    protected void init()
    {
        super.init();
        register();
        bindAuctionAdapter();
    }

    private void register()
    {
        ll_search.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        fl_head.setOnClickListener(this);
        iv_remark.setOnClickListener(this);
        SDViewUtil.show(iv_remark);
        SDViewUtil.show(ll_user_id);
        ll_my_focus.setOnClickListener(this);
        ll_my_fans.setOnClickListener(this);
        ll_video.setOnClickListener(this);
        rl_level.setOnClickListener(this);
        rl_accout.setOnClickListener(this);
        rl_income.setOnClickListener(this);
        include_cont_linear.setOnClickListener(this);
        rel_upgrade.setOnClickListener(this);
        rel_family.setOnClickListener(this);
        rel_sociaty.setOnClickListener(this);
        rel_pay.setOnClickListener(this);
        rel_distribution.setOnClickListener(this);
        rel_setting.setOnClickListener(this);
        ll_vip.setOnClickListener(this);
        ll_game_currency_exchange.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        if (!hidden)
        {
            request();
            changeUI();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume()
    {
        initUnReadNum();
        request();
        changeUI();
        super.onResume();
    }

    private void changeUI()
    {
        int live_pay = AppRuntimeWorker.getLive_pay();
        if (live_pay == 1)
        {
            SDViewUtil.show(rel_pay);
        } else
        {
            SDViewUtil.hide(rel_pay);
        }

        int distribution = AppRuntimeWorker.getDistribution();
        if (distribution == 1)
        {
            SDViewUtil.show(rel_distribution);
        } else
        {
            SDViewUtil.hide(rel_distribution);
        }
        if (AppRuntimeWorker.isOpenVip())
        {
            SDViewUtil.show(ll_vip);
        } else
        {
            SDViewUtil.hide(ll_vip);
        }

        if (AppRuntimeWorker.useGameCoin())
        {
            SDViewUtil.show(ll_game_currency_exchange);
        } else
        {
            SDViewUtil.hide(ll_game_currency_exchange);
        }

        if (AppRuntimeWorker.getOpen_family_module() == 1)
        {
            SDViewUtil.show(rel_family);
        } else
        {
            SDViewUtil.hide(rel_family);
        }

        if (AppRuntimeWorker.getOpen_sociaty_module() == 1)
        {
            SDViewUtil.show(rel_sociaty);
        } else
        {
            SDViewUtil.hide(rel_sociaty);
        }
    }

    /**
     * 绑定Adapter
     */
    private void bindAuctionAdapter()
    {
        if (adapter == null)
        {
            auction_gll_info.setColNumber(1);
            adapter = new AuctionTabMeItemNewAdapter(auction_gll_info_array, getActivity());
            adapter.setItemClickListener(new SDAdapter.ItemClickListener<AuctionTabMeItemModel>()
            {
                @Override
                public void onClick(int position, AuctionTabMeItemModel item, View view)
                {
                    if (AppRuntimeWorker.getIsOpenWebviewMain())
                    {
                        if (item.getStr_Tag().equals(AuctionTabMeItemModel.TabMeTag.tag5))
                        {
                            Intent intent = new Intent(getActivity(), O2OShoppingMystoreActivity.class);
                            startActivity(intent);
                            return;
                        }
                    }

                    if (item.getStr_Tag().equals(AuctionTabMeItemModel.TabMeTag.tag7))
                    {
                        Intent intent = new Intent(getActivity(), ShopMyStoreActivity.class);
                        startActivity(intent);
                        return;
                    }

                    if (!TextUtils.isEmpty(item.getUrl()))
                    {
                        Intent intent = new Intent(getActivity(), LiveWebViewActivity.class);
                        intent.putExtra(LiveWebViewActivity.EXTRA_URL, item.getUrl());
                        intent.putExtra(LiveWebViewActivity.EXTRA_IS_BACK_FINISH, true);
                        startActivity(intent);
                    } else
                    {
                        SDToast.showToast("url为空");
                    }
                }
            });
            auction_gll_info.setAdapter(adapter);
        }
    }

    private void request()
    {
        CommonInterface.requestMyUserInfo(new AppRequestCallback<App_userinfoActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    app_userinfoActModel = actModel;
                    UserModelDao.insertOrUpdate(actModel.getUser());
                    bindAuctionData(actModel);
                }
            }
        });
    }

    private void bindNormalData(UserModel user)
    {
        if (user != null)
        {
            SDViewBinder.setTextView(tv_user_id, user.getShowId());

            String user_diamonds = user.getUse_diamonds() + "";
            SDViewBinder.setTextView(tv_use_diamonds, user_diamonds);

            GlideUtil.loadHeadImage(user.getHead_image()).into(iv_head);

            if (!TextUtils.isEmpty(user.getV_icon()))
            {
                GlideUtil.load(user.getV_icon()).into(iv_level);
                SDViewUtil.show(iv_level);
            } else
            {
                SDViewUtil.hide(iv_level);
            }

            SDViewBinder.setTextView(tv_nick_name, user.getNick_name());
            if (user.getSexResId() > 0)
            {
                SDViewUtil.show(iv_global_male);
                iv_global_male.setImageResource(user.getSexResId());
            } else
            {
                SDViewUtil.hide(iv_global_male);
            }
            iv_rank.setImageResource(user.getLevelImageResId());

            String focus_count = user.getFocus_count() + "";
            SDViewBinder.setTextView(tv_focus_count, focus_count);

            SDViewBinder.setTextView(tv_fans_count, LiveUtils.getFormatNumber(user.getFans_count()));

            SDViewBinder.setTextView(tv_introduce, user.getSignature(), "TA好像忘记写签名了");

            if (!TextUtils.isEmpty(user.getV_explain()))
            {
                SDViewUtil.show(ll_v_explain);
                SDViewBinder.setTextView(tv_v_explain, user.getV_explain());
            } else
            {
                SDViewUtil.hide(ll_v_explain);
            }

            String video_count = user.getVideo_count() + "";
            SDViewBinder.setTextView(tv_video_num, video_count);

            String user_level = user.getUser_level() + "";
            SDViewBinder.setTextView(tv_level, user_level);

            SDViewBinder.setTextView(tv_income, LiveUtils.getFormatNumber(user.getUseable_ticket()));

            SDViewBinder.setTextView(tv_accout, LiveUtils.getFormatNumber(user.getDiamonds()));

            int v_type = SDTypeParseUtil.getInt(user.getV_type());
            if (v_type == 0)
            {
                SDViewUtil.show(rel_upgrade);
            } else if (v_type == 1)
            {
                SDViewUtil.hide(rel_upgrade);
            } else if (v_type == 2)
            {
                SDViewUtil.hide(rel_upgrade);
            }

            String anchor = SDResourcesUtil.getString(R.string.live_account_authentication);
            anchor = anchor + "认证";
            SDViewBinder.setTextView(tv_anchor, anchor);

            int is_authentication = user.getIs_authentication();
            if (is_authentication == 0)
            {
                tv_v_type.setText("未认证");
            } else if (is_authentication == 1)
            {
                tv_v_type.setText("认证待审核");
            } else if (is_authentication == 2)
            {
                tv_v_type.setText("已认证");
            } else if (is_authentication == 3)
            {
                tv_v_type.setText("认证审核不通过");
            }

            if (user.getIs_vip() == 1)
            {
                SDViewUtil.show(iv_vip);
                tv_vip.setText("已开通");
                tv_vip.setTextColor(SDResourcesUtil.getColor(R.color.main_color));
            } else
            {
                SDViewUtil.hide(iv_vip);
                tv_vip.setText(user.getVip_expire_time());
                tv_vip.setTextColor(SDResourcesUtil.getColor(R.color.user_home_text_gray));
            }

            SDViewBinder.setTextView(tv_game_currency, LiveUtils.getFormatNumber(user.getCoin()) + "游戏币");
        }
    }


    private void bindAuctionData(App_userinfoActModel actModel)
    {
        auction_gll_info_array.clear();
        auction_gll_info_array.addAll(actModel.getItem());

        if (auction_gll_info_array.size() == 0)
        {
            SDViewUtil.hide(ll_auction_gll_info);
        } else
        {
            SDViewUtil.show(ll_auction_gll_info);
            auction_gll_info.notifyDataSetChanged();
        }
    }

    private void initUnReadNum()
    {
        TotalConversationUnreadMessageModel model = IMHelper.getC2CTotalUnreadMessageModel();
        setUnReadNumModel(model);
    }

    public void onEventMainThread(ERefreshMsgUnReaded event)
    {
        TotalConversationUnreadMessageModel model = event.model;
        setUnReadNumModel(model);
    }

    /**
     * @param event 接收刷新UserModel信息事件
     */
    public void onEventMainThread(EUpdateUserInfo event)
    {
        UserModel user = event.user;
        bindNormalData(user);
    }


    private void setUnReadNumModel(TotalConversationUnreadMessageModel model)
    {
        SDViewUtil.hide(tv_total_unreadnum);
        if (model != null && model.getTotalUnreadNum() > 0)
        {
            SDViewUtil.show(tv_total_unreadnum);
            tv_total_unreadnum.setUnReadNumText(model.getTotalUnreadNum());
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.ll_search:
                clickLLSearch();
                break;
            case R.id.ll_chat:
                clickLlChat();
                break;
            case R.id.fl_head:
                clickFlHead();
                break;
            case R.id.iv_remark:
                clickIvRemark();
                break;
            case R.id.ll_video:
                clickRlVideo();
                break;
            case R.id.ll_my_focus:
                clickLlMyFocus();
                break;
            case R.id.ll_my_fans:
                clickLlMyFans();
                break;
            case R.id.rl_level:
                clickRlLevel();
                break;
            case R.id.rl_accout:
                clickRlAccout();
                break;
            case R.id.rl_income:
                clickRlIncome();
                break;
            case R.id.include_cont_linear:
                clickIncludeContLinear();
                break;
            case R.id.rel_upgrade:
                clickLlUpgrade();
                break;
            case R.id.rel_family:
                clickFamily();
                break;
            case R.id.rel_pay:
                clickRelPay();
                break;
            case R.id.rel_distribution:
                clickRelDistribution();
                break;
            case R.id.rel_setting:
                clickSetting();
                break;
            case R.id.ll_vip:
                clickVip();
                break;
            case R.id.rel_sociaty:
                clickSociaty();
                break;
            case R.id.ll_game_currency_exchange:
                doGameExchange();
            default:
                break;
        }
    }

    // 搜索
    private void clickLLSearch()
    {
        Intent intent = new Intent(getActivity(), LiveSearchUserActivity.class);
        startActivity(intent);
    }

    //聊天
    private void clickLlChat()
    {
        Intent intent = new Intent(getActivity(), LiveChatC2CActivity.class);
        startActivity(intent);
    }

    // 我的头像
    private void clickFlHead()
    {
        if (app_userinfoActModel != null)
        {
            UserModel user = app_userinfoActModel.getUser();
            if (user != null)
            {
                Intent intent = new Intent(getActivity(), LiveUserPhotoActivity.class);
                intent.putExtra(LiveUserPhotoActivity.EXTRA_USER_IMG_URL, user.getHead_image());
                startActivity(intent);
            }
        }
    }

    //编辑
    private void clickIvRemark()
    {
        Intent intent = new Intent(getActivity(), LiveUserEditActivity.class);
        startActivity(intent);
    }

    // 回放列表
    private void clickRlVideo()
    {
        Intent intent = new Intent(getActivity(), LiveUserHomeReplayActivity.class);
        startActivity(intent);
    }

    // 我关注的人
    private void clickLlMyFocus()
    {
        UserModel user = UserModelDao.query();
        if (user != null)
        {
            String user_id = user.getUser_id();
            if (!TextUtils.isEmpty(user_id))
            {
                Intent intent = new Intent(getActivity(), LiveFollowActivity.class);
                intent.putExtra(LiveFollowActivity.EXTRA_USER_ID, user_id);
                startActivity(intent);
            } else
            {
                SDToast.showToast("本地user_id为空");
            }
        }
    }

    // 我的粉丝
    private void clickLlMyFans()
    {
        Intent intent = new Intent(getActivity(), LiveMyFocusActivity.class);
        startActivity(intent);
    }

    //等级
    private void clickRlLevel()
    {
        Intent intent = new Intent(getActivity(), LiveWebViewActivity.class);
        intent.putExtra(LiveWebViewActivity.EXTRA_URL, AppRuntimeWorker.getUrl_my_grades());
        startActivity(intent);
    }

    //账户
    private void clickRlAccout()
    {
        Intent intent = new Intent(getActivity(), LiveRechargeDiamondsActivity.class);
        startActivity(intent);
    }

    /**
     * VIP充值页面
     */
    private void clickVip()
    {
        Intent intent = new Intent(getActivity(), LiveRechargeVipActivity.class);
        startActivity(intent);
    }

    private void doGameExchange()
    {
        showProgressDialog("");
        CommonInterface.requestGamesExchangeRate(new AppRequestCallback<App_gameExchangeRateActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    LiveGameExchangeDialog dialog = new LiveGameExchangeDialog(getActivity(), LiveGameExchangeDialog.TYPE_COIN_EXCHANGE, new LiveGameExchangeDialog.OnSuccessListener()
                    {
                        @Override
                        public void onExchangeSuccess(long diamonds, long coins)
                        {
                            UserModel user = UserModelDao.updateDiamondsAndCoins(diamonds, coins);
                            UserModelDao.insertOrUpdate(user);
                        }

                        @Override
                        public void onSendCurrencySuccess(Deal_send_propActModel model)
                        {

                        }
                    });
                    dialog.setRate(actModel.getExchange_rate());
                    dialog.setCurrency(app_userinfoActModel.getUser().getDiamonds());
                    dialog.showCenter();
                }
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dismissProgressDialog();
            }
        });
    }

    //收益
    private void clickRlIncome()
    {
        Intent intent = new Intent(getActivity(), LiveUserProfitActivity.class);
        startActivity(intent);
    }

    //印票贡献榜
    private void clickIncludeContLinear()
    {
        Intent intent = new Intent(getActivity(), LiveMySelfContActivity.class);
        startActivity(intent);
    }

    //认证
    private void clickLlUpgrade()
    {
        if (app_userinfoActModel != null)
        {
            Intent intent = new Intent(getActivity(), UserCenterAuthentActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 我的家族
     */
    private void clickFamily()
    {
        UserModel dao = UserModelDao.query();
        if (dao.getFamily_id() == 0)
        {
            showFamDialog();
        } else
        {
            //家族详情
            Intent intent = new Intent(getActivity(), LiveFamilyDetailsActivity.class);
            startActivity(intent);
        }
    }

    private void showFamDialog()
    {
        if (dialogFam == null)
        {
            dialogFam = new LiveAddNewFamilyDialog(getActivity());
        }
        dialogFam.showCenter();
    }

    /**
     * 我的公会
     */
    private void clickSociaty()
    {
        UserModel dao = UserModelDao.query();
        if (dao.getSociety_id() == 0)
        {
            showSocDialog();
        } else
        {
            //公会详情
            Intent intent = new Intent(getActivity(), LiveSociatyDetailsActivity.class);
            startActivity(intent);
        }
    }

    private void showSocDialog()
    {
        if (dialogSoc == null)
        {
            dialogSoc = new LiveJoinCreateSociatyDialog(getActivity());
        }
        dialogSoc.showCenter();
    }

    //付费榜
    private void clickRelPay()
    {
        Intent intent = new Intent(getActivity(), PayBalanceActivity.class);
        startActivity(intent);
    }

    /**
     * 我的分销
     */
    private void clickRelDistribution()
    {
        Intent intent = new Intent(getActivity(), LiveDistributionActivity.class);
        startActivity(intent);
    }

    /**
     * 设置
     */
    private void clickSetting()
    {
        Intent intent = new Intent(getActivity(), LiveUserSettingActivity.class);
        startActivity(intent);
    }
}
