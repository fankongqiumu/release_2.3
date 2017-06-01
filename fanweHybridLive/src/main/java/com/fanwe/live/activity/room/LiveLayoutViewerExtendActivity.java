package com.fanwe.live.activity.room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.auction.appview.room.RoomAuctionBtnView;
import com.fanwe.auction.dialog.AuctionSucPayDialog;
import com.fanwe.auction.model.App_pai_user_get_videoActModel;
import com.fanwe.auction.model.PaiBuyerModel;
import com.fanwe.auction.model.custommsg.CustomMsgAuctionFail;
import com.fanwe.auction.model.custommsg.CustomMsgAuctionSuccess;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.dialog.LiveRechargeDialog;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.fanwe.live.model.custommsg.MsgModel;
import com.fanwe.pay.LiveScenePayViewerBusiness;
import com.fanwe.pay.LiveTimePayViewerBusiness;
import com.fanwe.pay.appview.PayLiveBlackBgView;
import com.fanwe.pay.dialog.LiveJoinPayDialog;
import com.fanwe.pay.dialog.PayUserBalanceDialog;
import com.fanwe.pay.model.App_live_live_pay_deductActModel;
import com.fanwe.pay.room.RoomLivePayInfoViewerView;
import com.fanwe.pay.room.RoomLiveScenePayInfoView;

/**
 * 观众界面扩展
 */
public class LiveLayoutViewerExtendActivity extends LiveLayoutViewerActivity implements LiveTimePayViewerBusiness.LiveTimePayViewerBusinessListener, LiveScenePayViewerBusiness.LiveScenePayViewerBusinessListener
{
    //---------竞拍----------
    private RoomAuctionBtnView roomAuctionBtnView;
    private ViewGroup fl_auction_btn;

    //---------付费模式----------
    private boolean isShowBgBlack;//是否黑屏遮盖
    //---------按时直播-----------
    private ViewGroup ll_pay_bg_black;
    protected RoomLivePayInfoViewerView roomLivePayInfoViewerView;
    private PayLiveBlackBgView payLiveBlackBgView;
    private LiveJoinPayDialog timePayJoinDialog; // 是否进入付费直播间dialog
    private PayUserBalanceDialog timePayRechargeDialog;//付费弹出Dialog
    protected LiveTimePayViewerBusiness timePayViewerBusiness;

    //---------按场直播-----------
    private RoomLiveScenePayInfoView roomLiveScenePayInfoView;
    private LiveScenePayViewerBusiness scenePayViewerBusiness;
    private LiveJoinPayDialog scenePayJoinDialog;
    private PayUserBalanceDialog scenePayRechargeDialog;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        //---------付费模式----------
        ll_pay_bg_black = (ViewGroup) findViewById(R.id.ll_pay_bg_black);

        timePayViewerBusiness = new LiveTimePayViewerBusiness(this);
        timePayViewerBusiness.setBusinessListener(this);

        scenePayViewerBusiness = new LiveScenePayViewerBusiness(this);
        scenePayViewerBusiness.setBusinessListener(this);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        //---------竞拍----------
        fl_auction_btn = (ViewGroup) view.findViewById(R.id.fl_auction_btn);
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();

        //---------竞拍----------
        removeView(roomAuctionInfoView);
        removeView(roomAuctionBtnView);
        removeView(auctionUserRanklistView);
        if (roomViewerBottomView != null)
        {
            roomViewerBottomView.showMenuAuctionPay(false);
        }
        if (auctionSucPayDialog != null)
        {
            auctionSucPayDialog = null;
        }

