package com.fanwe.live.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fanwe.auction.common.AuctionCommonInterface;
import com.fanwe.auction.model.App_pai_user_open_goodsActModel;
import com.fanwe.games.model.GameBankerModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.umeng.UmengSocialManager;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.dialog.SDDialogMenu;
import com.fanwe.library.dialog.SDDialogProgress;
import com.fanwe.library.listener.SDVisibilityStateListener;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveFloatViewWebViewActivity;
import com.fanwe.live.appview.AMenuView;
import com.fanwe.live.appview.room.RoomSendGiftView;
import com.fanwe.live.appview.room.RoomViewerBottomView;
import com.fanwe.live.appview.room.RoomViewerFinishView;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.LiveInviteVideoDialog;
import com.fanwe.live.dialog.LiveViewerPlugDialog;
import com.fanwe.live.model.App_check_lianmaiActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.RandomPodcastModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgRejectVideo;
import com.fanwe.live.model.custommsg.CustomMsgViewerJoin;
import com.fanwe.live.utils.GlideUtil;
import com.fanwe.live.view.SDVerticalScollView;
import com.fanwe.o2o.dialog.O2OShoppingPodCastDialog;
import com.fanwe.shop.dialog.ShopPodcastGoodsDialog;

/**
 * 观众界面
 */
public class LiveLayoutViewerActivity extends LiveLayoutExtendActivity
{
    /**
     * 加载中的图片链接
     */
    public static final String EXTRA_LOADING_VIDEO_IMAGE_URL = "extra_loading_video_image_url";
    /**
     * 直播的类型，仅用于观众时候需要传入0-热门;1-最新;2-关注(int)
     */
    public static final String EXTRA_LIVE_TYPE = "extra_live_type";
    /**
     * 私密直播的key(String)
     */
    public static final String EXTRA_PRIVATE_KEY = "extra_private_key";
    /**
     * 性别0-全部，1-男，2-女(int)
     */
    public static final String EXTRA_SEX = "extra_sex";
    /**
     * 话题id(int)
     */
    public static final String EXTRA_CATE_ID = "extra_cate_id";
    /**
     * 城市(String)
     */
    public static final String EXTRA_CITY = "extra_city";

    protected View view_loading_video;
    protected ImageView iv_image_top;
    protected ImageView iv_loading_video;
    protected ImageView iv_image_bottom;

    protected ViewGroup fl_live_send_gift;

    protected RoomSendGiftView roomSendGiftView;
    protected RoomViewerBottomView roomViewerBottomView;

    /**
     * 直播的类型0-热门;1-最新
     */
    protected int type;
    /**
     * 私密直播的key
     */
    protected String strPrivateKey;
    /**
     * 性别0-全部，1-男，2-女
     */
    protected int sex;
    /**
     * 话题id
     */
    protected int cate_id;
    /**
     * 城市
     */
    protected String city;

    /**
     * 是否是滚动切换的房间
     */
    protected boolean isScrollChangeRoom = false;
    /**
     * 是否显示直播结束界面
     */
    protected boolean isNeedShowFinish = false;
    /**
     * 是否正在和主播连麦
     */
    protected boolean isVideoing = false;
    /**
     * 是否可以绑定连麦按钮的显示隐藏
     */
    private boolean canBindShowInviteVideoView = false;
    /**
     * 是否可以绑定分享按钮的显示隐藏
     */
    private boolean canBindShowShareView = false;
    protected LiveInviteVideoDialog inviteVideoDialog;
    private SDVerticalScollView verticalScollView;
    private int viewerNumber;

    private RoomViewerFinishView roomViewerFinishView;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        String loadingVideoImageUrl = getIntent().getStringExtra(EXTRA_LOADING_VIDEO_IMAGE_URL);
        type = getIntent().getIntExtra(EXTRA_LIVE_TYPE, 0);
        strPrivateKey = getIntent().getStringExtra(EXTRA_PRIVATE_KEY);
        sex = getIntent().getIntExtra(EXTRA_SEX, 0);
        city = getIntent().getStringExtra(EXTRA_CITY);

        view_loading_video = findViewById(R.id.view_loading_video);
        iv_image_top = (ImageView) findViewById(R.id.iv_image_top);
        iv_loading_video = (ImageView) findViewById(R.id.iv_loading_video);
        iv_image_bottom = (ImageView) findViewById(R.id.iv_image_bottom);

        setLoadingVideoImageUrl(loadingVideoImageUrl);

