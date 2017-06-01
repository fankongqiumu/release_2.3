package com.fanwe.live.appview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.blocker.SDBlocker;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.customview.SDSlidingPlayView;
import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.model.SDTaskRunnable;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveGiftAdapter;
import com.fanwe.live.adapter.LiveGiftPagerAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.LiveRechargeDialog;
import com.fanwe.live.event.EUpdateUserInfo;
import com.fanwe.live.model.App_propActModel;
import com.fanwe.live.model.LiveGiftModel;
import com.fanwe.live.model.UserModel;
import com.ta.util.netstate.TANetWorkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LiveSendGiftView extends BaseAppView implements ILivePrivateChatMoreView
{
    private static final long DURATION_CONTINUE = 5 * 1000;
    private static final long DURATION_COUNT = 100;

    private SDSlidingPlayView spv_content;
    private View ll_charge;
    private TextView tv_diamonds;
    private TextView tv_send;
    private View view_continue_send;
    private View view_click_continue_send;
    private TextView tv_continue_number;
    private TextView tv_count_down_number;

    private LiveGiftPagerAdapter adapterPager;
    private List<LiveGiftModel> listModel;

    private LiveGiftModel selectedGiftModel;
    private SDLooper looper = new SDSimpleLooper();
    private long countDownNumber = DURATION_CONTINUE / DURATION_COUNT;
    private int clickNumber = 0;
    /**
     * 是否连发模式
     */
    private boolean isContinueMode;
    private SDBlocker blocker = new SDBlocker(300);

    private UserModel user;

    private ClickListener clickListener;
    private ContinueListener continueListener;


    public LiveSendGiftView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LiveSendGiftView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LiveSendGiftView(Context context)
    {
        super(context);
        init();
    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public void setContinueListener(ContinueListener continueListener)
    {
        this.continueListener = continueListener;
    }

    @Override
    protected void init()
    {
        super.init();

        setContentView(R.layout.view_live_send_gift);

        spv_content = find(R.id.spv_content);
        ll_charge = find(R.id.ll_charge);
        tv_diamonds = find(R.id.tv_diamonds);
        tv_send = find(R.id.tv_send);
        view_continue_send = find(R.id.view_continue_send);
        view_click_continue_send = find(R.id.view_click_continue_send);
        tv_continue_number = find(R.id.tv_continue_number);
        tv_count_down_number = find(R.id.tv_count_down_number);

        listModel = new ArrayList<>();

        register();
        bindUserData();
    }

    public void bindUserData()
    {
        updateDiamonds(UserModelDao.query());
    }

    /**
     * 更新钻石数量
     *
     * @param user
     */
    private void updateDiamonds(UserModel user)
    {
        if (user != null)
        {
            this.user = user;
            SDViewBinder.setTextView(tv_diamonds, String.valueOf(user.getDiamonds()));
        }
    }

    /**
     * 发送某礼物成功调用，更新本地钻石数量
     *
     * @param giftModel
     */
    public void sendGiftSuccess(final LiveGiftModel giftModel)
    {
        if (giftModel != null)
        {
            SDHandlerManager.getBackgroundHandler().post(new SDTaskRunnable<UserModel>()
            {
                @Override
                public UserModel onBackground()
                {
                    UserModel user = UserModelDao.query();
                    user.payDiamonds(giftModel.getDiamonds());
                    UserModelDao.insertOrUpdate(user);
                    return user;
                }

                @Override
                public void onMainThread(UserModel result)
                {
                    updateDiamonds(result);
                }
            });
        }
    }

    private void register()
    {
        initSDSlidingPlayView();
        ll_charge.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                clickCharge();
            }
        });
        tv_send.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                clickSend();
            }
        });
        view_click_continue_send.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                clickContinueSend();
            }
        });
    }

    /**
     * 触发连击调用方法
     */
    protected void clickContinueSend()
    {
        if (blocker.block())
        {
            return;
        }

        if (validateSend())
        {
            int giftNumber = 1;
            int is_plus = 0;
            clickNumber++;
            startCountDownNumberLooper();
            if (clickNumber > 1)
            {
                is_plus = 1;
            } else
            {
                is_plus = 0;
            }

            if (clickListener != null)
            {
                clickListener.onClickSend(selectedGiftModel, is_plus);
            }
        }
    }

    /**
     * 开始连击倒计时
     */
    private void startCountDownNumberLooper()
    {
        resetCountDownNumber();
        looper.start(DURATION_COUNT, countDownNumberRunnable);
    }

    private Runnable countDownNumberRunnable = new Runnable()
    {

        @Override
        public void run()
        {
            countDownNumber--;
            if (countDownNumber <= 0)
            {
                resetClick();
            } else
            {
                if (clickNumber > 0)
                {
                    tv_continue_number.setText("X" + clickNumber);
                }
                tv_count_down_number.setText(String.valueOf(countDownNumber));
            }
        }
    };

    protected void clickCharge()
    {
        LiveRechargeDialog dialog = new LiveRechargeDialog(getActivity());
        dialog.showCenter();
    }

    private void resetClick()
    {
        if (isContinueMode)
        {
            isContinueMode = false;
            if (continueListener != null)
            {
                continueListener.onContinueFinish(selectedGiftModel, clickNumber);
            }
        }

        looper.stop();
        clickNumber = 0;
        tv_continue_number.setText("");
        hideContinueSend();
    }

    private void resetCountDownNumber()
    {
        countDownNumber = DURATION_CONTINUE / DURATION_COUNT;
    }

    /**
     * 找到选中的礼物
     */
    private void findSelectedGiftModel()
    {
        selectedGiftModel = null;
        if (listModel != null)
        {
            for (LiveGiftModel item : listModel)
            {
                if (item.isSelected())
                {
                    selectedGiftModel = item;
                    break;
                }
            }
        }
    }

    private void initSDSlidingPlayView()
    {
        spv_content.setNormalImageResId(R.drawable.ic_point_normal_white);
        spv_content.setSelectedImageResId(R.drawable.ic_point_selected_main_color);
        SDViewUtil.setViewMarginBottom(spv_content.vpg_content, SDViewUtil.dp2px(10));
    }

    /**
     * 请求礼物列表
     */
    public void requestData()
    {
        if (listModel.isEmpty())
        {
            CommonInterface.requestGift(new AppRequestCallback<App_propActModel>()
            {
                @Override
                protected void onSuccess(SDResponse resp)
                {
                    if (actModel.isOk())
                    {
                        listModel = actModel.getList();

                        List<List<LiveGiftModel>> listModelPager = SDCollectionUtil.splitList(listModel, 8);
                        adapterPager = new LiveGiftPagerAdapter(listModelPager, getActivity());
                        adapterPager.setListener(new LiveGiftAdapter.LiveGiftAdapterListener()
                        {

                            @Override
                            public void onClickItem(int position, LiveGiftModel model, LiveGiftAdapter adapter)
                            {
                                adapter.getSelectManager().performClick(position);
                                adapterPager.clickAdapter(adapter);
                                resetClick();
                            }
                        });
                        spv_content.setAdapter(adapterPager);
                    } else
                    {

                    }
                }

                @Override
                protected void onError(SDResponse resp)
                {
                    super.onError(resp);
                }
            });
        }
    }

    private boolean validateSend()
    {
        if (!TANetWorkUtil.isNetworkAvailable(getActivity()))
        {
            SDToast.showToast("无网络");
            return false;
        }

        findSelectedGiftModel();
        if (selectedGiftModel == null)
        {
            SDToast.showToast("请选择礼物");
            return false;
        }

        if (user == null)
        {
            return false;
        }

        if (!user.canDiamondsPay(selectedGiftModel.getDiamonds()))
        {
            SDToast.showToast("余额不足");
            return false;
        }

        return true;
    }

    /**
     * 点击发送按钮
     */
    private void clickSend()
    {
        if (validateSend())
        {
            if (selectedGiftModel.getIs_much() == 1)
            {
                showContinueSend();
                clickContinueSend();
                isContinueMode = true;
            } else
            {
                //通知发送按钮被点击接口
                if (clickListener != null)
                {
                    clickListener.onClickSend(selectedGiftModel, 0);
                }
            }
        }
    }

    private void showContinueSend()
    {
        SDViewUtil.hide(tv_send);
        SDViewUtil.show(view_continue_send);
    }

    private void hideContinueSend()
    {
        SDViewUtil.hide(view_continue_send);
        SDViewUtil.show(tv_send);
    }

    /**
     * 本地用户数据更新事件
     *
     * @param event
     */
    public void onEventMainThread(EUpdateUserInfo event)
    {
        bindUserData();
    }

    @Override
    public void setContentMatchParent()
    {
        SDViewUtil.setViewHeightWeightContent(spv_content, 1);
        spv_content.setContentMatchParent();
    }

    @Override
    public void setContentWrapContent()
    {
        SDViewUtil.setViewHeightWrapContent(spv_content);
        spv_content.setContentWrapContent();
    }

    public interface ClickListener
    {
        void onClickSend(LiveGiftModel model, int is_plus);
    }

    public interface ContinueListener
    {
        void onContinueFinish(LiveGiftModel model, int clickNumber);
    }

}
