package com.fanwe.live.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.auction.event.EBigToSmallScreen;
import com.fanwe.hybrid.event.EUnLogin;
import com.fanwe.library.common.SDWindowManager;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.event.EOnBackground;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDRunnableTryer;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveInformation;
import com.fanwe.live.R;
import com.fanwe.live.appview.LivePushFloatViewerView;
import com.fanwe.live.appview.LiveVideoExtendView;
import com.fanwe.live.appview.LiveVideoView;
import com.fanwe.live.common.AppRuntimeData;
import com.fanwe.live.control.LivePlayerSDK;
import com.fanwe.live.control.LivePushSDK;
import com.fanwe.live.event.EImOnForceOffline;
import com.fanwe.live.event.ELiveFloatViewClose;
import com.fanwe.live.event.EOnCallStateChanged;
import com.fanwe.live.event.EPushViewerOnDestroy;
import com.fanwe.live.event.EScrollChangeRoomComplete;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.JoinLiveData;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgInviteVideo;
import com.fanwe.live.model.custommsg.CustomMsgStopLive;
import com.fanwe.live.model.custommsg.CustomMsgUserStopVideo;
import com.sunday.eventbus.SDEventManager;
import com.ta.util.netstate.TANetWorkUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;

/**
 * 推流直播间观众界面
 */
public class LivePushViewerActivity extends LiveLayoutViewerExtendActivity implements LivePlayerSDK.PlayerListener
{

    private static final long DELAY_JOIN_ROOM = 1000;