        //-----------付费-------------
        onDestoryTimePayView();
        onDestoryScenePayView();
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);

        //---------付费模式----------
        timePayViewerBusiness.dealPayModelRoomInfoSuccess(actModel);
        scenePayViewerBusiness.dealPayModelRoomInfoSuccess(actModel);
    }

    @Override
    public void onLiveRequestRoomInfoError(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoError(actModel);

        //---------付费模式----------
        timePayViewerBusiness.dealPayModelRoomInfoSuccess(actModel);
        scenePayViewerBusiness.dealPayModelRoomInfoSuccess(actModel);
    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg)
    {
        super.onMsgEndVideo(msg);

        //---------竞拍----------
        removeView(roomAuctionBtnView); //主播强制关闭竞拍，移除竞拍锤子

        //---------付费模式----------
        onDestoryTimePayView();
        onDestoryScenePayView();
    }

    //---------竞拍----------

    @Override
    protected void onClickMenuAuctionPay(View v)
    {
        super.onClickMenuAuctionPay(v);

        auctionBusiness.clickAuctionPay(v);
    }

    @Override
    public void onAuctionRequestPaiInfoSuccess(App_pai_user_get_videoActModel actModel)
    {
        super.onAuctionRequestPaiInfoSuccess(actModel);

        // 添加竞拍锤子按钮
        addLiveAuctionBtnView(actModel);
    }

    @Override
    public void onAuctionNeedShowPay(boolean show)
    {
        super.onAuctionNeedShowPay(show);
        if (roomViewerBottomView != null)
        {
            roomViewerBottomView.showMenuAuctionPay(show);
        }
    }

    private AuctionSucPayDialog auctionSucPayDialog;

    @Override
    public void onAuctionPayClick(View v)
    {
        super.onAuctionPayClick(v);
        if (auctionSucPayDialog == null)
        {
            auctionSucPayDialog = new AuctionSucPayDialog(this, auctionBusiness);
        }
        auctionSucPayDialog.showBottom();
    }

    @Override
    public void onAuctionPayRemaining(PaiBuyerModel buyer, long day, long hour, long min, long sec)
    {
        super.onAuctionPayRemaining(buyer, day, hour, min, sec);
        if (auctionSucPayDialog != null)
        {
            auctionSucPayDialog.onAuctionPayRemaining(buyer, day, hour, min, sec);
        }
    }

    @Override
    public void onAuctionMsgSuccess(CustomMsgAuctionSuccess customMsg)
    {
        super.onAuctionMsgSuccess(customMsg);
        //竞拍成功移除锤子
        removeView(roomAuctionBtnView);
    }

    @Override
    public void onAuctionMsgFail(CustomMsgAuctionFail customMsg)
    {
        super.onAuctionMsgFail(customMsg);
        //流拍移除锤子
        removeView(roomAuctionBtnView);
    }

    /**
     * 添加竞拍锤子按钮
     */
    private void addLiveAuctionBtnView(App_pai_user_get_videoActModel app_pai_user_get_videoActModel)
    {
        roomAuctionBtnView = new RoomAuctionBtnView(this);
        replaceView(fl_auction_btn, roomAuctionBtnView);
        roomAuctionBtnView.bindData(app_pai_user_get_videoActModel);
    }

    //---------付费模式----------


    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg)
    {
        if (isShowBgBlack)
        {
            //如果黑屏，红包不弹出
        } else
        {
            super.onMsgRedEnvelope(msg);
        }
    }

    /**
     * 显示付费加载背景
     */
    protected void showPayModelBg()
    {
        if (!isShowBgBlack)
        {
            isShowBgBlack = true;
            sdkchange2Viewer();
            sdkVideoPause();
            if (payLiveBlackBgView == null)
            {
                payLiveBlackBgView = new PayLiveBlackBgView(this);
                replaceView(ll_pay_bg_black, payLiveBlackBgView);
            }
            SDViewUtil.show(ll_pay_bg_black);
        }
    }

    /**
     * 隐藏付费加载背景
     */
    protected void hidePayModelBg()
    {
        if (isShowBgBlack)
        {
            dismissScenePayRechargeDialog();
            dismissTimePayRechargeDialog();

            isShowBgBlack = false;
            sdkVideoResume();
            SDViewUtil.hide(ll_pay_bg_black);
        }
    }

    @Override
    public void onMsgPayMode(MsgModel msg)
    {
        super.onMsgPayMode(msg);
        timePayViewerBusiness.onMsgPayMode(msg);
        scenePayViewerBusiness.onMsgPayMode(msg);
    }

    //按时付费start=====================
    private void addRoomLivePayInfoViewerView()
    {
        if (roomLivePayInfoViewerView == null)
        {
            roomLivePayInfoViewerView = new RoomLivePayInfoViewerView(this);
            replaceView(fl_live_pay_mode, roomLivePayInfoViewerView);
        }
    }

    @Override
    public void onTimePayViewerRequestMonitorSuccess(App_live_live_pay_deductActModel model)
    {
        if (roomLivePayInfoViewerView != null)
        {
            roomLivePayInfoViewerView.bindData(model);
        }
        getLiveBusiness().setTicket(model.getTicket());
    }

    @Override
    public void onTimePayViewerShowCovering(boolean show)
    {
        if (show)
        {
            showPayModelBg();
        } else
        {
            hidePayModelBg();
        }
    }

    @Override
    public void onTimePayViewerLowDiamonds(App_live_live_pay_deductActModel model)
    {
        showIsDiamondsLowDialog(model);
    }


    @Override
    public void onTimePayViewerShowRecharge(App_live_live_pay_deductActModel model)
    {
        showIsDiamondsLowDialog(model);
    }

    //观众余额不足时或者为0时弹出提示
    private void showIsDiamondsLowDialog(final App_live_live_pay_deductActModel data)
    {
        if (timePayRechargeDialog == null)
        {
            timePayRechargeDialog = new PayUserBalanceDialog(this);
            timePayRechargeDialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
            {

                @Override
                public void onDismiss(SDDialogCustom dialog)
                {

                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                    timePayViewerBusiness.setRecharging(true);
                    LiveRechargeDialog liveRechargeDialog = new LiveRechargeDialog(LiveLayoutViewerExtendActivity.this);
                    liveRechargeDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            timePayViewerBusiness.setRecharging(false);
                            onExitRoom();
                        }
                    });

                    liveRechargeDialog.show();
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    if (timePayRechargeDialog.getIs_recharge() == 1)
                    {
                        //如果处于强制充值，取消就出去房间
                        onExitRoom();
                    }
                }
            });
        }

        if (timePayRechargeDialog.isShowing())
        {
            timePayRechargeDialog.dismiss();
        }
        timePayRechargeDialog.bindData(data);
        timePayRechargeDialog.show();
    }

    @Override
    public void onTimePayViewerShowWhetherJoin(int live_fee)
    {
        showTimePayJoinDialog(live_fee);
    }

    @Override
    public void onTimePayViewerShowWhetherJoinFuture(App_live_live_pay_deductActModel model)
    {
        showTimePayJoinDialog(model.getLive_fee());
    }

    private void showTimePayJoinDialog(int live_fee)
    {
        if (timePayJoinDialog == null)
        {
            timePayJoinDialog = new LiveJoinPayDialog(this);
            timePayJoinDialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
            {
                @Override
                public void onDismiss(SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                    if (payLiveBlackBgView != null)
                    {
                        payLiveBlackBgView.destroyVideo();
                    }
                    timePayViewerBusiness.agreePay();
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    timePayViewerBusiness.rejectPay();
                    onExitRoom();
                }
            });
            timePayJoinDialog.joinPaysetTextContent(live_fee, 0);
            timePayJoinDialog.show();
        }
    }

    @Override
    public void onTimePayShowPayInfoView(App_live_live_pay_deductActModel model)
    {
        addRoomLivePayInfoViewerView();
    }

    @Override
    public void onTimePayViewerCountDown(long leftTime)
    {
        if (roomLivePayInfoViewerView != null)
        {
            roomLivePayInfoViewerView.onPayModeCountDown(leftTime);
        }
    }

    @Override
    public void onTimePayViewerCanJoinRoom(boolean canJoinRoom)
    {
        getViewerBusiness().setCanJoinRoom(canJoinRoom);
        if (canJoinRoom)
        {
            if (payLiveBlackBgView != null)
            {
                payLiveBlackBgView.destroyVideo();
            }
            getViewerBusiness().startJoinRoom(false);
        }
    }

    @Override
    public void onTimePayViewerShowCoveringPlayeVideo(String preview_play_url, int countdown, int is_only_play_voice)
    {
        showPayModelBg();
        payLiveBlackBgView.setIs_only_play_voice(is_only_play_voice);
        payLiveBlackBgView.setProview_play_time(countdown * 1000);
        payLiveBlackBgView.startPlayer(preview_play_url);
    }

    /**
     * 销毁按时付费相关View，定时器
     */
    private void onDestoryTimePayView()
    {
        destroyTimePayJoinDialog();
        dismissTimePayRechargeDialog();
        if (timePayViewerBusiness != null)
        {
            timePayViewerBusiness.onDestroy();
        }

        removeView(roomLivePayInfoViewerView);
        roomLivePayInfoViewerView = null;
    }

    /**
     * 关闭按时直播加入框
     */
    private void destroyTimePayJoinDialog()
    {
        if (timePayJoinDialog != null && timePayJoinDialog.isShowing())
        {
            timePayJoinDialog.dismiss();
        }
        timePayJoinDialog = null;
    }

    /**
     * 关闭按时直播弹出充值框
     */
    private void dismissTimePayRechargeDialog()
    {
        if (timePayRechargeDialog != null && timePayRechargeDialog.isShowing())
        {
            timePayRechargeDialog.dismiss();
        }
    }
    //按时直播 end===================================


    //按场直播 start=================================
    @Override
    public void onScenePayViewerShowCovering(boolean show)
    {
        if (show)
        {
            showPayModelBg();
        } else
        {
            hidePayModelBg();
        }
    }

    @Override
    public void onScenePayViewerShowRecharge(App_live_live_pay_deductActModel model)
    {
        showSceneIsRechargingDialog(model);
    }

    @Override
    public void onScenePayViewerShowPayInfoView(App_live_live_pay_deductActModel model)
    {
        addRoomLiveScenePayInfoView(model.getLive_fee());
    }

    @Override
    public void onScenePayViewerShowWhetherJoin(int live_fee)
    {
        showScenePayJoinDialog(live_fee);
    }

    @Override
    public void onScenePayViewerCanJoinRoom(boolean canJoinRoom)
    {
        getViewerBusiness().setCanJoinRoom(canJoinRoom);
        if (canJoinRoom)
        {
            if (payLiveBlackBgView != null)
            {
                payLiveBlackBgView.destroyVideo();
            }
            getViewerBusiness().startJoinRoom(false);
        }
    }

    @Override
    public void onScenePayViewerShowCoveringPlayeVideo(String preview_play_url, int countdown, int is_only_play_voice)
    {
        showPayModelBg();
        payLiveBlackBgView.setPay_type(1);
        payLiveBlackBgView.setIs_only_play_voice(is_only_play_voice);
        payLiveBlackBgView.setProview_play_time(countdown * 1000);
        payLiveBlackBgView.startPlayer(preview_play_url);
    }

    private void addRoomLiveScenePayInfoView(int live_fee)
    {
        if (roomLiveScenePayInfoView == null)
        {
            roomLiveScenePayInfoView = new RoomLiveScenePayInfoView(this);
            replaceView(fl_live_pay_mode, roomLiveScenePayInfoView);
        }
        roomLiveScenePayInfoView.bindData(live_fee);
    }

    //观众余额不足时按场弹出框
    private void showSceneIsRechargingDialog(final App_live_live_pay_deductActModel data)
    {
        if (scenePayRechargeDialog == null)
        {
            scenePayRechargeDialog = new PayUserBalanceDialog(this);
            scenePayRechargeDialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
            {

                @Override
                public void onDismiss(SDDialogCustom dialog)
                {

                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                    LiveRechargeDialog liveRechargeDialog = new LiveRechargeDialog(LiveLayoutViewerExtendActivity.this);
                    liveRechargeDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            onExitRoom();
                        }
                    });
                    liveRechargeDialog.show();
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    onExitRoom();
                }
            });
        }

        if (scenePayRechargeDialog.isShowing())
        {
            scenePayRechargeDialog.dismiss();
        }
        scenePayRechargeDialog.bindData(data);
        scenePayRechargeDialog.show();
    }

    private void showScenePayJoinDialog(int live_fee)
    {
        if (scenePayJoinDialog == null)
        {
            scenePayJoinDialog = new LiveJoinPayDialog(this);
            scenePayJoinDialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
            {
                @Override
                public void onDismiss(SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                    if (payLiveBlackBgView != null)
                    {
                        payLiveBlackBgView.destroyVideo();
                    }
                    scenePayViewerBusiness.agreeJoinSceneLive();
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    scenePayViewerBusiness.rejectJoinSceneLive();
                    onExitRoom();
                }
            });
            scenePayJoinDialog.joinPaysetTextContent(live_fee, 1);
            scenePayJoinDialog.show();
        }
    }

    /**
     * 销毁按场付费相关View
     */
    private void onDestoryScenePayView()
    {
        destroyScenePayJoinDialog();
        dismissScenePayRechargeDialog();

        removeView(roomLiveScenePayInfoView);
        roomLiveScenePayInfoView = null;
        scenePayViewerBusiness.rejectJoinSceneLive();
    }

    /**
     * 关闭按场加入弹出框
     */
    private void destroyScenePayJoinDialog()
    {
        if (scenePayJoinDialog != null && scenePayJoinDialog.isShowing())
        {
            scenePayJoinDialog.dismiss();
        }
        scenePayJoinDialog = null;
    }

    /**
     * 关闭按场充值弹出框
     */
    private void dismissScenePayRechargeDialog()
    {
        if (scenePayRechargeDialog != null && scenePayRechargeDialog.isShowing())
        {
            scenePayRechargeDialog.dismiss();
        }
    }

    //按场直播 end=================================

    /**
     * 观众不进去付费直播
     */
    protected void onExitRoom()
    {

    }

    protected void sdkVideoPause()
    {

    }

    protected void sdkVideoResume()
    {

    }

    //付费直播 end=================================
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        timePayViewerBusiness.onDestroy();
    }


}