        initLayout(getWindow().getDecorView());
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);
        verticalScollView = (SDVerticalScollView) view.findViewById(R.id.view_vertical_scroll);
        fl_live_send_gift = (ViewGroup) view.findViewById(R.id.fl_live_send_gift);

        initSDVerticalScollView(verticalScollView);
    }


    /**
     * 重写此方法设置监听
     *
     * @param scollView
     */
    protected void initSDVerticalScollView(SDVerticalScollView scollView)
    {
        if (scollView == null)
        {
            return;
        }
        boolean enableVerticalScroll = getResources().getBoolean(R.bool.is_live_room_vertical_scroll);
        scollView.setEnableVerticalScroll(enableVerticalScroll);
        scollView.setTopView(iv_image_top);
        scollView.setVerticalView(findViewById(R.id.view_touch_scroll));
        scollView.setBottomView(iv_image_bottom);

        scollView.setLeftView(findViewById(R.id.view_left));
        scollView.setHorizontalView(findViewById(R.id.rl_root_layout));

        scollView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                addHeart();
            }
        });
        scollView.setListenerScroll(defaultScrollListener);
    }

    /**
     * 默认滚动监听
     */
    protected SDVerticalScollView.ScrollListener defaultScrollListener = new SDVerticalScollView.ScrollListener()
    {
        @Override
        public void onFinishTop()
        {
            afterVerticalScroll(true);
            verticalScollView.resetVerticalViews();
        }

        @Override
        public void onFinishCenter()
        {
            verticalScollView.resetVerticalViews();
        }

        @Override
        public void onFinishBottom()
        {
            afterVerticalScroll(false);
            verticalScollView.resetVerticalViews();
        }

        @Override
        public void onVerticalScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            LiveLayoutViewerActivity.this.onVerticalScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onHorizontalScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {

        }
    };

    protected void onVerticalScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {

    }

    protected void afterVerticalScroll(boolean top)
    {
        if (top)
        {
            iv_loading_video.setImageDrawable(iv_image_top.getDrawable());
            if (getRoomInfo() != null && getRoomInfo().getPodcast_previous() != null)
            {
                setRoomId(getRoomInfo().getPodcast_previous().getRoom_id());
            }
        } else
        {
            iv_loading_video.setImageDrawable(iv_image_bottom.getDrawable());
            if (getRoomInfo() != null && getRoomInfo().getPodcast_next() != null)
            {
                setRoomId(getRoomInfo().getPodcast_next().getRoom_id());
            }
        }
        isScrollChangeRoom = true;
        onChangeRoomActionComplete();
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();
        roomInfoView.invisible();
        roomGiftPlayView.invisible();
        roomPopMsgView.invisible();
        roomViewerJoinRoomView.invisible();
        roomMsgView.invisible();
        roomHeartView.invisible();
        roomGiftGifView.invisible();
        roomViewerBottomView.invisible();

        roomViewerBottomView.showMenuInviteVideo(false);
        roomViewerBottomView.showMenuShare(false);
    }

    @Override
    public void onChangeRoomComplete()
    {
        super.onChangeRoomComplete();
        roomInfoView.show();
        roomGiftPlayView.show();
        roomPopMsgView.show();
        roomViewerJoinRoomView.show();
        roomMsgView.show();
        roomHeartView.show();
        roomGiftGifView.show();
        roomViewerBottomView.show();

        bindShowInviteVideoView();
        bindShowShareView();
    }

    public void addHeart()
    {
        if (roomHeartView != null)
        {
            roomHeartView.addHeart();
        }
    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg)
    {
        super.onMsgEndVideo(msg);
        showSendMsgView(false);
        isNeedShowFinish = true;
        viewerNumber = msg.getShow_num();
    }

    /**
     * 是否正在请求连麦中
     *
     * @return
     */
    public boolean isInvitingVideo()
    {
        if (inviteVideoDialog != null)
        {
            return inviteVideoDialog.isShowing();
        } else
        {
            return false;
        }
    }

    @Override
    protected void addLiveFinish()
    {
        if (getRoomInfo() != null)
        {
            removeView(roomViewerFinishView);
            roomViewerFinishView = new RoomViewerFinishView(this);

            int status = getRoomInfo().getStatus();
            if (status == 1)
            {
                roomViewerFinishView.setHasFollow(roomInfoView.getHasFollow());
            } else if (status == 2)
            {
                viewerNumber = getRoomInfo().getShow_num();
                roomViewerFinishView.setHasFollow(getRoomInfo().getHas_focus());
            }
            roomViewerFinishView.setViewerNumber(viewerNumber);
            addView(roomViewerFinishView);
        }
    }

    /**
     * 设置直播间的加载背景图片链接
     *
     * @param loadingVideoImageUrl
     */
    public void setLoadingVideoImageUrl(final String loadingVideoImageUrl)
    {
        if (iv_loading_video != null && !TextUtils.isEmpty(loadingVideoImageUrl))
        {
            GlideUtil.load(loadingVideoImageUrl).into(iv_loading_video);
        }
    }

    /**
     * 显示加载背景
     */
    protected void showLoadingVideo()
    {
        if (view_loading_video != null)
        {
            SDViewUtil.show(view_loading_video);
        }
    }

    /**
     * 隐藏加载背景
     */
    protected void hideLoadingVideo()
    {
        if (view_loading_video != null)
        {
            SDViewUtil.hide(view_loading_video);
        }
    }

    protected void bindLoadingImages()
    {
        if (getRoomInfo() != null)
        {
            if (getRoomInfo().isOk())
            {
                RandomPodcastModel next = getRoomInfo().getPodcast_next();
                if (next != null)
                {
                    GlideUtil.load(next.getHead_image()).into(iv_image_bottom);
                }
                RandomPodcastModel previous = getRoomInfo().getPodcast_previous();
                if (previous != null)
                {
                    GlideUtil.load(previous.getHead_image()).into(iv_image_top);
                }
            }
        }
    }

    @Override
    protected SDRequestHandler requestRoomInfo()
    {
        return getLiveBusiness().requestRoomInfo(getRoomId(), 0, type, 1, sex, cate_id, city, strPrivateKey);
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);

        bindShowInviteVideoView();
        bindLoadingImages();
        sendViewerJoinMsg();
    }

    /**
     * 发送观众加入消息
     */
    public void sendViewerJoinMsg()
    {
        if (!getViewerIM().isCanSendViewerJoinMsg())
        {
            return;
        }
        App_get_videoActModel actModel = getRoomInfo();
        if (actModel == null)
        {
            return;
        }
        UserModel user = UserModelDao.query();
        if (user == null)
        {
            return;
        }

        boolean sendViewerJoinMsg = true;
        if (!user.isProUser() && actModel.getJoin_room_prompt() == 0)
        {
            sendViewerJoinMsg = false;
        }

        if (sendViewerJoinMsg)
        {
            CustomMsgViewerJoin joinMsg = new CustomMsgViewerJoin();
            joinMsg.setSortNumber(actModel.getSort_num());

            getViewerIM().sendViewerJoinMsg(joinMsg, null);
        }
    }

    public void setCanBindShowInviteVideoView(boolean canBindShowInviteVideoView)
    {
        this.canBindShowInviteVideoView = canBindShowInviteVideoView;
        bindShowInviteVideoView();
    }

    /**
     * 是否显示连麦按钮
     */
    protected void bindShowInviteVideoView()
    {
        if (roomViewerBottomView == null)
        {
            return;
        }
        if (getRoomInfo() != null)
        {
            if (canBindShowInviteVideoView)
            {
                if (getRoomInfo().getHas_lianmai() == 1)
                {
                    roomViewerBottomView.showMenuInviteVideo(true);
                } else
                {
                    roomViewerBottomView.showMenuInviteVideo(false);
                }
            }
        } else
        {
            roomViewerBottomView.showMenuInviteVideo(false);
        }
    }

    public void setCanBindShowShareView(boolean canBindShowShareView)
    {
        this.canBindShowShareView = canBindShowShareView;
        bindShowShareView();
    }

    @Override
    protected void bindShowShareView()
    {
        if (roomViewerBottomView == null)
        {
            return;
        }
        if (getRoomInfo() != null)
        {
            if (canBindShowShareView)
            {
                if (isPrivate() || UmengSocialManager.isAllSocialDisable())
                {
                    roomViewerBottomView.showMenuShare(false);
                } else
                {
                    roomViewerBottomView.showMenuShare(true);
                }
            }
        } else
        {
            roomViewerBottomView.showMenuShare(false);
        }
    }

    /**
     * 送礼物
     */
    protected void addLiveSendGift()
    {
        if (roomSendGiftView == null)
        {
            roomSendGiftView = new RoomSendGiftView(this);
            roomSendGiftView.addVisibilityStateListener(new SDVisibilityStateListener()
            {
                @Override
                public void onVisible(View view)
                {
                    onShowSendGiftView(view);
                }

                @Override
                public void onGone(View view)
                {
                    SDViewUtil.removeViewFromParent(roomSendGiftView);
                    onHideSendGiftView(view);
                }

                @Override
                public void onInvisible(View view)
                {
                    SDViewUtil.removeViewFromParent(roomSendGiftView);
                    onHideSendGiftView(view);
                }
            });
        }
        roomSendGiftView.requestData();
        roomSendGiftView.bindUserData();
        replaceView(fl_live_send_gift, roomSendGiftView);
    }

    @Override
    protected void addLiveBottomMenu()
    {
        roomViewerBottomView = new RoomViewerBottomView(this);
        roomViewerBottomView.setClickListener(bottomClickListener);
        replaceView(fl_live_bottom_menu, roomViewerBottomView);
    }

    /**
     * 底部菜单点击监听
     */
    protected RoomViewerBottomView.ClickListener bottomClickListener = new RoomViewerBottomView.ClickListener()
    {
        @Override
        public void onClickMenuSendMsg(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuSendMsg(v);
        }

        @Override
        public void onClickMenuViewerPlug(View v)
        {
            LiveLayoutViewerActivity.this.onClickViewerPlugMenu(v);
        }

        @Override
        public void onClickMenuPluginSwitch(View v)
        {
            togglePluginExtend();
        }

        @Override
        public void onClickMenuPrivateMsg(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuPrivateMsg(v);
        }

        @Override
        public void onClickMenuAuctionPay(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuAuctionPay(v);
        }

        @Override
        public void onClickMenuInviteVideo(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuInviteVideo(v);
        }

        @Override
        public void onClickMenuSendGift(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuSendGift(v);
        }

        @Override
        public void onClickMenuShare(View v)
        {
            LiveLayoutViewerActivity.this.onClickMenuShare(v);
        }

        @Override
        public void onClickApplyBanker(View v)
        {
            //申请上庄
            long principal = (long) v.getTag();
            onClickGameCtrlApplyBanker(principal);
        }

        @Override
        public void onClickMenuPodcast(View v)
        {
            onClickMenuPodcastOrder();
        }

        @Override
        public void onCLickMenuMyStore(View v)
        {
            onClickMenuMyStore();
        }
    };

    public void onClickViewerPlugMenu(View v)
    {
        final LiveViewerPlugDialog dialog = new LiveViewerPlugDialog(getActivity());
        dialog.setClickListener(new LiveViewerPlugDialog.ClickListener()
        {
            @Override
            public void onClickStarStore(AMenuView view)
            {
                dialog.dismiss();
                onClickMenuPodcastOrder();
            }

            @Override
            public void onClickShopStore(AMenuView view)
            {
                dialog.dismiss();
                onClickMenuMyStore();
            }
        });
        dialog.showBottom();
    }

    //星店订单
    protected void onClickMenuPodcastOrder()
    {
        if (AppRuntimeWorker.getIsOpenWebviewMain())
        {
            O2OShoppingPodCastDialog dialog = new O2OShoppingPodCastDialog(this, getCreaterId());
            dialog.showBottom();
            return;
        }

        AuctionCommonInterface.requestPaiUserOpenGoods(getCreaterId(), new AppRequestCallback<App_pai_user_open_goodsActModel>()
        {
            private SDDialogProgress dialog = new SDDialogProgress(SDActivityManager.getInstance().getLastActivity());

            @Override
            protected void onStart()
            {
                super.onStart();
                dialog.setTextMsg("");
                dialog.show();
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dialog.dismiss();
            }

            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.getStatus() == 1)
                {
                    Intent intent = new Intent(LiveLayoutViewerActivity.this, LiveFloatViewWebViewActivity.class);
                    intent.putExtra(LiveFloatViewWebViewActivity.EXTRA_URL, actModel.getUrl());
                    startActivity(intent);
                }
            }
        });
    }

    private void onClickMenuMyStore()
    {
        ShopPodcastGoodsDialog dialog = new ShopPodcastGoodsDialog(getActivity(), isCreater(), getCreaterId());
        dialog.showBottom();
    }

    /**
     * 观众竞拍付款
     */
    protected void onClickMenuAuctionPay(View v)
    {

    }

    /**
     * 发起连麦
     */
    protected void onClickMenuInviteVideo(View v)
    {
        if (!isVideoing)
        {
            CommonInterface.requestCheckLianmai(getRoomId(), new AppRequestCallback<App_check_lianmaiActModel>()
            {
                @Override
                protected void onSuccess(SDResponse resp)
                {
                    if (actModel.getStatus() == 1)
                    {
                        showInviteVideoDialog();
                    }
                }
            });
        } else
        {
            SDDialogMenu dialog = new SDDialogMenu(this);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setItems(new String[]{"关闭连麦"});
            dialog.setmListener(new SDDialogMenu.SDDialogMenuListener()
            {
                @Override
                public void onItemClick(View v, int index, SDDialogMenu dialog)
                {
                    switch (index)
                    {
                        case 0:
                            onClickCloseVideo();
                            break;

                        default:
                            break;
                    }
                }

                @Override
                public void onDismiss(SDDialogMenu dialog)
                {
                }

                @Override
                public void onCancelClick(View v, SDDialogMenu dialog)
                {
                }
            });
            dialog.showBottom();
        }
    }

    /**
     * 关闭连麦
     */
    protected void onClickCloseVideo()
    {
        //子类实现
    }

    @Override
    public void onMsgAcceptVideo(CustomMsgAcceptVideo msg)
    {
        super.onMsgAcceptVideo(msg);
        if (isInvitingVideo())
        {
            inviteVideoDialog.setTextContent("主播接受了你的连麦请求");
        }
    }

    @Override
    public void onMsgRejectVideo(CustomMsgRejectVideo msg)
    {
        super.onMsgRejectVideo(msg);
        if (isInvitingVideo())
        {
            inviteVideoDialog.setTextContent(msg.getMsg());
            inviteVideoDialog.startDismissRunnable(1000);
        }
    }

    public void showInviteVideoDialog()
    {
        SDDialogConfirm dialogConfirm = new SDDialogConfirm(this);
        dialogConfirm.setTextContent("是否请求与主播连麦？").setmListener(new SDDialogCustom.SDDialogCustomListener()
        {
            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {

            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                onConfirmInviteVideo();
            }

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {

            }
        }).show();
    }

    protected void onConfirmInviteVideo()
    {
        dismissInviteVideoDialog();
        inviteVideoDialog = new LiveInviteVideoDialog(LiveLayoutViewerActivity.this, getCreaterId());
        inviteVideoDialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {
            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
                onCancelInviteVideo();
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
            }

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
            }
        });
        inviteVideoDialog.show();
    }

    protected void onCancelInviteVideo()
    {

    }

    public void dismissInviteVideoDialog()
    {
        if (inviteVideoDialog != null)
        {
            inviteVideoDialog.dismiss();
        }
    }

    /**
     * 点击送礼物菜单
     *
     * @param v
     */
    protected void onClickMenuSendGift(View v)
    {
        addLiveSendGift();
        roomSendGiftView.show(true);
    }

    @Override
    protected void hideSendGiftView()
    {
        super.hideSendGiftView();
        if (roomSendGiftView != null)
            roomSendGiftView.invisible();
    }

    @Override
    protected boolean isSendGiftViewVisible()
    {
        if (roomSendGiftView == null)
        {
            return false;
        }
        return roomSendGiftView.isVisible();
    }

    @Override
    public void onMsgOpenBanker(long principal)
    {
        super.onMsgOpenBanker(principal);
        roomViewerBottomView.showMenuApplyBanker(true, principal);
    }

    @Override
    public void onMsgChooseBanker(GameBankerModel banker, String userId)
    {
        super.onMsgChooseBanker(banker, userId);
        roomViewerBottomView.showMenuApplyBanker(false, 0);
        if (TextUtils.equals(banker.getBanker_id(), userId))
        {
            getGameBusiness().requestGameIncome(0);
        }
    }

    @Override
    public void onMsgRemoveBanker(GameBankerMsg msg, String userId)
    {
        super.onMsgRemoveBanker(msg, userId);
        if (msg == null)
        {
            roomViewerBottomView.showMenuApplyBanker(false, 0);
            return;
        }
        String bankerId = msg.getData().getBanker().getBanker_id();
        if (TextUtils.equals(bankerId, userId))
        {
            getGameBusiness().requestGameIncome(0);
            mGamePanelView.setCreaterMode(false);
        }
    }

    @Override
    protected void showPluginExtendSwitch(boolean show)
    {
        super.showPluginExtendSwitch(show);
        roomViewerBottomView.showMenuPluginSwitch(show);
    }

    @Override
    protected void showBottomView(boolean show)
    {
        super.showBottomView(show);
        if (show)
        {
            roomViewerBottomView.show();
        } else
        {
            roomViewerBottomView.invisible();
        }
    }

    @Override
    protected void onHidePluginExtend()
    {
        super.onHidePluginExtend();
        roomViewerBottomView.setMenuPluginSwitchStateOpen();
    }

    @Override
    protected void onShowPluginExtend()
    {
        super.onShowPluginExtend();
        roomViewerBottomView.setMenuPluginSwitchStateClose();
    }

    @Override
    public void onGameMsgStopGame()
    {
        super.onGameMsgStopGame();
        roomViewerBottomView.showMenuApplyBanker(false, 0);
    }
}