    private View fl_view_video;
    private LiveVideoView playView;
    /**
     * 是否正在延迟加入房间
     */
    protected boolean isInDelayJoinRoom = false;
    private LiveVideoExtendView pushView;
    private LivePushSDK pushSDK = LivePushSDK.getInstance();
    /**
     * 是否正在播放主播的加速拉流地址
     */
    private boolean isPlayACC = false;
    private SDRunnableTryer groupTryer = new SDRunnableTryer();
    private boolean isBigToSmallScreen = false;//是否大屏变小屏
    private boolean hasInitPusher = false;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_push_viewer;
    }

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        if (isBigToSmallScreen)
        {
            smallToBigScreen();
            return;
        }

        fl_view_video = find(R.id.fl_view_video);
        playView = find(R.id.view_video);
        pushView = find(R.id.view_small_video);

        initPlayer();

        if (validateParams(getRoomId(), getGroupId(), getCreaterId()))
        {
            requestRoomInfo();
        }
    }

    /**
     * 初始化拉流对象
     */
    private void initPlayer()
    {
        playView.setPlayerListener(this);
    }

    /**
     * 初始化推流对象
     */
    private void initPusher()
    {
        if (hasInitPusher)
        {
            return;
        }
        pushView.setSmallMode(true);
        pushSDK.init(pushView.getVideoView());
        pushSDK.setConfigLinkMicSub();

        hasInitPusher = true;
    }

    public LivePlayerSDK getPlayer()
    {
        return playView.getPlayer();
    }

    @Override
    public LiveQualityData onLiveGetLiveQualityData()
    {
        return getPlayer().getLiveQualityData();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if (intent != null)
        {
            int oldRoomId = getRoomId();
            int newRoomId = intent.getIntExtra(EXTRA_ROOM_ID, 0);
            if (newRoomId != oldRoomId)
            {
                setIntent(intent);
                exitRoom(false);
                init(null);
            } else
            {
                SDToast.showToast("已经在直播间中");
            }
        }
        super.onNewIntent(intent);
    }

    protected boolean validateParams(int roomId, String groupId, String createrId)
    {
        if (roomId <= 0)
        {
            SDToast.showToast("房间id为空");
            finish();
            return false;
        }

        if (isEmpty(groupId))
        {
            SDToast.showToast("聊天室id为空");
            finish();
            return false;
        }

        if (isEmpty(createrId))
        {
            SDToast.showToast("主播id为空");
            finish();
            return false;
        }
        setRoomId(roomId);
        setGroupId(groupId);
        setCreaterId(createrId);

        return true;
    }

    @Override
    protected void initIM()
    {
        super.initIM();

        final String groupId = getGroupId();
        getViewerIM().joinGroup(groupId, new TIMCallBack()
        {
            @Override
            public void onError(int code, String desc)
            {
                onErrorJoinGroup(code, desc);
            }

            @Override
            public void onSuccess()
            {
                LiveInformation.getInstance().enterRoom(getRoomId(), getGroupId(), getCreaterId());
                onSuccessJoinGroup(groupId);
            }
        });
    }

    /**
     * 加入聊天组失败
     */
    public void onErrorJoinGroup(int code, String desc)
    {
        boolean result = groupTryer.tryRunDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                initIM();
            }
        }, 3000);

        if (!result)
        {
            showJoinGroupErrorDialog(code, desc);
        }
    }

    protected void showJoinGroupErrorDialog(int code, String desc)
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("加入聊天组失败，是否重试").setTextCancel("退出").setTextConfirm("重试");
        dialog.setCancelable(false);
        dialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                initIM();
            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
                exitRoom(true);
            }
        });
        dialog.show();
    }

    @Override
    protected void onSuccessJoinGroup(String groupId)
    {
        super.onSuccessJoinGroup(groupId);
        sendViewerJoinMsg();

        setCanBindShowShareView(true);
        setCanBindShowInviteVideoView(true);
        if (isScrollChangeRoom)
        {
            onChangeRoomComplete();
            EScrollChangeRoomComplete eventChangeRoomComplete = new EScrollChangeRoomComplete();
            SDEventManager.post(eventChangeRoomComplete);
        }
    }

    @Override
    protected void onConfirmInviteVideo()
    {
        super.onConfirmInviteVideo();
        IMHelper.sendMsgC2C(getCreaterId(), new CustomMsgInviteVideo(), new TIMValueCallBack<TIMMessage>()
        {
            @Override
            public void onError(int i, String s)
            {
                dismissInviteVideoDialog();
            }

            @Override
            public void onSuccess(TIMMessage timMessage)
            {
            }
        });
    }

    @Override
    public void onMsgAcceptVideo(CustomMsgAcceptVideo msg)
    {
        super.onMsgAcceptVideo(msg);
        if (!isInvitingVideo())
        {
            return;
        }
        dismissInviteVideoDialog();

        String pushUrl = msg.getPush_rtmp2();
        final String playUrl = msg.getPlay_rtmp_acc();
        if (!TextUtils.isEmpty(pushUrl) && !TextUtils.isEmpty(playUrl))
        {
            pushSDK.setPushListener(new ITXLivePushListener()
            {
                @Override
                public void onPushEvent(int event, Bundle bundle)
                {
                    if (TXLiveConstants.PUSH_EVT_PUSH_BEGIN == event)
                    {
                        getPlayer().stopPlay();
                        getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                        getPlayer().setUrl(playUrl);
                        getPlayer().startPlay();
                        isPlayACC = true;
                        LogUtil.i("play acc:" + playUrl);
                    }
                }

                @Override
                public void onNetStatus(Bundle bundle)
                {
                }
            });
            startPush(pushUrl);
        }
    }

    @Override
    public void onMsgUserStopVideo(CustomMsgUserStopVideo msg)
    {
        super.onMsgUserStopVideo(msg);
        if (isVideoing)
        {
            SDToast.showToast("主播关闭了连麦");
        }
        stopPush();
    }

    @Override
    protected void onClickCloseVideo()
    {
        super.onClickCloseVideo();
        stopPush();
    }

    /**
     * 开始推流（连麦中）
     *
     * @param pushUrl
     */
    private void startPush(String pushUrl)
    {
        if (TextUtils.isEmpty(pushUrl))
        {
            return;
        }
        if (isVideoing)
        {
            return;
        }
        initPusher();
        pushSDK.startPush(pushUrl);
        pushView.show();
        isVideoing = true;
    }

    /**
     * 停止推流
     */
    private void stopPush()
    {
        if (isVideoing)
        {
            IMHelper.sendMsgC2C(getCreaterId(), new CustomMsgUserStopVideo(), null);
            pushSDK.stopPush(false);
            pushView.hide();
            isVideoing = false;
        }

        if (isPlayACC)
        {
            playUrlByRoomInfo();
        }
    }

    /**
     * 暂停推流
     */
    private void pausePush()
    {
        pushSDK.pausePush();
    }

    /**
     * 恢复推流
     */
    private void resumePush()
    {
        pushSDK.resumePush();
    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg)
    {
        super.onMsgEndVideo(msg);
        exitRoom(false);
    }

    @Override
    public void onMsgStopLive(CustomMsgStopLive msg)
    {
        super.onMsgStopLive(msg);
        SDToast.showToast(msg.getDesc());
        exitRoom(true);
    }

    /**
     * 退出房间
     *
     * @param isNeedFinish true-关闭Activity
     */
    protected void exitRoom(boolean isNeedFinish)
    {
        setCanBindShowShareView(false);
        setCanBindShowInviteVideoView(false);
        destroyIM();
        stopPush();
        getPlayer().stopPlay();
        if (isNeedShowFinish)
        {
            addLiveFinish();
            return;
        }

        if (isNeedFinish)
        {
            finish();
        }
    }

    /**
     * 观众付费模式不进去的时候退出
     */
    @Override
    protected void onExitRoom()
    {
        super.onExitRoom();
        exitRoom(true);
    }

    @Override
    protected void destroyIM()
    {
        super.destroyIM();
        getViewerIM().destroyIM();
        LiveInformation.getInstance().exitRoom();
    }

    @Override
    protected void onVerticalScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        super.onVerticalScroll(e1, e2, distanceX, distanceY);
        if (isInDelayJoinRoom)
        {
            startJoinRoomRunnable(true);
        }
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();
        showLoadingVideo();
        exitRoom(false);
        requestRoomInfo();
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        LogUtil.i("onLiveRequestRoomInfoSuccess");
        int rId = actModel.getRoom_id();
        String gId = actModel.getGroup_id();
        String cId = actModel.getUser_id();

        if (!validateParams(rId, gId, cId))
        {
            return;
        }

        super.onLiveRequestRoomInfoSuccess(actModel);
        switchVideoViewMode();
        getViewerBusiness().startJoinRoom(isScrollChangeRoom);
    }

    private void switchVideoViewMode()
    {
        if (playView == null)
        {
            return;
        }
        if (getLiveBusiness().isPCCreate())
        {
            float height = 0.618f * SDViewUtil.getScreenWidth();

            SDViewUtil.setViewHeight(playView, (int) height);
            SDViewUtil.setViewMarginTop(playView, SDViewUtil.dp2px(80));
        } else
        {
            SDViewUtil.setViewHeight(playView, ViewGroup.LayoutParams.MATCH_PARENT);
            SDViewUtil.setViewMarginTop(playView, 0);
        }
    }

    @Override
    public void onLiveRequestRoomInfoError(App_get_videoActModel actModel)
    {
        LogUtil.i("onLiveRequestRoomInfoError");
        if (!actModel.canJoinRoom())
        {
            super.onLiveRequestRoomInfoError(actModel);
            return;
        }
        super.onLiveRequestRoomInfoError(actModel);
        if (actModel.isVideoStoped())
        {
            addLiveFinish();
        } else
        {
            exitRoom(true);
        }
    }

    @Override
    public void onLiveRequestRoomInfoException(String msg)
    {
        super.onLiveRequestRoomInfoException(msg);

        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setCancelable(false);
        dialog.setTextContent("请求直播间信息失败")
                .setTextCancel("退出").setTextConfirm("重试")
                .setmListener(new SDDialogCustom.SDDialogCustomListener()
                {
                    @Override
                    public void onClickCancel(View v, SDDialogCustom dialog)
                    {
                        exitRoom(true);
                    }

                    @Override
                    public void onClickConfirm(View v, SDDialogCustom dialog)
                    {
                        requestRoomInfo();
                    }

                    @Override
                    public void onDismiss(SDDialogCustom dialog)
                    {
                    }
                }).show();
    }

    @Override
    public void onLiveViewerStartJoinRoom(boolean delay)
    {
        super.onLiveViewerStartJoinRoom(delay);
        if (getRoomInfo() == null)
        {
            return;
        }
        String playUrl = getRoomInfo().getPlay_url();
        if (TextUtils.isEmpty(playUrl))
        {
            requestRoomInfo();
        } else
        {
            startJoinRoomRunnable(delay);
        }
    }

    private void startJoinRoomRunnable(boolean delay)
    {
        if (delay)
        {
            isInDelayJoinRoom = true;
            joinRoomRunnable.runDelay(DELAY_JOIN_ROOM);
        } else
        {
            joinRoomRunnable.run();
        }
    }

    /**
     * 加入房间runnable
     */
    private SDDelayRunnable joinRoomRunnable = new SDDelayRunnable()
    {

        @Override
        public void run()
        {
            isInDelayJoinRoom = false;
            initIM();
            playUrlByRoomInfo();
        }
    };

    /**
     * 根据接口返回的拉流地址开始拉流
     */
    protected void playUrlByRoomInfo()
    {
        if (getRoomInfo() == null)
        {
            return;
        }
        String url = getRoomInfo().getPlay_url();
        if (validatePlayUrl(url))
        {
            getPlayer().stopPlay();
            getPlayer().setUrl(url);
            getPlayer().startPlay();
            LogUtil.i("play normal:" + url);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param)
    {

    }

    @Override
    public void onPlayBegin(int event, Bundle param)
    {

    }

    @Override
    public void onPlayRecvFirstFrame(int event, Bundle param)
    {
        hideLoadingVideo();
    }

    @Override
    public void onPlayProgress(int event, Bundle param, int total, int progress)
    {

    }

    @Override
    public void onPlayEnd(int event, Bundle param)
    {

    }

    @Override
    public void onPlayLoading(int event, Bundle param)
    {

    }

    @Override
    public void onNetStatus(Bundle param)
    {

    }

    protected boolean validatePlayUrl(String playUrl)
    {
        if (TextUtils.isEmpty(playUrl))
        {
            SDToast.showToast("未找到直播地址");
            return false;
        }

        if (playUrl.startsWith("rtmp://"))
        {
            getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.endsWith(".flv"))
        {
            getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        } else
        {
            SDToast.showToast("播放地址不合法");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed()
    {
        showExitDialog();
    }

    @Override
    protected void onClickCloseRoom(View v)
    {
        exitRoom(true);
    }

    private void showExitDialog()
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("确定要退出吗？");
        dialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                exitRoom(true);
            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
            }
        });
        dialog.show();
    }


    public void onEventMainThread(EUnLogin event)
    {
        exitRoom(true);
    }

    public void onEventMainThread(EImOnForceOffline event)
    {
        exitRoom(true);
    }

    public void onEventMainThread(EOnCallStateChanged event)
    {
        switch (event.state)
        {
            case TelephonyManager.CALL_STATE_RINGING:
                sdkEnableAudioDataVolume(false);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                sdkEnableAudioDataVolume(false);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                sdkEnableAudioDataVolume(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnect(TANetWorkUtil.netType type)
    {
        if (type == TANetWorkUtil.netType.mobile)
        {
            SDDialogConfirm dialog = new SDDialogConfirm(this);
            dialog.setTextContent("当前处于数据网络下，会耗费较多流量，是否继续？").setTextCancel("否").setTextConfirm("是").setmListener(new SDDialogCustom.SDDialogCustomListener()
            {

                @Override
                public void onDismiss(SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    exitRoom(true);
                }
            }).show();
        }
        super.onConnect(type);
    }

    @Override
    protected void sdkEnableAudioDataVolume(boolean enable)
    {
        getPlayer().setMute(!enable);
    }

    @Override
    protected void sdkVideoPause()
    {
        super.sdkVideoPause();
        getPlayer().stopPlay();
    }

    @Override
    protected void sdkVideoResume()
    {
        super.sdkVideoResume();
        getPlayer().startPlay();
    }

    @Override
    protected void sdkchange2Viewer()
    {
        super.sdkchange2Viewer();
        stopPush();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (isBigToSmallScreen)
        {
            //如果大屏到小视屏不暂停
        } else
        {
            getPlayer().stopPlay();
            pausePush();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        joinRoomRunnable.removeDelay();
        AppRuntimeData.getInstance().viewerSmallView = null;
        groupTryer.onDestroy();
        getPlayer().onDestroy();
        pushSDK.onDestroy();
        SDEventManager.post(new EPushViewerOnDestroy());
    }

    //小视屏==================================
    @Override
    protected void onResume()
    {
        super.onResume();
        resumePush();
        getPlayer().startPlay();
        smallToBigScreen();
    }

    private void smallToBigScreen()
    {
        LivePushFloatViewerView view = (LivePushFloatViewerView) SDWindowManager.getInstance().getFirstView(LivePushFloatViewerView.class);
        if (view != null)
        {
            SDWindowManager.getInstance().removeView(LivePushFloatViewerView.class);
            View videoView = AppRuntimeData.getInstance().viewerSmallView;
            if (videoView != null)
            {
                isBigToSmallScreen = false;
                SDViewUtil.removeViewFromParent(videoView);
                SDViewUtil.replaceView(find(R.id.view_touch_scroll), videoView);
            }
        }
    }

    public void onEventMainThread(EBigToSmallScreen event)
    {
        switchToSmallVideo();
    }

    public void onEventMainThread(EOnBackground event)
    {
        smallToBigScreen();
    }

    public void onEventMainThread(ELiveFloatViewClose event)
    {
        exitRoom(true);
    }

    private void switchToSmallVideo()
    {
        resumePush();
        getPlayer().startPlay();

        isBigToSmallScreen = true;

        AppRuntimeData.getInstance().viewerSmallView = fl_view_video;

        JoinLiveData data = new JoinLiveData();
        data.setRoomId(getRoomId());
        data.setGroupId(getGroupId());
        data.setCreaterId(getCreaterId());

        LivePushFloatViewerView.addToFloatView(data);
    }
}
