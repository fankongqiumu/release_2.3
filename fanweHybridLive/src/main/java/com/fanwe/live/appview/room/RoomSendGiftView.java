package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.http.AppRequestParams;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.utils.SDAnimationUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.IMHelper;
import com.fanwe.live.R;
import com.fanwe.live.appview.LiveSendGiftView;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.App_pop_propActModel;
import com.fanwe.live.model.Deal_send_propActModel;
import com.fanwe.live.model.LiveGiftModel;
import com.fanwe.live.model.custommsg.CustomMsgPrivateGift;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

public class RoomSendGiftView extends RoomView
{
    public RoomSendGiftView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomSendGiftView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomSendGiftView(Context context)
    {
        super(context);
    }

    private LiveSendGiftView view_send_gift;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_send_gift;
    }

    @Override
    protected void baseConstructorInit()
    {
        super.baseConstructorInit();

        view_send_gift = (LiveSendGiftView) findViewById(R.id.view_send_gift);
        view_send_gift.setClickListener(new LiveSendGiftView.ClickListener()
        {
            @Override
            public void onClickSend(LiveGiftModel model, int is_plus)
            {
                sendGift(model, 1, is_plus, getLiveInfo().getRoomId());
            }
        });

        invisible();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        setShowAnimator(SDAnimationUtil.translateInBottom(this));
        setHideAnimator(SDAnimationUtil.translateOutBottom(this));
    }

    private void sendGift(final LiveGiftModel giftModel, final int giftNumber, int is_plus, int roomId)
    {
        if (giftModel != null)
        {
            if (getLiveInfo().getCreaterId().equals(UserModelDao.getUserId()))
            {

            } else
            {
                if (giftModel.getIs_much() != 1)
                {
                    SDToast.showToast("发送完成");
                }
            }

            if (getLiveInfo().getRoomInfo() == null)
            {
                return;
            }

            if (getLiveInfo().getRoomInfo().getLive_in() == 0)
            {
                //私聊发礼物接口
                final String createrId = getLiveInfo().getCreaterId();
                if (createrId != null)
                {
                    CommonInterface.requestSendGiftPrivate(giftModel.getId(), giftNumber, createrId, new AppRequestCallback<Deal_send_propActModel>()
                    {
                        @Override
                        protected void onSuccess(SDResponse resp)
                        {
                            if (actModel.isOk())
                            {
                                view_send_gift.sendGiftSuccess(giftModel);

                                // 发送私聊消息给主播
                                final CustomMsgPrivateGift msg = new CustomMsgPrivateGift();
                                msg.fillData(actModel);
                                IMHelper.sendMsgC2C(createrId, msg, new TIMValueCallBack<TIMMessage>()
                                {
                                    @Override
                                    public void onError(int i, String s)
                                    {
                                    }

                                    @Override
                                    public void onSuccess(TIMMessage timMessage)
                                    {
                                        // 如果私聊界面不是每次都加载的话要post一条来刷新界面
                                        // IMHelper.postMsgLocal(msg, createrId);
                                    }
                                });
                            }
                        }
                    });
                }
            } else
            {
                AppRequestParams params = CommonInterface.requestSendGiftParams(giftModel.getId(), giftNumber, is_plus, getLiveInfo().getRoomId());
                AppHttpUtil.getInstance().post(params, new AppRequestCallback<App_pop_propActModel>()
                {
                    @Override
                    protected void onSuccess(SDResponse resp)
                    {
                        // 扣费
                        if (actModel.isOk())
                        {
                            view_send_gift.sendGiftSuccess(giftModel);
                        }
                    }

                    @Override
                    protected void onError(SDResponse resp)
                    {
                        CommonInterface.requestMyUserInfo(null);
                    }
                });
            }
        }
    }

    public void requestData()
    {
        if (view_send_gift != null)
        {
            view_send_gift.requestData();
        }
    }

    public void bindUserData()
    {
        if (view_send_gift != null)
        {
            view_send_gift.bindUserData();
        }
    }

    @Override
    protected boolean onTouchDownOutside(MotionEvent ev)
    {
        invisible(true);
        return true;
    }

    @Override
    public boolean onBackPressed()
    {
        invisible(true);
        return true;
    }
}
